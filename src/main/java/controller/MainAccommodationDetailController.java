package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dto.MainAccommodationDto;
import util.DBCPUtil;

@WebServlet("/MainAccommodationDetail")
public class MainAccommodationDetailController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        String idParam = req.getParameter("accommodationId");

        // 메인에서 숙소를 선택하지 않은 일반 접근은
        // 기존 숙박 컨트롤러와 동일한 목록 조회 흐름이 필요함.
        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendRedirect(
                req.getContextPath() + "/Accommodation/acco_main"
            );
            return;
        }

        int id;

        try {
            id = Integer.parseInt(idParam);
            if (id <= 0) {
                resp.sendError(400, "잘못된 숙소 번호입니다.");
                return;
            }
        } catch (NumberFormatException e) {
            resp.sendError(400, "잘못된 숙소 번호입니다.");
            return;
        }

        HttpSession session = req.getSession(false);

        String loginId = session == null
                ? null
                : (String) session.getAttribute("loginId");

        String sql =
            "SELECT a.accommodation_id, a.placeid, a.name, " +
            "a.addr, a.type, a.tel, a.link, a.lat, a.lon, " +
            "a.avg_rating, a.review_count, a.picpath, a.updated_at, " +
            "(l.member_id IS NOT NULL) AS isliked " +
            "FROM accommodation a " +
            "LEFT JOIN accommodation_like l " +
            "ON l.accommodation_id = a.accommodation_id " +
            "AND l.member_id = ? " +
            "WHERE a.accommodation_id = ?";

        try (Connection con = DBCPUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, loginId);
            ps.setInt(2, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    resp.sendError(404, "숙소를 찾을 수 없습니다.");
                    return;
                }

                MainAccommodationDto stay =
                    new MainAccommodationDto(
                        rs.getInt("accommodation_id"),
                        rs.getString("placeid"),
                        rs.getString("name"),
                        rs.getString("addr"),
                        rs.getString("type"),
                        rs.getString("tel"),
                        rs.getString("link"),
                        rs.getObject("lat") == null ? null : rs.getDouble("lat"),
                		rs.getObject("lon") == null ? null : rs.getDouble("lon"),
                		rs.getObject("avg_rating") == null ? null : rs.getDouble("avg_rating"),
                        (Integer) rs.getObject("review_count"),
                        rs.getBoolean("isliked"),
                        rs.getString("picpath"),
                        rs.getObject("updated_at",
                            java.time.LocalDateTime.class)
                    );

                req.setAttribute("center", "accommodation/acco_main.jsp");
                req.setAttribute("pageCss", "accommodation/acco_main.css");
                req.setAttribute("volist", Collections.singletonList(stay));

                System.out.println(
                    "[MainAccommodationDetail] 선택한 숙소: "
                    + stay.getAccommodation_id()
                    + " / " + stay.getName()
                );

                req.getRequestDispatcher("/WEB-INF/main.jsp")
                   .forward(req, resp);
            }

        } catch (SQLException e) {
            throw new ServletException("숙소 조회 오류", e);
        }
    }
}