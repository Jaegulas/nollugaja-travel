package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dto.ChatbotDto;
import util.DBCPUtil;
import vo.ChatbotDayVo;
import vo.ChatbotRequestVo;
import vo.ChatbotVo;
import vo.ItineraryJsonVo;

// =========================================================
// 주제: 파일 역할
// 설명: ChatbotDao 관련 데이터베이스 조회·저장·수정·삭제를 담당하는 DAO
// =========================================================


public class ChatbotDao {


//	public int chatbotConversationProInsert(ChatbotDto chatbotDto) throws SQLException {
//		String sql ="insert into chatbot(count, datestart, dateend, region, type, style, content, basic ) "+
//					"values (?, ?, ?, ?, ?, ?, ?, ?)";
//		
//		
//		try(Connection con = DBCPUtil.getConnection();
//			PreparedStatement ps = con.prepareStatement(sql)){
//			
//			ps.setInt(1, chatbotDto.getCount());
//			ps.setObject(2, chatbotDto.getDatestart());
//			ps.setObject(3, chatbotDto.getDateend());
//			ps.setString(4, chatbotDto.getRegion());
//			ps.setString(5, chatbotDto.getType());
//			ps.setString(6, chatbotDto.getStyle());
//			ps.setString(7, chatbotDto.getContent());
//			ps.setString(8, chatbotDto.getBasic());
//			
//			return ps.executeUpdate(); // insert 처리되면 1 안되면 0 전달
//		
//		}catch(SQLException e) {
//			e.printStackTrace();
//			throw new SQLException("[ChatbotDao] AccommodationConversationPro() insert중 에러발생 ",e);
//		}
//		
//	}//AccommodationConversationPro 안씀


	// =========================================================
	// 주제: countItineraryByMember 처리
	// 설명: countItineraryByMember 기능에 필요한 처리 로직을 수행
	// =========================================================
//	public int countItineraryByMember(String loginId) throws SQLException {
//		System.out.println("[ChatbotDao] countItineraryByMember() 매개변수 loginId 확인 : "+loginId);
//		String sql ="select count(*) from itinerarypdf  where memberid =?  ";
//		
//		try(Connection con = DBCPUtil.getConnection();
//			PreparedStatement ps = con.prepareStatement(sql)){
//			
//			ps.setString(1, loginId);
//		
//			ResultSet rs = ps.executeQuery(); // insert 처리되면 1 안되면 0 전달
//			
//			if(rs.next()) {
//				return rs.getInt(1);
//			}
//		
//		}catch(SQLException e) {
//			e.printStackTrace();
//			throw new SQLException("[ChatbotDao] countItineraryByMember() select 중 에러발생 ",e);
//		}
//		return -1;
//	} 안씀

	// =========================================================
	// 주제: itineraryjsonInsert 처리
	// 설명: itineraryjsonInsert 기능에 필요한 처리 로직을 수행
	// =========================================================
	public int itineraryjsonInsert(String title, String planjson, String loginId, int MAX_COUNT) throws SQLException{
		System.out.println("[ChatbotDao] countItineraryByMember() 매개변수 itineraryjsonInsert 확인 title : "+title);
		
		String countSql = "SELECT COUNT(*) FROM itineraryjson WHERE memberid = ?";
        String insertSql = "INSERT INTO itineraryjson (memberid, title, planjson, sharekey) VALUES (?, ?, ?, ?)";

        
        try (Connection conn = DBCPUtil.getConnection()) {

            // 15개 제한 확인
            try (PreparedStatement ps = conn.prepareStatement(countSql)) {
                ps.setString(1, loginId);
                
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) >= MAX_COUNT) {
                    	// 둘중 한개만 throw new IllegalAccessException("파일저장개수 15개를 초과했습니다.");
                    	return 0;
                    }
                }
            }//PreparedStatement

            // 저장
            String sharekey = UUID.randomUUID().toString();
         // DAO : insert 후 새로 생긴 planid 받기
            try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, loginId);
                ps.setString(2, title);
                ps.setString(3, planjson);
                ps.setString(4, sharekey);
                
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                int planid = 0;
                if (rs.next()) planid = rs.getInt(1);
                return planid;
            }
        }catch(SQLException e) {
			throw new SQLException("[ChatbotDao] itineraryjsonInsert() select, insert 에러발생 : "+e);
		}
	}//itineraryjsonInsert

	// =========================================================
	// 주제: itineraryjsonList 처리
	// 설명: itineraryjsonList 기능에 필요한 처리 로직을 수행
	// =========================================================
	public List<ItineraryJsonVo> itineraryjsonList(String loginId) throws SQLException {
		
		List<ItineraryJsonVo> result = new ArrayList<>();
		
		String sql = "select planid, title, sharekey, created_at from itineraryjson where memberid= ?"; 
	        
        try (Connection conn = DBCPUtil.getConnection();
        	PreparedStatement ps = conn.prepareStatement(sql)) {
    		ps.setString(1, loginId);

    		ResultSet rs = ps.executeQuery();
        	
        	//db컬럼 planid memberid title planjson sharekey created_at
            while(rs.next()) {
            	ItineraryJsonVo vo = new ItineraryJsonVo();
            	
            	vo.setCreated_at(rs.getObject("created_at", LocalDateTime.class));
            	vo.setPlanid(rs.getInt("planid"));
            	vo.setSharekey(rs.getString("sharekey"));
            	vo.setTitle(rs.getString("title"));       
            	result.add(vo);
            }
           
        }catch(SQLException e) {
			throw new SQLException("[ChatbotDao] itineraryjsonList() select 에러발생 : "+e);
		}
	    return result;    
	}//itineraryjsonList

	//챗봇일정과 개별일정은 트랜잭션처리한다. 중간에 close() 필요하고 pstmt.addBatch(); 로 모아서 executeBatch() 실행가능 그리고 commit() 처리
	// =========================================================
	// 주제: newInsertChatbotRequest 처리
	// 설명: newInsertChatbotRequest 기능에 필요한 처리 로직을 수행
	// =========================================================
	public int newInsertChatbotRequest(ChatbotRequestVo vo) throws SQLException {
		String sqlRequest =
		        "INSERT INTO chatbotrequest (people, datestart, dateend, style, content, memid) "
		      + "VALUES (?, ?, ?, ?, ?, ?)";
	    String sqlDay =
		        "INSERT INTO chatbotday (chatbotid, dayno, tripdate, region, stay, plan) "
		      + "VALUES (?, ?, ?, ?, ?, ?)";
		 
	    int newId = 0;

	    try (Connection conn = DBCPUtil.getConnection()) {
	        conn.setAutoCommit(false);       // 두 테이블을 한 묶음으로 처리 시작

	        try {
	            // ---- 1) chatbotrequest 저장 + 생성된 id 받기 ----
	            try (PreparedStatement pstmt = conn.prepareStatement(sqlRequest, Statement.RETURN_GENERATED_KEYS)) {

	                if (vo.getPeople() == null)
	                    pstmt.setNull(1, Types.INTEGER);
	                else
	                    pstmt.setInt(1, vo.getPeople());
	                pstmt.setDate(2, vo.getDatestart());
	                pstmt.setDate(3, vo.getDateend());
	                pstmt.setString(4, vo.getStyle());
	                pstmt.setString(5, vo.getContent());
	                pstmt.setString(6, vo.getMemid());    // 비회원이면 null 그대로 들어감
	                pstmt.executeUpdate();

	                try (ResultSet rs = pstmt.getGeneratedKeys()) {
	                    if (rs.next()) newId = rs.getInt(1);
	                }   // ← 여기서 rs 자동 close
	            }       // ← 여기서 첫 번째 pstmt 자동 close

	            // ---- 2) chatbotday 일차 수만큼 저장 ----
	            try (PreparedStatement pstmt = conn.prepareStatement(sqlDay)) {
	                for (ChatbotDayVo d : vo.getDays()) {
	                    pstmt.setInt(1, newId);
	                    pstmt.setInt(2, d.getDayno());
	                    pstmt.setDate(3, d.getTripdate());
	                    pstmt.setString(4, d.getRegion());
	                    pstmt.setString(5, d.getStay());   // 마지막 날 null
	                    pstmt.setString(6, d.getPlan());
	                    pstmt.addBatch();                  // 모아두었다가
	                }
	                pstmt.executeBatch();                  // 한 번에 실행
	            }       // ← 여기서 두 번째 pstmt 자동 close

	            conn.commit();                             // 둘 다 성공하면 확정

	        } catch (Exception e) {
	            e.printStackTrace();
	            newId = 0;
	            conn.rollback();                           // 하나라도 실패하면 전부 취소
	        } finally {
	            conn.setAutoCommit(true);                  // 커넥션 풀에 돌려주기 전에 원래대로
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        newId = 0;
	    }   // ← 여기서 conn 자동 close

	    return newId;
	}

	
	
	
	
	
}
