package vo;

import java.sql.Timestamp;

// =========================================================
// 주제: 파일 역할
// 설명: ReviewCommentVo 관련 값을 보관하고 전달하는 VO
// =========================================================


public class ReviewCommentVo {

    // ========================================
    // DB : review_comment 테이블 컬럼
    // ========================================

    private long commentId;          // 댓글 고유번호
    private long reviewId;           // 댓글이 속한 게시글 번호
    private String memberId;         // 댓글 작성자 회원 ID
    private String authorNickname;   // 댓글 작성자 닉네임
    private String content;          // 댓글 내용
    private Timestamp createdAt;     // 댓글 작성일
    private Timestamp updatedAt;     // 댓글 수정일


    // ========================================
    // Getter / Setter
    // ========================================

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }

    public long getReviewId() {
        return reviewId;
    }

    public void setReviewId(long reviewId) {
        this.reviewId = reviewId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getAuthorNickname() {
        return authorNickname;
    }

    public void setAuthorNickname(String authorNickname) {
        this.authorNickname = authorNickname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
