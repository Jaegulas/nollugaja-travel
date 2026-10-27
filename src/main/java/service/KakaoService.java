
package service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

// =========================================================
// 주제: 파일 역할
// 설명: KakaoService의 핵심 비즈니스 로직과 데이터 가공을 담당하는 서비스
// =========================================================


public class KakaoService {

    // ========================================
    // 1. 카카오 API 주소
    // ========================================
    private static final String TOKEN_URL =
            "https://kauth.kakao.com/oauth/token";

    private static final String USER_URL =
            "https://kapi.kakao.com/v2/user/me";

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper mapper = new ObjectMapper();

    // =========================================================
    // 주제: getAccessToken 처리
    // 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
    // =========================================================
    public String getAccessToken(
            String code,
            String restApiKey,
            String redirectUri,
            String clientSecret) throws Exception {

        StringBuilder form = new StringBuilder();

        form.append("grant_type=authorization_code");
        form.append("&client_id=")
            .append(encode(restApiKey));
        form.append("&redirect_uri=")
            .append(encode(redirectUri));
        form.append("&code=")
            .append(encode(code));

        // Client Secret을 사용하는 앱인 경우
        if (clientSecret != null
                && !clientSecret.trim().isEmpty()) {

            form.append("&client_secret=")
                .append(encode(clientSecret));
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TOKEN_URL))
                .timeout(Duration.ofSeconds(15))
                .header(
                    "Content-Type",
                    "application/x-www-form-urlencoded"
                )
                .POST(HttpRequest.BodyPublishers.ofString(
                    form.toString()
                ))
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString(
                    StandardCharsets.UTF_8
                )
        );

        if (response.statusCode() != 200) {
            throw new IOException(
                "카카오 토큰 발급 실패. HTTP 상태: "
                + response.statusCode()
            );
        }

        JsonNode json = mapper.readTree(response.body());

        String accessToken =
                json.path("access_token").asText("");

        if (accessToken.isEmpty()) {
            throw new IOException(
                "카카오 응답에 액세스 토큰이 없습니다."
            );
        }

        System.out.println(
            "[KakaoService] 액세스 토큰 발급 성공"
        );

        return accessToken;
    }

    // =========================================================
    // 주제: getUserInfo 처리
    // 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
    // =========================================================
    public KakaoUser getUserInfo(String accessToken)
            throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(USER_URL))
                .timeout(Duration.ofSeconds(15))
                .header(
                    "Authorization",
                    "Bearer " + accessToken
                )
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString(
                    StandardCharsets.UTF_8
                )
        );

        if (response.statusCode() != 200) {
            throw new IOException(
                "카카오 사용자 조회 실패. HTTP 상태: "
                + response.statusCode()
            );
        }

        JsonNode json = mapper.readTree(response.body());

        long kakaoId = json.path("id").asLong(0);

        if (kakaoId <= 0) {
            throw new IOException(
                "카카오 사용자 ID를 확인할 수 없습니다."
            );
        }

        String nickname = json
                .path("properties")
                .path("nickname")
                .asText("");

        if (nickname.isEmpty()) {
            nickname = json
                    .path("kakao_account")
                    .path("profile")
                    .path("nickname")
                    .asText("");
        }

        if (nickname.isEmpty()) {
            nickname = "카카오회원";
        }

        System.out.println(
            "[KakaoService] 사용자 정보 조회 성공"
        );

        return new KakaoUser(kakaoId, nickname);
    }

    // =========================================================
    // 주제: encode 처리
    // 설명: encode 기능에 필요한 처리 로직을 수행
    // =========================================================
    private String encode(String value) {

        return URLEncoder.encode(
                value,
                StandardCharsets.UTF_8
        );
    }

    // ========================================
    // 5. 카카오 사용자 정보 객체
    // ========================================
    public static class KakaoUser {

        private final long kakaoId;
        private final String nickname;

        // =========================================================
        // 주제: KakaoUser 처리
        // 설명: KakaoUser 기능에 필요한 처리 로직을 수행
        // =========================================================
        public KakaoUser(long kakaoId, String nickname) {
            this.kakaoId = kakaoId;
            this.nickname = nickname;
        }

        public long getKakaoId() {
            return kakaoId;
        }

        // =========================================================
        // 주제: getNickname 처리
        // 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
        // =========================================================
        public String getNickname() {
            return nickname;
        }
    }
}
