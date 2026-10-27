package service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import config.AppConfigListener;

// =========================================================
// 주제: 파일 역할
// 설명: TravelDestinationService의 핵심 비즈니스 로직과 데이터 가공을 담당하는 서비스
// =========================================================



public class TravelDestinationService {

    // 서버 메모리 캐시: Tomcat 재시작 시 초기화됩니다.
    private static final long LIST_TTL_MS = 60L * 60 * 1000;   // 목록 1시간
    private static final long DETAIL_TTL_MS = 60L * 60 * 1000; // 상세 1시간
    private static final long SEARCH_TTL_MS = 10L * 60 * 1000; // 검색 10분
    private static final int MAX_CACHE_ENTRIES = 200;
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    private static final class CacheEntry {
        final String json;
        final long expiresAt;

        CacheEntry(String json, long expiresAt) {
            this.json = json;
            this.expiresAt = expiresAt;
        }
    }

    // 가장 오래 사용하지 않은 항목부터 제거하는 LRU 캐시
    private final Map<String, CacheEntry> cache =
            new LinkedHashMap<String, CacheEntry>(32, 0.75f, true) {
                private static final long serialVersionUID = 1L;

                @Override
                // =========================================================
                // 주제: removeEldestEntry 처리
                // 설명: 대상 데이터를 삭제하거나 제거
                // =========================================================
                protected boolean removeEldestEntry(Map.Entry<String, CacheEntry> eldest) {
                    return size() > MAX_CACHE_ENTRIES;
                }
            };

    private long ttlFor(String requestName) {
        if ("여행지 목록".equals(requestName)) return LIST_TTL_MS;
        if ("여행지 상세".equals(requestName)) return DETAIL_TTL_MS;
        return SEARCH_TTL_MS;
    }

    // TourAPI는 HTTP 200으로 오류 JSON을 반환할 수 있으므로 내용도 검사합니다.
    // =========================================================
    // 주제: isSuccessfulTourApiResponse 처리
    // 설명: 입력값 또는 현재 상태가 조건에 맞는지 검사
    // =========================================================
    private boolean isSuccessfulTourApiResponse(String json) {
        try {
            JsonNode root = JSON_MAPPER.readTree(json);
            JsonNode response = root.path("response");
            JsonNode header = response.path("header");
            String resultCode = header.path("resultCode").asText("");
            return ("0000".equals(resultCode) || "00".equals(resultCode))
                    && response.path("body").isObject();
        } catch (Exception e) {
            return false;
        }
    }



// =========================================================
    // API KEY
    // =========================================================
    // 지금 사용 중인 API KEY를 아래에 넣으면 됨.
    // GitHub PUSH 전에는 반드시 제거할 것!
    // =========================================================

    // =========================================================
    // 주제: getServiceKey 처리
    // 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
    // =========================================================
    private static String getServiceKey() {

        String serviceKey =
            AppConfigListener.getProperty(
                "tour.api.js.key"
            );

        if (serviceKey == null
                || serviceKey.trim().isEmpty()) {

            throw new IllegalStateException(
                "TourAPI Service Key가 설정되지 않았습니다."
            );
        }

        return serviceKey.trim();
    }

    // =========================================================
    // 주제: getDestinationsFromApi 처리
    // 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
    // =========================================================
    public String getDestinationsFromApi(String areaCode) {

        System.out.println(
            "===== TourAPI 여행지 목록 조회 ====="
        );


        String baseUrl =
            "https://apis.data.go.kr/"
            + "B551011/KorService2/"
            + "areaBasedList2";


        StringBuilder urlBuilder =
            new StringBuilder(baseUrl);


        try {

            String serviceKey =
                getServiceKey();


            // =========================================
            // API KEY
            // =========================================

            urlBuilder.append("?serviceKey=")
                      .append(serviceKey);


            // =========================================
            // 기본 파라미터
            // =========================================

            urlBuilder.append(
                "&MobileOS=ETC"
            );


            urlBuilder.append(
                "&MobileApp="
            ).append(
                URLEncoder.encode(
                    "TravelProject",
                    "UTF-8"
                )
            );


            urlBuilder.append(
                "&_type=json"
            );


            urlBuilder.append(
                "&pageNo=1"
            );


            // 테스트를 위해 100개 조회
            urlBuilder.append(
                "&numOfRows=100"
            );


            // =========================================
            // 현재 지역 필터는
            // JavaScript에서 addr1 기준으로 처리
            // =========================================

            System.out.println(
                "Controller에서 받은 areaCode : "
                + areaCode
            );


            return requestTourApi(
                urlBuilder.toString(),
                serviceKey,
                "여행지 목록"
            );


        } catch (Exception e) {

            System.out.println(
                "===== TourAPI 목록 URL 생성 오류 ====="
            );

            e.printStackTrace();


            return createErrorJson(
                "TourAPI 목록 조회 중 오류 발생"
            );
        }
    }



    // =========================================================
    // 3. TourAPI 여행지 상세정보 조회
    //
    // Controller:
    // service.getDestinationDetail(contentId)
    // =========================================================
    // =========================================================
    // 주제: getDestinationDetail 처리
    // 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
    // =========================================================
    public String getDestinationDetail(String contentId) {

        System.out.println(
            "===== TourAPI 상세정보 조회 ====="
        );


        if (contentId == null
                || contentId.trim().isEmpty()) {

            return createErrorJson(
                "contentId가 없습니다."
            );
        }


        String baseUrl =
            "https://apis.data.go.kr/"
            + "B551011/KorService2/"
            + "detailCommon2";


        StringBuilder urlBuilder =
            new StringBuilder(baseUrl);


        try {

            String serviceKey =
                getServiceKey();


            // =========================================
            // API KEY
            // =========================================

            urlBuilder.append("?serviceKey=")
                      .append(serviceKey);


            // =========================================
            // 기본 파라미터
            // =========================================

            urlBuilder.append(
                "&MobileOS=ETC"
            );


            urlBuilder.append(
                "&MobileApp="
            ).append(
                URLEncoder.encode(
                    "TravelProject",
                    "UTF-8"
                )
            );


            urlBuilder.append(
                "&_type=json"
            );


            // =========================================
            // 어떤 여행지인지 구분하는 고유번호
            // =========================================

            urlBuilder.append(
                "&contentId="
            ).append(
                URLEncoder.encode(
                    contentId.trim(),
                    "UTF-8"
                )
            );


            System.out.println(
                "상세조회 contentId : "
                + contentId
            );


            return requestTourApi(
                urlBuilder.toString(),
                serviceKey,
                "여행지 상세"
            );


        } catch (Exception e) {

            System.out.println(
                "===== TourAPI 상세 URL 생성 오류 ====="
            );

            e.printStackTrace();


            return createErrorJson(
                "TourAPI 상세정보 조회 중 오류 발생"
            );
        }
    }

 // =========================================================
 // 4. TourAPI 키워드 여행지 검색
 //
 // Controller:
 // service.searchDestinations(keyword)
 // =========================================================
 // =========================================================
 // 주제: searchDestinations 처리
 // 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
 // =========================================================
 public String searchDestinations(String keyword) {

     System.out.println(
         "===== TourAPI 키워드 검색 ====="
     );


     // 검색어 검사
     if (keyword == null
             || keyword.trim().isEmpty()) {

         return createErrorJson(
             "검색어가 없습니다."
         );
     }


     // TourAPI 키워드 검색 주소
     String baseUrl =
         "https://apis.data.go.kr/"
         + "B551011/KorService2/"
         + "searchKeyword2";


     StringBuilder urlBuilder =
         new StringBuilder(baseUrl);


     try {

         String serviceKey =
             getServiceKey();


         // =========================================
         // API KEY
         // =========================================

         urlBuilder.append("?serviceKey=")
                   .append(serviceKey);


         // =========================================
         // 기본 파라미터
         // =========================================

         urlBuilder.append(
             "&MobileOS=ETC"
         );


         urlBuilder.append(
             "&MobileApp="
         ).append(
             URLEncoder.encode(
                 "TravelProject",
                 "UTF-8"
             )
         );


         urlBuilder.append(
             "&_type=json"
         );


         urlBuilder.append(
             "&pageNo=1"
         );


         urlBuilder.append(
             "&numOfRows=1000"
         );


         // =========================================
         // 사용자가 입력한 검색어
         // 한글이 있기 때문에 반드시 URL 인코딩
         // =========================================

         urlBuilder.append(
             "&keyword="
         ).append(
             URLEncoder.encode(
                 keyword.trim(),
                 "UTF-8"
             )
         );


         System.out.println(
             "검색어 : " + keyword
         );


         // 기존 공통 TourAPI 호출 메소드 재사용
         return requestTourApi(
             urlBuilder.toString(),
             serviceKey,
             "여행지 키워드 검색"
         );


     } catch (Exception e) {

         System.out.println(
             "===== TourAPI 검색 URL 생성 오류 ====="
         );

         e.printStackTrace();


         return createErrorJson(
             "TourAPI 키워드 검색 중 오류 발생"
         );
     }
 }


    // =========================================================
    // 4. 실제 TourAPI HTTP 요청
    //
    // 목록 / 상세에서 공통으로 사용
    // =========================================================
    // =========================================================
    // 주제: requestTourApi 처리
    // 설명: requestTourApi 기능에 필요한 처리 로직을 수행
    // =========================================================
    private synchronized String requestTourApi(
            String requestUrl,
            String serviceKey,
            String requestName) {


        // 동일한 URL은 캐시 유효기간 동안 외부 TourAPI를 다시 호출하지 않습니다.
        // synchronized로 같은 Service 인스턴스의 동시 중복 호출도 막습니다.
        long now = System.currentTimeMillis();
        CacheEntry saved = cache.get(requestUrl);
        if (saved != null && now < saved.expiresAt) {
            System.out.println("[TourAPI CACHE HIT] " + requestName);
            return saved.json;
        }
        if (saved != null) cache.remove(requestUrl);
        System.out.println("[TourAPI CACHE MISS] " + requestName);

        HttpURLConnection conn = null;
        BufferedReader br = null;

        StringBuilder result =
            new StringBuilder();


        try {

            // =========================================
            // 콘솔에는 API KEY 숨기기
            // =========================================

            String safeUrl =
                requestUrl.replace(
                    serviceKey,
                    "API_KEY"
                );


            System.out.println(
                "===== "
                + requestName
                + " TourAPI 요청 ====="
            );


            System.out.println(
                safeUrl
            );


            // =========================================
            // URL 생성
            // =========================================

            URL url =
                new URL(requestUrl);


            // =========================================
            // HTTP 연결
            // =========================================

            conn =
                (HttpURLConnection)
                url.openConnection();


            conn.setRequestMethod(
                "GET"
            );


            conn.setRequestProperty(
                "Accept",
                "application/json"
            );


            conn.setConnectTimeout(
                5000
            );


            conn.setReadTimeout(
                5000
            );


            // =========================================
            // HTTP 응답 코드
            // =========================================

            int responseCode =
                conn.getResponseCode();


            System.out.println(
                requestName
                + " HTTP 응답 코드 : "
                + responseCode
            );


            // =========================================
            // 응답 Stream
            // =========================================

            InputStream stream;


            if (responseCode >= 200
                    && responseCode < 300) {

                stream =
                    conn.getInputStream();

            } else {

                stream =
                    conn.getErrorStream();
            }


            // 응답 자체가 없는 경우
            if (stream == null) {

                System.out.println(
                    "===== TourAPI 응답 없음 ====="
                );


                return "{"
                    + "\"success\":false,"
                    + "\"status\":"
                    + responseCode
                    + ","
                    + "\"message\":"
                    + "\"TourAPI 응답이 없습니다.\""
                    + "}";
            }


            // =========================================
            // 응답 읽기
            // =========================================

            br =
                new BufferedReader(
                    new InputStreamReader(
                        stream,
                        "UTF-8"
                    )
                );


            String line;


            while (
                (line = br.readLine()) != null
            ) {

                result.append(line);
            }


            // =========================================
            // 성공
            // =========================================

            if (responseCode >= 200
                    && responseCode < 300) {


                System.out.println(
                    "===== "
                    + requestName
                    + " TourAPI 응답 성공 ====="
                );


                System.out.println(
                    "응답 데이터 길이 : "
                    + result.length()
                );

                String responseJson = result.toString();
                if (isSuccessfulTourApiResponse(responseJson)) {
                    cache.put(requestUrl, new CacheEntry(
                            responseJson, System.currentTimeMillis() + ttlFor(requestName)));
                    System.out.println("[TourAPI CACHE SAVE] " + requestName);
                } else {
                    System.out.println("[TourAPI CACHE SKIP] 정상 TourAPI 응답이 아니므로 저장하지 않음");
                }


            }

            // =========================================
            // 실패
            // =========================================
            else {


                System.out.println(
                    "===== "
                    + requestName
                    + " TourAPI 응답 실패 ====="
                );


                System.out.println(
                    "HTTP 상태 코드 : "
                    + responseCode
                );


                System.out.println(
                    "TourAPI 오류 내용 : "
                    + result.toString()
                );

            }


        } catch (Exception e) {


            System.out.println(
                "===== "
                + requestName
                + " TourAPI Java 오류 ====="
            );


            System.out.println(
                "오류 종류 : "
                + e.getClass().getName()
            );


            System.out.println(
                "오류 내용 : "
                + e.getMessage()
            );


            e.printStackTrace();


            return createErrorJson(
                requestName
                + " TourAPI 호출 중 Java 오류 발생"
            );


        } finally {


            // =========================================
            // BufferedReader 종료
            // =========================================

            try {

                if (br != null) {

                    br.close();
                }

            } catch (Exception e) {

                e.printStackTrace();
            }


            // =========================================
            // HTTP 연결 종료
            // =========================================

            if (conn != null) {

                conn.disconnect();
            }
        }


        return result.toString();
    }



    // =========================================================
    // 주제: createErrorJson 처리
    // 설명: 입력받은 데이터를 생성하거나 저장
    // =========================================================
    private String createErrorJson(String message) {

        return "{"
            + "\"success\":false,"
            + "\"message\":\""
            + message
            + "\""
            + "}";
    }

}
