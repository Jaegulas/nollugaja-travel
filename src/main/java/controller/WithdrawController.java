package controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.RegisterDao;

// =========================================================
// 주제: 회원 탈퇴 컨트롤러
// 설명: 비밀번호를 다시 확인한 뒤 상태값 + 익명화 방식으로 회원 탈퇴를 처리
// =========================================================
@WebServlet("/Mypage/withdraw")
public class WithdrawController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // =========================================================
    // 주제: 회원 탈퇴 POST 처리
    // 설명: 로그인 세션과 비밀번호를 검증하고 성공 시 세션 종료 후 메인으로 이동
    // =========================================================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        HttpSession session = request.getSession(false);
        String loginId = session == null ? null : (String) session.getAttribute("loginId");

        if (loginId == null || loginId.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        String passwd = request.getParameter("passwd");
        RegisterDao dao = new RegisterDao();

        // =========================================================
        // 주제: 탈퇴 비밀번호 재확인
        // 설명: 현재 로그인 회원의 비밀번호가 일치하지 않으면 탈퇴를 진행하지 않음
        // =========================================================
        if (passwd == null || passwd.isEmpty() || !dao.checkWithdrawPassword(loginId, passwd)) {
            showAlert(response, "비밀번호가 일치하지 않습니다.",
                    request.getContextPath() + "/Mypage/mypage_main");
            return;
        }

        // =========================================================
        // 주제: 회원 탈퇴 실행
        // 설명: 개인 데이터 정리, 개인정보 익명화, WITHDRAWN 상태 변경을 하나의 트랜잭션으로 수행
        // =========================================================
        boolean success;
        try {
            success = dao.withdrawMember(loginId);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            showAlert(response, "회원 탈퇴 처리 중 오류가 발생했습니다.",
                    request.getContextPath() + "/Mypage/mypage_main");
            return;
        }

        if (!success) {
            showAlert(response, "회원 탈퇴 처리 중 오류가 발생했습니다.",
                    request.getContextPath() + "/Mypage/mypage_main");
            return;
        }

        // =========================================================
        // 주제: 탈퇴 완료 세션 종료
        // 설명: 탈퇴한 계정으로 기존 로그인 세션을 계속 사용할 수 없도록 즉시 무효화
        // =========================================================
        session.invalidate();
        showAlert(response, "회원 탈퇴 되셨습니다.\n이용해주셔서 감사합니다.",
                request.getContextPath() + "/Main");
    }

    // =========================================================
    // 주제: 탈퇴 요청 GET 차단
    // 설명: 주소창 접근만으로 회원 정보가 변경되지 않도록 POST 요청만 허용
    // =========================================================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    // =========================================================
    // 주제: 결과 안내창 출력
    // 설명: 처리 결과를 alert로 보여준 뒤 지정한 내부 페이지로 이동
    // =========================================================
    private void showAlert(HttpServletResponse response, String message, String moveUrl) throws IOException {
        String safeMessage = message.replace("\\", "\\\\").replace("'", "\\'")
                .replace("\r", "").replace("\n", "\\n");
        String safeUrl = moveUrl.replace("'", "");

        PrintWriter out = response.getWriter();
        out.print("<!doctype html><html><head><meta charset='UTF-8'></head><body>");
        out.print("<script>alert('" + safeMessage + "');location.href='" + safeUrl + "';</script>");
        out.print("</body></html>");
    }
}
