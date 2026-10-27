package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.GuideReservationDao;
import dao.RegisterDao;
import dao.ReviewDao;
import dto.AdminReservationDto;
import dto.AdminReviewDto;
import dto.RegisterDto;

// =========================================================
// 주제: 파일 역할
// 설명: AdminController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


@WebServlet("/Admin/*")
public class AdminController  extends HttpServlet{
	
	
	//전체회원목록
	private final RegisterDao registerDao = new RegisterDao();
	
	
	//리뷰목록
	private final ReviewDao reviewDao = new ReviewDao();
		

	//리뷰목록
	private final GuideReservationDao guideReservationDao= new GuideReservationDao();
	

	// =========================================================
	// 주제: doHandle 처리
	// 설명: doHandle 기능에 필요한 처리 로직을 수행
	// =========================================================
	private void doHandle(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		

        // 1. 세션이 없거나 관리자 플래그가 true가 아닌 경우 차단
//        if (session == null || session.getAttribute("isAdmin") == null || !(Boolean) session.getAttribute("isAdmin")) {
//            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "접근 권한이 없습니다."); // 403 에러
//            return;
//        }
        HttpSession session = req.getSession(false); // 기존 세션이 없으면 null 반환
        // 1. 로그인 안 함 → 로그인 페이지로
        if (session == null || session.getAttribute("loginId") == null) {
            resp.sendRedirect(req.getContextPath() + "/Login");
            return;
        }

        // 2. 로그인했지만 관리자가 아님 → 403 페이지
        String loginRole = (String) session.getAttribute("role");

        if (!"ADMIN".equals(loginRole)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "접근 권한이 없습니다.");
            return;
        }
     
		resp.setContentType("text/html; charset=utf-8");
		String contextPath = req.getContextPath();
		
		String action = req.getPathInfo();
		if (action == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
		System.out.println("[AdminController] doHandle() getPathInfo() 값 "+action);
		
		
		
		if(action.equals("/admin_main")) {
			// 년·월 검사 + setyear, setmonth, errorMsg, minYear, maxYear 저장 (TC-111)
			//req.setAttribute("setyear", setyear);
			//req.setAttribute("setmonth",setmonth);
			//아래코드에서 넣고 전달되서 온다
			String[] period = checkYearMonth(req);
			String startDate = period[0];
			String endDate   = period[1];

			List<RegisterDto> dtolist = new ArrayList<>();
			try {
				dtolist = registerDao.allMemberDetail(startDate, endDate);
			} catch (SQLException e) {
				e.printStackTrace();
				// 빈 목록과 구분되게 안내 (TC-115)
				req.setAttribute("errorMsg", "회원 목록을 불러오는 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
				req.setAttribute("dbError", true);
			}

			req.setAttribute("tab", "member");
			req.setAttribute("memberList", dtolist);
			req.setAttribute("pageCss", "admin/admin_main.css");
			req.setAttribute("center", "admin/admin_main.jsp");
			req.getRequestDispatcher("/WEB-INF/main.jsp").forward(req, resp);
			return;
		
		
			// ===== 관리자 - 리뷰글 목록 (POST) =====
		}else if (action.equals("/adminReviewAll")) {

				// 1. 년/월 검사 + 조회 기간 (setyear, setmonth, errorMsg 등은 메소드 안에서 setAttribute)
				String[] period = checkYearMonth(req);
				String startDate = period[0];
				String endDate   = period[1];

				String category = req.getParameter("category");
				

			    // 페이지 검사 : 숫자가 아니거나 1보다 작으면 1페이지
			    int pageSize = 15;
			    int page = 1;
			    String pageStr = req.getParameter("page");
			    if (pageStr != null && !pageStr.isEmpty()) {
			        try {
			            page = Integer.parseInt(pageStr);
			        } catch (NumberFormatException e) {
			            page = 1;
			        }
			    }
			    if (page < 1) page = 1;
			    

				List<AdminReviewDto> reviewList = new ArrayList<>();
				Map<String, Integer> categoryCount = new LinkedHashMap<>();
				int reviewTotal = 0;
			    int totalCount = 0;
			    int totalPages = 1;

				try {

					// 2. 카테고리별 글 수 (탭 목록 + 건수)
					categoryCount = reviewDao.reviewCategoryCount(startDate, endDate);
					
					for (int cnt : categoryCount.values()) {
						reviewTotal += cnt;
					}

					// 3. 카테고리 검사 : 이 달에 실제로 있는 카테고리만 허용, 나머지(빈 값 포함)는 전체
					if (category == null || !categoryCount.containsKey(category)) {
						category = "";
					}


			        // 3-1. 지금 보고 있는 목록의 전체 개수 (전체 or 선택한 카테고리)
			        totalCount = category.isEmpty() ? reviewTotal : categoryCount.get(category);

			        totalPages = (totalCount + pageSize - 1) / pageSize;
			        if (totalPages < 1) totalPages = 1;
			        if (page > totalPages) page = totalPages;   // 없는 페이지면 마지막 페이지로

			        // 4. 목록 : 이 페이지 15개만
			        int offset = (page - 1) * pageSize;
			        reviewList = reviewDao.reviewAllList(startDate, endDate, category, offset, pageSize);

					System.out.println("값 확인 page :"+page+" , category : "+category+" ,  totalCount : "+
							totalCount + " , totalPages : "+totalPages+" ,offset : "+offset);
					

				} catch (SQLException e) {
					e.printStackTrace();
					category = "";
					req.setAttribute("dbError", "리뷰 목록을 불러오는 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
				}

			    // 페이지 버튼 5개씩 묶기
			    int blockSize = 5;
			    int startPage = ((page - 1) / blockSize) * blockSize + 1;
			    int endPage = Math.min(startPage + blockSize - 1, totalPages);

			    // 5. 화면으로
			    req.setAttribute("totalCount", totalCount);    // ← 추가 (안내 줄 "총 n건")
			    req.setAttribute("page", page);                // ← 추가
			    req.setAttribute("totalPages", totalPages);    // ← 추가
			    req.setAttribute("startPage", startPage);      // ← 추가
			    req.setAttribute("endPage", endPage);          // ← 추가
			    
			    req.setAttribute("reviewList", reviewList);
				req.setAttribute("categoryCount", categoryCount);   // 탭 목록 + 건수
				req.setAttribute("reviewTotal", reviewTotal);       // 전체 건수
				req.setAttribute("selCategory", category);          // 지금 선택된 카테고리 ("" = 전체)
				req.setAttribute("tab", "review");
				req.setAttribute("listAction", "adminReviewAll");
//jsp에서 들어가는 부분 <form class="filter" action="${ctx}/Admin/${empty listAction ? 'admin_main' : listAction}" method="post">
				req.setAttribute("pageCss", "admin/admin_main.css");
				req.setAttribute("center", "admin/admin_main.jsp");
				req.getRequestDispatcher("/WEB-INF/main.jsp").forward(req, resp);
				return;
			
			
		}else if(action.equals("/adminGuideReservationAll")) {
			// 1. 년/월 검사 + 조회 기간 (setyear, setmonth, errorMsg 등은 메소드 안에서 setAttribute)
			String[] period = checkYearMonth(req);
			String startDate = period[0];
			String endDate   = period[1];
 
			// 2-1. 상태 검사 : PAID / PENDING / CANCELED 만 허용, 나머지(빈 값 포함)는 전체
			String status = req.getParameter("status");
			if (!"PAID".equals(status) && !"PENDING".equals(status) && !"CANCELED".equals(status)) {
				status = "";
			}
			System.out.println("[AdminController] /Admin/adminGuideReservationAll status ->  "+status); 
			//page :2 , statusCount : {ALL=19, CANCELED=1, PAID=2, PENDING=16} ,  totalCount : 19 , totalPages : 2 ,offset : 15
			
			
			// 2-2. 페이지 검사 : 숫자가 아니거나 1보다 작으면 1페이지
			int pageSize = 15;              // 한 페이지에 15개
			int page = 1;
			String pageStr = req.getParameter("page");
			if (pageStr != null && !pageStr.isEmpty()) {
			    try {
			        page = Integer.parseInt(pageStr);
			    } catch (NumberFormatException e) {
			        page = 1;
			    }
			}
			if (page < 1) page = 1;


 
			// 3. 조회
			List<AdminReservationDto> reservationList = new ArrayList<>();
			Map<String, Integer> statusCount = new HashMap<>();
			int totalCount = 0;
			int totalPages = 1;
			
			try {
				 // 3-1. 건수 먼저
				statusCount     = guideReservationDao.guideReservationStatusCount(startDate, endDate);
				 // 지금 보고 있는 목록의 전체 개수 (전체 or 선택한 상태)
			    String countKey = status.isEmpty() ? "ALL" : status;
			    totalCount = statusCount.getOrDefault(countKey, 0);

			    // 전체 페이지 수 (15개 → 1페이지, 16개 → 2페이지)
			    totalPages = (totalCount + pageSize - 1) / pageSize;
			    if (totalPages < 1) totalPages = 1;
			    if (page > totalPages) page = totalPages;   // 없는 페이지 요청이면 마지막 페이지로

			    // 3-2. 목록 : 이 페이지 15개만
			    int offset = (page - 1) * pageSize;
				System.out.println("값 확인 page :"+page+" , statusCount : "+statusCount+" ,  totalCount : "+
						totalCount + " , totalPages : "+totalPages+" ,offset : "+offset);

			    reservationList = guideReservationDao.adminGuideReservationAll(startDate, endDate, status, offset, pageSize);
				
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("[AdminController] /Admin/adminGuideReservationAll 예약조회시 예외발생 ");
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
 
			// 3-3. 아래 페이지 버튼을 5개씩 묶어서 보여주기 (1~5, 6~10 ...)
			int blockSize = 5;
			int startPage = ((page - 1) / blockSize) * blockSize + 1;
			int endPage = Math.min(startPage + blockSize - 1, totalPages);

			
			// 4. 화면으로
			//추가 페이지처리하면서 5개 추가됨
			req.setAttribute("page", page);
			req.setAttribute("totalPages", totalPages);
			req.setAttribute("startPage", startPage);
			req.setAttribute("endPage", endPage);
			req.setAttribute("totalCount", totalCount);
			
			req.setAttribute("reservationList", reservationList);
			req.setAttribute("statusCount", statusCount);
			req.setAttribute("selStatus", status);                       // 지금 선택된 상태 ("" = 전체) 클래스추가위해
			req.setAttribute("tab", "reservation");                      // 메뉴 강조
			req.setAttribute("listAction", "adminGuideReservationAll");  // 조회하기 버튼이 보낼 주소
			req.setAttribute("pageCss", "admin/admin_main.css");
			req.setAttribute("center", "admin/admin_main.jsp");
			req.getRequestDispatcher("/WEB-INF/main.jsp").forward(req, resp);
			return;
		}else if(action.equals("/memberAll")) {
			System.out.println("[AdminController] /Admin/memberAll 전체회원조회 ");
		    final int PAGE_SIZE  = 15;   // 한 페이지에 보여줄 회원 수
		    final int BLOCK_SIZE = 5;    // 아래 페이지 번호를 몇 개씩 보여줄지 (1~5, 6~10 ...)

		    // ---------- 1. 페이지 번호 읽기 (잘못된 값이면 1페이지) ----------
		    int page = 1;
		    String pageStr = req.getParameter("page");
		    if (pageStr != null && !pageStr.isEmpty()) {
		        try {
		            page = Integer.parseInt(pageStr);
		        } catch (NumberFormatException e) {
		            page = 1;
		        }
		    }
		    if (page < 1) page = 1;

		    List<RegisterDto> dtolist = new ArrayList<>();
		    int totalCount = 0;
		    int totalPages  = 1;
		    boolean dbError = false;

		    try {
		        RegisterDao dao = new RegisterDao();

		        // ---------- 2. 전체 개수 → 전체 페이지 수 ----------
		        totalCount = dao.countAllMembers();
		        totalPages  = (int) Math.ceil(totalCount / (double) PAGE_SIZE);
		        if (totalPages < 1) totalPages = 1;

		        // 마지막 페이지보다 큰 번호가 오면 마지막 페이지로
		        if (page > totalPages) page = totalPages;

		        // ---------- 3. 이 페이지의 회원만 가져오기 ----------
		        int offset = (page - 1) * PAGE_SIZE;
		        
		        dtolist = dao.allMemberPage(offset, PAGE_SIZE);

			    System.out.println("값 확인 page :"+page+",  totalCount : "+
						totalCount + " , totalPages : "+totalPages+" ,offset : "+offset);

		    } catch (IllegalStateException e) {
		        e.printStackTrace();
		        dbError = true;
		    }

		    // ---------- 4. 아래 페이지 번호 범위 (예: 7페이지면 6~10) ----------
		    int startPage = ((page - 1) / BLOCK_SIZE) * BLOCK_SIZE + 1;
		    int endPage   = Math.min(startPage + BLOCK_SIZE - 1, totalPages);

		    req.setAttribute("memberList", dtolist);
		    // 추가 — 오류가 났을 때만 문구를 담음 (JSP 기존 방식과 맞춤)
		    if (dbError) {
		        req.setAttribute("dbError", "회원 목록을 불러오는 중 오류가 발생했습니다.");
		    }
		    req.setAttribute("totalCount", totalCount);
		    req.setAttribute("page", page);
		    req.setAttribute("totalPages", totalPages);
		    req.setAttribute("startPage", startPage);
		    req.setAttribute("endPage", endPage);
		    req.setAttribute("pageSize", PAGE_SIZE);

		    req.setAttribute("tab", "memberAll");
		    req.setAttribute("pageCss", "admin/admin_main.css");
		    req.setAttribute("center", "admin/admin_main.jsp");
		    req.getRequestDispatcher("/WEB-INF/main.jsp").forward(req, resp);
		    return;
		}
		else { resp.sendError(HttpServletResponse.SC_NOT_FOUND); }
		
	}
	
	
	
	/**
	 * 년/월 파라미터 검사
	 * - 잘못된 값이면 이번 달로 바꾸고 errorMsg 를 남김
	 * - setyear, setmonth, errorMsg, minYear, maxYear 를 request 에 담음
	 * - 돌려주는 값 : [0] 시작일, [1] 끝일 (다음 달 1일)
	 */
	private String[] checkYearMonth(HttpServletRequest req) {

		// ---------- 1. 기본값 = 이번 달 ----------
		LocalDate today = LocalDate.now();
		int setyear  = today.getYear();
		int setmonth = today.getMonthValue();
		String errorMsg = null;

		// 년도 select 범위 (JSP 와 같이 사용)
		int minYear = 2026;
		int maxYear = 2027;

		String yearStr  = req.getParameter("year");
		String monthStr = req.getParameter("month");

		// ---------- 2. 년도 검사 ----------
		if (yearStr != null && !yearStr.isEmpty()) {
			try {
				int y = Integer.parseInt(yearStr);
				if (y >= minYear && y <= maxYear) {
					setyear = y;
				} else {
					errorMsg = "조회할 수 없는 년도입니다. 이번 달로 조회합니다.";
				}
			} catch (NumberFormatException e) {
				errorMsg = "년도 값이 올바르지 않습니다. 이번 달로 조회합니다.";
			}
		}

		// ---------- 3. 월 검사 ----------
		if (monthStr != null && !monthStr.isEmpty()) {
			try {
				int m = Integer.parseInt(monthStr);
				if (m >= 1 && m <= 12) {
					setmonth = m;
				} else {
					errorMsg = "월은 1~12 사이만 가능합니다. 이번 달로 조회합니다.";
				}
			} catch (NumberFormatException e) {
				errorMsg = "월 값이 올바르지 않습니다. 이번 달로 조회합니다.";
			}
		}

		// 하나라도 잘못됐으면 년/월 둘 다 이번 달로
		if (errorMsg != null) {
			setyear  = today.getYear();
			setmonth = today.getMonthValue();
		}

		// ---------- 4. 화면에 필요한 값 담기 ----------
		req.setAttribute("setyear", setyear);
		req.setAttribute("setmonth", setmonth);
		req.setAttribute("errorMsg", errorMsg);
		req.setAttribute("minYear", minYear);
		req.setAttribute("maxYear", maxYear);

		// ---------- 5. 조회 기간 만들어서 돌려주기 ----------
		String startDate = String.format("%04d-%02d-01 00:00:00", setyear, setmonth);
		int nextYear  = (setmonth == 12) ? setyear + 1 : setyear;
		int nextMonth = (setmonth == 12) ? 1 : setmonth + 1;
		String endDate = String.format("%04d-%02d-01 00:00:00", nextYear, nextMonth);

		return new String[] { startDate, endDate };
	}
	
	@Override
	// =========================================================
	// 주제: doGet 처리
	// 설명: GET 요청을 받아 필요한 데이터를 준비하고 화면 또는 응답으로 전달
	// =========================================================
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doHandle(req,resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doHandle(req,resp);
	}

	@Override
	// =========================================================
	// 주제: init 처리
	// 설명: init 기능에 필요한 처리 로직을 수행
	// =========================================================
	public void init() throws ServletException {
		super.init();
	}
	
	
	

}
