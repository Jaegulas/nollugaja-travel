
package dao;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import vo.LoginVo;

// =========================================================
// 주제: 파일 역할
// 설명: KakaoDao 관련 데이터베이스 조회·저장·수정·삭제를 담당하는 DAO
// =========================================================


public class KakaoDao {

    // =========================================================
    // 주제: getConnection 처리
    // 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
    // =========================================================
    private Connection getConnection() throws Exception {

        Context init = new InitialContext();

        DataSource ds = (DataSource)
                init.lookup("java:comp/env/jdbc/travel");

        return ds.getConnection();
    }

    // =========================================================
    // 주제: findByKakaoId 처리
    // 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
    // =========================================================
    public LoginVo findByKakaoId(long kakaoId)
            throws Exception {

        String sql =
                "SELECT id, name, role " +
                "FROM register " +
                "WHERE kakao_id = ? " +
                "AND login_type = 'KAKAO'";

        try (
            Connection con = getConnection();
            PreparedStatement pstmt = con.prepareStatement(sql)
        ) {

            pstmt.setLong(1, kakaoId);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (!rs.next()) {
                    return null;
                }

                LoginVo member = new LoginVo();

                member.setId(rs.getString("id"));
                member.setName(rs.getString("name"));
                member.setRole(rs.getString("role"));

                return member;
            }
        }
    }

    // =========================================================
    // 주제: registerKakaoMember 처리
    // 설명: 입력받은 데이터를 생성하거나 저장
    // =========================================================
    public LoginVo registerKakaoMember(
            long kakaoId, String nickname)
            throws Exception {

        // 카카오 닉네임이 없는 경우
        if (nickname == null || nickname.trim().isEmpty()) {
            nickname = "카카오회원";
        }

        // name 컬럼 최대 20자
        if (nickname.length() > 20) {
            nickname = nickname.substring(0, 20);
        }

        String sql =
                "INSERT INTO register " +
                "(id, passwd, name, reg_date, role, " +
                "login_type, kakao_id) " +
                "VALUES (?, ?, ?, NOW(), 'USER', 'KAKAO', ?)";

        SecureRandom random = new SecureRandom();

        // 충돌 가능성을 낮추기 위한 임의 ID
        // 최대 12자: k + 11자리
        byte[] idBytes = new byte[9];
        random.nextBytes(idBytes);

        String memberId = "k" +
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(idBytes)
                        .substring(0, 11);

        // 카카오 회원의 일반 비밀번호 로그인 차단용
        // 추측할 수 없는 임의 값
        byte[] passwordBytes = new byte[32];
        random.nextBytes(passwordBytes);

        String disabledPassword =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(passwordBytes);

        try (
            Connection con = getConnection();
            PreparedStatement pstmt = con.prepareStatement(sql)
        ) {

            pstmt.setString(1, memberId);
            pstmt.setString(2, disabledPassword);
            pstmt.setString(3, nickname);
            pstmt.setLong(4, kakaoId);

            pstmt.executeUpdate();

            LoginVo member = new LoginVo();

            member.setId(memberId);
            member.setName(nickname);
            member.setRole("USER");

            System.out.println(
                "[KakaoDao] 카카오 신규 회원 등록 성공"
            );

            return member;
        }
    }

    // =========================================================
    // 주제: findOrCreate 처리
    // 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
    // =========================================================
    public LoginVo findOrCreate(
            long kakaoId, String nickname)
            throws Exception {

        if (kakaoId <= 0) {
            throw new IllegalArgumentException(
                "유효하지 않은 카카오 사용자 ID입니다."
            );
        }

        // 기존 카카오 회원 조회
        LoginVo member = findByKakaoId(kakaoId);

        if (member != null) {

            System.out.println(
                "[KakaoDao] 기존 카카오 회원 로그인"
            );

            System.out.println(
                "[KakaoDao] 회원 ID : " + member.getId()
            );

            return member;
        }

        // 신규 회원 등록
        try {

            System.out.println(
                "[KakaoDao] 기존 회원 없음 → 신규 회원가입 진행"
            );

            return registerKakaoMember(kakaoId, nickname);

        } catch (SQLException e) {

            // 동시에 가입 요청이 들어온 경우
            // UNIQUE 제약 조건 충돌 시 재조회
            if (e.getErrorCode() == 1062) {

                member = findByKakaoId(kakaoId);

                if (member != null) {
                    return member;
                }
            }

            throw e;
        }
    }
}

