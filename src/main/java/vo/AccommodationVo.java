package vo;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

// =========================================================
// 주제: 파일 역할
// 설명: AccommodationVo 관련 값을 보관하고 전달하는 VO
// =========================================================


public class AccommodationVo {

	
	private Integer accommodation_id;
	private String placeid;
	private String name;
	private String addr;
	private String type;
	private String tel;
	private String link;
	
	private Double lat; //위도
	private Double lon; //경도
	
	//추가
	private String type2;
	private String contentid;
	
	private Double avg_rating;
	private Integer review_count;
	private Boolean isliked;
	private String picpath;
	
	//transient Gson이 날짜 필드를 건너뛰어요. 자바 -> json 으로 변환시
//	public class GsonUtil {
//	    private static final Gson GSON = new GsonBuilder()
//	        .registerTypeAdapter(LocalDateTime.class,
//	            (JsonSerializer<LocalDateTime>) (src, type, ctx) ->
//	                new JsonPrimitive(src.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))))
//	        .create();
//
//	    public static String toJson(Object obj) {
//	        return GSON.toJson(obj);
//	    }
//	}
	private transient  LocalDateTime upadated_at;
	
		
//	accommodation_id, placeid, name, addr, type, tel, link, lat, lon, avg_rating, review_count, isliked , picpath, updated_at - vo
//	type2 contentid 추가됨

	


		public AccommodationVo() {
			super();
		}

		@Override
		public String toString() {
			return "AccommodationVo [accommodation_id=" + accommodation_id + ", placeid=" + placeid + ", name=" + name
					+ ", addr=" + addr + ", type=" + type + ", tel=" + tel + ", link=" + link + ", lat=" + lat
					+ ", lon=" + lon + ", type2=" + type2 + ", contentid=" + contentid + ", avg_rating=" + avg_rating
					+ ", review_count=" + review_count + ", isliked=" + isliked + ", picpath=" + picpath
					+ ", upadated_at=" + upadated_at + "]";
		}

		public AccommodationVo(Integer accommodation_id, String placeid, String name, String addr, String type,
				String tel, String link, Double lat, Double lon, Double avg_rating, Integer review_count, Boolean isliked,
				String picpath, LocalDateTime upadated_at) {
			super();
			this.accommodation_id = accommodation_id;
			this.placeid = placeid;
			this.name = name;
			this.addr = addr;
			this.type = type;
			this.tel = tel;
			this.link = link;
			this.lat = lat;
			this.lon = lon;
			this.avg_rating = avg_rating;
			this.review_count = review_count;
			this.isliked = isliked;
			this.picpath = picpath;
			this.upadated_at = upadated_at;
			this.type2 =type2;
			this.contentid = contentid;
		}


		

		public AccommodationVo(String placeid, String name, String addr, String type, String tel, String link,
				Double lat, Double lon, String picpath, String type2, String content) {
			super();
			this.placeid = placeid;
			this.name = name;
			this.addr = addr;
			this.type = type;
			this.tel = tel;
			this.link = link;
			this.lat = lat;
			this.lon = lon;
			this.picpath = picpath;
			this.type2 =type2;
			this.contentid = contentid;
		}
		
		

		public String getType2() {
			return type2;
		}

		public void setType2(String type2) {
			this.type2 = type2;
		}

		public String getContentid() {
			return contentid;
		}

		public void setContentid(String contentid) {
			this.contentid = contentid;
		}

		public Boolean getIsliked() {
			return isliked;
		}

		public Integer getAccommodation_id() {
			return accommodation_id;
		}

		public void setAccommodation_id(Integer accommodation_id) {
			this.accommodation_id = accommodation_id;
		}

		public String getPlaceid() {
			return placeid;
		}

		public void setPlaceid(String placeid) {
			this.placeid = placeid;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAddr() {
			return addr;
		}

		public void setAddr(String addr) {
			this.addr = addr;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getTel() {
			return tel;
		}

		public void setTel(String tel) {
			this.tel = tel;
		}

		public String getLink() {
			return link;
		}

		public void setLink(String link) {
			this.link = link;
		}

		public Double getLat() {
			return lat;
		}

		public void setLat(Double lat) {
			this.lat = lat;
		}

		public Double getLon() {
			return lon;
		}

		public void setLon(Double lon) {
			this.lon = lon;
		}

		public Double getAvg_rating() {
			return avg_rating;
		}

		public void setAvg_rating(Double avg_rating) {
			this.avg_rating = avg_rating;
		}

		public Integer getReview_count() {
			return review_count;
		}

		public void setReview_count(Integer review_count) {
			this.review_count = review_count;
		}

		public boolean isIsliked() {
			return isliked;
		}

		public void setIsliked(Boolean isliked) {
			this.isliked = isliked;
		}

		public String getPicpath() {
			return picpath;
		}

		public void setPicpath(String picpath) {
			this.picpath = picpath;
		}

		public LocalDateTime getUpadated_at() {
			return upadated_at;
		}

		public void setUpadated_at(LocalDateTime upadated_at) {
			this.upadated_at = upadated_at;
		}
		
		public String getKakaoPlaceUrl() {
		    return placeid == null ? null : "https://place.map.kakao.com/" + placeid;
		}

		
		
}
