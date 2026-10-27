package controller;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.ReviewDao;

import java.sql.SQLException;
import java.util.List;

import vo.ReviewVo;

// =========================================================
// 주제: 파일 역할
// 설명: ReviewController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================



@WebServlet("/Review")
public class ReviewController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ReviewDao reviewDao = new ReviewDao();

    @Override
    // =========================================================
    // 주제: doGet 처리
    // 설명: GET 요청을 받아 필요한 데이터를 준비하고 화면 또는 응답으로 전달
    // =========================================================
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("====================================");
        System.out.println("[ReviewController] doGet() 실행 시작");
        System.out.println("[ReviewController] 요청 URI : "
                + request.getRequestURI());
        System.out.println("====================================");

        request.setCharacterEncoding("UTF-8");

        // DB 조회 시작
        System.out.println("[ReviewController] DB 조회 시작");

        int count = reviewDao.getReviewCount();

        // DB 조회 결과
        System.out.println("[ReviewController] DB 조회 완료");
        System.out.println("[ReviewController] DB 리뷰 개수 : " + count);

        if (count < 0) {
            System.out.println("[ReviewController] DB 조회 실패");

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "리뷰 DB 조회 실패"
            );
            return;
        }

        request.setAttribute("reviewCount", count);
        
        try {
            List<ReviewVo> reviewList = reviewDao.getReviewList();

            request.setAttribute("reviewList", reviewList);
            
            ObjectMapper mapper = new ObjectMapper();

            String reviewListJson = mapper.writeValueAsString(reviewList);

            // JSP의 script 태그 안에서도 안전하게 출력되도록 처리
            reviewListJson = reviewListJson.replace("<", "\\u003c");

            request.setAttribute("reviewListJson", reviewListJson);
            

            // ========================================
            // 좋아요 초기 조회
            // ========================================

            // 모든 리뷰의 좋아요 개수
            java.util.Map<Long, Integer> reviewLikeCounts =
                    reviewDao.getReviewLikeCounts();

            request.setAttribute(
                "reviewLikesJson",
                mapper.writeValueAsString(reviewLikeCounts)
            );

            // 로그인한 회원의 좋아요 목록
            java.util.List<Long> likedReviewIds =
                    new java.util.ArrayList<>();

            javax.servlet.http.HttpSession session =
                    request.getSession(false);

            if (session != null
                    && session.getAttribute("loginId") instanceof String) {

                String memberId =
                    (String) session.getAttribute("loginId");

                likedReviewIds =
                    reviewDao.getLikedReviewIds(memberId);
            }

            request.setAttribute(
                "reviewLikedJson",
                mapper.writeValueAsString(likedReviewIds)
            );

            System.out.println(
                "[ReviewController] 좋아요 개수 조회: "
                + reviewLikeCounts.size() + "개 리뷰"
            );


            System.out.println(
                "[ReviewController] 조회한 리뷰 목록 : "
                + reviewList.size() + "개"
            );

        } catch (SQLException e) {
            System.err.println("[ReviewController] 리뷰 목록 조회 실패");
            e.printStackTrace();

            response.sendError(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "리뷰 목록 조회 중 오류가 발생했습니다."
            );
            return;
        }

     // ========================================
     // 리뷰 게시판 화면 이동
     // ========================================
     System.out.println("[ReviewController] review.jsp 이동");

     request.getRequestDispatcher("/WEB-INF/review/review.jsp")
            .forward(request, response);
    }
}
