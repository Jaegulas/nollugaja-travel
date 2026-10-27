package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

// =========================================================
// 주제: 파일 역할
// 설명: TossPaymentFailController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


@WebServlet("/TossPaymentFail")
public class TossPaymentFailController extends HttpServlet {
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse resp)
            throws ServletException,IOException {
        resp.setHeader("Cache-Control", "no-store");
        req.setAttribute("center","paymentInfo/paymentFail.jsp");
        req.getRequestDispatcher("/WEB-INF/main.jsp").forward(req,resp);
    }
}
