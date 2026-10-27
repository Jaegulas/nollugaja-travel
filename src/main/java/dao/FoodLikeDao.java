package dao;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import util.DBCPUtil;

// =========================================================
// 주제: 파일 역할
// 설명: FoodLikeDao 관련 데이터베이스 조회·저장·수정·삭제를 담당하는 DAO
// =========================================================


public class FoodLikeDao{
 //=========================================================
 //주제: 맛집 찜 추가 / 취소 / 조회
 //설명: 로그인 회원 ID(String)와 food_id(int)를 이용
 //=========================================================
 public boolean addLike(String userId,int foodId){
  String sql="INSERT INTO food_like(member_id,food_id,regdate) VALUES(?,?,NOW())";
  try(Connection conn=DBCPUtil.getConnection();PreparedStatement ps=conn.prepareStatement(sql)){
   ps.setString(1,userId);ps.setInt(2,foodId);return ps.executeUpdate()==1;
  }catch(Exception e){e.printStackTrace();return false;}
 }
 // =========================================================
 // 주제: removeLike 처리
 // 설명: 대상 데이터를 삭제하거나 제거
 // =========================================================
 public boolean removeLike(String userId,int foodId){
  String sql="DELETE FROM food_like WHERE member_id=? AND food_id=?";
  try(Connection conn=DBCPUtil.getConnection();PreparedStatement ps=conn.prepareStatement(sql)){
   ps.setString(1,userId);ps.setInt(2,foodId);return ps.executeUpdate()==1;
  }catch(Exception e){e.printStackTrace();return false;}
 }
 // =========================================================
 // 주제: isLiked 처리
 // 설명: 입력값 또는 현재 상태가 조건에 맞는지 검사
 // =========================================================
 public boolean isLiked(String userId,int foodId){
  String sql="SELECT 1 FROM food_like WHERE member_id=? AND food_id=?";
  try(Connection conn=DBCPUtil.getConnection();PreparedStatement ps=conn.prepareStatement(sql)){
   ps.setString(1,userId);ps.setInt(2,foodId);
   try(ResultSet rs=ps.executeQuery()){return rs.next();}
  }catch(Exception e){e.printStackTrace();return false;}
 }
 //=========================================================
 //주제: 음식점 식별키 생성
 //설명: 공공 API content_id 또는 네이버 검색명+주소 해시 사용
 //=========================================================
 private String trim(String value){return value==null?"":value.trim();}
 private String externalId(String contentId,String name,String address){
  if(!trim(contentId).isEmpty())return trim(contentId);
  try{
   MessageDigest md=MessageDigest.getInstance("SHA-256");
   byte[] hash=md.digest((trim(name).toLowerCase()+"|"+trim(address).toLowerCase()).getBytes(StandardCharsets.UTF_8));
   StringBuilder sb=new StringBuilder("NV");
   for(int i=0;i<8;i++)sb.append(String.format("%02x",hash[i]&0xff));
   return sb.toString();
  }catch(Exception e){throw new IllegalStateException("식별키 생성 실패",e);}
 }
 //=========================================================
 //주제: 기존 음식점 찾기 (데이터 삽입 없음)
 //설명: content_id 우선, 다음으로 이름+주소 검색
 //=========================================================
 private int findFoodId(Connection conn,String contentId,String name,String address)throws SQLException{
  String cid=externalId(contentId,name,address);
  try(PreparedStatement ps=conn.prepareStatement("SELECT food_id FROM food WHERE content_id=? LIMIT 1")){
   ps.setString(1,cid);try(ResultSet rs=ps.executeQuery()){if(rs.next())return rs.getInt(1);}
  }
  if(!trim(name).isEmpty()&&!trim(address).isEmpty()){
   try(PreparedStatement ps=conn.prepareStatement("SELECT food_id FROM food WHERE f_name=? AND address=? LIMIT 1")){
    ps.setString(1,trim(name));ps.setString(2,trim(address));
    try(ResultSet rs=ps.executeQuery()){if(rs.next())return rs.getInt(1);}
   }
  }
  return 0;
 }
 // =========================================================
 // 주제: findFoodId 처리
 // 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
 // =========================================================
 public int findFoodId(String contentId,String name,String address)throws SQLException{
  try(Connection conn=DBCPUtil.getConnection()){return findFoodId(conn,contentId,name,address);}
 }
 //=========================================================
 //주제: DB 미등록 API 맛집 자동 등록
 //설명: 동일 장소를 여러 명이 동시에 눌러도 한 행만 생성 (MySQL GET_LOCK)
 //주의: food.food_id는 AUTO_INCREMENT여야 함
 //=========================================================
 public int findOrCreateFoodId(String contentId,String name,String address,String category,String image,String phone,double lat,double lng)throws SQLException{
  String cid=externalId(contentId,name,address);
  String lockName="foodlike:"+externalId("",cid,"");
  try(Connection conn=DBCPUtil.getConnection()){
   boolean locked=false;
   try{
    try(PreparedStatement ps=conn.prepareStatement("SELECT GET_LOCK(?,5)")){
     ps.setString(1,lockName);
     try(ResultSet rs=ps.executeQuery()){locked=rs.next()&&rs.getInt(1)==1;}
    }
    if(!locked)throw new SQLException("음식점 등록 잠금 시간 초과");
    int existing=findFoodId(conn,contentId,name,address);
    if(existing>0)return existing;
    String sql="INSERT INTO food(content_id,f_name,f_category,address,image,phone,latitude,longitude) VALUES(?,?,?,?,?,?,?,?)";
    try(PreparedStatement ps=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
     ps.setString(1,cid);ps.setString(2,trim(name));ps.setString(3,trim(category).isEmpty()?"맛집":trim(category));
     ps.setString(4,trim(address));ps.setString(5,trim(image));ps.setString(6,trim(phone));
     ps.setDouble(7,lat);ps.setDouble(8,lng);
     if(ps.executeUpdate()!=1)throw new SQLException("음식점 자동 등록 실패");
     try(ResultSet keys=ps.getGeneratedKeys()){if(keys.next()&&keys.getInt(1)>0)return keys.getInt(1);}
    }
    int found=findFoodId(conn,contentId,name,address);
    if(found>0)return found;
    throw new SQLException("신규 food_id 생성 결과 없음: food.food_id AUTO_INCREMENT 확인 필요");
   }finally{
    if(locked){try(PreparedStatement ps=conn.prepareStatement("SELECT RELEASE_LOCK(?)")){
     ps.setString(1,lockName);ps.executeQuery().close();
    }catch(SQLException e){System.err.println("잠금 반환 오류: "+e.getMessage());}}
   }
  }
 }
}
