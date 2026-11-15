package com.example.gazago.gazago.expense;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

// 여행 정보 (회원 전용): 가계부를 여행별로 묶고 캘린더에 여행 기간을 표시
@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {
    private final ExpenseService expenseService;

    public record TripItem(Long id, String name, LocalDate startDate, LocalDate endDate) {
        static TripItem from(Trip t) {
            return new TripItem(t.getId(), t.getName(), t.getStartDate(), t.getEndDate());
        }
    }

    @GetMapping
    public List<TripItem> list(@AuthenticationPrincipal String loginId) {
        return expenseService.getTrips(loginId).stream().map(TripItem::from).toList();
    }

    @PostMapping
    public TripItem add(@RequestBody TripItem req, @AuthenticationPrincipal String loginId) {
        return TripItem.from(expenseService.addTrip(loginId, req.name(), req.startDate(), req.endDate()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal String loginId) {
        expenseService.deleteTrip(loginId, id);
        return ResponseEntity.noContent().build();
    }
}
