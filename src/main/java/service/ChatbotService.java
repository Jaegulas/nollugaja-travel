package service;

import java.sql.SQLException;
import java.util.List;

import dao.ChatbotDao;
import dto.ChatbotDto;
import vo.ChatbotRequestVo;
import vo.ChatbotVo;
import vo.ItineraryJsonVo;

// =========================================================
// 주제: 파일 역할
// 설명: ChatbotService의 핵심 비즈니스 로직과 데이터 가공을 담당하는 서비스
// =========================================================

public class ChatbotService {

	
    private ChatbotDao cd;
    
    
    
    
    // =========================================================
    // 주제: ChatbotService 처리
    // 설명: ChatbotService 기능에 필요한 처리 로직을 수행
    // =========================================================
    public ChatbotService() {
		super();
		cd = new ChatbotDao();
	}



	// =========================================================
	// 주제: chatbotConversationProInsert 처리
	// 설명: chatbotConversationProInsert 기능에 필요한 처리 로직을 수행
	// =========================================================
	/*
	 * public int chatbotConversationProInsert(ChatbotDto chatbotDto) throws
	 * SQLException{ return cd.chatbotConversationProInsert(chatbotDto); }
	 */



	// =========================================================
	// 주제: countItineraryByMember 처리
	// 설명: countItineraryByMember 기능에 필요한 처리 로직을 수행
	// =========================================================
	/*
	 * public int countItineraryByMember(String loginId) throws SQLException{ return
	 * cd.countItineraryByMember(loginId); }
	 */



	// =========================================================
	// 주제: itineraryjsonInsert 처리
	// 설명: itineraryjsonInsert 기능에 필요한 처리 로직을 수행
	// =========================================================
	public int itineraryjsonInsert(String title, String planjson, String loginId, int MAX_COUNT) throws SQLException {
		return cd.itineraryjsonInsert(title, planjson, loginId, MAX_COUNT);
	}



	// =========================================================
	// 주제: itineraryjsonList 처리
	// 설명: itineraryjsonList 기능에 필요한 처리 로직을 수행
	// =========================================================
	public List<ItineraryJsonVo> itineraryjsonList(String loginId) throws SQLException {
		return cd.itineraryjsonList(loginId);
	}
	


	// =========================================================
	// 주제: newInsertChatbotRequest 처리
	// 설명: newInsertChatbotRequest 기능에 필요한 처리 로직을 수행
	// =========================================================
	public int newInsertChatbotRequest(ChatbotRequestVo vo) throws SQLException {
		return cd.newInsertChatbotRequest(vo);
	}
}
