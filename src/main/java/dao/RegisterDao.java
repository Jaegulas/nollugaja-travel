package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import org.mindrot.jbcrypt.BCrypt;

import dto.RegisterDto;
import vo.RegisterVo;

// =========================================================
// 주제: 파일 역할
// 설명: RegisterDao 관련 데이터베이스 조회·저장·수정·삭제를 담당하는 DAO
// =========================================================



/**
 * 놀러가자 - 회원관리 DAO
 *
 * DB 테이블 : register
 *
 * 주요 기능 1. 아이디 중복 확인 2. 회원가입 3. 로그인용 비밀번호 조회 4. 회원정보 조회 5. 회원정보 수정 6. 회원 탈퇴
 */
public class RegisterDao {

	private DataSource ds;

	// =========================================================
	// 주제: RegisterDao 처리
	// 설명: 입력받은 데이터를 생성하거나 저장
	// =========================================================
	public RegisterDao() {

		try {

			InitialContext ctx = new InitialContext();

			ds = (DataSource) ctx.lookup("java:comp/env/jdbc/travel");

			System.out.println("[RegisterDAO] JNDI 리소스 조회 성공");

		} catch (Exception e) {

			System.err.println("[RegisterDAO] JNDI 리소스 조회 실패");

			e.printStackTrace();

			throw new IllegalStateException("[RegisterDAO] DB 커넥션풀 조회 실패", e);
		}
	}

	// ==========================================
	// 1. 아이디 중복 확인
	//
	// true : 이미 존재하는 아이디
	// false : 사용 가능한 아이디
	// ==========================================
	// =========================================================
	// 주제: overlappedId 처리
	// 설명: overlappedId 기능에 필요한 처리 로직을 수행
	// =========================================================
	public boolean overlappedId(String id) {

		String sql = "SELECT id FROM register WHERE id = ?";

		try (Connection con = ds.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {

			System.out.println("[RegisterDAO] 아이디 중복확인 DB 연결 성공");

			System.out.println("[RegisterDAO] 현재 DB : " + con.getCatalog());

			System.out.println("[RegisterDAO] 확인할 아이디 : " + id);

			pstmt.setString(1, id);

			try (ResultSet rs = pstmt.executeQuery()) {

				// 한 행이라도 존재하면 중복
				boolean duplicated = rs.next();

				System.out.println("[RegisterDAO] 아이디 중복 여부 : " + duplicated);

				return duplicated;
			}

		} catch (SQLException e) {

			System.err.println("[RegisterDAO] 아이디 중복 확인 SQL 오류");

			System.err.println("SQLState : " + e.getSQLState());

			System.err.println("Error Code : " + e.getErrorCode());

			System.err.println("Message : " + e.getMessage());

			e.printStackTrace();

			throw new IllegalStateException("아이디 중복 확인 중 DB 오류가 발생했습니다.", e);
		}
	}

	// =========================================================
	// 주제: insertMember 처리
	// 설명: 입력받은 데이터를 생성하거나 저장
	// =========================================================
	public int insertMember(RegisterVo vo) {

		String sql = "INSERT INTO register " + "(id, passwd, name, reg_date, age, "
				+ "gender, email, address, tel, hp) " + "VALUES (?, ?, ?, NOW(), ?, ?, ?, ?, ?, ?)";

		try (Connection con = ds.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {

			pstmt.setString(1, vo.getId());

			// 회원가입 비밀번호 BCrypt 암호화
			String hashedPassword = BCrypt.hashpw(vo.getPass(), BCrypt.gensalt(12));

			pstmt.setString(2, hashedPassword);

			pstmt.setString(3, vo.getName());
			pstmt.setInt(4, vo.getAge());
			pstmt.setString(5, vo.getGender());
			pstmt.setString(6, vo.getEmail());
			pstmt.setString(7, vo.getAddress());
			pstmt.setString(8, vo.getTel());
			pstmt.setString(9, vo.getHp());

			return pstmt.executeUpdate();

		} catch (SQLException e) {

			e.printStackTrace();

			throw new IllegalStateException("회원가입 중 DB 오류가 발생했습니다.", e);
		}
	}

	// =========================================================
	// 주제: getPassword 처리
	// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
	// =========================================================
	public String getPassword(String id) {

		String sql = "SELECT passwd FROM register WHERE id = ?";

		try (Connection con = ds.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {

			pstmt.setString(1, id);

			try (ResultSet rs = pstmt.executeQuery()) {

				if (rs.next()) {

					return rs.getString("passwd");
				}
			}

		} catch (SQLException e) {

			e.printStackTrace();

			throw new IllegalStateException("비밀번호 조회 중 DB 오류가 발생했습니다.", e);
		}

		return null;
	}

	// =========================================================
	// 주제: memberDetail 처리
	// 설명: memberDetail 기능에 필요한 처리 로직을 수행
	// =========================================================
	public RegisterVo memberDetail(String id) {

		String sql = "SELECT id, name, age, gender, email, " + "address, tel, hp " + "FROM register " + "WHERE id = ?";

		try (Connection con = ds.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {

			pstmt.setString(1, id);

			try (ResultSet rs = pstmt.executeQuery()) {

				if (rs.next()) {

					RegisterVo vo = new RegisterVo();

					vo.setId(rs.getString("id"));

					vo.setName(rs.getString("name"));

					vo.setAge(rs.getInt("age"));

					vo.setGender(rs.getString("gender"));

					vo.setEmail(rs.getString("email"));

					vo.setAddress(rs.getString("address"));

					vo.setTel(rs.getString("tel"));

					vo.setHp(rs.getString("hp"));

					return vo;
				}
			}

		} catch (SQLException e) {

			e.printStackTrace();

			throw new IllegalStateException("회원정보 조회 중 DB 오류가 발생했습니다.", e);
		}

		return null;
	}

	// =========================================================
	// 주제: updateMember 처리
	// 설명: 기존 데이터를 입력값에 맞게 수정
	// =========================================================
	public int updateMember(RegisterVo vo) {

		boolean changePassword = vo.getPass() != null && !vo.getPass().trim().isEmpty();

		String sql;

		if (changePassword) {

			sql = "UPDATE register SET " + "passwd=?, " + "name=?, " + "age=?, " + "gender=?, " + "email=?, "
					+ "address=?, " + "tel=?, " + "hp=? " + "WHERE id=?";

		} else {

			sql = "UPDATE register SET " + "name=?, " + "age=?, " + "gender=?, " + "email=?, " + "address=?, "
					+ "tel=?, " + "hp=? " + "WHERE id=?";
		}

		try (Connection con = ds.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {

			int idx = 1;

			if (changePassword) {

				// 변경할 비밀번호를 BCrypt로 암호화
				String hashedPassword = BCrypt.hashpw(vo.getPass(), BCrypt.gensalt(12));

				pstmt.setString(idx++, hashedPassword);
			}

			pstmt.setString(idx++, vo.getName());

			pstmt.setInt(idx++, vo.getAge());

			pstmt.setString(idx++, vo.getGender());

			pstmt.setString(idx++, vo.getEmail());

			pstmt.setString(idx++, vo.getAddress());

			pstmt.setString(idx++, vo.getTel());

			pstmt.setString(idx++, vo.getHp());

			pstmt.setString(idx, vo.getId());

			return pstmt.executeUpdate();

		} catch (SQLException e) {

			e.printStackTrace();

			throw new IllegalStateException("회원정보 수정 중 DB 오류가 발생했습니다.", e);
		}
	}

	// =========================================================
	// 주제: 회원 탈퇴 비밀번호 확인
	// 설명: 일반 로그인 회원의 현재 비밀번호를 BCrypt/기존 평문 방식 모두 확인
	// =========================================================
	public boolean checkWithdrawPassword(String id, String inputPassword) {
		if (id == null || inputPassword == null || inputPassword.isEmpty()) return false;

		String sql = "SELECT passwd FROM register "
				+ "WHERE id = ? AND login_type = 'LOCAL' AND status = 'ACTIVE'";

		try (Connection con = ds.getConnection();
			 PreparedStatement pstmt = con.prepareStatement(sql)) {

			pstmt.setString(1, id);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (!rs.next()) return false;

				String dbPasswd = rs.getString("passwd");
				if (dbPasswd == null || dbPasswd.isEmpty()) return false;

				if (dbPasswd.startsWith("$2a$") || dbPasswd.startsWith("$2b$") || dbPasswd.startsWith("$2y$")) {
					try {
						return BCrypt.checkpw(inputPassword, dbPasswd);
					} catch (IllegalArgumentException e) {
						return false;
					}
				}

				return dbPasswd.equals(inputPassword);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new IllegalStateException("회원 탈퇴 비밀번호 확인 중 DB 오류가 발생했습니다.", e);
		}
	}

	// =========================================================
	// 주제: 회원 탈퇴 상태값 + 익명화 처리
	// 설명: register.id는 FK 유지를 위해 보존하고 개인정보/개인 데이터를 정리한 뒤 WITHDRAWN 상태로 변경
	// =========================================================
	public boolean withdrawMember(String id) {
		if (id == null || id.trim().isEmpty()) return false;

		Connection con = null;
		try {
			con = ds.getConnection();
			con.setAutoCommit(false);

			String[] personalDataSqls = {
				"DELETE FROM accommodation_like WHERE member_id = ?",
				"DELETE FROM travel_like WHERE member_id = ?",
				"DELETE FROM food_like WHERE member_id = ?",
				"DELETE FROM cultural_like WHERE member_id = ?",
				"DELETE FROM guide_like WHERE member_id = ?",
				"DELETE FROM tour_like WHERE member_id = ?",
				"DELETE FROM review_like WHERE member_id = ?",
				"DELETE FROM review_view_log WHERE member_id = ?"
			};

			for (String sql : personalDataSqls) {
				try (PreparedStatement pstmt = con.prepareStatement(sql)) {
					pstmt.setString(1, id);
					pstmt.executeUpdate();
				}
			}

			try (PreparedStatement pstmt = con.prepareStatement(
					"DELETE FROM itineraryjson WHERE memberid = ?")) {
				pstmt.setString(1, id);
				pstmt.executeUpdate();
			}

			try (PreparedStatement pstmt = con.prepareStatement(
					"UPDATE review SET author_nickname = '탈퇴회원' WHERE member_id = ?")) {
				pstmt.setString(1, id);
				pstmt.executeUpdate();
			}

			try (PreparedStatement pstmt = con.prepareStatement(
					"UPDATE review_comment SET author_nickname = '탈퇴회원' WHERE member_id = ?")) {
				pstmt.setString(1, id);
				pstmt.executeUpdate();
			}

			String disabledPassword = BCrypt.hashpw(UUID.randomUUID().toString(), BCrypt.gensalt(12));
			String updateSql = "UPDATE register SET "
					+ "passwd = ?, name = '탈퇴회원', age = NULL, gender = NULL, "
					+ "email = NULL, address = NULL, tel = NULL, hp = NULL, "
					+ "kakao_id = NULL, status = 'WITHDRAWN', withdrawn_at = NOW() "
					+ "WHERE id = ? AND status = 'ACTIVE'";

			int updated;
			try (PreparedStatement pstmt = con.prepareStatement(updateSql)) {
				pstmt.setString(1, disabledPassword);
				pstmt.setString(2, id);
				updated = pstmt.executeUpdate();
			}

			if (updated != 1) {
				con.rollback();
				return false;
			}

			con.commit();
			return true;

		} catch (SQLException e) {
			if (con != null) {
				try { con.rollback(); } catch (SQLException ignored) {}
			}
			e.printStackTrace();
			throw new IllegalStateException("회원 탈퇴 처리 중 DB 오류가 발생했습니다.", e);
		} finally {
			if (con != null) {
				try { con.setAutoCommit(true); } catch (SQLException ignored) {}
				try { con.close(); } catch (SQLException ignored) {}
			}
		}
	}

	// =========================================================
	// 주제: 기존 회원 삭제 메서드 호환
	// 설명: 기존 호출부는 유지하면서 실제 처리는 상태값 + 익명화 방식으로 연결
	// =========================================================
	public int deleteMember(String id) {
		return withdrawMember(id) ? 1 : 0;
	}


	// ==========================================
	// 7.기간별 회원정보 조회 - 관리자페이지용
	// ==========================================

	// =========================================================
	// 주제: allMemberDetail 처리
	// 설명: allMemberDetail 기능에 필요한 처리 로직을 수행
	// =========================================================

	public List<RegisterDto> allMemberDetail(String startDate, String endDate) throws SQLException {

		List<RegisterDto> dtolist = new ArrayList<>();

		StringBuilder sql = new StringBuilder("SELECT * FROM register ");
		List<Object> params = new ArrayList<>();

		// 조건이 있을 때만 붙이기
		if (startDate != null && !startDate.isBlank()) {
			sql.append(" where reg_date >= ? ");
			params.add(startDate);
		}
		if (endDate != null && !endDate.isBlank()) {
			sql.append(params.isEmpty() ? " WHERE reg_date < ? " : " AND reg_date < ? ");
			params.add(endDate);
		}
		sql.append(" ORDER BY reg_date DESC");

		try (Connection con = ds.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql.toString())) {

			// ? 순서대로 값 세팅 (인덱스는 1부터)
			for (int i = 0; i < params.size(); i++) {
				pstmt.setObject(i + 1, params.get(i));
			}

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {

				RegisterDto dto = new RegisterDto();
				dto.setId(rs.getString("id"));
				dto.setName(rs.getString("name"));
				dto.setAge(rs.getInt("age"));
				dto.setGender(rs.getString("gender"));
				dto.setEmail(rs.getString("email"));
				dto.setAddress(rs.getString("address"));
				dto.setTel(rs.getString("tel"));
				dto.setHp(rs.getString("hp"));
				dto.setReg_date(rs.getObject("reg_date", LocalDateTime.class));
				dtolist.add(dto);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new IllegalStateException("기간별 회원정보 조회 allMemberDetail() 중 DB 오류가 발생했습니다.", e);
		}

		return dtolist;
	}
	
	
	// =========================================================
	// 8.전체 회원 수 (페이지 개수 계산용) - 관리자페이지용 2026.10.20화
	// =========================================================
	public int countAllMembers() {
	    String sql = "SELECT COUNT(*) FROM register";

	    try (Connection con = ds.getConnection();
	         PreparedStatement pstmt = con.prepareStatement(sql);
	         ResultSet rs = pstmt.executeQuery()) {

	        if (rs.next()) {
	            return rs.getInt(1);
	        }
	        return 0;

	    } catch (SQLException e) {
	        e.printStackTrace();
	        throw new IllegalStateException("전체 회원 수 조회(페이지 개수 계산용) countAllMembers() 중 DB 오류가 발생했습니다.", e);
	    }
	}

	// =========================================================
	// 9. 전체 회원 한 페이지 분량 (최신 가입순) - 관리자페이지용 2026.10.20화
	//   offset : 건너뛸 개수 (2페이지, 10개씩이면 10)
	//   size   : 가져올 개수
	// =========================================================
	public List<RegisterDto> allMemberPage(int offset, int size) {
	    List<RegisterDto> dtolist = new ArrayList<>();

	    // 가입일이 같은 회원이 있어도 페이지마다 순서가 바뀌지 않게 id 로 한 번 더 정렬
	    String sql = "SELECT * FROM register ORDER BY reg_date DESC, id ASC LIMIT ? OFFSET ?";

	    try (Connection con = ds.getConnection();
	         PreparedStatement pstmt = con.prepareStatement(sql)) {

	        pstmt.setInt(1, size);
	        pstmt.setInt(2, offset);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                RegisterDto dto = new RegisterDto();
	                dto.setId(rs.getString("id"));
	                dto.setName(rs.getString("name"));
	                dto.setAge(rs.getInt("age"));
	                dto.setGender(rs.getString("gender"));
	                dto.setEmail(rs.getString("email"));
	                dto.setAddress(rs.getString("address"));
	                dto.setTel(rs.getString("tel"));
	                dto.setHp(rs.getString("hp"));
	                dto.setReg_date(rs.getObject("reg_date", LocalDateTime.class));
	                dtolist.add(dto);
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        throw new IllegalStateException("전체 회원 한 페이지 분량 (최신 가입순) allMemberPage() 중 DB 오류가 발생했습니다.", e);
	    }

	    return dtolist;
	}

}
