package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.google.gson.JsonObject;
import dao.TourLikeDao;

// =========================================================
// 주제: 파일 역할
// 설명: TourLikeController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


@WebServlet("/TourLike")
public class TourLikeController extends HttpServlet{
 private static final long serialVersionUID=1L;
 private final TourLikeDao dao=new TourLikeDao();
 //=========================================================
 //주제: 로그인한 회원 확인
 //설명: 클라이언트 입력이 아니라 로그인 세션의 id 사용
 //=========================================================
 private String memberId(HttpServletRequest req){
  HttpSession s=req.getSession(false);
  Object id=s==null?null:s.getAttribute("loginId"); //getAttribute("id") 2026.10.24토 권성애 수정
  return id instanceof String?(String)id:null;
 }
 private boolean validId(String id){return id!=null&&!id.trim().isEmpty()&&id.length()<=40;}
 // =========================================================
 // 주제: cut 처리
 // 설명: cut 기능에 필요한 처리 로직을 수행
 // =========================================================
 private String cut(String v,int n){if(v==null)return "";v=v.trim();return v.length()>n?v.substring(0,n):v;}
 private void send(HttpServletResponse resp,int status,JsonObject json)throws IOException{
  resp.setStatus(status);resp.setContentType("application/json;charset=UTF-8");
  resp.getWriter().write(json.toString());
 }
 //=========================================================
 //주제: 관광지 찜 여부 GET
 //설명: /TourLike?contentId=123456 조회
 //=========================================================
 @Override protected void doGet(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
  JsonObject json=new JsonObject();String uid=memberId(req);
  if(uid==null||uid.isBlank()){json.addProperty("success",false);json.addProperty("message","로그인 후 사용 가능합니다.");send(resp,401,json);return;}
  String cid=req.getParameter("contentId");
  if(!validId(cid)){json.addProperty("success",false);json.addProperty("message","관광지 ID가 없습니다.");send(resp,400,json);return;}
  try{json.addProperty("success",true);json.addProperty("liked",dao.isLiked(uid,cid));send(resp,200,json);}
  catch(Exception e){getServletContext().log("TourLike GET 오류",e);json.addProperty("success",false);json.addProperty("message","조회 실패");send(resp,500,json);}
 }
 //=========================================================
 //주제: 관광지 찜 추가/취소 POST
 //설명: 이미 있으면 삭제, 없으면 정보와 함께 저장
 //=========================================================
 @Override protected void doPost(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
  req.setCharacterEncoding("UTF-8");JsonObject json=new JsonObject();String uid=memberId(req);
  if(uid==null||uid.isBlank()){json.addProperty("success",false);json.addProperty("message","로그인 후 사용 가능합니다.");send(resp,401,json);return;}
  String cid=req.getParameter("contentId");
  if(!validId(cid)){json.addProperty("success",false);json.addProperty("message","관광지 ID가 없습니다.");send(resp,400,json);return;}
  String name=cut(req.getParameter("name"),255),address=cut(req.getParameter("address"),500);
  String category=cut(req.getParameter("category"),100),image=cut(req.getParameter("image"),2048);
  try{
   boolean wasLiked=dao.isLiked(uid,cid);
   if(!wasLiked&&name.isEmpty()){
    json.addProperty("success",false);json.addProperty("message","관광지 이름이 없습니다.");send(resp,400,json);return;
   }
   boolean ok=wasLiked?dao.removeLike(uid,cid):dao.addLike(uid,cid,name,address,category,image);
   json.addProperty("success",ok);
   json.addProperty("liked",ok?!wasLiked:wasLiked);
   json.addProperty("message",ok?(wasLiked?"찜 취소 완료":"찜하기 완료"):"찜 처리 실패");
   send(resp,ok?200:409,json);
  }catch(Exception e){getServletContext().log("TourLike POST 오류",e);json.addProperty("success",false);json.addProperty("message","DB 저장 실패");send(resp,500,json);}
 }
}
