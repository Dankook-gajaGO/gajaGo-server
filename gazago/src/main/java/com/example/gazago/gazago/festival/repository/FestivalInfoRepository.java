package com.example.gazago.gazago.festival.repository;

import com.example.gazago.gazago.festival.entity.FestivalInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface FestivalInfoRepository extends JpaRepository<FestivalInfo, Integer> {
    // startdate가 [now]와 [targetDate] 사이인 데이터를 조회
    List<FestivalInfo> findByEventStartDateBetween(LocalDate start, LocalDate end);

}
