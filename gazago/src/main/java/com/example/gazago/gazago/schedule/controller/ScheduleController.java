package com.example.gazago.gazago.schedule.controller;

import com.example.gazago.gazago.schedule.dto.SavedFestivalListResponse;
import com.example.gazago.gazago.schedule.dto.SavedFestivalResponse;
import com.example.gazago.gazago.schedule.dto.ScheduleItemRequest;
import com.example.gazago.gazago.schedule.dto.ScheduleItemResponse;
import com.example.gazago.gazago.schedule.dto.ScheduleItemsResponse;
import com.example.gazago.gazago.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ScheduleController {
    private final ScheduleService scheduleService;

    @GetMapping("/saved-festivals")
    public SavedFestivalListResponse getSavedFestivals(Authentication authentication) {
        return scheduleService.getSavedFestivals(authentication.getName());
    }

    @PostMapping("/saved-festivals/{contentId}")
    public SavedFestivalResponse saveFestival(Authentication authentication, @PathVariable int contentId) {
        return scheduleService.saveFestival(authentication.getName(), contentId);
    }

    @DeleteMapping("/saved-festivals/{contentId}")
    public ResponseEntity<Void> deleteSavedFestival(Authentication authentication, @PathVariable int contentId) {
        scheduleService.deleteSavedFestival(authentication.getName(), contentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/schedules")
    public ScheduleItemsResponse getScheduleItems(Authentication authentication, @RequestParam LocalDate date) {
        return scheduleService.getScheduleItemsByDate(authentication.getName(), date);
    }

    @GetMapping("/schedules/month")
    public ScheduleItemsResponse getMonthlyScheduleItems(
            Authentication authentication,
            @RequestParam int year,
            @RequestParam int month
    ) {
        return scheduleService.getScheduleItemsByMonth(authentication.getName(), year, month);
    }

    @PostMapping("/schedules/items")
    public ScheduleItemResponse createScheduleItem(
            Authentication authentication,
            @RequestBody ScheduleItemRequest request
    ) {
        return scheduleService.createScheduleItem(authentication.getName(), request);
    }

    @PutMapping("/schedules/items/{itemId}")
    public ScheduleItemResponse updateScheduleItem(
            Authentication authentication,
            @PathVariable Long itemId,
            @RequestBody ScheduleItemRequest request
    ) {
        return scheduleService.updateScheduleItem(authentication.getName(), itemId, request);
    }

    @DeleteMapping("/schedules/items/{itemId}")
    public ResponseEntity<Void> deleteScheduleItem(Authentication authentication, @PathVariable Long itemId) {
        scheduleService.deleteScheduleItem(authentication.getName(), itemId);
        return ResponseEntity.noContent().build();
    }
}
