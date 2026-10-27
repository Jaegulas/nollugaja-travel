package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import util.DBCPUtil;

// =========================================================
// 주제: 파일 역할
// 설명: MyFavoritesController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


@WebServlet("/MyFavorites")
public class MyFavoritesController extends HttpServlet{
 private static final long serialVersionUID=1L;
 //=========================================================
 //주제: 마이페이지 맛집·관광지 찜 목록 API
 //설명: 로그인한 회원의 데이터만 조회하여 JSON 반환
 //=========================================================
 @Override protected void doGet(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
  resp.setContentType("application/json;charset=UTF-8");
  //=========================================================
  //주제: 최신 찜 목록 조회
  //설명: 브라우저 캐시 때문에 관광지 찜이 갱신되지 않는 경우 방지
  //=========================================================
  resp.setHeader("Cache-Control","no-store, no-cache, max-age=0, must-revalidate");
  resp.setHeader("Pragma","no-cache");
  HttpSession session=req.getSession(false);

  // =========================================================
  // 주제: 로그인 회원 세션 키 조회
  // 설명: 프로젝트 공통 세션 키 loginId를 우선 사용하고, 기존 세션 id는 호환용으로만 확인
  // =========================================================
  Object id=session==null?null:session.getAttribute("loginId");
  if(!(id instanceof String)||((String)id).isBlank()){
   id=session==null?null:session.getAttribute("id");
  }

  JsonObject out=new JsonObject();
  if(!(id instanceof String)||((String)id).isBlank()){
   resp.setStatus(401);out.addProperty("success",false);out.addProperty("message","로그인 후 사용 가능합니다.");resp.getWriter().write(out.toString());return;
  }
  String userId=((String)id).trim();
  String foodSql="SELECT f.food_id,f.f_name,f.f_category,f.address,f.image,DATE_FORMAT(l.regdate,'%Y-%m-%d') AS likedAt FROM food_like l JOIN food f ON f.food_id=l.food_id WHERE l.member_id=? ORDER BY l.regdate DESC,l.like_id DESC";
  String tourSql="SELECT content_id,tour_name,address,category,image_url,DATE_FORMAT(regdate,'%Y-%m-%d') AS likedAt FROM tour_like WHERE member_id=? ORDER BY regdate DESC,like_id DESC";
  JsonArray foods=new JsonArray(),tours=new JsonArray();
  try(Connection conn=DBCPUtil.getConnection()){
   //주제: 맛집 찜 목록
   try(PreparedStatement ps=conn.prepareStatement(foodSql)){
    ps.setString(1,userId);
    try(ResultSet rs=ps.executeQuery()){while(rs.next()){
     JsonObject item=new JsonObject();
     item.addProperty("foodId",rs.getInt("food_id"));
     item.addProperty("name",rs.getString("f_name"));
     item.addProperty("category",rs.getString("f_category"));
     item.addProperty("address",rs.getString("address"));
     item.addProperty("image",rs.getString("image"));
     item.addProperty("likedAt",rs.getString("likedAt"));foods.add(item);
    }}
   }
   //주제: 관광지 찜 목록
   try(PreparedStatement ps=conn.prepareStatement(tourSql)){
    ps.setString(1,userId);
    try(ResultSet rs=ps.executeQuery()){while(rs.next()){
     JsonObject item=new JsonObject();
     item.addProperty("contentId",rs.getString("content_id"));
     item.addProperty("name",rs.getString("tour_name"));
     item.addProperty("category",rs.getString("category"));
     item.addProperty("address",rs.getString("address"));
     item.addProperty("image",rs.getString("image_url"));
     item.addProperty("likedAt",rs.getString("likedAt"));tours.add(item);
    }}
   }
   out.addProperty("success",true);out.add("foods",foods);out.add("tours",tours);
   // 본인 계정 기준 찜 개수 진단용 (추가 필드, 기존 JSON 구조 유지)
   out.addProperty("foodCount",foods.size());out.addProperty("tourCount",tours.size());
  }catch(Exception e){getServletContext().log("MyFavorites 조회 오류",e);resp.setStatus(500);out=new JsonObject();out.addProperty("success",false);out.addProperty("message","찜 목록을 불러오지 못했습니다.");}
  resp.getWriter().write(out.toString());
 }
}
