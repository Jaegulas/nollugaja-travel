package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.google.gson.JsonObject;
import dao.FoodLikeDao;

// =========================================================
// 주제: 파일 역할
// 설명: FoodLikeController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


@WebServlet("/FoodLike")
public class FoodLikeController extends HttpServlet{
 private static final long serialVersionUID=1L;
 private final FoodLikeDao dao=new FoodLikeDao();
 //=========================================================
 //주제: 로그인 세션에서 회원 ID 조회
 //설명: 클라이언트가 전달한 회원 ID는 사용하지 않음
 //=========================================================
 private String userId(HttpServletRequest req){
  HttpSession session=req.getSession(false);
  Object id=session==null?null:session.getAttribute("loginId");
  return id instanceof String?(String)id:null;
 }
 private String value(HttpServletRequest req,String key,int max){
  String s=req.getParameter(key);if(s==null)return "";s=s.trim();return s.length()>max?s.substring(0,max):s;
 }
 // =========================================================
 // 주제: intValue 처리
 // 설명: intValue 기능에 필요한 처리 로직을 수행
 // =========================================================
 private int intValue(String s){try{return Integer.parseInt(s);}catch(Exception e){return 0;}}
 private double coord(String s,double min,double max){
  try{double v=Double.parseDouble(s);return Double.isFinite(v)&&v>=min&&v<=max?v:0;}catch(Exception e){return 0;}
 }
 private void send(HttpServletResponse resp,int status,JsonObject result)throws IOException{
  resp.setStatus(status);resp.setContentType("application/json;charset=UTF-8");resp.getWriter().write(result.toString());
 }
 // =========================================================
 // 주제: validInfo 처리
 // 설명: 입력값 또는 현재 상태가 조건에 맞는지 검사
 // =========================================================
 private boolean validInfo(int foodId,String contentId,String name,String address){
  return foodId>0||(!name.isEmpty()&&(!contentId.isEmpty()||!address.isEmpty()));
 }
 //=========================================================
 //주제: DB 찜 여부 조회
 //설명: foodId가 0이면 contentId 또는 이름·주소로 기존 음식점 조회 (등록하지 않음)
 //=========================================================
 @Override protected void doGet(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
  JsonObject result=new JsonObject();String uid=userId(req);
  if(uid==null||uid.isBlank()){result.addProperty("success",false);result.addProperty("message","로그인 후 사용 가능합니다.");send(resp,401,result);return;}
  int fid=intValue(req.getParameter("foodId"));
  String cid=value(req,"contentId",40),name=value(req,"name",200),address=value(req,"address",450);
  if(!validInfo(fid,cid,name,address)){result.addProperty("success",false);result.addProperty("message","음식점 정보가 부족합니다.");send(resp,400,result);return;}
  try{
   if(fid<=0)fid=dao.findFoodId(cid,name,address);
   result.addProperty("success",true);result.addProperty("foodId",fid);
   result.addProperty("liked",fid>0&&dao.isLiked(uid,fid));send(resp,200,result);
  }catch(Exception e){getServletContext().log("FoodLike GET 오류",e);result.addProperty("success",false);result.addProperty("message","찜 조회 실패");send(resp,500,result);}
 }
 //=========================================================
 //주제: 찜 추가/취소
 //설명: 음식점 ID가 없으면 food 테이블 등록 후 food_like에 저장
 //=========================================================
 @Override protected void doPost(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
  req.setCharacterEncoding("UTF-8");JsonObject result=new JsonObject();String uid=userId(req);
  if(uid==null||uid.isBlank()){result.addProperty("success",false);result.addProperty("message","로그인 후 사용 가능합니다.");send(resp,401,result);return;}
  int fid=intValue(req.getParameter("foodId"));
  String cid=value(req,"contentId",40),name=value(req,"name",200),address=value(req,"address",450);
  if(!validInfo(fid,cid,name,address)){result.addProperty("success",false);result.addProperty("message","음식점 정보가 부족합니다.");send(resp,400,result);return;}
  try{
   if(fid<=0){
    fid=dao.findOrCreateFoodId(cid,name,address,value(req,"category",100),value(req,"image",2000),value(req,"phone",80),
     coord(req.getParameter("lat"),-90,90),coord(req.getParameter("lng"),-180,180));
   }
   boolean wasLiked=dao.isLiked(uid,fid);
   boolean ok=wasLiked?dao.removeLike(uid,fid):dao.addLike(uid,fid);
   result.addProperty("success",ok);result.addProperty("foodId",fid);result.addProperty("liked",ok?!wasLiked:wasLiked);
   result.addProperty("message",ok?(wasLiked?"찜 취소 완료":"찜하기 완료"):"찜 처리 실패");send(resp,ok?200:409,result);
  }catch(Exception e){getServletContext().log("FoodLike POST 오류",e);result.addProperty("success",false);result.addProperty("message","음식점 등록 또는 찜 저장 중 오류가 발생했습니다.");send(resp,500,result);}
 }
}
