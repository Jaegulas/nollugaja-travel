package vo;

// =========================================================
// 주제: 파일 역할
// 설명: CulturalHeritageVo 관련 값을 보관하고 전달하는 VO
// =========================================================


public class CulturalHeritageVo {

	private String contentId;
	private String title;
	private String addr1;
	private String firstImage;
	private String mapX;
	private String mapY;
	
	// DB 저장용 지역/콘텐츠 코드
	private String areaCode;
	private String sigunguCode;
	private String contentTypeId;


	// 운영시간
	private String useTime;

	// 휴무일
	private String restDate;

	// 주차정보
	private String parking;

	// 문의 및 안내
	private String infoCenter;

	public CulturalHeritageVo() {
	}

	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAddr1() {
		return addr1;
	}

	public void setAddr1(String addr1) {
		this.addr1 = addr1;
	}

	public String getFirstImage() {
		return firstImage;
	}

	public void setFirstImage(String firstImage) {
		this.firstImage = firstImage;
	}

	public String getMapX() {
		return mapX;
	}

	public void setMapX(String mapX) {
		this.mapX = mapX;
	}

	public String getMapY() {
		return mapY;
	}

	public void setMapY(String mapY) {
		this.mapY = mapY;
	}

	public String getAreaCode() {
	    return areaCode;
	}

	public void setAreaCode(String areaCode) {
	    this.areaCode = areaCode;
	}

	public String getSigunguCode() {
	    return sigunguCode;
	}

	public void setSigunguCode(String sigunguCode) {
	    this.sigunguCode = sigunguCode;
	}

	public String getContentTypeId() {
	    return contentTypeId;
	}

	public void setContentTypeId(String contentTypeId) {
	    this.contentTypeId = contentTypeId;
	}
	
	// ========================================
	// 운영시간
	// ========================================

	public String getUseTime() {
		return useTime;
	}

	public void setUseTime(String useTime) {
		this.useTime = useTime;
	}

	// ========================================
	// 휴무일
	// ========================================

	public String getRestDate() {
		return restDate;
	}

	public void setRestDate(String restDate) {
		this.restDate = restDate;
	}

	// ========================================
	// 주차정보
	// ========================================

	public String getParking() {
		return parking;
	}

	public void setParking(String parking) {
		this.parking = parking;
	}

	// ========================================
	// 문의 및 안내
	// ========================================

	public String getInfoCenter() {
		return infoCenter;
	}

	public void setInfoCenter(String infoCenter) {
		this.infoCenter = infoCenter;
	}
}
