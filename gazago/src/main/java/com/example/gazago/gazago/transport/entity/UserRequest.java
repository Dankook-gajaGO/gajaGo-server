package com.example.gazago.gazago.transport.entity;

import com.example.gazago.gazago.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="user_request")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;

    // 출발지 정보
    @Column(name="departure_nm")
    private String departureNm;

    @Column(name="departure_lat")
    private Double departureLat;

    @Column(name="departure_lon")
    private Double departureLon;

    // 목적지 정보
    @Column(name = "destination_nm")
    private String destinationNm;

    @Column(name="destination_lat")
    private Double destinationLat;

    @Column(name="destination_lon")
    private Double destinationLon;

    // Gemini가 분석한 주의사항 (휠체어 등)
    @Column(name="user_constraints", length=500)
    private String userConstraints;

    // 출발지명, 목적지명, 주의사항 저장 및 객체 생성
    public static UserRequest createInitialRequest(User user,String destination, String departure, String constraints) {
        if(destination == null || departure == null) throw new IllegalArgumentException("출발지와 목적지는 필수입니다.");

        UserRequest request = UserRequest.builder()
                .departureNm(departure)
                .destinationNm(destination)
                .userConstraints(constraints)
                .user(user)
                .build();

        user.addRequest(request);

        return request;
    }

    // 선택된 장소의 구체적인 좌표를 담는 함수
    public void updateCoordinates(String departureNm, double departureLat, double departureLon, String destinationNm, double destinationLat, double destinationLon) {
        this.departureNm = departureNm;
        this.departureLat = departureLat;
        this.departureLon = departureLon;
        this.destinationNm = destinationNm;
        this.destinationLat = destinationLat;
        this.destinationLon = destinationLon;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    public void setUser(User user) {
        this.user = user;
    }


}
