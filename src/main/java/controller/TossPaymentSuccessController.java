package controller;

import com.fasterxml.jackson.databind.JsonNode;
import dao.TossTestOrderDao;
import service.TossTestPaymentService;
import vo.TossTestOrder;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

// =========================================================
// 주제: 파일 역할
// 설명: TossPaymentSuccessController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


@WebServlet("/TossPaymentSuccess")
public class TossPaymentSuccessController extends HttpServlet {
    private final TossTestOrderDao dao = new TossTestOrderDao();
    private final TossTestPaymentService service = new TossTestPaymentService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setHeader("Cache-Control", "no-store");
        HttpSession session = req.getSession(false);
        String orderId = req.getParameter("orderId");
        String paymentKey = req.getParameter("paymentKey");
        String amountStr = req.getParameter("amount");
        if (session == null || orderId == null || paymentKey == null || amountStr == null ||
                !orderId.equals(session.getAttribute("tossTestOrderId"))) {
            resp.sendError(403,"현재 세션의 결제 주문이 아닙니다.");return;
        }
        try {
            long amount = Long.parseLong(amountStr);
            TossTestOrder order = dao.find(orderId);
            if (order == null || order.getAmount() != amount) {resp.sendError(400,"주문 금액 불일치");return;}
            if ("PAID".equals(order.getStatus())) {
                show(req,resp,order,"이미 승인된 테스트 결제입니다.");return;
            }
            if (!dao.claim(orderId)) {resp.sendError(409,"승인 중이거나 확인이 필요한 주문입니다.");return;}
            try {
                JsonNode result = service.confirm(paymentKey,orderId,order.getAmount());
                dao.paid(orderId, paymentKey, result.path("method").asText(), result.path("approvedAt").asText());
                show(req,resp,dao.find(orderId),"토스페이먼츠 테스트 결제가 승인됐습니다.");
            } catch (IOException | InterruptedException | SQLException | RuntimeException e) {
                // 네트워크/DB 오류 시 승인 성공 여부가 불명확할 수 있으므로 자동 재승인하지 않음.
                dao.markReview(orderId);
                if (e instanceof InterruptedException) Thread.currentThread().interrupt();
                getServletContext().log("토스 테스트 승인 결과 확인 필요: " + orderId, e);
                resp.sendError(502,"결제 결과 확인이 필요합니다. DB의 REVIEW 상태와 토스 테스트 결제내역을 확인하세요.");
            }
        } catch (NumberFormatException e) {resp.sendError(400,"결제 금액 형식 오류");}
        catch (SQLException e) {throw new ServletException("결제 주문 조회 오류",e);}
    }
    // =========================================================
    // 주제: show 처리
    // 설명: show 기능에 필요한 처리 로직을 수행
    // =========================================================
    private void show(HttpServletRequest req,HttpServletResponse resp,TossTestOrder order,String message)
            throws ServletException,IOException {
        req.setAttribute("paidOrder",order);
        req.setAttribute("paymentMessage",message);
        req.setAttribute("center","paymentInfo/paymentComplete.jsp");
        req.getRequestDispatcher("/WEB-INF/main.jsp").forward(req,resp);
    }
}
