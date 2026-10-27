package vo;

// =========================================================
// 주제: 파일 역할
// 설명: MypageLikeVo 관련 값을 보관하고 전달하는 VO
// =========================================================


public class MypageLikeVo {

	  private String type;     // acc / heritage / food / travel
	    private int id;          // 숙소·문화유산·음식 번호
	    private String name;     // 이름
	    private String addr;     // 주소
	    private String image;    // 사진 주소
	    private String url;      // 상세페이지 주소
	    private String likedAt;  // 찜한 날짜 "2026-10-14"
	 
	    public String getType() { return type; }
	    public void setType(String type) { this.type = type; }
	 
	    public int getId() { return id; }
	    public void setId(int id) { this.id = id; }
	 
	    public String getName() { return name; }
	    public void setName(String name) { this.name = name; }
	 
	    public String getAddr() { return addr; }
	    public void setAddr(String addr) { this.addr = addr; }
	 
	    public String getImage() { return image; }
	    public void setImage(String image) { this.image = image; }
	 
	    public String getUrl() { return url; }
	    public void setUrl(String url) { this.url = url; }
	 
	    public String getLikedAt() { return likedAt; }
	    public void setLikedAt(String likedAt) { this.likedAt = likedAt; }
}
