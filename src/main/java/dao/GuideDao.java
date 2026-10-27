package dao;

import java.sql.*;
import java.util.*;
import util.DBCPUtil;
import vo.GuideVO;

// =========================================================
// 주제: 파일 역할
// 설명: GuideDao 관련 데이터베이스 조회·저장·수정·삭제를 담당하는 DAO
// =========================================================


public class GuideDao {
    private String where(String region, String keyword, List<String> params) {
        StringBuilder sql = new StringBuilder(" WHERE 1=1");
        if (region != null && !region.isBlank()) {
            sql.append(" AND region_name = ?");
            params.add(region);
        }
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (guide_name LIKE ? OR specialty LIKE ? OR guide_intro LIKE ?)");
            String like = "%" + keyword.trim() + "%";
            params.add(like); params.add(like); params.add(like);
        }
        return sql.toString();
    }

    // =========================================================
    // 주제: count 처리
    // 설명: count 기능에 필요한 처리 로직을 수행
    // =========================================================
    public int count(String region, String keyword) throws SQLException {
        List<String> params = new ArrayList<>();
        String sql = "SELECT COUNT(*) FROM guide" + where(region, keyword, params);
        try (Connection conn = DBCPUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) ps.setString(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : 0; }
        }
    }

    // =========================================================
    // 주제: findPage 처리
    // 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
    // =========================================================
    public List<GuideVO> findPage(String region, String keyword, int page, int size) throws SQLException {
        List<String> params = new ArrayList<>();
        String sql = "SELECT guide_id, guide_name, guide_image, guide_intro, guide_phone, region_id, " +
            "region_name, specialty, tags, price, rating, review_count, tour_count FROM guide" +
            where(region, keyword, params) + " ORDER BY guide_id ASC LIMIT ? OFFSET ?";
        List<GuideVO> result = new ArrayList<>();
        try (Connection conn = DBCPUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            int index = 1;
            for (String param : params) ps.setString(index++, param);
            ps.setInt(index++, size);
            ps.setInt(index, (page - 1) * size);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    GuideVO g = new GuideVO();
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
                    g.setRating(rs.getDouble("rating"));
                    g.setReviewCount(rs.getInt("review_count"));
                    g.setTourCount(rs.getInt("tour_count"));
                    result.add(g);
                }
            }
        }
        return result;
    }
}
