package com.dongyang.seoyunjeong20230852.controller;

import com.dongyang.seoyunjeong20230852.dto.UserResponseDto;
import com.dongyang.seoyunjeong20230852.entity.User;
import com.dongyang.seoyunjeong20230852.repository.UserRepository;
import com.dongyang.seoyunjeong20230852.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @GetMapping("/info")
    public ResponseEntity<UserResponseDto> getUserInfo(@RequestHeader("Authorization") String token) {
        token = token.replace("Bearer ", "");

        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        UserResponseDto dto = new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole().name()
        );

        return ResponseEntity.ok(dto);
    }
}
