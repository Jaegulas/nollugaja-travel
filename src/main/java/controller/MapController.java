package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// =========================================================
// 주제: 파일 역할
// 설명: MapController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================



@WebServlet("/Map/*")
public class MapController extends HttpServlet{
	
	

	// =========================================================
	// 주제: doHandle 처리
	// 설명: doHandle 기능에 필요한 처리 로직을 수행
	// =========================================================
	private void doHandle(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		System.out.println("[MapController] doHandle() ");
		resp.setContentType("text/html");
		String contextPath = req.getContextPath();
		
		String action = req.getPathInfo();
		
		if(action.equals("/geocoder_marker_name")) {
			req.setAttribute("center", "map/geocoder_marker_name.jsp");
			req.getRequestDispatcher("/WEB-INF/main.jsp").forward(req, resp);
			return;
		}else if(action.equals("/zoom_marker")) {
			req.setAttribute("center", "map/zoom_marker.jsp");
			req.getRequestDispatcher("/WEB-INF/main.jsp").forward(req, resp);
			return;
		}else if(action.equals("/many_places_marker_name")) {
			req.setAttribute("center", "map/many_places_marker_name.jsp");
			req.getRequestDispatcher("/WEB-INF/main.jsp").forward(req, resp);
			return;
		}else if(action.equals("/zoom_marker_name")) {
			req.setAttribute("center", "map/zoom_marker_name.jsp");
			req.getRequestDispatcher("/WEB-INF/main.jsp").forward(req, resp);
			return;
		}else if(action.equals("/keyword_marker")) {
			req.setAttribute("center", "map/keyword_marker.jsp");
			req.getRequestDispatcher("/WEB-INF/main.jsp").forward(req, resp);
			return;
		}
		
		
		
	}
	
	
	
	@Override
	// =========================================================
	// 주제: doGet 처리
	// 설명: GET 요청을 받아 필요한 데이터를 준비하고 화면 또는 응답으로 전달
	// =========================================================
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doHandle(req, resp);
		
	}

	@Override
	// =========================================================
	// 주제: doPost 처리
	// 설명: POST 요청을 받아 입력값을 검증하고 저장/처리 결과를 응답
	// =========================================================
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doHandle(req, resp);
	}


	@Override
	// =========================================================
	// 주제: init 처리
	// 설명: init 기능에 필요한 처리 로직을 수행
	// =========================================================
	public void init() throws ServletException {
	
	}
	
	
	
	

}
