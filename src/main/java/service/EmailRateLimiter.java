package service;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

// =========================================================
// 주제: 파일 역할
// 설명: EmailRateLimiter의 핵심 비즈니스 로직과 데이터 가공을 담당하는 서비스
// =========================================================


public class EmailRateLimiter {

    private static final int MAX_SENDS = 5;
    private static final long BLOCK_MS = 5 * 60 * 1000L;

    private static final ConcurrentHashMap<String, LimitInfo> LIMITS =
            new ConcurrentHashMap<>();

    private static class LimitInfo {
        int count = 0;
        long blockedUntil = 0;
    }

    // =========================================================
    // 주제: EmailRateLimiter 처리
    // 설명: 필요한 데이터를 외부 또는 사용자에게 전송
    // =========================================================
    private EmailRateLimiter() {
    }

    public static synchronized boolean tryAcquire(String email) {

        String key = email.trim().toLowerCase(Locale.ROOT);
        long now = System.currentTimeMillis();

        LimitInfo info = LIMITS.computeIfAbsent(
                key, k -> new LimitInfo()
        );

        // 제한 시간이 끝났으면 초기화
        if (info.blockedUntil > 0 && now >= info.blockedUntil) {
            info.count = 0;
            info.blockedUntil = 0;
        }

        // 현재 차단 중
        if (info.blockedUntil > now) {
            return false;
        }

        // 5회 사용 후 6번째 요청부터 5분 차단
        if (info.count >= MAX_SENDS) {
            info.blockedUntil = now + BLOCK_MS;
            return false;
        }

        info.count++;
        return true;
    }
}
