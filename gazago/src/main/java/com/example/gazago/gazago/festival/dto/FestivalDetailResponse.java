package com.example.gazago.gazago.festival.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FestivalDetailResponse {
    private int contentId;
    private String title;
    private String addr;
    private int zipCode;
    private LocalDate eventStartDate;
    private LocalDate eventEndDate;
    private String image;
    private String tel;
    private String fee;
    private String eventHomepage;
    private String overview;
}
