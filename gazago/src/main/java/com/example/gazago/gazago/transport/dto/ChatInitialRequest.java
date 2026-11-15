// 사용자가 입력한 첫번째 채팅 전체 문장(React -> Spring)
package com.example.gazago.gazago.transport.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatInitialRequest {
    private String message; //사용자 첫 메시지
    private Double lat; // (선택) 기기 현재 위치. 출발지를 말하지 않으면 출발지로 사용
    private Double lng;
    // (선택) 정확한 도착지. 행사 상세의 "길찾기"처럼 도착지 좌표를 이미 알 때 장소 검색 대신 사용
    private String destName;
    private String destAddress;
    private Double destLat;
    private Double destLng;
}
