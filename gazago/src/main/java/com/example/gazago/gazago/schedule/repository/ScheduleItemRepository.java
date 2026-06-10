package com.example.gazago.gazago.schedule.repository;

import com.example.gazago.gazago.schedule.entity.ScheduleItem;
import com.example.gazago.gazago.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScheduleItemRepository extends JpaRepository<ScheduleItem, Long> {
    List<ScheduleItem> findByUserAndScheduleDateOrderBySortOrderAscCreatedAtAsc(User user, LocalDate scheduleDate);

    List<ScheduleItem> findByUserAndScheduleDateBetweenOrderByScheduleDateAscSortOrderAscCreatedAtAsc(
            User user,
            LocalDate startDate,
            LocalDate endDate
    );

    Optional<ScheduleItem> findByItemIdAndUser(Long itemId, User user);
}
