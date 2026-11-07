package com.example.gazago.gazago.expense;

import com.example.gazago.gazago.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByUserOrderByStartDateDesc(User user);
}
