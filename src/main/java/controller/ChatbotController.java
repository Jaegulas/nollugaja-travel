package controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.modelmapper.ModelMapper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import api.AccommodationChatbotOpenrouterApi;
import config.AppConfigListener;
import dto.ChatbotDto;
import service.ChatbotService;
import vo.ChatbotDayVo;
import vo.ChatbotRequestVo;
import vo.ChatbotVo;
import vo.ItineraryJsonVo;

// =========================================================
// 주제: 파일 역할
// 설명: ChatbotController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


//ChatbotController클래스 정리
//1.@MultipartConfig 사용
//2.Part 라이브러리 사용


@WebServlet("/Chatbot/*")
//@MultipartConfig(
//	    fileSizeThreshold = 1024 * 1024 * 2, // 2MB 이 크기까지는 메모리에 잠깐 두고, 넘으면 **임시 파일(디스크)**로 받아둠 기본값은0 (항상 디스크)
//	    maxFileSize = 1024 * 1024 * 10,      // 10MB
//	    maxRequestSize = 1024 * 1024 * 11    // 50MB 파일 + 제목 등 요청 전체의 최대 크기	 기본값-1 (무제한)
//	    //req.getPart()에서 IllegalStateException
//	)
public class ChatbotController extends HttpServlet{
	
	//private static final String UPLOAD_DIR = FileRepoRoot.getFileRepoRoot(); // 실제 파일 저장 경로 윈도우 리눅스 구분
	
	// [직렬화 번호] 서블릿은 규칙상 이 번호를 갖는 게 좋다. 없으면 노란 경고줄이 뜬다. 기능과는 무관하니 그냥 1L 로 둔다.
	private static final long serialVersionUID = 1L;

	// AI 서버의 주소. static final = "절대 안 바뀌는 값" 이라는 표시(상수).
	private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";

	// 사용할 무료 모델 이름. openrouter/free 는 "지금 쓸 수 있는 무료 모델 아무거나 알아서 골라줘" 라는 뜻.
	private static final String DEFAULT_MODEL = "openrouter/free";


	// 실제로 사용할 모델명을 담는 변수. 상수(DEFAULT_MODEL)를 기본값으로 복사해 둔다.
	// [왜 상수를 그대로 안 쓰고 변수에 담나?] 무료 모델 목록은 자주 바뀐다.
	// 변수로 두면 나중에 설정파일에서 읽어와 '재컴파일 없이' 모델을 바꿀 수 있다.
	private String modelName = DEFAULT_MODEL;

	private ChatbotService cs;
	
	private AccommodationChatbotOpenrouterApi openrouterApi;
	
	//파일업로드 개수 지정 15개까지
	private static final int MAX_COUNT =15;
	
	private static final ObjectMapper mapper = new ObjectMapper()
	        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	// 최대 일차 
	private static final int MAX_DAYS = 10;                    

	
	 // 화면과 같은 선택지 (이 목록에 없는 값은 저장 안 함)
    private static final List<String> REGIONS = List.of(
        "서울","인천","경기","강원","대전","세종","충북","충남","광주",
        "전북","전남","부산","대구","울산","경북","경남","제주");
    private static final List<String> STAYS = List.of("호텔","펜션","게스트하우스","콘도","모텔");
    
    private static final List<String> STYLES = List.of(
        "휴양/힐링형","관광/랜드마크형","미식형","액티비티/체험형","문화/예술형","쇼핑형");
    
    
	// =========================================================
	// 주제: doHandle 처리
	// 설명: doHandle 기능에 필요한 처리 로직을 수행
	// =========================================================
	private void doHandle(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		resp.setContentType("text/html; charset=UTF-8");
		String contextPath = req.getContextPath();
		
		String action = req.getPathInfo();
		if (action == null) {
		    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
		    return;
		}
		
		
		System.out.println("[ChatbotController] doHandle() getPathInfo() 값 "+action);
		
		if(action.equals("/newconversation")) {
			
			if ("POST".equalsIgnoreCase(req.getMethod())) {     // 대소문자 상관없이 비교
		        req.setCharacterEncoding("UTF-8");               // getParameter보다 먼저

		        String startDate = req.getParameter("startDate");
		        String endDate   = req.getParameter("endDate");
		        String people    = req.getParameter("people");
		        String region    = req.getParameter("region");
		        System.out.println("startDate: " + startDate + ", endDate: " + endDate
		                + ", people: " + people + ", region: " + region);

		        // JSP로 넘기기 (이게 있어야 ${startDate}에 값이 들어감)
		        req.setAttribute("startDate", startDate);
		        req.setAttribute("endDate", endDate);
		        req.setAttribute("people", people);
		        req.setAttribute("region", region);
		    }

			req.setAttribute("center", "chatbot/newconversation.jsp");
			req.setAttribute("pageCss", "chatbot/newconversation.css");
			req.getRequestDispatcher("/WEB-INF/main.jsp").forward(req, resp);
			return;
		
		
		}else if(action.equals("/itineraryjson")) {
		    resp.setContentType("application/json; charset=utf-8");
		    PrintWriter out = resp.getWriter();
	
	        // 링크(GET)로 저장되지 않게 POST만 허용
	        if (!"POST".equals(req.getMethod())) {
	            resp.setStatus(400);
	            out.print("{\"success\": false, \"message\": \"잘못된 요청 방식입니다.\"}");
	            return;
	        }
	        
		    // 로그인 확인
		    String loginId = sessionCheck(req);
	
		    if (loginId == null) {
		        resp.setStatus(401);
		        out.print("{\"success\": false, \"loginNeed\": true, \"message\": \"로그인이 필요합니다.\"}");
		        return;
		    }
	
		    // 파라미터 받기
		    String title = req.getParameter("title");
		    String planjson = req.getParameter("planjson");
	
		    if (planjson == null || planjson.trim().isEmpty()) {
		        resp.setStatus(400);
		        out.print("{\"success\": false, \"message\": \"저장할 일정 데이터가 없습니다.\"}");
		        return;
		    }
		    if (title == null || title.trim().isEmpty()) {
		        title = "나의 여행 일정";
		    }
	
		    try {
		        int insertresult = cs.itineraryjsonInsert(title, planjson, loginId, MAX_COUNT);
		        System.out.println("[ChatbotController] /Chatbot/itineraryjson 결과 0 이면 15개 넘은상황 아니면 insert한 planid 번호 : "+insertresult);
		        if (insertresult == 0) {
		        	resp.setStatus(409);
		            out.print("{\"success\": false, \"message\": \"일정은 최대 " + MAX_COUNT + "개까지 저장할 수 있어요.\"}");
		            return;
		        }
	
		        out.print("{\"success\":true,\"message\":\"저장되었습니다.\",\"planid\":" + insertresult + "}");
		        return;
		    } catch (SQLException e) {
		        e.printStackTrace();
		        resp.setStatus(500);
		        out.print("{\"success\": false, \"message\": \"저장 중 오류가 발생했습니다.\"}");
		        return;
		    }
		} 
		else if(action.equals("/newConversationPro")) {
			req.setAttribute("center", "chatbot/newconversation.jsp");
			req.setAttribute("pageCss", "chatbot/newconversation.css");
			resp.setContentType("text/json; charset=UTF-8");
			PrintWriter out = resp.getWriter();
			System.out.println("[ChatbotController] /Chatbot/newConversationPro 오픈라우터요청 처리 ObjectMapper객체가 json읽어서 DTO 담음 ");
			
			//테스트시 이 부분 주석 시작
			//먼저 json을 자바DTO담음
			ChatbotRequestVo vo = new ChatbotRequestVo();

			try {
	            JsonNode root = mapper.readTree(req.getReader());
	            vo = jsonToVo(root, req);
	            System.out.println("챗봇 요청 ChatbotRequestVo 변환 : " + vo);//ChatbotRequestVO [id=0, people=2, datestart=2026-10-15, dateend=2026-10-17, style=휴양/힐링형, content=가성비여행  우정여행, created_at=null, memid=null, days=[ChatbotDayVO [chatbotid=0, dayno=1, tripdate=2026-10-15, region=부산, stay=호텔, plan=광안리 야경], ChatbotDayVO [chatbotid=0, dayno=2, tripdate=2026-10-16, region=부산, stay=호텔, plan=부평 깡통시장 점심 저녁 해결], ChatbotDayVO [chatbotid=0, dayno=3, tripdate=2026-10-17, region=부산, stay=null, plan=해동 용궁사 방문]]]
//챗봇 요청 ChatbotRequestVo 변환 : ChatbotRequestVO [id=0, people=4, datestart=2026-10-18, dateend=2026-10-20, style=관광/랜드마크형, content=가성비여행 가족여행, created_at=null, memid=choi04, days=[ChatbotDayVO [chatbotid=0, dayno=1, tripdate=2026-10-18, region=서울, stay=호텔, plan=점심 경복궁 투어 서울타워 야경], ChatbotDayVO [chatbotid=0, dayno=2, tripdate=2026-10-19, region=서울, stay=호텔, plan=서울 동대문시장 점심], ChatbotDayVO [chatbotid=0, dayno=3, tripdate=2026-10-20, region=서울, stay=null, plan=국중박 관람 점심]]]

	        } catch (IllegalArgumentException e) { // 입력값이 잘못된 경우 → 사용자에게 보여줄 문구
	        	System.out.println("[ChatbotController] /Chatbot/newConversationPro 챗봇요청 입력값 잘못되서 false 처리");
				
				resp.setStatus(400);
				out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
	        	return;
	        } catch (Exception e) { // JSON 자체가 깨진 경우                       
	            e.printStackTrace();
	            System.out.println("[ChatbotController] /Chatbot/newConversationPro JSON 자체가 깨진 경우 false 처리");
	            resp.setStatus(400);
	            out.print("{\"success\": false, \"message\": \"요청한 입력값이 잘못되었습니다.\"}");
				return;
	        }
	        
	        int chatbotId=0;

	        try {
				chatbotId = cs.newInsertChatbotRequest(vo);
				System.out.println("[ChatbotController] /Chatbot/newConversationPro 챗봇db 2번 insert 후 값: "+ chatbotId);
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("[ChatbotController] /Chatbot/newConversationPro 챗봇db 2번 insert 실패 false 처리"+e);
				resp.setStatus(500);
				out.print("{\"success\": false, \"message\": \"서버 처리 중 오류가 발생했습니다.\"}");
				return;
			}
	        
	        if (chatbotId == 0) {
	            System.out.println("챗봇 요청 DB 저장 실패 (일정 생성은 계속 진행)");			            
	        }


			try {
				//JsonNode root = mapper.readTree(req.getReader());
				//오픈라우터요청 결과 data.choices[0].message.content //처리된 핵심만 전달
				
				//테스트중
				//json 전달되어서 옴 objectMapper.readTree(json); json 없으면 다음모델로 처리되서 정리후 옴
				
				//테스트용 1일
				//String airesult ="""
				//		{"title": "2026 부산 힐링 & 맛집 탐방 1 일", "summary": "부산에서 바다를 보며 여유를 즐기며 하루를 보내는 휴양형 여행으로, 광안리 일대에서 맛집과 전망을 중심으로 구성된 일정입니다.", "days": [{"day": 1, "date": "2026-10-06", "theme": "바다 뷰와 맛집 탐방", "items": [{"time": "09:00", "place": "숙박 호텔", "activity": "휴식 및 힐링 조식", "tip": "여행의 피로를 풀며 편안하게 아침을 즐기세요."}, {"time": "12:00", "place": "광안리 해변가 근처 맛집", "activity": "점심 식사 (해산물 요리)", "tip": "바다 전망이 보이는 식당에서 현지 음식을 즐깁니다."}, {"time": "14:30", "place": "광안대교 근처 힐링 카페", "activity": "카페 시간 및 해변 산책", "tip": "여유로운 커피와 함께 일몰을 감상합니다."}, {"time": "18:30", "place": "광안리 근처 맛집", "activity": "저녁 식사 (한국 음식)", "tip": "분위기 좋은 식당에서 하루의 마무리 식사를 합니다."}, {"time": "20:30", "place": "광안리 해수욕장", "activity": "광안대교 야경 감상", "tip": "밤바람을 맞으며 조용히 야경을 즐깁니다."}]}]}""";
				//테스트용 3일				
//				String airesult = """
//						{"title":"부산 휴양/힐링 3일 일정","summary":"호텔 숙박과 함께 남포동 BIFF 행사, 부평 깡통시장 저녁, 광안대교 야경, 태종대 점심을 즐기는 부산 3일 휴양 여행 일정입니다.","days":[{"day":1,"date":"2026-10-11","theme":"남포동 문화 & 야시장 체험","items":[{"time":"09:00","place":"부산 남포동 BIFF 광장","activity":"부산 국제 영화제 행사 관람","tip":"무료 관람 가능, 사진 촬영 가능"},{"time":"12:30","place":"남포동 인근 맛집","activity":"점심 식사","tip":"해산물 쌀국수 추천"},{"time":"15:00","place":"부산 부평 깡통시장","activity":"깡통시장 산책 및 다양한 간식 체험","tip":"현지 감성 사진 촬영 명소"},{"time":"19:00","place":"부평 깡통시장 내 저녁 맛집","activity":"저녁 식사","tip":"가성비 좋은 해산물 튀김"}]},{"day":2,"date":"2026-10-12","theme":"광안대교 야경 감상","items":[{"time":"09:00","place":"호텔 체크인 및 아침 식사","activity":"호텔에서 여유로운 아침","tip":"부산 바다 전망 객실 추천"},{"time":"12:30","place":"해운대 인근 맛집","activity":"점심 식사","tip":"짧은 대기, 가성비 좋음"},{"time":"15:00","place":"광안대교","activity":"야외 산책 및 전망대 전망","tip":"일몰 시간 맞춰 방문"},{"time":"19:00","place":"광안리 해변가 맛집","activity":"저녁 식사 및 야경 감상","tip":"게장 정식 가성비 좋음"}]},{"day":3,"date":"2026-10-13","theme":"태종대 자연 탐방","items":[{"time":"09:00","place":"호텔 체크아웃","activity":"호텔에서 출발","tip":"짐은 호텔에 맡기기"},{"time":"12:30","place":"태종대","activity":"점심 식사 및 등대 산책","tip":"해산물 칼국수 추천"},{"time":"15:00","place":"태종대 해변","activity":"산책 및 사진 촬영","tip":"날씨 좋으면 해상 전망 굿"},{"time":"19:00","place":"서면 인근 맛집","activity":"저녁 식사","tip":"과일장 sinh 비빔밥 맛있음"}]}]}
//						""";

				//실제
				String airesult = openrouterApi.newChatbotConversationPro(vo);
				System.out.println("[ChatbotController] /Chatbot/newConversationPro 오픈라우터 요청처리 결과: "+ airesult);
				
				//테스트용 3일
				//String airesult ="{\"title\": \"부산 휴양·힐링 2박 3일 우정 여행\", \"summary\": \"부산의 해변과 시장에서 여유롭게 휴식을 취하며 광안리 야경, 부평 깡통시장 식사, 해동 용궁사 방문을 즐기는 가성비 좋은 친구 여행입니다.\", \"days\": [{\"day\": 1, \"date\": \"2026-10-15\", \"region\": \"부산\", \"theme\": \"해변 산책 및 광안리 야경\", \"items\": [{\"time\": \"08:00\", \"place\": \"해운대 해변\", \"activity\": \"아침 산책 및 해변 스트레칭\", \"tip\": \"아침 햇살이 좋으니 가볍게 스트레칭하기\"}, {\"time\": \"12:00\", \"place\": \"광안리 근처 맛집\", \"activity\": \"점심 식사 (회무침 및 해산물)\", \"tip\": \"광안리 회센터에서 신선한 회 추천\"}, {\"time\": \"15:00\", \"place\": \"광안리 카페 거리\", \"activity\": \"카페에서 디저트 및 휴식\", \"tip\": \"오션뷰 좌석 선택\"}, {\"time\": \"18:30\", \"place\": \"광안리 해변\", \"activity\": \"광안리 야경 감상\", \"tip\": \"조명이 켜지는 19시 무렵에 맞추면 최고\"}, {\"time\": \"20:00\", \"place\": \"광안리 호텔\", \"activity\": \"숙소 체크인 및 휴식\", \"tip\": \"체크인 후 호텔 내 사우나 이용\"}]}, {\"day\": 2, \"date\": \"2026-10-16\", \"region\": \"부산\", \"theme\": \"부평 깡통시장 탐방 및 식사\", \"items\": [{\"time\": \"08:00\", \"place\": \"송정 해변\", \"activity\": \"아침 jogging 및 해변 산책\", \"tip\": \"파도 소리 들으며 스트레칭\"}, {\"time\": \"12:00\", \"place\": \"부평 깡통시장\", \"activity\": \"점심 해결 (떡볶이, 어묵, 핫도그)\", \"tip\": \"시장 안 길거리 음식 골고루 맛보기\"}, {\"time\": \"15:00\", \"place\": \"부평 깡통시장 주변\", \"activity\": \"쇼핑 및 기념품 구경\", \"tip\": \"전통 공예품과 저렴한 액세서리\"}, {\"time\": \"18:30\", \"place\": \"부평 깡통시장\", \"activity\": \"저녁 식사 (시장 내 횟집 또는 국수집)\", \"tip\": \"시장 안 secrète 맛집에서 매운탕 추천\"}, {\"time\": \"20:00\", \"place\": \"광안리 호텔\", \"activity\": \"숙소 체크인 (재입실) 및 휴식\", \"tip\": \"호텔 라운지에서 차 한잔하며 대화\"}]}, {\"day\": 3, \"date\": \"2026-10-17\", \"region\": \"부산\", \"theme\": \"해동 용궁사 방문 및 해변 마무리\", \"items\": [{\"time\": \"08:00\", \"place\": \"해동 용궁사\", \"activity\": \"사찰 방문 및 풍경 감상\", \"tip\": \"아침에 방문하면 사람 적고 평화롭음\"}, {\"time\": \"12:00\", \"place\": \"해동 용궁사 근처 맛집\", \"activity\": \"점심 식사 (채식 도시락 또는 산채 비빔밥)\", \"tip\": \"사찰 근처 전통 한식당 이용\"}, {\"time\": \"15:00\", \"place\": \"해운대 블루라인 파크\", \"activity\": \"산책 및 자전거 타기\", \"tip\": \"바다를 따라 달리면 힐링됨\"}, {\"time\": \"17:00\", \"place\": \"해운대 해변\", \"activity\": \"자유 시간 및 기념 사진 촬영\", \"tip\": \"일몰 전 해변에서 사진 찍기 좋음\"}]}]}";
				//테스트끝
			
				
				// 모든 AI 모델이 실패하면 null
				if (airesult == null) {
					resp.setStatus(503);
					out.print("{\"success\": false, \"message\": \"AI 서비스를 잠시 이용할 수 없습니다. 잠시 후 다시 시도해주세요.\"}");
					return;
				}

				
				//String일경우 writeValueAsString()안에 넣지말자 이중처리
				//객체(JsonNode, Map, Vo) → writeValueAsString()으로 글자로 바꿈
				//이미 JSON 글자(String) → 그대로 넣음
				//resp.getWriter().write(mapper.writeValueAsString(Map.of("result", mapper.writeValueAsString(airesult))));
				//resp.getWriter().write(mapper.writeValueAsString(Map.of("result",airesult)));
				out.print(mapper.writeValueAsString(Map.of("success", true, "result", airesult)));
				
					
			}catch(Exception e) {
			    e.printStackTrace();
		        System.out.println("[[ChatbotController] /Chatbot/newConversationPro 에러발생"+e.getMessage());
		        resp.setStatus(500);
				out.print("{\"success\": false, \"message\": \"서버 처리 중 오류가 발생했습니다.\"}");
				return;
			}

		}else { resp.sendError(HttpServletResponse.SC_NOT_FOUND); }
    }//doHandle()
		
	
	
	@Override
	// =========================================================
	// 주제: doGet 처리
	// 설명: GET 요청을 받아 필요한 데이터를 준비하고 화면 또는 응답으로 전달
	// =========================================================
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doHandle(req, resp);
		
	}

	@Override
	// =========================================================
	// 주제: doPost 처리
	// 설명: POST 요청을 받아 입력값을 검증하고 저장/처리 결과를 응답
	// =========================================================
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doHandle(req, resp);
	}


	@Override   // @Override = "부모가 가진 메소드를 내가 다시 만든다" 는 표시. 오타를 내면 컴파일 에러로 잡아 준다.
	// =========================================================
	// 주제: init 처리
	// 설명: init 기능에 필요한 처리 로직을 수행
	// =========================================================
	public void init(ServletConfig config) throws ServletException {
		openrouterApi = new AccommodationChatbotOpenrouterApi();
		cs = new ChatbotService();
	}
	
	private String sessionCheck(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        String loginId = null;

	     //session이 null이 아닌지 먼저 체크 (Null Pointer Exception 방지)
	     if (session != null) {
	         loginId = (String) session.getAttribute("loginId");
	     }

	     //로그인 여부 판단
	     if (loginId != null && !loginId.isBlank()) {
	        return loginId; 
	     } else {
	         // 비로그인 사용자 처리 (로그인 페이지로 리다이렉트 등)
	        return null;
	     }
	}
	
	// "부산 휴양/힐링 3일 일정" → "부산 휴양_힐링 3일 일정_20261010.pdf"
    // =========================================================
    // 주제: makeOriginalName 처리
    // 설명: 입력 데이터를 화면/API/DB에서 사용하기 좋은 형태로 변환 또는 가공
    // =========================================================
    private String makeOriginalName(String title) {
        String safe = (title == null) ? "" : title;
        safe = safe.replaceAll("[\\\\/:*?\"<>|\\r\\n]", "_").trim();   // 파일명에 못 쓰는 문자 제거
        if (safe.isEmpty()) safe = "여행일정표";
        if (safe.length() > 50) safe = safe.substring(0, 50);           // 너무 긴 제목 자르기

        String today = LocalDate.now(ZoneId.of("Asia/Seoul"))
                .format(DateTimeFormatter.BASIC_ISO_DATE);              // 20261010
        return safe + "_" + today + ".pdf";
    }
    
    
    // =========================================================
    // 주제: jsonToVo 처리
    // 설명: jsonToVo 기능에 필요한 처리 로직을 수행
    // =========================================================
    private ChatbotRequestVo jsonToVo(JsonNode root, HttpServletRequest req) {
        ChatbotRequestVo vo = new ChatbotRequestVo();
 
        // 인원 (선택 입력)
        String peopleStr = root.path("people").asText("").trim();
        if (!peopleStr.isEmpty()) {
            int people;
            try {
                people = Integer.parseInt(peopleStr);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("인원수는 숫자로 입력해주세요.");
            }
            if (people < 1) {
                throw new IllegalArgumentException("인원수는 1명 이상으로 입력해주세요.");
            }
            vo.setPeople(people);
        }
 
        // 날짜
        LocalDate start;
        LocalDate end;
        try {
            start = LocalDate.parse(root.path("datestart").asText());
            end   = LocalDate.parse(root.path("dateend").asText());
        } catch (Exception e) {
            throw new IllegalArgumentException("날짜 형식이 올바르지 않습니다.");
        }
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("마지막날짜는 시작날짜보다 앞설 수 없어요.");
        }
        int dayCnt = (int) ChronoUnit.DAYS.between(start, end) + 1;
        if (dayCnt > MAX_DAYS) {
            throw new IllegalArgumentException("여행 기간은 최대 " + MAX_DAYS + "일까지 가능해요.");
        }
        vo.setDatestart(java.sql.Date.valueOf(start));
        vo.setDateend(java.sql.Date.valueOf(end));
 
        // 여행스타일 (목록에 있는 것만, 중복 제거)
        List<String> styles = new ArrayList<>();
        for (JsonNode s : root.path("styleValues")) {
            String style = s.asText();
            if (STYLES.contains(style) && !styles.contains(style)) styles.add(style);
        }
        vo.setStyle(styles.isEmpty() ? null : String.join(",", styles));   // 쉼표 뒤 공백 없이
 
        // 추가 내용 (필수)
        String content = root.path("content").asText("").trim();
        if (content.isEmpty()) {
            throw new IllegalArgumentException("추가하고 싶은 내용을 적어주세요.");
        }
        if (content.length() > 600) {
            throw new IllegalArgumentException("추가 내용은 600자까지 적을 수 있어요.");
        }
        vo.setContent(content);
 
        // 회원 아이디 (비회원이면 null)
        Object loginId = req.getSession().getAttribute("loginId");   // ★ 세션 이름 확인
        vo.setMemid(loginId == null || loginId.toString().isEmpty() ? null : loginId.toString());
 
        // 일차 : 개수는 날짜로 다시 계산해서 비교, 날짜도 서버가 직접 계산
        JsonNode daysNode = root.path("days");
        if (!daysNode.isArray() || daysNode.size() != dayCnt) {
            throw new IllegalArgumentException("일차 정보가 날짜와 맞지 않아요. 날짜를 다시 골라주세요.");
        }
 
        for (int i = 0; i < dayCnt; i++) {
            JsonNode d = daysNode.get(i);
            boolean isLast = (i == dayCnt - 1);
 
            String region = d.path("region").asText("");
            if (!REGIONS.contains(region)) {
                throw new IllegalArgumentException((i + 1) + "일차 지역을 다시 골라주세요.");
            }
 
            String stay = d.path("stay").asText("");
            if (isLast || !STAYS.contains(stay)) stay = null;     // 마지막 날이거나 이상한 값 → NULL
 
            String plan = d.path("plan").asText("").trim();
            if (plan.length() > 100) plan = plan.substring(0, 100);
 
            ChatbotDayVo day = new ChatbotDayVo();
            day.setDayno(i + 1);
            day.setTripdate(java.sql.Date.valueOf(start.plusDays(i)));
            day.setRegion(region);
            day.setStay(stay);
            day.setPlan(plan.isEmpty() ? null : plan);
            vo.getDays().add(day);
        }
 
        return vo;
    }

//    {
//    	  "title": "2026 제주 휴양 우정 여행 일정",
//    	  "summary": "3일간 제주의 자연, 해변, 전통 시장을 둘러보며 휴양과 여유, 맛있는 식사를 만끽하는 일정입니다.",
//    	  "days": [
//    	    {
//    	      "day": 1,
//    	      "date": "2026-10-13",
//    	      "theme": "자연 속 휴식",
//    	      "items": [
//    	        {
//    	          "time": "09:00",
//    	          "place": "한라산 정상",
//    	          "activity": "일출 전망 감상 및 간단한 산책",
//    	          "tip": "초지점 옆에서 보는 일출이 인상적"
//    	        },
//    	        {
//    	          "time": "12:00",
//    	          "place": "제주 올레시장 근처 맛집",
//    	          "activity": "현지 흑돼지 식당에서 점심 식사",
//    	          "tip": "코로롱 식당 추천, 가성비 좋음"
//    	        },
//    	        {
//    	          "time": "15:00",
//    	          "place": "만장굴 입구",
//    	          "activity": "시원한 동굴 산책하기",
//    	          "tip": "편한 신발과 얇은 재킷 준비"
//    	        },
//    	        {
//    	          "time": "19:00",
//    	          "place": "서귀포 매일올레시장",
//    	          "activity": "밤시장 산책 및 야식 먹기",
//    	          "tip": "야시장에서 과일쥬스 등 간식 추천"
//    	        }
//    	      ]
//    	    },
//    	    {
//    	      "day": 2,
//    	      "date": "2026-10-14",
//    	      "theme": "해변과 여유",
//    	      "items": [
//    	        {
//    	          "time": "09:00",
//    	          "place": "성산일출봉",
//    	          "activity": "일출 관람 및 등반",
//    	          "tip": "전망대까지 걸어 올라 사진 찍기 좋은 포인트"
//    	        },
//    	        {
//    	          "time": "12:00",
//    	          "place": "성산항 근처 맛집",
//    	          "activity": "회국수 점심 식사",
//    	          "tip": "항구 바로 앞에 있는 어촌식당 추천"
//    	        },
//    	        {
//    	          "time": "15:00",
//    	          "place": "시운데 해수욕장",
//    	          "activity": "바다 산책 및 휴식",
//    	          "tip": "우도 페리 하차장과 가까워 접근성 좋음"
//    	        },
//    	        {
//    	          "time": "19:00",
//    	          "place": "협재해수욕장 근처 맛집",
//    	          "activity": "seafood 저녁 식사, 일몰 감상",
//    	          "tip": "바다를 보며 식사할 수 있는 초록빛 식당"
//    	        }
//    	      ]
//    	    },
//    	    {
//    	      "day": 3,
//    	      "date": "2026-10-15",
//    	      "theme": "문화와 작별",
//    	      "items": [
//    	        {
//    	          "time": "09:00",
//    	          "place": "남원읍 민속마을",
//    	          "activity": "전통 가옥 견학 및 산책",
//    	          "tip": "해설사와 함께하는 투어 추천"
//    	        },
//    	        {
//    	          "time": "12:00",
//    	          "place": "중문수산시장 근처 맛집",
//    	          "activity": "갈치조림 점심 식사",
//    	          "tip": "직영 횟집에서 먹으면 싱싱함"
//    	        },
//    	        {
//    	          "time": "15:00",
//    	          "place": "천지연 폭포",
//    	          "activity": "폭포 및 주변 산책",
//    	          "tip": "빗물 떨어지는 소리 명상적"
//    	        },
//    	        {
//    	          "time": "19:00",
//    	          "place": "제주 시내 마무리 저녁",
//    	          "activity": "야경이 아름다운 전망대에서 저녁식사",
//    	          "tip": "일몰과 함께 불멍 즐기며 마무리"
//    	        }
//    	      ]
//    	    }
//    	  ]
//    	}

}
