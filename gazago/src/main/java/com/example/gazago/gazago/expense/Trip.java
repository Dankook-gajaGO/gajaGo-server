package com.example.gazago.gazago.expense;

import com.example.gazago.gazago.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

// 여행 정보 (회원 전용). 이 기간에 쓴 지출이 이 여행의 가계부가 됨 (지출 쪽 연결 없이 날짜로 묶음)
@Entity
@Table(name = "trip")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 40)
    private String name;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;
}
