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
        private String id;
        private String name;
        private String noorLat;
        private String noorLon;
        private String radius;
        private String upperAddrName;
        private String middleAddrName;
        private String lowerAddrName;
        private String detailAddrName;
        private String firstNo;
        private String secondNo;
        private String roadName;
        private String firstBuildNo;
        private String secondBuildNo;
        private String buildingNo1;
        private String buildingNo2;
        private String upperBizName;
        private String middleBizName;
        private String lowerBizName;
        private NewAddressList newAddressList;

        @Getter
        @NoArgsConstructor
        public static class NewAddressList {
            private List<NewAddress> newAddress;

            @Getter
            @NoArgsConstructor
            public static class NewAddress {
                private String fullAddressRoad;
            }
        }
    }
}