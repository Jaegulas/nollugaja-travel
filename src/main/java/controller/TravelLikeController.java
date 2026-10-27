package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import dao.TravelLikeDao;

// =========================================================
// 주제: 파일 역할
// 설명: TravelLikeController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


@WebServlet("/TravelLike")
public class TravelLikeController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final TravelLikeDao dao = new TravelLikeDao();

    @Override
    // =========================================================
    // 주제: doGet 처리
    // 설명: GET 요청을 받아 필요한 데이터를 준비하고 화면 또는 응답으로 전달
    // =========================================================
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        HttpSession session = req.getSession(false);
        if (session == null || !(session.getAttribute("loginId") instanceof String)) {
            resp.getWriter().write("{\"loggedIn\":false,\"ids\":[]}");
            return;
        }
        try {
            Set<String> ids = dao.getLikedContentIds((String) session.getAttribute("loginId"));
            StringBuilder json = new StringBuilder("{\"loggedIn\":true,\"ids\":[");
            boolean first = true;
            for (String id : ids) {
                if (!id.matches("[0-9]{1,30}")) continue;
                if (!first) json.append(',');
                json.append('"').append(id).append('"');
                first = false;
            }
            resp.getWriter().write(json.append("]}").toString());
        } catch (SQLException e) {
            log("TravelLike GET DB error", e);
            resp.sendError(500, "좋아요 조회 실패");
        }
    }

    @Override
    // =========================================================
    // 주제: doPost 처리
    // 설명: POST 요청을 받아 입력값을 검증하고 저장/처리 결과를 응답
    // =========================================================
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");
        HttpSession session = req.getSession(false);
        if (session == null || !(session.getAttribute("loginId") instanceof String)) {
            resp.setStatus(401);
            resp.getWriter().write("{\"message\":\"로그인이 필요합니다.\"}");
            return;
        }
        String id = req.getParameter("contentId");
        String desired = req.getParameter("liked");
        if (id == null || !id.matches("[0-9]{1,30}") ||
                !("true".equals(desired) || "false".equals(desired))) {
            resp.setStatus(400);
            resp.getWriter().write("{\"message\":\"잘못된 요청입니다.\"}");
            return;
        }
        try {
            boolean liked = dao.setLiked((String)session.getAttribute("loginId"), id,
                    Boolean.parseBoolean(desired));
            resp.getWriter().write("{\"liked\":" + liked + "}");
        } catch (SQLException e) {
            log("TravelLike POST DB error", e);
            resp.sendError(500, "좋아요 저장 실패");
        }
    }
}
