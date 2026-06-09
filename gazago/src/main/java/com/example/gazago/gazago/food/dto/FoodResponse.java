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
        private List<String> foodNames;
        private List<FoodItemDto> foods;
    }

    @Getter
    @AllArgsConstructor
    public static class FoodItemDto {
        private Long foodId;
        private String foodName;
        private String foodNm;
        private String category;
        private List<String> ingredients;
        private String ingredientNm;
    }

    public static FoodResponse from(List<FoodInfo> foodInfoList) {
        Map<String, List<FoodInfo>> grouped = foodInfoList.stream()
                .collect(Collectors.groupingBy(food -> normalizeCategory(food.getTyNm())));

        List<CategoryDto> categoryList = grouped.entrySet().stream()
                .map(entry -> new CategoryDto(
                        entry.getKey(),
                        entry.getValue().stream().map(FoodInfo::getFoodNm).collect(Collectors.toList()),
                        entry.getValue().stream().map(FoodResponse::toFoodItemDto).collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

        return new FoodResponse(categoryList);
    }

    private static FoodItemDto toFoodItemDto(FoodInfo food) {
        List<String> ingredients = food.getIngredients().stream()
                .map(ingredient -> ingredient.getIngredientNm())
                .filter(value -> value != null && !value.isBlank())
                .distinct()
                .collect(Collectors.toList());

        return new FoodItemDto(
                food.getFoodId(),
                food.getFoodNm(),
                food.getFoodNm(),
                normalizeCategory(food.getTyNm()),
                ingredients,
                String.join(", ", ingredients)
        );
    }

    private static String normalizeCategory(String category) {
        return category == null || category.isBlank() ? "기타" : category;
    }
}