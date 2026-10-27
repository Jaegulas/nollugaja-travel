package vo;

import java.sql.Date;

// =========================================================
// 주제: 파일 역할
// 설명: ChatbotDayVo 관련 값을 보관하고 전달하는 VO
// =========================================================


public class ChatbotDayVo {
	
	  private int    chatbotid;  // chatbotid  INT  (chatbotrequest.id)
    private int    dayno;      // dayno      INT  (1, 2, 3 ...)
    private Date   tripdate;   // tripdate   DATE
    private String region;     // region     VARCHAR(10)
    private String stay;       // stay       VARCHAR(20) NULL  (마지막 날은 null)
    private String plan;       // plan       VARCHAR(100) NULL
 
    public ChatbotDayVo() { }
 
    public int getChatbotid() { return chatbotid; }
    public void setChatbotid(int chatbotid) { this.chatbotid = chatbotid; }
 
    public int getDayno() { return dayno; }
    public void setDayno(int dayno) { this.dayno = dayno; }
 
    public Date getTripdate() { return tripdate; }
    public void setTripdate(Date tripdate) { this.tripdate = tripdate; }
 
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
 
    public String getStay() { return stay; }
    public void setStay(String stay) { this.stay = stay; }
 
    public String getPlan() { return plan; }
    public void setPlan(String plan) { this.plan = plan; }
 
    @Override
    public String toString() {
        return "ChatbotDayVO [chatbotid=" + chatbotid + ", dayno=" + dayno
             + ", tripdate=" + tripdate + ", region=" + region
             + ", stay=" + stay + ", plan=" + plan + "]";
    }

}
