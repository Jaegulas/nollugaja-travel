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

import service.EmailService;

// =========================================================
// 주제: 파일 역할
// 설명: EmailSendController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


@WebServlet("/EmailSend")
public class EmailSendController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final SecureRandom RANDOM = new SecureRandom();
	
	// 이메일 발송 제한 설정
	private static final int MAX_SEND_COUNT = 5;

	// 5분 (밀리초)
	private static final long SEND_LIMIT_TIME = 5 * 60 * 1000L;

	@Override
	// =========================================================
	// 주제: doPost 처리
	// 설명: POST 요청을 받아 입력값을 검증하고 저장/처리 결과를 응답
	// =========================================================
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		response.setContentType("application/json; charset=UTF-8");

		// =========================================
		// 이메일 받기
		// =========================================
		String email = request.getParameter("email");

		if (email != null) {
			email = email.trim();
		}

		// =========================================
		// 이메일 입력 검사
		// =========================================
		if (email == null || email.isEmpty()) {

			response.getWriter().write("{\"success\":false," + "\"message\":\"이메일을 입력해주세요.\"}");

			return;
		}

		// =========================================
		// 이메일 형식 검사
		// =========================================
		if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {

			response.getWriter().write("{\"success\":false," + "\"message\":\"올바른 이메일 형식이 아닙니다.\"}");

			return;
		}

		// =========================================
		// 이메일 인증번호 재발송 횟수 제한
		// =========================================

		HttpSession session = request.getSession();

		long now = System.currentTimeMillis();

		// 현재 이메일에 대한 발송 횟수
		String countKey = "emailSendCount:" + email;

		// 현재 이메일에 대한 제한 시작 시간
		String timeKey = "emailSendLimitTime:" + email;

		Integer sendCount = (Integer) session.getAttribute(countKey);
		Long limitTime = (Long) session.getAttribute(timeKey);

		if (sendCount == null) {
		    sendCount = 0;
		}

		// 5회 이상 발송한 상태라면
		if (sendCount >= MAX_SEND_COUNT) {

		    // 제한 시간이 아직 남았다면 발송 차단
		    if (limitTime != null && now - limitTime < SEND_LIMIT_TIME) {

		        long remainingSeconds =
		                (SEND_LIMIT_TIME - (now - limitTime) + 999) / 1000;

		        long minutes = remainingSeconds / 60;
		        long seconds = remainingSeconds % 60;

		        response.setStatus(429);

		        response.getWriter().write(
		            "{\"success\":false,"
		            + "\"message\":\"인증번호 발송은 5회까지 가능합니다. "
		            + minutes + "분 " + seconds + "초 후 다시 시도해주세요.\"}"
		        );

		        return;
		    }

		    // 5분이 지났다면 발송 횟수 초기화
		    sendCount = 0;

		    session.setAttribute(countKey, 0);
		    session.removeAttribute(timeKey);
		}
		
		// =========================================
		// 6자리 인증번호 생성
		// =========================================
		String verificationCode = String.format("%06d", RANDOM.nextInt(1000000));

		System.out.println("[EmailSendController] 인증번호 생성 완료");

		try {

			// =========================================
			// 이메일 발송
			// =========================================
			EmailService emailService = new EmailService();

			emailService.sendVerificationEmail(email, verificationCode);

			// =========================================
			// 이메일 발송 성공 횟수 기록
			// =========================================

			sendCount++;

			session.setAttribute(countKey, sendCount);

			// 5번째 발송에 성공하면 5분 제한 시작
			if (sendCount >= MAX_SEND_COUNT) {
			    session.setAttribute(timeKey, System.currentTimeMillis());
			}

			System.out.println(
			    "[EmailSendController] 이메일 발송 횟수 : " + sendCount
			);
			
			// =========================================
			// 발송 성공 후 세션 저장
			// =========================================
			session.setAttribute("emailCode", verificationCode);

			session.setAttribute("emailCodeEmail", email);

			session.setAttribute("emailCodeCreatedTime", System.currentTimeMillis());

			session.setAttribute("emailVerified", false);

			session.removeAttribute("verifiedEmail");

			// =========================================
			// 성공 응답
			// =========================================
			response.getWriter().write("{\"success\":true," + "\"message\":\"인증번호를 이메일로 발송했습니다.\"}");

		} catch (IllegalStateException e) {

			e.printStackTrace();

			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

			response.getWriter().write("{\"success\":false," + "\"message\":\"네이버 이메일 발송 설정을 확인해주세요.\"}");

		} catch (MessagingException e) {

			e.printStackTrace();

			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

			response.getWriter().write("{\"success\":false," + "\"message\":\"인증 이메일 발송에 실패했습니다.\"}");
		}
	}
}
