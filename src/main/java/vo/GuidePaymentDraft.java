package vo;

import java.io.Serializable;

// =========================================================
// 주제: 파일 역할
// 설명: GuidePaymentDraft 관련 값을 보관하고 전달하는 VO
// =========================================================


public class GuidePaymentDraft implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int guideId;
    private final String reservationDate;
    private final int peopleCount;

    public GuidePaymentDraft(int guideId, String reservationDate, int peopleCount) {
        this.guideId = guideId;
        this.reservationDate = reservationDate;
        this.peopleCount = peopleCount;
    }
    public int getGuideId() { return guideId; }
    public String getReservationDate() { return reservationDate; }
    public int getPeopleCount() { return peopleCount; }
}
