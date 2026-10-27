package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainAccommodationReviewDao {

    private final DataSource dataSource;

    public MainAccommodationReviewDao() throws NamingException {
        dataSource = (DataSource) new InitialContext()
                .lookup("java:comp/env/jdbc/travel");
    }

    // 숙소별 실제 리뷰 개수
    public Map<Integer, Integer> getReviewCounts(List<Integer> ids)
            throws SQLException {

        Map<Integer, Integer> result = new HashMap<>();

        if (ids == null || ids.isEmpty()) {
            return result;
        }

        String sql =
            "SELECT accommodation_id, COUNT(*) AS review_count " +
            "FROM accommodation_review " +
            "WHERE accommodation_id IN (" + placeholders(ids.size()) + ") " +
            "GROUP BY accommodation_id";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setIds(pstmt, ids);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.put(
                        rs.getInt("accommodation_id"),
                        rs.getInt("review_count")
                    );
                }
            }
        }

        return result;
    }

    // 숙소별 가장 최근 리뷰 내용 1개
    public Map<Integer, String> getLatestReviewContents(List<Integer> ids)
            throws SQLException {

        Map<Integer, String> result = new HashMap<>();

        if (ids == null || ids.isEmpty()) {
            return result;
        }

        String sql =
            "SELECT accommodation_id, content " +
            "FROM accommodation_review " +
            "WHERE accommodation_id IN (" + placeholders(ids.size()) + ") " +
            "ORDER BY created_at DESC, review_id DESC";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setIds(pstmt, ids);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("accommodation_id");
                    String content = rs.getString("content");

                    if (!result.containsKey(id)
                            && content != null
                            && !content.trim().isEmpty()) {
                    	String summary = summarizeReview(content);

                    	if (!summary.isEmpty()) {
                    	    result.put(id, summary);
                    	}
                    }
                }
            }
        }

        return result;
    }

    private String placeholders(int size) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append("?");
        }

        return sb.toString();
    }

    private void setIds(PreparedStatement pstmt, List<Integer> ids)
            throws SQLException {

        for (int i = 0; i < ids.size(); i++) {
            pstmt.setInt(i + 1, ids.get(i));
        }
    }
    
 // 리뷰 내용을 카드용으로 짧게 정리
    private String summarizeReview(String content) {

        if (content == null || content.trim().isEmpty()) {
            return "";
        }

        String summary = content.trim();

        // {"pros":["청결함","친절한 호스트"]} 형태 처리
        if (summary.startsWith("{")) {

            Pattern pattern = Pattern.compile(
                "\"pros\"\\s*:\\s*\\[\\s*\"((?:\\\\.|[^\"\\\\])*)\""
            );

            Matcher matcher = pattern.matcher(summary);

            if (matcher.find()) {
                summary = matcher.group(1)
                        .replace("\\\"", "\"")
                        .replace("\\n", " ")
                        .replace("\\\\", "\\");
            } else {
                // 구조를 알 수 없는 JSON은 그대로 노출하지 않음
                return "";
            }
        }

        summary = summary.replaceAll("\\s+", " ").trim();

        if (summary.length() > 45) {
            summary = summary.substring(0, 45) + "...";
        }

        return summary;
    }
}