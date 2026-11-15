package com.example.gazago.gazago.user.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;

// 관리자 웹 페이지(/admin.html)용 회원 조회·삭제. JWT 대신 .env 의 ADMIN_PASSWORD 를 X-Admin-Password 헤더로 확인
// shortcut: 비밀번호 시도 횟수 제한 없음, 공개 서비스로 키우면 로그인 제한·HTTPS 추가
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final JdbcTemplate jdbc;
    private final byte[] adminPassword;

    public AdminController(JdbcTemplate jdbc, @Value("${ADMIN_PASSWORD:}") String adminPassword) {
        this.jdbc = jdbc;
        this.adminPassword = adminPassword.getBytes(StandardCharsets.UTF_8);
    }

    private void checkAdmin(String password) {
        if (adminPassword.length == 0) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "서버에 관리자 비밀번호(ADMIN_PASSWORD)가 설정되지 않았어요.");
        }
        byte[] given = password == null ? new byte[0] : password.getBytes(StandardCharsets.UTF_8);
        if (!MessageDigest.isEqual(adminPassword, given)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "관리자 비밀번호가 틀렸어요.");
        }
    }

    @GetMapping("/members")
    public List<Map<String, Object>> members(@RequestHeader(value = "X-Admin-Password", required = false) String password) {
        checkAdmin(password);
        return jdbc.queryForList("""
                SELECT u.user_id AS userId, u.login_id AS loginId, u.nickname, u.email, u.created_at AS createdAt,
                       (SELECT COUNT(*) FROM expense e WHERE e.user_id = u.user_id) AS expenseCount,
                       (SELECT COUNT(*) FROM schedule_item s WHERE s.user_id = u.user_id) AS scheduleCount
                FROM user u ORDER BY u.user_id DESC""");
    }

    // 회원과 그 회원의 모든 데이터를 함께 삭제 (FK 순서: 자식 테이블 먼저)
    @Transactional
    @DeleteMapping("/members/{userId}")
    public Map<String, Object> delete(@PathVariable long userId,
                                      @RequestHeader(value = "X-Admin-Password", required = false) String password) {
        checkAdmin(password);
        jdbc.update("DELETE rs FROM route_summary rs JOIN user_request ur ON rs.request_id = ur.request_id WHERE ur.user_id = ?", userId);
        for (String table : List.of("user_request", "route_history", "expense", "trip", "saved_festival", "schedule_item")) {
            jdbc.update("DELETE FROM " + table + " WHERE user_id = ?", userId);
        }
        if (jdbc.update("DELETE FROM user WHERE user_id = ?", userId) == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "이미 없는 회원이에요.");
        }
        return Map.of("deleted", userId);
    }
}
