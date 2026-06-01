package com.example.gazago.gazago.festival.service;

import com.example.gazago.gazago.festival.dto.FestivalDetailResponse;
import com.example.gazago.gazago.festival.dto.FestivalListResponse;
import com.example.gazago.gazago.festival.entity.FestivalInfo;
import com.example.gazago.gazago.festival.repository.FestivalInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FestivalService {
    private final FestivalInfoRepository festivalInfoRepository;

    public FestivalService(FestivalInfoRepository festivalInfoRepository) {
        this.festivalInfoRepository = festivalInfoRepository;
    }

    @Transactional(readOnly = true)
    public FestivalListResponse getFestivalList(int months) {
        // 조회 시점을 기준으로 "1달", "2달", "3달" 까지의 축제 목록 제공
        LocalDate now = LocalDate.now(); //오늘
        LocalDate targetDate = now.plusMonths(months); //오늘 + N달

        // 조회 범위 : 지금 ~ N달 뒤
        List<FestivalInfo> festivals = festivalInfoRepository.findByEventStartDateBetween(now, targetDate);

        List<FestivalListResponse.FestivalSummary> summaries = festivals.stream()
                .map(f -> new FestivalListResponse.FestivalSummary(
                        f.getContentId(), f.getTitle(), f.getImage(),
                        f.getEventStartDate(), f.getEventEndDate(), f.getAddr()
                )).collect(Collectors.toList());

        return new FestivalListResponse(summaries);
    }

    @Transactional(readOnly = true)
    public FestivalDetailResponse getFestivalDetail(int contentId) {
        // 해당 contentid의 나머지 컬럼 가져오기
        FestivalInfo info = festivalInfoRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 축제를 찾을 수 없습니다."));

        return new FestivalDetailResponse(
                info.getContentId(), info.getTitle(), info.getAddr(), info.getZipCode(),
                info.getEventStartDate(), info.getEventEndDate(), info.getImage(),
                info.getTel(), info.getFee(), info.getEventHomepage(), info.getOverview()
        );

    }
}
