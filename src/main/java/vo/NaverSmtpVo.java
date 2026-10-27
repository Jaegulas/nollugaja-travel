package vo;

import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.io.IOException;
import java.io.InputStream;

// =========================================================
// 주제: 파일 역할
// 설명: NaverSmtpVo 관련 값을 보관하고 전달하는 VO
// =========================================================


public class NaverSmtpVo {

	private final Properties serverInfo;
	private final Authenticator auth;

	private final String naverEmail;
	private final String naverPassword;

	public NaverSmtpVo() {

		// =========================================
		// Eclipse Tomcat 환경변수에서 가져오기
		// =========================================

		Properties config = new Properties();

		try (InputStream input = NaverSmtpVo.class.getClassLoader().getResourceAsStream("config.properties")) {

			if (input == null) {
				throw new IllegalStateException("config.properties 파일을 찾을 수 없습니다.");
			}

			config.load(input);

		} catch (IOException e) {
			throw new IllegalStateException("config.properties 파일을 읽는 중 오류가 발생했습니다.", e);
		}

		naverEmail = config.getProperty("NAVER_EMAIL");
		naverPassword = config.getProperty("NAVER_APP_PASSWORD");

		System.out.println("========== NAVER SMTP 환경변수 확인 ==========");

		System.out.println("NAVER_EMAIL 설정 여부 : " + (naverEmail != null && !naverEmail.trim().isEmpty()));

		System.out.println("NAVER_APP_PASSWORD 설정 여부 : " + (naverPassword != null && !naverPassword.trim().isEmpty()));

		System.out.println("============================================");

		// 환경변수가 없는 경우 서버 시작 후
		// 메일 전송 시 바로 원인을 알 수 있도록 처리
		if (naverEmail == null || naverEmail.trim().isEmpty()) {
			throw new IllegalStateException("NAVER_EMAIL 환경변수가 설정되지 않았습니다.");
		}

		if (naverPassword == null || naverPassword.trim().isEmpty()) {
			throw new IllegalStateException("NAVER_APP_PASSWORD 환경변수가 설정되지 않았습니다.");
		}

		// =========================================
		// 네이버 SMTP 설정
		// =========================================
		serverInfo = new Properties();

		serverInfo.put("mail.smtp.host", "smtp.naver.com");
		serverInfo.put("mail.smtp.port", "465");

		serverInfo.put("mail.smtp.ssl.enable", "true");
		serverInfo.put("mail.smtp.ssl.protocols", "TLSv1.2");
		serverInfo.put("mail.smtp.ssl.trust", "smtp.naver.com");

		serverInfo.put("mail.smtp.auth", "true");

		serverInfo.put("mail.smtp.socketFactory.port", "465");

		serverInfo.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

		serverInfo.put("mail.smtp.socketFactory.fallback", "false");

		// =========================================
		// 네이버 로그인
		// =========================================
		auth = new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {

				return new PasswordAuthentication(naverEmail, naverPassword);
			}
		};
	}

	// =========================================
	// 이메일 발송
	// =========================================
	public void emailSending(Map<String, String> mailInfo) throws MessagingException {

		Session session = Session.getInstance(serverInfo, auth);

		// 개발 중에는 true
		// 나중에 완성 후 false로 변경 가능
		session.setDebug(true);

		MimeMessage message = new MimeMessage(session);

		// 보내는 사람은 반드시
		// 로그인한 네이버 계정으로 사용
		message.setFrom(new InternetAddress(naverEmail));

		// 받는 사람
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(mailInfo.get("to")));

		// 제목
		message.setSubject(mailInfo.get("subject"), "UTF-8");

		// 내용
		message.setContent(mailInfo.get("content"), mailInfo.get("format"));

		System.out.println("[NaverSMTP] 네이버 SMTP 이메일 발송 시작");

		Transport.send(message);

		System.out.println("[NaverSMTP] 네이버 SMTP 이메일 발송 성공");
	}
}
