package com.example.gazago.gazago.user.controller;

import com.example.gazago.gazago.security.JwtTokenProvider;
import com.example.gazago.gazago.user.dto.LoginRequestDto;
import com.example.gazago.gazago.user.dto.SignupRequestDto;
import com.example.gazago.gazago.user.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequestDto requestDto) {
        memberService.signUp(requestDto);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto loginDto) {
        String token = memberService.login(loginDto);
        return ResponseEntity.ok(token); // 발급된 토큰 반환
    }
}
