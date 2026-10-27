package vo;

import java.security.Timestamp;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

// =========================================================
// 주제: 파일 역할
// 설명: ChatbotRequestVo 관련 값을 보관하고 전달하는 VO
// =========================================================


public class ChatbotRequestVo {
	  private int       id;          // id          INT PK AUTO_INCREMENT
	    private Integer   people;      // people      INT NULL   (int 가 아니라 Integer : 비어 있으면 null)
	    private Date      datestart;   // datestart   DATE
	    private Date      dateend;     // dateend     DATE
	    private String    style;       // style       VARCHAR(100) NULL  ("미식형,쇼핑형")
	    private String    content;     // content     TEXT
	    private Timestamp created_at;  // created_at  DATETIME
	    private String    memid;       // memid       VARCHAR(12) NULL  (비회원이면 null)
	 
	    // ★ DB 컬럼 아님 : 이 요청의 일차 목록 (chatbotday 테이블 행들)
	    private List<ChatbotDayVo> days = new ArrayList<>();
	 
	    public ChatbotRequestVo() { }
	 
	    // ===== getter / setter =====
	    public int getId() { return id; }
	    public void setId(int id) { this.id = id; }
	 
	    public Integer getPeople() { return people; }
	    public void setPeople(Integer people) { this.people = people; }
	 
	    public Date getDatestart() { return datestart; }
	    public void setDatestart(Date datestart) { this.datestart = datestart; }
	 
	    public Date getDateend() { return dateend; }
	    public void setDateend(Date dateend) { this.dateend = dateend; }
	 
	    public String getStyle() { return style; }
	    public void setStyle(String style) { this.style = style; }
	 
	    public String getContent() { return content; }
	    public void setContent(String content) { this.content = content; }
	 
	    public Timestamp getCreated_at() { return created_at; }
	    public void setCreated_at(Timestamp created_at) { this.created_at = created_at; }
	 
	    public String getMemid() { return memid; }
	    public void setMemid(String memid) { this.memid = memid; }
	 
	    public List<ChatbotDayVo> getDays() { return days; }
	    public void setDays(List<ChatbotDayVo> days) { this.days = days; }
	 
	    @Override
	    public String toString() {
	        return "ChatbotRequestVO [id=" + id + ", people=" + people
	             + ", datestart=" + datestart + ", dateend=" + dateend
	             + ", style=" + style + ", content=" + content
	             + ", created_at=" + created_at + ", memid=" + memid
	             + ", days=" + days + "]";
	    }
}
