package com.example.gazago.gazago.festival.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FestivalListResponse {
    private List<FestivalSummary> festivals;

    @Getter
    @AllArgsConstructor
    public static class FestivalSummary {
        private int contentId; //축제 ID
        private String title; //축제명
        private String image; //이미지
        private LocalDate eventStartDate;
        private LocalDate eventEndDate;
        private String addr; //주소
    }
}
