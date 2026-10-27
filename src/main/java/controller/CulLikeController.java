package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.CulLikeDao;

// =========================================================
// 주제: 파일 역할
// 설명: CulLikeController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


@WebServlet("/CulLike")
public class CulLikeController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	// =========================================================
	// 주제: doPost 처리
	// 설명: POST 요청을 받아 입력값을 검증하고 저장/처리 결과를 응답
	// =========================================================
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=UTF-8");

		// ==========================================
		// 1. 로그인 세션 확인
		// ==========================================
		HttpSession session = request.getSession(false);

		if (session == null || session.getAttribute("loginId") == null) {

			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

			response.getWriter().write("{\"message\":\"로그인이 필요합니다.\"}");

			return;
		}

		// ==========================================
		// 2. 로그인한 회원 ID 가져오기
		// ==========================================
		String memberId = (String) session.getAttribute("loginId");

		System.out.println("[CulLikeController] 로그인 회원 ID : " + memberId);

		// ==========================================
		// 3. 문화유산 번호 가져오기
		// ==========================================
		String heritageIdParam = request.getParameter("heritageId");

		int heritageId;

		try {

			heritageId = Integer.parseInt(heritageIdParam);

		} catch (NumberFormatException e) {

			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

			response.getWriter().write("{\"message\":\"잘못된 문화유산 번호입니다.\"}");

			return;
		}

		System.out.println("[CulLikeController] 문화유산 ID : " + heritageId);

		// ==========================================
		// 4. 좋아요 처리
		// ==========================================
		CulLikeDao dao = new CulLikeDao();

		try {

			boolean liked = dao.toggleLike(memberId, heritageId);

			// ==========================================
			// 5. 처리 결과 반환
			// ==========================================
			System.out.println("[CulLikeController] 좋아요 상태 : " + liked);

			response.getWriter().write("{\"liked\":" + liked + "}");

		} catch (Exception e) {

			System.err.println("[CulLikeController] 좋아요 DB 처리 오류");

			e.printStackTrace();

			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

			response.getWriter().write("{\"message\":\"DB 처리 오류\"}");
		}
	}
}
