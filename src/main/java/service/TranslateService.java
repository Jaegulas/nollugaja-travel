package service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import config.AppConfigListener;

// =========================================================
// 주제: 파일 역할
// 설명: TranslateService의 핵심 비즈니스 로직과 데이터 가공을 담당하는 서비스
// =========================================================


public class TranslateService {


    /*
     * =========================================================
     * Google Translation API Key 가져오기
     *
     * config.properties
     * google.api.key=XXXXX
     *
     * ↓
     *
     * AppConfigListener
     *
     * ↓
     *
     * TranslateService
     * =========================================================
     */
    private String getGoogleApiKey() {

        String apiKey =
                AppConfigListener.getProperty(
                        "google.api.key"
                );

        if (apiKey == null
                || apiKey.trim().isEmpty()) {

            throw new IllegalStateException(
                    "Google Translation API Key가 설정되지 않았습니다."
            );
        }

        return apiKey.trim();
    }



    /*
     * =========================================================
     * 여러 문장 번역
     * =========================================================
     */
    public List<String> translate(
            List<String> texts,
            String targetLanguage) {

        System.out.println(
                "=== TranslateService 도착 ==="
        );

        System.out.println(
                "번역할 문장 : " + texts
        );

        System.out.println(
                "목표 언어 : " + targetLanguage
        );


        List<String> translatedTexts =
                new ArrayList<String>();


        try {

            /*
             * config.properties에서
             * Google API Key 가져오기
             */
            String apiKey =
                    getGoogleApiKey();


            System.out.println(
                    "Google API KEY 존재 여부 : "
                    + (apiKey != null)
            );


            // 받은 문장을 하나씩 번역
            for (String text : texts) {

                String translatedText =
                        translateOneText(
                                text,
                                targetLanguage,
                                apiKey
                        );

                translatedTexts.add(
                        translatedText
                );
            }


        } catch (Exception e) {

            System.out.println(
                    "Google 번역 처리 중 오류 발생"
            );

            e.printStackTrace();
        }


        return translatedTexts;
    }



    /*
     * =========================================================
     * 실제 Google Translation API 요청
     * =========================================================
     */
    private String translateOneText(
            String text,
            String targetLanguage,
            String apiKey) throws Exception {


        /*
         * Google Translation API 주소
         *
         * API Key는 config.properties에서
         * 가져온 값을 사용
         */
        URL url = new URL(
                "https://translation.googleapis.com/"
                + "language/translate/v2"
                + "?key="
                + apiKey
        );


        // Google 서버와 연결
        HttpURLConnection conn =
                (HttpURLConnection)
                        url.openConnection();


        // POST 방식
        conn.setRequestMethod(
                "POST"
        );


        // JSON 데이터 전송
        conn.setRequestProperty(
                "Content-Type",
                "application/json; charset=UTF-8"
        );


        conn.setDoOutput(true);



        /*
         * =====================================================
         * Google에게 보낼 JSON
         * =====================================================
         */

        JsonObject requestJson =
                new JsonObject();


        requestJson.addProperty(
                "q",
                text
        );


        requestJson.addProperty(
                "source",
                "ko"
        );


        requestJson.addProperty(
                "target",
                targetLanguage
        );


        requestJson.addProperty(
                "format",
                "text"
        );



        // JSON → 문자열
        String jsonBody =
                requestJson.toString();


        System.out.println(
                "Google 요청 : "
                + jsonBody
        );



        /*
         * =====================================================
         * Google 서버로 JSON 전송
         * =====================================================
         */

        try (OutputStream os =
                     conn.getOutputStream()) {


            byte[] input =
                    jsonBody.getBytes(
                            StandardCharsets.UTF_8
                    );


            os.write(input);
        }



        /*
         * =====================================================
         * Google 응답 코드 확인
         * =====================================================
         */

        int responseCode =
                conn.getResponseCode();


        System.out.println(
                "Google 응답 코드 : "
                + responseCode
        );



        /*
         * =====================================================
         * Google 응답 읽기
         * =====================================================
         */

        BufferedReader br;


        if (responseCode >= 200
                && responseCode < 300) {


            // 성공
            br = new BufferedReader(
                    new InputStreamReader(
                            conn.getInputStream(),
                            StandardCharsets.UTF_8
                    )
            );


        } else {


            // 실패
            br = new BufferedReader(
                    new InputStreamReader(
                            conn.getErrorStream(),
                            StandardCharsets.UTF_8
                    )
            );
        }



        StringBuilder response =
                new StringBuilder();


        String line;


        while ((line = br.readLine()) != null) {

            response.append(line);
        }


        br.close();



        System.out.println(
                "Google 응답 JSON : "
                + response.toString()
        );



        /*
         * =====================================================
         * HTTP 오류 처리
         * =====================================================
         */

        if (responseCode < 200
                || responseCode >= 300) {

            conn.disconnect();

            throw new RuntimeException(
                    "Google Translation API 요청 실패 : "
                    + responseCode
                    + " / "
                    + response.toString()
            );
        }



        /*
         * =====================================================
         * JSON 분석
         * =====================================================
         */

        Gson gson =
                new Gson();


        JsonObject responseJson =
                gson.fromJson(
                        response.toString(),
                        JsonObject.class
                );



        /*
         * Google 응답 구조
         *
         * data
         *   ↓
         * translations
         *   ↓
         * 0번째
         *   ↓
         * translatedText
         */

        String translatedText =
                responseJson
                        .getAsJsonObject("data")
                        .getAsJsonArray("translations")
                        .get(0)
                        .getAsJsonObject()
                        .get("translatedText")
                        .getAsString();



        System.out.println(
                "번역 결과 : "
                + translatedText
        );


        conn.disconnect();


        return translatedText;
    }
}
