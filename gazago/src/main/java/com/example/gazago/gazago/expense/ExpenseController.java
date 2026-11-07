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

// 여행 가계부 (회원 전용). 정산·환율 계산은 앱에서 함
@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
@Transactional
public class ExpenseController {
    private static final List<String> CATEGORIES = List.of("교통", "식비", "쇼핑", "숙박", "입장료", "기타");

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public record ExpenseItem(Long id, LocalDate spentOn, String title, Long amount, String category) {
        static ExpenseItem from(Expense e) {
            return new ExpenseItem(e.getId(), e.getSpentOn(), e.getTitle(), e.getAmount(), e.getCategory());
        }
    }

    @GetMapping
    public List<ExpenseItem> list(@AuthenticationPrincipal String loginId) {
        return expenseRepository.findByUserOrderBySpentOnDescIdDesc(member(loginId)).stream().map(ExpenseItem::from).toList();
    }

    @PostMapping
    public ExpenseItem add(@RequestBody ExpenseItem req, @AuthenticationPrincipal String loginId) {
        validate(req);
        String category = CATEGORIES.contains(req.category()) ? req.category() : "기타";
        Expense saved = expenseRepository.save(Expense.builder()
                .user(member(loginId))
                .spentOn(req.spentOn() != null ? req.spentOn() : LocalDate.now())
                .title(req.title().trim())
                .amount(req.amount())
                .category(category)
                .build());
        return ExpenseItem.from(saved);
    }

    // 세부 내용(예: 00음식점)·금액·분류 수정
    @PutMapping("/{id}")
    public ExpenseItem update(@PathVariable Long id, @RequestBody ExpenseItem req, @AuthenticationPrincipal String loginId) {
        validate(req);
        Expense expense = owned(id, loginId);
        expense.update(req.spentOn(), req.title().trim(), req.amount(), CATEGORIES.contains(req.category()) ? req.category() : "기타");
        return ExpenseItem.from(expense);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal String loginId) {
        expenseRepository.delete(owned(id, loginId));
        return ResponseEntity.noContent().build();
    }

    private void validate(ExpenseItem req) {
        if (req.title() == null || req.title().isBlank()) throw new IllegalArgumentException("내용을 입력해주세요.");
        if (req.amount() == null || req.amount() <= 0) throw new IllegalArgumentException("금액은 0보다 커야 합니다.");
    }

    private Expense owned(Long id, String loginId) {
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
