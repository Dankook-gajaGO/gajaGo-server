package com.example.gazago.gazago.user.service;

import com.example.gazago.gazago.security.JwtTokenProvider;
import com.example.gazago.gazago.user.dto.LoginRequestDto;
import com.example.gazago.gazago.user.dto.SignupRequestDto;
import com.example.gazago.gazago.user.entity.User;
import com.example.gazago.gazago.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void signUp(SignupRequestDto requestDto) {
        // 1. 이미 존재하는 이메일/ID인지 검증
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        // 3. User 엔티티 생성 및 저장
        User user = User.builder()
                .loginId(requestDto.getLoginId())
                .password(encodedPassword)
                .nickname(requestDto.getNickname())
                .email(requestDto.getEmail())
                .build();

        userRepository.save(user);
    }

    public String login(LoginRequestDto loginDto) {
        // 1. 아이디로 회원 찾기
        User user = userRepository.findByLoginId(loginDto.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 잘못되었습니다."));

        // 2. 비밀번호 암호화 비교
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 잘못되었습니다.");
        }

        // 3. 토큰 생성 (JwtTokenProvider를 주입받아 사용)
        return jwtTokenProvider.createToken(user.getLoginId());
    }
}
