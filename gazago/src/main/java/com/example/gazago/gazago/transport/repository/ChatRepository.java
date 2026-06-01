package com.example.gazago.gazago.transport.repository;

import com.example.gazago.gazago.transport.entity.UserRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<UserRequest, Long> {
}