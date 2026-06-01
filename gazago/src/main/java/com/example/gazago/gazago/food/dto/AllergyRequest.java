// 사용자의 알러지 정보를 받을 DTO (React -> Spring)
package com.example.gazago.gazago.food.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class AllergyRequest {
    private List<String> selectedAllergies; // 사용자가 체크한 알러지 목록
}
