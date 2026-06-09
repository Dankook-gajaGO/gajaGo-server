package com.example.gazago.gazago.schedule.service;

import com.example.gazago.gazago.festival.entity.FestivalInfo;
import com.example.gazago.gazago.festival.repository.FestivalInfoRepository;
import com.example.gazago.gazago.schedule.dto.SavedFestivalListResponse;
import com.example.gazago.gazago.schedule.dto.SavedFestivalResponse;
import com.example.gazago.gazago.schedule.dto.ScheduleItemRequest;
import com.example.gazago.gazago.schedule.dto.ScheduleItemResponse;
import com.example.gazago.gazago.schedule.dto.ScheduleItemsResponse;
import com.example.gazago.gazago.schedule.entity.SavedFestival;
import com.example.gazago.gazago.schedule.entity.ScheduleItem;
import com.example.gazago.gazago.schedule.entity.ScheduleItemType;
import com.example.gazago.gazago.schedule.repository.SavedFestivalRepository;
import com.example.gazago.gazago.schedule.repository.ScheduleItemRepository;
import com.example.gazago.gazago.user.entity.User;
import com.example.gazago.gazago.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {
    private final UserRepository userRepository;
    private final FestivalInfoRepository festivalInfoRepository;
    private final SavedFestivalRepository savedFestivalRepository;
    private final ScheduleItemRepository scheduleItemRepository;

    public SavedFestivalListResponse getSavedFestivals(String loginId) {
        User user = getUser(loginId);

        return new SavedFestivalListResponse(
                savedFestivalRepository.findByUserOrderByCreatedAtDesc(user).stream()
                        .map(this::toSavedFestivalResponse)
                        .toList()
        );
    }

    @Transactional
    public SavedFestivalResponse saveFestival(String loginId, int contentId) {
        User user = getUser(loginId);
        FestivalInfo festival = getFestival(contentId);

        return savedFestivalRepository.findByUserAndFestival(user, festival)
                .map(this::toSavedFestivalResponse)
                .orElseGet(() -> {
                    SavedFestival savedFestival = new SavedFestival();
                    savedFestival.setUser(user);
                    savedFestival.setFestival(festival);
                    return toSavedFestivalResponse(savedFestivalRepository.save(savedFestival));
                });
    }

    @Transactional
    public void deleteSavedFestival(String loginId, int contentId) {
        User user = getUser(loginId);
        FestivalInfo festival = getFestival(contentId);

        if (savedFestivalRepository.existsByUserAndFestival(user, festival)) {
            savedFestivalRepository.deleteByUserAndFestival(user, festival);
        }
    }

    public ScheduleItemsResponse getScheduleItemsByDate(String loginId, LocalDate date) {
        User user = getUser(loginId);

        return new ScheduleItemsResponse(
                scheduleItemRepository.findByUserAndScheduleDateOrderBySortOrderAscCreatedAtAsc(user, date).stream()
                        .map(this::toScheduleItemResponse)
                        .toList()
        );
    }

    public ScheduleItemsResponse getScheduleItemsByMonth(String loginId, int year, int month) {
        User user = getUser(loginId);
        YearMonth yearMonth = YearMonth.of(year, month);

        return new ScheduleItemsResponse(
                scheduleItemRepository.findByUserAndScheduleDateBetweenOrderByScheduleDateAscSortOrderAscCreatedAtAsc(
                                user,
                                yearMonth.atDay(1),
                                yearMonth.atEndOfMonth()
                        ).stream()
                        .map(this::toScheduleItemResponse)
                        .toList()
        );
    }

    @Transactional
    public ScheduleItemResponse createScheduleItem(String loginId, ScheduleItemRequest request) {
        User user = getUser(loginId);
        FestivalInfo festival = request.getFestivalContentId() == null ? null : getFestival(request.getFestivalContentId());
        String title = resolveTitle(request.getTitle(), festival);

        ScheduleItem item = new ScheduleItem();
        item.setUser(user);
        item.setScheduleDate(requireDate(request.getDate()));
        item.setItemType(resolveType(request.getType()));
        item.setTitle(title);
        item.setMemo(trimToNull(request.getMemo()));
        item.setStartTime(request.getStartTime());
        item.setEndTime(request.getEndTime());
        item.setFestival(festival);
        item.setPlaceName(trimToNull(request.getPlaceName()));
        item.setAddress(trimToNull(request.getAddress()));
        item.setLat(request.getLat());
        item.setLng(request.getLng());
        item.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());

        return toScheduleItemResponse(scheduleItemRepository.save(item));
    }

    @Transactional
    public ScheduleItemResponse updateScheduleItem(String loginId, Long itemId, ScheduleItemRequest request) {
        User user = getUser(loginId);
        ScheduleItem item = scheduleItemRepository.findByItemIdAndUser(itemId, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule item not found"));
        FestivalInfo festival = request.getFestivalContentId() == null ? null : getFestival(request.getFestivalContentId());

        item.setScheduleDate(requireDate(request.getDate()));
        item.setItemType(resolveType(request.getType()));
        item.setTitle(resolveTitle(request.getTitle(), festival));
        item.setMemo(trimToNull(request.getMemo()));
        item.setStartTime(request.getStartTime());
        item.setEndTime(request.getEndTime());
        item.setFestival(festival);
        item.setPlaceName(trimToNull(request.getPlaceName()));
        item.setAddress(trimToNull(request.getAddress()));
        item.setLat(request.getLat());
        item.setLng(request.getLng());
        item.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());

        return toScheduleItemResponse(item);
    }

    @Transactional
    public void deleteScheduleItem(String loginId, Long itemId) {
        User user = getUser(loginId);
        ScheduleItem item = scheduleItemRepository.findByItemIdAndUser(itemId, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule item not found"));
        scheduleItemRepository.delete(item);
    }

    private User getUser(String loginId) {
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private FestivalInfo getFestival(int contentId) {
        return festivalInfoRepository.findById(contentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Festival not found"));
    }

    private LocalDate requireDate(LocalDate date) {
        if (date == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "date is required");
        }
        return date;
    }

    private ScheduleItemType resolveType(String value) {
        if (value == null || value.isBlank()) return ScheduleItemType.CUSTOM;

        try {
            return ScheduleItemType.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException error) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid schedule item type");
        }
    }

    private String resolveTitle(String title, FestivalInfo festival) {
        String normalized = trimToNull(title);
        if (normalized != null) return normalized;
        if (festival != null && festival.getTitle() != null && !festival.getTitle().isBlank()) return festival.getTitle();
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "title is required");
    }

    private String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private SavedFestivalResponse toSavedFestivalResponse(SavedFestival savedFestival) {
        FestivalInfo festival = savedFestival.getFestival();
        return new SavedFestivalResponse(
                savedFestival.getSavedFestivalId(),
                festival.getContentId(),
                festival.getTitle(),
                festival.getImage(),
                festival.getEventStartDate(),
                festival.getEventEndDate(),
                festival.getAddr(),
                savedFestival.getCreatedAt()
        );
    }

    private ScheduleItemResponse toScheduleItemResponse(ScheduleItem item) {
        FestivalInfo festival = item.getFestival();
        return new ScheduleItemResponse(
                item.getItemId(),
                item.getScheduleDate(),
                item.getItemType().name(),
                item.getTitle(),
                item.getMemo(),
                item.getStartTime(),
                item.getEndTime(),
                festival == null ? null : festival.getContentId(),
                festival == null ? null : festival.getTitle(),
                festival == null ? null : festival.getImage(),
                festival == null ? null : festival.getAddr(),
                item.getPlaceName(),
                item.getAddress(),
                item.getLat(),
                item.getLng(),
                item.getSortOrder(),
                item.getCreatedAt(),
                item.getUpdatedAt()
        );
    }
}
