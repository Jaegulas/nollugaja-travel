package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// =========================================================
// 주제: 파일 역할
// 설명: EmailVerifyController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


@WebServlet("/EmailVerify")
public class EmailVerifyController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	// 인증번호 유효시간
	private static final long CODE_EXPIRE_TIME = 5 * 60 * 1000;

	// 인증번호 최대 실패 횟수
	private static final int MAX_VERIFY_FAIL_COUNT = 5;

	// 인증 실패 5회 시 제한 시간 (5분)
	private static final long VERIFY_LIMIT_TIME = 5 * 60 * 1000L;

	@Override
	// =========================================================
	// 주제: doPost 처리
	// 설명: POST 요청을 받아 입력값을 검증하고 저장/처리 결과를 응답
	// =========================================================
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		response.setContentType("application/json; charset=UTF-8");

		// =====================================================
		// 사용자가 입력한 값
		// =====================================================

		String email = request.getParameter("email");

		String inputCode = request.getParameter("code");

		if (email != null) {
			email = email.trim();
		}

		if (inputCode != null) {
			inputCode = inputCode.trim();
		}

		if (email == null || email.isEmpty() || inputCode == null || inputCode.isEmpty()) {

			response.getWriter().write("{\"success\":false," + "\"message\":\"이메일과 인증번호를 입력해주세요.\"}");

			return;
		}

		// =====================================================
		// Session 가져오기
		// =====================================================

		HttpSession session = request.getSession(false);

		if (session == null) {

			response.getWriter().write("{\"success\":false," + "\"message\":\"먼저 인증번호를 발송해주세요.\"}");

			return;
		}

		// =====================================================
		// 인증번호 입력 실패 횟수 확인
		// =====================================================

		Integer failCount = (Integer) session.getAttribute("emailVerifyFailCount");

		Long blockedTime = (Long) session.getAttribute("emailVerifyBlockedTime");

		long now = System.currentTimeMillis();

		if (failCount == null) {
			failCount = 0;
		}

		// 인증 실패 5회 이상이면
		if (failCount >= MAX_VERIFY_FAIL_COUNT) {

			// 제한 시간이 남아 있는 경우
			if (blockedTime != null && now - blockedTime < VERIFY_LIMIT_TIME) {

				long remainingSeconds = (VERIFY_LIMIT_TIME - (now - blockedTime) + 999) / 1000;

				response.setStatus(429);

				response.getWriter().write("{\"success\":false," + "\"message\":\"인증번호 입력 횟수를 초과했습니다. "
						+ remainingSeconds + "초 후 다시 시도해주세요.\"}");

				return;
			}

			// 제한 시간 경과 후 초기화
			session.removeAttribute("emailVerifyFailCount");
			session.removeAttribute("emailVerifyBlockedTime");

			failCount = 0;
		}

		String savedCode = (String) session.getAttribute("emailCode");

		String savedEmail = (String) session.getAttribute("emailCodeEmail");

		Long createdTime = (Long) session.getAttribute("emailCodeCreatedTime");

		// =====================================================
		// 인증번호 존재 여부
		// =====================================================

		if (savedCode == null || savedEmail == null || createdTime == null) {

			response.getWriter().write("{\"success\":false," + "\"message\":\"인증번호를 다시 발송해주세요.\"}");

			return;
		}

		// =====================================================
		// 이메일 주소가 변경되었는지 확인
		// =====================================================

		if (!savedEmail.equalsIgnoreCase(email)) {

			response.getWriter().write("{\"success\":false," + "\"message\":\"인증번호를 받은 이메일과 현재 이메일이 다릅니다.\"}");

			return;
		}

		// =====================================================
		// 5분 만료 확인
		// =====================================================

		long currentTime = System.currentTimeMillis();

		if (currentTime - createdTime > CODE_EXPIRE_TIME) {

			session.removeAttribute("emailCode");
			session.removeAttribute("emailCodeEmail");
			session.removeAttribute("emailCodeCreatedTime");

			response.getWriter().write("{\"success\":false," + "\"message\":\"인증번호가 만료되었습니다. 다시 발송해주세요.\"}");

			return;
		}

		// =====================================================
		// 인증번호 비교
		// =====================================================

		if (!savedCode.equals(inputCode)) {

			// 인증 실패 횟수 증가
			failCount++;

			session.setAttribute("emailVerifyFailCount", failCount);

			// 5회 실패하면 5분 제한 시작
			if (failCount >= MAX_VERIFY_FAIL_COUNT) {

				session.setAttribute("emailVerifyBlockedTime", System.currentTimeMillis());

				response.setStatus(429);

				response.getWriter()
						.write("{\"success\":false," + "\"message\":\"인증번호를 5회 잘못 입력했습니다. " + "5분 후 다시 시도해주세요.\"}");

				return;
			}

			// 남은 인증 시도 횟수
			int remainingCount = MAX_VERIFY_FAIL_COUNT - failCount;

			response.getWriter().write(
					"{\"success\":false," + "\"message\":\"인증번호가 일치하지 않습니다. " + "남은 시도 횟수: " + remainingCount + "회\"}");

			return;
		}

		// 인증 성공 시 실패 횟수와 제한 시간 초기화
		session.removeAttribute("emailVerifyFailCount");
		session.removeAttribute("emailVerifyBlockedTime");
		
		// =====================================================
		// 인증 성공
		// =====================================================

		session.setAttribute("emailVerified", true);

		session.setAttribute("verifiedEmail", email);

		/*
		 * 사용이 끝난 인증번호 삭제
		 */
		session.removeAttribute("emailCode");

		session.removeAttribute("emailCodeCreatedTime");

		response.getWriter().write("{\"success\":true," + "\"message\":\"이메일 인증이 완료되었습니다.\"}");

		System.out.println("[EmailVerifyController] 이메일 인증 성공");
	}
}
