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
    private Integer zipCode;

    @Column(name="eventstartdate")
    private LocalDate eventStartDate;

    @Column(name="eventenddate")
    private LocalDate eventEndDate;

    // TourAPI 값은 255자를 넘는 경우가 많아 길이를 명시 (ddl-auto=update 가 DB 컬럼을 이 정의로 맞춤)
    @Column(name="image", length = 500)
    private String image;

    @Column(name="tel")
    private String tel;

    @Column(name="fee", columnDefinition = "TEXT")
    private String fee;

    @Column(name="eventhomepage", columnDefinition = "TEXT")
    private String eventHomepage;

    @Column(name="overview", columnDefinition = "TEXT")
    private String overview;

    // TourAPI mapx(경도)/mapy(위도). 길찾기 도착지로 사용
    @Column(name="lat")
    private Double lat;

    @Column(name="lng")
    private Double lng;
}
