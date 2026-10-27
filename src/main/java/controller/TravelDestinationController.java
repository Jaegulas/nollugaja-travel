package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import service.TravelDestinationService;

// =========================================================
// 주제: 파일 역할
// 설명: TravelDestinationController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================



@WebServlet("/TravelDestination/*")
public class TravelDestinationController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private TravelDestinationService service =
            new TravelDestinationService();


    // =========================================================
    // 주제: doHandle 처리
    // 설명: doHandle 기능에 필요한 처리 로직을 수행
    // =========================================================
    private void doHandle(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        // 한글 처리
        req.setCharacterEncoding("UTF-8");

        // /TravelDestination 뒤의 주소
        String action = req.getPathInfo();

        System.out.println(
            "===== TravelDestinationController 실행 : "
            + action
            + " ====="
        );


        // =====================================================
        // 1. 여행지 메인 페이지
        //
        // 주소 :
        // /TravelDestination/list
        //
        // 화면 :
        // /WEB-INF/travelDestination/travelDestination.jsp
        // =====================================================
        if (action == null || "/list".equals(action)) {

            resp.setContentType(
                "text/html; charset=UTF-8"
            );


            /*
             * main.jsp에서
             *
             * <jsp:include page="${center}" />
             *
             * 로 여행지 JSP를 포함
             */
            req.setAttribute(
                "center",
                "travelDestination/travelDestination.jsp"
            );


            req.getRequestDispatcher(
                "/WEB-INF/main.jsp"
            ).forward(req, resp);


            return;
        }



        // =====================================================
        // 2. TourAPI 여행지 목록 조회
        //
        // 주소 :
        // /TravelDestination/api
        //
        // travelDestination.jsp의 fetch()에서 호출
        // =====================================================
        if ("/api".equals(action)) {

            String areaCode =
                    req.getParameter("areaCode");


            System.out.println(
                "요청받은 지역코드 : "
                + areaCode
            );


            String json =
                    service.getDestinationsFromApi(
                        areaCode
                    );


            resp.setContentType(
                "application/json; charset=UTF-8"
            );


            resp.getWriter().write(json);


            return;
        }



        // =====================================================
        // 3. TourAPI 여행지 검색
        //
        // 주소 :
        // /TravelDestination/searchApi?keyword=부산
        //
        // travelDestination.jsp 검색창에서 호출
        // =====================================================
        if ("/searchApi".equals(action)) {

            String keyword =
                    req.getParameter("keyword");


            System.out.println(
                "===== TourAPI 키워드 검색 요청 ====="
            );

            System.out.println(
                "검색어 : " + keyword
            );


            // 검색어 검사
            if (keyword == null
                    || keyword.trim().isEmpty()) {

                resp.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST
                );


                resp.setContentType(
                    "application/json; charset=UTF-8"
                );


                resp.getWriter().write(
                    "{"
                    + "\"success\":false,"
                    + "\"message\":\"검색어가 없습니다.\""
                    + "}"
                );


                return;
            }


            String json =
                    service.searchDestinations(
                        keyword.trim()
                    );


            resp.setContentType(
                "application/json; charset=UTF-8"
            );


            resp.getWriter().write(json);


            return;
        }



        // =====================================================
        // 4. 여행지 상세 페이지
        //
        // 주소 :
        // /TravelDestination/detail?contentId=126508
        //
        // 화면 :
        // /WEB-INF/travelDestination/
        // travelDestinationDetail.jsp
        // =====================================================
        if ("/detail".equals(action)) {

            String contentId =
                    req.getParameter("contentId");


            System.out.println(
                "===== 여행지 상세 페이지 요청 ====="
            );

            System.out.println(
                "contentId : " + contentId
            );


            // contentId 검사
            if (contentId == null
                    || contentId.trim().isEmpty()) {

                System.out.println(
                    "contentId가 없습니다."
                );


                resp.sendRedirect(
                    req.getContextPath()
                    + "/TravelDestination/list"
                );


                return;
            }


            // 상세 JSP에서 사용
            req.setAttribute(
                "contentId",
                contentId
            );


            // ★ 실제 상세 JSP로 이동
            req.getRequestDispatcher(
                "/WEB-INF/travelDestination/"
                + "travelDestinationDetail.jsp"
            ).forward(req, resp);


            return;
        }



        // =====================================================
        // 5. TourAPI 여행지 상세정보 조회
        //
        // 주소 :
        // /TravelDestination/detailApi?contentId=126508
        //
        // travelDestinationDetail.jsp의 fetch()에서 호출
        // =====================================================
        if ("/detailApi".equals(action)) {

            String contentId =
                    req.getParameter("contentId");


            System.out.println(
                "===== TourAPI 상세정보 요청 ====="
            );

            System.out.println(
                "contentId : " + contentId
            );


            // contentId 검사
            if (contentId == null
                    || contentId.trim().isEmpty()) {

                resp.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST
                );


                resp.setContentType(
                    "application/json; charset=UTF-8"
                );


                resp.getWriter().write(
                    "{"
                    + "\"success\":false,"
                    + "\"message\":\"contentId가 없습니다.\""
                    + "}"
                );


                return;
            }


            String json =
                    service.getDestinationDetail(
                        contentId
                    );


            resp.setContentType(
                "application/json; charset=UTF-8"
            );


            resp.getWriter().write(json);


            return;
        }



        // =====================================================
        // 6. 존재하지 않는 주소
        // =====================================================
        resp.sendError(
            HttpServletResponse.SC_NOT_FOUND
        );
    }



    // =========================================================
    // GET
    // =========================================================
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



    // =========================================================
    // POST
    // =========================================================
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

}
