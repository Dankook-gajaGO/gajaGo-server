package com.example.gazago.gazago.transport.controller;

import com.example.gazago.gazago.transport.dto.ChatInitialRequest;
import com.example.gazago.gazago.transport.dto.LocationCandidateResponse;
import com.example.gazago.gazago.transport.dto.LocationSelectRequest;
import com.example.gazago.gazago.transport.dto.RouteSelectRequest;
import com.example.gazago.gazago.transport.dto.RouteHistoryItem;
import com.example.gazago.gazago.transport.service.ChatService;
import com.example.gazago.gazago.transport.service.RouteHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transport")
public class ChatController {
    private final ChatService chatService;
    private final RouteHistoryService routeHistoryService;

    // 리액트에서 사용자의 최초 메시지를 받아서 후보군을 전달하는 API
    @PostMapping("/InitialRequest")
    public LocationCandidateResponse initialMessage(@RequestBody ChatInitialRequest message, Authentication authentication) {
        return chatService.processInitialMessage(message, loginIdOrNull(authentication));
    }

    // 사용자가 고른 장소를 받아서 경로를 제공하는 API
    @PostMapping("/routes")
    public RouteSelectRequest selectLocation(@RequestBody LocationSelectRequest request, Authentication authentication) {
        // 1단계: 저장
        chatService.updateRequestCoordinates(request, loginIdOrNull(authentication));

        // 2단계: 추천 경로 조회
        return chatService.getRecommendedRoutes(request.getRequestId());
    }

    // 회원별 최근 교통 경로 기록 (최근 5개)
    @GetMapping("/history")
    public List<RouteHistoryItem> getHistory(@AuthenticationPrincipal String loginId) {
        return routeHistoryService.getRecent(loginId);
    }

    @PostMapping("/history")
    public List<RouteHistoryItem> saveHistory(@RequestBody RouteHistoryItem item, @AuthenticationPrincipal String loginId) {
        return routeHistoryService.save(loginId, item);
    }

    @DeleteMapping("/history")
    public ResponseEntity<Void> clearHistory(@AuthenticationPrincipal String loginId) {
        routeHistoryService.clear(loginId);
        return ResponseEntity.noContent().build();
    }

    // 비회원(익명)이면 null. "anonymousUser" 라는 아이디의 회원과 헷갈리지 않게 토큰 종류로 판단
    private String loginIdOrNull(Authentication authentication) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) return null;
        return authentication.getName();
    }
}
