package com.example.gazago.gazago.festival.controller;

import com.example.gazago.gazago.festival.dto.FestivalDetailResponse;
import com.example.gazago.gazago.festival.dto.FestivalListResponse;
import com.example.gazago.gazago.festival.service.FestivalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/festival")
public class FestivalController {
    private final FestivalService festivalService;

    public FestivalController(FestivalService festivalService){
        this.festivalService = festivalService;
    }

    @GetMapping("/festivalList")
    public ResponseEntity<FestivalListResponse> getFestivalLists(@RequestParam int months) {
        FestivalListResponse festivalList = festivalService.getFestivalList(months);
        return ResponseEntity.ok(festivalList);
    }

    @PostMapping("/festivalDetail")
    public ResponseEntity<FestivalDetailResponse> getFestivalDetail(@RequestParam int contentId) {
        FestivalDetailResponse festivalDetail = festivalService.getFestivalDetail(contentId);
        return ResponseEntity.ok(festivalDetail);
    }
}
