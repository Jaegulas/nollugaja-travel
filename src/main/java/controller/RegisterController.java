package controller;

import java.io.IOException;

import dao.RegisterDao;
import vo.RegisterVo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// =========================================================
// 주제: 파일 역할
// 설명: RegisterController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


@WebServlet("/Register")
public class RegisterController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	// =====================================================
	// GET
	// 1. 회원가입 페이지 출력
	// 2. 아이디 중복확인
	// =====================================================
	@Override
	// =========================================================
	// 주제: doGet 처리
	// 설명: GET 요청을 받아 필요한 데이터를 준비하고 화면 또는 응답으로 전달
	// =========================================================
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		String action = request.getParameter("action");

		// =================================================
		// 아이디 중복확인
		//
		// /Register?action=checkId&id=test123
		// =================================================
		if ("checkId".equals(action)) {

			response.setContentType("application/json; charset=UTF-8");

			String id = request.getParameter("id");

			if (id != null) {
				id = id.trim();
			}

			System.out.println("==============================");
			System.out.println("[RegisterController] 아이디 중복확인");
			System.out.println("입력 아이디 : " + id);
			System.out.println("==============================");

			// ---------------------------------------------
			// 아이디 미입력
			// ---------------------------------------------
			if (isBlank(id)) {

				response.getWriter()
						.write("{\"success\":false," + "\"available\":false," + "\"message\":\"아이디를 입력해주세요.\"}");

				return;
			}

			// ---------------------------------------------
			// 아이디 형식 검사
			// 영문 소문자 + 숫자 4~12자
			// ---------------------------------------------
			if (!id.matches("^[a-z0-9]{4,12}$")) {

				response.getWriter().write("{\"success\":false," + "\"available\":false,"
						+ "\"message\":\"아이디는 영문 소문자와 숫자로 4~12자 입력해주세요.\"}");

				return;
			}
			

			try {

				RegisterDao dao = new RegisterDao();
				
				

				// -----------------------------------------
				// 실제 MySQL DB 조회
				// -----------------------------------------
				boolean duplicated = dao.overlappedId(id);

				System.out.println("[RegisterController] DB 중복 여부 : " + duplicated);

				// -----------------------------------------
				// DB에 이미 존재
				// -----------------------------------------
				if (duplicated) {

					response.getWriter()
							.write("{\"success\":true," + "\"available\":false," + "\"message\":\"이미 사용 중인 아이디입니다.\"}");

				}

				// -----------------------------------------
				// DB에 존재하지 않음
				// -----------------------------------------
				else {

					response.getWriter()
							.write("{\"success\":true," + "\"available\":true," + "\"message\":\"사용 가능한 아이디입니다.\"}");
				}

			} catch (IllegalStateException e) {

				e.printStackTrace();

				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

				response.getWriter().write(
						"{\"success\":false," + "\"available\":false," + "\"message\":\"아이디 중복확인 중 DB 오류가 발생했습니다.\"}");
			}
			
			

			return;
		}
		
		

		// =================================================
		// 일반 회원가입 화면
		// =================================================
		System.out.println("[RegisterController] 회원가입 화면 요청");

		request.getRequestDispatcher("/WEB-INF/members/registration.jsp").forward(request, response);
	}

	// =====================================================
	// POST : 회원가입 처리
	// =====================================================
	@Override
	// =========================================================
	// 주제: doPost 처리
	// 설명: POST 요청을 받아 입력값을 검증하고 저장/처리 결과를 응답
	// =========================================================
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		System.out.println("===== 회원가입 POST 진입 성공 =====");

		request.setCharacterEncoding("UTF-8");

		// =================================================
		// 회원가입 값 받기
		// =================================================

		String id = request.getParameter("id");
		String pass = request.getParameter("pass");
		String passConfirm = request.getParameter("passConfirm");
		String name = request.getParameter("name");
		String age = request.getParameter("age");
		String gender = request.getParameter("gender");
		String hp = request.getParameter("hp");
		String email = request.getParameter("email");
		String address = request.getParameter("address");
		String tel = request.getParameter("tel");

		// 앞뒤 공백 제거
		if (id != null) {
			id = id.trim();
		}

		if (name != null) {
			name = name.trim();
		}

		if (email != null) {
			email = email.trim();
		}

		if (address != null) {
			address = address.trim();
		}

		// =================================================
		// 필수 입력값 검사
		// =================================================

		if (isBlank(id) || isBlank(pass) || isBlank(name) || isBlank(age) || isBlank(gender) || isBlank(hp)
				|| isBlank(email) || isBlank(address) || request.getParameter("agree_terms") == null
				|| request.getParameter("agree_privacy") == null) {

			showRegisterError(
				    request,
				    response,
				    "필수 회원가입 정보를 모두 입력해주세요."
				);

			return;
		}

		// =================================================
		// 아이디 형식 서버에서도 다시 검사
		// =================================================

		if (!id.matches("^[a-z0-9]{4,12}$")) {

		    showRegisterError(
		        request,
		        response,
		        "아이디는 영문 소문자와 숫자로 4~12자 입력해주세요."
		    );

		    return;
		}

		// =================================================
		// 비밀번호 형식 서버에서도 다시 검사
		// =================================================

		if (!pass.matches(
		        "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[^A-Za-z0-9\\s])\\S{8,16}$"
		)) {

		    showRegisterError(
		        request,
		        response,
		        "비밀번호는 8~16자의 영문, 숫자, 특수문자를 포함해야 합니다."
		    );

		    return;
		}
		
		// 비밀번호 확인 일치 여부 서버 검사
		if (passConfirm == null || !pass.equals(passConfirm)) {

		    showRegisterError(
		        request,
		        response,
		        "비밀번호와 비밀번호 확인이 일치하지 않습니다."
		    );

		    return;
		}

		// =================================================
		// 세션 확인 (이메일 인증에 필요)
		// =================================================

		HttpSession session = request.getSession(false);

		if (session == null) {

		    showRegisterError(
		        request,
		        response,
		        "이메일 인증 세션이 만료되었습니다. 다시 인증해주세요."
		    );

		    return;
		}

		// =================================================
		// 이메일 인증 확인
		// =================================================

		Boolean emailVerified = (Boolean) session.getAttribute("emailVerified");

		String verifiedEmail = (String) session.getAttribute("verifiedEmail");

		// =================================================
		// 이메일 인증 성공 여부
		// =================================================

		if (!Boolean.TRUE.equals(emailVerified)) {

			showRegisterError(request, response, "이메일 인증을 완료해주세요.");

			return;
		}

		if (verifiedEmail == null || !verifiedEmail.equalsIgnoreCase(email)) {

		    showRegisterError(
		        request,
		        response,
		        "인증받은 이메일과 회원가입 이메일이 다릅니다."
		    );

		    return;
		}


		// =================================================
		// 나이 숫자 변환
		// =================================================

		int ageValue;

		try {

		    ageValue = Integer.parseInt(age);

		    if (ageValue < 14 || ageValue > 120) {

		        showRegisterError(
		            request,
		            response,
		            "나이는 14세부터 120세까지 입력할 수 있습니다."
		        );

		        return;
		    }

		} catch (NumberFormatException e) {

		    showRegisterError(
		        request,
		        response,
		        "나이는 숫자로 입력해야 합니다."
		    );

		    return;
		}

		// =================================================
		// 전달값 확인
		// =================================================

		System.out.println("===== 회원가입 전달값 확인 =====");

		System.out.println("id : " + id);

		// 비밀번호 자체는 출력하지 않음
		System.out.println("pass 입력 여부 : " + !isBlank(pass));

		System.out.println("name : " + name);
		System.out.println("age : " + age);
		System.out.println("gender : " + gender);
		System.out.println("hp : " + hp);
		System.out.println("email : " + email);
		System.out.println("address : " + address);
		System.out.println("tel : " + tel);

		// =================================================
		// VO 생성
		// =================================================

		RegisterVo vo = new RegisterVo();

		vo.setId(id);
		vo.setPass(pass);
		vo.setName(name);
		vo.setAge(ageValue);
		vo.setGender(gender);
		vo.setHp(hp);
		vo.setEmail(email);
		vo.setAddress(address);
		vo.setTel(tel);

		// =================================================
		// DB 처리
		// =================================================

		try {

			RegisterDao dao = new RegisterDao();

			// ========================================
			// 최종 아이디 중복확인
			// ========================================
			if (dao.overlappedId(id)) {

			    System.out.println(
			        "[RegisterController] 중복 아이디 회원가입 차단 : " + id
			    );

			    showRegisterError(
			        request,
			        response,
			        "이미 사용 중인 아이디입니다."
			    );

			    return;
			}

			// =================================================
			// 회원가입 INSERT
			// =================================================

			int result = dao.insertMember(vo);

			if (result == 1) {

			    System.out.println(
			        "[RegisterController] 회원가입 성공 : " + id
			    );

			    // 회원가입 성공 후 이메일 인증 정보 삭제
			    session.removeAttribute("emailCode");
			    session.removeAttribute("emailCodeEmail");
			    session.removeAttribute("emailCodeCreatedTime");
			    session.removeAttribute("emailVerified");
			    session.removeAttribute("verifiedEmail");

			    response.setContentType("text/html; charset=UTF-8");

			    response.getWriter().println(
			        "<script>"
			        + "alert('회원가입이 완료되었습니다. 로그인해주세요.');"
			        + "location.href='"
			        + request.getContextPath()
			        + "/Login';"
			        + "</script>"
			    );

			    return;
			}

			// =================================================
			// INSERT 실패
			// =================================================

			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "회원가입에 실패했습니다.");

		} catch (IllegalStateException e) {

			System.err.println("[RegisterController] DB 처리 오류 : " + e.getMessage());

			e.printStackTrace();

			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "회원가입 처리 중 DB 오류가 발생했습니다.");
		}
	}

	// =========================================================
	// 주제: isBlank 처리
	// 설명: 입력값 또는 현재 상태가 조건에 맞는지 검사
	// =========================================================
	private boolean isBlank(String value) {

		return value == null || value.trim().isEmpty();
	}

	// =========================================================
	// 주제: showRegisterError 처리
	// 설명: showRegisterError 기능에 필요한 처리 로직을 수행
	// =========================================================
	private void showRegisterError(HttpServletRequest request, HttpServletResponse response, String message)
			throws ServletException, IOException {

		request.setAttribute("registerError", message);

		request.getRequestDispatcher("/WEB-INF/members/registration.jsp").forward(request, response);
	}

}
