package dto;   // ★ 패키지명은 프로젝트에 맞게 바꾸세요

import java.util.ArrayList;
import java.util.List;

// =========================================================
// 주제: 파일 역할
// 설명: MypageReviewDto 화면/계층 사이에서 필요한 데이터를 전달하는 DTO
// =========================================================


/**
 * 마이페이지 "내 리뷰" 화면에 보여줄 리뷰 1개
 *
 * ReviewVo 와 다른 점
 *   - ReviewVo   : review 테이블 한 줄 그대로 (DB 저장/조회용)
 *   - MyReviewDTO: 화면에 필요한 것만 + 다른 테이블에서 가져온 값
 *       likeCount    ← review_like    개수
 *       commentCount ← review_comment 개수
 *       images       ← review_image   사진 주소들 (image_order 순서)
 *       createdAt    ← "2026-10-11" 글자로 (JS 에서 바로 찍을 수 있게)
 *
 * ★ createdAt 을 Timestamp 가 아니라 String 으로 둔 이유
 *   Timestamp 를 그대로 JSON 으로 바꾸면 1791234567000 같은 숫자로 나가요.
 *   DAO 에서 SQL 로 "yyyy-MM-dd" 글자로 만들어 담습니다.
 */
public class MypageReviewDto {

    private long reviewId;
    private String category;
    private String title;
    private String content;
    private int viewCount;
    private String createdAt;          // "2026-10-11"

    private int likeCount;             // 좋아요 수
    private int commentCount;          // 댓글 수
    private List<String> images = new ArrayList<>();   // 사진 없으면 빈 목록 []

    public long getReviewId() { return reviewId; }
    public void setReviewId(long reviewId) { this.reviewId = reviewId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public int getCommentCount() { return commentCount; }
    public void setCommentCount(int commentCount) { this.commentCount = commentCount; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
}
