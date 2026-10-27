package controller;

import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import service.GuideDetailService;

// =========================================================
// 주제: 파일 역할
// 설명: GuideDetailController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


@WebServlet("/GuideDetail")
public class GuideDetailController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final GuideDetailService service = new GuideDetailService();
    private final Gson gson = new Gson();

    @Override
    // =========================================================
    // 주제: doGet 처리
    // 설명: GET 요청을 받아 필요한 데이터를 준비하고 화면 또는 응답으로 전달
    // =========================================================
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Cache-Control", "no-store");
        try {
            int id = Integer.parseInt(req.getParameter("guideId"));
            if (id <= 0) throw new NumberFormatException();
            Map<String, Object> guide = service.getGuide(id);
            if (guide == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"해당 가이드가 없습니다.\"}");
                return;
            }
            gson.toJson(guide, resp.getWriter());
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"잘못된 가이드 번호입니다.\"}");
        } catch (Exception e) {
            getServletContext().log("GuideDetail DB 조회 오류", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"가이드 조회에 실패했습니다.\"}");
        }
    }
}
