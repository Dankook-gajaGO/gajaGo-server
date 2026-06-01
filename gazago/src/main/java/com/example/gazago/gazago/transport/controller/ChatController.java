package com.example.gazago.gazago.transport.controller;

import com.example.gazago.gazago.transport.dto.ChatInitialRequest;
import com.example.gazago.gazago.transport.dto.LocationCandidateResponse;
import com.example.gazago.gazago.transport.dto.LocationSelectRequest;
import com.example.gazago.gazago.transport.dto.RouteSelectRequest;
import com.example.gazago.gazago.transport.service.ChatService;
import lombok.RequiredArgsConstructor;
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

    // 리액트에서 사용자의 최초 메시지를 받아서 후보군을 전달하는 API
    @PostMapping("/InitialRequest")
    public LocationCandidateResponse initialMessage(@RequestBody ChatInitialRequest message, @AuthenticationPrincipal String loginId) {
        System.out.println("LOGIN_ID = " + loginId);
        return chatService.processInitialMessage(message.getMessage(), loginId);
    }

    // 사용자가 고른 장소를 받아서 경로를 제공하는 API
    @PostMapping("/routes")
    public RouteSelectRequest selectLocation(@RequestBody LocationSelectRequest request) {
        // 1단계: 저장
        chatService.updateRequestCoordinates(request);

        // 2단계: 추천 경로 조회
        return chatService.getRecommendedRoutes(request.getRequestId());
    }
}
