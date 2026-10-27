package controller;

import dao.GuideLikeDAO;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// =========================================================
// 주제: 파일 역할
// 설명: GuideLikeController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


@WebServlet("/GuideLike")
public class GuideLikeController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final GuideLikeDAO dao = new GuideLikeDAO();

    // LoginController.java에서 session.setAttribute("id", member.getId())로 저장합니다. -> loginId로 수정 2026.10.24토 권성애
    // =========================================================
    // 주제: memberId 처리
    // 설명: memberId 기능에 필요한 처리 로직을 수행
    // =========================================================
    private String memberId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        Object value = session.getAttribute("id");
        return value == null ? null : String.valueOf(value);
    }
    // =========================================================
    // 주제: json 처리
    // 설명: json 기능에 필요한 처리 로직을 수행
    // =========================================================
    private void json(HttpServletResponse response, int status, String body) throws IOException {
        response.setStatus(status);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store");
        response.getWriter().write(body);
    }
    @Override
    // =========================================================
    // 주제: doGet 처리
    // 설명: GET 요청을 받아 필요한 데이터를 준비하고 화면 또는 응답으로 전달
    // =========================================================
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String member = memberId(request);
        if (member == null || member.isEmpty()) { json(response, 200, "{\"guideIds\":[],\"loggedIn\":false}"); return; }
        try {
            List<Integer> ids = dao.findGuideIds(member);
            StringBuilder sb = new StringBuilder("{\"guideIds\":[");
            for (int i=0; i<ids.size(); i++) { if (i>0) sb.append(','); sb.append(ids.get(i)); }
            sb.append("],\"loggedIn\":true}");
            json(response, 200, sb.toString());
        } catch (SQLException e) { throw new ServletException("가이드 좋아요 조회 실패", e); }
    }
    @Override
    // =========================================================
    // 주제: doPost 처리
    // 설명: POST 요청을 받아 입력값을 검증하고 저장/처리 결과를 응답
    // =========================================================
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String member = memberId(request);
        if (member == null || member.isEmpty()) { json(response, 401, "{\"error\":\"login_required\"}"); return; }
        // 다른 출처의 폼 요청을 거부합니다 (운영 환경에서는 CSRF 토큰도 적용 권장).
        String origin = request.getHeader("Origin");
        if (origin != null && !origin.equals(request.getScheme()+"://"+request.getServerName()+((request.getServerPort()==80||request.getServerPort()==443)?"":":"+request.getServerPort()))) {
            json(response, 403, "{\"error\":\"invalid_origin\"}"); return;
        }
        int guideId;
        try { guideId = Integer.parseInt(request.getParameter("guideId")); }
        catch (NumberFormatException e) { json(response, 400, "{\"error\":\"invalid_guide_id\"}"); return; }
        if (guideId <= 0) { json(response, 400, "{\"error\":\"invalid_guide_id\"}"); return; }
        String action = request.getParameter("action");
        if (!"add".equals(action) && !"remove".equals(action)) { json(response, 400, "{\"error\":\"invalid_action\"}"); return; }
        try {
            if ("add".equals(action)) dao.add(member, guideId);
            else dao.remove(member, guideId);
            json(response, 200, "{\"liked\":" + dao.exists(member, guideId) + "}");
        } catch (SQLException e) { throw new ServletException("가이드 좋아요 처리 실패", e); }
    }
}
