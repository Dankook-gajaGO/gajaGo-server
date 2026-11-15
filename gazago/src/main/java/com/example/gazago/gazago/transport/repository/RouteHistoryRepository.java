package com.example.gazago.gazago.transport.repository;

import com.example.gazago.gazago.transport.entity.RouteHistory;
import com.example.gazago.gazago.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RouteHistoryRepository extends JpaRepository<RouteHistory, Long> {
    List<RouteHistory> findTop5ByUserOrderBySavedAtDesc(User user);

    Optional<RouteHistory> findFirstByUserAndRequestIdAndRouteId(User user, Long requestId, String routeId);

    void deleteByUser(User user);
}
