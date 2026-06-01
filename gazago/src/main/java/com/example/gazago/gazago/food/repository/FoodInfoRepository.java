package com.example.gazago.gazago.food.repository;

import com.example.gazago.gazago.food.entity.FoodInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface FoodInfoRepository extends JpaRepository<FoodInfo, Long> {

    // AllergyMapping 테이블에서 사용자가 선택한 알러지 종류 (:allergies)에 해당하는 알러지 유발 재료명들을 모두 가져옴
    // IngredientInfo 테이블에서 위에서 찾은 '알러지 유발 재료'들이 포함된 모든 음식의 foodId를 가져옴
    // FoodInfo 테이블에서 전체 음식 목록을 조회하되, NOT IN을 사용하여 "피해야 할 음식들의 ID 목록"에 포함되지 않은 음식들만 최종적으로 선택
    @Query("SELECT f FROM FoodInfo f WHERE f.foodId NOT IN (" +
            "SELECT i.foodInfo.foodId FROM IngredientInfo i " +
            "WHERE i.ingredientNm IN (" +
            "  SELECT am.ingredientNm FROM AllergyMapping am " +
            "  WHERE am.allergyCategory IN :allergies" +
            "))")

    List<FoodInfo> findAllExceptAllergyFoods(@Param("allergies") List<String> allergies);
}