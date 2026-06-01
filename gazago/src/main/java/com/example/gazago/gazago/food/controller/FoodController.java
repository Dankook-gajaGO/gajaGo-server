package com.example.gazago.gazago.food.controller;

import com.example.gazago.gazago.food.dto.AllergyRequest;
import com.example.gazago.gazago.food.dto.FoodResponse;
import com.example.gazago.gazago.food.service.FoodService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/food")
public class FoodController {
    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @PostMapping("/safeFoodList")
    public ResponseEntity<FoodResponse> getSafeFoods(@RequestBody AllergyRequest request) {
        FoodResponse safeFoods = foodService.getSafeFoods(request.getSelectedAllergies());
        return ResponseEntity.ok(safeFoods);
    }
}
