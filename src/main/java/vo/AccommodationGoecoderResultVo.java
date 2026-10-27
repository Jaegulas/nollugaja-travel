package vo;

// =========================================================
// 주제: 파일 역할
// 설명: AccommodationGoecoderResultVo 관련 값을 보관하고 전달하는 VO
// =========================================================


public class AccommodationGoecoderResultVo {

	 	private Double lat;
	    private Double lon;
	    private Integer id;   // 주소 검색으로 찾은 경우 null
	    private String tel;
	    private String placeid;
	    
	    
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
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public String getTel() {
			return tel;
		}
		public void setTel(String tel) {
			this.tel = tel;
		}
		public String getPlaceid() {
			return placeid;
		}
		public void setPlaceid(String placeid) {
			this.placeid = placeid;
		}
		@Override
		public String toString() {
			return "AccommodationGoecoderResult [lat=" + lat + ", lon=" + lon + ", id=" + id + ", tel=" + tel
					+ ", placeid=" + placeid + "]";
		}
		public AccommodationGoecoderResultVo(Double lat, Double lon, Integer id, String tel, String placeid) {
			super();
			this.lat = lat;
			this.lon = lon;
			this.id = id;
			this.tel = tel;
			this.placeid = placeid;
		}
}
