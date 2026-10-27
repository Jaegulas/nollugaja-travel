package filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// =========================================================
// 주제: 파일 역할
// 설명: LoginCheckFilter 요청 전후 공통 검사와 접근 제어를 담당하는 필터
// =========================================================




@WebFilter("/Mypage/*")
public class LoginCheckFilter implements Filter{

	

	@Override
	// =========================================================
	// 주제: destroy 처리
	// 설명: destroy 기능에 필요한 처리 로직을 수행
	// =========================================================
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
	    HttpServletResponse res = (HttpServletResponse) response;

	    HttpSession session = req.getSession();

	    String path = req.getRequestURI().substring(req.getContextPath().length());

	     // ★ 로그인 없이 열려야 하는 주소는 바로 통과
        if (path.equals("/Mypage/findItineraryJsonFromSharekey")
                || path.startsWith("/resources/")) {    // CSS, JS, 이미지도 통과
            chain.doFilter(request, response);
            return;
        }

        
	    if (session.getAttribute("loginId") == null) {
	    	System.out.println("[AUTH] 비로그인 접근 차단: " + path);
	    	
	    	String url = req.getRequestURI();            // 예: /travel/Mypage/main
	    	if (req.getQueryString() != null) {
	    	    url += "?" + req.getQueryString();       // 예: /travel/Mypage/main?tab=like
	    	}
	    	
	    	// 화면 이동 요청인지 확인 (아니면 fetch 요청)
	        // Sec-Fetch-Mode : https·localhost에서만 옴 / 없으면 Accept 헤더로 판단
	    	String fetchMode = req.getHeader("Sec-Fetch-Mode");
	    	String accept = req.getHeader("Accept");
	    	boolean isPageRequest = "navigate".equals(fetchMode)
	    	        || (fetchMode == null && accept != null && accept.contains("text/html"));

	    	// fetch 요청이면 redirect 대신 401 + JSON
	    	if (!isPageRequest) {
	    	    res.setStatus(401);
	    	    res.setContentType("application/json; charset=UTF-8");
	    	    res.getWriter().write("{\"success\": false, \"message\": \"로그인이 필요합니다.\"}");
	    	    return;
	    	}

	    	// 화면 요청이면 지금처럼 원래 주소 저장 후 로그인 페이지로
	    	session.setAttribute("redirectUrl", url);
	    	res.sendRedirect(req.getContextPath() + "/Login");
	    	return;
	    }
	    	// 로그인한 사람은 원래 가려던 서블릿/JSP로 통과
    	chain.doFilter(request, response);
		
	}

	@Override
	// =========================================================
	// 주제: init 처리
	// 설명: init 기능에 필요한 처리 로직을 수행
	// =========================================================
	public void init(FilterConfig arg0) throws ServletException {
		
	}
	
	
}
