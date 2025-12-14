package com.dongyang.seoyunjeong20230852.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
//JWT 토큰의 유요성 검사
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        //인증이 필요 없는 요청 통과
        if (uri.equals("/api/auth/login") ||
                uri.equals("/api/auth/signup") ||
                uri.equals("/api/auth/reissue") ||
                uri.equals("/api/auth/logout")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = resolveToken(request);
        //유효성 검사
        if (token != null && jwtUtil.isTokenValid(token)) {
            //사용자 정보 로드
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractRole(token);

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            //SecurityContext에 등록하여 유저의 로그인 상태 유지
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    //JWT토큰 추출 함수
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

