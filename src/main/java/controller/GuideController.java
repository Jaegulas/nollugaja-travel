package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import service.GuideService;

// =========================================================
// 주제: 파일 역할
// 설명: GuideController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


@WebServlet("/GuideList")
public class GuideController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final GuideService service = new GuideService();
    private static final int PAGE_SIZE = 10;
    private static final List<String> REGIONS = Arrays.asList("전체", "서울", "부산", "제주", "경주", "강릉", "전주");

    @Override
    // =========================================================
    // 주제: doGet 처리
    // 설명: GET 요청을 받아 필요한 데이터를 준비하고 화면 또는 응답으로 전달
    // =========================================================
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    
            throws ServletException, IOException {
    	System.out.println("====== GuideList Controller 실행 성공 ======");
        request.setCharacterEncoding("UTF-8");
        String region = request.getParameter("region");
        if (region == null || !REGIONS.contains(region)) region = "전체";
        String keyword = request.getParameter("keyword");
        if (keyword == null) keyword = "";
        keyword = keyword.trim();
        if (keyword.length() > 100) keyword = keyword.substring(0, 100);
        int page = 1;
        try { page = Integer.parseInt(request.getParameter("page")); }
        catch (NumberFormatException ignored) { }
        if (page < 1) page = 1;
        String dbRegion = "전체".equals(region) ? null : region;
        try {
            int totalCount = service.count(dbRegion, keyword);
            int totalPages = Math.max(1, (totalCount + PAGE_SIZE - 1) / PAGE_SIZE);
            page = Math.min(page, totalPages);
            request.setAttribute("guideList", service.findPage(dbRegion, keyword, page, PAGE_SIZE));
            request.setAttribute("totalCount", totalCount);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("currentPage", page);
            request.setAttribute("regions", REGIONS);
            request.setAttribute("selectedRegion", region);
            request.setAttribute("keyword", keyword);
            request.setAttribute("center", "guideList/guideList.jsp");
            request.getRequestDispatcher("/WEB-INF/main.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("가이드 목록 DB 조회 실패", e);
        }
    }
}
