package controller;

import java.io.IOException;
import java.security.SecureRandom;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// =========================================================
// 주제: 파일 역할
// 설명: SmsController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


@WebServlet("/Sms")
public class SmsController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	// 인증번호 유효시간 : 5분
	private static final long SMS_EXPIRE_TIME = 5 * 60 * 1000;

	// 안전한 랜덤번호 생성을 위한 객체
	private final SecureRandom random = new SecureRandom();

	@Override
	// =========================================================
	// 주제: doPost 처리
	// 설명: POST 요청을 받아 입력값을 검증하고 저장/처리 결과를 응답
	// =========================================================
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		String action = request.getParameter("action");

		System.out.println("==============================");
		System.out.println("[SmsController] 요청");
		System.out.println("action : " + action);
		System.out.println("==============================");

		// ========================================
		// 1. 인증번호 발송
		// ========================================
		if ("send".equals(action)) {

			sendSmsCode(request, response);
			return;
		}

		// ========================================
		// 2. 인증번호 확인
		// ========================================
		if ("verify".equals(action)) {

			verifySmsCode(request, response);
			return;
		}

		// 잘못된 요청
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

		response.getWriter().write("{\"success\":false,\"message\":\"잘못된 요청입니다.\"}");
	}

	// =========================================================
	// 주제: sendSmsCode 처리
	// 설명: 필요한 데이터를 외부 또는 사용자에게 전송
	// =========================================================
	private void sendSmsCode(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String hp = request.getParameter("hp");

		System.out.println("[SMS 인증번호 발송 요청]");
		System.out.println("휴대폰 번호 : " + hp);

		// 휴대폰 번호가 없는 경우
		if (hp == null || hp.trim().isEmpty()) {

			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

			response.getWriter().write("{\"success\":false,\"message\":\"휴대폰 번호를 입력해주세요.\"}");

			return;
		}

		// -------------------------------------------------
		// 하이픈 제거
		// 010-1234-5678
		// ↓
		// 01012345678
		// -------------------------------------------------

		String cleanHp = hp.replaceAll("[^0-9]", "");

		// -------------------------------------------------
		// 휴대폰 번호 형식 검사
		// -------------------------------------------------

		if (!cleanHp.matches("^01[016789][0-9]{7,8}$")) {

			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

			response.getWriter().write("{\"success\":false,\"message\":\"올바른 휴대폰 번호를 입력해주세요.\"}");

			return;
		}

		// -------------------------------------------------
		// 6자리 인증번호 생성
		// 100000 ~ 999999
		// -------------------------------------------------

		int number = random.nextInt(900000) + 100000;

		String smsCode = String.valueOf(number);

		// -------------------------------------------------
		// 세션에 인증번호 저장
		// -------------------------------------------------

		HttpSession session = request.getSession(true);

		session.setAttribute("smsCode", smsCode);

		// 인증번호를 발송한 휴대폰 번호
		session.setAttribute("smsPhone", cleanHp);

		// 인증번호 생성 시간
		session.setAttribute("smsCodeCreatedTime", System.currentTimeMillis());

		// 새로운 인증번호를 발급했으므로 인증상태 초기화
		session.setAttribute("phoneVerified", false);

		// =================================================
		// ★ 현재는 테스트 단계
		//
		// 나중에 SOLAPI 같은 실제 SMS API를 사용할 경우
		// 아래 부분에서 API를 호출하면 됩니다.
		// =================================================

		System.out.println();
		System.out.println("======================================");
		System.out.println("       [SMS 테스트 인증번호]");
		System.out.println("휴대폰 : " + cleanHp);
		System.out.println("인증번호 : " + smsCode);
		System.out.println("유효시간 : 5분");
		System.out.println("======================================");
		System.out.println();

		/*
		 * 나중에 실제 SMS API 연결
		 *
		 * 예:
		 *
		 * SmsService smsService = new SmsService();
		 *
		 * smsService.send( cleanHp, "[놀러가자] 인증번호는 " + smsCode + " 입니다." );
		 *
		 */

		// 브라우저에게 성공 결과 전달
		response.getWriter().write("{\"success\":true," + "\"message\":\"인증번호가 발송되었습니다.\"}");
	}

	// =========================================================
	// 주제: verifySmsCode 처리
	// 설명: 입력값 또는 현재 상태가 조건에 맞는지 검사
	// =========================================================
	private void verifySmsCode(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String inputCode = request.getParameter("code");

		String hp = request.getParameter("hp");

		System.out.println("[SMS 인증번호 확인 요청]");
		System.out.println("입력 인증번호 : " + inputCode);

		if (inputCode == null || inputCode.trim().isEmpty()) {

			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

			response.getWriter().write("{\"success\":false,\"message\":\"인증번호를 입력해주세요.\"}");

			return;
		}

		HttpSession session = request.getSession(false);

		// 세션 자체가 없는 경우
		if (session == null) {

			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

			response.getWriter().write("{\"success\":false," + "\"message\":\"인증번호를 먼저 발송해주세요.\"}");

			return;
		}

		String savedCode = (String) session.getAttribute("smsCode");

		String savedPhone = (String) session.getAttribute("smsPhone");

		Long createdTime = (Long) session.getAttribute("smsCodeCreatedTime");

		// 저장된 인증번호가 없는 경우
		if (savedCode == null || savedPhone == null || createdTime == null) {

			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

			response.getWriter().write("{\"success\":false," + "\"message\":\"인증번호를 먼저 발송해주세요.\"}");

			return;
		}

		// -------------------------------------------------
		// 인증번호 유효시간 검사
		// -------------------------------------------------

		long currentTime = System.currentTimeMillis();

		long elapsedTime = currentTime - createdTime;

		if (elapsedTime > SMS_EXPIRE_TIME) {

			// 만료된 인증정보 제거
			clearSmsSession(session);

			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

			response.getWriter().write("{\"success\":false," + "\"message\":\"인증번호가 만료되었습니다. 다시 발송해주세요.\"}");

			return;
		}

		// -------------------------------------------------
		// 인증 요청한 휴대폰 번호가 변경됐는지 확인
		// -------------------------------------------------

		String cleanHp = "";

		if (hp != null) {
			cleanHp = hp.replaceAll("[^0-9]", "");
		}

		if (!savedPhone.equals(cleanHp)) {

			session.setAttribute("phoneVerified", false);

			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

			response.getWriter().write("{\"success\":false," + "\"message\":\"인증번호를 발송한 휴대폰 번호와 다릅니다.\"}");

			return;
		}

		// -------------------------------------------------
		// 인증번호 비교
		// -------------------------------------------------

		if (savedCode.equals(inputCode.trim())) {

			// 인증 성공
			session.setAttribute("phoneVerified", true);

			// 어떤 번호가 인증됐는지도 저장
			session.setAttribute("verifiedPhone", savedPhone);

			System.out.println("==============================");
			System.out.println("[SMS 인증 성공]");
			System.out.println("휴대폰 : " + savedPhone);
			System.out.println("==============================");

			response.getWriter().write("{\"success\":true," + "\"message\":\"휴대폰 인증이 완료되었습니다.\"}");

		} else {

			session.setAttribute("phoneVerified", false);

			System.out.println("[SMS 인증 실패]");

			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

			response.getWriter().write("{\"success\":false," + "\"message\":\"인증번호가 일치하지 않습니다.\"}");
		}
	}

	// =========================================================
	// 주제: clearSmsSession 처리
	// 설명: clearSmsSession 기능에 필요한 처리 로직을 수행
	// =========================================================
	private void clearSmsSession(HttpSession session) {

		session.removeAttribute("smsCode");

		session.removeAttribute("smsPhone");

		session.removeAttribute("smsCodeCreatedTime");

		session.removeAttribute("phoneVerified");

		session.removeAttribute("verifiedPhone");
	}
}
