package service;

import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;

import vo.NaverSmtpVo;

// =========================================================
// 주제: 파일 역할
// 설명: EmailService의 핵심 비즈니스 로직과 데이터 가공을 담당하는 서비스
// =========================================================


public class EmailService {

	public void sendVerificationEmail(String email, String verificationCode) throws MessagingException {

		// =========================================
		// 인증번호 이메일 HTML
		// =========================================
		String html = "<div style='" + "font-family:Arial,sans-serif;" + "max-width:500px;" + "margin:auto;"
				+ "padding:30px;" + "'>"

				+ "<h2 style='margin-bottom:20px;'>" + "놀러가자 이메일 인증" + "</h2>"

				+ "<p>" + "회원가입을 위한 이메일 인증번호입니다." + "</p>"

				+ "<div style='" + "font-size:32px;" + "font-weight:bold;" + "letter-spacing:8px;" + "padding:20px;"
				+ "margin:20px 0;" + "background:#f5f5f5;" + "text-align:center;" + "border-radius:10px;" + "'>"

				+ verificationCode

				+ "</div>"

				+ "<p style='" + "color:#777;" + "font-size:13px;" + "'>" + "인증번호는 5분 동안 유효합니다." + "</p>"

				+ "</div>";

		// =========================================
		// NaverSMTP에 전달할 이메일 정보
		// =========================================
		Map<String, String> emailInfo = new HashMap<String, String>();

		// 받는 사람
		emailInfo.put("to", email);

		// 제목
		emailInfo.put("subject", "[놀러가자] 회원가입 이메일 인증번호");

		// 이메일 본문
		emailInfo.put("content", html);

		// HTML 형식
		emailInfo.put("format", "text/html;charset=UTF-8");

		System.out.println("[EmailService] 네이버 SMTP 이메일 발송 요청");

		// =========================================
		// 실제 이메일 발송
		// =========================================
		NaverSmtpVo naverSMTP = new NaverSmtpVo();

		naverSMTP.emailSending(emailInfo);

		System.out.println("[EmailService] 이메일 발송 완료");
	}
}
