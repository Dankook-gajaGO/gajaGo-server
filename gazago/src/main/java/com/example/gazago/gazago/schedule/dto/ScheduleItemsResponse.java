package com.example.gazago.gazago.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleItemsResponse {
    private List<ScheduleItemResponse> items;
}
