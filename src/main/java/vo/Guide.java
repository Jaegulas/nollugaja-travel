package vo;

import java.math.BigDecimal;

// =========================================================
// 주제: 파일 역할
// 설명: Guide 관련 값을 보관하고 전달하는 VO
// =========================================================


public class Guide {
    private int guideId;
    private String guideName, guideImage, guideIntro, guidePhone, regionName, specialty, tags;
    private int regionId, price, reviewCount, tourCount;
    private BigDecimal rating;

    public int getGuideId() { return guideId; }
    public void setGuideId(int v) { guideId = v; }
    public String getGuideName() { return guideName; }
    public void setGuideName(String v) { guideName = v; }
    public String getGuideImage() { return guideImage; }
    public void setGuideImage(String v) { guideImage = v; }
    public String getGuideIntro() { return guideIntro; }
    public void setGuideIntro(String v) { guideIntro = v; }
    public String getGuidePhone() { return guidePhone; }
    public void setGuidePhone(String v) { guidePhone = v; }
    public String getRegionName() { return regionName; }
    public void setRegionName(String v) { regionName = v; }
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String v) { specialty = v; }
    public String getTags() { return tags; }
    public void setTags(String v) { tags = v; }
    public int getRegionId() { return regionId; }
    public void setRegionId(int v) { regionId = v; }
    public int getPrice() { return price; }
    public void setPrice(int v) { price = v; }
    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int v) { reviewCount = v; }
    public int getTourCount() { return tourCount; }
    public void setTourCount(int v) { tourCount = v; }
    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal v) { rating = v; }
}
