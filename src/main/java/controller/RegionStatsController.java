
package controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import config.AppConfigListener;
import util.DBCPUtil;

// =========================================================
// 주제: 파일 역할
// 설명: RegionStatsController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


@WebServlet("/RegionStats")
public class RegionStatsController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ObjectMapper mapper = new ObjectMapper();

    // API 호출 횟수를 줄이기 위한 30분 캐시
    private static final long CACHE_TIME = 30 * 60 * 1000L;
    // API 호출 횟수를 줄이기 위한 7일 캐시 권성애 2026.10.24토 아래 걸로해도 무방 30일하려면 7을 30으로 변경
    //private static final long CACHE_TIME = 7 * 24 * 60 * 60 * 1000L;

    private static final Map<String, CachedStats> CACHE =
            new ConcurrentHashMap<>();

    private static class CachedStats {
        final Map<String, Object> data;
        final long savedAt;

        CachedStats(Map<String, Object> data) {
            this.data = data;
            this.savedAt = System.currentTimeMillis();
        }
    }

    @Override
    // =========================================================
    // 주제: doGet 처리
    // 설명: GET 요청을 받아 필요한 데이터를 준비하고 화면 또는 응답으로 전달
    // =========================================================
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json; charset=UTF-8");

        String region = request.getParameter("region");

        String[] prefixes = getAddressPrefixes(region);
        int[] areaCodes = getAreaCodes(region);

        if (prefixes == null || areaCodes == null) {
            response.sendError(400, "잘못된 지역입니다.");
            return;
        }

        // 최근 조회 결과가 있으면 재사용
        CachedStats cached = CACHE.get(region);

        if (cached != null
                && System.currentTimeMillis() - cached.savedAt < CACHE_TIME) {

            mapper.writeValue(response.getWriter(), cached.data);
            return;
        }

        try {

            // 1. TourAPI 등록 명소 전체 건수
            int places = countTourApi(areaCodes, 12);

            // 2. TourAPI 음식점 전체 건수
            int foods = countTourApi(areaCodes, 39);

            // 3. 숙소 DB 전체 건수
            int stays;

            try (Connection conn = DBCPUtil.getConnection()) {
                stays = countStays(conn, prefixes);
            }

            Map<String, Object> result = new LinkedHashMap<>();

            result.put("region", region);
            result.put("places", places);
            result.put("foods", foods);
            result.put("stays", stays);

            CACHE.put(region, new CachedStats(result));

            mapper.writeValue(response.getWriter(), result);

        } catch (Exception e) {

            System.err.println("[RegionStats] 조회 실패: " + region);
            e.printStackTrace();

            response.sendError(502, "지역 통계 조회 실패");
        }
    }

    // =========================================================
    // 주제: getAreaCodes 처리
    // 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
    // =========================================================
    private int[] getAreaCodes(String region) {

        if (region == null) return null;

        switch (region) {

            case "gw":
                return new int[]{32};

            case "cap":
                return new int[]{1, 2, 31};

            case "cc":
                return new int[]{3, 8, 33, 34};

            case "hn":
                return new int[]{5, 37, 38};

            case "yn":
                return new int[]{4, 6, 7, 35, 36};

            case "jj":
                return new int[]{39};

            default:
                return null;
        }
    }

    // =========================================================
    // 주제: countTourApi 처리
    // 설명: countTourApi 기능에 필요한 처리 로직을 수행
    // =========================================================
    private int countTourApi(int[] areaCodes, int contentTypeId)
            throws Exception {

        int total = 0;

        for (int areaCode : areaCodes) {
            total += getTourApiTotalCount(areaCode, contentTypeId);
        }

        return total;
    }

    // =========================================================
    // 주제: getTourApiTotalCount 처리
    // 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
    // =========================================================
    private int getTourApiTotalCount(
            int areaCode, int contentTypeId) throws Exception {

        String serviceKey =
                AppConfigListener.getProperty("tour.api.js.key");

        if (serviceKey == null || serviceKey.trim().isEmpty()) {
            throw new IllegalStateException("TourAPI 키가 없습니다.");
        }

        // 기존 프로젝트의 TourAPI 키 형식 사용
        String apiUrl =
                "https://apis.data.go.kr/B551011/KorService2/areaBasedList2"
                + "?serviceKey=" + serviceKey.trim()
                + "&MobileOS=ETC"
                + "&MobileApp=" + URLEncoder.encode("TravelProject", "UTF-8")
                + "&_type=json"
                + "&pageNo=1"
                + "&numOfRows=1"
                + "&areaCode=" + areaCode
                + "&contentTypeId=" + contentTypeId;

        HttpURLConnection conn = null;

        try {

            conn = (HttpURLConnection)
                    new URL(apiUrl).openConnection();

            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestProperty("Accept", "application/json");

            int status = conn.getResponseCode();

            if (status != 200) {
                throw new IOException(
                    "TourAPI HTTP 오류: " + status
                );
            }

            try (InputStream in = conn.getInputStream()) {

                JsonNode root = mapper.readTree(in);

                JsonNode header = root.path("response").path("header");
                String resultCode = header.path("resultCode").asText();

                if (!"0000".equals(resultCode)) {
                    throw new IOException(
                        "TourAPI 응답 오류: "
                        + header.path("resultMsg").asText()
                    );
                }

                JsonNode totalNode =
                        root.path("response")
                            .path("body")
                            .path("totalCount");

                if (totalNode.isMissingNode()
                        || totalNode.isNull()) {
                    throw new IOException("totalCount가 없습니다.");
                }

                int count = totalNode.asInt(-1);

                if (count < 0) {
                    throw new IOException("totalCount 형식 오류");
                }

                System.out.println(
                    "[RegionStats] areaCode=" + areaCode
                    + ", type=" + contentTypeId
                    + ", count=" + count
                );

                return count;
            }

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    // =========================================================
    // 주제: getAddressPrefixes 처리
    // 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
    // =========================================================
    private String[] getAddressPrefixes(String region) {

        if (region == null) return null;

        switch (region) {

            case "gw":
                return new String[]{"강원"};

            case "cap":
                return new String[]{"서울", "경기", "인천"};

            case "cc":
                return new String[]{
                    "충북", "충청북도",
                    "충남", "충청남도",
                    "대전", "세종"
                };

            case "hn":
                return new String[]{
                    "전북", "전라북도", "전북특별자치도",
                    "전남", "전라남도", "광주"
                };

            case "yn":
                return new String[]{
                    "경북", "경상북도",
                    "경남", "경상남도",
                    "대구", "부산", "울산"
                };

            case "jj":
                return new String[]{"제주"};

            default:
                return null;
        }
    }

    // =========================================================
    // 주제: countStays 처리
    // 설명: countStays 기능에 필요한 처리 로직을 수행
    // =========================================================
    private int countStays(Connection conn, String[] prefixes)
            throws Exception {

        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) FROM accommodation WHERE ("
        );

        for (int i = 0; i < prefixes.length; i++) {
            if (i > 0) sql.append(" OR ");
            sql.append("addr LIKE ?");
        }

        sql.append(")");

        try (PreparedStatement pstmt =
                     conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < prefixes.length; i++) {
                pstmt.setString(i + 1, prefixes[i] + "%");
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }
}
