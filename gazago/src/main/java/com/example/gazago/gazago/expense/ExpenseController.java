package com.example.gazago.gazago.expense;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

// 여행 가계부 지출 (회원 전용). 정산·환율 계산은 앱에서 함
@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {
    private final ExpenseService expenseService;

    public record ExpenseItem(Long id, LocalDate spentOn, String title, Long amount, String category) {
        static ExpenseItem from(Expense e) {
            return new ExpenseItem(e.getId(), e.getSpentOn(), e.getTitle(), e.getAmount(), e.getCategory());
        }
    }

    @GetMapping
    public List<ExpenseItem> list(@AuthenticationPrincipal String loginId) {
        return expenseService.getExpenses(loginId).stream().map(ExpenseItem::from).toList();
    }

    @PostMapping
    public ExpenseItem add(@RequestBody ExpenseItem req, @AuthenticationPrincipal String loginId) {
        return ExpenseItem.from(expenseService.addExpense(loginId, req.spentOn(), req.title(), req.amount(), req.category()));
    }

    @PutMapping("/{id}")
    public ExpenseItem update(@PathVariable Long id, @RequestBody ExpenseItem req, @AuthenticationPrincipal String loginId) {
        return ExpenseItem.from(expenseService.updateExpense(loginId, id, req.spentOn(), req.title(), req.amount(), req.category()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal String loginId) {
        expenseService.deleteExpense(loginId, id);
        return ResponseEntity.noContent().build();
    }
}
