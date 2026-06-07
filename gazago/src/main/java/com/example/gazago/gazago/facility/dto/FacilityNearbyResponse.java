package com.example.gazago.gazago.facility.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class FacilityNearbyResponse {
    private List<FacilityInfo> items;
    private int offset;
    private int size;
    private boolean hasMore;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class FacilityInfo {
        private String id;
        private String name;
        private String category;
        private String address;
        private double lat;
        private double lng;
        private int distance;
    }
}