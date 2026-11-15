package com.example.gazago.gazago.festival.repository;

import com.example.gazago.gazago.festival.entity.FestivalInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface FestivalInfoRepository extends JpaRepository<FestivalInfo, Integer> {
    // [targetDate] 이전에 시작하고 [now] 이후에 끝나는 축제 = 진행 중 + N달 안에 시작
    List<FestivalInfo> findByEventStartDateLessThanEqualAndEventEndDateGreaterThanEqual(LocalDate targetDate, LocalDate now);

}
