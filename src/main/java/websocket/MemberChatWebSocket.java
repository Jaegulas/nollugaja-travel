package websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.MemberChatDao;
import dao.MemberChatDao.ChatRow;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.*;

// =========================================================
// 주제: 파일 역할
// 설명: MemberChatWebSocket 실시간 WebSocket 통신을 담당
// =========================================================


/** Tomcat 9 / javax.websocket; never trusts user-supplied names or IDs. */
@ServerEndpoint(value="/member-chat", configurator=MemberChatWebSocket.HttpSessionConfigurator.class)
public class MemberChatWebSocket {
    // IMPORTANT: Replace with the EXACT HttpSession attribute written by your login controller.
    // This implementation assumes session.setAttribute("loginId", register.id).
    private static final String LOGIN_SESSION_KEY = "loginId";
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final MemberChatDao DAO = new MemberChatDao();
    private static final Set<Session> CLIENTS = ConcurrentHashMap.newKeySet();

    public static class HttpSessionConfigurator extends ServerEndpointConfig.Configurator {
        @Override public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest req, HandshakeResponse resp) {
            Object session = req.getHttpSession();
            if (session instanceof HttpSession) sec.getUserProperties().put("httpSession", session);
        }
    }

    // =========================================================
    // 주제: memberId 처리
    // 설명: memberId 기능에 필요한 처리 로직을 수행
    // =========================================================
    private static String memberId(Session socket) {
        Object obj = socket.getUserProperties().get("httpSession");
        if (!(obj instanceof HttpSession)) return null;
        try {
            Object id = ((HttpSession)obj).getAttribute(LOGIN_SESSION_KEY);
            if (!(id instanceof String)) return null;
            String value = ((String) id).trim();
            return value.isEmpty() ? null : value;
        } catch (IllegalStateException ex) { return null; }
    }

    @OnOpen public void open(Session socket) throws Exception {
        String id = memberId(socket);
        if (id == null) { socket.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY,"Login required")); return; }
        String name = DAO.findMemberName(id);
        if (name == null) { socket.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY,"Member not found")); return; }
        socket.getUserProperties().put("memberId", id);
        socket.getUserProperties().put("memberName", name);
        CLIENTS.add(socket);
        send(socket, event("ready", id, name, "채팅에 연결됐습니다.", null));
        for (ChatRow row : DAO.recent(30)) send(socket, event("history", row.memberId, row.memberName, row.message, row.sentAt));
    }

    @OnMessage public void message(String raw, Session socket) throws Exception {
        if (!CLIENTS.contains(socket) || !socket.isOpen()) return;
        String id = memberId(socket);
        if (id == null || !id.equals(socket.getUserProperties().get("memberId"))) {
            socket.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY,"Session expired")); return;
        }
        if (raw == null) return;
        String content = raw.trim();
        if (content.isEmpty()) return;
        if (content.length() > 500) { send(socket, event("error", null, null, "메시지는 500자 이내로 입력하세요.", null)); return; }
        String name = (String) socket.getUserProperties().get("memberName");
        try { DAO.save(id, content); }
        catch (Exception ex) { send(socket, event("error", null, null, "메시지를 저장하지 못했습니다.", null)); throw ex; }
        String payload = event("message", id, name, content, java.time.LocalDateTime.now().toString());
        for (Session client : CLIENTS) {
            if (client.isOpen()) {
                try { send(client, payload); } catch (Exception ignored) { CLIENTS.remove(client); }
            }
        }
    }

    @OnClose public void close(Session socket) { CLIENTS.remove(socket); }
    @OnError public void error(Session socket, Throwable ex) { CLIENTS.remove(socket); }

    // =========================================================
    // 주제: event 처리
    // 설명: event 기능에 필요한 처리 로직을 수행
    // =========================================================
    private static String event(String type, String id, String name, String message, String time) throws Exception {
        Map<String,String> data = new LinkedHashMap<>();
        data.put("type", type); data.put("memberId", id); data.put("memberName", name);
        data.put("message", message); data.put("time", time);
        return JSON.writeValueAsString(data);
    }
    // =========================================================
    // 주제: send 처리
    // 설명: 필요한 데이터를 외부 또는 사용자에게 전송
    // =========================================================
    private static void send(Session socket, String data) throws Exception {
        if (socket.isOpen()) socket.getBasicRemote().sendText(data);
    }
}
