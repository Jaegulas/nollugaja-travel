package vo;

// =========================================================
// 주제: 파일 역할
// 설명: TossTestOrder 관련 값을 보관하고 전달하는 VO
// =========================================================


public class TossTestOrder {
    private final String orderId;
    private final int guideId;
    private final String reservationDate;
    private final int peopleCount;
    private final long amount;
    private final String status;

    public TossTestOrder(String orderId, int guideId, String reservationDate,
                         int peopleCount, long amount, String status) {
        this.orderId = orderId;
        this.guideId = guideId;
        this.reservationDate = reservationDate;
        this.peopleCount = peopleCount;
        this.amount = amount;
        this.status = status;
    }
    public String getOrderId() { return orderId; }
    public int getGuideId() { return guideId; }
    public String getReservationDate() { return reservationDate; }
    public int getPeopleCount() { return peopleCount; }
    public long getAmount() { return amount; }
    public String getStatus() { return status; }
}
