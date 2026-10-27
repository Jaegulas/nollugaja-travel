package controller;

import config.AppConfigListener;
import dao.GuideReservationDao;
import dao.TossTestOrderDao;
import vo.Guide;
import vo.GuidePaymentDraft;
import vo.TossTestOrder;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

// =========================================================
// 주제: 파일 역할
// 설명: PaymentInfoController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


@WebServlet("/PaymentInfo")
public class PaymentInfoController extends HttpServlet {
    private final GuideReservationDao guideDAO = new GuideReservationDao();
    private final TossTestOrderDao orderDAO = new TossTestOrderDao();

    @Override
    // =========================================================
    // 주제: doGet 처리
    // 설명: GET 요청을 받아 필요한 데이터를 준비하고 화면 또는 응답으로 전달
    // =========================================================
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setHeader("Cache-Control", "no-store");

        HttpSession session = req.getSession(false);

        // LoginController에서 실제로 저장하는 세션 속성 이름은 "loginId"입니다.
        if (session == null || session.getAttribute("loginId") == null) {
            resp.setContentType("text/html; charset=UTF-8");
            java.io.PrintWriter out = resp.getWriter();
            out.println("<script>");
            out.println("alert('로그인 후 이용 가능합니다.');");
            out.println("location.href='" + req.getContextPath() + "/Login';");
            out.println("</script>");
            return;
        }

        GuidePaymentDraft draft = (GuidePaymentDraft) session.getAttribute("guidePaymentDraft");
        if (draft == null) {
            resp.sendRedirect(req.getContextPath() + "/GuideList");
            return;
        }

        String clientKey = AppConfigListener.getProperty("toss.client.key");
        if (clientKey == null || !clientKey.startsWith("test_ck")) {
            resp.sendError(503, "config.properties의 toss.client.key에 테스트 클라이언트 키를 설정하세요.");
            return;
        }

        try {
            LocalDate date = LocalDate.parse(draft.getReservationDate());
            if (date.isBefore(LocalDate.now()) || draft.getPeopleCount() < 1 || draft.getPeopleCount() > 15) {
                resp.sendError(400, "예약 날짜와 인원을 다시 선택해 주세요.");
                return;
            }

            Guide guide = guideDAO.findGuideById(draft.getGuideId());
            if (guide == null || guide.getPrice() <= 0) {
                resp.sendError(404, "가이드를 찾을 수 없습니다.");
                return;
            }

            long amount = Math.multiplyExact((long) guide.getPrice(), draft.getPeopleCount());
            String orderId = "TRAVEL-" + UUID.randomUUID().toString().replace("-", "");
            TossTestOrder order = new TossTestOrder(
            	    orderId,
            	    draft.getGuideId(),
            	    draft.getReservationDate(),
            	    draft.getPeopleCount(),
            	    amount,
            	    "PENDING"
            	);

            	try {
            	    orderDAO.insert(order, (String) session.getAttribute("loginId"));

            	} catch (java.sql.SQLIntegrityConstraintViolationException e) {

            	    // MySQL 1062: UNIQUE KEY 중복
            	    if (e.getErrorCode() == 1062 && e.getMessage() != null
                            && e.getMessage().contains("uk_guide_booking")) {
            	        resp.sendError(
            	            HttpServletResponse.SC_CONFLICT,
            	            "이미 예약된 가이드입니다. 다른 날짜를 선택해 주세요."
            	        );
            	        return;
            	    }

            	    throw e;
            	}

            session.setAttribute("tossTestOrderId", orderId);
            if (session.getAttribute("tossTestCustomerKey") == null) {
                session.setAttribute("tossTestCustomerKey", "customer-" + UUID.randomUUID());
            }

            req.setAttribute("guide", guide);
            req.setAttribute("draft", draft);
            req.setAttribute("amount", amount);
            req.setAttribute("orderId", orderId);
            req.setAttribute("clientKey", clientKey);
            req.setAttribute("customerKey", session.getAttribute("tossTestCustomerKey"));
            req.setAttribute("center", "paymentInfo/paymentInfo.jsp");
            req.getRequestDispatcher("/WEB-INF/main.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("주문 생성 DB 오류", e);
        } catch (ArithmeticException | java.time.format.DateTimeParseException e) {
            resp.sendError(400, "예약정보 오류");
        }
    }
}
