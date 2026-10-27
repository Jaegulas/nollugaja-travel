package api;

import java.io.InputStream;
import java.lang.module.Configuration;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import config.AppConfigListener;

import dao.ChatbotDao;
import dto.ChatbotDto;
import service.AccommodationService;
import vo.ChatbotDayVo;
import vo.ChatbotRequestVo;
import vo.ChatbotVo;

// =========================================================
// 주제: 파일 역할
// 설명: AccommodationChatbotOpenrouterApi 외부 API 연동에 필요한 요청/응답 처리를 담당
// =========================================================


//AI오픈라우터요청처리순서
//1.오픈라우터API키 얻음 properties파일에서 Properties 객체이용
//2.화면에서 json으로 전달된 호텔명과 호텔기본키 확인
//3.프롬프트작성전 먼저 호텔기본키로 저장된 리뷰내용확인 후 있으면 VO객체를 result 키로 만들어 json객체만들어 전달
//4.없으면 오픈라우터에 전달할 프롬프트작성 후 Map.of() 페이로드 생성 후 Jackson 으로 json 객체로 만들어서 String 담음
//5.HttpRequest로 오픈라우터에게 요청 -Authorization 헤더에 키전달
//Map.of() 넣어서 요청
//6.응답내용 파싱처리 choices.message.content 에 들어있다
//7.Map의 키로 꺼내서 본문상관없는 부분있다면 없애고 리뷰db저장
//8.화면에게 String 만들어전달
//9.json.parse() 로 js객체로 꺼낸다. 키로 map() 처리해서 보여준다. 이스케이프문자처리하기


public class AccommodationChatbotOpenrouterApi {

	
	private static final String OPENROUTER_URL = "https://openrouter.ai/api/v1/chat/completions";
    
    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(60)).build();

    
//    private static final String[] MODEL_FALLBACK_LIST = {
//    		"deepseek/deepseek-chat"
//    		//,google/gemini-2.0-flash-001
//    		//,meta-llama/llama-3.3-70b-instruct
//    };

//    NVIDIA: Nemotron 3 Super (free)
//    Google: Gemma 4 31B (free)
//    NVIDIA: Nemotron 3.5 Lightning (free)

     
    
    private static final String MODEL = "openrouter/free";
    private static final String [] MODEL_FALLBACK_LIST =  {
    		 "nvidia/nemotron-3-super-120b-a12b:free",
		    "google/gemma-4-31b-it:free",
		    "nvidia/nemotron-3.5-lightning:free",
    		"openrouter/free",
 
    };
   
	private static final String  OPENROUTER_API_KEY =CommonGetApiKey.get("openrouter.api.key", "OPENROUTER_API_KEY", "openrouter.api.key");


	private static final ObjectMapper objectMapper = new ObjectMapper()
	        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    
	// =========================================================
	// 주제: get 처리
	// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
	// =========================================================
	public static String get(String sysKey, String envKey, String def) {

		// 1순위 : java -Dfile.repo.path=... 로 준 값
		String v = System.getProperty(sysKey);

		// 2순위 : export FILE_REPO_PATH=... 로 준 값
		if (isBlank(v)) {
			try {
				v = System.getenv(envKey);
			} catch (SecurityException e) {
				v = null;   // 보안 설정으로 환경변수를 못 읽는 경우 그냥 넘어간다
			}
		}

		// 3순위 : Properties 객체이용 properties 파일에서 들고옴
		if (isBlank(v)) {
			v = AppConfigListener.getProperty("openrouter.api.key");
		}

		return (v == null) ? null : v.trim();
	}
	
	
	// =========================================================
	// 주제: isBlank 처리
	// 설명: 입력값 또는 현재 상태가 조건에 맞는지 검사
	// =========================================================
	private static boolean isBlank(String s) {
		return s == null || s.trim().isEmpty();
	}

	
	//챗봇페이지 여행일정표 오픈라우트요청
	// =========================================================
	// 주제: chatbotConversationPro 처리
	// 설명: chatbotConversationPro 기능에 필요한 처리 로직을 수행
	// =========================================================
	public String chatbotConversationPro(ChatbotDto chatbotDto) throws SQLException {
		System.out.println("[AccommodationChatbotOpenrotuerApi] accommodationConversationPro() 진입 - 프롬프트생성과 오픈라우터 요청처리 ");
		String prompt =buildItineraryPrompt(chatbotDto);

		String content ="당신은 여행일정표제작 전문가입니다. 항상 순수 JSON 객체 하나만 출력합니다.";
		return requestOpenRouter(prompt, content);
   	}//AccommodationConversationPro


	// =========================================================
	// 주제: newChatbotConversationPro 처리
	// 설명: newChatbotConversationPro 기능에 필요한 처리 로직을 수행
	// =========================================================
	public String newChatbotConversationPro(ChatbotRequestVo vo) throws SQLException {
		System.out.println("[AccommodationChatbotOpenrotuerApi] newAccommodationConversationPro() 진입 - 프롬프트생성과 오픈라우터 요청처리 ");
		String prompt =newBuildItineraryPrompt(vo);
		System.out.println("[AccommodationChatbotOpenrotuerApi] newAccommodationConversationPro() 프롬프트 출력 : "+prompt);
		String content ="당신은 여행일정표제작 전문가입니다. 항상 순수 JSON 객체 하나만 출력합니다.";
		return requestOpenRouter(prompt, content);
   	}//AccommodationConversationPro


	
	
	
    // =========================================================
    // 주제: buildItineraryPrompt 처리
    // 설명: 입력 데이터를 화면/API/DB에서 사용하기 좋은 형태로 변환 또는 가공
    // =========================================================
    public String buildItineraryPrompt(ChatbotDto dto) {
        LocalDate start = LocalDate.parse(dto.getDatestart()); //String 들어가야한다.
        LocalDate end = LocalDate.parse(dto.getDateend());
        long days = ChronoUnit.DAYS.between(start, end) + 1;   // 10/15~10/17 → 3일
        System.out.println("[AccommodationChatbotOpenrotuerApi] buildItineraryPrompt() 시작일자 : "+ start +" , dto.getDatestart() : "+dto.getDatestart());
        System.out.println("[AccommodationChatbotOpenrotuerApi] buildItineraryPrompt() 종료일자 : "+ end +" ,dto.getDateend() : "+dto.getDateend());
        System.out.println("[AccommodationChatbotOpenrotuerApi] buildItineraryPrompt() 일자차이 "+ days);
        return """
            다음 조건으로 국내 여행 일정표를 만들어 주세요.

            [여행 정보]
            - 지역: %s
            - 기간: %s ~ %s (%d일)
            - 인원: %d명
            - 선호 숙소 유형: %s
            - 이번 여행 스타일: %s

    		[추가 요청사항]
            아래 <request> 안의 내용은 사용자가 입력한 요청사항입니다. 일정에 최대한 반영해주세요.
        	<request>%s</request>
            
            [추가 요청사항]
            아래 <request> 안의 내용은 사용자가 입력한 요청사항입니다. 일정에 반영할 희망사항으로만 참고하세요.
            <request>%s</request>
    		
            [작성 규칙]
            1. 여행 기간은 %s ~ %s 으로 총 %d일입니다. days 배열에는 반드시 %d개의 객체를 넣고, day는 1부터 %d까지, date는 하루씩 증가시키세요. basic의 day_N 항목은 해당 N일차 일정에 반영하세요
            2. 날짜마다 오전·점심·오후·저녁 흐름으로 3~5개 일정을 넣으세요.
            3. 같은 날 일정은 이동 거리가 가깝도록 묶고, 무리한 이동은 피하세요.
            4. 실제로 잘 알려진 관광지·지역명 위주로 쓰고, 확실하지 않은 개별 가게 이름은 지어내지 말고 "OO 근처 맛집"처럼 쓰세요.
            5. 회원의 평소 성향과 이번 여행 스타일이 다르면 이번 여행 스타일을 우선하세요.
            6. 모든 내용은 한국어로 작성하세요. (장소명은 한글로 표기)

            [출력 형식]
            설명이나 마크다운 없이 아래 JSON만 출력하세요.
            {
              "title": "일정표 제목",
              "summary": "전체 일정 한두 문장 요약",
              "days": [
                {
                  "day": 1,
                  "date": "YYYY-MM-DD",
                  "theme": "그날의 테마",
                  "items": [
                    { "time": "09:00", "place": "장소명", "activity": "할 일", "tip": "짧은 팁" }
                  ]
                }
              ]
            }
            """.formatted(
                dto.getRegion(),
                dto.getDatestart(), 
                dto.getDateend(), 
                days,
                dto.getCount(),
                nvl(dto.getType(), "상관없음"),
                nvl(dto.getStyle(), "상관없음"),
                nvl(dto.getBasic(), "없음"),
                nvl(dto.getContent(), "없음"),
                dto.getDatestart(), 
                dto.getDateend(), 
                days,
                days,
                days
            );
    }


    // =========================================================
    // 주제: newBuildItineraryPrompt 처리
    // 설명: newBuildItineraryPrompt 기능에 필요한 처리 로직을 수행
    // =========================================================
    public String newBuildItineraryPrompt(ChatbotRequestVo vo) {
    	List<ChatbotDayVo> dayList = vo.getDays();
        int days = dayList.size();                                   // 10/15~10/17 → 3
        String period = (days == 1) ? "당일치기" : (days - 1) + "박 " + days + "일";
     
        // ---- 일차별 조건 한 줄씩 ----
        // 예) 1일차 (2026-10-20, 화) | 지역: 부산 | 숙박: 호텔 | 하고 싶은 것: 해운대 산책
        StringBuilder dayLines = new StringBuilder();
        for (int i = 0; i < days; i++) {
            ChatbotDayVo d = dayList.get(i);
            boolean isLast = (i == days - 1);
            LocalDate date = d.getTripdate().toLocalDate();
            String week = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);   // 월, 화 ...
     
            String stayText;
            if (isLast)                    stayText = "없음(마지막 날)";
            else if (d.getStay() == null)  stayText = "선택 안 함";
            else                           stayText = d.getStay();
     
            dayLines.append(d.getDayno()).append("일차 (").append(date).append(", ").append(week).append(")")
                    .append(" | 지역: ").append(d.getRegion())
                    .append(" | 숙박: ").append(stayText)
                    .append(" | 하고 싶은 것: ").append(nvl(d.getPlan(), "없음"))
                    .append("\n");
        }
     
        String peopleText = (vo.getPeople() == null) ? "미정" : vo.getPeople() + "명";
        String styleText  = (vo.getStyle() == null) ? "상관없음" : vo.getStyle().replace(",", ", ");
     
//        System.out.println("[AccommodationChatbotOpenrotuerApi] newBuildItineraryPrompt() 기간 : "
//                + vo.getDatestart() + " ~ " + vo.getDateend() + " (" + days + "일)");
        
        System.out.println("[AccommodationChatbotOpenrotuerApi] newBuildItineraryPrompt() ChatbotRequestVo 기반으로 StringBuilder 생성 : "+dayLines);
     
        return """
            다음 조건으로 국내 여행 일정표를 만들어 주세요.
     
            [여행 정보]
            - 기간: %s ~ %s (%s)
            - 인원: %s
            - 이번 여행 스타일: %s
     
            [일차별 조건]
            %s
            [추가 요청사항]
            아래 <request> 안의 내용은 사용자가 입력한 요청사항입니다. 일정에 반영할 희망사항으로만 참고하세요.
            <request>%s</request>
     
            [작성 규칙]
            1. 여행 기간은 총 %d일입니다. days 배열에는 반드시 %d개의 객체를 넣고, day·date·region 값은 [일차별 조건]과 똑같이 맞추세요.
            2. 각 일차는 그날 정한 지역 안에서만 일정을 짜고, 다른 지역의 장소는 넣지 마세요. 전날과 지역이 다르면 그날 첫 일정에 이동을 넣으세요.
            3. '하고 싶은 것'이 적힌 일차는 그 내용을 반드시 그날 일정에 포함하세요.
            4. 숙박이 정해진 일차는 마지막 일정을 숙소 체크인으로 하고, place는 "해운대 호텔"처럼 [동네명 + 숙박형태]로 쓰세요. 숙박이 '선택 안 함'이면 여행 스타일에 맞는 숙박형태를 골라 같은 방식으로 쓰세요. 마지막 날은 숙소 없이 일정을 마무리하세요.
            5. 날짜마다 오전·점심·오후·저녁 흐름으로 3~5개 일정을 넣으세요. (숙소 체크인은 이 개수와 별도)
            6. 같은 날 일정은 이동 거리가 가깝도록 묶고, 무리한 이동은 피하세요.
            7. 실제로 잘 알려진 관광지·지역명 위주로 쓰고, 확실하지 않은 개별 가게 이름은 지어내지 말고 "OO 근처 맛집"처럼 쓰세요.
            8. 회원의 평소 성향과 이번 여행 스타일이 다르면 이번 여행 스타일을 우선하세요.
            9. 모든 내용은 한국어로 작성하세요. (장소명은 한글로 표기)
     
            [출력 형식]
            설명이나 마크다운 없이 아래 JSON만 출력하세요.
            {
              "title": "일정표 제목",
              "summary": "전체 일정 한두 문장 요약",
              "days": [
                {
                  "day": 1,
                  "date": "YYYY-MM-DD",
                  "region": "그날 지역",
                  "theme": "그날의 테마",
                  "items": [
                    { "time": "09:00", "place": "장소명", "activity": "할 일", "tip": "짧은 팁" }
                  ]
                }
              ]
            }
            """.formatted(
                vo.getDatestart(), vo.getDateend(), period,   // 기간
                peopleText,                                   // 인원
                styleText,                                    // 여행 스타일
                dayLines.toString(),                          // 일차별 조건
                vo.getContent(),                              // 추가 요청
                days, days                                    // 규칙 1
            );
    }

    
    //숙박페이지 장단점 오른라우터요청
    // =========================================================
    // 주제: accommodation_openrouter_summary 처리
    // 설명: accommodation_openrouter_summary 기능에 필요한 처리 로직을 수행
    // =========================================================
    public String accommodation_openrouter_summary(String accommodationName) {
    	System.out.println("[AccommodationChatbotOpenrotuerApi] accommodation_openrouter_summary() 진입");

    	String promptText = String.format(
    		"숙소 '%s'의 블로그 후기를 바탕으로 요약해줘.\n" +
    		"반드시 아래 JSON 형식으로만 답하고, 마크다운(**, ```)이나 다른 설명은 절대 쓰지 마세요.\n" +
    		"{\"pros\": [\"장점1\", \"장점2\", \"장점3\"], \"cons\": [\"단점1\", \"단점2\"]}\n" +
    		"규칙:\n" +
    		"1. 장점과 단점 문장 안에는 큰따옴표(\")를 절대 쓰지 마세요. 인용이 필요하면 작은따옴표(')를 쓰세요.\n" +
    		"2. 각 배열은 ]로 닫고, 전체는 }로 한 번만 닫으세요.\n" +
    		"3. 장점과 단점은 각각 최소 1개 이상 작성하세요.",
    		accommodationName);

    	String content = "당신은 호텔 및 숙박 정보 요약 전문가입니다. "
    	               + "항상 문법이 올바른 순수 JSON 객체 하나만 출력하며, 값 안에는 큰따옴표를 쓰지 않습니다.";

    	return requestOpenRouter(promptText, content);
    }
    
	//챗봇페이지와 숙박페이지 공용사용 실제 오픈라우터요청메소드 choices.message.content 실제 데이터만 반환해줌
    // =========================================================
    // 주제: requestOpenRouter 처리
    // 설명: requestOpenRouter 기능에 필요한 처리 로직을 수행
    // =========================================================
    public String requestOpenRouter(String promptText, String content) {

        for (String modelName : MODEL_FALLBACK_LIST) {
            try {
                System.out.println("호출 시도 모델: " + modelName);

                System.out.println("[AccommodationChatbotOpenrotuerApi] requestOpenRouter() Map.of() 페이로드 생성후 요청");
                
           
                Map<String, Object> payload = Map.of(
                        "model", modelName,
                        "response_format", Map.of("type", "json_object"),
                        "messages", List.of(
                                Map.of("role", "system", "content", content),
                                Map.of("role", "user", "content", promptText)
                        )
                );
       	     
                String jsonPayload = objectMapper.writeValueAsString(payload); // 객체를 json처리 하고 String 저장
                
                HttpRequest httpRequest = HttpRequest.newBuilder()
                        .uri(URI.create(OPENROUTER_URL))
                        .header("Authorization", "Bearer " + OPENROUTER_API_KEY)
                        .header("Content-Type", "application/json")
                        .timeout(Duration.ofSeconds(60)) // 응답 타임아웃 15초 설정
                        .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                        .build();

                HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
//httpClient.send()는 401, 429, 500 같은 에러 응답이 와도 예외를 던지지 않아요. 네트워크가 끊기거나 타임아웃 같은 "연결 자체가 실패"할 때만 예외가 나요.
                System.out.println("[AccommodationChatbotOpenrotuerApi] requestOpenRouter() HTTP 200 OK 인 경우 응답 파싱 후 반환 OpenRouter 응답 구조에 맞게 text 추출");
                if (httpResponse.statusCode() == 200) {
                    JsonNode responseJson = objectMapper.readTree(httpResponse.body());
                    
                	//js 저리를 백엔드에서 함 const textContent = data.choices[0].message.content;
                    System.out.println("[AccommodationChatbotOpenrotuerApi] requestOpenRouter() 실제 응답한 모델 : " + responseJson.path("model").asText());
                    
                    String text = responseJson.path("choices").path(0).path("message").path("content").asText("");

                    // JSON 이 아니면 (guard 모델, 설명문만 온 경우 등) 다음 모델로
                    int s = text.indexOf('{');
                    int e = text.lastIndexOf('}');
                    if (s < 0 || e <= s) {
                        System.err.println("[AccommodationChatbotOpenrotuerApi] requestOpenRouter() JSON 아닌 응답 → 다음 모델로 : " + text);
                        continue;
                    }
                    
                    String json = text.substring(s, e + 1);   // ```json 같은 앞뒤 글자 제거
                    
                    try {
                    	objectMapper.readTree(json);          // 깨진 JSON 이면 여기서 예외
                    } catch (Exception ex) {
                        System.err.println("[AccommodationChatbotOpenrotuerApi] requestOpenRouter() 깨진 JSON → 다음 모델로");
                        continue;
                    }
                    return json;
                }

                //System.out.println("과부하 시 다음 모델 진행");
//                System.err.printf("[%s] 호출 실패 - Status Code: %d, Response: %s%n",
//                        modelName, httpResponse.statusCode(), httpResponse.body());
                System.err.printf("[AccommodationChatbotOpenrotuerApi] requestOpenRouter() 오픈라우터 호출 실패 - Status Code: %d, Response: %s%n", 
                		httpResponse.statusCode(), httpResponse.body());
                        
            } catch (Exception e) {
            	System.err.printf("[AccommodationChatbotOpenrotuerApi] requestOpenRouter() 오픈라우터 호출 중 예외 발생:"+ e.getMessage());
            }
        }
        // 모든 모델 실패 시 null 반환
        return null;
    }
    
    
    // =========================================================
    // 주제: nvl 처리
    // 설명: nvl 기능에 필요한 처리 로직을 수행
    // =========================================================
    private static String nvl(String s, String def) {
        return (s == null || s.isBlank()) ? def : s;
    }
    

	private String escapeJsonString(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\b", "\\b")
                    .replace("\f", "\\f")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }//escapeJsonString
		
	// HTTP 에러 상태코드 보관용 커스텀 Exception 클래스
    private static class HttpException extends Exception {
        private final int errorCode;

        // =========================================================
        // 주제: HttpException 처리
        // 설명: HttpException 기능에 필요한 처리 로직을 수행
        // =========================================================
        public HttpException(int errorCode, String message) {
            super(message);
            this.errorCode = errorCode;
        }

        public int getErrorCode() {
            return errorCode;
        }
    }

	
}


