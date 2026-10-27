package dto;

// =========================================================
// 주제: 파일 역할
// 설명: GuideTourDto 화면/계층 사이에서 필요한 데이터를 전달하는 DTO
// =========================================================


public class GuideTourDto {
    private int tourId;
    private int guideId;
    private String tourTitle;
    private String duration;
    private String description;
    private String included;
    private String excluded;
    private String cancellationPolicy;

    public int getTourId() { return tourId; }
    public void setTourId(int value) { this.tourId = value; }
    public int getGuideId() { return guideId; }
    public void setGuideId(int value) { this.guideId = value; }
    public String getTourTitle() { return tourTitle; }
    public void setTourTitle(String value) { this.tourTitle = value; }
    public String getDuration() { return duration; }
    public void setDuration(String value) { this.duration = value; }
    public String getDescription() { return description; }
    public void setDescription(String value) { this.description = value; }
    public String getIncluded() { return included; }
    public void setIncluded(String value) { this.included = value; }
    public String getExcluded() { return excluded; }
    public void setExcluded(String value) { this.excluded = value; }
    public String getCancellationPolicy() { return cancellationPolicy; }
    public void setCancellationPolicy(String value) { this.cancellationPolicy = value; }
}
