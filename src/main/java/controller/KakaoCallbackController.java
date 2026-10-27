
package controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.KakaoDao;
import service.KakaoService;
import service.KakaoService.KakaoUser;
import vo.LoginVo;

// =========================================================
// 주제: 파일 역할
// 설명: KakaoCallbackController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


@WebServlet("/KakaoCallback")
public class KakaoCallbackController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	// ==========================================
	// 1. 카카오 설정 정보
	// ==========================================
	private String kakaoRestApiKey;
	private String kakaoClientSecret;

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

			kakaoClientSecret = props.getProperty("kakao.client-secret", "");

			if (kakaoRestApiKey == null || kakaoRestApiKey.trim().isEmpty()) {

				throw new ServletException("카카오 REST API 키가 없습니다.");
			}

		} catch (IOException e) {
			throw new ServletException("카카오 설정 파일 읽기 실패", e);
		}
	}

	// ==========================================
	// 2. 카카오 로그인 콜백 처리
	// ==========================================
	@Override
	// =========================================================
	// 주제: doGet 처리
	// 설명: GET 요청을 받아 필요한 데이터를 준비하고 화면 또는 응답으로 전달
	// =========================================================
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		String code = request.getParameter("code");
		String state = request.getParameter("state");
		String error = request.getParameter("error");

		// 기존 로그인 요청 세션 확인
		HttpSession oldSession = request.getSession(false);

		if (oldSession == null) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "로그인 세션이 없습니다.");
			return;
		}

		// ==========================================
		// 3. state 검증
		// ==========================================
		String savedState = (String) oldSession.getAttribute("KAKAO_STATE");

		// 로그인 시작 시 저장했던 Redirect URI 가져오기
		String redirectUri =
		        (String) oldSession.getAttribute("KAKAO_REDIRECT_URI");

		// 재사용 방지
		oldSession.removeAttribute("KAKAO_STATE");
		oldSession.removeAttribute("KAKAO_REDIRECT_URI");

		// state와 Redirect URI 검증
		if (savedState == null
		        || state == null
		        || !savedState.equals(state)
		        || redirectUri == null
		        || redirectUri.trim().isEmpty()) {

		    response.sendError(
		            HttpServletResponse.SC_FORBIDDEN,
		            "잘못된 카카오 로그인 요청입니다."
		    );
		    return;
		}

		// 카카오 인증 실패 또는 취소
		if (error != null) {

			System.out.println("[KakaoCallback] 인증 실패 또는 취소");

			response.sendRedirect(request.getContextPath() + "/Login");
			return;
		}

		if (code == null || code.trim().isEmpty()) {

			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "카카오 인가 코드가 없습니다.");
			return;
		}

		try {

			// ==========================================
			// 4. 카카오 API 통신
			// ==========================================
			KakaoService kakaoService = new KakaoService();


			// 인가 코드 → 액세스 토큰
			String accessToken = kakaoService.getAccessToken(code, kakaoRestApiKey, redirectUri, kakaoClientSecret);

			// 액세스 토큰 → 카카오 사용자 정보
			KakaoUser kakaoUser = kakaoService.getUserInfo(accessToken);

			System.out.println("[KakaoCallback] 사용자 정보 조회 성공");

			// ==========================================
			// 5. DB 회원 조회 및 재인증 처리
			// ==========================================

			KakaoDao kakaoDao = new KakaoDao();

			long kakaoId = kakaoUser.getKakaoId();

			// 재인증 요청이었는지 확인
			boolean reAuth = Boolean.TRUE.equals(
			        oldSession.getAttribute("KAKAO_REAUTH")
			);

			// 이전 인증에서 확인한 카카오 ID
			Object pendingId = oldSession.getAttribute(
			        "PENDING_KAKAO_ID"
			);

			// ==========================================
			// A. 신규/탈퇴 회원 재인증 완료
			// ==========================================

			LoginVo member;

			if (reAuth) {

			    if (!(pendingId instanceof Long)
			            || ((Long) pendingId).longValue() != kakaoId) {

			        oldSession.removeAttribute("PENDING_KAKAO_ID");
			        oldSession.removeAttribute("PENDING_KAKAO_NICKNAME");
			        oldSession.removeAttribute("KAKAO_REAUTH");

			        response.sendError(
			                HttpServletResponse.SC_FORBIDDEN,
			                "카카오 재인증 계정이 일치하지 않습니다."
			        );
			        return;
			    }

			    System.out.println(
			        "[KakaoCallback] 카카오 재인증 완료"
			    );

			    // 동시 요청 등으로 이미 가입됐을 가능성 확인
			    member = kakaoDao.findOrCreate(
			            kakaoId,
			            kakaoUser.getNickname()
			    );

			    oldSession.removeAttribute("PENDING_KAKAO_ID");
			    oldSession.removeAttribute("PENDING_KAKAO_NICKNAME");
			    oldSession.removeAttribute("KAKAO_REAUTH");

			} else {

			    // ==========================================
			    // B. 최초 카카오 인증
			    // ==========================================

			    member = kakaoDao.findByKakaoId(kakaoId);

			    if (member == null) {

			        System.out.println(
			            "[KakaoCallback] 신규/탈퇴 회원 → 카카오 재인증"
			        );

			        // 재인증 대상 카카오 ID 임시 저장
			        oldSession.setAttribute(
			                "PENDING_KAKAO_ID", kakaoId
			        );

			        oldSession.setAttribute(
			                "PENDING_KAKAO_NICKNAME",
			                kakaoUser.getNickname()
			        );

			        // 카카오 로그인 시작 컨트롤러로 다시 이동
			        response.sendRedirect(
			                request.getContextPath() + "/KakaoLogin"
			        );

			        return;
			    }

			    System.out.println(
			        "[KakaoCallback] 기존 회원 → 바로 로그인"
			    );
			}

			if (member == null) {
			    throw new ServletException(
			        "카카오 회원정보 처리 실패"
			    );
			}
			// ==========================================
			// 6. 로그인 세션 생성
			// ==========================================

			// 로그인 전에 보고 있던 페이지 주소 확보
			String redirectUrl = (String) oldSession.getAttribute("KAKAO_REDIRECT_URL");

			// 기존 인증 세션 삭제
			oldSession.invalidate();
			// 새로운 로그인 세션 생성
			HttpSession session = request.getSession(true);

			// 일반 로그인과 동일한 세션 이름
			session.setAttribute("loginId", member.getId());

			session.setAttribute("loginName", member.getName());

			// 카카오 자동가입 회원은 일반 회원
			session.setAttribute("role", "USER");
			session.setAttribute("isAdmin", false);

			// 60분 유지
			session.setMaxInactiveInterval(60 * 60);

			// ==========================================
			// 7. 로그인 성공
			// ==========================================
			System.out.println("========== 카카오 로그인 성공 ==========");

			System.out.println("회원 ID : " + member.getId());

			System.out.println("회원 이름 : " + member.getName());

			System.out.println("======================================");

			// ==========================================
			// 8. 로그인 전 페이지로 복귀
			// ==========================================

			String contextPath = request.getContextPath();

			boolean validRedirect = false;

			if (redirectUrl != null) {
				try {
					java.net.URI uri = new java.net.URI(redirectUrl);
					String path = uri.getRawPath();

					validRedirect = !uri.isAbsolute() && uri.getRawAuthority() == null && uri.getRawFragment() == null
							&& path != null && path.startsWith(contextPath + "/")
							&& !path.startsWith(contextPath + "//") && !path.matches("(?i).*%(?:2f|5c|2e|25).*")
							&& !redirectUrl.contains("\\") && !redirectUrl.contains("\r") && !redirectUrl.contains("\n")
							&& !path.equals(contextPath + "/Login");

				} catch (java.net.URISyntaxException e) {
					validRedirect = false;
				}
			}

			if (!validRedirect) {
				redirectUrl = contextPath + "/Main";
			}

			System.out.println("[KakaoCallback] 로그인 후 복귀 페이지 : " + redirectUrl);

			response.sendRedirect(redirectUrl);

		} catch (Exception e) {

			System.err.println("[KakaoCallback] 로그인 처리 오류");

			e.printStackTrace();

			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "카카오 로그인 처리 중 오류가 발생했습니다.");
		}
	}
}
