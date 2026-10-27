
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.nio.charset.StandardCharsets;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.mindrot.jbcrypt.BCrypt;

// =========================================================
// 주제: 파일 역할
// 설명: AccountRecoveryDao 관련 데이터베이스 조회·저장·수정·삭제를 담당하는 DAO
// =========================================================


public class AccountRecoveryDao {

    private final DataSource ds;

    // =========================================================
    // 주제: AccountRecoveryDao 처리
    // 설명: AccountRecoveryDao 기능에 필요한 처리 로직을 수행
    // =========================================================
    public AccountRecoveryDao() {

        try {

            InitialContext ctx = new InitialContext();

            ds = (DataSource) ctx.lookup(
                    "java:comp/env/jdbc/travel"
            );

        } catch (Exception e) {

            throw new IllegalStateException(
                    "DB 연결 설정을 확인해주세요.", e
            );
        }
    }

    // ==========================================
    // 2. 아이디 찾기
    // 이름 + 이메일 일치 회원 조회
    // ==========================================
    // =========================================================
    // 주제: findId 처리
    // 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
    // =========================================================
    public String findId(String name, String email) {

        String sql =
                "SELECT id FROM register " +
                "WHERE name = ? AND email = ? " +
                "AND login_type = 'LOCAL' AND status = 'ACTIVE' LIMIT 1";

        try (
            Connection con = ds.getConnection();
            PreparedStatement pstmt = con.prepareStatement(sql)
        ) {

            pstmt.setString(1, name);
            pstmt.setString(2, email);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    return rs.getString("id");
                }
            }

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "아이디 조회 중 DB 오류가 발생했습니다.", e
            );
        }

        return null;
    }

    // ==========================================
    // 3. 아이디 존재 여부 확인
    // 일반 회원만 조회
    // ==========================================
    // =========================================================
    // 주제: existsLocalId 처리
    // 설명: existsLocalId 기능에 필요한 처리 로직을 수행
    // =========================================================
    public boolean existsLocalId(String id) {

        String sql =
                "SELECT 1 FROM register " +
                "WHERE id = ? " +
                "AND login_type = 'LOCAL' AND status = 'ACTIVE' LIMIT 1";

        try (
            Connection con = ds.getConnection();
            PreparedStatement pstmt = con.prepareStatement(sql)
        ) {

            pstmt.setString(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "아이디 존재 여부 확인 중 DB 오류가 발생했습니다.", e
            );
        }
    }

    // =========================================================
    // 주제: existsLocalMember 처리
    // 설명: existsLocalMember 기능에 필요한 처리 로직을 수행
    // =========================================================
    public boolean existsLocalMember(String id, String email) {

        String sql =
                "SELECT 1 FROM register " +
                "WHERE id = ? AND email = ? " +
                "AND login_type = 'LOCAL' AND status = 'ACTIVE' LIMIT 1";

        try (
            Connection con = ds.getConnection();
            PreparedStatement pstmt = con.prepareStatement(sql)
        ) {

            pstmt.setString(1, id);
            pstmt.setString(2, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "회원 확인 중 DB 오류가 발생했습니다.", e
            );
        }
    }

    // ==========================================
    // 5. 비밀번호 재설정
    // 이메일 인증 완료 후 Controller에서 호출
    // ==========================================
    // =========================================================
    // 주제: resetPassword 처리
    // 설명: resetPassword 기능에 필요한 처리 로직을 수행
    // =========================================================
    public boolean resetPassword(
            String id,
            String email,
            String newPassword) {

        if (id == null || id.isBlank() ||
            email == null || email.isBlank() ||
            newPassword == null ||
            newPassword.length() < 8 ||
            newPassword.getBytes(StandardCharsets.UTF_8).length > 72) {

            throw new IllegalArgumentException(
                    "비밀번호 재설정 입력값이 올바르지 않습니다."
            );
        }

        // BCrypt 암호화
        String hashedPassword = BCrypt.hashpw(
                newPassword, BCrypt.gensalt(12)
        );

        String sql =
                "UPDATE register SET passwd = ? " +
                "WHERE id = ? AND email = ? " +
                "AND login_type = 'LOCAL' AND status = 'ACTIVE'";

        try (
            Connection con = ds.getConnection();
            PreparedStatement pstmt = con.prepareStatement(sql)
        ) {

            pstmt.setString(1, hashedPassword);
            pstmt.setString(2, id);
            pstmt.setString(3, email);

            return pstmt.executeUpdate() == 1;

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "비밀번호 변경 중 DB 오류가 발생했습니다.", e
            );
        }
    }
}
