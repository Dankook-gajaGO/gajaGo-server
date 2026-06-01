package com.example.gazago.gazago.client;

import com.example.gazago.gazago.transport.dto.LocationCandidateResponse;
import com.example.gazago.gazago.transport.dto.TmapPoiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TmapClient {

    // Tmap API 통신을 위한 WebClient 설정
    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://apis.openapi.sk.com")
            .build();

    // application.properties에 정의된 API 키를 주입받음
    @Value("${tmap.api.key}") // application.properties에 저장된 키 사용
    private String appKey;

    // Tmap POI 검색 API를 호출하여 장소 후보군 리스트를 반환
    public List<LocationCandidateResponse.LocationInfo> searchPois(String keyword) {
        // Tmap API 서버로 GET 요청 전송
        TmapPoiResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tmap/pois") //POI 검색 엔드포인트
                        .queryParam("version", 1)
                        .queryParam("searchKeyword", keyword) // 파이썬이 분석해준 목적지명
                        .queryParam("count", 5) // 후보군 5개 추출
                        .queryParam("appKey", appKey)
                        .build())
                .retrieve()
                .bodyToMono(TmapPoiResponse.class)
                .block();

        // 2. Tmap 응답 데이터 유효성 검사 (결과가 없을 경우를 대비)
        if (response == null || response.getSearchPoiInfo() == null ||
                response.getSearchPoiInfo().getPois() == null) {
            return List.of(); // 빈 리스트 반환
        }

        // 3. Tmap 전용 객체를 DTO로 변환
        return response.getSearchPoiInfo().getPois().getPoi().stream()
                .map(poi -> new LocationCandidateResponse.LocationInfo(
                        poi.getName(), //장소명
                        poi.getNewAddressList().getNewAddress().get(0).getFullAddressRoad(), //첫 번째 도로명 주소 추출
                        Double.parseDouble(poi.getNoorLat()), //위도
                        Double.parseDouble(poi.getNoorLon()) //경도
                ))
                .collect(Collectors.toList());
    }
}
