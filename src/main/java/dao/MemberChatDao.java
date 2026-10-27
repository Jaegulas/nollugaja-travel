package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

// =========================================================
// 주제: 파일 역할
// 설명: MemberChatDao 관련 데이터베이스 조회·저장·수정·삭제를 담당하는 DAO
// =========================================================


/** Uses the project's existing Tomcat JNDI jdbc/travel datasource. */
public class MemberChatDao {
    private DataSource dataSource() throws NamingException {
        return (DataSource) new InitialContext().lookup("java:comp/env/jdbc/travel");
    }

    // =========================================================
    // 주제: findMemberName 처리
    // 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
    // =========================================================
    public String findMemberName(String id) throws Exception {
        String sql = "SELECT name FROM register WHERE id = ?";
        try (Connection c = dataSource().getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, id);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? r.getString(1) : null;
            }
        }
    }

    // =========================================================
    // 주제: save 처리
    // 설명: 입력받은 데이터를 생성하거나 저장
    // =========================================================
    public void save(String id, String message) throws Exception {
        String sql = "INSERT INTO live_chat_message (member_id, message) VALUES (?, ?)";
        try (Connection c = dataSource().getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, id);
            p.setString(2, message);
            p.executeUpdate();
        }
    }

    // =========================================================
    // 주제: recent 처리
    // 설명: recent 기능에 필요한 처리 로직을 수행
    // =========================================================
    public List<ChatRow> recent(int limit) throws Exception {
        String sql = "SELECT x.member_id, COALESCE(r.name, x.member_id) AS member_name, x.message, "
            + "DATE_FORMAT(x.created_at, '%Y-%m-%d %H:%i:%s') AS sent_at "
            + "FROM (SELECT member_id, message, created_at, id FROM live_chat_message ORDER BY id DESC LIMIT ?) x "
            + "LEFT JOIN register r ON r.id = x.member_id ORDER BY x.id ASC";
        List<ChatRow> rows = new ArrayList<>();
        try (Connection c = dataSource().getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, limit);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) rows.add(new ChatRow(r.getString(1), r.getString(2), r.getString(3), r.getString(4)));
            }
        }
        return rows;
    }

    public static final class ChatRow {
        public final String memberId, memberName, message, sentAt;
        // =========================================================
        // 주제: ChatRow 처리
        // 설명: ChatRow 기능에 필요한 처리 로직을 수행
        // =========================================================
        public ChatRow(String memberId, String memberName, String message, String sentAt) {
            this.memberId=memberId; this.memberName=memberName; this.message=message; this.sentAt=sentAt;
        }
    }
}
