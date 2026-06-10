package com.example.gazago.gazago.facility.controller;

import com.example.gazago.gazago.facility.dto.FacilityNearbyResponse;
import com.example.gazago.gazago.facility.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/facilities")
public class FacilityController {
    private final FacilityService facilityService;

    @GetMapping("/nearby")
    public FacilityNearbyResponse nearby(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "3") int radius,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String categories
    ) {
        return facilityService.getNearbyFacilities(lat, lng, radius, offset, size, categories);
    }
}