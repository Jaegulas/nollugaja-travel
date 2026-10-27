package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import api.AccommodationChatbotOpenrouterApi;
import dao.MypageDao;
import dao.ReviewDao;
import dto.MypageReviewDto;
import service.ChatbotService;
import vo.ItineraryJsonVo;
import vo.MypageLikeVo;

// =========================================================
// 주제: 파일 역할
// 설명: MypageController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


@WebServlet("/Mypage/*")
public class MypageController  extends HttpServlet{
	
	
	
	private AccommodationChatbotOpenrouterApi openrouterApi;
    private final ObjectMapper mapper = new ObjectMapper();

    private final MypageDao dao = new MypageDao();

	private ChatbotService cs;

	// =========================================================
	// 주제: doHandle 처리
	// 설명: doHandle 기능에 필요한 처리 로직을 수행
	// =========================================================
	protected void doHandle(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		resp.setContentType("text/html; charset=UTF-8");
		String contextPath = req.getContextPath();
		
		String action = req.getPathInfo();
		if (action == null) {
		    resp.sendError(HttpServletResponse.SC_NOT_FOUND); //404
		    return;
		}
		
		System.out.println("[MypageController] doHandle() getPathInfo() 값 "+action);
		
		if(action.equals("/findItineraryJsonFromSharekey")) {
		    	
		    	
	    	  String key = req.getParameter("key");
	    	  
	          // 1. 공유키 모양 확인 (UUID 36자리가 아니면 DB 조회 안 함)
	          if (key == null || !key.matches("[0-9a-fA-F-]{36}")) {
	        	  resp.setContentType("text/html; charset=UTF-8");
	        	  PrintWriter out = resp.getWriter();
	        	  out.print("<script>");
	        	  out.print("alert('삭제되었거나 없는 일정입니다.');");
	        	  out.print("location.href='" + req.getContextPath() + "/Main';");
	        	  out.print("</script>");
	        	  return;
	          }
	   
	          ItineraryJsonVo vo = dao.getPlanBySharekey(key);

		      System.out.println("[MypageController] /findItineraryJsonFromSharekey db에서 공유키로 ItineraryJsonVo 객체얻기 : "+ vo);
	          
		      if (vo == null) {
		    	  resp.setContentType("text/html; charset=UTF-8");
		    	  PrintWriter out = resp.getWriter();
		    	  out.print("<script>");
		    	  out.print("alert('삭제되었거나 없는 일정입니다.');");
		    	  out.print("location.href='" + req.getContextPath() + "/Main';");
		    	  out.print("</script>");
		    	  return;
	          }
	   
	          // 3. JSP 의 <script> 안에 그대로 넣을 거라서
	          //    일정 글자 안에 </script> 가 있어도 태그가 닫히지 않게 바꿔주기
	          String planjson = vo.getPlanjson().replace("</", "<\\/");
	   
	          req.setAttribute("planTitle", vo.getTitle());
	          req.setAttribute("planjson", planjson);
          	  req.setAttribute("center", "mypage/shareItinerary.jsp");
			  req.setAttribute("pageCss", "mypage/shareItinerary.css");

			  req.getRequestDispatcher("/WEB-INF/main.jsp").forward(req, resp);
			  return;
   	      
	    }
		 
		 
		HttpSession session = req.getSession(false);
        String loginId = null;

	     //session이 null이 아닌지 먼저 체크 (Null Pointer Exception 방지)
	     if (session != null) {
	         loginId = (String) session.getAttribute("loginId");
	     }

	     //로그인 여부 판단
	     if (loginId == null) {
	         // 비로그인 사용자 처리 (로그인 페이지로 리다이렉트 등)
	    	System.out.println("비회원 마이페이지 이동시 회원가입페이지로 이동시킴");
			//req.setAttribute("center", "members/registration.jsp");
	    	req.getRequestDispatcher("/Login").forward(req, resp);
			return;
		 }
	     

		req.setAttribute("pageCss", "mypage/mypage_main.css");
		req.setAttribute("center", "mypage/mypage_main.jsp");
		     
	     
		 if(action.equals("/mypage_main")) {
			resp.setContentType("text/html; charset=UTF-8");
	  	 	PrintWriter out = resp.getWriter();
            
            try {
				List<ItineraryJsonVo> list = cs.itineraryjsonList(loginId);
				
				req.setAttribute("volist", list);
				
				
				req.setAttribute("pageCss", "mypage/mypage_main.css");
				req.setAttribute("center", "mypage/mypage_main.jsp");
				req.getRequestDispatcher("/WEB-INF/main.jsp").forward(req, resp);
				return;
				
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("[MypageController] /Mypage/mypage 마이페이지 json목록보여주기 select 에러 "+e);
				// 화면 요청이라 JSON 대신 500 에러 페이지
				resp.sendError(500, "일정표 목록을 불러오지 못했습니다.");
                return;
		
			}

		
		}else if(action.equals("/deleteItineraryJsonPlan")) {
			req.setCharacterEncoding("UTF-8");
	        resp.setContentType("application/json; charset=UTF-8");
	        PrintWriter out = resp.getWriter();

	        // 주소창(GET)으로 삭제되지 않게 POST만 허용 (TC-076)
	        if (!"POST".equals(req.getMethod())) {
	            resp.setStatus(400);
	            out.print("{\"success\": false, \"message\": \"잘못된 요청 방식입니다.\"}");
	            return;
	        }
	        
	        int planid;
	        try {
	            planid = Integer.parseInt(req.getParameter("planid"));
	        } catch (NumberFormatException e) {
	            out.print("{\"success\":false,\"message\":\"잘못된 요청입니다.\"}");
	            return;
	        }

	        MypageDao dao = new MypageDao();
	        
	        int count =0;
	        
			try {
				count = dao.deleteItineraryJsonPlan(planid, loginId);

		        System.out.println("[MypageController] /deleteItineraryJsonPlan db에서 현재 회원 "+loginId +" , 여행일정표번호 "+planid +" , 삭제결과 :" + count);
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("[MypageController] /Mypage/deleteItineraryJsonPlan 삭제 에러발생 "+e);
	            out.print("{\"success\":false,\"message\":\"삭제할 일정이 없거나 권한이 없습니다.\"}");
	            return;
			}

	        if (count == 1) {
	            out.print("{\"success\":true}");
	            return;
	        } else {
	            out.print("{\"success\":false,\"message\":\"삭제할 일정이 없거나 권한이 없습니다.\"}");
	            return;
	        }
	    }else if(action.equals("/itineraryJsonSharekeyPro")) {
			req.setCharacterEncoding("UTF-8");
	        resp.setContentType("application/json; charset=UTF-8");
	        PrintWriter out = resp.getWriter();

	        // 1. 로그인 확인
	        String memberid = (String) req.getSession().getAttribute("loginId");
	        if (memberid == null) {
	        	resp.setStatus(401);   
	            out.print("{\"success\":false,\"message\":\"로그인이 필요합니다.\",\"sharekey\":\"\"}");
	            return;
	        }
	 
	        // 2. planid 숫자 확인
	        int planid;
	        try {
	            planid = Integer.parseInt(req.getParameter("planid"));
	        } catch (NumberFormatException e) {
	        	resp.setStatus(400);   
	            out.print("{\"success\":false,\"message\":\"잘못된 요청입니다.\",\"sharekey\":\"\"}");
	            return;
	        }
	 
	        // 3. 공유키 가져오기 (없으면 새로 만들기)
	        MypageDao dao = new MypageDao();
	        String sharekey = dao.getOrCreateSharekey(planid, memberid);
	        System.out.println("[MypageController] /itineraryjsonSharekeyPro db에서 가져온 공유키 확인 : "+ sharekey);
	 
	        if (sharekey == null) {
	        	resp.setStatus(400);   
	            out.print("{\"success\":false,\"message\":\"일정이 없거나 권한이 없습니다.\",\"sharekey\":\"\"}");
	            return;
	        }
	 
	        // UUID 는 영문·숫자·- 만 있어서 그대로 넣어도 JSON 이 깨지지 않음
	        out.print("{\"success\":true,\"message\":\"\",\"sharekey\":\"" + sharekey + "\"}");
	        
	     // ★ 공유 링크는 비회원(카톡으로 받은 친구)도 봐야 하므로 로그인 확인보다 먼저 처리 (TC-079)
	    }else if(action.equals("/mypageLikeListPerMember")) {
		    
	    	 resp.setContentType("application/json; charset=UTF-8");
	         PrintWriter out = resp.getWriter();
	  
	         // 찜 목록 가져오기
	         List<MypageLikeVo> list = new ArrayList<>();
			 
	         try {
			 	 list = dao.mypageLikeListPerMember(loginId);
			 } catch (SQLException e) {
				 e.printStackTrace();
				 System.out.println("[MypageController] /Mypage/mypageLikeListPerMember mypageLikeListPerMember() 예외발생 ");
				 resp.setStatus(500);                                                       // 추가
				 out.print("{\"success\": false, \"message\": \"찜 목록을 불러오지 못했습니다.\"}");  // 추가
				 return;    
			 }

	         Map<String, Object> result = new HashMap<>();
	         result.put("success", true);
	         result.put("list", list);
	         resp.getWriter().write(mapper.writeValueAsString(result));

	    }else if(action.equals("/mypageLikeDeletePerMember")) {
	    	  req.setCharacterEncoding("UTF-8");
	          resp.setContentType("application/json; charset=UTF-8");
	          deleteLikePerMember(req, resp, loginId);
	          return;
	      
        }else if(action.equals("/getOnlyItineraryJsonContent")) {
	    	  
	    	   resp.setContentType("application/json; charset=UTF-8");
	           PrintWriter out = resp.getWriter();
	   
	           int planid;
	           try {
	               planid = Integer.parseInt(req.getParameter("planid"));
	           } catch (NumberFormatException e) {
	        	   resp.setStatus(400); 
	               out.print("{\"success\":false,\"message\":\"잘못된 요청입니다.\"}");
	               return;
	           }
	    
	           // 3. 본인 일정만 가져오기
	           MypageDao dao = new MypageDao();
	           String planjson;
	           
				try {
					planjson = dao.getOnlyItineraryJsonContent(planid, loginId);
				} catch (SQLException e) {
					e.printStackTrace();
					resp.setStatus(500);
					out.print("{\"success\":false,\"message\":\"서버 처리 중 오류가 발생했습니다.\"}");
		            return;
				}
		    
	           if (planjson == null || planjson.isEmpty()) {
	        	   resp.setStatus(400);
	               out.print("{\"success\":false,\"message\":\"일정이 없거나 권한이 없습니다.\"}");
	               return;
	           }
	    
	           // 4. planjson 은 이미 JSON 글자라서 그대로 보내면 됨
	           out.print(planjson);
	           return;
	       }else if(action.equals("/getMypageReviewAll")) {
	    	  
	    	   resp.setContentType("application/json; charset=UTF-8");
	           PrintWriter out = resp.getWriter();
	   
	           // 2. 내 리뷰 목록 가져오기
	           List<MypageReviewDto> list= new ArrayList<>();
			try {
				list = new ReviewDao().getMypageReviewAll(loginId);
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("[MypageController] /Mypage/getMypageReviewAll 예외발생 ");
				resp.setStatus(500);                                                       // 추가
				out.print("{\"success\": false, \"message\": \"리뷰 목록을 불러오지 못했습니다.\"}");  // 추가
				return;    
			}

	           // 3. DTO 목록 → JSON 으로 바꿔서 보내기
	           //    (Jackson 이 getter 이름을 보고 "reviewId", "likeCount" ... 로 만들어 줌)
	           Map<String, Object> result = new HashMap<>();
	           result.put("success", true);
	           result.put("list", list);
	           resp.getWriter().write(mapper.writeValueAsString(result));
	           return;
	       }else { resp.sendError(HttpServletResponse.SC_NOT_FOUND); }
	      
		 
		 
	}//doHandle
	
	
	
	// 찜 해제 : 유효성 검사 → 삭제 → JSON 응답 까지 전부 여기서
	// =========================================================
	// 주제: deleteLikePerMember 처리
	// 설명: 대상 데이터를 삭제하거나 제거
	// =========================================================
	private void deleteLikePerMember(HttpServletRequest req, HttpServletResponse resp, String loginId)
	        throws ServletException, IOException {

	    resp.setContentType("application/json; charset=UTF-8");

	    // 0. POST 로만 받기
	    if (!"POST".equals(req.getMethod())) {
	        Map<String, Object> result = new HashMap<>();
	        result.put("success", false);
	        result.put("message", "잘못된 요청 방식입니다.");
	        resp.setStatus(400);
	        resp.getWriter().write(mapper.writeValueAsString(result));
	        return;
	    }

	    // 1. 로그인 확인
	    if (loginId == null) {
	        Map<String, Object> result = new HashMap<>();
	        result.put("success", false);
	        result.put("message", "로그인이 필요합니다.");
	        resp.setStatus(401);
	        resp.getWriter().write(mapper.writeValueAsString(result));
	        return;
	    }

	    String type  = req.getParameter("type");
	    String idStr = req.getParameter("id");

	    // 2. type : 비어있는지
	    if (type == null || type.trim().isEmpty()) {
	        Map<String, Object> result = new HashMap<>();
	        result.put("success", false);
	        result.put("message", "찜 종류가 전달되지 않았습니다.");
	        resp.setStatus(400);
	        resp.getWriter().write(mapper.writeValueAsString(result));
	        return;
	    }
	    type = type.trim();

	    // 3. type : 정해진 4개 중 하나인지
	    if (!type.equals("acc") && !type.equals("place")
	            && !type.equals("food") && !type.equals("heritage")) {
	        Map<String, Object> result = new HashMap<>();
	        result.put("success", false);
	        result.put("message", "알 수 없는 찜 종류입니다.");
	        resp.setStatus(400);
	        resp.getWriter().write(mapper.writeValueAsString(result));
	        return;
	    }

	    // 4. id : 비어있는지
	    if (idStr == null || idStr.trim().isEmpty()) {
	        Map<String, Object> result = new HashMap<>();
	        result.put("success", false);
	        result.put("message", "찜 번호가 전달되지 않았습니다.");
	        resp.setStatus(400);
	        resp.getWriter().write(mapper.writeValueAsString(result));
	        return;
	    }

	    // 5. id : 숫자인지
	    int id;
	    try {
	        id = Integer.parseInt(idStr.trim());
	    } catch (NumberFormatException e) {
	        Map<String, Object> result = new HashMap<>();
	        result.put("success", false);
	        result.put("message", "찜 번호가 올바르지 않습니다.");
	        resp.setStatus(400);
	        resp.getWriter().write(mapper.writeValueAsString(result));
	        return;
	    }

	    // 6. id : 1 이상인지
	    if (id <= 0) {
	        Map<String, Object> result = new HashMap<>();
	        result.put("success", false);
	        result.put("message", "찜 번호가 올바르지 않습니다.");
	        resp.setStatus(400);
	        resp.getWriter().write(mapper.writeValueAsString(result));
	        return;
	    }

	    System.out.println("[MypageController] /Mypage/mypageLikeDeletePerMember 회원: "+loginId+ ", 삭제유형 : "+type+" , 삭제번호 : "+id);
        
	    // 7. 여기까지 오면 type, id 모두 올바름 → 바로 삭제
	    boolean ok = false;
		try {
			ok = dao.mypageLikeDeletePerMember(loginId, type, id);
		} catch (SQLException e) {
			e.printStackTrace();
		    System.out.println("[MypageController] /Mypage/mypageLikeDeletePerMember mypageLikeListPerMember() 예외발생 ");
		    resp.setStatus(500);                                                            // 추가
		    resp.getWriter().write("{\"success\": false, \"message\": \"서버 처리 중 오류가 발생했습니다.\"}");  // 추가
		    return;    
		}

	    if (ok) {
	        Map<String, Object> result = new HashMap<>();
	        result.put("success", true);
	        resp.getWriter().write(mapper.writeValueAsString(result));
	    } else {
	        Map<String, Object> result = new HashMap<>();
	        result.put("success", false);
	        result.put("message", "이미 해제되었거나 찾을 수 없는 찜입니다.");
	        resp.setStatus(400);
	        resp.getWriter().write(mapper.writeValueAsString(result));
	    }
	}

	@Override
	// =========================================================
	// 주제: doGet 처리
	// 설명: GET 요청을 받아 필요한 데이터를 준비하고 화면 또는 응답으로 전달
	// =========================================================
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doHandle(req, resp);
	}

	@Override
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
		
		cs = new ChatbotService();
	}
	
	
	

	
	
}
