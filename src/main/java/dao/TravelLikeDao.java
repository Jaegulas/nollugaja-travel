package dao;

import java.sql.*;
import java.util.*;
import util.DBCPUtil;

// =========================================================
// 주제: 파일 역할
// 설명: TravelLikeDao 관련 데이터베이스 조회·저장·수정·삭제를 담당하는 DAO
// =========================================================


public class TravelLikeDao {
    public Set<String> getLikedContentIds(String memberId) throws SQLException {
        Set<String> ids = new HashSet<>();
        String sql = "SELECT content_id FROM travel_like WHERE member_id=?";
        try (Connection con = DBCPUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getString(1));
            }
        }
        return ids;
    }

    // 원하는 상태를 명시적으로 지정: 중복 요청에도 토글 오작동 방지
    // =========================================================
    // 주제: setLiked 처리
    // 설명: 객체의 값을 설정
    // =========================================================
    public boolean setLiked(String memberId, String contentId, boolean liked) throws SQLException {
        String sql = liked
            ? "INSERT INTO travel_like(member_id,content_id) VALUES(?,?) " +
              "ON DUPLICATE KEY UPDATE content_id=VALUES(content_id)"
            : "DELETE FROM travel_like WHERE member_id=? AND content_id=?";
        try (Connection con = DBCPUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, memberId);
            ps.setString(2, contentId);
            ps.executeUpdate();
        }
        return liked;
    }
}
