package com.example.gazago.gazago.client;

import com.example.gazago.gazago.facility.dto.FacilityNearbyResponse;
import com.example.gazago.gazago.transport.dto.LocationCandidateResponse;
import com.example.gazago.gazago.transport.dto.TmapPoiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TmapClient {
    private static final String DEFAULT_ADDRESS = "주소 정보 없음";

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://apis.openapi.sk.com")
            .build();

    @Value("${TMAP_KEY}")
    private String appKey;

    public List<LocationCandidateResponse.LocationInfo> searchPois(String keyword) {
        TmapPoiResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tmap/pois")
                        .queryParam("version", 1)
                        .queryParam("searchKeyword", keyword)
                        .queryParam("count", 5)
                        .queryParam("appKey", appKey)
                        .build())
                .retrieve()
                .bodyToMono(TmapPoiResponse.class)
                .block();

        return readPois(response).stream()
                .map(this::toLocationInfo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<FacilityNearbyResponse.FacilityInfo> searchNearbyFacilities(
            double lat,
            double lng,
            int radius,
            int offset,
            int size,
            List<String> categories
    ) {
        int tmapPage = (offset / size) + 1;
        String categoryParam = String.join(";", categories);

        TmapPoiResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tmap/pois/search/around")
                        .queryParam("version", 1)
                        .queryParam("centerLat", lat)
                        .queryParam("centerLon", lng)
                        .queryParam("radius", radius)
                        .queryParam("categories", categoryParam)
                        .queryParam("page", tmapPage)
                        .queryParam("count", size)
                        .queryParam("sort", "distance")
                        .queryParam("reqCoordType", "WGS84GEO")
                        .queryParam("resCoordType", "WGS84GEO")
                        .queryParam("multiPoint", "N")
                        .queryParam("appKey", appKey)
                        .build())
                .retrieve()
                .bodyToMono(TmapPoiResponse.class)
                .block();

        return readPois(response).stream()
                .map(poi -> toFacilityInfo(poi, lat, lng, categories))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<TmapPoiResponse.Poi> readPois(TmapPoiResponse response) {
        if (response == null || response.getSearchPoiInfo() == null
                || response.getSearchPoiInfo().getPois() == null
                || response.getSearchPoiInfo().getPois().getPoi() == null) {
            return List.of();
        }
        return response.getSearchPoiInfo().getPois().getPoi();
    }

    private LocationCandidateResponse.LocationInfo toLocationInfo(TmapPoiResponse.Poi poi) {
        Double poiLat = parseDouble(poi.getNoorLat());
        Double poiLng = parseDouble(poi.getNoorLon());
        if (poiLat == null || poiLng == null) return null;

        return new LocationCandidateResponse.LocationInfo(
                safeText(poi.getName(), "이름 없는 장소"),
                readAddress(poi),
                poiLat,
                poiLng
        );
    }

    private FacilityNearbyResponse.FacilityInfo toFacilityInfo(
            TmapPoiResponse.Poi poi,
            double centerLat,
            double centerLng,
            List<String> requestedCategories
    ) {
        Double poiLat = parseDouble(poi.getNoorLat());
        Double poiLng = parseDouble(poi.getNoorLon());
        if (poiLat == null || poiLng == null) return null;

        return FacilityNearbyResponse.FacilityInfo.builder()
                .id(safeText(poi.getId(), safeText(poi.getName(), "facility") + "-" + poiLat + "-" + poiLng))
                .name(safeText(poi.getName(), "이름 없는 시설"))
                .category(resolveCategory(poi, requestedCategories))
                .address(readAddress(poi))
                .lat(poiLat)
                .lng(poiLng)
                .distance(resolveDistance(poi, centerLat, centerLng, poiLat, poiLng))
                .build();
    }

    private String readAddress(TmapPoiResponse.Poi poi) {
        String newAddress = readNewAddress(poi);
        if (!newAddress.isBlank()) return newAddress;

        String roadAddress = readRoadAddress(poi);
        if (!roadAddress.isBlank()) return roadAddress;

        String lotAddress = readLotAddress(poi);
        if (!lotAddress.isBlank()) return lotAddress;

        return DEFAULT_ADDRESS;
    }

    private String readNewAddress(TmapPoiResponse.Poi poi) {
        if (poi.getNewAddressList() != null
                && poi.getNewAddressList().getNewAddress() != null
                && !poi.getNewAddressList().getNewAddress().isEmpty()) {
            return trimToEmpty(poi.getNewAddressList().getNewAddress().get(0).getFullAddressRoad());
        }
        return "";
    }

    private String readRoadAddress(TmapPoiResponse.Poi poi) {
        String buildingNo1 = firstNonBlank(poi.getBuildingNo1(), poi.getFirstBuildNo());
        String buildingNo2 = firstNonBlank(poi.getBuildingNo2(), poi.getSecondBuildNo());
        String buildingNo = joinBuildingNo(buildingNo1, buildingNo2);

        if (trimToEmpty(poi.getRoadName()).isBlank()) return "";

        return joinNonBlank(
                poi.getUpperAddrName(),
                poi.getMiddleAddrName(),
                poi.getRoadName(),
                buildingNo
        );
    }

    private String readLotAddress(TmapPoiResponse.Poi poi) {
        String lotNo = joinBuildingNo(poi.getFirstNo(), poi.getSecondNo());
        return joinNonBlank(
                poi.getUpperAddrName(),
                poi.getMiddleAddrName(),
                poi.getLowerAddrName(),
                poi.getDetailAddrName(),
                lotNo
        );
    }

    private String resolveCategory(TmapPoiResponse.Poi poi, List<String> requestedCategories) {
        String categoryText = String.join(" ",
                safeText(poi.getUpperBizName(), ""),
                safeText(poi.getMiddleBizName(), ""),
                safeText(poi.getLowerBizName(), ""),
                safeText(poi.getName(), "")
        ).toLowerCase(Locale.ROOT);

        if (categoryText.contains("atm") || categoryText.contains("cd기")) return "ATM";
        if (categoryText.contains("화장실") || categoryText.contains("toilet")) return "화장실";
        if (categoryText.contains("편의점")
                || categoryText.contains("세븐일레븐")
                || categoryText.contains("7-eleven")
                || categoryText.contains("gs25")
                || categoryText.contains("cu")
                || categoryText.contains("이마트24")
                || categoryText.contains("미니스톱")
                || categoryText.contains("매점")) return "편의점";
        if (categoryText.contains("은행")
                || categoryText.contains("농협")
                || categoryText.contains("수협")
                || categoryText.contains("신협")
                || categoryText.contains("새마을금고")
                || categoryText.contains("우체국")) return "은행";
        if (requestedCategories.size() == 1) return requestedCategories.get(0);
        return "편의시설";
    }

    private int resolveDistance(
            TmapPoiResponse.Poi poi,
            double centerLat,
            double centerLng,
            double poiLat,
            double poiLng
    ) {
        Double tmapDistanceKm = parseDouble(poi.getRadius());
        if (tmapDistanceKm != null) return (int) Math.round(tmapDistanceKm * 1000);
        return (int) Math.round(calculateDistanceMeters(centerLat, centerLng, poiLat, poiLng));
    }

    private double calculateDistanceMeters(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return earthRadius * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    private Double parseDouble(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException error) {
            return null;
        }
    }

    private String safeText(String value, String fallback) {
        if (value == null || value.isBlank()) return fallback;
        return value;
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private String firstNonBlank(String... values) {
        return Arrays.stream(values)
                .map(this::trimToEmpty)
                .filter(value -> !value.isBlank())
                .findFirst()
                .orElse("");
    }

    private String joinBuildingNo(String firstNo, String secondNo) {
        String first = trimToEmpty(firstNo);
        String second = trimToEmpty(secondNo);
        if (first.isBlank()) return "";
        if (second.isBlank() || "0".equals(second)) return first;
        return first + "-" + second;
    }

    private String joinNonBlank(String... parts) {
        return Arrays.stream(parts)
                .map(this::trimToEmpty)
                .filter(value -> !value.isBlank())
                .collect(Collectors.joining(" "));
    }
}