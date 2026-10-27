
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import vo.LoginVo;

import org.mindrot.jbcrypt.BCrypt;

// =========================================================
// 주제: 파일 역할
// 설명: LoginDao 관련 데이터베이스 조회·저장·수정·삭제를 담당하는 DAO
// =========================================================


public class LoginDao {

	// =========================================================
	// 주제: getConnection 처리
	// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
	// =========================================================
	private Connection getConnection() throws Exception {

		Context init = new InitialContext();

		DataSource ds = (DataSource) init.lookup("java:comp/env/jdbc/travel");

		return ds.getConnection();
	}

	// =========================================================
	// 주제: login 처리
	// 설명: 로그인 관련 검증과 세션 처리를 수행
	// =========================================================
	public LoginVo login(LoginVo loginVO) throws Exception {

		// LOCAL 회원만 일반 로그인 가능
		String sql = "SELECT id, passwd, name, role " + "FROM register " + "WHERE id = ? AND login_type = 'LOCAL' AND status = 'ACTIVE'";

		try (Connection con = getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {

			// 사용자 아이디 설정
			pstmt.setString(1, loginVO.getId());

			System.out.println("[LoginDAO] SQL 실행 직전");

			// SQL 실행 및 ResultSet 자동 종료
			try (ResultSet rs = pstmt.executeQuery()) {

				System.out.println("[LoginDAO] SQL 실행 완료");

				// 아이디가 존재하지 않는 경우
				if (!rs.next()) {

					System.out.println("[LoginDAO] 존재하지 않는 아이디");

					return null;
				}

				// DB에 저장된 비밀번호
				String dbPasswd = rs.getString("passwd");

				// 사용자가 입력한 비밀번호
				String inputPasswd = loginVO.getPasswd();

				// ==========================================
				// 비밀번호 검증 (기존 평문 + BCrypt)
				// ==========================================

				if (dbPasswd == null || inputPasswd == null) {
					return null;
				}

				boolean passwordMatches = false;

				// BCrypt 형식인지 확인
				boolean isBCrypt = dbPasswd.matches("^\\$2a\\$\\d{2}\\$.{53}$");

				if (isBCrypt) {

					// BCrypt 비밀번호 검증
					try {
						passwordMatches = BCrypt.checkpw(inputPasswd, dbPasswd);
					} catch (IllegalArgumentException e) {
						return null;
					}

				} else {

					// 기존 회원의 평문 비밀번호 검증
					passwordMatches = dbPasswd.equals(inputPasswd);
				}

				if (!passwordMatches) {

					System.out.println("[LoginDAO] 비밀번호 불일치");

					return null;
				}

				// 기존 평문 비밀번호를 BCrypt로 자동 전환
				if (!isBCrypt) {

					String hashedPassword = BCrypt.hashpw(inputPasswd, BCrypt.gensalt(12));

					String updateSql = "UPDATE register SET passwd = ? "
							+ "WHERE id = ? AND passwd = ? AND login_type = 'LOCAL' AND status = 'ACTIVE'";

					try (PreparedStatement updateStmt = con.prepareStatement(updateSql)) {

						updateStmt.setString(1, hashedPassword);
						updateStmt.setString(2, loginVO.getId());
						updateStmt.setString(3, dbPasswd);

						updateStmt.executeUpdate();
					}
				}

				// 로그인 성공 시 회원정보 반환
				LoginVo member = new LoginVo();

				member.setId(rs.getString("id"));
				member.setName(rs.getString("name"));
				member.setRole(rs.getString("role"));

				System.out.println("[LoginDAO] 로그인 성공 : " + member.getId());

				return member;
			}

		} catch (SQLException e) {

			System.err.println("[LoginDAO] SQL 오류 발생");

			System.err.println("SQLState : " + e.getSQLState());

			System.err.println("Error Code : " + e.getErrorCode());

			System.err.println("Message : " + e.getMessage());

			throw e;
		}
	}
}
