// 최근 교통 경로 기록 1건 (React <-> Spring). 앱의 TransportRouteHistoryItem 과 같은 모양
package com.example.gazago.gazago.transport.dto;

import com.example.gazago.gazago.transport.entity.RouteHistory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RouteHistoryItem {
    private String id;
    private String savedAt;
    private Long requestId;
    private LocationCandidateResponse.LocationInfo departure;
    private LocationCandidateResponse.LocationInfo destination;
    private RouteSelectRequest.RouteInfo route;

    public static RouteHistoryItem from(RouteHistory h) {
        return new RouteHistoryItem(
                String.valueOf(h.getId()),
                h.getSavedAt().toString(),
                h.getRequestId(),
                new LocationCandidateResponse.LocationInfo(h.getDepartureName(), h.getDepartureAddress(),
                        h.getDepartureLat(), h.getDepartureLng()),
                new LocationCandidateResponse.LocationInfo(h.getDestinationName(), h.getDestinationAddress(),
                        h.getDestinationLat(), h.getDestinationLng()),
                new RouteSelectRequest.RouteInfo(h.getRouteId(), h.getReason(), h.getTransfers(),
                        h.getWalkingDistance(), h.getRouteRank(), h.getTravelTime(), h.getFare(), h.getDetail())
        );
    }
}
