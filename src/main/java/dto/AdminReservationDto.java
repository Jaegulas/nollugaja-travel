package dto;

import java.sql.Date;
import java.sql.Timestamp;

// =========================================================
// 주제: 파일 역할
// 설명: AdminReservationDto 화면/계층 사이에서 필요한 데이터를 전달하는 DTO
// =========================================================


public class AdminReservationDto {

    // ----- toss_test_order -----
    private String orderId;          // 주문번호
    private String memberId;         // 예약자 ID
    private Date reservationDate;    // 투어일
    private int peopleCount;         // 인원
    private long amount;             // 금액
    private String status;           // PAID / PENDING / CANCELED
    private String paymentMethod;    // 결제수단
    private String approvedAt;       // 결제일시 (토스에서 받은 문자열) 토스 형식(2026-10-12T14:22:10+09:00)이라고 가정하고 앞 16자리를 잘라 표시
    private Timestamp createdAt;     // 예약일시
 
    // ----- register (예약자) -----
    private String memberName;       // 예약자 이름
    private String memberHp;         // 예약자 연락처
 
    // ----- guide -----
    private String guideName;        // 가이드 이름
    private String guidePhone;       // 가이드 연락처
    private String regionName;       // 가이드 지역
 
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
 
    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }
 
    public Date getReservationDate() { return reservationDate; }
    public void setReservationDate(Date reservationDate) { this.reservationDate = reservationDate; }
 
    public int getPeopleCount() { return peopleCount; }
    public void setPeopleCount(int peopleCount) { this.peopleCount = peopleCount; }
 
    public long getAmount() { return amount; }
    public void setAmount(long amount) { this.amount = amount; }
 
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
 
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
 
    public String getApprovedAt() { return approvedAt; }
    public void setApprovedAt(String approvedAt) { this.approvedAt = approvedAt; }
 
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
 
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
 
    public String getMemberHp() { return memberHp; }
    public void setMemberHp(String memberHp) { this.memberHp = memberHp; }
 
    public String getGuideName() { return guideName; }
    public void setGuideName(String guideName) { this.guideName = guideName; }
 
    public String getGuidePhone() { return guidePhone; }
    public void setGuidePhone(String guidePhone) { this.guidePhone = guidePhone; }
 
    public String getRegionName() { return regionName; }
    public void setRegionName(String regionName) { this.regionName = regionName; }
	@Override
	public String toString() {
		return "AdminReservationDto [orderId=" + orderId + ", memberId=" + memberId + ", reservationDate="
				+ reservationDate + ", peopleCount=" + peopleCount + ", amount=" + amount + ", status=" + status
				+ ", paymentMethod=" + paymentMethod + ", approvedAt=" + approvedAt + ", createdAt=" + createdAt
				+ ", memberName=" + memberName + ", memberHp=" + memberHp + ", guideName=" + guideName + ", guidePhone="
				+ guidePhone + ", regionName=" + regionName + "]";
	}
	public AdminReservationDto() {
		super();
	}



}
