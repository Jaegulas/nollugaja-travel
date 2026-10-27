package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import service.TranslateService;

// =========================================================
// 주제: 파일 역할
// 설명: TranslateController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


@WebServlet("/translate")
public class TranslateController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // Service 객체 생성
    private TranslateService translateService = new TranslateService();


    @Override
    // =========================================================
    // 주제: doPost 처리
    // 설명: POST 요청을 받아 입력값을 검증하고 저장/처리 결과를 응답
    // =========================================================
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        // 한글 처리
        request.setCharacterEncoding("UTF-8");

        // 응답은 JSON
        response.setContentType("application/json; charset=UTF-8");


        // =====================================
        // 1. fetch()가 보낸 JSON 읽기
        // =====================================

        BufferedReader reader = request.getReader();

        Gson gson = new Gson();

        TranslateRequest translateRequest =
                gson.fromJson(reader, TranslateRequest.class);


        System.out.println("=== TranslateController 도착 ===");

        System.out.println(
                "문장 : " + translateRequest.getTexts()
        );

        System.out.println(
                "목표 언어 : " + translateRequest.getTargetLanguage()
        );


        // =====================================
        // 2. Service에게 번역 업무 요청
        // =====================================

        List<String> translatedTexts =
                translateService.translate(
                        translateRequest.getTexts(),
                        translateRequest.getTargetLanguage()
                );


        // =====================================
        // 3. Service 결과를 JSON으로 변환
        // =====================================

        String json = gson.toJson(translatedTexts);


        // =====================================
        // 4. 브라우저에게 JSON 응답
        // =====================================

        response.getWriter().write(json);
    }


    // =========================================
    // fetch에서 받은 데이터를 담는 DTO
    // =========================================

    private static class TranslateRequest {

        private List<String> texts;

        private String targetLanguage;


        // =========================================================
        // 주제: getTexts 처리
        // 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
        // =========================================================
        public List<String> getTexts() {

            return texts;
        }


        // =========================================================
        // 주제: getTargetLanguage 처리
        // 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
        // =========================================================
        public String getTargetLanguage() {

            return targetLanguage;
        }
    }
}
