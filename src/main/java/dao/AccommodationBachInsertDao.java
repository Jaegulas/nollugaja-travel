package dao;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import util.DBCPUtil;
import vo.AccommodationReviewVo;
import vo.AccommodationVo;

// =========================================================
// 주제: 파일 역할
// 설명: AccommodationBachInsertDao 관련 데이터베이스 조회·저장·수정·삭제를 담당하는 DAO
// =========================================================


public class AccommodationBachInsertDao {

	private static final String KAKAO_API_KEY = "";
	private static final String GOOGLE_API_KEY = "";
	private final HttpClient httpClient;
	private final ObjectMapper objectMapper;
	
	
	
	
	//공공api name addr, picpath
	// =========================================================
	// 주제: publicAPIInsert 처리
	// 설명: publicAPIInsert 기능에 필요한 처리 로직을 수행
	// =========================================================
	public void publicAPIInsert(List<AccommodationVo> accommodationBachList) throws SQLException{

		String sql="insert into accommodation(name, addr, picpath, type, type2, contentid) values (?,?,?,?,?,?)";
		
		try(Connection con = DBCPUtil.getConnection();
			PreparedStatement pstmt = con.prepareStatement(sql)) {
			con.setAutoCommit(false); // 트랜잭션설정

			int count = 0;
			
			for(AccommodationVo vo : accommodationBachList) {
				
				pstmt.setString(1, vo.getName());
				pstmt.setString(2, vo.getAddr());
				pstmt.setString(3, vo.getPicpath());
				pstmt.setString(4, vo.getType());
				pstmt.setString(5, vo.getType2());
				pstmt.setString(6, vo.getContentid());

				pstmt.addBatch(); //PreparedStatement 이 객체에 추가
				count++;

				if (count % 1000 == 0) { //1000건되면 배치실행
					pstmt.executeBatch();
					pstmt.clearBatch();
				}
			}//for
			con.commit();// 커밋
        }
		catch (SQLException e) {
			e.printStackTrace();
			throw new SQLException(e.getMessage());
		}//try-catch
	}
	
	

	// =========================================================
	// 주제: AccommodationBachInsertDao 처리
	// 설명: AccommodationBachInsertDao 기능에 필요한 처리 로직을 수행
	// =========================================================
	public AccommodationBachInsertDao() {
		this.httpClient = HttpClient.newHttpClient();
		this.objectMapper = new ObjectMapper();
	}
		
	
	
}
