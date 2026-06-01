package com.example.gazago.gazago.config;

import com.example.gazago.gazago.security.JwtAuthenticationFilter;
import com.example.gazago.gazago.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable()) // API 서버이므로 CSRF 보호 비활성화
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 대신 JWT 사용
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/member/signup", "/api/member/login").permitAll() // 가입/로그인은 누구나 가능
                        //.requestMatchers("/api/transport/InitialRequest", "/api/transport/routes").permitAll()
                        //.requestMatchers("/api/food/safe-foods").permitAll()
                        .requestMatchers("/api/festival/festivalDetail", "/api/festival/festivalList").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated() // 그 외 요청은 모두 인증 필요
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}