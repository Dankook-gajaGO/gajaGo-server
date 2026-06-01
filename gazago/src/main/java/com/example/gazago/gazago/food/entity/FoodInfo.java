package com.example.gazago.gazago.food.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "food_info")
@Getter
@Setter
@NoArgsConstructor
public class FoodInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_id")
    private Long foodId;

    @Column(name = "food_nm", nullable = false)
    private String foodNm;

    @Column(name = "nation_nm")
    private String nationNm;

    @Column(name = "ty_nm")
    private String tyNm;

    @OneToMany(mappedBy = "foodInfo", cascade = CascadeType.ALL)
    private List<IngredientInfo> ingredients = new ArrayList<>();
}
