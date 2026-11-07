package com.example.gazago.gazago.transport.entity;

import com.example.gazago.gazago.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

// 회원이 선택한 교통 경로 기록 (선택 시점 그대로 저장해 두어 나중에 다시 볼 수 있게 함)
@Entity
@Table(name = "route_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RouteHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Long requestId;
    private String routeId;

    private String departureName;
    private String departureAddress;
    private Double departureLat;
    private Double departureLng;

    private String destinationName;
    private String destinationAddress;
    private Double destinationLat;
    private Double destinationLng;

    private Integer travelTime;
    private Integer transfers;
    private Integer walkingDistance;
    private Integer fare;
    @Column(name = "route_rank")
    private Integer routeRank;
    @Column(columnDefinition = "TEXT")
    private String reason;
    @Column(columnDefinition = "TEXT")
    private String detail;

    private LocalDateTime savedAt;

    public void touch() {
        this.savedAt = LocalDateTime.now();
    }
}
