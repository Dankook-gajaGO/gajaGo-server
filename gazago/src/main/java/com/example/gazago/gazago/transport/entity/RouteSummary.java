package com.example.gazago.gazago.transport.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "route_summary")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RouteSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "summary_id") //기본키
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private UserRequest userRequest; // 외래키(FK), 경로 식별자

    @Column(name = "travel_time")
    private Integer travelTime; //총 소요 시간(분 단위)

    private Integer transfers; //총 환승 횟수

    @Column(name="walking_distance")
    private Integer walkingDistance; //총 도보거리

    @Column(name="fare")
    private Integer fare; // 총 요금

    @Column(columnDefinition = "TEXT")
    private String reason; // AI 추천 사유

    @Column(name="`rank`")
    private Integer rank;  // 우선순위

    @Column(columnDefinition = "TEXT")
    private String detail; // 상세 경로 데이터

    @Builder
    public RouteSummary(UserRequest userRequest, Integer travelTime, Integer transfers,
                        Integer walkingDistance, Integer fare, String reason, Integer rank, String detail) {
        this.userRequest = userRequest;
        this.travelTime = travelTime;
        this.transfers = transfers;
        this.walkingDistance = walkingDistance;
        this.fare = fare;
        this.reason = reason;
        this.rank = rank;
        this.detail = detail;
    }

    public void setUserRequest(UserRequest userRequest) {
        this.userRequest = userRequest;
    }
}
