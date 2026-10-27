package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// =========================================================
// 주제: 파일 역할
// 설명: PrivacyPolicyController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


@WebServlet("/PrivacyPolicy")
public class PrivacyPolicyController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    // =========================================================
    // 주제: doGet 처리
    // 설명: GET 요청을 받아 필요한 데이터를 준비하고 화면 또는 응답으로 전달
    // =========================================================
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("[PrivacyPolicyController] 개인정보처리방침 화면 요청");

        request.getRequestDispatcher(
            "/WEB-INF/privacy/privacyPolicy.jsp"
        ).forward(request, response);
    }
}
