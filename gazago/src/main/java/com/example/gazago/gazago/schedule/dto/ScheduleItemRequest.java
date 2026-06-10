package com.example.gazago.gazago.schedule.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleItemRequest {
    private LocalDate date;
    private String type;
    private String title;
    private String memo;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer festivalContentId;
    private String placeName;
    private String address;
    private Double lat;
    private Double lng;
    private Integer sortOrder;
}
