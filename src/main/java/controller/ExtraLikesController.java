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
// 설명: ExtraLikesController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


//=========================================================
// 주제: 숙소 + 문화유산 찜 DB / 마이페이지 연결
// 설명: 로그인 회원의 찜 상태 저장 / 취소 / 목록 조회
// 주의: 다른 찜(음식, 여행지)은 기존 컨트롤러 그대로 사용
//=========================================================
@WebServlet("/ExtraLikes")
public class ExtraLikesController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final ObjectMapper JSON = new ObjectMapper();

    // =========================================================
    // 주제: loginMember 처리
    // 설명: 로그인 관련 검증과 세션 처리를 수행
    // =========================================================
    private String loginMember(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        Object id = session.getAttribute("loginId");
        if (!(id instanceof String) || ((String) id).trim().isEmpty()) {
            id = session.getAttribute("id"); // 현재 프로젝트 LoginController의 호환용 세션 키
        }
        if (!(id instanceof String)) return null;
        String memberId = ((String) id).trim();
        return memberId.isEmpty() ? null : memberId;
    }

    // =========================================================
    // 주제: json 처리
    // 설명: json 기능에 필요한 처리 로직을 수행
    // =========================================================
    private void json(HttpServletResponse response, int status, Map<String, Object> data) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json; charset=UTF-8");
        response.setHeader("Cache-Control", "no-store");
        JSON.writeValue(response.getWriter(), data);
    }

    private Map<String, Object> result(boolean success, String message) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("success", success);
        out.put("message", message);
        return out;
    }

    @Override
    // =========================================================
    // 주제: doGet 처리
    // 설명: GET 요청을 받아 필요한 데이터를 준비하고 화면 또는 응답으로 전달
    // =========================================================
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String memberId = loginMember(request);
        if (memberId == null) {
            json(response, HttpServletResponse.SC_UNAUTHORIZED, result(false, "로그인이 필요합니다."));
            return;
        }

        List<Map<String, Object>> rows = new ArrayList<>();
        // 문화유산 데이터는 cultural_heritage.content_id와 연결해야 합니다.
        // 문화유산 정보가 없는 경우에도 찜 내역 자체는 숨기지 않습니다 (LEFT JOIN).
        String accSql = "SELECT l.accommodation_id AS id, a.name AS name, a.addr AS addr, "
                + "a.picpath AS image, DATE_FORMAT(l.created_at, '%Y-%m-%d') AS likedAt "
                + "FROM accommodation_like l LEFT JOIN accommodation a "
                + "ON a.accommodation_id = l.accommodation_id WHERE l.member_id = ?";
        String heritageSql = "SELECT l.heritage_id AS id, "
                + "COALESCE(NULLIF(c.title, ''), CONCAT('문화유산 #', l.heritage_id)) AS name, "
                + "COALESCE(c.addr1, '') AS addr, COALESCE(c.first_image, '') AS image, "
                + "DATE_FORMAT(l.created_at, '%Y-%m-%d') AS likedAt "
                + "FROM cultural_like l LEFT JOIN cultural_heritage c "
                + "ON c.content_id = l.heritage_id WHERE l.member_id = ?";
        try (Connection con = DBCPUtil.getConnection()) {
            addRows(con, accSql, "acc", memberId, rows);
            addRows(con, heritageSql, "heritage", memberId, rows);
            Map<String, Object> out = result(true, "");
            out.put("list", rows);
            json(response, HttpServletResponse.SC_OK, out);
        } catch (SQLException e) {
            log("숙소/문화유산 찜 목록 조회 실패", e);
            json(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    result(false, "숙소/문화유산 찜 조회에 실패했습니다."));
        }
    }

    // =========================================================
    // 주제: addRows 처리
    // 설명: 입력받은 데이터를 생성하거나 저장
    // =========================================================
    private void addRows(Connection con, String sql, String type, String memberId,
            List<Map<String, Object>> rows) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> entry = new LinkedHashMap<>();
                    entry.put("type", type);
                    entry.put("id", rs.getInt("id"));
                    entry.put("name", rs.getString("name"));
                    entry.put("addr", rs.getString("addr"));
                    entry.put("image", rs.getString("image"));
                    entry.put("likedAt", rs.getString("likedAt"));
                    entry.put("url", type.equals("acc")
                            ? "/Accommodation/acco_main" : "/Cultural");
                    rows.add(entry);
                }
            }
        }
    }

    @Override
    // =========================================================
    // 주제: doPost 처리
    // 설명: POST 요청을 받아 입력값을 검증하고 저장/처리 결과를 응답
    // =========================================================
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        String memberId = loginMember(request);
        if (memberId == null) {
            json(response, HttpServletResponse.SC_UNAUTHORIZED, result(false, "로그인이 필요합니다."));
            return;
        }

        String type = request.getParameter("type");
        String rawId = request.getParameter("id");
        String rawLiked = request.getParameter("liked");
        if (!("acc".equals(type) || "heritage".equals(type))
                || !("true".equals(rawLiked) || "false".equals(rawLiked))) {
            json(response, HttpServletResponse.SC_BAD_REQUEST, result(false, "잘못된 찜 요청입니다."));
            return;
        }
        int itemId;
        try {
            itemId = Integer.parseInt(rawId);
            if (itemId <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            json(response, HttpServletResponse.SC_BAD_REQUEST, result(false, "유효하지 않은 장소 번호입니다."));
            return;
        }

        boolean desired = Boolean.parseBoolean(rawLiked);
        String table = "acc".equals(type) ? "accommodation_like" : "cultural_like";
        String column = "acc".equals(type) ? "accommodation_id" : "heritage_id";
        String findSql = "SELECT 1 FROM " + table + " WHERE member_id = ? AND " + column + " = ? LIMIT 1";
        String insertSql = "INSERT INTO " + table + " (member_id, " + column + ") VALUES (?, ?)";
        String deleteSql = "DELETE FROM " + table + " WHERE member_id = ? AND " + column + " = ?";

        try (Connection con = DBCPUtil.getConnection()) {
            // 동일 상태 중복 요청 방지. DB의 UNIQUE(member_id, item_id) 설정도 권장합니다.
            boolean existed;
            try (PreparedStatement ps = con.prepareStatement(findSql)) {
                ps.setString(1, memberId);
                ps.setInt(2, itemId);
                try (ResultSet rs = ps.executeQuery()) { existed = rs.next(); }
            }
            if (desired != existed) {
                try (PreparedStatement ps = con.prepareStatement(desired ? insertSql : deleteSql)) {
                    ps.setString(1, memberId);
                    ps.setInt(2, itemId);
                    ps.executeUpdate();
                }
            }
            Map<String, Object> out = result(true, "");
            out.put("liked", desired);
            json(response, HttpServletResponse.SC_OK, out);
        } catch (SQLException e) {
            log("숙소/문화유산 찜 저장 실패: " + type + ", id=" + itemId, e);
            json(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    result(false, "찜 저장 또는 취소에 실패했습니다."));
        }
    }
}
