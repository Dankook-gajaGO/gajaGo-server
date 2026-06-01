package com.example.gazago.gazago.food.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "allergy_mapping")
@Getter
@Setter
@NoArgsConstructor
public class AllergyMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mapping_id")
    private Long mappingId;

    @Column(name = "allergy_category", nullable = false)
    private String allergyCategory;

    @Column(name = "ingredient_nm", nullable = false)
    private String ingredientNm;
}