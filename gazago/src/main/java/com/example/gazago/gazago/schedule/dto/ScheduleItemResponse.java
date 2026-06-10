package com.example.gazago.gazago.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleItemResponse {
    private Long itemId;
    private LocalDate date;
    private String type;
    private String title;
    private String memo;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer festivalContentId;
    private String festivalTitle;
    private String festivalImage;
    private String festivalAddr;
    private String placeName;
    private String address;
    private Double lat;
    private Double lng;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
