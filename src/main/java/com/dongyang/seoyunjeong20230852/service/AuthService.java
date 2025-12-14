package com.dongyang.seoyunjeong20230852.service;

import com.dongyang.seoyunjeong20230852.dto.LoginRequestDto;
import com.dongyang.seoyunjeong20230852.dto.TokenResponseDto;
import com.dongyang.seoyunjeong20230852.dto.UserRequestDto;
import com.dongyang.seoyunjeong20230852.dto.UserResponseDto;
import com.dongyang.seoyunjeong20230852.entity.RefreshToken;
import com.dongyang.seoyunjeong20230852.entity.Role;
import com.dongyang.seoyunjeong20230852.entity.User;
import com.dongyang.seoyunjeong20230852.repository.RefreshTokenRepository;
import com.dongyang.seoyunjeong20230852.repository.UserRepository;
import com.dongyang.seoyunjeong20230852.security.JwtUtil;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    //---------유저 정보 관리---------

    //회원가입
    public void signup(UserRequestDto dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("이미 가입된 이메일입니다.");
        }
        User user = User.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .role(Role.ROLE_USER)  // 기본 ROLE_USER 적용
                .build();
        userRepository.save(user);
    }

    //로그인
    public TokenResponseDto login(LoginRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        // Refresh Token 저장
        refreshTokenRepository.findByEmail(user.getEmail())
                .ifPresentOrElse(
                        t -> { t.setRefreshToken(refreshToken); refreshTokenRepository.save(t); },
                        () -> refreshTokenRepository.save(RefreshToken.builder()
                                .email(user.getEmail())
                                .refreshToken(refreshToken)
                                .build())
                );

        return new TokenResponseDto(accessToken, refreshToken, user.getName());
    }

    //로그아웃
    @Transactional
    public void logout(String refreshToken) {
        String email = jwtUtil.extractEmail(refreshToken);
        refreshTokenRepository.deleteByEmail(email);
    }

    //회원 탈퇴
    @Transactional
    public void deleteAccount(String token) {
        String email = jwtUtil.extractEmail(token);

        // RefreshToken 먼저 삭제
        refreshTokenRepository.deleteByEmail(email);

        // User 삭제
        userRepository.findByEmail(email)
                .ifPresent(userRepository::delete);
    }

    //---------관리자 전용 API---------

    // 전체 회원 조회
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> UserResponseDto.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .name(user.getName())
                        .role(user.getRole().name())
                        .build())
                .toList();
    }

    // 회원 정보 수정 (관리자 전용)
    @Transactional
    public void updateUserByAdmin(Long id, UserRequestDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 이메일 중복 검사 (자기 자신 제외)
        if (dto.getEmail() != null && !dto.getEmail().isBlank() && !dto.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
            }
            user.setEmail(dto.getEmail());
        }

        if (dto.getName() != null && !dto.getName().isBlank()) {
            user.setName(dto.getName());
        }

        if (dto.getRole() != null) {
            user.setRole(dto.getRole());
        }

        userRepository.save(user);
    }

    // 회원 삭제 (관리자 전용)
    @Transactional
    public void deleteUserByAdmin(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("해당 회원이 존재하지 않습니다.");
        }
        userRepository.deleteById(id);
    }

    //---------JWT 토큰 관리---------

    //리프레시 토큰 재발급
    public TokenResponseDto reissue(String refreshToken) {
        String email = jwtUtil.extractEmail(refreshToken);
        RefreshToken token = refreshTokenRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("토큰이 DB에 없습니다."));
        if (!token.getRefreshToken().equals(refreshToken)) {
            throw new RuntimeException("토큰값이 일치하지 않습니다.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("유저 정보 없음"));

        String newAccessToken = jwtUtil.generateAccessToken(email, user.getRole().name());
        String newRefreshToken = jwtUtil.generateRefreshToken(email);
        token.setRefreshToken(newRefreshToken);
        refreshTokenRepository.save(token);

        return new TokenResponseDto(newAccessToken, newRefreshToken, user.getName());
    }
}