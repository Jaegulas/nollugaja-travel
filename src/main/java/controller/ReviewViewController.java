
package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

// =========================================================
// 주제: 파일 역할
// 설명: ReviewViewController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


@WebServlet("/ReviewView")
public class ReviewViewController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    // =========================================================
    // 주제: doPost 처리
    // 설명: POST 요청을 받아 입력값을 검증하고 저장/처리 결과를 응답
    // =========================================================
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
    	
    	System.out.println("[ReviewViewController] 요청 도착!");

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        // ========================================
        // 1. 리뷰 번호 확인
        // ========================================
        long reviewId;

        try {
            reviewId = Long.parseLong(
                request.getParameter("reviewId")
            );

            if (reviewId <= 0) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException e) {

            response.setStatus(
                HttpServletResponse.SC_BAD_REQUEST
            );

            response.getWriter().write(
                "{\"success\":false,\"message\":\"잘못된 리뷰 번호입니다.\"}"
            );

            return;
        }

        // ========================================
        // 2. 회원 / 비회원 구분
        // ========================================
        HttpSession session = request.getSession(false);

        String memberId = null;
        String ipAddress = null;
        String viewerType;

        if (session != null
                && session.getAttribute("loginId") != null) {

            // 회원: 회원 ID 기록
            memberId = String.valueOf(
                session.getAttribute("loginId")
            );

            viewerType = "MEMBER";

        } else {

            // 비회원: IP 주소 기록
            ipAddress = request.getRemoteAddr();

            viewerType = "GUEST";
        }

        // ========================================
        // 3. DB 연결
        // ========================================
        Connection con = null;

        try {

            DataSource ds = (DataSource)
                new InitialContext().lookup(
                    "java:comp/env/jdbc/travel"
                );

            con = ds.getConnection();

            // 조회수 증가와 조회 기록을 함께 처리
            con.setAutoCommit(false);

            int views;

            // ========================================
            // 4. 조회수 +1
            // ========================================
            String updateSql =
                "UPDATE review " +
                "SET view_count = view_count + 1 " +
                "WHERE review_id = ?";

            try (PreparedStatement pstmt =
                     con.prepareStatement(updateSql)) {

                pstmt.setLong(1, reviewId);

                int updated = pstmt.executeUpdate();

                if (updated != 1) {

                    con.rollback();

                    response.setStatus(
                        HttpServletResponse.SC_NOT_FOUND
                    );

                    response.getWriter().write(
                        "{\"success\":false,\"message\":\"리뷰를 찾을 수 없습니다.\"}"
                    );

                    return;
                }
            }

            // ========================================
            // 5. 조회 기록 DB 저장
            // ========================================
            String insertSql =
                "INSERT INTO review_view_log " +
                "(review_id, member_id, ip_address, viewer_type) " +
                "VALUES (?, ?, ?, ?)";

            try (PreparedStatement pstmt =
                     con.prepareStatement(insertSql)) {

                pstmt.setLong(1, reviewId);
                pstmt.setString(2, memberId);
                pstmt.setString(3, ipAddress);
                pstmt.setString(4, viewerType);

                pstmt.executeUpdate();
            }

            // ========================================
            // 6. 최신 조회수 조회
            // ========================================
            String selectSql =
                "SELECT view_count " +
                "FROM review " +
                "WHERE review_id = ?";

            try (PreparedStatement pstmt =
                     con.prepareStatement(selectSql)) {

                pstmt.setLong(1, reviewId);

                try (ResultSet rs = pstmt.executeQuery()) {

                    if (!rs.next()) {
                        throw new IllegalStateException(
                            "조회수 조회 실패"
                        );
                    }

                    views = rs.getInt("view_count");
                }
            }

            // ========================================
            // 7. DB 저장 확정
            // ========================================
            con.commit();

            System.out.println(
                "[ReviewView] 리뷰 ID : " + reviewId
                + " / 구분 : " + viewerType
                + " / 회원 ID : " + memberId
                + " / 비회원 IP : " + ipAddress
                + " / 조회수 : " + views
            );

            // ========================================
            // 8. 화면에 최신 조회수 전달
            // ========================================
            response.getWriter().write(
                "{\"success\":true,\"views\":" + views + "}"
            );

        } catch (Exception e) {

            if (con != null) {
                try {
                    con.rollback();
                } catch (Exception ignored) {
                }
            }

            System.err.println(
                "[ReviewViewController] 조회수 저장 오류"
            );

            e.printStackTrace();

            response.setStatus(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            response.getWriter().write(
                "{\"success\":false,\"message\":\"조회수 저장 실패\"}"
            );

        } finally {

            if (con != null) {
                try {
                    con.close();
                } catch (Exception ignored) {
                }
            }
        }
    }
}
