package com.example.gazago.gazago.food.service;

import com.example.gazago.gazago.food.dto.FoodResponse;
import com.example.gazago.gazago.food.entity.FoodInfo;
import com.example.gazago.gazago.food.repository.FoodInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FoodService {
    private final FoodInfoRepository foodInfoRepository;

    public FoodService(FoodInfoRepository foodInfoRepository) {
        this.foodInfoRepository = foodInfoRepository;
    }

    // 컨트롤러에서 매개변수로 알러지 리스트를 넘겨받음
    @Transactional(readOnly = true)
    public FoodResponse getSafeFoods(List<String> selectedAllergies) {
        // 알러지 리스트가 비어있을 경우
        if (selectedAllergies == null || selectedAllergies.isEmpty()) {
            return FoodResponse.from(foodInfoRepository.findAll());
        }

        List<FoodInfo> foodList = foodInfoRepository.findAllExceptAllergyFoods(selectedAllergies);

        return FoodResponse.from(foodList);

    }
}
