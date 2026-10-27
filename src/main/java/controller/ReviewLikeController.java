
package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import dao.ReviewDao;

// =========================================================
// 주제: 파일 역할
// 설명: ReviewLikeController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


@WebServlet("/ReviewLike")
public class ReviewLikeController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ReviewDao reviewDao = new ReviewDao();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    // =========================================================
    // 주제: doPost 처리
    // 설명: POST 요청을 받아 입력값을 검증하고 저장/처리 결과를 응답
    // =========================================================
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null
                || !(session.getAttribute("loginId") instanceof String)) {
            response.sendError(401, "로그인이 필요합니다.");
            return;
        }

        String memberId = (String) session.getAttribute("loginId");

        long reviewId;

        try {
            reviewId = Long.parseLong(
                request.getParameter("reviewId")
            );

            if (reviewId <= 0) {
                response.sendError(400, "잘못된 리뷰 번호입니다.");
                return;
            }

        } catch (NumberFormatException | NullPointerException e) {
            response.sendError(400, "잘못된 리뷰 번호입니다.");
            return;
        }

        try {
            Map<String, Object> result =
                reviewDao.toggleReviewLike(reviewId, memberId);

            mapper.writeValue(response.getWriter(), result);

            System.out.println(
                "[ReviewLike] 리뷰 ID: " + reviewId
                + " / 회원: " + memberId
                + " / 좋아요 상태: " + result.get("liked")
                + " / 개수: " + result.get("likes")
            );

        } catch (SQLException e) {
            System.err.println("[ReviewLike] 좋아요 처리 실패");
            e.printStackTrace();

            response.sendError(500, "좋아요 처리 중 오류가 발생했습니다.");
        }
    }
}
