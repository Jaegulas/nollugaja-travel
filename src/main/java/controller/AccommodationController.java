package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.http.HttpClient;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import api.AccommodationChatbotOpenrouterApi;
import api.CommonGetApiKey;
import config.AppConfigListener;
import service.AccommodationService;
import vo.AccommodationReviewVo;
import vo.AccommodationVo;

// =========================================================
// 주제: 파일 역할
// 설명: AccommodationController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================



@WebServlet("/Accommodation/*")
public class AccommodationController extends HttpServlet{
	

	private transient AccommodationService as; 
	
	private AccommodationChatbotOpenrouterApi routerApi;

	private static final ObjectMapper objectMapper = new ObjectMapper()
	        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


	// =========================================================
	// 주제: doHandle 처리
	// 설명: doHandle 기능에 필요한 처리 로직을 수행
	// =========================================================
	private void doHandle(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		System.out.println("[AccommodationController] doHandle() ");
		
		resp.setContentType("text/html;charset=UTF-8");
		String contextPath = req.getContextPath();
		PrintWriter writer = resp.getWriter();
		String action = req.getPathInfo();
		
		if (action == null) {
		    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
		    return;
		}
		
		if(action.equals("/acco_main")) {
			req.setAttribute("center", "accommodation/acco_main.jsp");
			req.setAttribute("pageCss", "accommodation/acco_main.css");
			System.out.println("AccommodationController/acco_main 진입 ");
			List<AccommodationVo> volist = new ArrayList<>();
			
			//세션확인
			String loginId = sessionCheck(req);
			
			//기본은 서울로 지정
			String region = ((req.getParameter("region") != null) && !(req.getParameter("region").trim().isEmpty()) ) ? req.getParameter("region").trim() : "서울" ;
			
			// ==========================================
			// 메인에서 전달한 숙소 ID 확인
			// ==========================================
			String accommodationIdParam =
			        req.getParameter("accommodationId");
			
			System.out.println(
				    "[AccommodationController] 전달받은 숙소 ID : "
				    + accommodationIdParam
				);

			if (accommodationIdParam != null
			        && !accommodationIdParam.trim().isEmpty()) {

			    try {
			        int accommodationId =
			                Integer.parseInt(accommodationIdParam);

			        if (accommodationId <= 0) {
			            resp.sendError(400, "올바르지 않은 숙소 번호입니다.");
			            return;
			        }

			        AccommodationVo selectedStay =
			                as.findAccommdationByAccommdationId(
			                        accommodationId
			                );

			        if (selectedStay == null
			                || selectedStay.getAccommodation_id() == null) {

			            resp.sendError(404, "해당 숙소를 찾을 수 없습니다.");
			            return;
			        }

			        volist.add(selectedStay);

			        req.setAttribute("volist", volist);

			        System.out.println(
			            "[AccommodationController] 메인에서 선택한 숙소 : "
			            + selectedStay.getAccommodation_id()
			            + " / "
			            + selectedStay.getName()
			        );

			        req.getRequestDispatcher(
			                "/WEB-INF/main.jsp"
			        ).forward(req, resp);

			        return;

			    } catch (NumberFormatException e) {
			        resp.sendError(400, "숙소 번호는 숫자여야 합니다.");
			        return;

			    } catch (SQLException e) {
			        e.printStackTrace();
			        throw new ServletException(
			                "선택한 숙소 조회 중 DB 오류", e
			        );
			    }
			}
			
			try {
				volist = as.getAccommodationListFromRegion(region, loginId);
				
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("[AccommodationController] /acco_main catch() "+region+ " 지역 숙박List select 중 에러발생 " +e.getMessage());
				
			}				
		
			if(volist.isEmpty()) {
				try {
					volist = as.getAccommodationListFromRegion("서울", loginId);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("[AccommodationController] /acco_main catch() 해당지역 숙박List 없어서 기본 지역 서울로 지정시 에러 "+e.getMessage());
				}
			}
			
			req.setAttribute("volist", volist);
			req.getRequestDispatcher("/WEB-INF/main.jsp").forward(req, resp);
			return;
		}else if(action.equals("/acco_batch_insert")) {
			HttpSession session = req.getSession(false);
			if (session == null || session.getAttribute("loginId") == null) {
				resp.sendRedirect(req.getContextPath() + "/Login");
				return;
			}
			if (!"ADMIN".equals(session.getAttribute("role"))) {
				resp.sendError(HttpServletResponse.SC_FORBIDDEN, "관리자만 이용할 수 있습니다.");
				return;
			}
			
			resp.setContentType("text/plain; charset=UTF-8");
			ObjectMapper objectMapper = new ObjectMapper();
		    
			try {
				int result = as.bachInsertPublicAPI();
			
				if(result >= 1) {
					System.out.println("숙박 공공API 배치INSERT 성공"+result+"건");
					return;
				}else {
					writer.write("숙박 공공API 배치INSERT 실패");
					return;
				}
				
			} catch (Exception e) {
				writer.write("숙박 공공API 배치INSERT 실패");
				System.out.println(e.getMessage());
				e.printStackTrace();
	            //resp.sendError(500, "배치 수집 실패: " + e.getMessage());
				return;
			}
		}else if(action.equals("/acco_batch_insert_culture")) {
			HttpSession session = req.getSession(false);
			if (session == null || session.getAttribute("loginId") == null) {
				resp.sendRedirect(req.getContextPath() + "/Login");
				return;
			}
			if (!"ADMIN".equals(session.getAttribute("role"))) {
				resp.sendError(HttpServletResponse.SC_FORBIDDEN, "관리자만 이용할 수 있습니다.");
				return;
			}
			resp.setContentType("text/plain; charset=UTF-8");
			ObjectMapper objectMapper = new ObjectMapper();

		    
			try {
				int result = as.bachInsertPublicAPICulture();
			
				if(result >= 1) {
					System.out.println("숙박 행안부 숙박 배치INSERT 성공"+result+"건");
					return;
				}else {
					writer.write("숙박 행안부 숙박 배치INSERT 실패");
					return;
				}
				
			} catch (Exception e) {
				writer.write("숙박 행안부 숙박 배치INSERT 실패");
				System.out.println(e.getMessage());
				e.printStackTrace();
	            //resp.sendError(500, "배치 수집 실패: " + e.getMessage());
				return;
			}
		}else if(action.equals("/acco_batch_insert_culture_tourism")) {
			HttpSession session = req.getSession(false);
			if (session == null || session.getAttribute("loginId") == null) {
				resp.sendRedirect(req.getContextPath() + "/Login");
				return;
			}
			if (!"ADMIN".equals(session.getAttribute("role"))) {
				resp.sendError(HttpServletResponse.SC_FORBIDDEN, "관리자만 이용할 수 있습니다.");
				return;
			}
			
			resp.setContentType("text/plain; charset=UTF-8");
			ObjectMapper objectMapper = new ObjectMapper();
 
			try {
				int result = as.bachInsertPublicAPICultureTourism();
			
				if(result >= 1) {
					System.out.println("숙박 행안부 관광숙박 배치INSERT 성공"+result+"건");
					return;
				}else {
					writer.write("숙박 행안부 관광숙박 배치INSERT 실패");
					return;
				}
				
			} catch (Exception e) {
				writer.write("숙박 행안부 관광숙박 배치INSERT 실패");
				System.out.println(e.getMessage());
				e.printStackTrace();
	            //resp.sendError(500, "배치 수집 실패: " + e.getMessage());
				return;
			}
		}else if(action.equals("/openrouter_summary")) {
			resp.setContentType("application/json; charset=UTF-8");
			System.out.println("Accommodation/openrouter_summary 진입");
			System.out.println("1.API Key 로드");

	        try {
				System.out.println("2.화면에서 호텔명과 호텔기본키번호 수신");

	        	JsonNode requestJson = objectMapper.readTree(req.getInputStream());
	            String accommodationName = requestJson.path("accommodationName").asText();
	            System.out.println("accommodationName : "+accommodationName);
	            JsonNode idNode = requestJson.path("accommodationId");

	            // accommodationId 데이터 없거나 null이면 null, 값이 있으면 int로 파싱
	            Integer accommodationId = (idNode.isMissingNode() || idNode.isNull()) 
	                                   ? null : idNode.asInt();

	            System.out.println("accommodationId : "+accommodationId);
	            
	            if (accommodationName == null || accommodationName.isBlank()) {
	            	System.out.println("[AccommodationApiOpenrouterController] accommodationName 값 비었음");
	            	resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); //400
	            	resp.getWriter().write("{\"success\": false, \"message\": \"숙소 이름이 없습니다.\"}");
	                return;
	            }

	            if (accommodationId == null || accommodationId <= 0) {    // 수정 전 : accommodationId == null
	            	System.out.println("[AccommodationController] accommodationId 값 없음 또는 0 이하");
	            	resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	                resp.getWriter().write("{\"success\": false, \"message\": \"숙소 번호가 없습니다.\"}");
	                return;
	            }
	            
	            System.out.println("3-0.먼저 리뷰테이블 확인");
	            AccommodationReviewVo findResult = as.findReviewByAccommodationId(accommodationId);
	            
	            if(findResult.getAccommodation_id() !=null  && findResult.getAccommodation_id() > 0) {
	            	System.out.println("3-1.리뷰테이블 존재해서 결과를 화면에 전달");
	            	
	            	resp.getWriter().write(objectMapper.writeValueAsString(Map.of("success", true, "result", findResult.getContent())));
	            	return;
	            	
	            }else {//리뷰테이블에 없음 새로운 리뷰임
					System.out.println("3.리뷰테이블에 없는 업체이므로 서블릿 내부에서 동적 프롬프트 생성");
					
					String aiResponseContent= routerApi.accommodation_openrouter_summary(accommodationName);
		            
		            if (aiResponseContent != null) {
		                // 성공 응답 작성
		            	System.out.println("7.오픈라우터 응답 성공 화면으로 전달 숙박id : "+accommodationId+ ", 응답내용 :"+aiResponseContent);
//		            	{
//		            		  "pros": [
//		            		    "럭셔리한 인테리어와 고급스러운 분위기",
//		            		    "편안한 객실과 우수한 침구 제공",
//		            		    "탁월한 위치와 전망"
//		            		  ],
//		            		  "cons": [
//		            		    "높은 가격대",
//		            		    "특정 층에서 소음 문제 발생"
//		            		  ]
//		            		}
		            	
		                
		                //오픈라우터 응답검증 숙박json에 포함되어야할 부분 재 확인처리
		                String cleanJson = openrouter_review_clean(aiResponseContent);
		                System.out.println("accommodationReviewInsert() 호출전 accommodationId 확인 :"+accommodationId);
		                int insertResult = as.accommodationReviewInsert(cleanJson, accommodationId); //AI가 준 JSON 문자열을 통째로 그대로 저장하는 게 가장 간단합니다.
		                System.out.println("[AccommodationApiOpenrouterController] Accommodation/openrouter_summary 리뷰테이블 숙박id : " + accommodationId +" insert 결과 : "+ insertResult);
		               
		                if(insertResult == 0) {
		            	    throw new SQLException();
		                }
		                resp.getWriter().write(objectMapper.writeValueAsString(Map.of("success", true, "result", cleanJson)));
		            } else {
		                // 모든 모델 호출 실패 시
		            	resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
		            	resp.getWriter().write("{\"success\": false, \"message\": \"AI 서비스를 잠시 이용할 수 없습니다. 잠시 후 다시 시도해주세요.\"}");
		                return;
		            }
	            }//리뷰테이블 존재여부
	        }catch (JsonProcessingException e) {
	            System.out.println("AI 응답 JSON 파싱 실패: " + e.getMessage());
	            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	            resp.getWriter().write("{\"success\": false, \"message\": \"요약을 만들지 못했어요. 다시 시도해주세요.\"}");
	            return;
	        }catch (SQLException e) {
	            e.printStackTrace();
	            System.out.println("[AccommodationApiOpenrouterController] Accommodation/openrouter_summary 리뷰테이블 insert 중 에러발생 ");
	            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	            resp.getWriter().write("{\"success\": false, \"message\": \"서버 처리 중 오류가 발생했습니다.\"}");
	            return;
	        }catch (RuntimeException e) {
	            System.out.println("AI 응답 형식 오류: " + e.getMessage());
	            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	            resp.getWriter().write("{\"success\": false, \"message\": \"서버 처리 중 오류가 발생했습니다.\"}");
	            return;
	        }
		}else if(action.equals("/openrouter_summary_search")) {
			resp.setContentType("application/json; charset=UTF-8");
			System.out.println("Accommodation/openrouter_summary_search 진입");   
		        
		     // accommodationName kakaoPlaceId addr lat lon tel category

			String loginId = (String) req.getSession().getAttribute("loginId");   // ← 실제 세션 키 이름으로
			
	        try {
				System.out.println("화면에서 accommodationName kakaoPlaceId addr lat lon tel category 받음");

	        	JsonNode requestJson = objectMapper.readTree(req.getInputStream());
	            String accommodationName = requestJson.path("accommodationName").asText();
	            System.out.println("accommodationName : "+accommodationName);
	            JsonNode kakaoPlaceIdNode = requestJson.path("kakaoPlaceId");

	            // accommodationName kakaoPlaceId addr lat lon tel category
	            // kakaoPlaceIdNode 데이터 없거나 null이면 null, 값이 있으면 String 으로 파싱
	            String kakaoPlaceId = (kakaoPlaceIdNode.isMissingNode() || kakaoPlaceIdNode.isNull()) 
	                                   ? null : kakaoPlaceIdNode.asText();
	            System.out.println("accommodationName : "+accommodationName +" , kakaoPlaceId : "+kakaoPlaceId);
	            
	            if (accommodationName == null || accommodationName.isBlank()) {
	            	System.out.println("[AccommodationApiOpenrouterController] accommodationName 필드 비었음");
	            	resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	            	resp.getWriter().write("{\"success\": false, \"message\": \"숙소 이름이 없습니다.\"}");
	                return;
	            }

	            if (kakaoPlaceId == null) {
	            	System.out.println("[AccommodationApiOpenrouterController] kakaoPlaceId 필드 비었음");
	            	resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	            	resp.getWriter().write("{\"success\": false, \"message\": \"카카오 장소 번호가 없습니다.\"}");
	                return;
	            }
	            
	            // accommodationName kakaoPlaceId addr lat lon tel category
	            AccommodationVo findResult = as.findAccommodationByPlaceid(kakaoPlaceId);
	            System.out.println("kakaoPlaceId 숙박db에 존재하는지 확인 후 결과 : " +findResult);
	            
	            if(findResult.getAccommodation_id() !=null  && findResult.getAccommodation_id() > 0) {
	            	System.out.println("카카오placeid가 숙박db 존재하기에 숙박 기본키로 리뷰 조회");
	            	AccommodationReviewVo reviewVo = as.findReviewByAccommodationId(findResult.getAccommodation_id());
	            	
	            	//첫번째 리뷰 존재하는 경우
	            	if(reviewVo.getReview_id() !=null  && reviewVo.getReview_id() > 0) {
	            		System.out.println("리뷰도 존재함 Gson() 생성해서 화면으로 전달");
//	            		resp.getWriter().write(objectMapper.writeValueAsString(Map.of("success", true, "result", reviewVo.getContent())));
//		            	return;
	            		
	            		boolean isliked = as.isAccommodationLiked(loginId, findResult.getAccommodation_id());
	            		resp.getWriter().write(objectMapper.writeValueAsString(
	            			Map.of("success", true, "result", reviewVo.getContent(),
	            			       "accommodationId",  findResult.getAccommodation_id(), "isliked", isliked)));
	            		
	            	}else {//2번째 리뷰테이블에 없음 새로운 리뷰임
	            		System.out.println("리뷰테이블에 없는 업체이므로 서블릿 내부에서 동적 프롬프트 생성");
					
	            		String aiResponseContent= routerApi.accommodation_openrouter_summary(accommodationName);
		            
			            if (aiResponseContent != null) {
			                // 성공 응답 작성
			            	System.out.println("오픈라우터 응답 성공 화면으로 전달 : "+ findResult.getAccommodation_id() +" , 응답내용 : "+aiResponseContent);
//			            	{
//			            		  "pros": [
//			            		    "럭셔리한 인테리어와 고급스러운 분위기",
//			            		    "편안한 객실과 우수한 침구 제공",
//			            		    "탁월한 위치와 전망"
//			            		  ],
//			            		  "cons": [
//			            		    "높은 가격대",
//			            		    "특정 층에서 소음 문제 발생"
//			            		  ]
//			            		}
			                 
			                String cleanJson = openrouter_review_clean(aiResponseContent);
			                System.out.println("accommodationReviewInsert() 호출전 accommodationId 확인 :"+findResult.getAccommodation_id());
			                int insertResult = as.accommodationReviewInsert(cleanJson, findResult.getAccommodation_id()); //AI가 준 JSON 문자열을 통째로 그대로 저장하는 게 가장 간단합니다.
			                System.out.println("[AccommodationApiOpenrouterController] Accommodation/openrouter_summary 리뷰테이블 숙박id : "+findResult.getAccommodation_id()+" insert 결과 : "+ insertResult);
			               
			                if(insertResult == 0) {
			            	    throw new SQLException();
			                }
			               //resp.getWriter().write(objectMapper.writeValueAsString(Map.of("success", true, "result", cleanJson)));

		            		boolean isliked = as.isAccommodationLiked(loginId, findResult.getAccommodation_id());
		            		resp.getWriter().write(objectMapper.writeValueAsString(
		            			Map.of("success", true, "result", cleanJson, //reviewVo.getContent() 기존 바꿈 2026.10.24 null 이기에 NPE발생
		            			       "accommodationId",  findResult.getAccommodation_id(), "isliked", isliked)));
			                return;
			            } else {
			                // 모든 모델 호출 실패 시
			            	System.out.println("오픈라우터 응답 실패");
			            	resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);//503
			            	resp.getWriter().write("{\"success\": false, \"message\": \"AI 서비스를 잠시 이용할 수 없습니다. 잠시 후 다시 시도해주세요.\"}");
			                return;
			            }
	            	}//리뷰테이블 존재여부
	            }//카카오placeid 존재여부 즉, 검색시 숙박db에 있는 업체인지여부
	            else {
	            	//세번째 숙소를 새로 등록한 경우
	            	System.out.println("카카오placeid가 숙박db 존재하지 않는다. insert처리하자");
	            	

	   		     // accommodationName kakaoPlaceId addr lat lon tel category 7개
		            String addr = requestJson.path("addr").asText();
		            Double lat = requestJson.path("lat").asDouble();
		            Double lon = requestJson.path("lon").asDouble();
		            String tel = requestJson.path("tel").asText();
		            String category = requestJson.path("category").asText();
		            System.out.println("서버 전달된 값확인 addr :"+addr +", lat : "+lat +", lon : "+lon +" ,tel :"+tel +", category : "+category);
		           
		            AccommodationVo vo = new AccommodationVo();
		            vo.setAddr(addr);
		            vo.setLat(lat);
		            vo.setLon(lon);
		            vo.setName(accommodationName);
		            vo.setPlaceid(kakaoPlaceId);
		            vo.setTel(tel);
		            vo.setType(category);
		            
		            int result =as.registerAccommodation(vo);
		            
		            //카카오placeid가 숙박db 존재하지 않는다. 리뷰생성
		            System.out.println("서버전달된 값으로 INSERT 처리 후: "+result);
		        	
		            String aiResponseContent= routerApi.accommodation_openrouter_summary(accommodationName);
		            System.out.println("오픈라우터 요청");
		            if (aiResponseContent != null) {
		                // 성공 응답 작성
		            	System.out.println("오픈라우터 응답 성공 화면으로 전달 숙박id :"+ vo.getAccommodation_id()+" , 응답내용 : "+aiResponseContent);
		            
		            	  int id = as.searchAccommodationId_registerAccommodation(kakaoPlaceId);
		            	  
		            	  if( id !=0 ) {
		            		  //숙박 id값이 존재하면 리뷰테이블에 insert
		            		  System.out.println("숙박테이블에 등록한 후 accommodation_id 값 조회 : "+id);
		            		  
		            		//리뷰 db저장전 처리  
		            		String cleanJson = openrouter_review_clean(aiResponseContent);
			                int insertResult = as.accommodationReviewInsert(cleanJson, id); //AI가 준 JSON 문자열을 통째로 그대로 저장하는 게 가장 간단합니다.
			                System.out.println("[AccommodationApiOpenrouterController] Accommodation/openrouter_summary 리뷰테이블 숙박 id : "+vo.getAccommodation_id()+" insert 결과 : "+ insertResult);
			               
			                if(insertResult == 0) {
			            	    throw new SQLException();
			                }
			                
//			                resp.getWriter().write(objectMapper.writeValueAsString(Map.of("success", true, "result", cleanJson)));
		            		boolean isliked = as.isAccommodationLiked(loginId, id); // findResult.getAccommodation_id() 2026.10.24토 id에 값있다
		            		resp.getWriter().write(objectMapper.writeValueAsString(
		            			Map.of("success", true, "result",cleanJson,
		            			       "accommodationId", id, "isliked", isliked))); // findResult.getAccommodation_id() 2026.10.24토 id에 값있다
			                return;
		            	 
		            	  }else { //검색처리에서 숙박테이블에 placeid 없어서 받은 데이터를 숙박테이블에 insert 하고 
		            		  	  //오픈라우터 요청후 리뷰insert 전 숙박 id값이 없는경우 
		            		  System.out.println("숙박테이블에 등록한 후 accommodation_id 값 없음");
		            		  resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //500
		            		  resp.getWriter().write("{\"success\": false, \"message\": \"서버 처리 중 오류가 발생했습니다.\"}");
		  	                  return;
		            	  }
		            }else {
		            	 //오픈라우터 결과 없음
		            	 System.out.println("오픈라우터 응답 실패"); //503
		            	 resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
		            	 resp.getWriter().write("{\"success\": false, \"message\": \"AI 서비스를 잠시 이용할 수 없습니다. 잠시 후 다시 시도해주세요.\"}");
	  	                 return;
		            }
	            }//카카오placeid 존재여부 즉, 검색시 숙박db에 있는 업체인지여부
	        }catch (Exception e) {
	            e.printStackTrace();
	            System.out.println(e.getMessage());
	            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	            resp.getWriter().write("{\"success\": false, \"message\": \"서버 처리 중 오류가 발생했습니다.\"}");
	            return;
	        }
		}//search
		else if(action.equals("/acco_distance")) {
			System.out.println("[AccommodationController] Accommoation/acco_distance  진입");
			resp.setContentType("text/json; charset=utf-8");
			// accommodation_id distance
			
			JsonNode requestJson = objectMapper.readTree(req.getInputStream()); 
            int accommodation_id = requestJson.path("accommodation_id").asInt(); // int Integer
            System.out.println("accommodation_id : "+accommodation_id);
            int distance = requestJson.path("distance").asInt();
            System.out.println("distance : "+distance);
            
            // 1. 입력값 확인 → 400 (asInt()는 null이 아니라 0을 주므로 0 이하로 확인)
			if (accommodation_id <= 0 || distance <= 0) {
				resp.setStatus(400);
				resp.getWriter().write("{\"success\": false, \"message\": \"숙소 번호 또는 거리가 올바르지 않습니다.\"}");
				return;
			}
            
            
            AccommodationVo vo;
			try {
				
				vo = as.findAccommdationByAccommdationId(accommodation_id);
				System.out.println("findAccommdationByAccommdationId() AccommdationVo 반환");
				
				if (vo.getAccommodation_id() == null) {
					resp.setStatus(404);
					resp.getWriter().write("{\"success\": false, \"message\": \"숙소를 찾을 수 없습니다.\"}");
					return;
				}
				
				List<AccommodationVo> volist = as.st_distance_sphere(vo.getLon(), vo.getLat(), accommodation_id, distance);
				//System.out.println("거리계산 후 List<AccommodationVo> 반환 응답처리 List<AccommodationVo> : "+volist);
				resp.getWriter().write(new Gson().toJson(Map.of("success", true, "list", volist)));
            	return;

			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("[AccommodationApiOpenrouterController]  Accommoation/acco_distance db문제 "+e);
            	resp.setStatus(500); 
				resp.getWriter().write("{\"success\": false, \"message\": \"주변 숙소를 불러오지 못했습니다.\"}");
				
                return;
			}
		}else if(action.equals("/acco_distance_lon_lat")) {
			
			//lat :lat, lon: lon, distance: radius , accommodation_id: accommodation_id
			System.out.println("[AccommodationController] Accommoation/acco_distance_lon_lat  진입");
			resp.setContentType("text/json; charset=utf-8");
			// accommodation_id distance
			
			JsonNode requestJson = objectMapper.readTree(req.getInputStream()); 
            Integer accommodation_id = requestJson.path("accommodation_id").asInt(); 
            Double lat = requestJson.path("lat").asDouble(); 
            Double lon = requestJson.path("lon").asDouble(); 
            System.out.println("accommodation_id : "+accommodation_id);
            int distance = requestJson.path("distance").asInt();
            System.out.println("distance : "+distance+" , lat :"+lat +" , lon : "+ lon);
            
            if (lat == 0 || lon == 0 || distance <= 0) {
				resp.setStatus(400);
				resp.getWriter().write("{\"success\": false, \"message\": \"위치 또는 거리가 올바르지 않습니다.\"}");
				return;
			}

			
			try {
				List<AccommodationVo> volist = as.st_distance_sphere(lon, lat, accommodation_id, distance);
				//System.out.println("거리계산 후 List<AccommodationVo> 반환 응답처리 List<AccommodationVo> : "+volist);
				resp.getWriter().write(new Gson().toJson(Map.of("success", true, "list", volist)));
				return;
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("[AccommodationApiOpenrouterController]  Accommoation/acco_distance_lon_lat db문제 "+e);
				resp.setStatus(500);
				resp.getWriter().write("{\"success\": false, \"message\": \"주변 숙소를 불러오지 못했습니다.\"}");
				return;
			}
		}else if(action.equals("/acco_like")) {
			resp.setContentType("application/json; charset=UTF-8");

			 // 0. POST만 허용 → 400 (데이터 변경은 POST만)
		    if (!"POST".equals(req.getMethod())) {
		        resp.setStatus(400);
		        resp.getWriter().write("{\"success\": false, \"message\": \"잘못된 요청 방식입니다.\"}");
		        return;
		    }
			
			// 1. 로그인 확인 → 401 (TC-042·044·045)
			String loginId = sessionCheck(req);
			if (loginId == null) {
				resp.setStatus(401);
				resp.getWriter().write("{\"success\": false, \"message\": \"로그인이 필요합니다.\"}");
				return;
			}

			// 2. 입력값 확인 → 400 (수정 전 404)
			JsonNode requestJson;
			try {
				requestJson = objectMapper.readTree(req.getInputStream());
			} catch (IOException e) {   // JSON이 깨진 경우
				resp.setStatus(400);
				resp.getWriter().write("{\"success\": false, \"message\": \"잘못된 요청입니다.\"}");
				return;
			}
			int accommodationId = requestJson.path("accommodationId").asInt();
			if (accommodationId <= 0 || !requestJson.path("liked").isBoolean()) {
				resp.setStatus(400);
				resp.getWriter().write("{\"success\": false, \"message\": \"잘못된 요청입니다.\"}");
				return;
			}
			boolean liked = requestJson.path("liked").asBoolean();

			try {
				// 3. 없는 숙소 → 400 (TC-046)
				AccommodationVo vo = as.findAccommdationByAccommdationId(accommodationId);
				if (vo.getAccommodation_id() == null) {
					resp.setStatus(400);
					resp.getWriter().write("{\"success\": false, \"message\": \"없는 숙소입니다.\"}");
					return;
				}

				// 4. 찜 추가·해제 (TC-047)
				//    이미 찜한 숙소를 또 찜하거나, 이미 해제된 걸 또 해제해도
				//    원하는 상태가 됐으므로 성공으로 응답 (결과가 0이어도 성공)
				if (liked) {
					as.insertLike(loginId, accommodationId);
				} else {
					as.deleteLike(loginId, accommodationId);
				}
				resp.getWriter().write("{\"success\": true, \"liked\": " + liked + "}");
				return;

			} catch (SQLException e) {
				e.printStackTrace();   // TC-048 : 예외 로그 남기기
				System.out.println("[AccommodationController] /acco_like 처리 중 예외 " + e);
				resp.setStatus(500);
				resp.getWriter().write("{\"success\": false, \"message\": \"찜을 처리하지 못했습니다.\"}");
				return;
			}
		} else {
		    resp.sendError(HttpServletResponse.SC_NOT_FOUND);//404
		}
	}//doHandle();
	
	
	
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


	@Override
	// =========================================================
	// 주제: init 처리
	// 설명: init 기능에 필요한 처리 로직을 수행
	// =========================================================
	public void init() throws ServletException {	
		super.init();
        as = new AccommodationService();
        routerApi = new AccommodationChatbotOpenrouterApi();
	}
	
	
	//오픈라우터 json 문법 확인 이미 replaceAll("```json|```", "").trim(); 처리되어서 전달됨
	// =========================================================
	// 주제: openrouter_review_clean 처리
	// 설명: openrouter_review_clean 기능에 필요한 처리 로직을 수행
	// =========================================================
	private String openrouter_review_clean(String afterReplaceAll) throws JsonMappingException, JsonProcessingException {

		 ObjectMapper mapper = new ObjectMapper();
	     JsonNode summaryNode;
		 
	     	try {
		        summaryNode = mapper.readTree(afterReplaceAll);
		    } catch (JsonProcessingException e) {
		        // 배열 닫는 괄호 실수 보정: ..."}} → ..."]}
//		        String fixed = result.replaceAll("\"\\s*\\}\\s*\\}$", "\"]}");
//		        summaryNode = mapper.readTree(fixed);   // 그래도 실패하면 예외가 호출한 쪽으로 넘어감
//		        result = fixed;
		    	 
		    	// 괄호가 깨졌으면 pros/cons 문장만 뽑아서 새로 만들기
		    	afterReplaceAll = rebuildProsCons(afterReplaceAll, mapper);
		        summaryNode = mapper.readTree(afterReplaceAll);
		    }
		// 필요한 항목이 있는지까지 확인하면 더 안전
	     	JsonNode pros = summaryNode.path("pros");
	        JsonNode cons = summaryNode.path("cons");

	        // 항목이 없거나, 배열이 아니거나, 둘 다 비어 있으면 실패 처리
	        if (!pros.isArray() || !cons.isArray()) {
	            throw new RuntimeException("장단점 형식 오류: " + afterReplaceAll);
	        }
	        if (pros.size() == 0 && cons.size() == 0) {
	            throw new RuntimeException("장단점이 모두 비어 있음: " + afterReplaceAll);
	        }

		return afterReplaceAll;
	}

	// pros, cons 뒤의 "문장"들만 뽑아서 올바른 JSON으로 다시 만들기
	// =========================================================
	// 주제: rebuildProsCons 처리
	// 설명: rebuildProsCons 기능에 필요한 처리 로직을 수행
	// =========================================================
	private String rebuildProsCons(String text, ObjectMapper mapper) throws JsonProcessingException {
		int prosIdx = text.indexOf("\"pros\"");
		int consIdx = text.indexOf("\"cons\"");
		if (prosIdx == -1 || consIdx == -1) {
			throw new RuntimeException("pros/cons 키가 없음: " + text);
		}

		String prosPart, consPart;
		if (prosIdx < consIdx) {   // 보통은 pros가 먼저
			prosPart = text.substring(prosIdx + 6, consIdx);
			consPart = text.substring(consIdx + 6);
		} else {                   // 혹시 cons가 먼저 온 경우
			consPart = text.substring(consIdx + 6, prosIdx);
			prosPart = text.substring(prosIdx + 6);
		}

		Map<String, List<String>> map = new LinkedHashMap<>();   // pros, cons 순서 유지
		map.put("pros", extractQuoted(prosPart));
		map.put("cons", extractQuoted(consPart));
		return mapper.writeValueAsString(map);
	}

	// "..." 로 감싸진 문장들만 꺼내기
	// =========================================================
	// 주제: extractQuoted 처리
	// 설명: extractQuoted 기능에 필요한 처리 로직을 수행
	// =========================================================
	private List<String> extractQuoted(String part) {
		List<String> list = new ArrayList<>();
		Matcher m = Pattern.compile("\"([^\"]*)\"").matcher(part);
		while (m.find()) {
			String s = m.group(1).trim();
			if (!s.isEmpty()) list.add(s);
		}
		return list;
	}
	
	// =========================================================
	// 주제: sessionCheck 처리
	// 설명: sessionCheck 기능에 필요한 처리 로직을 수행
	// =========================================================
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

}
