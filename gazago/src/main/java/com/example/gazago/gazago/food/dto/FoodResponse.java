// 섭취 가능한 음식 목록 반환 (Spring -> React)
package com.example.gazago.gazago.food.dto;

import com.example.gazago.gazago.food.entity.FoodInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FoodResponse {
    private List<CategoryDto> categories;

    @Getter
    @AllArgsConstructor
    public static class CategoryDto {
        private String category;
        private List<String> foodNames; // 해당 카테고리에 속한 음식 이름들
    }

    public static FoodResponse from(List<FoodInfo> foodInfoList) {
        // 1. 카테고리별로 그룹화
        Map<String, List<FoodInfo>> grouped = foodInfoList.stream()
                .collect(Collectors.groupingBy(FoodInfo::getTyNm));

        // 2. DTO 구조로 변환
        List<CategoryDto> categoryList = grouped.entrySet().stream()
                .map(entry -> new CategoryDto(
                        entry.getKey(),
                        entry.getValue().stream().map(FoodInfo::getFoodNm).collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

        return new FoodResponse(categoryList);
    }
}
