package controller;

import dao.GuideReservationDao;
import dto.GuideTourDto;
import vo.Guide;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// =========================================================
// 주제: 파일 역할
// 설명: GuideReservationController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


@WebServlet("/GuideReservation")
public class GuideReservationController extends HttpServlet {
    private final GuideReservationDao dao = new GuideReservationDao();

    @Override
    // =========================================================
    // 주제: doGet 처리
    // 설명: GET 요청을 받아 필요한 데이터를 준비하고 화면 또는 응답으로 전달
    // =========================================================
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int guideId;
        try {
            guideId = Integer.parseInt(req.getParameter("guideId"));
            if (guideId <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "올바른 가이드 ID가 필요합니다.");
            return;
        }
        try {
            Guide guide = dao.findGuideById(guideId);
            if (guide == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "해당 가이드를 찾을 수 없습니다.");
                return;
            }
            req.setAttribute("guide", guide);
            GuideTourDto tour = dao.findTourByGuideId(guideId);
            req.setAttribute("tour", tour);
            if (tour != null) {
                req.setAttribute("tourCourses", dao.findCoursesByTourId(tour.getTourId()));
            }
            req.setAttribute("today", LocalDate.now().toString());
            HttpSession currentSession = req.getSession(false);
            if (currentSession != null) {
                Object saved = currentSession.getAttribute("guidePaymentDraft");
                if (saved instanceof vo.GuidePaymentDraft) {
                    vo.GuidePaymentDraft draft = (vo.GuidePaymentDraft) saved;
                    if (draft.getGuideId() == guideId) {
                        req.setAttribute("savedDate", draft.getReservationDate());
                        req.setAttribute("savedPeople", draft.getPeopleCount());
                    }
                }
            }
            // 기존 main.jsp 공통 레이아웃 사용: top + center + bottom
            req.setAttribute("center", "guideReservation/guideReservation.jsp");
            req.getRequestDispatcher("/WEB-INF/main.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("가이드 조회 중 DB 오류", e);
        }
    }

    @Override
    // =========================================================
    // 주제: doPost 처리
    // 설명: POST 요청을 받아 입력값을 검증하고 저장/처리 결과를 응답
    // =========================================================
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            int guideId = Integer.parseInt(req.getParameter("guideId"));
            int people = Integer.parseInt(req.getParameter("peopleCount"));
            LocalDate date = LocalDate.parse(req.getParameter("reservationDate"));
            if (guideId <= 0 || people < 1 || people > 15 || date.isBefore(LocalDate.now())) {
                resp.sendError(400, "예약 날짜 또는 인원을 확인해 주세요.");
                return;
            }
            Guide guide = dao.findGuideById(guideId);
            if (guide == null || guide.getPrice() < 0) {
                resp.sendError(404, "가이드 정보를 찾을 수 없습니다.");
                return;
            }
            // 결제 연동 전 단계: DB 예약을 확정하지 않고 세션에 '예약 초안'만 저장합니다.
            // 클라이언트가 보낸 가격을 신뢰하지 않고 DB에서 다시 조회합니다.
            HttpSession session = req.getSession(true);
            session.setAttribute("guidePaymentDraft",
                    new vo.GuidePaymentDraft(guideId, date.toString(), people));
            resp.sendRedirect(req.getContextPath() + "/PaymentInfo");
        } catch (NumberFormatException | DateTimeParseException | NullPointerException e) {
            resp.sendError(400, "날짜 또는 인원 입력이 올바르지 않습니다.");
        } catch (SQLException e) {
            throw new ServletException("가이드 조회 중 DB 오류", e);
        }
    }
}
