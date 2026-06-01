// 이전에 받은 요청 ID, 사용자가 선택한 장소 (React -> Spring)
package com.example.gazago.gazago.transport.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LocationSelectRequest {
    private Long requestId; //요청 ID

    private SelectedLocation selectedDestination; //사용자가 선택한 출발지 장소
    private SelectedLocation selectedDeparture; //사용자가 선택한 목적지 장소

    @Getter
    @NoArgsConstructor
    public static class SelectedLocation {
        private String name; //장소명
        private String address; //주소
        private double lat; //위도
        private double lng; //경도
    }
}
