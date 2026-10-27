package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import util.DBCPUtil;

// =========================================================
// 주제: 파일 역할
// 설명: GuideLikeDAO 관련 데이터베이스 조회·저장·수정·삭제를 담당하는 DAO
// =========================================================


public class GuideLikeDAO {
    public List<Integer> findGuideIds(String memberId) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT guide_id FROM guide_like WHERE member_id = ?";
        try (Connection con = DBCPUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt(1));
            }
        }
        return ids;
    }
    // =========================================================
    // 주제: add 처리
    // 설명: 입력받은 데이터를 생성하거나 저장
    // =========================================================
    public void add(String memberId, int guideId) throws SQLException {
        String sql = "INSERT IGNORE INTO guide_like (member_id, guide_id) VALUES (?, ?)";
        try (Connection con = DBCPUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, memberId); ps.setInt(2, guideId); ps.executeUpdate();
        }
    }
    // =========================================================
    // 주제: remove 처리
    // 설명: 대상 데이터를 삭제하거나 제거
    // =========================================================
    public void remove(String memberId, int guideId) throws SQLException {
        String sql = "DELETE FROM guide_like WHERE member_id = ? AND guide_id = ?";
        try (Connection con = DBCPUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, memberId); ps.setInt(2, guideId); ps.executeUpdate();
        }
    }
    // =========================================================
    // 주제: exists 처리
    // 설명: exists 기능에 필요한 처리 로직을 수행
    // =========================================================
    public boolean exists(String memberId, int guideId) throws SQLException {
        String sql = "SELECT 1 FROM guide_like WHERE member_id = ? AND guide_id = ? LIMIT 1";
        try (Connection con = DBCPUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, memberId); ps.setInt(2, guideId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }
}
