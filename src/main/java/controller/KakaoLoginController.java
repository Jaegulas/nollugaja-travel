package controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.InputStream;
import java.util.Properties;

// =========================================================
// 주제: 파일 역할
// 설명: KakaoLoginController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


/**
 * 카카오 로그인 시작 Controller
 *
 * login.jsp ↓ /KakaoLogin ↓ KakaoLoginController ↓ 카카오 로그인 화면
 */
@WebServlet("/KakaoLogin")
public class KakaoLoginController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	// =========================================================
	// 카카오 REST API 키
	// =========================================================
	// 나중에 Kakao Developers에서 발급받은
	// REST API 키로 변경해야 한다.
	private String kakaoRestApiKey;

	@Override
	// =========================================================
	// 주제: init 처리
	// 설명: init 기능에 필요한 처리 로직을 수행
	// =========================================================
	public void init() throws ServletException {

		Properties props = new Properties();

		try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {

			if (input == null) {
				throw new ServletException("config.properties 파일을 찾을 수 없습니다.");
			}

			props.load(input);

			kakaoRestApiKey = props.getProperty("kakao.login-api.key");

			if (kakaoRestApiKey == null || kakaoRestApiKey.trim().isEmpty()) {

				throw new ServletException("카카오 REST API 키가 설정되지 않았습니다.");
			}

		} catch (IOException e) {
			throw new ServletException("카카오 설정 파일 읽기 실패", e);
		}
	}

	// =========================================================
	// 카카오 로그인 Redirect URI
	// =========================================================
	// 카카오 로그인이 끝난 후
	// 카카오가 다시 우리 프로젝트로 보내줄 주소
	//
	// 주의:
	// Kakao Developers에 등록할 Redirect URI와
	// 아래 주소가 정확히 같아야 한다.
	// =========================================================

	@Override
	// =========================================================
	// 주제: doGet 처리
	// 설명: GET 요청을 받아 필요한 데이터를 준비하고 화면 또는 응답으로 전달
	// =========================================================
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		// =====================================================
		// 1. REST API 키 설정 확인
		// =====================================================

		if (kakaoRestApiKey == null || kakaoRestApiKey.trim().isEmpty()) {

			response.setContentType("text/html; charset=UTF-8");

			response.getWriter()
					.println("<script>" + "alert('카카오 REST API 키를 설정해주세요.');" + "history.back();" + "</script>");

			return;
		}

		// =====================================================
		// 2. state 값 생성
		// =====================================================
		// state는 다른 사람이 로그인 요청을 위조하는 것을
		// 방지하기 위해 사용하는 임의의 문자열이다.
		// =====================================================

		byte[] randomBytes = new byte[16];

		SecureRandom secureRandom = new SecureRandom();

		secureRandom.nextBytes(randomBytes);

		String state = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

		// =====================================================
		// 3. state 및 로그인 전 페이지 주소 저장
		// =====================================================

		HttpSession session = request.getSession();

		session.setAttribute("KAKAO_STATE", state);
		
		// ==========================================
		// 카카오 재인증 여부 확인
		// ==========================================

		// 콜백에서 저장한 재인증 대상이 있는지 확인
		boolean reAuth = session.getAttribute(
		        "PENDING_KAKAO_ID") instanceof Long;

		// 재인증이 아니라면 이전 임시 인증 상태 정리
		if (!reAuth) {
		    session.removeAttribute("KAKAO_REAUTH");
		}

		// 재인증 요청 여부 저장
		session.setAttribute("KAKAO_REAUTH", reAuth);

		// 현재 접속 주소로 카카오 Redirect URI 생성
		String redirectUri = makeRedirectUri(request);

		// 콜백에서도 동일한 주소를 사용하도록 세션에 저장
		session.setAttribute("KAKAO_REDIRECT_URI", redirectUri);

		// 로그인 전 보고 있던 페이지 주소
		String redirectUrl = request.getParameter("redirect");

		// 로그인 JSP를 거쳐 온 경우 세션에 저장된 주소도 확인
		if (redirectUrl == null || redirectUrl.trim().isEmpty()) {
			Object saved = session.getAttribute("redirectUrl");

			if (saved instanceof String) {
				redirectUrl = (String) saved;
			}
		}

		// 재인증 중에는 최초 로그인 복귀 주소 유지
		if (!reAuth) {
		    session.setAttribute("KAKAO_REDIRECT_URL", redirectUrl);
		}

		// =====================================================
		// 4. 카카오 로그인 주소 만들기
		// =====================================================

		String kakaoLoginUrl = "https://kauth.kakao.com/oauth/authorize"

        + "?client_id=" + URLEncoder.encode(
                kakaoRestApiKey, StandardCharsets.UTF_8.name())

        + "&redirect_uri=" + URLEncoder.encode(
                redirectUri, StandardCharsets.UTF_8.name())

        + "&response_type=code"

        + "&state=" + URLEncoder.encode(
                state, StandardCharsets.UTF_8.name());

		// 신규 또는 탈퇴 회원만 카카오 로그인 화면 재요청
		if (reAuth) {
		    kakaoLoginUrl += "&prompt=login";
		
		    System.out.println(
		        "[KakaoLogin] 신규/탈퇴 회원 카카오 재인증 요청"
		    );
		}

		// =====================================================
		// 5. 확인용 Eclipse Console 출력
		// =====================================================

		System.out.println("======================================");

		System.out.println("[KakaoLoginController] 카카오 로그인 시작");

		System.out.println("[KakaoLoginController] Redirect URI : " + redirectUri);

		System.out.println("======================================");

		// =====================================================
		// 6. 카카오 로그인 화면으로 이동
		// =====================================================

		response.sendRedirect(kakaoLoginUrl);
	}

	@Override
	// =========================================================
	// 주제: doPost 처리
	// 설명: POST 요청을 받아 입력값을 검증하고 저장/처리 결과를 응답
	// =========================================================
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}

	// =========================================================
	// 주제: makeRedirectUri 처리
	// 설명: 입력 데이터를 화면/API/DB에서 사용하기 좋은 형태로 변환 또는 가공
	// =========================================================
	private String makeRedirectUri(HttpServletRequest request) {

		String scheme = request.getScheme();
		String host = request.getServerName();
		int port = request.getServerPort();

		boolean defaultPort = ("http".equals(scheme) && port == 80) || ("https".equals(scheme) && port == 443);

		return scheme + "://" + host + (defaultPort ? "" : ":" + port) + request.getContextPath() + "/KakaoCallback";
	}

}
