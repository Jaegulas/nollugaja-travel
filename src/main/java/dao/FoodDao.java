package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import dto.FoodDto;
import util.DBCPUtil;

// =========================================================
// 주제: 파일 역할
// 설명: FoodDao 관련 데이터베이스 조회·저장·수정·삭제를 담당하는 DAO
// =========================================================


public class FoodDao{

 //=========================================================
 //주제: DB 조회 결과 DTO 변환
 //설명: 현재 food 테이블의 9개 컬럼만 사용
 //=========================================================
 private FoodDto mapFood(ResultSet rs)throws SQLException{
  FoodDto food=new FoodDto();
  food.setFood_id(rs.getInt("food_id"));
  food.setContent_id(rs.getString("content_id"));
  food.setF_name(rs.getString("f_name"));
  food.setF_category(rs.getString("f_category"));
  food.setAddress(rs.getString("address"));
  food.setImage(rs.getString("image"));
  food.setPhone(rs.getString("phone"));
  food.setLatitude(rs.getDouble("latitude"));
  food.setLongitude(rs.getDouble("longitude"));
  return food;
 }

 //=========================================================
 //주제: 음식점 전체 조회
 //설명: food 테이블 전체 데이터를 food_id 순서로 반환
 //=========================================================
 public List<FoodDto> selectAll(){
  List<FoodDto> foodList=new ArrayList<>();
  String sql="SELECT food_id,content_id,f_name,f_category,address,image,phone,latitude,longitude FROM food ORDER BY food_id";
  try(Connection conn=DBCPUtil.getConnection();PreparedStatement pstmt=conn.prepareStatement(sql);ResultSet rs=pstmt.executeQuery()){
   while(rs.next())foodList.add(mapFood(rs));
  }catch(Exception e){
   System.err.println("[FoodDao] 전체 조회 실패");
   e.printStackTrace();
  }
  return foodList;
 }

 //=========================================================
 //주제: content_id 기준 음식점 상세 조회
 //설명: API 음식점과 DB 데이터를 연결할 때 사용
 //=========================================================
 public FoodDto selectDetailByContentId(String contentId){
  String sql="SELECT food_id,content_id,f_name,f_category,address,image,phone,latitude,longitude FROM food WHERE content_id=?";
  try(Connection conn=DBCPUtil.getConnection();PreparedStatement pstmt=conn.prepareStatement(sql)){
   pstmt.setString(1,contentId);
   try(ResultSet rs=pstmt.executeQuery()){
    if(rs.next())return mapFood(rs);
   }
  }catch(Exception e){
   System.err.println("[FoodDao] content_id 상세 조회 실패: "+contentId);
   e.printStackTrace();
  }
  return null;
 }

 //=========================================================
 //주제: 현재 위치 기준 음식점 조회
 //설명: 선택한 좌표 주변 음식점을 거리순으로 반환
 //=========================================================
 public List<FoodDto> selectByLocation(double latitude,double longitude){
  List<FoodDto> foodList=new ArrayList<>();
  String sql="SELECT food_id,content_id,f_name,f_category,address,image,phone,latitude,longitude,"+
   "(6371*2*ASIN(SQRT(POWER(SIN(RADIANS(latitude-?)/2),2)+"+
   "COS(RADIANS(?))*COS(RADIANS(latitude))*POWER(SIN(RADIANS(longitude-?)/2),2)))) AS distance "+
   "FROM food WHERE latitude BETWEEN ? AND ? AND longitude BETWEEN ? AND ? ORDER BY distance ASC";
  try(Connection conn=DBCPUtil.getConnection();PreparedStatement pstmt=conn.prepareStatement(sql)){
   pstmt.setDouble(1,latitude);
   pstmt.setDouble(2,latitude);
   pstmt.setDouble(3,longitude);
   pstmt.setDouble(4,latitude-0.02);
   pstmt.setDouble(5,latitude+0.02);
   pstmt.setDouble(6,longitude-0.02);
   pstmt.setDouble(7,longitude+0.02);
   try(ResultSet rs=pstmt.executeQuery()){
    while(rs.next()){
     FoodDto food=mapFood(rs);
     food.setDistance(rs.getDouble("distance"));
     foodList.add(food);
    }
   }
  }catch(Exception e){
   System.err.println("[FoodDao] 위치 기준 조회 실패");
   e.printStackTrace();
  }
  return foodList;
 }
}
