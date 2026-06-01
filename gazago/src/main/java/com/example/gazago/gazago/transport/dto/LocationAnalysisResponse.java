// 추출된 출발지 명칭, 추출된 목적지 명칭, 추출된 사용자 주의사항 (FastApi -> Spring)
package com.example.gazago.gazago.transport.dto;

import lombok.Getter;

@Getter
public class LocationAnalysisResponse {
    private String departure; //추출된 출발지명
    private String destination; //추출된 목적지명
    private String constraints; // 추출된 주의사항들
}