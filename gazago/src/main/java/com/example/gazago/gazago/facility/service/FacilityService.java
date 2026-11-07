package com.example.gazago.gazago.facility.service;

import com.example.gazago.gazago.client.TmapClient;
import com.example.gazago.gazago.facility.dto.FacilityNearbyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FacilityService {
    // 카테고리 정보가 없을 경우 기본적으로 조회할 편의시설 리스트
    private static final List<String> DEFAULT_CATEGORIES = List.of("은행", "ATM", "편의점", "화장실", "약국", "지하철역");
    private static final String EXCHANGE = "환전소";
    private final TmapClient tmapClient;

    // 특정 좌표 반경 내의 시설 정보를 조회
    public FacilityNearbyResponse getNearbyFacilities(
            double lat,
            double lng,
            int radius,
            int offset,
            int size,
            String categories
    ) {
        int safeRadius = Math.min(Math.max(radius, 1), 10);
        int safeOffset = Math.max(offset, 0);
        int safeSize = Math.min(Math.max(size, 1), 20);
        List<String> selectedCategories = parseCategories(categories);

        // 환전소는 TMAP 업종 분류로 안 잡혀 키워드 검색 (전용 탭에서만). 물품보관함은 결과가 부정확해 제외
        List<FacilityNearbyResponse.FacilityInfo> items = selectedCategories.equals(List.of(EXCHANGE))
                ? tmapClient.searchNearbyByKeyword("환전", EXCHANGE, lat, lng, safeRadius, safeOffset, safeSize)
                : tmapClient.searchNearbyFacilities(
                        lat,
                        lng,
                        safeRadius,
                        safeOffset,
                        safeSize,
                        selectedCategories.stream().filter(c -> !c.equals(EXCHANGE)).toList()
                );

        // API 응답 결과와 요청 조건을 조합하여 최종 응답 DTO 생성
        return FacilityNearbyResponse.builder()
                .items(items)
                .offset(safeOffset)
                .size(safeSize)
                .hasMore(items.size() == safeSize)
                .build();
    }

    private List<String> parseCategories(String categories) {
        if (categories == null || categories.isBlank()) {
            return DEFAULT_CATEGORIES;
        }

        List<String> parsed = Arrays.stream(categories.split("[,;]"))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .toList();

        return parsed.isEmpty() ? DEFAULT_CATEGORIES : parsed;
    }
}