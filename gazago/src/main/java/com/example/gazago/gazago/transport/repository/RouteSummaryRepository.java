package com.example.gazago.gazago.transport.repository;

import com.example.gazago.gazago.transport.entity.RouteSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteSummaryRepository extends JpaRepository<RouteSummary, Long> {
    // 외래키인 userRequest의 id를 기반으로 경로 목록을 우선순위(rank) 오름차순으로 정렬해 긁어옴
    List<RouteSummary> findByUserRequestIdOrderByRankAsc(Long requestId);

    // ChatService에서 사용 중인 기본 메서드 이름도 호환되도록 추가해 둠
    default List<RouteSummary> findByUserRequestId(Long requestId) {
        return findByUserRequestIdOrderByRankAsc(requestId);
    }
}
