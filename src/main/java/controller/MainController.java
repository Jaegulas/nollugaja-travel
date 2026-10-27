package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.CulLikeDao;
import dao.CulturalHeritageDao;
import dao.MainAccommodationImageDao;
import dao.ReviewDao;
import dao.MainAccommodationReviewDao;

import vo.ReviewVo;
import vo.CulturalHeritageVo;
import vo.AccommodationVo;

import service.AccommodationService;
import service.MainFoodService;

import dto.FoodDto;

import com.google.gson.JsonObject;
import util.TourApiUtil;





// =========================================================
// 주제: 파일 역할
// 설명: MainController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================



@WebServlet("/Main")
public class MainController extends HttpServlet {

	private static final long serialVersionUID = 1L;


	// =========================================================
	// 주제: doHandle 처리
	// 설명: doHandle 기능에 필요한 처리 로직을 수행
	// =========================================================
	private void doHandle(
			HttpServletRequest req,
			HttpServletResponse resp)
			throws ServletException, IOException {

		// ==========================================
		// 1. 기본 설정
		// ==========================================
		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html; charset=UTF-8");

		System.out.println(
				"[MainController] doHandle() main.jsp 이동"
		);


		// ==========================================
		// 2. 좋아요 목록을 담을 List
		//
		// 로그인하지 않은 경우에는
		// 빈 배열 [] 상태로 center.jsp에 전달된다.
		// ==========================================
		List<Integer> likedHeritageIds =
				new ArrayList<>();


		// ==========================================
		// 3. 현재 로그인 세션 확인
		// ==========================================
		HttpSession session =
				req.getSession(false);


		if (session != null &&
				session.getAttribute("loginId") != null) {

			// ======================================
			// 로그인한 회원 ID 가져오기
			// LoginController에서 저장한
			// session의 "loginId" 사용
			// ======================================
			String memberId =
					(String) session.getAttribute("loginId");

			System.out.println(
					"[MainController] 로그인 회원 ID : "
					+ memberId
			);


			// ======================================
			// 4. DB에서 좋아요 목록 조회
			// ======================================
			CulLikeDao dao =
					new CulLikeDao();

			try {

				likedHeritageIds =
						dao.getLikedHeritageIds(
								memberId
						);

				System.out.println(
						"[MainController] DB 좋아요 목록 : "
						+ likedHeritageIds
				);

			} catch (Exception e) {

				System.err.println(
						"[MainController] "
						+ "좋아요 초기 조회 실패"
				);

				e.printStackTrace();
			}

		} else {

			System.out.println(
					"[MainController] 비로그인 상태"
			);
		}


		// ==========================================
		// 5. 조회한 좋아요 목록을 JSP로 전달
		//
		// center.jsp에서
		// ${likedHeritageIds}
		// 로 사용할 수 있다.
		// ==========================================
		req.setAttribute(
				"likedHeritageIds",
				likedHeritageIds
		);

		System.out.println(
				"[MainController] JSP 전달 좋아요 목록 : "
				+ likedHeritageIds
		);

		// ==========================================
		// 6. 메인 화면 문화유산 4개 조회
		// ==========================================
		CulturalHeritageDao heritageDao =
		        new CulturalHeritageDao();

		List<CulturalHeritageVo> mainHeritageList =
		        new ArrayList<>();

		try {

		    // DB에서 첫 페이지 4개만 조회
		    mainHeritageList =
		            heritageDao.findPage(1, 4);

		    System.out.println(
		            "[MainController] 메인 문화유산 조회 : "
		            + mainHeritageList.size()
		            + "건"
		    );

		    // 어떤 문화유산이 조회됐는지 확인
		    for (CulturalHeritageVo heritage
		            : mainHeritageList) {

		        System.out.println(
		                "[MainController] 문화유산 : "
		                + heritage.getContentId()
		                + " / "
		                + heritage.getTitle()
		        );
		    }

		} catch (Exception e) {

		    System.err.println(
		            "[MainController] 메인 문화유산 조회 실패"
		    );

		    e.printStackTrace();
		}


		// ==========================================
		// 7. 문화유산 목록 JSP 전달
		// ==========================================
		req.setAttribute(
		        "mainHeritageList",
		        mainHeritageList
		);

		// ==========================================
		// 6. 기존 center.jsp 설정
		// ==========================================
		req.setAttribute(
				"center",
				"center.jsp"
		);
		

        // ==========================================
        // 8. 메인 페이지 최신 여행 리뷰 3개 조회
        // ==========================================
        ReviewDao reviewDao = new ReviewDao();

        List<ReviewVo> recentReviews = new ArrayList<>();

        try {

            recentReviews = reviewDao.getRecentReviews();

            System.out.println(
                "[MainController] 메인 여행 리뷰 조회 : "
                + recentReviews.size() + "건"
            );

        } catch (Exception e) {

            System.err.println(
                "[MainController] 메인 여행 리뷰 조회 실패"
            );

            e.printStackTrace();
        }

        // ==========================================
        // 9. 최신 리뷰 목록 JSP 전달
        // ==========================================
        req.setAttribute("recentReviews", recentReviews);
        

     // ==========================================
     // 10. 메인 화면 숙박 4개 조회
     // ==========================================

     List<AccommodationVo> mainAccommodationList =
             new ArrayList<>();

     // 로그인 회원 ID 확인
     String accommodationLoginId = null;

     if (session != null) {

         Object id = session.getAttribute("loginId");

         if (id != null) {
             accommodationLoginId = id.toString();
         }
     }

     try {

         // ======================================
         // 1. 기존 Service에서 숙소 4개 조회
         // ======================================
         AccommodationService accommodationService =
                 new AccommodationService();

         mainAccommodationList =
                 accommodationService.getMainAccommodationList(
                         accommodationLoginId
                 );

         System.out.println(
             "[MainController] 메인 숙박 조회 : "
             + mainAccommodationList.size()
             + "건"
         );

         // ======================================
         // 2. 숙소 ID 목록 추출
         // ======================================
         List<Integer> accommodationIds =
                 mainAccommodationList.stream()
                 .map(AccommodationVo::getAccommodation_id)
                 .collect(Collectors.toList());

         // ======================================
         // 3. 새 DAO에서 DB 사진 경로 직접 조회
         // ======================================
         if (!accommodationIds.isEmpty()) {

             try {

                 MainAccommodationImageDao imageDao =
                         new MainAccommodationImageDao();

                 Map<Integer, String> imagePaths =
                         imageDao.getImagePaths(
                                 accommodationIds
                         );

                 // ==================================
                 // 4. 숙소 ID에 맞는 사진 경로 연결
                 // ==================================
                 for (AccommodationVo vo
                         : mainAccommodationList) {

                     String picpath =
                             imagePaths.get(
                                 vo.getAccommodation_id()
                             );

                     if (picpath != null
                             && !picpath.trim().isEmpty()) {

                         vo.setPicpath(picpath);
                     }
                 }

             } catch (Exception imageException) {

                 // 사진 조회가 실패해도
                 // 숙소 목록과 좋아요 기능은 유지
                 System.err.println(
                     "[MainController] 숙소 사진 조회 실패"
                 );

                 imageException.printStackTrace();
             }
         }

         // ======================================
         // 5. 최종 숙소 정보 확인
         // ======================================
         for (AccommodationVo vo
                 : mainAccommodationList) {

             System.out.println(
                 "[MainController] 숙박 : "
                 + vo.getAccommodation_id()
                 + " / "
                 + vo.getName()
                 + " / 사진경로 : "
                 + vo.getPicpath()
             );
         }

     } catch (Exception e) {

         System.err.println(
             "[MainController] 메인 숙박 조회 실패"
         );

         e.printStackTrace();
     }


  // ================================================
  // 메인 숙소 카드: 실제 DB 리뷰 개수 및 내용 조회
  // ================================================

  Map<Integer, Integer> mainReviewCounts = new HashMap<>();
  Map<Integer, String> mainReviewSummaries = new HashMap<>();

  try {

      List<Integer> reviewAccommodationIds =
              new java.util.ArrayList<>();

      for (AccommodationVo stay : mainAccommodationList) {
          reviewAccommodationIds.add(stay.getAccommodation_id());
      }

      if (!reviewAccommodationIds.isEmpty()) {

    	  MainAccommodationReviewDao mainAccommodationReviewDao =
    		        new MainAccommodationReviewDao();

    		mainReviewCounts =
    		        mainAccommodationReviewDao.getReviewCounts(reviewAccommodationIds);

    		mainReviewSummaries =
    		        mainAccommodationReviewDao.getLatestReviewContents(reviewAccommodationIds);
    	}

  } catch (Exception e) {
      System.err.println("[MainController] 숙소 리뷰 조회 실패");
      e.printStackTrace();
  }

  // JSP에서 사용할 데이터 전달
  req.setAttribute("mainReviewCounts", mainReviewCounts);
  req.setAttribute("mainReviewSummaries", mainReviewSummaries);
     
     // ==========================================
     // 11. 숙박 목록 JSP 전달
     // ==========================================
     req.setAttribute(
         "mainAccommodationList",
         mainAccommodationList
     );

  // =========================================================
  // 주제: 메인 페이지 랜덤 음식점 4개 조회
  // 설명: TourAPI 음식점 후보 중 랜덤 4개를 가져옴
  // =========================================================

  List<FoodDto> mainFoodList = new ArrayList<>();

  try {

      MainFoodService mainFoodService =
              new MainFoodService();

      mainFoodList =
              mainFoodService.getRandomFoods();

      System.out.println(
          "[MainController] 메인 음식점 조회 : "
          + mainFoodList.size() + "건"
      );

      for (FoodDto food : mainFoodList) {

          System.out.println(
              "[MainController] 음식점 : "
              + food.getContent_id()
              + " / "
              + food.getF_name()
              + " / 사진 : "
              + food.getImage()
          );
      }

  } catch (Exception e) {

      System.err.println(
          "[MainController] 메인 음식점 조회 실패"
      );

      e.printStackTrace();
  }

//=========================================================
//주제: 메인 음식점 운영시간 및 대표메뉴 조회
//설명: 랜덤으로 선정된 4개 음식점만 상세 API 호출
//      값이 없으면 "연락문의"로 표시
//=========================================================

Map<String, String> mainFoodHours = new HashMap<>();
Map<String, String> mainFoodMenus = new HashMap<>();

for (FoodDto food : mainFoodList) {

   String contentId = food.getContent_id();

   String hours = "연락문의";
   String menu = "연락문의";

   if (contentId != null && !contentId.trim().isEmpty()) {

       try {
           JsonObject detail =
                   TourApiUtil.getFoodIntroDetail(contentId);

           if (detail != null) {

               if (detail.has("opentimefood")
                       && !detail.get("opentimefood").isJsonNull()) {

                   String value =
                           detail.get("opentimefood").getAsString().trim();

                   if (!value.isEmpty()) {

                	    // 운영시간에서 첫 번째 시작~종료 시간만 추출
                	    Pattern pattern = Pattern.compile(
                	        "(\\d{1,2}:\\d{2})\\s*[~〜\\-–]\\s*(\\d{1,2}:\\d{2})"
                	    );

                	    Matcher matcher = pattern.matcher(value);

                	    if (matcher.find()) {

                	        String startTime = matcher.group(1);
                	        String endTime = matcher.group(2);

                	        hours = startTime + "~" + endTime;
                	    }
                	}
               }

               if (detail.has("firstmenu")
                       && !detail.get("firstmenu").isJsonNull()) {

                   String value =
                           detail.get("firstmenu").getAsString().trim();

                   if (!value.isEmpty()) {
                       menu = value;
                   }
               }
           }

       } catch (Exception e) {
           System.err.println(
               "[MainController] 음식점 상세 조회 실패: " + contentId
           );
       }
   }

   mainFoodHours.put(contentId, hours);
   mainFoodMenus.put(contentId, menu);
}

//JSP로 전달
req.setAttribute("mainFoodHours", mainFoodHours);
req.setAttribute("mainFoodMenus", mainFoodMenus);
  
  // =========================================================
  // 주제: 랜덤 음식점 목록 JSP 전달
  // 설명: center.jsp에서 mainFoodList 사용 가능
  // =========================================================

  req.setAttribute("mainFoodList", mainFoodList);


		// ==========================================
		// 7. main.jsp로 이동
		// ==========================================
		req.getRequestDispatcher(
				"/WEB-INF/main.jsp"
		).forward(req, resp);
	}



	// ==========================================
	// GET
	// ==========================================
	@Override
	// =========================================================
	// 주제: doGet 처리
	// 설명: GET 요청을 받아 필요한 데이터를 준비하고 화면 또는 응답으로 전달
	// =========================================================
	protected void doGet(
			HttpServletRequest req,
			HttpServletResponse resp)
			throws ServletException, IOException {

		doHandle(req, resp);
	}



	// ==========================================
	// POST
	// ==========================================
	@Override
	// =========================================================
	// 주제: doPost 처리
	// 설명: POST 요청을 받아 입력값을 검증하고 저장/처리 결과를 응답
	// =========================================================
	protected void doPost(
			HttpServletRequest req,
			HttpServletResponse resp)
			throws ServletException, IOException {

		doHandle(req, resp);
	}



	// ==========================================
	// Servlet 최초 실행 시
	// ==========================================
	@Override
	// =========================================================
	// 주제: init 처리
	// 설명: init 기능에 필요한 처리 로직을 수행
	// =========================================================
	public void init()
			throws ServletException {

		System.out.println(
				"여기에 서비스클래스를 초기화시킵니다"
		);
	}
}
