package controller;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.ReviewDao;

// =========================================================
// 주제: 파일 역할
// 설명: ReviewDeleteController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


@WebServlet("/ReviewDelete")
public class ReviewDeleteController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ReviewDao reviewDao = new ReviewDao();

    @Override
    // =========================================================
    // 주제: doPost 처리
    // 설명: POST 요청을 받아 입력값을 검증하고 저장/처리 결과를 응답
    // =========================================================
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // 1. 로그인 확인
        HttpSession session = request.getSession(false);

        if (session == null ||
            !(session.getAttribute("loginId") instanceof String)) {

            response.sendError(
                HttpServletResponse.SC_UNAUTHORIZED,
                "로그인이 필요합니다."
            );
            return;
        }

        String memberId = (String) session.getAttribute("loginId");

        // 2. 관리자 권한 확인
        // 실제 로그인 시스템의 관리자 세션 속성명에 맞춰야 함
        boolean isAdmin =
            "ADMIN".equals(session.getAttribute("role"));

        // 3. 삭제할 리뷰 번호 확인
        String reviewIdParam = request.getParameter("reviewId");

        long reviewId;

        try {
            reviewId = Long.parseLong(reviewIdParam);

            if (reviewId <= 0) {
                response.sendError(400, "잘못된 리뷰 번호입니다.");
                return;
            }

        } catch (NumberFormatException e) {
            response.sendError(400, "잘못된 리뷰 번호입니다.");
            return;
        }

        // 4. DB 삭제
        try {

            int result = reviewDao.deleteReview(
                reviewId,
                memberId,
                isAdmin
            );

            if (result == 1) {

                System.out.println(
                    "[ReviewDelete] 삭제 성공 : " + reviewId
                );

                response.setStatus(HttpServletResponse.SC_NO_CONTENT);

            } else {

                response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "삭제 권한이 없거나 존재하지 않는 리뷰입니다."
                );
            }

        } catch (SQLException e) {

            System.err.println("[ReviewDelete] 리뷰 삭제 실패");
            e.printStackTrace();

            response.sendError(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "리뷰 삭제 중 오류가 발생했습니다."
            );
        }
    }
}
