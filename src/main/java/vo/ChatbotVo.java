package vo;

import java.time.LocalDateTime;

// =========================================================
// 주제: 파일 역할
// 설명: ChatbotVo 관련 값을 보관하고 전달하는 VO
// =========================================================


public class ChatbotVo {
	
	private Integer id;
	private Integer count;
	private LocalDateTime datestart;
	private LocalDateTime dateend;
	private String region;
	private String type;
	private String style;
	private String content;
	private String mem_id;
	private String basic;
	private LocalDateTime created_at;
	//11개
	@Override
	public String toString() {
		return "ChatbotVo [id=" + id + ", count=" + count + ", datestart=" + datestart + ", dateend=" + dateend
				+ ", region=" + region + ", type=" + type + ", style=" + style + ", content="
				+ content + ", mem_id=" + mem_id + ", basic=" + basic + ", created_at=" + created_at + "]";
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public LocalDateTime getDatestart() {
		return datestart;
	}
	public void setDatestart(LocalDateTime datestart) {
		this.datestart = datestart;
	}
	public LocalDateTime getDateend() {
		return dateend;
	}
	public void setDateend(LocalDateTime dateend) {
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
	public String getMem_id() {
		return mem_id;
	}
	public void setMem_id(String mem_id) {
		this.mem_id = mem_id;
	}
	
	
	public String getBasic() {
		return basic;
	}
	public void setBasic(String basic) {
		this.basic = basic;
	}
	public LocalDateTime getCreated_at() {
		return created_at;
	}
	public void setCreated_at(LocalDateTime created_at) {
		this.created_at = created_at;
	}
	public ChatbotVo() {
		super();
	}
	public ChatbotVo(Integer id, Integer count, LocalDateTime datestart, LocalDateTime dateend, String option,
			String region, String type, String style, String content, String mem_id, String basic, LocalDateTime created_at) {
		super();
		this.id = id;
		this.count = count;
		this.datestart = datestart;
		this.dateend = dateend;
		this.region = region;
		this.type = type;
		this.style = style;
		this.content = content;
		this.mem_id = mem_id;
		this.basic = basic;
		this.created_at = created_at;
	}
	
	
	

}
