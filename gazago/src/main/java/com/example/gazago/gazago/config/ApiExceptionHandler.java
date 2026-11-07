package com.example.gazago.gazago.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

// 의도해서 던진 ResponseStatusException 의 사유만 앱에 {"message": ...} 로 전달 (그 외 내부 오류 내용은 숨김)
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handle(ResponseStatusException e) {
        String message = e.getReason() != null ? e.getReason() : "요청을 처리하지 못했습니다.";
        return ResponseEntity.status(e.getStatusCode()).body(Map.of("message", message));
    }
}
