package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import util.DBCPUtil;
import vo.ItineraryJsonVo;
import vo.MypageLikeVo;

// =========================================================
// 주제: 파일 역할
// 설명: MypageDao 관련 데이터베이스 조회·저장·수정·삭제를 담당하는 DAO
// =========================================================


public class MypageDao {

    // ------------------------------------------------------------
    // 상세페이지 주소 만들기  ★ 실제 상세페이지 주소로 바꾸세요
    //   (JS 에서 앞에 CTX 를 붙이니까 여기는 "/" 부터)
    // ------------------------------------------------------------
    // =========================================================
    // 주제: makeUrl 처리
    // 설명: 입력 데이터를 화면/API/DB에서 사용하기 좋은 형태로 변환 또는 가공
    // =========================================================
    private String makeUrl(String type, int id) {
        if ("acc".equals(type))      return "/acc/detail?id=" + id;
        if ("place".equals(type))    return "/travel/detail?id=" + id;
        if ("food".equals(type))     return "/food/detail?id=" + id;
        if ("heritage".equals(type)) return "/heritage/detail?id=" + id;
        return "#";
    }

	
	
	
	// =========================================================
	// 주제: mypageLikeListPerMember 처리
	// 설명: 찜 상태 조회 또는 추가/해제를 처리
	// =========================================================
	public List<MypageLikeVo> mypageLikeListPerMember(String loginId) throws SQLException {
		
		  List<MypageLikeVo> list = new ArrayList<>();

	        // 칸 순서 : type, id, name, addr, image, liked_at(글자), sort_at(정렬용 진짜 날짜)
	        String sql =
	              // [숙소]
	              "SELECT 'acc' AS type, a.accommodation_id AS id, a.name AS name, a.addr AS addr, "
	            + "       a.picpath AS image, "
	            + "       DATE_FORMAT(l.created_at, '%Y-%m-%d') AS liked_at, l.created_at AS sort_at "
	            + "FROM accommodation_like l "
	            + "JOIN accommodation a ON a.accommodation_id = l.accommodation_id "
	            + "WHERE l.member_id = ? "

	            + "UNION ALL "

	              // [여행지] ★ travel_destination 이면 이렇게 바꾸기
	              //   SELECT 'place', d.destination_id, d.name, d.address, d.image_url, ...
	              //   FROM travel_like t JOIN travel_destination d ON d.destination_id = t.content_id
	            + "SELECT 'place', c.content_id, c.title, c.addr1, "
	            + "       c.first_image, "
	            + "       DATE_FORMAT(t.created_at, '%Y-%m-%d'), t.created_at "
	            + "FROM travel_like t "
	            + "JOIN cultural_heritage c ON c.content_id = t.content_id "
	            + "WHERE t.member_id = ? "

	            + "UNION ALL "

	              // [음식] 회원 칸 이름이 member_id, 날짜 칸이 regdate 인 것 주의
	            + "SELECT 'food', f.food_id, f.f_name, f.address, "
	            + "       f.image, "
	            + "       DATE_FORMAT(fl.regdate, '%Y-%m-%d'), fl.regdate "
	            + "FROM food_like fl "
	            + "JOIN food f ON f.food_id = fl.food_id "
	            + "WHERE fl.member_id = ? "

	            + "UNION ALL "

	              // [문화유산]
	            + "SELECT 'heritage', h.heritage_id, h.name, h.address, "
	            + "       h.image_url, "
	            + "       DATE_FORMAT(cl.created_at, '%Y-%m-%d'), cl.created_at "
	            + "FROM cultural_like cl "
	            + "JOIN culturalHeritage h ON h.heritage_id = cl.heritage_id "
	            + "WHERE cl.member_id = ? "

	              // 전체를 찜한 날짜 최신순으로 (날짜가 비어있는 건 맨 뒤)
	            + "ORDER BY sort_at DESC";

	        try (Connection conn = DBCPUtil.getConnection();
	             PreparedStatement ps = conn.prepareStatement(sql)) {

	            // ? 가 4개 → 전부 로그인 아이디
	            ps.setString(1, loginId);
	            ps.setString(2, loginId);
	            ps.setString(3, loginId);
	            ps.setString(4, loginId);

	            try (ResultSet rs = ps.executeQuery()) {
	                while (rs.next()) {
	                    MypageLikeVo vo = new MypageLikeVo();
	                    vo.setType(rs.getString("type"));
	                    vo.setId(rs.getInt("id"));
	                    vo.setName(rs.getString("name"));
	                    vo.setAddr(rs.getString("addr"));
	                    vo.setImage(rs.getString("image"));
	                    vo.setLikedAt(rs.getString("liked_at"));
	                    vo.setUrl(makeUrl(vo.getType(), vo.getId()));
	                    list.add(vo);
	                }
	            }

	        } catch (Exception e) {
	           throw new SQLException("[MypageDao] mypageLikeListPerMember() 예외발생"+e);
	        }

	        return list;

	    }
	 

    // ============================================================
    // 2. 찜 해제 : 내 찜만 지워짐. 성공하면 true
    //    type 마다 테이블 이름, 칸 이름이 달라서 경우를 나눠요
    // ============================================================
    // =========================================================
    // 주제: mypageLikeDeletePerMember 처리
    // 설명: 찜 상태 조회 또는 추가/해제를 처리
    // =========================================================
    public boolean mypageLikeDeletePerMember(String loginId, String type, int id)  throws SQLException{

    	boolean result = false;
        String sql;
        if ("acc".equals(type)) {
            sql = "DELETE FROM accommodation_like WHERE member_id = ? AND accommodation_id = ?";
        } else if ("place".equals(type)) {
            sql = "DELETE FROM travel_like WHERE member_id = ? AND content_id = ?";
        } else if ("food".equals(type)) {
            sql = "DELETE FROM food_like WHERE member_id = ? AND food_id = ?";
        } else if ("heritage".equals(type)) {
            sql = "DELETE FROM cultural_like WHERE member_id = ? AND heritage_id = ?";
        } else {
            return result;   // 모르는 type
        }

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, loginId);
            if ("place".equals(type)) {
                ps.setString(2, String.valueOf(id));   // travel_like.content_id 는 varchar
            } else {
                ps.setInt(2, id);
            }
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            throw new SQLException("[MypageDao] deleteLike() 예외발생 "+e);
            
        }
    }    
 	

	
	
	// =========================================================
	// 주제: deleteItineraryJsonPlan 처리
	// 설명: 대상 데이터를 삭제하거나 제거
	// =========================================================
	public int deleteItineraryJsonPlan(int planid, String loginId) throws SQLException{
		System.out.println("[MypageDao] deleteItineraryJsonPlan() 로그인한 아이디 : "+loginId +" , 일정표번호 : "+planid);
	    
		String sql = "DELETE FROM itineraryjson WHERE planid = ? AND memberid = ?";
	    int count = 0;

	    // getConnection() 부분은 프로젝트에서 쓰는 DB 연결 방식으로 바꾸기
	    try (Connection conn = DBCPUtil.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setInt(1, planid);
	        pstmt.setString(2, loginId);
	        count = pstmt.executeUpdate();   // 지워진 줄 수 (성공이면 1)

	    } catch (SQLException e) {
	        e.printStackTrace();
	        throw new SQLException("[MypageDao] deleteItineraryJsonPlan() 여행일정 삭제 실패 : "+loginId + " , 일정번호: "+planid);
	    }
	    return count;

	}

	// =========================================================
	// 주제: getOrCreateSharekey 처리
	// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
	// =========================================================
	public String getOrCreateSharekey(int planid, String loginId) {
		  String selectSql = "SELECT sharekey FROM itineraryjson WHERE planid = ? AND memberid = ?";
	        String updateSql = "UPDATE itineraryjson SET sharekey = ? "
	                         + "WHERE planid = ? AND memberid = ? AND sharekey IS NULL";
	 
	        try (Connection conn = DBCPUtil.getConnection()) {
	 
	            // 1. 내 일정인지 + 공유키가 있는지 확인
	            String sharekey;
	            try (PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
	                pstmt.setInt(1, planid);
	                pstmt.setString(2, loginId);
	 
	                try (ResultSet rs = pstmt.executeQuery()) {
	                    if (!rs.next()) {
	                        return null;               // 없는 일정 or 남의 일정
	                    }
	                    sharekey = rs.getString("sharekey");
	                }
	            }
	 
	            // 2. 이미 공유키가 있으면 그대로 사용
	            if (sharekey != null && !sharekey.isEmpty()) {
	                return sharekey;
	            }
	 
	            // 3. 없으면 새로 만들기 (UUID = 36자리, 컬럼 varchar(36)에 딱 맞음)
	            String newKey = UUID.randomUUID().toString();
	            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
	                pstmt.setString(1, newKey);
	                pstmt.setInt(2, planid);
	                pstmt.setString(3, loginId);
	 
	                int count = pstmt.executeUpdate();
	                if (count == 1) {
	                    return newKey;
	                }
	            }
	 
	            // 4. 거의 동시에 두 번 눌러서 다른 요청이 먼저 저장한 경우 → 저장된 키 다시 읽기
	            try (PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
	                pstmt.setInt(1, planid);
	                pstmt.setString(2, loginId);
	                try (ResultSet rs = pstmt.executeQuery()) {
	                    if (rs.next()) return rs.getString("sharekey");
	                }
	            }
	 
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return null;
	}
	
	 // =========================================================
	 // 주제: getPlanBySharekey 처리
	 // 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
	 // =========================================================
	 public ItineraryJsonVo getPlanBySharekey(String sharekey) {
	        String sql = "SELECT planid, title, planjson FROM itineraryjson WHERE sharekey = ?";
	        ItineraryJsonVo vo = null;
	 
	        try (Connection conn = DBCPUtil.getConnection();
	             PreparedStatement pstmt = conn.prepareStatement(sql)) {
	 
	            pstmt.setString(1, sharekey);
	 
	            try (ResultSet rs = pstmt.executeQuery()) {
	                if (rs.next()) {
	                    vo = new ItineraryJsonVo();
	                    vo.setPlanid(rs.getInt("planid"));
	                    vo.setTitle(rs.getString("title"));
	                    vo.setPlanjson(rs.getString("planjson"));
	                }
	            }
	 
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return vo;
	    }


	 // =========================================================
	 // 주제: getOnlyItineraryJsonContent 처리
	 // 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
	 // =========================================================
	 public String getOnlyItineraryJsonContent(int planid, String loginId) throws SQLException {
		  String sql = "SELECT planjson FROM itineraryjson WHERE planid = ? AND memberid = ?";
		    String planjson = null;

		    try (Connection conn = DBCPUtil.getConnection();
		         PreparedStatement pstmt = conn.prepareStatement(sql)) {

		        pstmt.setInt(1, planid);
		        pstmt.setString(2, loginId);

		        try (ResultSet rs = pstmt.executeQuery()) {
		            if (rs.next()) {
		                planjson = rs.getString("planjson");
		            }
		        }

		    } catch (SQLException e) {
		        e.printStackTrace();
		        throw new SQLException("[MypageDao] getOnlyItineraryJsonContent 여행일정표 중에서 json만 select 에러발생 "+e);
		    }
		    return planjson;
	 }
	
	
	
}
