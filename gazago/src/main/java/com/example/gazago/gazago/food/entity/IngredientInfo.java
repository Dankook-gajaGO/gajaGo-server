package com.example.gazago.gazago.food.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ingredient_info")
@Getter
@Setter
public class IngredientInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_id")
    private Long ingredientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id")
    private FoodInfo foodInfo;

    @Column(name = "ingredient_nm")
    private String ingredientNm;

    @Column(name = "ty_nm")
    private String tyNm;
}