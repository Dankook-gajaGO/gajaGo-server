package com.example.gazago.gazago.transport.service;

import com.example.gazago.gazago.client.FastApiClient;
import com.example.gazago.gazago.client.TmapClient;
import com.example.gazago.gazago.transport.entity.RouteSummary;
import com.example.gazago.gazago.transport.entity.UserRequest;
import com.example.gazago.gazago.transport.repository.ChatRepository;
import com.example.gazago.gazago.transport.repository.RouteSummaryRepository;
import com.example.gazago.gazago.transport.dto.*;
import com.example.gazago.gazago.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {
    private final ChatRepository chatRepository;
    private final RouteSummaryRepository routeSummaryRepository;
    private final FastApiClient fastApiClient;
    private final TmapClient tmapClient;
    private final com.example.gazago.gazago.user.repository.UserRepository userRepository;

    // 사용자의 첫 메시지를 처리하는 메서드
    @Transactional
    public LocationCandidateResponse processInitialMessage(String userMessage, String loginId) {
        // 1. 유저 조회
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> {
                    List<User> allUsers = userRepository.findAll();
                    log.error("--- 디버깅 시작 ---");
                    for (User u : allUsers) {
                        log.error("DB에 저장된 유저: '{}' (길이: {})", u.getLoginId(), u.getLoginId().length());
                    }
                    log.error("--- 요청받은 ID: '{}' (길이: {}) ---", loginId, loginId.length());
                    return new EntityNotFoundException("사용자를 찾을 수 없습니다: " + loginId);
                });

        // 2. FastAPI 호출: 자연어 분석 (출발지/목적지 키워드, 주의사항 추출)
        FastApiChatRequest fastApiRequest = new FastApiChatRequest(userMessage);
        var pythonResponse = fastApiClient.analyzeText(fastApiRequest);

        //2. DB 저장
        UserRequest userRequest = UserRequest.createInitialRequest(
                user,
                pythonResponse.getDestination(),
                pythonResponse.getDeparture(),
                pythonResponse.getConstraints());

        userRequest.setUser(user);
        user.addRequest(userRequest);

        UserRequest savedRequest = chatRepository.save(userRequest);

        // 3. Tmap 호출: 추출된 키워드로 실제 장소들 검색
        var departurecan = tmapClient.searchPois(pythonResponse.getDeparture());
        var destinationcan = tmapClient.searchPois(pythonResponse.getDestination());

        // 4. 응답 DTO 조립 : 리액트가 화면에 후보를 띄울 수 있도록 가공하여 반환
        return LocationCandidateResponse.builder()
                .requestId(savedRequest.getId())
                .departurecandidates(departurecan)
                .destinationcandidates(destinationcan)
                .build();
    }

    // 사용자가 리스트 중 최종 장소를 선택했을 때 호출되는 메서드
    @Transactional
    public void updateRequestCoordinates(LocationSelectRequest request) {
        UserRequest userRequest = chatRepository.findById(request.getRequestId())
                .orElseThrow(() -> new EntityNotFoundException("요청을 찾을 수 없습니다."));

        userRequest.updateCoordinates(
                request.getSelectedDeparture().getName(),
                request.getSelectedDeparture().getLat(),
                request.getSelectedDeparture().getLng(),
                request.getSelectedDestination().getName(),
                request.getSelectedDestination().getLat(),
                request.getSelectedDestination().getLng()
        );
    }

    // 파이썬에서 경로 후보군 받아오는 메서드
    @Transactional(readOnly = true)
    public RouteSelectRequest getRecommendedRoutes(Long requestId) {

        PythonSuccessResponse response = fastApiClient.getRecommendedRoutes(requestId);

        if (response == null || !"success".equals(response.getStatus())) {
            throw new RuntimeException("파이썬 서버에서 경로 생성 실패");
        }

        List<RouteSummary> savedRoutes = routeSummaryRepository.findByUserRequestId(requestId);

        return RouteSelectRequest.builder()
                .requestId(requestId)
                .routes(savedRoutes.stream().map(RouteSelectRequest.RouteInfo::from).toList())
                .build();
    }
}
