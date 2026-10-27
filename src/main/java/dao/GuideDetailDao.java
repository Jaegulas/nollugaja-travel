package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import util.DBCPUtil;

// =========================================================
// 주제: 파일 역할
// 설명: GuideDetailDao 관련 데이터베이스 조회·저장·수정·삭제를 담당하는 DAO
// =========================================================


public class GuideDetailDao {
    public Map<String, Object> findById(int guideId) throws SQLException {
        String sql = "SELECT guide_id, guide_name, guide_image, guide_intro, "
                + "region_name, specialty, tags, price, rating, review_count, tour_count "
                + "FROM guide WHERE guide_id = ?";
        try (Connection con = DBCPUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, guideId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Map<String, Object> g = new HashMap<>();
                g.put("guideId", rs.getInt("guide_id"));
                g.put("guideName", rs.getString("guide_name"));
                g.put("guideImage", rs.getString("guide_image"));
                g.put("guideIntro", rs.getString("guide_intro"));
                g.put("regionName", rs.getString("region_name"));
                g.put("specialty", rs.getString("specialty"));
                g.put("tags", rs.getString("tags"));
                g.put("price", rs.getInt("price"));
                g.put("rating", rs.getDouble("rating"));
                g.put("reviewCount", rs.getInt("review_count"));
                g.put("tourCount", rs.getInt("tour_count"));
                return g;
            }
        }
    }
}
