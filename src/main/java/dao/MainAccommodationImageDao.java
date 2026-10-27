
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import dao.MainAccommodationImageDao;

// =========================================================
// 주제: 메인 화면 숙소 사진 전용 DAO
// 설명: accommodation 테이블에서 사진 경로만 조회
// 기존 팀원 DAO 및 Service는 수정하지 않는다.
// =========================================================

public class MainAccommodationImageDao {

    // 기존 프로젝트의 JNDI 커넥션풀 사용
    private Connection getConnection()
            throws SQLException {

        try {
            InitialContext context =
                    new InitialContext();

            DataSource dataSource =
                    (DataSource) context.lookup(
                        "java:comp/env/jdbc/travel"
                    );

            return dataSource.getConnection();

        } catch (NamingException e) {

            throw new SQLException(
                "숙소 사진 DB 연결 실패", e
            );
        }
    }

    // =====================================================
    // 숙소 ID 목록을 받아 사진 경로를 한 번에 조회
    // 결과: 숙소 ID -> 사진 URL
    // =====================================================
    public Map<Integer, String> getImagePaths(
            List<Integer> accommodationIds)
            throws SQLException {

        Map<Integer, String> imagePaths =
                new HashMap<>();

        if (accommodationIds == null
                || accommodationIds.isEmpty()) {

            return imagePaths;
        }

        // IN (?, ?, ?, ?) 생성
        String placeholders =
                accommodationIds.stream()
                .map(id -> "?")
                .collect(Collectors.joining(", "));

        String sql =
                "SELECT accommodation_id, picpath "
                + "FROM accommodation "
                + "WHERE accommodation_id IN ("
                + placeholders + ")";

        try (
            Connection conn = getConnection();
            PreparedStatement pstmt =
                    conn.prepareStatement(sql)
        ) {

            // 숙소 ID를 SQL 파라미터로 전달
            for (int i = 0;
                    i < accommodationIds.size();
                    i++) {

                pstmt.setInt(
                    i + 1,
                    accommodationIds.get(i)
                );
            }

            try (ResultSet rs =
                    pstmt.executeQuery()) {

                while (rs.next()) {

                    int accommodationId =
                            rs.getInt("accommodation_id");

                    String picpath =
                            rs.getString("picpath");

                    imagePaths.put(
                        accommodationId,
                        picpath
                    );
                }
            }
        }

        return imagePaths;
    }
}
