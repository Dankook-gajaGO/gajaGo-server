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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.server.ResponseStatusException;

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
    public LocationCandidateResponse processInitialMessage(ChatInitialRequest req, String loginId) {
        Double lat = req.getLat(), lng = req.getLng();
        boolean knownDestination = req.getDestLat() != null && req.getDestLng() != null;

        // 1. 유저 조회 (비회원이면 null: 길찾기는 누구나, 기록 저장은 회원만)
        User user = loginId == null ? null : userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + loginId));

        // 2. FastAPI 호출: 자연어 분석 (출발지/목적지 키워드, 주의사항 추출)
        FastApiChatRequest fastApiRequest = new FastApiChatRequest(req.getMessage());
        LocationAnalysisResponse pythonResponse;
        try {
            pythonResponse = fastApiClient.analyzeText(fastApiRequest);
        } catch (WebClientException e) {
            log.warn("챗봇 분석 서버 호출 실패: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "AI 분석이 잠시 안 됩니다. 잠시 후 다시 시도해주세요.");
        }
        if (pythonResponse == null || (!knownDestination && isUnknownPlace(pythonResponse.getDestination()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "어디로 가고 싶은지 알려주세요. 예: 서울역에서 경복궁까지 어떻게 가?");
        }
        // 출발지를 말하지 않았거나 "현재 위치"면 기기 위치를 출발지로 사용
        boolean fromHere = isUnknownPlace(pythonResponse.getDeparture()) || CURRENT_LOCATION.equals(pythonResponse.getDeparture().trim());
        if (fromHere && (lat == null || lng == null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "출발지를 함께 알려주시거나, 위치 권한을 허용해주세요.");
        }
        String departure = fromHere ? CURRENT_LOCATION : pythonResponse.getDeparture();
        String destination = knownDestination && req.getDestName() != null ? req.getDestName() : pythonResponse.getDestination();

        //2. DB 저장
        UserRequest userRequest = UserRequest.createInitialRequest(
                user,
                destination,
                departure,
                pythonResponse.getConstraints());


        UserRequest savedRequest = chatRepository.save(userRequest);

        // 3. Tmap 호출: 추출된 키워드로 실제 장소들 검색
        var departurecan = fromHere
                ? List.of(new LocationCandidateResponse.LocationInfo(CURRENT_LOCATION, "내 위치", lat, lng))
                : tmapClient.searchPois(departure);
        var destinationcan = knownDestination
                ? List.of(new LocationCandidateResponse.LocationInfo(destination,
                        req.getDestAddress() != null ? req.getDestAddress() : "", req.getDestLat(), req.getDestLng()))
                : tmapClient.searchPois(destination);

        // 4. 응답 DTO 조립 : 리액트가 화면에 후보를 띄울 수 있도록 가공하여 반환
        return LocationCandidateResponse.builder()
                .requestId(savedRequest.getId())
                .departurecandidates(departurecan)
                .destinationcandidates(destinationcan)
                .build();
    }

    // 사용자가 리스트 중 최종 장소를 선택했을 때 호출되는 메서드
    @Transactional
    public void updateRequestCoordinates(LocationSelectRequest request, String loginId) {
        UserRequest userRequest = chatRepository.findById(request.getRequestId())
                .orElseThrow(() -> new EntityNotFoundException("요청을 찾을 수 없습니다."));
        // 회원의 요청은 본인만 이어서 사용 (요청 번호만 알면 남의 기록을 바꾸거나 보던 문제 방지)
        if (userRequest.getUser() != null && !userRequest.getUser().getLoginId().equals(loginId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "다른 사용자의 요청입니다.");
        }

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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "대중교통 경로를 찾지 못했습니다. 다른 출발지나 도착지를 골라주세요.");
        }

        List<RouteSummary> savedRoutes = routeSummaryRepository.findByUserRequestId(requestId);

        return RouteSelectRequest.builder()
                .requestId(requestId)
                .routes(savedRoutes.stream().map(RouteSelectRequest.RouteInfo::from).toList())
                .build();
    }

    private static final String CURRENT_LOCATION = "현재 위치";

    // Gemini 는 장소를 못 찾으면 "unknown" 을 돌려줌 (프롬프트 규칙)
    private boolean isUnknownPlace(String place) {
        return place == null || place.isBlank() || "unknown".equalsIgnoreCase(place.trim());
    }
}
