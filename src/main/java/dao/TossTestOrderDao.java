package dao;

import util.DBCPUtil;
import vo.TossTestOrder;

import java.sql.*;

// =========================================================
// 주제: 파일 역할
// 설명: TossTestOrderDao 관련 데이터베이스 조회·저장·수정·삭제를 담당하는 DAO
// =========================================================


public class TossTestOrderDao {
    public void insert(TossTestOrder o) throws SQLException {
        String sql = "INSERT INTO toss_test_order " +
                "(order_id,guide_id,reservation_date,people_count,amount,status) VALUES (?,?,?,?,?,'PENDING')";
        try (Connection c = DBCPUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, o.getOrderId());
            p.setInt(2, o.getGuideId());
            p.setDate(3, Date.valueOf(o.getReservationDate()));
            p.setInt(4, o.getPeopleCount());
            p.setLong(5, o.getAmount());
            p.executeUpdate();
        }
    }
    // 로그인한 회원의 ID를 함께 저장하는 예약 주문 생성
    // =========================================================
    // 주제: insert 처리
    // 설명: 입력받은 데이터를 생성하거나 저장
    // =========================================================
    public void insert(TossTestOrder o, String memberId) throws SQLException {
        if (memberId == null || memberId.isBlank()) {
            throw new IllegalArgumentException("로그인 회원 ID가 없습니다.");
        }
        String sql = "INSERT INTO toss_test_order " +
                "(order_id,member_id,guide_id,reservation_date,people_count,amount,status) " +
                "VALUES (?,?,?,?,?,?,'PENDING')";
        try (Connection c = DBCPUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, o.getOrderId());
            p.setString(2, memberId);
            p.setInt(3, o.getGuideId());
            p.setDate(4, Date.valueOf(o.getReservationDate()));
            p.setInt(5, o.getPeopleCount());
            p.setLong(6, o.getAmount());
            p.executeUpdate();
        }
    }

 // 동일 가이드·날짜의 활성 예약 존재 여부
    // =========================================================
    // 주제: isAlreadyBooked 처리
    // 설명: 입력값 또는 현재 상태가 조건에 맞는지 검사
    // =========================================================
    public boolean isAlreadyBooked(int guideId, String date)
            throws SQLException {

        String sql =
            "SELECT COUNT(*) FROM toss_test_order " +
            "WHERE guide_id = ? " +
            "AND reservation_date = ? " +
            "AND status IN ('PENDING','CONFIRMING','PAID','REVIEW')";

        try (Connection con = DBCPUtil.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, guideId);
            pstmt.setDate(2, Date.valueOf(date));

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
    // =========================================================
    // 주제: find 처리
    // 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
    // =========================================================
    public TossTestOrder find(String id) throws SQLException {
        String sql = "SELECT order_id,guide_id,reservation_date,people_count,amount,status FROM toss_test_order WHERE order_id=?";
        try (Connection c = DBCPUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, id);
            try (ResultSet r = p.executeQuery()) {
                if (!r.next()) return null;
                return new TossTestOrder(r.getString(1), r.getInt(2), r.getDate(3).toString(),
                        r.getInt(4), r.getLong(5), r.getString(6));
            }
        }
    }
    // 상태 전이는 조건부 UPDATE로 보호: 동시 승인 시 한 요청만 처리
    // =========================================================
    // 주제: claim 처리
    // 설명: claim 기능에 필요한 처리 로직을 수행
    // =========================================================
    public boolean claim(String id) throws SQLException {
        try (Connection c = DBCPUtil.getConnection(); PreparedStatement p = c.prepareStatement(
                "UPDATE toss_test_order SET status='CONFIRMING' WHERE order_id=? AND status='PENDING'")) {
            p.setString(1, id);
            return p.executeUpdate() == 1;
        }
    }
    // =========================================================
    // 주제: paid 처리
    // 설명: paid 기능에 필요한 처리 로직을 수행
    // =========================================================
    public void paid(String id, String paymentKey, String method, String approvedAt) throws SQLException {
        try (Connection c = DBCPUtil.getConnection(); PreparedStatement p = c.prepareStatement(
                "UPDATE toss_test_order SET status='PAID', payment_key=?,payment_method=?,approved_at=? WHERE order_id=? AND status='CONFIRMING'")) {
            p.setString(1, paymentKey);p.setString(2, method);p.setString(3, approvedAt);p.setString(4,id);
            if (p.executeUpdate()!=1) throw new SQLException("주문 상태 변경 실패: " + id);
        }
    }
    // =========================================================
    // 주제: markReview 처리
    // 설명: markReview 기능에 필요한 처리 로직을 수행
    // =========================================================
    public void markReview(String id) throws SQLException {
        try (Connection c = DBCPUtil.getConnection(); PreparedStatement p = c.prepareStatement(
                "UPDATE toss_test_order SET status='REVIEW' WHERE order_id=? AND status='CONFIRMING'")) {
            p.setString(1,id);p.executeUpdate();
        }
    }
}
