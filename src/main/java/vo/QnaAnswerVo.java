package vo;

// =========================================================
// 주제: 파일 역할
// 설명: QnaAnswerVo 관련 값을 보관하고 전달하는 VO
// =========================================================


//=========================================================
//주제: Q&A 답변 VO
//설명: 답변 작성자/내용/날짜를 JSP로 전달
//=========================================================
public class QnaAnswerVo {
 private long answerId,questionId;
 private String memberId,content,createdAt;
 private boolean admin;
 public long getAnswerId(){return answerId;}
 public void setAnswerId(long v){answerId=v;}
 public long getQuestionId(){return questionId;}
 public void setQuestionId(long v){questionId=v;}
 public String getMemberId(){return memberId;}
 public void setMemberId(String v){memberId=v;}
 public String getContent(){return content;}
 public void setContent(String v){content=v;}
 public String getCreatedAt(){return createdAt;}
 public void setCreatedAt(String v){createdAt=v;}
 public boolean isAdmin(){return admin;}
 public void setAdmin(boolean v){admin=v;}
}
