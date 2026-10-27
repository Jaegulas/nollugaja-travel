package dao;

import java.sql.*;
import java.util.*;
import util.DBCPUtil;
import vo.QnaAnswerVo;
import vo.QnaQuestionVo;

// =========================================================
// 주제: 파일 역할
// 설명: QnaDao 관련 데이터베이스 조회·저장·수정·삭제를 담당하는 DAO
// =========================================================


//=========================================================
//주제: Q&A 질문/답변 DB 접근
//설명: PreparedStatement 사용, 로그인 사용자 본인 글만 변경 가능
//=========================================================
public class QnaDao {
 private String conditions(String category,String status,String keyword,List<Object> args){
  StringBuilder w=new StringBuilder(" WHERE 1=1");
  if(category!=null&&!category.isEmpty()){w.append(" AND q.category=?");args.add(category);}
  if("waiting".equals(status)){w.append(" AND NOT EXISTS (SELECT 1 FROM qna_answer a WHERE a.question_id=q.question_id)");}
  if("answered".equals(status)){w.append(" AND EXISTS (SELECT 1 FROM qna_answer a WHERE a.question_id=q.question_id)");}
  if(keyword!=null&&!keyword.trim().isEmpty()){String k="%"+keyword.trim()+"%";w.append(" AND (q.title LIKE ? OR q.content LIKE ? OR q.member_id LIKE ?)");args.add(k);args.add(k);args.add(k);}
  return w.toString();
 }
 // =========================================================
 // 주제: bind 처리
 // 설명: bind 기능에 필요한 처리 로직을 수행
 // =========================================================
 private void bind(PreparedStatement p,List<Object> args)throws SQLException{for(int i=0;i<args.size();i++)p.setObject(i+1,args.get(i));}
 public int count(String category,String status,String keyword)throws SQLException{
  List<Object> args=new ArrayList<>();String w=conditions(category,status,keyword,args);
  try(Connection c=DBCPUtil.getConnection();PreparedStatement p=c.prepareStatement("SELECT COUNT(*) FROM qna_question q"+w)){
   bind(p,args);try(ResultSet r=p.executeQuery()){return r.next()?r.getInt(1):0;}
  }
 }
 // =========================================================
 // 주제: stats 처리
 // 설명: stats 기능에 필요한 처리 로직을 수행
 // =========================================================
 public int[] stats()throws SQLException{
  String sql="SELECT COUNT(*), SUM(CASE WHEN EXISTS(SELECT 1 FROM qna_answer a WHERE a.question_id=q.question_id) THEN 1 ELSE 0 END) FROM qna_question q";
  try(Connection c=DBCPUtil.getConnection();PreparedStatement p=c.prepareStatement(sql);ResultSet r=p.executeQuery()){
   return r.next()?new int[]{r.getInt(1),r.getInt(2)}:new int[]{0,0};
  }
 }
 // =========================================================
 // 주제: list 처리
 // 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
 // =========================================================
 public List<QnaQuestionVo> list(String category,String status,String keyword,String sort,int page,int size)throws SQLException{
  List<Object> args=new ArrayList<>();String w=conditions(category,status,keyword,args);
  String order="popular".equals(sort)?"q.view_count DESC,q.created_at DESC":"answers".equals(sort)?"answer_count DESC,q.created_at DESC":"q.created_at DESC,q.question_id DESC";
  String sql="SELECT q.question_id,CASE WHEN r.status='WITHDRAWN' THEN '탈퇴회원' ELSE q.member_id END AS member_id,q.category,q.title,q.content,q.view_count,"+
   "DATE_FORMAT(q.created_at,'%Y.%m.%d') created_at,DATE_FORMAT(q.updated_at,'%Y.%m.%d') updated_at,"+
   "(SELECT COUNT(*) FROM qna_answer a WHERE a.question_id=q.question_id) answer_count "+
   "FROM qna_question q LEFT JOIN register r ON r.id=q.member_id "+w+" ORDER BY "+order+" LIMIT ? OFFSET ?";
  List<QnaQuestionVo> list=new ArrayList<>();
  try(Connection c=DBCPUtil.getConnection();PreparedStatement p=c.prepareStatement(sql)){
   bind(p,args);p.setInt(args.size()+1,size);p.setInt(args.size()+2,Math.max(0,(page-1)*size));
   try(ResultSet r=p.executeQuery()){
    while(r.next()){
     QnaQuestionVo q=new QnaQuestionVo();q.setQuestionId(r.getLong("question_id"));q.setMemberId(r.getString("member_id"));
     q.setCategory(r.getString("category"));q.setTitle(r.getString("title"));q.setContent(r.getString("content"));
     q.setViewCount(r.getInt("view_count"));q.setAnswerCount(r.getInt("answer_count"));q.setCreatedAt(r.getString("created_at"));q.setUpdatedAt(r.getString("updated_at"));list.add(q);
    }
   }
   if(!list.isEmpty())loadAnswers(c,list);
  }
  return list;
 }
 // =========================================================
 // 주제: loadAnswers 처리
 // 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
 // =========================================================
 private void loadAnswers(Connection c,List<QnaQuestionVo> qs)throws SQLException{
  Map<Long,QnaQuestionVo> byId=new HashMap<>();StringJoiner j=new StringJoiner(",");
  for(QnaQuestionVo q:qs){byId.put(q.getQuestionId(),q);j.add("?");}
  String sql="SELECT a.answer_id,a.question_id,CASE WHEN r.status='WITHDRAWN' THEN '탈퇴회원' ELSE a.member_id END AS member_id,a.content,DATE_FORMAT(a.created_at,'%Y.%m.%d %H:%i') created_at "+
   "FROM qna_answer a LEFT JOIN register r ON r.id=a.member_id WHERE a.question_id IN ("+j+") ORDER BY a.created_at,a.answer_id";
  try(PreparedStatement p=c.prepareStatement(sql)){
   for(int i=0;i<qs.size();i++)p.setLong(i+1,qs.get(i).getQuestionId());
   try(ResultSet r=p.executeQuery()){
    while(r.next()){
     QnaAnswerVo a=new QnaAnswerVo();a.setAnswerId(r.getLong("answer_id"));a.setQuestionId(r.getLong("question_id"));
     a.setMemberId(r.getString("member_id"));a.setContent(r.getString("content"));a.setCreatedAt(r.getString("created_at"));
     QnaQuestionVo q=byId.get(a.getQuestionId());if(q!=null)q.getAnswers().add(a);
    }
   }
  }
 }
 // =========================================================
 // 주제: create 처리
 // 설명: 입력받은 데이터를 생성하거나 저장
 // =========================================================
 public long create(String user,String category,String title,String content)throws SQLException{
  String sql="INSERT INTO qna_question(member_id,category,title,content) VALUES(?,?,?,?)";
  try(Connection c=DBCPUtil.getConnection();PreparedStatement p=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
   p.setString(1,user);p.setString(2,category);p.setString(3,title);p.setString(4,content);p.executeUpdate();
   try(ResultSet r=p.getGeneratedKeys()){return r.next()?r.getLong(1):0;}
  }
 }
 // =========================================================
 // 주제: update 처리
 // 설명: 기존 데이터를 입력값에 맞게 수정
 // =========================================================
 public int update(long id,String user,boolean admin,String category,String title,String content)throws SQLException{
  String sql="UPDATE qna_question SET category=?,title=?,content=? WHERE question_id=?"+(admin?"":" AND member_id=?");
  try(Connection c=DBCPUtil.getConnection();PreparedStatement p=c.prepareStatement(sql)){
   p.setString(1,category);p.setString(2,title);p.setString(3,content);p.setLong(4,id);if(!admin)p.setString(5,user);return p.executeUpdate();
  }
 }
 // =========================================================
 // 주제: delete 처리
 // 설명: 대상 데이터를 삭제하거나 제거
 // =========================================================
 public int delete(long id,String user,boolean admin)throws SQLException{
  String sql="DELETE FROM qna_question WHERE question_id=?"+(admin?"":" AND member_id=?");
  try(Connection c=DBCPUtil.getConnection();PreparedStatement p=c.prepareStatement(sql)){
   p.setLong(1,id);if(!admin)p.setString(2,user);return p.executeUpdate();
  }
 }
 // =========================================================
 // 주제: addAnswer 처리
 // 설명: 입력받은 데이터를 생성하거나 저장
 // =========================================================
 public int addAnswer(long id,String user,String content)throws SQLException{
  String sql="INSERT INTO qna_answer(question_id,member_id,content) SELECT question_id,?,? FROM qna_question WHERE question_id=?";
  try(Connection c=DBCPUtil.getConnection();PreparedStatement p=c.prepareStatement(sql)){
   p.setString(1,user);p.setString(2,content);p.setLong(3,id);return p.executeUpdate();
  }
 }
 // =========================================================
 // 주제: deleteAnswer 처리
 // 설명: 대상 데이터를 삭제하거나 제거
 // =========================================================
 public int deleteAnswer(long answerId,String user,boolean admin)throws SQLException{
  String sql="DELETE FROM qna_answer WHERE answer_id=?"+(admin?"":" AND member_id=?");
  try(Connection c=DBCPUtil.getConnection();PreparedStatement p=c.prepareStatement(sql)){
   p.setLong(1,answerId);if(!admin)p.setString(2,user);return p.executeUpdate();
  }
 }
 // =========================================================
 // 주제: incrementView 처리
 // 설명: incrementView 기능에 필요한 처리 로직을 수행
 // =========================================================
 public int incrementView(long id)throws SQLException{
  try(Connection c=DBCPUtil.getConnection();PreparedStatement p=c.prepareStatement("UPDATE qna_question SET view_count=view_count+1 WHERE question_id=?")){
   p.setLong(1,id);return p.executeUpdate();
  }
 }
}
