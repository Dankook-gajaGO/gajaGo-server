// Tmap 에서 가져온 장소 후보 리스트 (Tamp -> Spring)
package com.example.gazago.gazago.transport.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class TmapPoiResponse {
    private SearchPoiInfo searchPoiInfo;

    @Getter
    @NoArgsConstructor
    public static class SearchPoiInfo {
        private Pois pois;
    }

    @Getter
    @NoArgsConstructor
    public static class Pois {
        private List<Poi> poi;
    }

    @Getter
    @NoArgsConstructor
    public static class Poi {
        private String name;        // 장소명
        private String noorLat;     // 위도
        private String noorLon;     // 경도
        private NewAddressList newAddressList; // 도로명 주소 정보

        @Getter
        @NoArgsConstructor
        public static class NewAddressList {
            private List<NewAddress> newAddress;

            @Getter
            @NoArgsConstructor
            public static class NewAddress {
                private String fullAddressRoad; // 전체 도로명 주소
            }
        }
    }
}
