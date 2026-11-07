package com.example.gazago.gazago.expense;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// 가계부 환산용 환율 (누구나). 외부 API는 서버가 12시간에 한 번만 호출하고 앱은 이 캐시를 받음 -> 사용자 수와 무관하게 하루 2회 수준
@RestController
public class ExchangeRateController {
    // 방한객이 많은 국가 통화
    private static final List<String> CURRENCIES =
            List.of("USD", "JPY", "CNY", "TWD", "HKD", "EUR", "THB", "VND", "PHP", "SGD");
    private static final Duration TTL = Duration.ofHours(12);

    private final WebClient webClient = WebClient.create("https://open.er-api.com");
    private Map<String, Object> cached;
    private Instant cachedAt = Instant.EPOCH;

    @GetMapping("/api/exchange-rates")
    public synchronized Map<String, Object> rates() {
        if (cached != null && Instant.now().isBefore(cachedAt.plus(TTL))) return cached;
        try {
            Map<?, ?> body = webClient.get().uri("/v6/latest/KRW").retrieve().bodyToMono(Map.class)
                    .block(Duration.ofSeconds(10));
            Map<?, ?> perKrw = (Map<?, ?>) body.get("rates");
            Map<String, Double> krwPer = new LinkedHashMap<>();
            for (String c : CURRENCIES) {
                Object v = perKrw.get(c);
                if (v instanceof Number n && n.doubleValue() > 0) krwPer.put(c, 1 / n.doubleValue()); // 1 외화 = N원
            }
            cached = Map.of("krwPer", krwPer, "updatedAt", String.valueOf(body.get("time_last_update_utc")));
            cachedAt = Instant.now();
        } catch (Exception e) {
            // 외부 API 실패 시 마지막 환율을 그대로 제공. 한 번도 못 받았으면 앱이 원화만 표시
            if (cached == null) throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "환율을 불러오지 못했어요.");
        }
        return cached;
    }
}
