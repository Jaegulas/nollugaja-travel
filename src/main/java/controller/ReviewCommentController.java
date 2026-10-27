package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import dao.ReviewCommentDao;
import vo.ReviewCommentVo;

// =========================================================
// 주제: 파일 역할
// 설명: ReviewCommentController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


@WebServlet("/ReviewComment")
public class ReviewCommentController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ReviewCommentDao commentDao = new ReviewCommentDao();
    private final ObjectMapper mapper = new ObjectMapper();

    // ========================================
    // GET : 댓글 목록 조회
    // ========================================
    @Override
    // =========================================================
    // 주제: doGet 처리
    // 설명: GET 요청을 받아 필요한 데이터를 준비하고 화면 또는 응답으로 전달
    // =========================================================
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        long reviewId = parseId(request.getParameter("reviewId"));

        if (reviewId <= 0) {
            response.sendError(400, "잘못된 게시글 번호입니다.");
            return;
        }

        try {
            List<ReviewCommentVo> comments =
                    commentDao.getComments(reviewId);

            mapper.writeValue(response.getWriter(), comments);

            System.out.println(
                "[ReviewComment] 댓글 조회 성공 : " + reviewId
            );

        } catch (SQLException e) {
            System.err.println("[ReviewComment] 댓글 조회 실패");
            e.printStackTrace();

            if (!response.isCommitted()) {
                response.sendError(500, "댓글 조회 중 오류가 발생했습니다.");
            }
        }
    }

    // ========================================
    // POST : 댓글 작성 / 수정 / 삭제
    // ========================================
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

        if (session == null
                || !(session.getAttribute("loginId") instanceof String)
                || ((String) session.getAttribute("loginId")).trim().isEmpty()) {

            response.sendError(401, "로그인이 필요합니다.");
            return;
        }

        String memberId = (String) session.getAttribute("loginId");

        // 2. 닉네임 가져오기
        Object name = session.getAttribute("loginName");

        String authorNickname =
                name == null ? memberId : name.toString();

        // 3. 관리자 권한 확인
        // 실제 로그인 시스템에서 role을 설정해야 관리자 권한이 적용됨
        boolean isAdmin =
                "ADMIN".equals(session.getAttribute("role"));

        // 4. 요청 종류 확인
        String action = request.getParameter("action");
        String content = request.getParameter("content");

        if (!"create".equals(action)
                && !"update".equals(action)
                && !"delete".equals(action)) {

            response.sendError(400, "잘못된 요청입니다.");
            return;
        }

        // 5. 작성 / 수정 시 내용 검사
        if (!"delete".equals(action)) {

            if (content == null
                    || content.trim().isEmpty()
                    || content.trim().length() > 1000) {

                response.sendError(400, "댓글은 1~1000자로 입력해 주세요.");
                return;
            }

            content = content.trim();
        }

        try {

            int result;

            // ========================================
            // 댓글 작성
            // ========================================
            if ("create".equals(action)) {

                long reviewId =
                        parseId(request.getParameter("reviewId"));

                if (reviewId <= 0) {
                    response.sendError(400, "잘못된 게시글 번호입니다.");
                    return;
                }

                result = commentDao.insertComment(
                        reviewId,
                        memberId,
                        authorNickname,
                        content
                );
                
                System.out.println("=================================");
                System.out.println("[ReviewComment] 댓글 등록 확인");
                System.out.println("reviewId : " + reviewId);
                System.out.println("memberId : " + memberId);
                System.out.println("authorNickname : " + authorNickname);
                System.out.println("INSERT 결과 : " + result);
                System.out.println("=================================");

                System.out.println(
                    "[ReviewComment] 댓글 작성 요청 : " + reviewId
                );

            // ========================================
            // 댓글 수정 / 삭제
            // ========================================
            } else {

                long commentId =
                        parseId(request.getParameter("commentId"));

                if (commentId <= 0) {
                    response.sendError(400, "잘못된 댓글 번호입니다.");
                    return;
                }

                if ("update".equals(action)) {

                    result = commentDao.updateComment(
                            commentId,
                            memberId,
                            isAdmin,
                            content
                    );

                    System.out.println(
                        "[ReviewComment] 댓글 수정 요청 : " + commentId
                    );

                } else {

                    result = commentDao.deleteComment(
                            commentId,
                            memberId,
                            isAdmin
                    );

                    System.out.println(
                        "[ReviewComment] 댓글 삭제 요청 : " + commentId
                    );
                }
            }

            // 6. 처리 결과 응답
            if (result == 1) {

                response.setStatus(
                    HttpServletResponse.SC_NO_CONTENT
                );

            } else {

                response.sendError(
                    "create".equals(action) ? 404 : 403,
                    "대상이 없거나 처리 권한이 없습니다."
                );
            }

        } catch (SQLException e) {

            System.err.println("[ReviewComment] DB 처리 실패");
            e.printStackTrace();

            response.sendError(
                500,
                "댓글 처리 중 오류가 발생했습니다."
            );
        }
    }

    // =========================================================
    // 주제: parseId 처리
    // 설명: 입력 데이터를 화면/API/DB에서 사용하기 좋은 형태로 변환 또는 가공
    // =========================================================
    private long parseId(String value) {

        try {
            long id = Long.parseLong(value);
            return id > 0 ? id : -1;

        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
