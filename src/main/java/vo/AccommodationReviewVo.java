package vo;

import java.time.LocalDateTime;

// =========================================================
// 주제: 파일 역할
// 설명: AccommodationReviewVo 관련 값을 보관하고 전달하는 VO
// =========================================================


public class AccommodationReviewVo {

	private Integer review_id;
	private Integer accommodation_id;
	private Integer google_review_id;
	private Double rating;
	private String content;
	private LocalDateTime created_at;
	
	//review_id  accommodation_id google_review_id rating content
	

	

	public Integer getReview_id() {
		return review_id;
	}

	@Override
	public String toString() {
		return "AccommodationReviewVo [review_id=" + review_id + ", accommodation_id=" + accommodation_id
				+ ", google_review_id=" + google_review_id + ", rating=" + rating + ", content=" + content
				+ ", created_at=" + created_at + "]";
	}

	public void setReview_id(Integer review_id) {
		this.review_id = review_id;
	}

	public Integer getAccommodation_id() {
		return accommodation_id;
	}

	public void setAccommodation_id(Integer accommodation_id) {
		this.accommodation_id = accommodation_id;
	}

	public Integer getGoogle_review_id() {
		return google_review_id;
	}

	public void setGoogle_review_id(Integer google_review_id) {
		this.google_review_id = google_review_id;
	}

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public LocalDateTime getCreated_at() {
		return created_at;
	}

	public void setCreated_at(LocalDateTime created_at) {
		this.created_at = created_at;
	}

	public AccommodationReviewVo(Integer review_id, Integer accommodation_id, Integer google_review_id, Double rating,
			String content, LocalDateTime created_at) {
		super();
		this.review_id = review_id;
		this.accommodation_id = accommodation_id;
		this.google_review_id = google_review_id;
		this.rating = rating;
		this.content = content;
		this.created_at = created_at;
	}

	public AccommodationReviewVo() {
		super();
	}

	
	
	
}
