
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
// 설명: FindIdController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


@WebServlet("/FindId")
public class FindIdController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final SecureRandom RANDOM =
            new SecureRandom();

    private static final long EXPIRE_TIME =
            5 * 60 * 1000L;

    private static final int MAX_ATTEMPTS = 5;

    @Override
    // =========================================================
    // 주제: doGet 처리
    // 설명: GET 요청을 받아 필요한 데이터를 준비하고 화면 또는 응답으로 전달
    // =========================================================
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher(
            "/WEB-INF/members/findId.jsp"
        ).forward(request, response);
    }

    @Override
    // =========================================================
    // 주제: doPost 처리
    // 설명: POST 요청을 받아 입력값을 검증하고 저장/처리 결과를 응답
    // =========================================================
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType(
            "application/json; charset=UTF-8"
        );
        response.setHeader("Cache-Control", "no-store");

        String action = request.getParameter("action");

        if ("send".equals(action)) {
            sendCode(request, response);
        } else if ("verify".equals(action)) {
            verifyCode(request, response);
        } else {
            response.setStatus(400);
            writeJson(response, false, "잘못된 요청입니다.", null);
        }
    }

    // 인증번호 발송
    // =========================================================
    // 주제: sendCode 처리
    // 설명: 필요한 데이터를 외부 또는 사용자에게 전송
    // =========================================================
    private void sendCode(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        String name = trim(request.getParameter("name"));
        String email = trim(request.getParameter("email"));

        if (name.isEmpty() || email.isEmpty()) {
            writeJson(response, false,
                "이름과 이메일을 입력해주세요.", null);
            return;
        }

        if (!email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            writeJson(response, false,
                "올바른 이메일을 입력해주세요.", null);
            return;
        }

        HttpSession session = request.getSession();

        // 이전 아이디 찾기 인증 정보 제거
        clearRecovery(session);

        try {
            AccountRecoveryDao dao =
                new AccountRecoveryDao();

            String id = dao.findId(name, email);

            // 가입 여부를 직접 노출하지 않음
            if (id == null) {
                writeJson(response, true,
                    "등록된 이메일이라면 인증번호가 발송됩니다.",
                    null);
                return;
            }
            
         // 이메일 인증번호 발송 횟수 제한
            if (!EmailRateLimiter.tryAcquire(email)) {
                response.setStatus(429);
                writeJson(response, false,
                    "인증번호 발송 횟수를 초과했습니다. 5분 후 다시 시도해주세요.",
                    null);
                return;
            }

            String code = String.format(
                "%06d", RANDOM.nextInt(1000000)
            );

            EmailService emailService =
                new EmailService();

            emailService.sendVerificationEmail(email, code);

            session.setAttribute("findIdCode", code);
            session.setAttribute("findIdEmail", email);
            session.setAttribute("findIdName", name);
            session.setAttribute("findIdTime",
                System.currentTimeMillis());
            session.setAttribute("findIdAttempts", 0);

            writeJson(response, true,
                "등록된 이메일이라면 인증번호가 발송됩니다.",
                null);

        } catch (MessagingException | IllegalStateException e) {
            getServletContext().log(
                "아이디 찾기 인증번호 발송 실패", e
            );
            response.setStatus(500);
            writeJson(response, false,
                "처리 중 오류가 발생했습니다.", null);
        }
    }

    // 인증번호 확인
    // =========================================================
    // 주제: verifyCode 처리
    // 설명: 입력값 또는 현재 상태가 조건에 맞는지 검사
    // =========================================================
    private void verifyCode(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);

        if (session == null) {
            writeJson(response, false,
                "인증번호를 다시 발송해주세요.", null);
            return;
        }

        String inputCode =
            trim(request.getParameter("code"));

        String savedCode =
            (String) session.getAttribute("findIdCode");

        String email =
            (String) session.getAttribute("findIdEmail");

        String name =
            (String) session.getAttribute("findIdName");

        Long createdTime =
            (Long) session.getAttribute("findIdTime");

        Integer attempts =
            (Integer) session.getAttribute("findIdAttempts");

        if (savedCode == null || email == null ||
                name == null || createdTime == null) {
            writeJson(response, false,
                "인증번호를 다시 발송해주세요.", null);
            return;
        }

        if (System.currentTimeMillis() - createdTime
                > EXPIRE_TIME) {
            clearRecovery(session);
            writeJson(response, false,
                "인증번호가 만료되었습니다.", null);
            return;
        }

        int count = attempts == null ? 0 : attempts;

        if (count >= MAX_ATTEMPTS) {
            clearRecovery(session);
            writeJson(response, false,
                "인증 횟수를 초과했습니다. 다시 발송해주세요.",
                null);
            return;
        }

        if (!savedCode.equals(inputCode)) {

            int nextCount = count + 1;

            if (nextCount >= MAX_ATTEMPTS) {
                clearRecovery(session);

                writeJson(response, false,
                    "인증 횟수를 초과했습니다. 인증번호를 다시 발송해주세요.",
                    null);
                return;
            }

            session.setAttribute("findIdAttempts", nextCount);

            writeJson(response, false,
                "인증번호가 일치하지 않습니다. 남은 횟수: "
                + (MAX_ATTEMPTS - nextCount), null);
            return;
        }

        try {
            AccountRecoveryDao dao =
                new AccountRecoveryDao();

            // 인증 성공 후 DB에서 다시 조회
            String id = dao.findId(name, email);

            clearRecovery(session);

            if (id == null) {
                writeJson(response, false,
                    "회원정보를 확인할 수 없습니다.", null);
                return;
            }

            writeJson(response, true,
                "아이디 찾기가 완료되었습니다.", id);

        } catch (IllegalStateException e) {
            getServletContext().log(
                "아이디 찾기 조회 실패", e
            );
            response.setStatus(500);
            writeJson(response, false,
                "처리 중 오류가 발생했습니다.", null);
        }
    }

    // =========================================================
    // 주제: clearRecovery 처리
    // 설명: clearRecovery 기능에 필요한 처리 로직을 수행
    // =========================================================
    private void clearRecovery(HttpSession session) {
        session.removeAttribute("findIdCode");
        session.removeAttribute("findIdEmail");
        session.removeAttribute("findIdName");
        session.removeAttribute("findIdTime");
        session.removeAttribute("findIdAttempts");
    }

    // =========================================================
    // 주제: trim 처리
    // 설명: trim 기능에 필요한 처리 로직을 수행
    // =========================================================
    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private void writeJson(
            HttpServletResponse response,
            boolean success,
            String message,
            String id) throws IOException {

        String json =
            "{\"success\":" + success +
            ",\"message\":\"" + escapeJson(message) + "\"";

        if (id != null) {
            json += ",\"id\":\"" + escapeJson(id) + "\"";
        }

        json += "}";

        response.getWriter().write(json);
    }

    // =========================================================
    // 주제: escapeJson 처리
    // 설명: escapeJson 기능에 필요한 처리 로직을 수행
    // =========================================================
    private String escapeJson(String value) {
        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r");
    }
}
