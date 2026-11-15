package com.example.gazago.gazago.expense;

import com.example.gazago.gazago.user.entity.User;
import com.example.gazago.gazago.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

// 여행 가계부(지출·여행) 비즈니스 로직. 모든 조회·수정은 본인 데이터만
@Service
@RequiredArgsConstructor
@Transactional
public class ExpenseService {
    private static final List<String> CATEGORIES = List.of("교통", "식비", "쇼핑", "숙박", "입장료", "기타");

    private final ExpenseRepository expenseRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Expense> getExpenses(String loginId) {
        return expenseRepository.findByUserOrderBySpentOnDescIdDesc(member(loginId));
    }

    public Expense addExpense(String loginId, LocalDate spentOn, String title, Long amount, String category) {
        validateExpense(title, amount);
        return expenseRepository.save(Expense.builder()
                .user(member(loginId))
                .spentOn(spentOn != null ? spentOn : LocalDate.now())
                .title(title.trim())
                .amount(amount)
                .category(normalizeCategory(category))
                .build());
    }

    public Expense updateExpense(String loginId, Long id, LocalDate spentOn, String title, Long amount, String category) {
        validateExpense(title, amount);
        Expense expense = ownedExpense(id, loginId);
        expense.update(spentOn, title.trim(), amount, normalizeCategory(category));
        return expense;
    }

    public void deleteExpense(String loginId, Long id) {
        expenseRepository.delete(ownedExpense(id, loginId));
    }

    @Transactional(readOnly = true)
    public List<Trip> getTrips(String loginId) {
        return tripRepository.findByUserOrderByStartDateDesc(member(loginId));
    }

    public Trip addTrip(String loginId, String name, LocalDate startDate, LocalDate endDate) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("여행 이름을 입력해주세요.");
        if (name.trim().length() > 40) throw new IllegalArgumentException("여행 이름은 40자까지 쓸 수 있어요.");
        if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("여행 기간을 확인해주세요.");
        }
        return tripRepository.save(Trip.builder()
                .user(member(loginId)).name(name.trim()).startDate(startDate).endDate(endDate).build());
    }

    // 여행만 지움 (그 기간의 지출 기록은 그대로 남음)
    public void deleteTrip(String loginId, Long id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "여행을 찾을 수 없습니다."));
        if (!trip.getUser().getLoginId().equals(loginId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "다른 사용자의 여행입니다.");
        }
        tripRepository.delete(trip);
    }

    private void validateExpense(String title, Long amount) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("내용을 입력해주세요.");
        if (amount == null || amount <= 0) throw new IllegalArgumentException("금액은 0보다 커야 합니다.");
    }

    private String normalizeCategory(String category) {
        return CATEGORIES.contains(category) ? category : "기타";
    }

    private Expense ownedExpense(Long id, String loginId) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "지출을 찾을 수 없습니다."));
        if (!expense.getUser().getLoginId().equals(loginId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "다른 사용자의 지출입니다.");
        }
        return expense;
    }

    private User member(String loginId) {
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "다시 로그인해주세요."));
    }
}
