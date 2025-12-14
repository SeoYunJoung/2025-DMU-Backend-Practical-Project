package com.dongyang.seoyunjeong20230852.controller;

import com.dongyang.seoyunjeong20230852.dto.*;
import com.dongyang.seoyunjeong20230852.entity.User;
import com.dongyang.seoyunjeong20230852.repository.UserRepository;
import com.dongyang.seoyunjeong20230852.security.CustomUserDetails;
import com.dongyang.seoyunjeong20230852.security.JwtUtil;
import com.dongyang.seoyunjeong20230852.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;


    //---------유저 정보 관리---------

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserRequestDto dto) {
        authService.signup(dto);
        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto dto) {
        try {
            return ResponseEntity.ok(authService.login(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());  // 400 Bad Request와 함께 메시지 전달
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody LogoutRequestDto requestDto) {
        authService.logout(requestDto.getRefreshToken());
        return ResponseEntity.ok("로그아웃 완료");
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<String> withdraw(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("인증 토큰이 없습니다.");
        }
        String token = authHeader.substring(7);
        authService.deleteAccount(token);
        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
    }

    @GetMapping("/info")
    public ResponseEntity<UserResponseDto> getUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        User user = userDetails.getUser();

        UserResponseDto dto = new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole().name()
        );

        return ResponseEntity.ok(dto);
    }

    //---------관리자 전용 API---------

    // 관리자: 전체 회원 조회
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin/users")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(authService.getAllUsers());
    }

    // 관리자: 회원 정보 수정
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/admin/users/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody UserRequestDto dto) {
        try {
            authService.updateUserByAdmin(id, dto);
            return ResponseEntity.ok("회원 정보가 수정되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 400 Bad Request로 변경
        }
    }

    // 관리자: 회원 삭제
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/admin/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        authService.deleteUserByAdmin(id);
        return ResponseEntity.ok("회원이 삭제되었습니다.");
    }


    //---------JWT 토큰 관리---------

    @PostMapping("/reissue")
    public ResponseEntity<TokenResponseDto> reissue(@RequestBody RefreshTokenRequestDto requestDto) {
        return ResponseEntity.ok(authService.reissue(requestDto.getRefreshToken()));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("인증 헤더가 없습니다.");
        }

        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        UserResponseDto response = new UserResponseDto(
                user.getId(), user.getEmail(), user.getName(), user.getRole().name()
        );

        return ResponseEntity.ok(response);
    }
}