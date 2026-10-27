
package controller;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// =========================================================
// 주제: 파일 역할
// 설명: LogoutController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


@WebServlet("/Logout")
public class LogoutController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    // =========================================================
    // 주제: doGet 처리
    // 설명: GET 요청을 받아 필요한 데이터를 준비하고 화면 또는 응답으로 전달
    // =========================================================
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException {

        // 1. 현재 로그인 세션 가져오기
        HttpSession session = request.getSession(false);

        // 2. 로그인 정보 전체 삭제
        if (session != null) {
            session.invalidate();
        }

        // 3. 메인 화면으로 이동
        response.sendRedirect(
            request.getContextPath() + "/Main"
        );
    }
}
