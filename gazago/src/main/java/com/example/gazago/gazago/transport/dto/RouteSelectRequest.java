// 사용자에게 추천할 경로 목록을 담는 응답 DTO (Spring -> React)
package com.example.gazago.gazago.transport.dto;

import com.example.gazago.gazago.transport.entity.RouteSummary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteSelectRequest {
    private Long requestId; // 현재 진행 중인 서비스 요청 ID
    private List<RouteInfo> routes; // 추천된 경로 리스트

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RouteInfo {
        private String id;      // 경로 식별자
        private String reason;      // Gemini가 작성한 추천 사유
        private int transfers; // 총 환승 횟수
        private int walkingDistance; // 총 도보거리
        private int rank;        // 추천 순위 (1, 2, 3)
        private int travelTime;       // 총 소요 시간 (분 단위)
        private int fare;       // 총 요금
        private String detail; // 상세 경로 데이터


        public static RouteInfo from(RouteSummary routeSummary) {
            return RouteInfo.builder()
                    .id(String.valueOf(routeSummary.getId())) // 엔티티의 PK ID 사용
                    .reason(routeSummary.getReason())              // 파이썬이 채운 추천 사유
                    .transfers(routeSummary.getTransfers())
                    .walkingDistance(routeSummary.getWalkingDistance())
                    .rank(routeSummary.getRank())                  // AI가 매긴 우선순위
                    .travelTime(routeSummary.getTravelTime())       // 소요 시간
                    .fare(routeSummary.getFare())             // 요금
                    .detail(routeSummary.getDetail())
                    .build();
        }
    }
}
