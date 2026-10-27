package dto;

// =========================================================
// 주제: 파일 역할
// 설명: TourDto 화면/계층 사이에서 필요한 데이터를 전달하는 DTO
// =========================================================


public class TourDto {

    // =========================================================
    // 주제: 관광지 기본 정보
    // 설명: TourAPI에서 받아온 관광지 데이터를 담는 필드
    // =========================================================

    private String content_id;
    private String title;
    private String category;
    private String address;
    private String image;
    private String content_type_id;


    // =========================================================
    // 주제: 위치 정보
    // 설명: 관광지의 위도, 경도, 현재 위치와의 거리
    // =========================================================

    private double latitude;
    private double longitude;
    private double distance;


    // =========================================================
    // Getter / Setter
    // 설명: JSP, Service에서 값을 꺼내거나 넣을 때 사용
    // =========================================================

    public String getContent_id() {
        return content_id;
    }

    public void setContent_id(String content_id) {
        this.content_id = content_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
    
    public String getContent_type_id() {
        return content_type_id;
    }

    public void setContent_type_id(String content_type_id) {
        this.content_type_id = content_type_id;
    }
}
