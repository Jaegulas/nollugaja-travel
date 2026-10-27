package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
// 설명: CulturalIntroSyncController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


@WebServlet("/CulturalIntroSync")
public class CulturalIntroSyncController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String API_BASE_URL =
            "https://apis.data.go.kr/B551011/KorService2";

    // 한 번 실행할 때 처리할 개수
    private static final int SYNC_LIMIT = 50;


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

        System.out.println("★★★ CulturalIntroSyncController 전체 동기화 시작 ★★★");

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");

        CulturalHeritageDao dao = new CulturalHeritageDao();

        int totalSuccess = 0;
        int totalFail = 0;
        int batchNumber = 1;

        while (true) {

            // 상세정보가 없는 데이터 50개씩 가져오기
            List<CulturalHeritageVo> list =
                    dao.findMissingIntroInfo(SYNC_LIMIT);

            // 더 이상 처리할 데이터가 없으면 종료
            if (list.isEmpty()) {

                System.out.println();
                System.out.println(
                        "[IntroSync] 더 이상 처리할 데이터가 없습니다."
                );

                break;
            }

            System.out.println();
            System.out.println("==========================================");
            System.out.println(
                    "[IntroSync] " + batchNumber + "번째 묶음 시작"
            );
            System.out.println(
                    "[IntroSync] 처리 대상 : " + list.size() + "건"
            );
            System.out.println("==========================================");

            int batchSuccess = 0;
            int batchFail = 0;

            for (CulturalHeritageVo vo : list) {

                try {

                    IntroInfo intro =
                            getIntroInfo(vo.getContentId());

                    int result =
                            dao.updateIntroInfo(
                                    vo.getContentId(),
                                    intro.useTime,
                                    intro.restDate,
                                    intro.parking,
                                    intro.infoCenter
                            );

                    if (result > 0) {

                        batchSuccess++;
                        totalSuccess++;

                        System.out.println(
                                "[IntroSync] 성공 : "
                                + vo.getContentId()
                                + " / "
                                + vo.getTitle()
                        );

                    } else {

                        batchFail++;
                        totalFail++;

                        System.err.println(
                                "[IntroSync] DB 업데이트 실패 : "
                                + vo.getContentId()
                        );
                    }

                } catch (Exception e) {

                    batchFail++;
                    totalFail++;

                    System.err.println(
                            "[IntroSync] API 처리 실패 : "
                            + vo.getContentId()
                            + " / "
                            + vo.getTitle()
                    );

                    System.err.println(
                            "[IntroSync] API 오류가 발생하여 즉시 작업을 중단합니다."
                    );

                    e.printStackTrace();

                    // 429 등 API 오류 발생 시
                    // 나머지 문화유산에 추가 요청을 보내지 않고 즉시 중단
                    break;
                }

                // API 서버에 너무 빠르게 요청하지 않도록 잠깐 대기
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();

                    System.err.println(
                            "[IntroSync] 작업이 중단되었습니다."
                    );

                    break;
                }
            }

            System.out.println("------------------------------------------");
            System.out.println(
                    "[IntroSync] "
                    + batchNumber
                    + "번째 묶음 완료"
            );
            System.out.println(
                    "[IntroSync] 성공 : "
                    + batchSuccess
                    + " / 실패 : "
                    + batchFail
            );
            System.out.println("------------------------------------------");

            /*
             * 중요한 안전장치
             *
             * 실패한 데이터는 DB 상세정보가 NULL 상태라서
             * 다음 findMissingIntroInfo()에서 또 조회될 수 있다.
             *
             * 따라서 실패가 발생하면 무한 반복하지 않고
             * 이번 전체 동기화를 종료한다.
             *
             * 다시 /CulturalIntroSync를 실행하면
             * 실패한 데이터부터 다시 시도할 수 있다.
             */
            if (batchFail > 0) {

                System.err.println(
                        "[IntroSync] 실패 데이터가 있어서 작업을 중단합니다."
                );

                break;
            }

            batchNumber++;
        }

        System.out.println();
        System.out.println("==========================================");
        System.out.println("[IntroSync] 전체 동기화 작업 종료");
        System.out.println("[IntroSync] 총 성공 : " + totalSuccess);
        System.out.println("[IntroSync] 총 실패 : " + totalFail);
        System.out.println("==========================================");

        response.getWriter().println(
                "문화유산 상세정보 동기화 작업 종료"
        );

        response.getWriter().println(
                "성공 : " + totalSuccess + "건"
        );

        response.getWriter().println(
                "실패 : " + totalFail + "건"
        );

        if (totalFail > 0) {

            response.getWriter().println();
            response.getWriter().println(
                    "실패한 데이터가 있습니다."
            );

            response.getWriter().println(
                    "CulturalIntroSync를 다시 실행하면 이어서 처리합니다."
            );

        } else {

            response.getWriter().println();
            response.getWriter().println(
                    "상세정보 동기화가 완료되었습니다."
            );
        }
    }


    // =========================================================
    // 주제: getIntroInfo 처리
    // 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
    // =========================================================
    private IntroInfo getIntroInfo(String contentId)
            throws Exception {

        String serviceKey =
                getEncodedServiceKey();

        String apiUrl =
                API_BASE_URL
                + "/detailIntro2"
                + "?serviceKey=" + serviceKey
                + "&MobileOS=ETC"
                + "&MobileApp=Nolleogaja"
                + "&_type=json"
                + "&contentId="
                + URLEncoder.encode(
                        contentId,
                        StandardCharsets.UTF_8
                )
                + "&contentTypeId=12";


        HttpURLConnection connection =
                (HttpURLConnection)
                URI.create(apiUrl)
                   .toURL()
                   .openConnection();

        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);

        int responseCode =
                connection.getResponseCode();


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

        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }

        reader.close();
        connection.disconnect();


        if (responseCode < 200
                || responseCode >= 300) {

        	throw new IOException(
        	        "detailIntro2 HTTP 오류 : "
        	        + responseCode
        	        + " / 응답내용 : "
        	        + jsonBuilder.toString()
        	);
        }


        ObjectMapper mapper =
                new ObjectMapper();

        JsonNode root =
                mapper.readTree(
                        jsonBuilder.toString()
                );


        JsonNode item =
                root.path("response")
                    .path("body")
                    .path("items")
                    .path("item");


        if (item.isArray()
                && item.size() > 0) {

            item = item.get(0);
        }


        IntroInfo info =
                new IntroInfo();


        // 기존 CulturalHeritageController에서 사용하던
        // 문화유산 detailIntro2 필드
        info.useTime =
                valueOrDefault(
                        item.path("usetime").asText(),
                        "문의로 직접확인"
                );

        info.restDate =
                valueOrDefault(
                        item.path("restdate").asText(),
                        "연중무휴"
                );

        info.parking =
                valueOrDefault(
                        item.path("parking").asText(),
                        "불가능"
                );

        info.infoCenter =
                valueOrDefault(
                        item.path("infocenter").asText(),
                        "문의정보 없음"
                );

        return info;
    }


    // =========================================================
    // 주제: valueOrDefault 처리
    // 설명: valueOrDefault 기능에 필요한 처리 로직을 수행
    // =========================================================
    private String valueOrDefault(
            String value,
            String defaultValue) {

        if (value == null
                || value.trim().isEmpty()) {

            return defaultValue;
        }

        return value.trim();
    }
    
    // =========================================================
    // 주제: getEncodedServiceKey 처리
    // 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
    // =========================================================
    private String getEncodedServiceKey() {

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

        serviceKey =
                serviceKey
                .replace("\r", "")
                .replace("\n", "")
                .trim();

        return URLEncoder.encode(
                serviceKey,
                StandardCharsets.UTF_8
        );
    }

    // =========================================================
    // detailIntro2 결과 보관용
    // =========================================================
    private static class IntroInfo {

        private String useTime;

        private String restDate;

        private String parking;

        private String infoCenter;
    }
}
