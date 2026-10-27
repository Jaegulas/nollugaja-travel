package dao;

import dto.GuideTourDto;
import dto.AdminReservationDto;
import dto.GuideTourCourseDto;
import java.util.List;
import java.util.Map;

import com.mysql.cj.jdbc.exceptions.SQLError;

import java.util.ArrayList;
import java.util.HashMap;

import util.DBCPUtil;
import vo.Guide;

import java.sql.*;
import java.time.LocalDate;

// =========================================================
// 주제: 파일 역할
// 설명: GuideReservationDao 관련 데이터베이스 조회·저장·수정·삭제를 담당하는 DAO
// =========================================================


public class GuideReservationDao {
    public Guide findGuideById(int id) throws SQLException {
        String sql = "SELECT guide_id, guide_name, guide_image, guide_intro, guide_phone, " +
                "region_id, region_name, specialty, tags, price, rating, review_count, tour_count " +
                "FROM guide WHERE guide_id = ?";
        try (Connection con = DBCPUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Guide g = new Guide();
                g.setGuideId(rs.getInt("guide_id"));
                g.setGuideName(rs.getString("guide_name"));
                g.setGuideImage(rs.getString("guide_image"));
                g.setGuideIntro(rs.getString("guide_intro"));
                g.setGuidePhone(rs.getString("guide_phone"));
                g.setRegionId(rs.getInt("region_id"));
                g.setRegionName(rs.getString("region_name"));
                g.setSpecialty(rs.getString("specialty"));
                g.setTags(rs.getString("tags"));
                g.setPrice(rs.getInt("price"));
                g.setRating(rs.getBigDecimal("rating"));
                g.setReviewCount(rs.getInt("review_count"));
                g.setTourCount(rs.getInt("tour_count"));
                return g;
            }
        }
    }

    // =========================================================
    // 주제: createReservation 처리
    // 설명: 입력받은 데이터를 생성하거나 저장
    // =========================================================
    public long createReservation(int memberId, int guideId, LocalDate date, int people) throws SQLException {
        String sql = "INSERT INTO guide_reservation " +
                "(member_id, guide_id, reservation_date, people_count, reservation_status) " +
                "VALUES (?, ?, ?, ?, 'request')";
        try (Connection con = DBCPUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, memberId);
            ps.setInt(2, guideId);
            ps.setDate(3, Date.valueOf(date));
            ps.setInt(4, people);
            if (ps.executeUpdate() != 1) throw new SQLException("예약 저장 실패");
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
                throw new SQLException("예약 ID를 가져오지 못했습니다.");
            }
        }
    }

    // =========================================================
    // 주제: findTourByGuideId 처리
    // 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
    // =========================================================
    public GuideTourDto findTourByGuideId(int guideId) throws SQLException {
        String sql = "SELECT tour_id, guide_id, tour_title, duration, description, included, excluded, cancellation_policy "
                + "FROM guide_tour WHERE guide_id = ? ORDER BY tour_id LIMIT 1";
        try (Connection con = DBCPUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, guideId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                GuideTourDto t = new GuideTourDto();
                t.setTourId(rs.getInt("tour_id"));
                t.setGuideId(rs.getInt("guide_id"));
                t.setTourTitle(rs.getString("tour_title"));
                t.setDuration(rs.getString("duration"));
                t.setDescription(rs.getString("description"));
                t.setIncluded(rs.getString("included"));
                t.setExcluded(rs.getString("excluded"));
                t.setCancellationPolicy(rs.getString("cancellation_policy"));
                return t;
            }
        }
    }

    // =========================================================
    // 주제: findCoursesByTourId 처리
    // 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
    // =========================================================
    public List<GuideTourCourseDto> findCoursesByTourId(int tourId) throws SQLException {
        String sql = "SELECT course_id, tour_id, course_order, course_name, course_description, duration_minutes "
                + "FROM guide_tour_course WHERE tour_id = ? ORDER BY course_order, course_id";
        List<GuideTourCourseDto> courses = new ArrayList<>();
        try (Connection con = DBCPUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, tourId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    GuideTourCourseDto c = new GuideTourCourseDto();
                    c.setCourseId(rs.getInt("course_id"));
                    c.setTourId(rs.getInt("tour_id"));
                    c.setCourseOrder(rs.getInt("course_order"));
                    c.setCourseName(rs.getString("course_name"));
                    c.setCourseDescription(rs.getString("course_description"));
                    c.setDurationMinutes(rs.getInt("duration_minutes"));
                    courses.add(c);
                }
            }
        }
        return courses;
    }
    
  
    // 관리자페이지에서 예약자현환시 사용하는 메소드 권성애 2026.10.17토
    // =========================================================
    // 주제: adminGuideReservationAll 처리
    // 설명: adminGuideReservationAll 기능에 필요한 처리 로직을 수행
    // =========================================================
    public List<AdminReservationDto> adminGuideReservationAll(String startDate, String endDate, String status
    		, int offset, int limit)
            throws SQLException {
 
        List<AdminReservationDto> list = new ArrayList<>();
 
        boolean useStatus = status != null && !status.isEmpty();
 
        String sql =
              "SELECT o.order_id, o.member_id, o.reservation_date, o.people_count, o.amount, "
            + "       o.status, o.payment_method, o.approved_at, o.created_at, "
            + "       r.name AS member_name, r.hp AS member_hp, "
            + "       g.guide_name, g.guide_phone, g.region_name "
            + "FROM toss_test_order o "
            + "LEFT JOIN register r ON o.member_id = r.id "      // 탈퇴 회원이어도 예약은 보이게
            + "JOIN guide g ON o.guide_id = g.guide_id "
            + "WHERE o.reservation_date >= ? AND o.reservation_date < ? "
            + (useStatus ? "AND o.status = ? " : "")
            + "ORDER BY o.reservation_date ASC, o.created_at ASC "
            + "LIMIT ? OFFSET ?";
 
        // ↓ 기존 DAO 에서 쓰는 커넥션 얻는 코드로 바꾸세요
        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
        	 int idx = 1;
            pstmt.setString(idx++, startDate);
            pstmt.setString(idx++, endDate);
            if (useStatus) {
                pstmt.setString(idx++, status);
            }
            pstmt.setInt(idx++, limit);                 // ← 추가 : 몇 개
            pstmt.setInt(idx++, offset); 
 
            System.out.println("SQL : " + sql);
            System.out.println("startDate=" + startDate + ", endDate=" + endDate + ", status=" + status);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    AdminReservationDto dto = new AdminReservationDto();
                    dto.setOrderId(rs.getString("order_id"));
                    dto.setMemberId(rs.getString("member_id"));
                    dto.setReservationDate(rs.getDate("reservation_date"));
                    dto.setPeopleCount(rs.getInt("people_count"));
                    dto.setAmount(rs.getLong("amount"));
                    dto.setStatus(rs.getString("status"));
                    dto.setPaymentMethod(rs.getString("payment_method"));
                    dto.setApprovedAt(rs.getString("approved_at"));
                    dto.setCreatedAt(rs.getTimestamp("created_at"));
                    dto.setMemberName(rs.getString("member_name"));
                    dto.setMemberHp(rs.getString("member_hp"));
                    dto.setGuideName(rs.getString("guide_name"));
                    dto.setGuidePhone(rs.getString("guide_phone"));
                    dto.setRegionName(rs.getString("region_name"));
                    
                    System.out.println("[GuideReservationDao] adminGuideReservationAll() AdminReservationDto "+dto);
                    list.add(dto);
                }
            }catch(SQLException e) {
            	throw new SQLException("[GuideReservationDao] adminGuideReservationAll() 예외발생 "+e);
            }
        }
 
        return list;
    }
 
    /**
     * 같은 기간의 상태별 건수
     * 돌려주는 값 예) {"ALL"=12, "PAID"=7, "PENDING"=3, "CANCELED"=2}
     * (상태 필터를 눌러도 다른 버튼 숫자가 그대로 보이도록 따로 셈)
     */
    public Map<String, Integer> guideReservationStatusCount(String startDate, String endDate)
            throws SQLException {
 
        Map<String, Integer> countMap = new HashMap<>();
        countMap.put("ALL", 0);
        countMap.put("PAID", 0);
        countMap.put("PENDING", 0);
        countMap.put("CANCELED", 0);
 
        String sql =
              "SELECT status, COUNT(*) AS cnt "
            + "FROM toss_test_order "
            + "WHERE reservation_date >= ? AND reservation_date < ? "
            + "GROUP BY status";
 
        // ↓ 기존 DAO 에서 쓰는 커넥션 얻는 코드로 바꾸세요
        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
 
            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);
 
            try (ResultSet rs = pstmt.executeQuery()) {
                int total = 0;
                while (rs.next()) {
                    String st = rs.getString("status");
                    int cnt = rs.getInt("cnt");
                    countMap.put(st, cnt);
                    total += cnt;
                }
                countMap.put("ALL", total);
            }
        }catch(SQLException e) {
        	throw new SQLException("[GuideReservationDao] guideReservationStatusCount() 예외발생 "+e);
        }
        System.out.println("[GuideReservationDao] guideReservationStatusCount()  Map<String, Integer> "+countMap);
        //Map<String, Integer> {ALL=3, CANCELED=0, PAID=0, PENDING=3}

        return countMap;
    }
    
    
}
