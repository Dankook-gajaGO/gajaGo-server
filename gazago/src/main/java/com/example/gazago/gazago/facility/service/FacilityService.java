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
    private static final List<String> DEFAULT_CATEGORIES = List.of("은행", "ATM", "편의점", "화장실");
    private final TmapClient tmapClient;

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

        List<FacilityNearbyResponse.FacilityInfo> items = tmapClient.searchNearbyFacilities(
                lat,
                lng,
                safeRadius,
                safeOffset,
                safeSize,
                selectedCategories
        );

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