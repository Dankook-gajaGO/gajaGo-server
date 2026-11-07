package com.example.gazago.gazago.expense;

import com.example.gazago.gazago.user.entity.User;
import com.example.gazago.gazago.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

// 여행 정보 (회원 전용): 가계부를 여행별로 묶고 캘린더에 여행 기간을 표시
@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
@Transactional
public class TripController {
    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    public record TripItem(Long id, String name, LocalDate startDate, LocalDate endDate) {
        static TripItem from(Trip t) {
            return new TripItem(t.getId(), t.getName(), t.getStartDate(), t.getEndDate());
        }
    }

    @GetMapping
    public List<TripItem> list(@AuthenticationPrincipal String loginId) {
        return tripRepository.findByUserOrderByStartDateDesc(member(loginId)).stream().map(TripItem::from).toList();
    }

    @PostMapping
    public TripItem add(@RequestBody TripItem req, @AuthenticationPrincipal String loginId) {
        if (req.name() == null || req.name().isBlank()) throw new IllegalArgumentException("여행 이름을 입력해주세요.");
        if (req.name().trim().length() > 40) throw new IllegalArgumentException("여행 이름은 40자까지 쓸 수 있어요.");
        if (req.startDate() == null || req.endDate() == null || req.endDate().isBefore(req.startDate())) {
            throw new IllegalArgumentException("여행 기간을 확인해주세요.");
        }
        Trip saved = tripRepository.save(Trip.builder()
                .user(member(loginId)).name(req.name().trim()).startDate(req.startDate()).endDate(req.endDate()).build());
        return TripItem.from(saved);
    }

    // 여행만 지움 (그 기간의 지출 기록은 그대로 남음)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal String loginId) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "여행을 찾을 수 없습니다."));
        if (!trip.getUser().getLoginId().equals(loginId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "다른 사용자의 여행입니다.");
        }
        tripRepository.delete(trip);
        return ResponseEntity.noContent().build();
    }

    private User member(String loginId) {
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "다시 로그인해주세요."));
    }
}
