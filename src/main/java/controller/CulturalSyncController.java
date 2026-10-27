package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import config.AppConfigListener;
import dao.CulturalHeritageDao;
import vo.CulturalHeritageVo;

import javax.servlet.http.HttpSession;

// =========================================================
// 주제: 파일 역할
// 설명: CulturalSyncController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


@WebServlet("/CulturalSync")
public class CulturalSyncController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String API_BASE_URL =
            "https://apis.data.go.kr/B551011/KorService2";

    private static final String CONTENT_TYPE_ID = "12";
    private static final String LCLS_SYSTM_1 = "HS";

    // 한 번에 최대 1000건씩 조회
    private static final int BULK_ROWS = 1000;


    @Override
    // =========================================================
    // 주제: doGet 처리
    // 설명: GET 요청을 받아 필요한 데이터를 준비하고 화면 또는 응답으로 전달
    // =========================================================
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

    	// 관리자 권한 확인
    	HttpSession session = request.getSession(false);

    	if (session == null
    	        || !"ADMIN".equals(session.getAttribute("role"))) {

    	    response.sendError(403, "관리자만 실행할 수 있습니다.");
    	    return;
    	}
    	
        request.setCharacterEncoding("UTF-8");
        response.setContentType(
                "text/plain; charset=UTF-8"
        );

        System.out.println();
        System.out.println("==========================================");
        System.out.println("[CulturalSync] 전체 문화유산 동기화 시작");
        System.out.println("==========================================");

        try {

            // ==========================================
            // 1. 첫 번째 페이지 조회
            // ==========================================

            TourApiResult firstResult =
                    callAreaApi(1, BULK_ROWS);

            int totalCount = firstResult.totalCount;

            int totalPages = (int) Math.ceil(
                    totalCount / (double) BULK_ROWS
            );

            System.out.println(
                    "[CulturalSync] 전체 데이터 수 : "
                    + totalCount
            );

            System.out.println(
                    "[CulturalSync] API 전체 페이지 수 : "
                    + totalPages
            );


            // ==========================================
            // 2. DAO 준비
            // ==========================================

            CulturalHeritageDao heritageDao =
                    new CulturalHeritageDao();

            int totalSaved = 0;


            // ==========================================
            // 3. 첫 페이지 저장
            // ==========================================

            totalSaved +=
                    heritageDao.saveAll(firstResult.items);

            System.out.println(
                    "[CulturalSync] 1/"
                    + totalPages
                    + " 페이지 저장 완료"
            );


            // ==========================================
            // 4. 나머지 페이지 조회 + 저장
            // ==========================================

            for (int page = 2;
                 page <= totalPages;
                 page++) {

                System.out.println();
                System.out.println(
                        "[CulturalSync] "
                        + page
                        + "/"
                        + totalPages
                        + " 페이지 요청"
                );

                TourApiResult result =
                        callAreaApi(page, BULK_ROWS);

                int saved =
                        heritageDao.saveAll(result.items);

                totalSaved += saved;

                System.out.println(
                        "[CulturalSync] "
                        + page
                        + "/"
                        + totalPages
                        + " 페이지 저장 완료 : "
                        + saved
                        + "건"
                );
            }


            // ==========================================
            // 5. 완료
            // ==========================================

            System.out.println();
            System.out.println("==========================================");
            System.out.println("[CulturalSync] 전체 동기화 완료");
            System.out.println(
                    "[CulturalSync] API 전체 : "
                    + totalCount
                    + "건"
            );
            System.out.println(
                    "[CulturalSync] 저장/수정 : "
                    + totalSaved
                    + "건"
            );
            System.out.println("==========================================");

            response.getWriter().println(
                    "문화유산 기본정보 동기화 완료"
            );

            response.getWriter().println(
                    "TourAPI 전체 데이터 : "
                    + totalCount
                    + "건"
            );

            response.getWriter().println(
                    "DB 저장/수정 : "
                    + totalSaved
                    + "건"
            );


        } catch (Exception e) {

            // 상세 오류는 서버 콘솔에만 출력
            System.err.println("[CulturalSync] 전체 동기화 실패");
            e.printStackTrace();

            // 사용자에게는 상세 오류를 노출하지 않음
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            response.getWriter().println("동기화 실패");
        }
    }


    // =========================================================
    // 주제: callAreaApi 처리
    // 설명: callAreaApi 기능에 필요한 처리 로직을 수행
    // =========================================================
    private TourApiResult callAreaApi(
            int pageNo,
            int numOfRows)
            throws Exception {

        String serviceKey = encodeServiceKey();

        String apiUrl =
                API_BASE_URL
                + "/areaBasedList2"
                + "?serviceKey=" + serviceKey
                + "&numOfRows=" + numOfRows
                + "&pageNo=" + pageNo
                + "&MobileOS=ETC"
                + "&MobileApp=Nolleogaja"
                + "&_type=json"
                + "&arrange=C"
                + "&contentTypeId=" + CONTENT_TYPE_ID
                + "&lclsSystm1=" + LCLS_SYSTM_1;

        System.out.println(
                "[CulturalSync] TourAPI 요청 page : "
                + pageNo
        );

        return callTourApi(apiUrl);
    }


    // =========================================================
    // 주제: callTourApi 처리
    // 설명: callTourApi 기능에 필요한 처리 로직을 수행
    // =========================================================
    private TourApiResult callTourApi(
            String apiUrl)
            throws Exception {

        URI uri = URI.create(apiUrl);

        HttpURLConnection connection =
                (HttpURLConnection)
                uri.toURL().openConnection();

        connection.setRequestMethod("GET");

        connection.setConnectTimeout(10000);
        connection.setReadTimeout(20000);

        int responseCode =
                connection.getResponseCode();

        System.out.println(
                "[CulturalSync] HTTP 응답 : "
                + responseCode
        );

        BufferedReader reader;

        if (responseCode >= 200
                && responseCode < 300) {

            reader = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream(),
                            StandardCharsets.UTF_8
                    )
            );

        } else {

            reader = new BufferedReader(
                    new InputStreamReader(
                            connection.getErrorStream(),
                            StandardCharsets.UTF_8
                    )
            );
        }


        StringBuilder jsonBuilder =
                new StringBuilder();

        String line;

        while ((line = reader.readLine())
                != null) {

            jsonBuilder.append(line);
        }

        reader.close();
        connection.disconnect();

        String json =
                jsonBuilder.toString();


        if (responseCode < 200
                || responseCode >= 300) {

            throw new IOException(
                    "TourAPI HTTP 오류 : "
                    + responseCode
                    + " / "
                    + json
            );
        }

        return parseTourApiJson(json);
    }


    // =========================================================
    // 주제: parseTourApiJson 처리
    // 설명: 입력 데이터를 화면/API/DB에서 사용하기 좋은 형태로 변환 또는 가공
    // =========================================================
    private TourApiResult parseTourApiJson(
            String json)
            throws Exception {

        ObjectMapper mapper =
                new ObjectMapper();

        JsonNode root =
                mapper.readTree(json);

        JsonNode header =
                root.path("response")
                    .path("header");

        String resultCode =
                header.path("resultCode")
                      .asText();

        String resultMsg =
                header.path("resultMsg")
                      .asText();

        if (!"0000".equals(resultCode)) {

            throw new IOException(
                    "TourAPI 오류 : "
                    + resultCode
                    + " / "
                    + resultMsg
            );
        }


        JsonNode body =
                root.path("response")
                    .path("body");

        TourApiResult result =
                new TourApiResult();

        result.totalCount =
                body.path("totalCount")
                    .asInt(0);

        result.items =
                new ArrayList<>();


        JsonNode itemNode =
                body.path("items")
                    .path("item");


        if (itemNode.isArray()) {

            for (JsonNode item : itemNode) {

                result.items.add(
                        createHeritageVo(item)
                );
            }

        } else if (itemNode.isObject()) {

            result.items.add(
                    createHeritageVo(itemNode)
            );
        }


        System.out.println(
                "[CulturalSync] 받은 데이터 : "
                + result.items.size()
                + "건"
        );

        return result;
    }


    // =========================================================
    // 주제: createHeritageVo 처리
    // 설명: 입력받은 데이터를 생성하거나 저장
    // =========================================================
    private CulturalHeritageVo createHeritageVo(
            JsonNode item) {

        CulturalHeritageVo vo =
                new CulturalHeritageVo();

        vo.setContentId(
                item.path("contentid").asText()
        );

        vo.setTitle(
                item.path("title").asText()
        );

        vo.setAddr1(
                item.path("addr1").asText()
        );

        vo.setFirstImage(
                item.path("firstimage").asText()
        );

        vo.setMapX(
                item.path("mapx").asText()
        );

        vo.setMapY(
                item.path("mapy").asText()
        );

        vo.setAreaCode(
                item.path("areacode").asText()
        );

        vo.setSigunguCode(
                item.path("sigungucode").asText()
        );

        vo.setContentTypeId(
                item.path("contenttypeid").asText()
        );

        return vo;
    }


    // =========================================================
    // 주제: encodeServiceKey 처리
    // 설명: encodeServiceKey 기능에 필요한 처리 로직을 수행
    // =========================================================
    private String encodeServiceKey() {

        String serviceKey =
                AppConfigListener.getProperty(
                    "CulturalHeritageController.api.key"
                );

        if (serviceKey == null
                || serviceKey.isBlank()) {

            throw new IllegalStateException(
                "문화유산 API Key가 없습니다."
            );
        }

        String cleanServiceKey =
                serviceKey
                .replace("\r", "")
                .replace("\n", "")
                .trim();

        return URLEncoder.encode(
                cleanServiceKey,
                StandardCharsets.UTF_8
        );
    }


    // =====================================================
    // TourAPI 결과 객체
    // =====================================================

    private static class TourApiResult {

        private List<CulturalHeritageVo> items;

        private int totalCount;
    }
}
