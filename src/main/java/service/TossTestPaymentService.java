package service;

import config.AppConfigListener;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;

// =========================================================
// 주제: 파일 역할
// 설명: TossTestPaymentService의 핵심 비즈니스 로직과 데이터 가공을 담당하는 서비스
// =========================================================


public class TossTestPaymentService {
    private static final String URL = "https://api.tosspayments.com/v1/payments/confirm";
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8)).build();

    // =========================================================
    // 주제: confirm 처리
    // 설명: confirm 기능에 필요한 처리 로직을 수행
    // =========================================================
    public JsonNode confirm(String paymentKey, String orderId, long amount)
            throws IOException, InterruptedException {
        String secret = AppConfigListener.getProperty("toss.secret.key");
        if (secret == null || !secret.startsWith("test_sk")) {
            throw new IllegalStateException("config.properties의 toss.secret.key에 테스트 시크릿 키를 설정하세요.");
        }
        String auth = Base64.getEncoder().encodeToString((secret + ":").getBytes(StandardCharsets.UTF_8));
        String json = mapper.writeValueAsString(Map.of("paymentKey",paymentKey,"orderId",orderId,"amount",amount));
        HttpRequest request = HttpRequest.newBuilder(URI.create(URL))
                .timeout(Duration.ofSeconds(20))
                .header("Authorization", "Basic " + auth)
                .header("Content-Type", "application/json")
                .header("Idempotency-Key", "travel-test-" + orderId)
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        JsonNode result = mapper.readTree(response.body());
        if (response.statusCode() != 200) {
            throw new IOException("PG 승인 실패 HTTP " + response.statusCode() + ", code=" + result.path("code").asText());
        }
        if (!"DONE".equals(result.path("status").asText()) ||
                !orderId.equals(result.path("orderId").asText()) ||
                amount != result.path("totalAmount").asLong(-1) ||
                !paymentKey.equals(result.path("paymentKey").asText())) {
            throw new IOException("PG 승인 응답 검증 실패");
        }
        return result;
    }
}
