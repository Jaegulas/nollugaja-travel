package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import util.DBCPUtil;

// =========================================================
// 주제: 파일 역할
// 설명: TourLikeDao 관련 데이터베이스 조회·저장·수정·삭제를 담당하는 DAO
// =========================================================


public class TourLikeDao{
 //=========================================================
 //주제: 관광지 찜 여부 조회
 //설명: 회원 ID + 공공 API content_id로 확인
 //=========================================================
 public boolean isLiked(String memberId,String contentId)throws SQLException{
  String sql="SELECT 1 FROM tour_like WHERE member_id=? AND content_id=?";
  try(Connection conn=DBCPUtil.getConnection();PreparedStatement ps=conn.prepareStatement(sql)){
   ps.setString(1,memberId);ps.setString(2,contentId);
   try(ResultSet rs=ps.executeQuery()){return rs.next();}
  }
 }
 //=========================================================
 //주제: 관광지 찜 추가
 //설명: 마이페이지에 표시할 관광지 정보를 함께 저장
 //=========================================================
 public boolean addLike(String memberId,String contentId,String name,String address,String category,String image)throws SQLException{
  String sql="INSERT INTO tour_like(member_id,content_id,tour_name,address,category,image_url) VALUES(?,?,?,?,?,?)";
  try(Connection conn=DBCPUtil.getConnection();PreparedStatement ps=conn.prepareStatement(sql)){
   ps.setString(1,memberId);ps.setString(2,contentId);ps.setString(3,name);
   ps.setString(4,address);ps.setString(5,category);ps.setString(6,image);
   return ps.executeUpdate()==1;
  }
 }
 //=========================================================
 //주제: 관광지 찜 취소
 //설명: 본인 찜만 삭제
 //=========================================================
 public boolean removeLike(String memberId,String contentId)throws SQLException{
  String sql="DELETE FROM tour_like WHERE member_id=? AND content_id=?";
  try(Connection conn=DBCPUtil.getConnection();PreparedStatement ps=conn.prepareStatement(sql)){
   ps.setString(1,memberId);ps.setString(2,contentId);
   return ps.executeUpdate()==1;
  }
 }
}
