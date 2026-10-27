package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import util.DBCPUtil;
import vo.ReviewCommentVo;

// =========================================================
// 주제: 파일 역할
// 설명: ReviewCommentDao 관련 데이터베이스 조회·저장·수정·삭제를 담당하는 DAO
// =========================================================


public class ReviewCommentDao {

    // =========================================================
    // 주제: getComments 처리
    // 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
    // =========================================================
    public List<ReviewCommentVo> getComments(long reviewId)
            throws SQLException {

        List<ReviewCommentVo> comments = new ArrayList<>();

        String sql =
            "SELECT comment_id, review_id, member_id, "
          + "author_nickname, content, created_at, updated_at "
          + "FROM review_comment "
          + "WHERE review_id = ? "
          + "ORDER BY created_at ASC, comment_id ASC";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, reviewId);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {

                    ReviewCommentVo comment = new ReviewCommentVo();

                    comment.setCommentId(rs.getLong("comment_id"));
                    comment.setReviewId(rs.getLong("review_id"));
                    comment.setMemberId(rs.getString("member_id"));
                    comment.setAuthorNickname(
                        rs.getString("author_nickname")
                    );
                    comment.setContent(rs.getString("content"));
                    comment.setCreatedAt(rs.getTimestamp("created_at"));
                    comment.setUpdatedAt(rs.getTimestamp("updated_at"));

                    comments.add(comment);
                }
            }
        }

        return comments;
    }

    // =========================================================
    // 주제: insertComment 처리
    // 설명: 입력받은 데이터를 생성하거나 저장
    // =========================================================
    public int insertComment(
            long reviewId,
            String memberId,
            String authorNickname,
            String content) throws SQLException {

        String sql =
            "INSERT INTO review_comment "
          + "(review_id, member_id, author_nickname, content) "
          + "SELECT review_id, ?, ?, ? "
          + "FROM review WHERE review_id = ?";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, memberId);
            pstmt.setString(2, authorNickname);
            pstmt.setString(3, content);
            pstmt.setLong(4, reviewId);

            return pstmt.executeUpdate();
        }
    }

    // ========================================
    // 3. 댓글 수정
    // 작성자 또는 관리자만 가능
    // ========================================
    // =========================================================
    // 주제: updateComment 처리
    // 설명: 기존 데이터를 입력값에 맞게 수정
    // =========================================================
    public int updateComment(
            long commentId,
            String memberId,
            boolean isAdmin,
            String content) throws SQLException {

        String sql = isAdmin
            ? "UPDATE review_comment "
              + "SET content = ? WHERE comment_id = ?"
            : "UPDATE review_comment "
              + "SET content = ? "
              + "WHERE comment_id = ? AND member_id = ?";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, content);
            pstmt.setLong(2, commentId);

            if (!isAdmin) {
                pstmt.setString(3, memberId);
            }

            return pstmt.executeUpdate();
        }
    }

    // ========================================
    // 4. 댓글 삭제
    // 작성자 또는 관리자만 가능
    // ========================================
    // =========================================================
    // 주제: deleteComment 처리
    // 설명: 대상 데이터를 삭제하거나 제거
    // =========================================================
    public int deleteComment(
            long commentId,
            String memberId,
            boolean isAdmin) throws SQLException {

        String sql = isAdmin
            ? "DELETE FROM review_comment WHERE comment_id = ?"
            : "DELETE FROM review_comment "
              + "WHERE comment_id = ? AND member_id = ?";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, commentId);

            if (!isAdmin) {
                pstmt.setString(2, memberId);
            }

            return pstmt.executeUpdate();
        }
    }
}
