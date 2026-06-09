package com.example.gazago.gazago.schedule.repository;

import com.example.gazago.gazago.festival.entity.FestivalInfo;
import com.example.gazago.gazago.schedule.entity.SavedFestival;
import com.example.gazago.gazago.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedFestivalRepository extends JpaRepository<SavedFestival, Long> {
    List<SavedFestival> findByUserOrderByCreatedAtDesc(User user);

    Optional<SavedFestival> findByUserAndFestival(User user, FestivalInfo festival);

    boolean existsByUserAndFestival(User user, FestivalInfo festival);

    void deleteByUserAndFestival(User user, FestivalInfo festival);
}
