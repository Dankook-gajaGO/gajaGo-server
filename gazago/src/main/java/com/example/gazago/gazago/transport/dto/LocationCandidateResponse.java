// Tmap에서 가져온 후보 장소 리스트 (Spring -> React)
package com.example.gazago.gazago.transport.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class LocationCandidateResponse {
    private Long requestId; //요청을 식별할 ID
    private List<LocationInfo> departurecandidates; //출발지 장소 정보들을 담은 리스트
    private List<LocationInfo> destinationcandidates; //목적지 장소 정보들을 담은 리스트

    @Getter
    @AllArgsConstructor
    public static class LocationInfo {
        private String name; //장소명
        private String address; //상세 주소
        private double lat;
        private double lng;
    }

}
