package vo;

import java.util.ArrayList;
import java.util.List;

// =========================================================
// 주제: 파일 역할
// 설명: QnaQuestionVo 관련 값을 보관하고 전달하는 VO
// =========================================================


//=========================================================
//주제: Q&A 질문 VO
//설명: 검색/목록/답변 펼치기에 필요한 정보
//=========================================================
public class QnaQuestionVo {
 private long questionId;
 private String memberId,category,title,content,createdAt,updatedAt;
 private int viewCount,answerCount;
 private List<QnaAnswerVo> answers=new ArrayList<>();
 public long getQuestionId(){return questionId;}
 public void setQuestionId(long v){questionId=v;}
 public String getMemberId(){return memberId;}
 public void setMemberId(String v){memberId=v;}
 public String getCategory(){return category;}
 public void setCategory(String v){category=v;}
 public String getTitle(){return title;}
 public void setTitle(String v){title=v;}
 public String getContent(){return content;}
 public void setContent(String v){content=v;}
 public String getCreatedAt(){return createdAt;}
 public void setCreatedAt(String v){createdAt=v;}
 public String getUpdatedAt(){return updatedAt;}
 public void setUpdatedAt(String v){updatedAt=v;}
 public int getViewCount(){return viewCount;}
 public void setViewCount(int v){viewCount=v;}
 public int getAnswerCount(){return answerCount;}
 public void setAnswerCount(int v){answerCount=v;}
 public boolean isAnswered(){return answerCount>0;}
 public String getPreview(){String s=content==null?"":content.replace('\n',' ').replace('\r',' ').trim();return s.length()>100?s.substring(0,100)+"…":s;}
 public List<QnaAnswerVo> getAnswers(){return answers;}
 public void setAnswers(List<QnaAnswerVo> v){answers=v;}
}
