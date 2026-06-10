package com.example.gazago.gazago.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SavedFestivalResponse {
    private Long savedFestivalId;
    private int contentId;
    private String title;
    private String image;
    private LocalDate eventStartDate;
    private LocalDate eventEndDate;
    private String addr;
    private LocalDateTime createdAt;
}
