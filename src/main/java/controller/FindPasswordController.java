
package controller;

import java.io.IOException;
import java.security.SecureRandom;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.AccountRecoveryDao;
import service.EmailService;

import service.EmailRateLimiter;

// =========================================================
// 주제: 파일 역할
// 설명: FindPasswordController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


@WebServlet("/FindPassword")
public class FindPasswordController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final long EXPIRE_MS = 5 * 60 * 1000L;
	private static final SecureRandom RANDOM = new SecureRandom();

	private final AccountRecoveryDao dao = new AccountRecoveryDao();
	private final EmailService emailService = new EmailService();

	// ==========================================
	// 1. 비밀번호 찾기 페이지
	// ==========================================
	@Override
	// =========================================================
	// 주제: doGet 처리
	// 설명: GET 요청을 받아 필요한 데이터를 준비하고 화면 또는 응답으로 전달
	// =========================================================
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.getRequestDispatcher("/WEB-INF/members/findPassword.jsp").forward(request, response);
	}

	// ==========================================
	// 2. 요청 분기
	// ==========================================
	@Override
	// =========================================================
	// 주제: doPost 처리
	// 설명: POST 요청을 받아 입력값을 검증하고 저장/처리 결과를 응답
	// =========================================================
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-store");

		String action = request.getParameter("action");

		try {

			if ("send".equals(action)) {
				sendCode(request, response);

			} else if ("verify".equals(action)) {
				verifyCode(request, response);

			} else if ("reset".equals(action)) {
				resetPassword(request, response);

			} else {
				reply(response, 400, false, "잘못된 요청입니다.");
			}

		} catch (MessagingException | IllegalStateException e) {

			log("[FindPassword] 처리 오류", e);

			reply(response, 500, false, "요청 처리 중 오류가 발생했습니다.");
		}
	}

	// =========================================================
	// 주제: sendCode 처리
	// 설명: 필요한 데이터를 외부 또는 사용자에게 전송
	// =========================================================
	private void sendCode(HttpServletRequest request, HttpServletResponse response)
			throws IOException, MessagingException {

		String id = trim(request.getParameter("id"));
		String email = trim(request.getParameter("email"));

		if (id.isEmpty() || email.isEmpty()) {

			reply(response, 400, false, "아이디와 이메일을 입력해주세요.");
			return;
		}

		HttpSession session = request.getSession();

		

		// 아이디 존재 여부 확인
		if (!dao.existsLocalId(id)) {

			System.out.println("[FindPassword] 존재하지 않는 아이디");

			reply(response, 400, false, "존재하지 않는 아이디입니다.");
			return;
		}

		// 이메일 일치 여부 확인
		if (!dao.existsLocalMember(id, email)) {

			System.out.println("[FindPassword] 이메일 불일치");

			reply(response, 400, false, "가입된 이메일과 일치하지 않습니다.");
			return;
		}

		// 이메일 인증번호 발송 횟수 제한
		if (!EmailRateLimiter.tryAcquire(email)) {
		    reply(response, 429, false,
		        "인증번호 발송 횟수를 초과했습니다. 5분 후 다시 시도해주세요.");
		    return;
		}
		
		// 새로운 인증 요청이므로 기존 인증정보 초기화
		clearRecovery(session);

		String code = String.format("%06d", RANDOM.nextInt(1000000));

		System.out.println("[FindPassword] 이메일 발송 시작");

		// 메일 발송이 성공한 경우에만 인증정보 저장
		emailService.sendVerificationEmail(email, code);

		long sentTime = System.currentTimeMillis();

		session.setAttribute("pwResetId", id);
		session.setAttribute("pwResetEmail", email);
		session.setAttribute("pwResetCode", code);
		session.setAttribute("pwResetTime", sentTime);
		session.setAttribute("pwResetAttempts", 0);
		session.setAttribute("pwResetVerified", false);

		System.out.println("[FindPassword] 이메일 발송 완료");

		reply(response, 200, true, "인증번호가 이메일로 발송되었습니다.");
	}

	// =========================================================
	// 주제: verifyCode 처리
	// 설명: 입력값 또는 현재 상태가 조건에 맞는지 검사
	// =========================================================
	private void verifyCode(HttpServletRequest request, HttpServletResponse response) throws IOException {

		HttpSession session = request.getSession(false);

		if (session == null) {

			reply(response, 400, false, "인증번호를 다시 발송해주세요.");
			return;
		}

		String code = trim(request.getParameter("code"));

		String savedCode = (String) session.getAttribute("pwResetCode");

		Long created = (Long) session.getAttribute("pwResetTime");

		Integer attempts = (Integer) session.getAttribute("pwResetAttempts");

		if (savedCode == null || created == null) {

			reply(response, 400, false, "인증번호를 다시 발송해주세요.");
			return;
		}

		// 5분 만료
		if (System.currentTimeMillis() - created > EXPIRE_MS) {

			clearRecovery(session);

			reply(response, 400, false, "인증시간이 만료되었습니다.");
			return;
		}

		int count = attempts == null ? 0 : attempts;

		if (count >= 5) {

			clearRecovery(session);

			reply(response, 400, false, "인증 횟수를 초과했습니다.");
			return;
		}

		// 인증번호 불일치
		if (!savedCode.equals(code)) {

			int nextCount = count + 1;

			if (nextCount >= 5) {

				clearRecovery(session);

				reply(response, 400, false, "인증 횟수를 초과했습니다. 인증번호를 다시 발송해주세요.");
				return;
			}

			session.setAttribute("pwResetAttempts", nextCount);

			reply(response, 400, false, "인증번호가 일치하지 않습니다. 남은 횟수: " + (5 - nextCount));
			return;
		}

		// 인증 성공: 인증번호 재사용 방지
		session.removeAttribute("pwResetCode");
		session.removeAttribute("pwResetAttempts");

		session.setAttribute("pwResetVerified", true);

		reply(response, 200, true, "이메일 인증이 완료되었습니다.");
	}

	// =========================================================
	// 주제: resetPassword 처리
	// 설명: resetPassword 기능에 필요한 처리 로직을 수행
	// =========================================================
	private void resetPassword(HttpServletRequest request, HttpServletResponse response) throws IOException {

		HttpSession session = request.getSession(false);

		if (session == null || !Boolean.TRUE.equals(session.getAttribute("pwResetVerified"))) {

			reply(response, 403, false, "이메일 인증이 필요합니다.");
			return;
		}

		String id = (String) session.getAttribute("pwResetId");

		String email = (String) session.getAttribute("pwResetEmail");

		Long created = (Long) session.getAttribute("pwResetTime");

		if (id == null || email == null || created == null || System.currentTimeMillis() - created > EXPIRE_MS) {

			clearRecovery(session);

			reply(response, 403, false, "인증시간이 만료되었습니다.");
			return;
		}

		String password = request.getParameter("password");

		String confirm = request.getParameter("confirmPassword");

		if (password == null || password.length() < 8 || password.length() > 72
				|| password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72) {

			reply(response, 400, false, "비밀번호는 8자 이상, UTF-8 기준 72바이트 이하로 입력해주세요.");
			return;
		}

		if (!password.equals(confirm)) {

			reply(response, 400, false, "비밀번호 확인이 일치하지 않습니다.");
			return;
		}

		boolean updated = dao.resetPassword(id, email, password);

		clearRecovery(session);

		if (updated) {

			reply(response, 200, true, "비밀번호가 변경되었습니다. 다시 로그인해주세요.");

		} else {

			reply(response, 400, false, "비밀번호 변경에 실패했습니다.");
		}
	}

	// =========================================================
	// 주제: clearRecovery 처리
	// 설명: clearRecovery 기능에 필요한 처리 로직을 수행
	// =========================================================
	private void clearRecovery(HttpSession session) {

		session.removeAttribute("pwResetId");
		session.removeAttribute("pwResetEmail");
		session.removeAttribute("pwResetCode");
		session.removeAttribute("pwResetTime");
		session.removeAttribute("pwResetAttempts");
		session.removeAttribute("pwResetVerified");
	}

	// =========================================================
	// 주제: trim 처리
	// 설명: trim 기능에 필요한 처리 로직을 수행
	// =========================================================
	private String trim(String value) {

		return value == null ? "" : value.trim();
	}

	// =========================================================
	// 주제: reply 처리
	// 설명: reply 기능에 필요한 처리 로직을 수행
	// =========================================================
	private void reply(HttpServletResponse response, int status, boolean success, String message) throws IOException {

		response.setStatus(status);

		String escapedMessage = message.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r",
				"\\r");

		response.getWriter().write("{\"success\":" + success + ",\"message\":\"" + escapedMessage + "\"}");
	}
}
