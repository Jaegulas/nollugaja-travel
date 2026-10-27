package dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

// =========================================================
// 주제: 파일 역할
// 설명: ChatbotDto 화면/계층 사이에서 필요한 데이터를 전달하는 DTO
// =========================================================


public class ChatbotDto {

	private Integer count;
	private String datestart;
	private String dateend;
	private String region;
	private String type;
	private String style;
	private String content;
	private String basic;
	
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public String getDatestart() {
		return datestart;
	}
	public void setDatestart(String datestart) {
		this.datestart = datestart;
	}
	public String getDateend() {
		return dateend;
	}
	public void setDateend(String dateend) {
		this.dateend = dateend;
	}

	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	
	
	public String getBasic() {
		return basic;
	}
	public void setBasic(String basic) {
		this.basic = basic;
	}
	@Override
	public String toString() {
		return "ChatbotDto [count=" + count + ", datestart=" + datestart + ", dateend=" + dateend + 
			 ", region=" + region + ", type=" + type + ", style=" + style + ", content=" + content + ", basic=" + basic + "]";
	}
	public ChatbotDto(Integer count, String datestart, String dateend, String option, String region,
			String type, String style, String content, String basic) {
		super();
		this.count = count;
		this.datestart = datestart;
		this.dateend = dateend;
			this.region = region;
		this.type = type;
		this.style = style;
		this.content = content;
		this.basic = basic;
	}
	public ChatbotDto() {
		super();
	}

	private static LocalDateTime toStartOfDay(String s) {
	    if (s == null || s.isBlank()) return null;
	    try {
	    	// 종료일은 그날 끝까지 포함하려면 → 2026-10-15T23:59:59
	    	//LocalDateTime end = LocalDate.parse(dto.getDateend()).atTime(23, 59, 59);
	        return LocalDate.parse(s.trim()).atStartOfDay();
	    } catch (DateTimeParseException e) {
	    	System.out.println("[ChatbotDto] String JSON -> LocalDateTime 객체로 변환시 에러발생 : "+e);
	        return null;
	    }
	}
	
	

}
