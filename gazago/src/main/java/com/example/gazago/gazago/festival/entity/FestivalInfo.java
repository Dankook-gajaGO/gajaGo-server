package com.example.gazago.gazago.festival.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
@Table(name="festival_info")
@Getter
@Setter
@NoArgsConstructor
public class FestivalInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="contentid")
    private int contentId;

    @Column(name="title")
    private String title;

    @Column(name="addr1")
    private String addr;

    @Column(name="zipcode")
    private int zipCode;

    @Column(name="eventstartdate")
    private LocalDate eventStartDate;

    @Column(name="eventenddate")
    private LocalDate eventEndDate;

    @Column(name="image")
    private String image;

    @Column(name="tel")
    private String tel;

    @Column(name="fee")
    private String fee;

    @Column(name="eventhomepage")
    private String eventHomepage;

    @Column(name="overview")
    private String overview;
}
