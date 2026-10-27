package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import util.DBCPUtil;

// =========================================================
// 주제: 파일 역할
// 설명: MypageGuideApiController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


//=========================================================
// 주제: 가이드 예약/테스트 결제 내역 마이페이지 API
// 설명: toss_test_order의 로그인 회원 데이터만 목록/상세 조회
//       결제 대기(PENDING)만 취소 가능 (PAID 환불 처리 아님)
// 주의: 테스트 결제 완료 = 실제 가이드 예약 확정이 아님
//=========================================================
@WebServlet(urlPatterns = {
        "/mypage/guide/list",
        "/mypage/guide/detail",
        "/mypage/guide/cancel"
})
public class MypageGuideApiController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final ObjectMapper JSON = new ObjectMapper();

    // 현재 결제/로그인 기능은 loginId 세션을 사용합니다.
    // =========================================================
    // 주제: memberId 처리
    // 설명: memberId 기능에 필요한 처리 로직을 수행
    // =========================================================
    private String memberId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        Object id = session.getAttribute("loginId");
        if (!(id instanceof String) || ((String) id).trim().isEmpty()) {
            id = session.getAttribute("id");
        }
        if (!(id instanceof String)) return null;
        String value = ((String) id).trim();
        return value.isEmpty() ? null : value;
    }

    // =========================================================
    // 주제: respond 처리
    // 설명: respond 기능에 필요한 처리 로직을 수행
    // =========================================================
    private void respond(HttpServletResponse response, int status, Map<String, Object> data) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json; charset=UTF-8");
        response.setHeader("Cache-Control", "no-store");
        JSON.writeValue(response.getWriter(), data);
    }

    private Map<String, Object> reply(boolean success, String message) {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("success", success);
        r.put("message", message);
        return r;
    }

    // =========================================================
    // 주제: val 처리
    // 설명: val 기능에 필요한 처리 로직을 수행
    // =========================================================
    private String val(ResultSet rs, String column) throws SQLException {
        String value = rs.getString(column);
        return value == null ? "" : value;
    }

    // toss_test_order 컬럼은 기존 팀원의 TossTestOrderDao에서 실제 사용하는 항목만 조회합니다.
    private static final String SELECT_BASE =
            "SELECT o.order_id, o.guide_id, o.reservation_date, o.people_count, "
            + "o.amount, o.status, o.payment_method, o.approved_at, "
            + "g.guide_name, g.guide_image, g.region_name, g.guide_intro, "
            + "g.specialty, g.rating, g.review_count, g.guide_phone, g.price "
            + "FROM toss_test_order o LEFT JOIN guide g ON g.guide_id = o.guide_id ";

    private Map<String, Object> row(ResultSet rs) throws SQLException {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("reservationId", val(rs, "order_id")); // 주문번호는 문자형입니다.
        m.put("guideId", rs.getInt("guide_id"));
        m.put("guideName", val(rs, "guide_name"));
        m.put("guideImage", val(rs, "guide_image"));
        m.put("regionName", val(rs, "region_name"));
        m.put("reservationDate", val(rs, "reservation_date"));
        m.put("peopleCount", rs.getInt("people_count"));
        m.put("amount", rs.getLong("amount"));
        m.put("status", val(rs, "status").toUpperCase(java.util.Locale.ROOT));
        // 기존 테이블의 생성일시 컬럼은 확인되지 않아, 없는 날짜를 임의로 만들지 않습니다.
        m.put("createdAt", "");
        m.put("approvedAt", val(rs, "approved_at"));
        return m;
    }

    @Override
    // =========================================================
    // 주제: doGet 처리
    // 설명: GET 요청을 받아 필요한 데이터를 준비하고 화면 또는 응답으로 전달
    // =========================================================
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String member = memberId(request);
        if (member == null) {
            respond(response, 401, reply(false, "로그인이 필요합니다."));
            return;
        }
        String path = request.getServletPath();
        try (Connection con = DBCPUtil.getConnection()) {
            if ("/mypage/guide/list".equals(path)) {
                List<Map<String, Object>> list = new ArrayList<>();
                String sql = SELECT_BASE + "WHERE o.member_id = ? ORDER BY o.reservation_date DESC, o.order_id DESC";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, member);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) list.add(row(rs));
                    }
                }
                Map<String, Object> out = reply(true, "");
                out.put("list", list);
                respond(response, 200, out);
            } else if ("/mypage/guide/detail".equals(path)) {
                String orderId = request.getParameter("reservationId");
                if (orderId == null || orderId.trim().isEmpty() || orderId.length() > 150) {
                    respond(response, 400, reply(false, "예약번호가 올바르지 않습니다."));
                    return;
                }
                Map<String, Object> out = loadDetail(con, member, orderId.trim());
                if (out == null) {
                    respond(response, 404, reply(false, "해당 예약을 찾을 수 없습니다."));
                    return;
                }
                Map<String, Object> reply = reply(true, "");
                reply.put("data", out);
                respond(response, 200, reply);
            } else {
                respond(response, 404, reply(false, "지원하지 않는 요청입니다."));
            }
        } catch (SQLException e) {
            log("가이드 예약 내역 조회 오류", e);
            respond(response, 500, reply(false, "가이드 예약 내역을 불러오지 못했습니다."));
        }
    }

    private Map<String, Object> loadDetail(Connection con, String member, String orderId) throws SQLException {
        String sql = SELECT_BASE + "WHERE o.member_id = ? AND o.order_id = ?";
        Map<String, Object> result;
        int guideId;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, member);
            ps.setString(2, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                result = new LinkedHashMap<>();
                result.put("reservation", row(rs));
                guideId = rs.getInt("guide_id");
                Map<String, Object> guide = new LinkedHashMap<>();
                guide.put("guideIntro", val(rs, "guide_intro"));
                guide.put("specialty", val(rs, "specialty"));
                guide.put("rating", rs.getBigDecimal("rating"));
                guide.put("reviewCount", rs.getInt("review_count"));
                guide.put("guidePhone", val(rs, "guide_phone"));
                guide.put("price", rs.getInt("price"));
                result.put("guide", guide);
                Map<String, Object> payment = new LinkedHashMap<>();
                payment.put("amount", rs.getLong("amount"));
                payment.put("paymentMethod", val(rs, "payment_method"));
                payment.put("status", val(rs, "status"));
                payment.put("approvedAt", val(rs, "approved_at"));
                payment.put("orderId", val(rs, "order_id"));
                result.put("payment", payment);
            }
        }
        Map<String, Object> tour = new LinkedHashMap<>();
        tour.put("tourTitle", "기본 가이드 투어");
        tour.put("duration", "");
        tour.put("included", "");
        tour.put("excluded", "");
        tour.put("cancellationPolicy", "");
        List<Map<String, Object>> courses = new ArrayList<>();
        String tourSql = "SELECT tour_id, tour_title, duration, included, excluded, cancellation_policy "
                + "FROM guide_tour WHERE guide_id=? ORDER BY tour_id LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(tourSql)) {
            ps.setInt(1, guideId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int tourId = rs.getInt("tour_id");
                    tour.put("tourTitle", val(rs, "tour_title"));
                    tour.put("duration", val(rs, "duration"));
                    tour.put("included", val(rs, "included"));
                    tour.put("excluded", val(rs, "excluded"));
                    tour.put("cancellationPolicy", val(rs, "cancellation_policy"));
                    String courseSql = "SELECT course_name, duration_minutes FROM guide_tour_course "
                            + "WHERE tour_id=? ORDER BY course_order, course_id";
                    try (PreparedStatement coursePs = con.prepareStatement(courseSql)) {
                        coursePs.setInt(1, tourId);
                        try (ResultSet courseRs = coursePs.executeQuery()) {
                            while (courseRs.next()) {
                                Map<String, Object> course = new LinkedHashMap<>();
                                course.put("courseName", val(courseRs, "course_name"));
                                course.put("durationMinutes", courseRs.getInt("duration_minutes"));
                                courses.add(course);
                            }
                        }
                    }
                }
            }
        }
        tour.put("courses", courses);
        result.put("tour", tour);
        return result;
    }

    @Override
    // =========================================================
    // 주제: doPost 처리
    // 설명: POST 요청을 받아 입력값을 검증하고 저장/처리 결과를 응답
    // =========================================================
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        String member = memberId(request);
        if (member == null) {
            respond(response, 401, reply(false, "로그인이 필요합니다."));
            return;
        }
        if (!"/mypage/guide/cancel".equals(request.getServletPath())) {
            respond(response, 404, reply(false, "지원하지 않는 요청입니다."));
            return;
        }
        String orderId = request.getParameter("reservationId");
        if (orderId == null || orderId.trim().isEmpty() || orderId.length() > 150) {
            respond(response, 400, reply(false, "예약번호가 올바르지 않습니다."));
            return;
        }
        // 카드 승인(PAID) 건은 결제 취소/환불 처리 없이 상태값만 바꾸면 안 됩니다.
        String sql = "UPDATE toss_test_order SET status='CANCELED' "
                + "WHERE order_id=? AND member_id=? AND status='PENDING'";
        try (Connection con = DBCPUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, orderId.trim());
            ps.setString(2, member);
            int updated = ps.executeUpdate();
            if (updated != 1) {
                respond(response, 409, reply(false, "결제 대기 중인 본인 주문만 취소할 수 있습니다. 결제 완료 건은 환불 절차가 필요합니다."));
                return;
            }
            Map<String, Object> out = reply(true, "결제 대기 주문이 취소되었습니다.");
            out.put("status", "CANCELED");
            respond(response, 200, out);
        } catch (SQLException e) {
            log("가이드 예약 결제 대기 취소 오류", e);
            respond(response, 500, reply(false, "취소 처리에 실패했습니다."));
        }
    }
}
