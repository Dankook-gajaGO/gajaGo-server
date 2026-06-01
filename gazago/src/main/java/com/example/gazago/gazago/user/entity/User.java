package com.example.gazago.gazago.user.entity;

import com.example.gazago.gazago.transport.entity.UserRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="user")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long userId; //기본키

    @Column(name="login_id", nullable = false, unique = true)
    private String loginId; //회원 아이디

    @Column(nullable = false)
    private String password; //회원 비밀번호

    private String nickname; //닉네임
    private String email; //이메일

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserRequest> userRequests = new ArrayList<>();

    public void addRequest(UserRequest request) {
        this.userRequests.add(request);
        if (request.getUser() != this) {
            request.setUser(this);
        }
    }
}
