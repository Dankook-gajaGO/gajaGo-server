package com.example.gazago.gazago.expense;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// 가계부 환산용 환율 (비회원도 사용)
@RestController
@RequiredArgsConstructor
public class ExchangeRateController {
    private final ExchangeRateService exchangeRateService;

    @GetMapping("/api/exchange-rates")
    public Map<String, Object> rates() {
        return exchangeRateService.getRates();
    }
}
