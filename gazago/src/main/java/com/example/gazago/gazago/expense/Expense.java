package com.example.gazago.gazago.expense;

import com.example.gazago.gazago.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

// 여행 가계부 지출 1건 (회원 전용). 금액은 원화
@Entity
@Table(name = "expense")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate spentOn;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Long amount;

    private String category;

    public void update(LocalDate spentOn, String title, Long amount, String category) {
        if (spentOn != null) this.spentOn = spentOn;
        this.title = title;
        this.amount = amount;
        this.category = category;
    }
}
