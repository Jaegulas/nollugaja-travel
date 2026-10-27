package vo;

import java.time.LocalDateTime;

// =========================================================
// 주제: 파일 역할
// 설명: ItineraryJsonVo 관련 값을 보관하고 전달하는 VO
// =========================================================


public class ItineraryJsonVo {

//	CREATE TABLE itineraryjson (
//	  planid    INT AUTO_INCREMENT PRIMARY KEY,
//	  memberid  VARCHAR(50) NOT NULL,
//	  title      VARCHAR(200),
//	  planjson  LONGTEXT NOT NULL,          -- AI가 준 data 통째로
//	  sharekey  VARCHAR(36) UNIQUE,         -- 공유용 랜덤 키 (UUID)
//	  created_at DATETIME DEFAULT NOW()
//	);
	private Integer planid;
	private String memberid;
	private String title;
	private String planjson;
	private String sharekey;
	private LocalDateTime created_at;

	
	//planid  memberid memberid planjson sharekey created_at
	//JSTL의 <fmt:formatDate>는 LocalDateTime을 못 받아요. 에러가 나요. VO에 출력용 getter를 하나 추가하는 게 가장 간단해요.
	public String getCreatedDate() {
	    if (created_at == null) return "";
	    return created_at.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
	}
	
	public Integer getPlanid() {
		return planid;
	}
	public void setPlanid(Integer planid) {
		this.planid = planid;
	}
	public String getMemberid() {
		return memberid;
	}
	public void setMemberid(String memberid) {
		this.memberid = memberid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPlanjson() {
		return planjson;
	}
	public void setPlanjson(String planjson) {
		this.planjson = planjson;
	}
	public String getSharekey() {
		return sharekey;
	}
	public void setSharekey(String sharekey) {
		this.sharekey = sharekey;
	}
	public LocalDateTime getCreated_at() {
		return created_at;
	}
	public void setCreated_at(LocalDateTime created_at) {
		this.created_at = created_at;
	}
	public ItineraryJsonVo(Integer planid, String memberid, String title, String planjson, String sharekey,
			LocalDateTime created_at) {
		super();
		this.planid = planid;
		this.memberid = memberid;
		this.title = title;
		this.planjson = planjson;
		this.sharekey = sharekey;
		this.created_at = created_at;
	}
	@Override
	public String toString() {
		return "ItineraryJsonVo [planid=" + planid + ", memberid=" + memberid + ", title=" + title + ", planjson="
				+ planjson + ", sharekey=" + sharekey + ", created_at=" + created_at + "]";
	}
	public ItineraryJsonVo() {
		super();
	}
}
