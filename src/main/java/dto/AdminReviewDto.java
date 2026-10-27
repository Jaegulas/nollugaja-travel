package dto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import vo.ReviewCommentVo;

// =========================================================
// 주제: 파일 역할
// 설명: AdminReviewDto 화면/계층 사이에서 필요한 데이터를 전달하는 DTO
// =========================================================


public class AdminReviewDto {
	
	private long reviewId;
    private String memberId;
    private String category;
    private String title;
    private String content;
    private String authorNickname;
    private int viewCount;
    private Timestamp createdAt;

    // 이 글에 달린 댓글 목록 (화면용)
    private List<ReviewCommentVo> commentList = new ArrayList<>();

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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAuthorNickname() {
		return authorNickname;
	}

	public void setAuthorNickname(String authorNickname) {
		this.authorNickname = authorNickname;
	}

	public int getViewCount() {
		return viewCount;
	}

	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public List<ReviewCommentVo> getCommentList() {
		return commentList;
	}

	public void setCommentList(List<ReviewCommentVo> commentList) {
		this.commentList = commentList;
	}

	public AdminReviewDto(long reviewId, String memberId, String category, String title, String content,
			String authorNickname, int viewCount, Timestamp createdAt, List<ReviewCommentVo> commentList) {
		super();
		this.reviewId = reviewId;
		this.memberId = memberId;
		this.category = category;
		this.title = title;
		this.content = content;
		this.authorNickname = authorNickname;
		this.viewCount = viewCount;
		this.createdAt = createdAt;
		this.commentList = commentList;
	}

	public AdminReviewDto() {
		super();
	}

	    
	    

}
