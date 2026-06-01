package com.example.gazago.gazago.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        System.out.println(">>> [DEBUG] 요청 URL: " + request.getRequestURI());
        System.out.println(">>> [DEBUG] Authorization 헤더 값: " + (authHeader == null ? "NULL임!" : authHeader));

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
        } else {
            System.out.println(">>> [DEBUG] 헤더가 없거나 Bearer로 시작하지 않음!");
        }

        // 1. 헤더에서 토큰 추출
        String token = resolveToken(request);

        // 2. 토큰 유효성 검사
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 3. 토큰에서 loginId 추출
            String loginId = jwtTokenProvider.getLoginId(token);

            log.info("DEBUG: [필터] 토큰에서 추출한 loginId = '{}'", loginId);

            // 4. 인증 객체 생성 및 Context에 저장
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(loginId, null, Collections.emptyList());

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        // 5. 다음 필터로 진행
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 제거 후 토큰 값만 반환
        }
        return null;
    }
}