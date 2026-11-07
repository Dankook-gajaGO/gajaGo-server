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

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void signUp(SignupRequestDto requestDto) {
        // 1. 이미 존재하는 이메일/ID인지 검증
        if (userRepository.existsByLoginId(requestDto.getLoginId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
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

    // 앱 알러지 필터와 allergy_mapping.allergy_category 의 이름 (이 값만 저장 허용)
    private static final List<String> ALLERGY_NAMES =
            List.of("난류", "우유", "밀", "메밀", "견과류", "대두", "갑각류", "해산물", "돼지고기", "쇠고기", "닭고기");

    public List<String> getAllergies(String loginId) {
        String saved = findMember(loginId).getAllergies();
        return saved == null || saved.isBlank() ? List.of() : List.of(saved.split(","));
    }

    @Transactional
    public List<String> saveAllergies(String loginId, List<String> allergies) {
        List<String> cleaned = allergies == null ? List.of() : allergies.stream().distinct().toList();
        if (!ALLERGY_NAMES.containsAll(cleaned)) {
            throw new IllegalArgumentException("알 수 없는 알러지 항목이 있습니다.");
        }
        findMember(loginId).setAllergies(String.join(",", cleaned));
        return cleaned;
    }

    private User findMember(String loginId) {
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다. 다시 로그인해주세요."));
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
