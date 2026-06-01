package com.example.gazago.gazago.client;

import com.example.gazago.gazago.transport.dto.FastApiChatRequest;
import com.example.gazago.gazago.transport.dto.LocationAnalysisResponse;
import com.example.gazago.gazago.transport.dto.PythonSuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class FastApiClient {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://python-server:8000")
            .build();

    // 사용자 메시지에서 키워드 추출 요청
    public LocationAnalysisResponse analyzeText(FastApiChatRequest fastApiRequest) {
        return webClient.post()
                .uri("/api/v1/extract-userinput") // FastAPI의 엔드포인트
                .bodyValue(fastApiRequest)
                .retrieve()
                .bodyToMono(LocationAnalysisResponse.class)
                .block(); // 동기 방식으로 결과 대기
    }

    // 확정된 좌표로 경로 추천 요청
    public PythonSuccessResponse getRecommendedRoutes(Long requestId) {
        return webClient.post()
                .uri("/api/v1/routes/{request_id}", requestId)
                .retrieve()
                .bodyToMono(PythonSuccessResponse.class)
                .block();
    }
}
