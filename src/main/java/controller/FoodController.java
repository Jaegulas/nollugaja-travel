package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import dto.FoodDto;
import dto.TourDto;
import service.FoodService;
import service.TourService;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import util.TourApiUtil;
import util.NaverLocalSearchUtil;

// =========================================================
// 주제: 파일 역할
// 설명: FoodController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


@WebServlet("/food/*")
public class FoodController extends HttpServlet{
    private static final long serialVersionUID=1L;
    private FoodService foodService=new FoodService();
    private TourService tourService=new TourService();

    // =========================================================
    // 주제: 음식 페이지 요청 처리
    // 설명: 첫 진입은 위치 선택 화면, lat/lng가 있을 때만 맛집/관광지 조회
    // =========================================================
    protected void doHandle(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
        req.setCharacterEncoding("UTF-8"); resp.setContentType("text/html; charset=UTF-8");
        
     // =========================================================
     // 주제: 요청 action 구분
     // 설명: 네이버 장소 링크 API와 관광지 상세 API를 먼저 처리
     // =========================================================
     String action=req.getPathInfo();
     
	   //=========================================================
	   //주제: 음식점 API 상세정보 조회
	   //설명: 선택한 음식점의 운영시간, 전화번호, 메뉴 반환
	   //=========================================================
	   if(action.equals("/foodintroapi")){
	    resp.setContentType("application/json; charset=UTF-8");
	    String contentId=req.getParameter("contentId");
	    JsonObject detail=contentId==null||contentId.trim().isEmpty()?null:TourApiUtil.getFoodIntroDetail(contentId);
	    resp.getWriter().write(detail==null?"{}":detail.toString());
	    return;
	   }

	   //=========================================================
	   //주제: 잘못된 음식 페이지 경로 보정
	   //설명: /food/list로 들어와도 중복 페이지가 생기지 않도록 /food/로 돌림
	   //=========================================================
	   if(action.equals("/list")){
	       resp.sendRedirect(req.getContextPath()+"/food/");
	       return;
	   }
     
	  // =========================================================
	  // 주제: 맛집/관광지 목록 JSON API
	  // 설명: 지도 클릭 시 페이지 새로고침 없이 fetch로 목록을 다시 받음
	  // =========================================================
	  if(action.equals("/listapi")){
	      resp.setContentType("application/json; charset=UTF-8");
	      double lat=Double.parseDouble(req.getParameter("lat"));
	      double lng=Double.parseDouble(req.getParameter("lng"));
	      String region=req.getParameter("region");
	      List<FoodDto> foodList=foodService.getNearbyFoods(lat,lng);
	      List<TourDto> tourList=tourService.getNearbyTours(lat,lng);
	      JsonObject root=new JsonObject();
	      root.addProperty("region",region==null||region.trim().isEmpty()?"지도 선택 위치":region);
	      root.add("foodList",toFoodJsonArray(foodList,req));
	      root.add("tourList",toTourJsonArray(tourList,req));
	      resp.getWriter().write(new Gson().toJson(root));
	      return;
	  }
     
	// =========================================================
	// 주제: 지역+키워드 맛집 검색 JSON API
	// 설명: 부산+국밥처럼 선택 지역 안에서 음식 키워드로 맛집을 다시 검색
	// =========================================================
	if(action.equals("/searchapi")){
	    resp.setContentType("application/json; charset=UTF-8");
	    String region=req.getParameter("region");
	    String keyword=req.getParameter("keyword");
	    double lat=Double.parseDouble(req.getParameter("lat"));
	    double lng=Double.parseDouble(req.getParameter("lng"));
	    JsonObject root=new JsonObject();
	    root.addProperty("region",(region==null||region.trim().isEmpty()?"선택 지역":region)+" · "+(keyword==null?"":keyword));
	    root.add("foodList",NaverLocalSearchUtil.searchFoodList(region,keyword,lat,lng));
	    root.add("tourList",new JsonArray());
	    resp.getWriter().write(new Gson().toJson(root));
	    return;
	}
	  
     // =========================================================
     // 주제: 네이버 실제 장소 링크 API
     // 설명: 장소명과 주소로 네이버 지역검색 후 실제 장소 link 반환
     // =========================================================
     if(action.equals("/naverplaceapi")){
         resp.setContentType("application/json; charset=UTF-8");
         String name=req.getParameter("name");
         String address=req.getParameter("address");
         String placeLink=NaverLocalSearchUtil.getPlaceLink(name,address);

         JsonObject obj=new JsonObject();
         obj.addProperty("url",placeLink==null?"":placeLink);
         resp.getWriter().write(new Gson().toJson(obj));
         return;
     }

     // =========================================================
     // 주제: 관광지 상세 설명 API
     // 설명: JS fetch 요청을 받아 TourAPI detailCommon2 결과를 JSON으로 반환
     // =========================================================
     if(action.equals("/detailapi")){
         resp.setContentType("application/json; charset=UTF-8");
         String contentId=req.getParameter("contentId");
         String contentTypeId=req.getParameter("contentTypeId");

         if(contentId==null||contentId.trim().isEmpty()){
             resp.getWriter().write("{}");
             return;
         }

         JsonObject detail=TourApiUtil.getTourCommonDetail(contentId,contentTypeId);
         resp.getWriter().write(detail==null?"{}":new Gson().toJson(detail));
         return;
     }
        
        String latParam=req.getParameter("lat"); String lngParam=req.getParameter("lng"); String region=req.getParameter("region");
        boolean hasLocation=latParam!=null&&!latParam.trim().isEmpty()&&lngParam!=null&&!lngParam.trim().isEmpty();
        List<FoodDto> foodList=new ArrayList<>(); List<TourDto> tourList=new ArrayList<>();

        // =========================================================
        // 주제: 좌표가 없는 첫 진입
        // 설명: 부산/서울 같은 임의 좌표를 강제로 보여주지 않고 위치 선택 화면만 표시
        // =========================================================
        if(!hasLocation){
            req.setAttribute("foodList",foodList); req.setAttribute("tourList",tourList); req.setAttribute("hasLocation",false); req.setAttribute("selectedRegion","위치 선택 전"); req.setAttribute("center","food/food.jsp");
            req.getRequestDispatcher("/WEB-INF/main.jsp").forward(req,resp); return;
        }

        // =========================================================
        // 주제: 좌표가 있는 요청
        // 설명: 내 위치/지역 버튼/지도 클릭으로 들어온 좌표 기준으로 목록 조회
        // =========================================================
        try{
            double latitude=Double.parseDouble(latParam); double longitude=Double.parseDouble(lngParam);
            foodList=foodService.getNearbyFoods(latitude,longitude); tourList=tourService.getNearbyTours(latitude,longitude);
            if(region==null||region.trim().isEmpty()) region="선택한 위치";
            System.out.println("[FoodController] 위치 기준 조회: "+region+" / lat="+latitude+" / lng="+longitude);
        }catch(Exception e){
            System.err.println("[FoodController] 좌표 조회 실패"); e.printStackTrace(); hasLocation=false; region="위치 선택 전";
        }

        // =========================================================
        // 주제: JSP 전달
        // 설명: 조회 결과가 있으면 food.jsp에서 1위 맛집을 자동 선택, 없으면 결과 없음 안내 표시
        // =========================================================
        req.setAttribute("foodList",foodList); req.setAttribute("tourList",tourList); req.setAttribute("hasLocation",hasLocation); req.setAttribute("selectedRegion",region); req.setAttribute("center","food/food.jsp");
        req.getRequestDispatcher("/WEB-INF/main.jsp").forward(req,resp);
    }

  //=========================================================
  //주제: 맛집 DTO JSON 변환
  //설명: 현재 food 테이블에 남은 컬럼만 JSON으로 전달
  //=========================================================
  private JsonArray toFoodJsonArray(List<FoodDto> list,HttpServletRequest req){
   JsonArray arr=new JsonArray();
   if(list==null)return arr;
   for(FoodDto f:list){
    JsonObject o=new JsonObject();
    
  //=========================================================
  //주제: 맛집 찜하기 ID 전달
  //설명: fetch 로 새로 생성하는 음식 카드에도 food_id 전달
  //=========================================================
  o.addProperty("foodId",f.getFood_id());
    
  //=========================================================
  //주제: 음식점 상세 API 연결
  //설명: fetch로 만든 카드에도 contentId 전달
  //=========================================================
  o.addProperty("contentId",f.getContent_id()==null?"":f.getContent_id());
    
    o.addProperty("name",f.getF_name()==null?"":f.getF_name());
    o.addProperty("category",f.getF_category()==null?"맛집":f.getF_category());
    o.addProperty("address",f.getAddress()==null?"":f.getAddress());
    o.addProperty("phone",f.getPhone()==null?"":f.getPhone());
    o.addProperty("image",f.getImage()==null?"":f.getImage());
    o.addProperty("distance",f.getDistance());
    o.addProperty("lat",f.getLatitude());
    o.addProperty("lng",f.getLongitude());
    o.addProperty("contentId",f.getContent_id()==null?"":f.getContent_id());
    arr.add(o);
   }
   return arr;
  }

	 // =========================================================
	 // 주제: 관광지 DTO JSON 변환
	 // 설명: JSP fetch에서 바로 쓰기 좋은 이름으로 변환
	 // =========================================================
	 private JsonArray toTourJsonArray(List<TourDto> list,HttpServletRequest req){
	     JsonArray arr=new JsonArray();
	     if(list==null)return arr;
	     for(TourDto t:list){
	         JsonObject o=new JsonObject();
	         o.addProperty("name",t.getTitle()==null?"":t.getTitle());
	         o.addProperty("category",t.getCategory()==null?"관광지":t.getCategory());
	         o.addProperty("address",t.getAddress()==null?"":t.getAddress());
	         o.addProperty("contentId",t.getContent_id()==null?"":t.getContent_id());
	         o.addProperty("contentTypeId",t.getContent_type_id()==null?"12":t.getContent_type_id());
	         o.addProperty("image",t.getImage()==null?"":t.getImage());
	         o.addProperty("distance",t.getDistance());
	         o.addProperty("lat",t.getLatitude());
	         o.addProperty("lng",t.getLongitude());
	         arr.add(o);
	     }
	     return arr;
	 }
    
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{doHandle(req,resp);}
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{doHandle(req,resp);}
}
