package com.example.gazago.gazago.transport.service;

import com.example.gazago.gazago.transport.dto.RouteHistoryItem;
import com.example.gazago.gazago.transport.entity.RouteHistory;
import com.example.gazago.gazago.transport.repository.RouteHistoryRepository;
import com.example.gazago.gazago.user.entity.User;
import com.example.gazago.gazago.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RouteHistoryService {
    private final RouteHistoryRepository routeHistoryRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<RouteHistoryItem> getRecent(String loginId) {
        return routeHistoryRepository.findTop5ByUserOrderBySavedAtDesc(findUser(loginId)).stream()
                .map(RouteHistoryItem::from)
                .toList();
    }

    // 같은 요청의 같은 경로를 다시 고르면 새로 쌓지 않고 시간만 갱신
    public List<RouteHistoryItem> save(String loginId, RouteHistoryItem item) {
        if (item.getDeparture() == null || item.getDestination() == null || item.getRoute() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "경로 정보가 부족합니다.");
        }
        User user = findUser(loginId);
        String routeId = item.getRoute().getId();
        RouteHistory history = routeHistoryRepository
                .findFirstByUserAndRequestIdAndRouteId(user, item.getRequestId(), routeId)
                .orElseGet(() -> RouteHistory.builder()
                        .user(user)
                        .requestId(item.getRequestId())
                        .routeId(routeId)
                        .departureName(item.getDeparture().getName())
                        .departureAddress(item.getDeparture().getAddress())
                        .departureLat(item.getDeparture().getLat())
                        .departureLng(item.getDeparture().getLng())
                        .destinationName(item.getDestination().getName())
                        .destinationAddress(item.getDestination().getAddress())
                        .destinationLat(item.getDestination().getLat())
                        .destinationLng(item.getDestination().getLng())
                        .travelTime(item.getRoute().getTravelTime())
                        .transfers(item.getRoute().getTransfers())
                        .walkingDistance(item.getRoute().getWalkingDistance())
                        .fare(item.getRoute().getFare())
                        .routeRank(item.getRoute().getRank())
                        .reason(item.getRoute().getReason())
                        .detail(item.getRoute().getDetail())
                        .build());
        history.touch();
        routeHistoryRepository.save(history);
        return getRecent(loginId);
    }

    public void clear(String loginId) {
        routeHistoryRepository.deleteByUser(findUser(loginId));
    }

    private User findUser(String loginId) {
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "다시 로그인해주세요."));
    }
}
