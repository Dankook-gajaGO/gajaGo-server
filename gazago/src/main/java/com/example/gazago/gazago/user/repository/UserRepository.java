package com.example.gazago.gazago.user.repository;

import com.example.gazago.gazago.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 아이디로 회원을 찾는 기능 (로그인 시 사용)
    Optional<User> findByLoginId(String loginId);

    // 이메일 중복 체크 시 사용
    boolean existsByEmail(String email);
}
