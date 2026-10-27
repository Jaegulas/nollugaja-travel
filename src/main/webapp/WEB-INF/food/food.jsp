<%-- =========================================================
     주제: 파일 역할
     설명: food 영역의 food 화면을 구성하고 서버에서 전달받은 데이터를 표시
========================================================= --%>
<%@ page import="java.util.List" %>
<%@ page import="dto.FoodDto" %>
<%@ page import="dto.TourDto" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="config.AppConfigListener" %>

<%!// ======================================================
// 주제: HTML 속성 문자열 안전 처리
// 설명: API 문자열의 따옴표/특수문자로 onclick/data 속성이 깨지는 문제 방지
// =========================================================
private String attr(String text){
    if(text==null)return "";
    return text.replace("&","&amp;").replace("\"","&quot;").replace("<","&lt;").replace(">","&gt;").replace("\r"," ").replace("\n"," ");
	}

//=========================================================
//주제: 문자열 존재 여부 확인
//설명: null 또는 공백 문자열 체크
//=========================================================
private boolean hasText(String s){return s!=null&&!s.trim().isEmpty();}

//=========================================================
//주제: 키워드 포함 여부 확인
//설명: 카테고리/메뉴/음식명 안에 특정 단어가 있는지 확인
//=========================================================
private boolean containsAny(String text,String...keys){
  if(text==null)return false;
  String t=text.toLowerCase();
  for(String k:keys){if(k!=null&&t.contains(k.toLowerCase()))return true;}
  return false;
}

//=========================================================
//주제: 음식 fallback 이미지 파일명 선택
//설명: 음식명/카테고리/메뉴 기준으로 기본 이미지 파일명 반환
//=========================================================
private String getFoodFallbackFile(String category,String menu,String name){
  String text=((category==null?"":category)+" "+(menu==null?"":menu)+" "+(name==null?"":name)).toLowerCase();
  if(containsAny(text,"치킨","chicken","닭","통닭","후라이드","양념치킨","닭강정"))return "fallback_chicken.png";
  if(containsAny(text,"라멘","ramen","우동","국수","누들","noodle","냉면","밀면","쌀국수","칼국수","소바","면"))return "fallback_noodle.png";
  if(containsAny(text,"초밥","스시","sushi","사시미","회","롤"))return "fallback_sushi.png";
  if(containsAny(text,"햄버거","버거","burger","치즈버거"))return "fallback_burger.png";
  if(containsAny(text,"피자","pizza"))return "fallback_pizza.png";
  if(containsAny(text,"디저트","케이크","브런치","베이커리","마카롱","타르트"))return "fallback_dessert.png";
  if(containsAny(text,"카페","cafe","커피","라떼"))return "fallback_cafe.png";
  if(containsAny(text,"국밥","한식","백반","찌개","불고기","비빔밥","삼겹살","갈비","곰탕","설렁탕","돼지국밥","순대국","보쌈","족발"))return "fallback_korean.png";
  if(containsAny(text,"중식","짜장","짬뽕","탕수육","마라","딤섬","중국집"))return "fallback_chinese.png";
  if(containsAny(text,"양식","파스타","스테이크","리조또"))return "fallback_western.png";
  return "no-image.png";
}

//=========================================================
//주제: 음식 이미지 경로 생성
//설명: API/DB 이미지가 있으면 사용하고, 없으면 fallback 이미지 사용
//=========================================================
private String getFoodImagePath(javax.servlet.http.HttpServletRequest request,String image,String category,String menu,String name){
  if(hasText(image))return image;
  return request.getContextPath()+"/resources/images/food/"+getFoodFallbackFile(category,menu,name);
}

//=========================================================
//주제: 관광지 이미지 경로 생성
//설명: 관광지 API 이미지가 없으면 관광지 기본 이미지 사용
//=========================================================
private String getTourImagePath(javax.servlet.http.HttpServletRequest request,String image){
if(image!=null&&!image.trim().isEmpty())return image;
return request.getContextPath()+"/resources/images/food/fallback_tour.png";
}

%>



<%
// =========================================================
    // 주제: 맛집/관광지 목록 받기
    // 설명: FoodController에서 request.setAttribute로 보낸 foodList, tourList를 JSP에서 사용
    // =========================================================
    List<FoodDto> foodList=(List<dto.FoodDto>)request.getAttribute("foodList");
    int foodCount=(foodList!=null)?foodList.size():0;
    List<TourDto> tourList=(List<dto.TourDto>)request.getAttribute("tourList");
    int tourCount=(tourList!=null)?tourList.size():0;
    Boolean hasLocationObj=(Boolean)request.getAttribute("hasLocation");
    boolean hasLocation=hasLocationObj!=null&&hasLocationObj.booleanValue();
    String selectedRegion=(String)request.getAttribute("selectedRegion");
    if(selectedRegion==null||selectedRegion.trim().isEmpty()) selectedRegion="선택한 위치";
    
 // =========================================================
 // 주제: 네이버 지도 키 가져오기
 // 설명: config.properties의 지도용 Client ID를 JSP에서 사용
 // =========================================================
 String naverMapClientId=AppConfigListener.getProperty("naver.map.client-id");
 if(naverMapClientId==null)naverMapClientId="";
%>



<%-- =========================================================
주제: 위치 선택 첫 화면 전용 라이트 테마
설명: 지역 선택 후 지도/상세 화면과 팀 공통 top.jsp에는 적용되지 않음
========================================================= --%>
<style>
.food-page.food-intro-light{background:linear-gradient(180deg,#f7fbff 0%,#ecf8ff 68%,#f9fcff 100%)!important;color:#15283d!important;color-scheme:light;min-height:calc(100vh - 65px)}
.food-intro-light .food-hero-section{padding-top:24px!important;padding-bottom:20px!important}
.food-intro-light .food-hero-badge,.food-intro-light .food-hero-title,.food-intro-light .food-hero-subtitle{display:none!important}
.food-intro-light .food-hero-search{max-width:820px!important;margin-top:0!important}
.food-intro-light .food-hero-search-form{background:#fff!important;border:1px solid #e1edf4;box-shadow:0 8px 30px rgba(47,106,139,.10)!important;min-height:62px}
.food-intro-light .food-hero-search-form>span.material-symbols-outlined{color:#5f758a!important}
.food-intro-light #global-food-search{color:#122b41!important}
.food-intro-light #global-food-search::placeholder{color:#8494a6!important}
.food-intro-light .food-hero-search-form button[type=submit]{background:#008ba1!important;color:white!important}
.food-intro-light .food-hero-search-form button[type=submit]:hover{background:#007586!important}
.food-intro-wrap{position:relative;isolation:isolate;padding-top:18px!important}
.food-intro-wrap::before,.food-intro-wrap::after{content:"";position:absolute;top:130px;height:430px;width:315px;background-size:cover;background-repeat:no-repeat;background-position:center bottom;pointer-events:none;z-index:-1;opacity:.74}
.food-intro-wrap::before{left:-195px}
.food-intro-wrap::after{right:-195px}
.food-intro-card{position:relative;overflow:hidden;text-align:center;background:rgba(255,255,255,.96);border:1px solid #e1ecf5;border-radius:30px;padding:46px 42px 38px;box-shadow:0 18px 65px rgba(27,85,122,.10);max-width:1200px;margin:0 auto}
.food-intro-card::before{content:"";position:absolute;left:-115px;top:80px;width:290px;height:290px;border-radius:50%;background:radial-gradient(circle,#dff5fc 0%,transparent 73%);pointer-events:none}
.food-intro-card::after{content:"";position:absolute;right:-120px;bottom:60px;width:300px;height:250px;border-radius:50%;background:radial-gradient(circle,#e7f8ef 0%,transparent 73%);pointer-events:none}
.food-intro-icon{position:relative;z-index:1;display:flex;align-items:center;justify-content:center;width:80px;height:80px;margin:0 auto 22px;border-radius:50%;background:#e1f7fa;color:#0097ac}
.food-intro-icon .material-symbols-outlined{font-size:40px!important}
.food-intro-card h2{position:relative;z-index:1;font-size:clamp(26px,3vw,42px)!important;line-height:1.3;font-weight:800!important;letter-spacing:-.04em;color:#14253a!important}
.food-intro-card h2 em{font-style:normal;color:#008ba1}
.food-intro-description{position:relative;z-index:1;font-size:16px!important;line-height:1.8!important;color:#526579!important;max-width:720px;margin:18px auto 0}
.food-intro-actions{position:relative;z-index:1;display:flex;flex-wrap:wrap;justify-content:center;gap:12px;margin-top:29px}
.food-intro-btn{display:inline-flex;align-items:center;justify-content:center;gap:10px;min-height:56px;padding:0 24px;border-radius:16px;text-decoration:none!important;font-size:16px;font-weight:750;box-shadow:0 5px 15px rgba(23,76,100,.09);transition:transform .2s,background .2s,box-shadow .2s;cursor:pointer;border:1px solid #e1ebf2}
.food-intro-btn:hover{transform:translateY(-2px);box-shadow:0 10px 25px rgba(20,95,125,.13)}
.food-intro-btn .material-symbols-outlined{font-size:23px}
.food-intro-btn-primary{background:#008da1!important;border-color:#008da1;color:#fff!important}
.food-intro-btn-primary:hover{background:#007789!important}
.food-intro-btn-city{background:#fff!important;color:#213348!important;min-width:116px}
.food-intro-btn-city .material-symbols-outlined{color:#008ca2}
.food-intro-btn-city:hover{background:#edfaff!important}
.food-intro-tip{position:relative;z-index:1;display:flex;align-items:flex-start;gap:14px;max-width:790px;margin:34px auto 0;padding:20px 24px;border-radius:17px;text-align:left;background:#eaf7ff;border:1px solid #d6ebf8;color:#2e536d}
.food-intro-tip-icon{font-size:27px!important;color:#2a9ad5;flex-shrink:0;margin-top:1px}
.food-intro-tip strong{display:block;font-size:16px;font-weight:800;color:#087aab;margin-bottom:5px}
.food-intro-tip p{font-size:14px;line-height:1.75;margin:0;color:#4a657d}
@media(max-width:1000px){.food-intro-wrap::before,.food-intro-wrap::after{display:none}.food-intro-card{padding:40px 22px}}
@media(max-width:560px){.food-intro-light .food-hero-section{padding-top:20px!important}.food-intro-wrap{padding-top:8px!important}.food-intro-card{padding:34px 18px 25px;border-radius:22px}.food-intro-icon{width:65px;height:65px;margin-bottom:17px}.food-intro-card h2{font-size:26px!important}.food-intro-description{font-size:14px!important;margin-top:13px}.food-intro-break{display:none}.food-intro-actions{gap:9px;margin-top:22px}.food-intro-btn{padding:0 14px;min-height:50px;font-size:14px}.food-intro-btn-primary{width:100%}.food-intro-btn-city{flex:1;min-width:0}.food-intro-tip{padding:16px;gap:10px;margin-top:25px}.food-intro-tip p{font-size:13px}.food-intro-light .food-hero-search-form button[type=submit]{padding-left:14px!important;padding-right:14px!important}.food-intro-light #global-food-search{font-size:12px!important}}

/*=========================================================
주제: 음식 페이지 화면별 풍경 배경
설명: 첫 화면은 봄 한옥, 지도부터 상세정보까지는 선택한 부산 야경 사진이며 스크롤과 함께 이동
      기존 지도/찜하기/상세정보 스크립트 변경 없음
=========================================================*/
.food-page.food-intro-light{
 background:linear-gradient(rgba(242,248,252,.10),rgba(242,248,252,.16)),url('${pageContext.request.contextPath}/resources/images/food/food_intro_bg.png') center center / cover no-repeat scroll!important;
 min-height:calc(100vh - 65px);color-scheme:light;
}
.food-page.food-map-scene{
 background:linear-gradient(rgba(6,15,28,.07),rgba(6,15,28,.15)),url('${pageContext.request.contextPath}/resources/images/food/food_map_night_bg.png') center center / cover no-repeat scroll!important;
 min-height:calc(100vh - 65px);color-scheme:light;
}
/* 첫 화면: 풍경 위에 어두운 반투명 안내 카드 */
.food-intro-light .food-intro-wrap::before,.food-intro-light .food-intro-wrap::after{display:none!important}
.food-intro-light .food-intro-wrap{padding-top:28px!important}
.food-intro-light .food-intro-card{background:rgba(15,23,32,.80)!important;border:1px solid rgba(255,255,255,.22)!important;box-shadow:0 20px 60px rgba(9,22,35,.26)!important;backdrop-filter:blur(15px);-webkit-backdrop-filter:blur(15px)}
.food-intro-light .food-intro-card h2{color:#fff!important}
.food-intro-light .food-intro-card h2 em{color:#33d9e4!important}
.food-intro-light .food-intro-description{color:#e7f0f6!important}
.food-intro-light .food-intro-icon{background:rgba(0,153,174,.19)!important;color:#48d8e3!important}
.food-intro-light .food-intro-btn-city{background:rgba(20,30,40,.88)!important;border-color:rgba(255,255,255,.23)!important;color:#fff!important}
.food-intro-light .food-intro-btn-city:hover{background:rgba(29,48,62,.94)!important}
.food-intro-light .food-intro-btn-city .material-symbols-outlined{color:#38d4e2!important}
.food-intro-light .food-intro-tip{background:rgba(14,51,70,.87)!important;border-color:rgba(89,192,231,.45)!important;color:#e8f5fc!important}
.food-intro-light .food-intro-tip strong{color:#63d6f3!important}
.food-intro-light .food-intro-tip p{color:#e1ecf4!important}
.food-intro-light .food-intro-tip-icon{color:#58caff!important}
/*=========================================================
주제: 첫 화면과 통일한 지도·목록·상세정보 테마
설명: 남색 반투명 패널 + 흰색/연회색 글씨 + 청록색 포인트
      지도/찜하기/DB/검색 스크립트 및 풍경 스크롤 방식 유지
=========================================================*/
.food-map-scene{color:#f4f8fb!important;color-scheme:dark!important}
/* 지도 검색창: 첫 화면과 같은 깔끔한 검색창 */
.food-map-scene .food-hero-section{padding-top:27px!important;padding-bottom:22px!important}
.food-map-scene .food-hero-badge,.food-map-scene .food-hero-title,.food-map-scene .food-hero-subtitle{display:none!important}
.food-map-scene .food-hero-search{margin-top:0!important;max-width:820px!important}
.food-map-scene .food-hero-search-form{background:rgba(245,250,253,.95)!important;border:1px solid rgba(211,229,239,.8)!important;box-shadow:0 8px 26px rgba(6,24,38,.19)!important}
.food-map-scene #global-food-search{color:#1c3548!important}
.food-map-scene #global-food-search::placeholder{color:#63788b!important}
.food-map-scene .food-hero-search-form>span.material-symbols-outlined{color:#247a8f!important}
/* 지도 위 탐색 설명: 첫 화면 카드와 동일한 푸른 차콜 */
.food-map-scene .food-map-section-header{padding:16px 20px;border:1px solid rgba(169,208,222,.24);border-radius:22px;background:rgba(31,43,56,.94);box-shadow:0 12px 30px rgba(6,20,33,.2);backdrop-filter:blur(12px);-webkit-backdrop-filter:blur(12px)}
.food-map-scene .food-map-section-header h2{color:#fff!important}
.food-map-scene .food-map-section-header .text-on-surface-variant{color:#c3d2dc!important}
.food-map-scene .food-map-section-header .text-primary-container{color:#4ed6e5!important}
/* 지도 우측 선택 구역 및 드롭다운 */
.food-map-scene .food-map-sidebar-header{background:rgba(31,43,56,.96)!important;color:#f5fbff!important;border:1px solid rgba(169,208,222,.22)!important;box-shadow:0 10px 27px rgba(7,24,36,.2)!important}
.food-map-scene .food-map-sidebar-header #selected-region-text{color:#fff!important}
.food-map-scene .food-map-sidebar-header .text-on-surface-variant{color:#becdd8!important}
.food-map-scene .food-map-sidebar-header .text-primary{color:#4ad7e3!important}
.food-map-scene #btn-region-change{background:#3c5064!important;color:#f7fbfe!important;border:1px solid rgba(175,204,219,.22)}
.food-map-scene #btn-region-change:hover{background:#47627a!important}
.food-map-scene #region-menu{background:#27394a!important;border:1px solid #527081!important;box-shadow:0 14px 35px rgba(8,20,30,.35)!important}
.food-map-scene #region-menu button{color:#eff7fa!important}
.food-map-scene #region-menu button:hover{background:#36566a!important}
/* 맛집 / 관광지 탭 */
.food-map-scene .food-map-venue-tabs{background:rgba(34,47,62,.96)!important;border:1px solid rgba(141,187,202,.23)!important}
.food-map-scene .food-map-venue-tabs button:not(.bg-primary){color:#c4d3dd!important}
.food-map-scene .food-map-venue-tabs button:not(.bg-primary):hover{background:#344b5d!important;color:#fff!important}
.food-map-scene .food-map-venue-tabs button.bg-primary{background:#008da1!important;color:#fff!important;box-shadow:0 4px 12px rgba(0,110,132,.30)!important}
/* 목록: 첫 화면 어두운 유리 카드 느낌 */
.food-map-scene #list-food-container .venue-card,.food-map-scene #list-tour-container .venue-card{background:rgba(37,50,65,.96)!important;border:1px solid rgba(156,201,219,.2)!important;box-shadow:0 7px 21px rgba(8,24,36,.18)!important;transition:background .18s,border-color .18s,box-shadow .18s,transform .18s}
.food-map-scene #list-food-container .venue-card:hover,.food-map-scene #list-tour-container .venue-card:hover{background:rgba(46,67,80,.98)!important;border-color:rgba(89,211,220,.6)!important;box-shadow:0 10px 25px rgba(6,40,57,.27)!important;transform:translateY(-1px)}
.food-map-scene #list-food-container .venue-card h3,.food-map-scene #list-tour-container .venue-card h3{color:#fff!important}
.food-map-scene #list-food-container .venue-card .text-on-surface-variant,.food-map-scene #list-tour-container .venue-card .text-on-surface-variant{color:#c2d0dc!important}
.food-map-scene #list-food-container .venue-card .bg-surface-container,.food-map-scene #list-tour-container .venue-card .bg-secondary-fixed{background:rgba(22,71,87,.9)!important;color:#a6edee!important}
.food-map-scene #list-food-container .venue-card .bg-on-surface\/75,.food-map-scene #list-tour-container .venue-card .bg-on-surface\/75{background:rgba(12,24,37,.85)!important;color:#fff!important}
.food-map-scene #list-food-container .venue-card .text-secondary-container,.food-map-scene #list-tour-container .venue-card .text-secondary-container{color:#ff776b!important}
.food-map-scene #list-tour-container + div{background:rgba(24,45,61,.93)!important;border:1px solid rgba(102,193,217,.36)!important;color:#d5e7ef!important}
.food-map-scene #list-tour-container + div .text-primary{color:#50d6e8!important}
/* 상세정보: 첫 화면과 동일한 남색 카드, 내부는 조금 밝은 남색 */
.food-map-scene #selected-detail-section>div{background:rgba(31,43,56,.97)!important;color:#f3f8fc!important;border:1px solid rgba(160,205,224,.26)!important;box-shadow:0 18px 48px rgba(7,25,38,.27)!important;backdrop-filter:blur(12px);-webkit-backdrop-filter:blur(12px)}
.food-map-scene #selected-detail-section .text-on-surface{color:#f6fbff!important}
.food-map-scene #selected-detail-section .text-on-surface-variant{color:#c3d1dc!important}
.food-map-scene #selected-detail-section .text-outline{color:#a6bfce!important}
.food-map-scene #selected-detail-section .text-primary{color:#54d5e2!important}
.food-map-scene #selected-detail-section .bg-surface-container-low{background:rgba(45,61,79,.97)!important}
.food-map-scene #selected-detail-section .bg-surface-container{background:#365367!important;color:#d9eff5!important}
.food-map-scene #selected-detail-section .bg-surface-container-lowest{background:#243a4b!important}
.food-map-scene #selected-detail-section .border-surface-container{border-color:rgba(151,188,204,.27)!important}
.food-map-scene #selected-detail-section #detail-title{color:#fff!important}
.food-map-scene #selected-detail-section #detail-subtitle{color:#c4d8e2!important}
.food-map-scene #selected-detail-section #detail-address,.food-map-scene #selected-detail-section #detail-phone,.food-map-scene #selected-detail-section #detail-hours,.food-map-scene #selected-detail-section #detail-menu{color:#f7fcff!important}
.food-map-scene #selected-detail-section #detail-hours-note,.food-map-scene #selected-detail-section #detail-phone-note,.food-map-scene #selected-detail-section #detail-menu-note{color:#b7ccda!important}
/* 하단 설명: 지나치게 밝지 않은 청록 카드 */
.food-map-scene #selected-detail-section .border-l-4{background:rgba(18,55,72,.96)!important;border-left-color:#42cdda!important;box-shadow:none!important}
.food-map-scene #selected-detail-section #detail-comment{color:#e7f5fa!important;line-height:1.85!important}
/* 버튼/태그: 청록 강조, 찜 완료는 코랄 유지 */
.food-map-scene #selected-detail-section .bg-primary.text-on-primary{background:#008ea2!important;color:#fff!important}
.food-map-scene #selected-detail-section .bg-secondary-fixed{background:#354e61!important;color:#d8e7f3!important}
.food-map-scene #btn-detail-save[data-saved="true"]{background:#b63327!important;color:#fff!important}
.food-map-scene #btn-detail-save[data-saved="false"]{background:#365064!important;color:#fff!important}
.food-map-scene #btn-naver-review{background:#00889c!important;color:#fff!important}
.food-map-scene #detail-main-label,.food-map-scene #detail-main-menu{color:#fff!important;text-shadow:0 1px 4px rgba(0,0,0,.5)}
@media(max-width:767px){.food-map-scene .food-map-section-header{padding:14px}.food-map-scene #selected-detail-section>div{background:rgba(31,43,56,.98)!important}}
/* =========================================================
주제: 지도 마커 hover 및 선택 표시
설명: 원에 커서를 올리면 커지고 이름이 나타남 (지도에서만 표시)
========================================================= */
.food-place-map-marker{--marker-color:#087f82;position:relative;display:flex;align-items:center;justify-content:center;width:26px;height:26px;cursor:pointer;overflow:visible}
.food-place-map-marker__dot{display:block;width:17px;height:17px;border:2px solid #fff;border-radius:50%;background:var(--marker-color);box-shadow:0 2px 7px rgba(10,33,49,.38);transition:transform .18s ease,box-shadow .18s ease;pointer-events:none}
.food-place-map-marker__name{position:absolute;left:50%;bottom:33px;max-width:260px;padding:7px 12px;border-radius:999px;background:var(--marker-color);color:#fff;font-size:12px;font-weight:750;line-height:1.35;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;box-shadow:0 4px 14px rgba(7,32,45,.32);opacity:0;visibility:hidden;transform:translate(-50%,5px);transition:opacity .15s ease,transform .15s ease,visibility .15s;pointer-events:none}
.food-place-map-marker__name::after{content:"";position:absolute;left:50%;bottom:-4px;width:8px;height:8px;background:var(--marker-color);transform:translateX(-50%) rotate(45deg)}
.food-place-map-marker:hover .food-place-map-marker__dot{transform:scale(1.55);box-shadow:0 0 0 5px rgba(255,255,255,.24),0 4px 15px rgba(10,33,49,.48)}
.food-place-map-marker:hover .food-place-map-marker__name,.food-place-map-marker.is-selected .food-place-map-marker__name{opacity:1;visibility:visible;transform:translate(-50%,0)}
.food-place-map-marker.is-selected .food-place-map-marker__dot{transform:scale(1.32);box-shadow:0 0 0 4px rgba(255,255,255,.18),0 4px 13px rgba(10,33,49,.4)}
.food-place-map-marker.is-selected:hover .food-place-map-marker__dot{transform:scale(1.65)}

/* =========================================================
주제: 검색창 안쪽 배경 강조
설명: 지역 직접 검색 클릭 시 입력창 배경이 청록색으로 세 번 깜빡임
========================================================= */
.food-hero-search-form.food-search-highlight #global-food-search{
 animation:foodSearchInputFlash .65s ease-in-out 3!important;
 border-radius:9999px;
}
@keyframes foodSearchInputFlash{
 0%,100%{background-color:transparent}
 40%,60%{background-color:rgba(38,205,216,.48)}
}
@media(prefers-reduced-motion:reduce){
 .food-hero-search-form.food-search-highlight #global-food-search{animation:none!important;background-color:rgba(38,205,216,.25)!important}
}

/* =========================================================
주제: 상세정보 스크롤 정렬
설명: 상단 메뉴 아래 여백을 남겨 야경과 상세정보가 함께 보이게 설정
========================================================= */
.food-map-scene #selected-detail-section{scroll-margin-top:165px}
@media(max-width:767px){.food-map-scene #selected-detail-section{scroll-margin-top:95px}}
</style>

<div class="food-page flex flex-col w-full bg-background text-on-surface<%=hasLocation?" food-map-scene":" food-intro-light"%>">
<!-- Top Background Ambient Glow -->
<div class="relative w-full overflow-hidden">

<div class="absolute -top-40 left-1/2 -translate-x-1/2 w-[900px] h-[350px] bg-gradient-to-b from-primary-fixed/20 via-surface-container-low to-transparent rounded-full blur-3xl pointer-events-none -z-10"></div>

<!-- 1. COMPACT HERO SECTION -->
<section class="food-hero-section max-w-[1200px] mx-auto px-margin-mobile md:px-margin pt-12 pb-8 md:pt-16 md:pb-12 w-full">
<div class="flex flex-col items-center text-center space-y-4 max-w-4xl mx-auto">

<!-- Label Badge -->
<span class="food-hero-badge inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-primary-container/15 text-primary font-label-md text-label-md tracking-wider uppercase font-semibold">
<span class="w-1.5 h-1.5 rounded-full bg-primary animate-pulse"></span>
FOOD &amp; DESTINATIONS
</span>

<!-- Headline & Subtitle -->
<h1 class="food-hero-title font-headline-lg text-headline-lg md:text-display-lg text-on-surface tracking-tight font-extrabold">어디에서 무엇을 먹을까요?</h1>
<p class="food-hero-subtitle font-body-lg text-body-lg text-on-surface-variant max-w-3xl md:whitespace-nowrap">여행하는 곳에서 진짜 현지인이 찾는 미식과 주변의 즐길거리를 한눈에 찾아보세요.</p>

<!-- Search Bar -->
<div class="food-hero-search w-full max-w-2xl mt-4">

<form class="food-hero-search-form relative flex items-center w-full bg-surface-container-lowest rounded-full shadow-[0_4px_24px\_-4px_rgba(0,104,95,0.08)] transition-all focus-within:shadow-[0_4px_24px_0_rgba(0,104,95,0.18)]" onsubmit="searchFoodLocation();return false;">

<span class="material-symbols-outlined text-outline pl-5 pr-2 select-none text-[22px]">search</span>

<input class="w-full py-4 pr-32 bg-transparent text-on-surface placeholder:text-outline font-body-md text-body-md focus:outline-none" id="global-food-search" placeholder="지역, 음식점 이름 또는 메뉴를 검색해보세요 (예: 부산 밀면, 초량 국밥)" type="text"/>

<button class="absolute right-1.5 px-6 py-2.5 rounded-full bg-primary text-on-primary font-label-lg text-label-lg font-semibold hover:bg-primary-container hover:shadow-md transition-all flex items-center gap-1.5 active:scale-95" type="submit">

<span>검색</span>

<span class="material-symbols-outlined text-[18px]">arrow_forward</span>

</button>

</form>

</div>



</div>

</section>

<%
if(!hasLocation){
%>
<!-- =========================================================
     주제: 첫 진입 위치 선택 화면 (라이트 테마)
     설명: 위치가 선택되기 전만 표시, 지도/검색/찜 기능 영향 없음
========================================================= -->
<section class="food-intro-wrap max-w-[1200px] mx-auto px-4 md:px-8 pb-16 w-full">
 <div class="food-intro-card">
  <div class="food-intro-icon"><span class="material-symbols-outlined">travel_explore</span></div>
  <h2>어디 주변 <em>맛집</em>을 찾아볼까요?</h2>
  <p class="food-intro-description">원하는 지역을 직접 검색하거나 서울·부산·제주 중에서 선택해 보세요.<span class="food-intro-break"> 선택한 지역의 주변 맛집과 관광지를 지도에서 확인할 수 있어요.</span></p>
  <div class="food-intro-actions">
   <button type="button" id="introRegionSearchBtn" class="food-intro-btn food-intro-btn-primary"><span class="material-symbols-outlined">search</span>지역 직접 검색</button>
   <a class="food-intro-btn food-intro-btn-city" href="${pageContext.request.contextPath}/food/?lat=37.5665&amp;lng=126.9780&amp;region=서울"><span class="material-symbols-outlined">location_city</span>서울</a>
   <a class="food-intro-btn food-intro-btn-city" href="${pageContext.request.contextPath}/food/?lat=35.1150&amp;lng=129.0420&amp;region=부산"><span class="material-symbols-outlined">directions_boat</span>부산</a>
   <a class="food-intro-btn food-intro-btn-city" href="${pageContext.request.contextPath}/food/?lat=33.4996&amp;lng=126.5312&amp;region=제주"><span class="material-symbols-outlined">landscape</span>제주</a>
  </div>
  <div class="food-intro-tip">
   <span class="material-symbols-outlined food-intro-tip-icon">info</span>
   <div>
    <strong>지도로 더 자세히 찾아보세요</strong>
    <p>지역을 선택한 뒤 지도에서 원하는 위치를 클릭해 보세요. 그 주변의 맛집과 관광지를 다시 찾고, 첫 번째 장소의 상세정보를 바로 보여드려요.</p>
   </div>
  </div>
 </div>
</section>
<%
}else{
%>

<!-- 2. MAIN FUNCTIONAL SECTION (MAP + LIST SPLIT) -->

<section class="max-w-[1200px] mx-auto px-margin-mobile md:px-margin pb-10 w-full">

<!-- Section Header -->

<div class="food-map-section-header flex flex-col sm:flex-row sm:items-end justify-between mb-5 gap-2">

<div>

<!--=========================================================
주제: 지도 탐색 안내 디자인
설명: 제목, 설명, 사용방법을 세로로 정렬하여 가독성 개선
=========================================================-->
<div class="flex flex-col min-w-0 gap-1.5">
 <h2 class="flex items-center gap-2 text-[19px] font-bold text-on-surface leading-7">
  <span class="material-symbols-outlined text-primary-container text-[21px]">explore</span>지도에서 맛집을 찾아보세요
 </h2>
 <p class="text-on-surface-variant text-[13px] leading-5">원하는 위치를 클릭하면 주변 맛집과 관광지가 표시돼요.</p>
 <p class="flex items-center gap-1 text-primary-container text-[11px] font-medium leading-4">
  <span class="material-symbols-outlined text-[14px]">touch_app</span>지도 클릭: 주변 장소 검색 · 목록 클릭: 아래 정보 갱신 · 마커 클릭: 상세정보로 이동
 </p>
</div>

</div>

<button type="button" id="nearbyFoodBtn" class="group relative overflow-hidden px-5 py-3.5 rounded-2xl bg-primary text-on-primary shadow-[0_10px_30px_-10px_rgba(0,184,169,0.65)] hover:shadow-[0_14px_34px_-8px_rgba(0,184,169,0.85)] active:scale-95 transition-all flex items-center gap-3 lg:-translate-x-[100px]">
<span class="absolute inset-0 bg-gradient-to-r from-white/0 via-white/20 to-white/0 translate-x-[-120%] group-hover:translate-x-[120%] transition-transform duration-700"></span>
<span class="relative flex items-center justify-center w-9 h-9 rounded-full bg-on-primary/15">
<span class="material-symbols-outlined text-[22px]">my_location</span>
</span>
<span class="relative flex flex-col items-start leading-tight">
<span class="font-label-sm text-label-sm opacity-85">지도 중심 기준</span>
<span class="font-label-lg text-label-lg font-extrabold">현재 보고 있는 지도로 찾기</span>
</span>
</button>

<div class="flex items-center gap-2 self-start sm:self-auto text-on-surface-variant font-label-md text-label-md">

<span class="w-2 h-2 rounded-full bg-primary"></span>

<span>실시간 영업 데이터 동기화됨</span>

</div>

</div>

<!-- 60/40 Split Container -->

<div class="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">

<!-- LEFT COLUMN: Custom Interactive Styled Vector Map (60% \~ 7 cols) -->

<div class="lg:col-span-7 flex flex-col bg-surface-container-lowest rounded-2xl shadow-sm overflow-hidden relative">

<%-- =========================================================
     주제: 실제 네이버 지도 영역
     설명: SVG 가짜 지도 대신 네이버 지도 API가 표시될 영역
========================================================= --%>
<div class="relative w-full h-[520px] md:h-[560px] bg-surface-container overflow-hidden select-none rounded-2xl" id="korean-vector-map">
<div id="naver-map" class="w-full h-full"></div>
<div class="absolute top-4 left-4 z-20 flex items-center gap-2 bg-surface-container-lowest/95 backdrop-blur-md px-3.5 py-1.5 rounded-full shadow-sm text-on-surface pointer-events-none">
<span class="w-2.5 h-2.5 rounded-full bg-secondary-container animate-ping"></span>
<span id="map-region-badge" class="font-label-md text-label-md font-bold">📍 탐색 중: <%=selectedRegion%></span>
</div>
</div>

</div>

<!-- RIGHT COLUMN: Location Header, Switchable Tab & Compact Venue Cards (40% \~ 5 cols) -->

<div class="lg:col-span-5 flex flex-col space-y-3.5">

<!-- Location Header & Quick Change -->

<div class="food-map-sidebar-header relative flex items-center justify-between bg-surface-container-lowest px-4 py-3 rounded-2xl shadow-sm">

<div class="flex items-center gap-2">

<span class="material-symbols-outlined text-primary text-[22px]">near_me</span>

<div>

<span class="font-label-sm text-label-sm text-on-surface-variant uppercase tracking-wider block">선택 구역</span>

<span id="selected-region-text" class="font-headline-sm text-headline-sm font-bold text-on-surface"><%=selectedRegion%></span>

</div>

</div>

<div class="flex items-center gap-2">
<p id="food-loading-msg" class="hidden text-red-400 font-label-sm text-label-sm font-bold animate-pulse">조회중입니다. 잠시 기다려주세요...</p>
<button id="btn-region-change" class="px-3 py-1.5 rounded-lg bg-surface-container hover:bg-surface-container-high text-on-surface font-label-md text-label-md font-semibold transition-colors flex items-center gap-1" onclick="toggleRegionMenu(event)" type="button">
<span>지역 변경</span>
<span class="material-symbols-outlined text-[16px]">expand_more</span>
</button>
</div>

<div id="region-menu" class="hidden absolute right-4 top-[58px] z-50 w-48 max-h-[360px] overflow-y-auto rounded-2xl bg-surface-container-lowest shadow-xl border border-surface-container">
<button type="button" class="w-full px-4 py-3 text-left hover:bg-surface-container font-label-md text-label-md text-on-surface" onclick="selectRegionQuick(37.5665,126.9780,'서울')">서울</button>
<button type="button" class="w-full px-4 py-3 text-left hover:bg-surface-container font-label-md text-label-md text-on-surface" onclick="selectRegionQuick(35.1796,129.0756,'부산')">부산</button>
<button type="button" class="w-full px-4 py-3 text-left hover:bg-surface-container font-label-md text-label-md text-on-surface" onclick="selectRegionQuick(35.8714,128.6014,'대구')">대구</button>
<button type="button" class="w-full px-4 py-3 text-left hover:bg-surface-container font-label-md text-label-md text-on-surface" onclick="selectRegionQuick(37.4563,126.7052,'인천')">인천</button>
<button type="button" class="w-full px-4 py-3 text-left hover:bg-surface-container font-label-md text-label-md text-on-surface" onclick="selectRegionQuick(35.1595,126.8526,'광주')">광주</button>
<button type="button" class="w-full px-4 py-3 text-left hover:bg-surface-container font-label-md text-label-md text-on-surface" onclick="selectRegionQuick(36.3504,127.3845,'대전')">대전</button>
<button type="button" class="w-full px-4 py-3 text-left hover:bg-surface-container font-label-md text-label-md text-on-surface" onclick="selectRegionQuick(35.5384,129.3114,'울산')">울산</button>
<button type="button" class="w-full px-4 py-3 text-left hover:bg-surface-container font-label-md text-label-md text-on-surface" onclick="selectRegionQuick(36.4800,127.2890,'세종')">세종</button>
<button type="button" class="w-full px-4 py-3 text-left hover:bg-surface-container font-label-md text-label-md text-on-surface" onclick="selectRegionQuick(37.2636,127.0286,'경기')">경기</button>
<button type="button" class="w-full px-4 py-3 text-left hover:bg-surface-container font-label-md text-label-md text-on-surface" onclick="selectRegionQuick(37.8813,127.7298,'강원')">강원</button>
<button type="button" class="w-full px-4 py-3 text-left hover:bg-surface-container font-label-md text-label-md text-on-surface" onclick="selectRegionQuick(36.6357,127.4917,'충북')">충북</button>
<button type="button" class="w-full px-4 py-3 text-left hover:bg-surface-container font-label-md text-label-md text-on-surface" onclick="selectRegionQuick(36.6588,126.6728,'충남')">충남</button>
<button type="button" class="w-full px-4 py-3 text-left hover:bg-surface-container font-label-md text-label-md text-on-surface" onclick="selectRegionQuick(35.8242,127.1480,'전북')">전북</button>
<button type="button" class="w-full px-4 py-3 text-left hover:bg-surface-container font-label-md text-label-md text-on-surface" onclick="selectRegionQuick(34.8118,126.3922,'전남')">전남</button>
<button type="button" class="w-full px-4 py-3 text-left hover:bg-surface-container font-label-md text-label-md text-on-surface" onclick="selectRegionQuick(36.5760,128.5056,'경북')">경북</button>
<button type="button" class="w-full px-4 py-3 text-left hover:bg-surface-container font-label-md text-label-md text-on-surface" onclick="selectRegionQuick(35.2383,128.6924,'경남')">경남</button>
<button type="button" class="w-full px-4 py-3 text-left hover:bg-surface-container font-label-md text-label-md text-on-surface" onclick="selectRegionQuick(33.4996,126.5312,'제주')">제주</button>
</div>

</div>

<!-- Switchable Tab Pills -->

<div class="food-map-venue-tabs grid grid-cols-2 p-1 bg-surface-container rounded-xl gap-1">

<button class="flex items-center justify-center gap-2 py-2.5 rounded-lg font-label-lg text-label-lg font-bold transition-all bg-primary text-on-primary shadow-sm" id="tab-food" onclick="switchTab('food')" type="button">

<span class="material-symbols-outlined text-[18px]">restaurant</span>

<span>맛집 (<%=foodCount%>)</span>

</button>

<button class="flex items-center justify-center gap-2 py-2.5 rounded-lg font-label-lg text-label-lg font-semibold transition-all text-on-surface-variant hover:text-on-surface hover:bg-surface-container-lowest/60" id="tab-tour" onclick="switchTab('tour')" type="button">

<span class="material-symbols-outlined text-[18px]">attractions</span>

<span>관광지 (<%=tourCount%>)</span>

</button>

</div>

<!-- Active Tab List: RESTAURANTS (Scrollable Container) -->

<div class="flex flex-col space-y-2.5 max-h-[460px] overflow-y-auto pr-1" id="list-food-container">



<%
if (foodList != null && !foodList.isEmpty()) {
        int rank = 1;

        for (FoodDto food : foodList) {
%>

<%
 //=========================================================
 //주제: 음식 카드 상세 데이터
 //설명: DB 기본 정보, 이미지 fallback, 거리 표시용 변수 생성
 //=========================================================
 String detailName=food.getF_name()!=null?food.getF_name():"";
 String detailAddress=food.getAddress()!=null?food.getAddress():"";
 String detailPhone=food.getPhone()!=null?food.getPhone():"";
 String cardImage=getFoodImagePath(request,food.getImage(),food.getF_category(),"",food.getF_name());
 String detailImage=cardImage;
 String fallbackImage=request.getContextPath()+"/resources/images/food/"+getFoodFallbackFile(food.getF_category(),"",food.getF_name());
 String foodCardId=rank==1?"first-food-card":"food-card-"+rank;

 //=========================================================
 //주제: 음식 카드 출력 변수
 //설명: 이름, 주소, 카테고리, 현재 위치 기준 거리 표시
 //=========================================================
 String foodName=detailName;
 String foodAddress=detailAddress;
 String foodCategory=food.getF_category()!=null&&!food.getF_category().trim().isEmpty()?food.getF_category():"맛집";
 // TourAPI 위치기반 응답의 dist는 m(미터) 단위입니다.
  // food.jsp의 거리 출력 함수는 km(킬로미터)를 기준으로 처리하므로 1000으로 나눕니다.
  double foodDistance=food.getDistance()/1000.0;
 String foodDistanceText=foodDistance>0&&foodDistance<1?Math.round(foodDistance*1000)+"m":foodDistance>=1?String.format("%.1f",foodDistance)+"km":"거리 정보 없음";
%>

	<%-- =========================================================
	     주제: 음식 카드
	     설명: onclick 대신 data 속성으로 상세정보 전달
	========================================================= --%>
<div id="<%=foodCardId%>" class="venue-card cursor-pointer group bg-surface-container-lowest p-3.5 rounded-2xl shadow-sm hover:shadow-md transition-all duration-200 relative"
 data-food-id="<%=food.getFood_id()%>"
 data-category="<%=attr(food.getF_category())%>"
 data-lat="<%=food.getLatitude()%>"
 data-lng="<%=food.getLongitude()%>"
 data-content-id="<%=attr(food.getContent_id())%>"
 data-name="<%=attr(detailName)%>"
 data-address="<%=attr(detailAddress)%>"
 data-phone="<%=attr(detailPhone)%>"
 data-image="<%=attr(detailImage)%>"
 onclick="selectPlaceFromList(this)">
	
	<div class="flex gap-3.5">
	<div class="relative w-24 h-24 sm:w-28 sm:h-28 rounded-xl overflow-hidden shrink-0 bg-surface-container">
	<img class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300" src="<%=cardImage%>" alt="<%=attr(foodName)%>" onerror="this.onerror=null;this.src='<%=attr(fallbackImage)%>';">
	<span class="absolute top-1.5 left-1.5 px-2 py-0.5 rounded-md bg-on-surface/75 backdrop-blur-md text-surface font-label-sm text-label-sm font-semibold"><%=rank%>위</span>
	</div>
	
	<div class="flex flex-col justify-between flex-1 min-w-0">
	<div>
	<div class="flex items-start justify-between gap-1">
	<span class="inline-block px-2 py-0.5 rounded bg-surface-container text-on-surface-variant font-label-sm text-label-sm font-semibold"><%=foodCategory%></span>
	<button aria-label="찜하기" class="favorite-btn text-secondary-container hover:scale-110 active:scale-95 transition-transform p-1 -mr-1" type="button" onclick="event.stopPropagation();">
	<span class="material-symbols-outlined text-[20px]">favorite</span>
	</button>
	</div>
	
	<h3 class="font-title-md text-title-md font-bold text-on-surface truncate mt-1"><%=foodName%></h3>

	</div>
	
	<div class="flex items-center justify-between text-on-surface-variant font-body-sm text-body-sm pt-2">
	<span class="truncate max-w-[170px]" title="<%=attr(foodAddress)%>"><%=foodAddress%></span>
	</div>
	
	<div class="flex items-center gap-1 text-on-surface-variant font-body-sm text-body-sm mt-1">
	<span class="material-symbols-outlined text-[16px]">location_on</span><%=foodDistanceText%>
	</div>
	</div>
	</div>
	</div>
	
	<%
	rank++;
	}
	}else{
	%>
	<div class="p-6 text-center text-on-surface-variant">주변에 등록된 맛집이 없습니다.</div>
	<%
	}
	%>
	</div>

<!-- Tourist Attractions List Container -->

<div class="hidden flex flex-col space-y-2.5 max-h-[460px] overflow-y-auto pr-1" id="list-tour-container">

<%
if(tourList!=null&&!tourList.isEmpty()){

    int tourRank=1;

    for(TourDto tour:tourList){

        String tourName=tour.getTitle()!=null?tour.getTitle():"";

        String tourAddress=tour.getAddress()!=null?tour.getAddress():"";

        String tourCategory=tour.getCategory()!=null?tour.getCategory():"관광지";

        String tourContentId=tour.getContent_id()!=null?tour.getContent_id():"";
        
        String tourContentTypeId=tour.getContent_type_id()!=null?tour.getContent_type_id():"12";
        
        String tourImage=getTourImagePath(request,tour.getImage());
		
        double tourDistance=tour.getDistance();

        String tourDistanceText="";

        if(tourDistance>0&&tourDistance<1) tourDistanceText=Math.round(tourDistance*1000)+"m";

        else if(tourDistance>=1) tourDistanceText=String.format("%.1f",tourDistance)+"km";

        else tourDistanceText="거리 정보 없음";

        tourName=tourName.replace("\\\\","\\\\\\\\").replace("'","\\\\'").replace("\"","&quot;").replace("\r"," ").replace("\n"," ");

        tourAddress=tourAddress.replace("\\\\","\\\\\\\\").replace("'","\\\\'").replace("\"","&quot;").replace("\r"," ").replace("\n"," ");

        tourCategory=tourCategory.replace("\\\\","\\\\\\\\").replace("'","\\\\'").replace("\"","&quot;").replace("\r"," ").replace("\n"," ");

        tourImage=tourImage.replace("\\\\","\\\\\\\\").replace("'","\\\\'").replace("\"","&quot;").replace("\r"," ").replace("\n"," ");

        tourDistanceText=tourDistanceText.replace("\\\\","\\\\\\\\").replace("'","\\\\'").replace("\"","&quot;").replace("\r"," ").replace("\n"," ");
        
        tourContentId=tourContentId.replace("\\","\\\\").replace("'","\\'").replace("\"","&quot;").replace("\r"," ").replace("\n"," ");
        
        tourContentTypeId=tourContentTypeId.replace("\\","\\\\").replace("'","\\'").replace("\"","&quot;").replace("\r"," ").replace("\n"," ");
%>

		<%
		//=========================================================
		//주제: 관광지 카드 ID 생성
		//설명: 지도 마커 클릭 시 해당 관광지 카드를 찾아 클릭하기 위해 사용
		//=========================================================
		String tourCardId=tourRank==1?"first-tour-card":"tour-card-"+tourRank;
		%>

    <div id="<%=tourCardId%>" data-content-id="<%=attr(tour.getContent_id())%>" data-name="<%=attr(tour.getTitle())%>" data-address="<%=attr(tour.getAddress())%>" data-category="<%=attr(tour.getCategory())%>" data-image="<%=attr(getTourImagePath(request,tour.getImage()))%>" data-content-type-id="<%=attr(tour.getContent_type_id())%>" data-distance="<%=attr(tourDistanceText)%>" class="venue-card bg-surface-container-lowest p-3.5 rounded-2xl shadow-sm hover:shadow-md transition-all cursor-pointer"
onclick="selectPlaceFromList(this)">
        <div class="flex gap-3.5">

            <div class="relative w-24 h-24 sm:w-28 sm:h-28 rounded-xl overflow-hidden shrink-0 bg-surface-container">

                <img class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300" src="<%=tourImage%>" alt="<%=attr(tour.getTitle())%>" onerror="this.src='${pageContext.request.contextPath}/resources/images/food/fallback_tour.png';this.onerror=null;">

                <span class="absolute top-1.5 left-1.5 px-2 py-0.5 rounded-md bg-on-surface/75 backdrop-blur-md text-surface font-label-sm text-label-sm font-semibold"><%= tourRank %>위</span>

            </div>

            <div class="flex flex-col justify-between flex-1 min-w-0">

                <div>

                    <span class="inline-block px-2 py-0.5 rounded bg-secondary-fixed text-on-secondary-fixed font-label-sm text-label-sm font-semibold"><%= tour.getCategory() %></span>

                    <h3 class="font-title-md text-title-md font-bold text-on-surface truncate mt-1"><%= tour.getTitle() %></h3>

                    <p class="font-body-sm text-body-sm text-on-surface-variant line-clamp-1 mt-0.5"><%= tour.getAddress() %></p>

                </div>

                <div class="flex items-center gap-1 text-on-surface-variant font-body-sm text-body-sm mt-1">

                    <span class="material-symbols-outlined text-[16px]">location_on</span>

                    <%= tourDistanceText %>

                </div>

            </div>

        </div>

    </div>

<%

        tourRank++;

    }

}else{

%>

    <div class="p-6 text-center text-on-surface-variant">주변에 표시할 관광지가 없습니다.</div>

<%

}

%>

</div>



<!-- Subtle Interactive Helper Note -->

<div class="flex items-center gap-1.5 px-3 py-2 rounded-xl bg-surface-container-low text-on-surface-variant font-label-sm text-label-sm">

<span class="material-symbols-outlined text-[16px] text-primary">info</span>

<span>목록 클릭: 아래 상세정보 갱신 · 지도 마커 클릭: 상세정보 위치로 이동</span>

</div>

</div>

</div>

</section>

<% if(foodCount>0||tourCount>0){ %>

<!-- 3. SELECTED RESTAURANT DETAIL SECTION (INLINE EXPANDED CARD) -->

<section class="max-w-[1200px] mx-auto px-margin-mobile md:px-margin pb-12 w-full" id="selected-detail-section">

<div class="bg-surface-container-lowest rounded-2xl shadow-md p-5 sm:p-7 transition-all">

<!-- Header Tag Bar -->

<div class="flex flex-wrap items-center justify-between gap-3 pb-5 mb-6 border-b border-surface-container">

<div class="flex items-center gap-2">

<span class="px-3 py-1 rounded-full bg-primary text-on-primary font-label-sm text-label-sm font-bold flex items-center gap-1">

<span class="material-symbols-outlined text-[14px]">verified</span>

              선택된 매장 정보

            </span>

<span class="px-2.5 py-1 rounded-full bg-secondary-fixed text-on-secondary-fixed font-label-sm text-label-sm font-bold">

              현지인 추천

            </span>

<span class="px-2.5 py-1 rounded-full bg-surface-container text-on-surface-variant font-label-sm text-label-sm font-semibold">

              안심식당

            </span>

</div>

<span class="font-label-sm text-label-sm text-on-surface-variant">

            최종 업데이트: 2026년 10월 기준

          </span>

</div>

<!-- 2-Column Responsive Body -->

<div class="grid grid-cols-1 lg:grid-cols-12 gap-8 items-stretch">

<!-- LEFT: Main Photo & Mini Thumbnails (5 cols) -->

<div class="lg:col-span-5 flex flex-col h-full">

<div class="relative w-full h-[320px] lg:h-full lg:min-h-[520px] rounded-2xl overflow-hidden bg-surface-container shadow-inner">

<img class="w-full h-full object-cover" id="detail-main-img" src="${pageContext.request.contextPath}/resources/images/food/fallback_tour.png" onerror="this.src='${pageContext.request.contextPath}/resources/images/food/fallback_tour.png';this.onerror=null;">

<div class="absolute inset-0 bg-gradient-to-t from-on-surface/60 via-transparent to-transparent"></div>

<div class="absolute bottom-3.5 left-4 text-surface">

<span id="detail-main-label" class="font-label-sm text-label-sm font-semibold opacity-90">대표 메뉴</span>

<p id="detail-main-menu" class="font-title-md text-title-md font-bold text-white">따로돼지국밥 (9,500원)</p>

</div>

</div>



</div>

<!-- RIGHT: Detailed Information, Action Buttons & Verified Review (7 cols) -->

<div class="lg:col-span-7 flex flex-col justify-between space-y-5">

<div>

<!-- Title & Header Actions -->

<div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">

<div>

<div class="flex items-center gap-2">

<h2 class="font-headline-md text-headline-md text-on-surface font-extrabold tracking-tight" id="detail-title">

                      부산 돼지국밥 본가

                    </h2>

<span id="detail-rating-wrap" class="inline-flex items-center text-secondary font-headline-sm text-headline-sm font-bold">
<span class="material-symbols-outlined text-[20px] mr-0.5" style="font-variation-settings:'FILL' 1;">star</span>
<span id="detail-rating">4.8</span>
</span>

</div>

<p id="detail-subtitle" class="font-body-md text-body-md text-on-surface-variant mt-0.5">
  장소를 선택하면 상세 설명이 표시됩니다.
</p>
<span id="detail-review" style="display:none;"></span>

</div>

<!-- =========================================================
     주제: 상세정보 액션 버튼
     설명: 저장 버튼과 네이버 지도 버튼만 표시
========================================================= -->
<div class="flex items-center gap-2 shrink-0">
<button class="px-4 py-2 rounded-xl bg-secondary text-on-secondary font-label-md text-label-md font-bold shadow-sm hover:opacity-95 active:scale-95 transition-all flex items-center gap-1.5" id="btn-detail-save" onclick="toggleDetailSave(this)" type="button">
<span class="material-symbols-outlined text-[18px]" style="font-variation-settings:'FILL' 1;">favorite</span>
<span id="save-btn-text">찜하기</span>
</button>
<a id="btn-naver-review" class="px-4 py-2 rounded-xl bg-primary text-on-primary font-label-md text-label-md font-bold hover:bg-primary-container transition-all flex items-center gap-1" href="#" target="_blank" rel="noopener">
<span class="material-symbols-outlined text-[18px]">map</span>
<span>네이버 지도</span>
</a>
</div>

</div>

<!-- Information Grid -->



<%--  --%>

<div class="grid grid-cols-1 sm:grid-cols-2 gap-3.5 mt-5 bg-surface-container-low p-4 rounded-xl">

<div class="flex items-start gap-2.5">

<span class="material-symbols-outlined text-primary text-[20px] shrink-0 mt-0.5">location_on</span>

<div class="min-w-0">

<span class="font-label-sm text-label-sm text-on-surface-variant font-bold block">주소 및 접근성</span>

<p id="detail-address"

	 class="font-body-md text-body-md text-on-surface font-medium truncate" title="부산광역시 동구 중앙대로 214번길 8">${food.address }</p>

<span class="font-body-sm text-body-sm text-primary font-semibold">초량역 1번 출구에서 150m (도보 2분)</span>

</div>

</div>

<div class="flex items-start gap-2.5">

<span class="material-symbols-outlined text-primary text-[20px] shrink-0 mt-0.5">schedule</span>

<div>

<span id="detail-hours-label" class="font-label-sm text-label-sm text-on-surface-variant font-bold block">운영시간</span>

<p id="detail-hours"

   class="font-body-md text-body-md text-on-surface font-medium">

    매일 08:30 - 21:30

</p>



<span id="detail-hours-note"

      class="font-body-sm text-body-sm text-outline">

    라스트오더 21:00 (연중무휴)

</span>

</div>

</div>

<div class="flex items-start gap-2.5">

<span class="material-symbols-outlined text-primary text-[20px] shrink-0 mt-0.5">call</span>

<div>

<span id="detail-phone-label" class="font-label-sm text-label-sm text-on-surface-variant font-bold block">전화번호</span>

<p id="detail-phone"

	 class="font-body-md text-body-md text-on-surface font-medium">051-468-1230</p>

<span id="detail-phone-note" class="font-body-sm text-body-sm text-outline">단체 예약 및 포장 가능</span>

</div>

</div>

<div class="flex items-start gap-2.5">

<span class="material-symbols-outlined text-primary text-[20px] shrink-0 mt-0.5">restaurant_menu</span>

<div>

<span id="detail-menu-label" class="font-label-sm text-label-sm text-on-surface-variant font-bold block">대표 메뉴</span>

<p id="detail-menu"

   class="font-body-md text-body-md text-on-surface font-medium">

    따로돼지국밥 9.5 / 수육백반 12.0

</p>

<span id="detail-menu-note"

      class="font-body-sm text-body-sm text-outline">

    모듬수육 (소 28.0 / 대 38.0)

</span>

</div>

</div>

</div><%--  --%>





<!-- Editorial Summary Box -->

<%-- --%>

<div class="mt-4 p-4 rounded-xl bg-surface-container-lowest border-l-4 border-primary">

<p id="detail-comment" class="font-body-md text-body-md text-on-surface leading-relaxed" style="word-break:keep-all; overflow-wrap:break-word;">
장소를 선택하면 상세 설명이 표시됩니다.
</p>

</div>

</div>

</div>

</div> <%-- --%>

</div>

</section>

<% }else{ %>
<!-- =========================================================
     주제: 조회 결과 없음 안내
     설명: 선택한 좌표 주변에 맛집/관광지가 없을 때 하드코딩된 상세정보 대신 안내 화면 표시
========================================================= -->
<section class="max-w-[1200px] mx-auto px-margin-mobile md:px-margin pb-16 w-full" id="selected-detail-section">
<div class="bg-surface-container-lowest rounded-3xl shadow-sm p-8 md:p-12 text-center border border-surface-container">
<span class="inline-flex items-center justify-center w-14 h-14 rounded-full bg-surface-container text-on-surface-variant mb-4"><span class="material-symbols-outlined text-[30px]">search_off</span></span>
<h2 class="font-headline-md text-headline-md text-on-surface font-bold">이 주변에는 표시할 맛집/관광지가 없습니다.</h2>
<p class="font-body-md text-body-md text-on-surface-variant mt-2">다른 지역을 선택하거나 지도를 조금 이동해서 다시 탐색해보세요.</p>
<div class="flex flex-wrap justify-center gap-3 mt-6">
<a class="px-4 py-2 rounded-xl bg-surface-container text-on-surface font-label-md text-label-md font-bold" href="${pageContext.request.contextPath}/food/?lat=37.5665&lng=126.9780&region=서울">서울</a>
<a class="px-4 py-2 rounded-xl bg-surface-container text-on-surface font-label-md text-label-md font-bold" href="${pageContext.request.contextPath}/food/?lat=35.1150&lng=129.0420&region=부산">부산</a>
<a class="px-4 py-2 rounded-xl bg-surface-container text-on-surface font-label-md text-label-md font-bold" href="${pageContext.request.contextPath}/food/?lat=33.4996&lng=126.5312&region=제주">제주</a>
</div>
</div>
</section>
<% } %>
<% } %>

</div>

</div>

<%-- =========================================================
     주제: 네이버 지도 API 로드
     설명: 지도 + 주소검색/geocoder 기능을 함께 불러옴
========================================================= --%>
<script type="text/javascript" src="https://oapi.map.naver.com/openapi/v3/maps.js?ncpKeyId=80ugvgf7e3&submodules=geocoder"></script>

<%-- =========================================================
     주제: 비로그인 찜하기 안내 팝업
     설명: 하트를 클릭하면 로그인 안내 표시 (기존 찜 API 기능 변경 없음)
========================================================= --%>
<style>
 #like-login-dialog{box-sizing:border-box;border:1px solid rgba(255,255,255,.11);padding:0;margin:auto;width:420px;max-width:calc(100vw - 32px);max-height:calc(100dvh - 32px);border-radius:24px;overflow:auto;background:#202329;color:#f7f9fb;box-shadow:0 28px 90px rgba(0,0,0,.5);font-family:inherit;}
 #like-login-dialog::backdrop{background:rgba(6,9,14,.76);backdrop-filter:blur(6px);-webkit-backdrop-filter:blur(6px);}
 #like-login-dialog[open]{animation:likeModalIn .2s ease-out;}
 #like-login-dialog .like-modal-body{position:relative;padding:34px 30px 28px;text-align:center;background:radial-gradient(circle at 50% 0%,rgba(0,163,169,.16),transparent 58%),#202329;}
 #like-login-dialog .like-modal-close{position:absolute;right:15px;top:15px;display:flex;align-items:center;justify-content:center;width:34px;height:34px;border:0;border-radius:11px;background:rgba(255,255,255,.065);color:#c7ccd3;cursor:pointer;transition:background .2s,color .2s;}
 #like-login-dialog .like-modal-close:hover{background:rgba(255,255,255,.15);color:white;}
 #like-login-dialog .like-modal-icon{position:relative;display:flex;align-items:center;justify-content:center;margin:9px auto 20px;width:75px;height:75px;border-radius:24px;background:linear-gradient(140deg,rgba(255,111,97,.2),rgba(255,111,97,.065));border:1px solid rgba(255,132,115,.21);color:#ff7567;}
 #like-login-dialog .like-modal-icon .material-symbols-outlined{font-size:36px;font-variation-settings:'FILL' 1;}
 #like-login-dialog .like-modal-lock{position:absolute;right:-6px;bottom:-4px;display:flex;align-items:center;justify-content:center;width:28px;height:28px;background:#007e88;color:#fff;border:3px solid #202329;border-radius:10px;}
 #like-login-dialog .like-modal-lock .material-symbols-outlined{font-size:15px;font-variation-settings:'FILL' 0;}
 #like-login-dialog .like-modal-kicker{font-size:11px;letter-spacing:.12em;font-weight:800;color:#44ccd3;margin:0 0 8px;}
 #like-login-dialog .like-modal-title{font-size:23px;font-weight:800;line-height:1.45;letter-spacing:-.035em;color:#fff;margin:0;word-break:keep-all;}
 #like-login-dialog .like-modal-desc{font-size:13px;line-height:1.75;color:#b4bdc7;margin:12px 0 25px;word-break:keep-all;}
 #like-login-dialog .like-modal-login{display:flex;align-items:center;justify-content:center;gap:9px;min-height:49px;width:100%;box-sizing:border-box;border-radius:13px;background:#007e88;color:white;text-decoration:none;font-size:14px;font-weight:800;transition:background .18s,transform .18s;}
 #like-login-dialog .like-modal-login:hover{background:#0096a1;transform:translateY(-1px);}
 #like-login-dialog .like-modal-later{display:block;margin:8px auto 0;width:100%;min-height:42px;border:0;border-radius:11px;background:transparent;color:#aeb6c0;font-size:13px;font-weight:600;cursor:pointer;}
 #like-login-dialog .like-modal-later:hover{background:rgba(255,255,255,.045);color:#fff;}
 #like-login-dialog .like-modal-note{font-size:11px;line-height:1.5;color:#88929e;margin:11px 0 0;}
 #like-login-dialog a:focus-visible,#like-login-dialog button:focus-visible{outline:2px solid #61dfe4;outline-offset:3px;}
 @keyframes likeModalIn{from{opacity:0;transform:translateY(10px) scale(.985);}to{opacity:1;transform:translateY(0) scale(1);}}
 @media(max-width:420px){#like-login-dialog .like-modal-body{padding:30px 22px 24px;}#like-login-dialog .like-modal-title{font-size:21px;}}
 @media(prefers-reduced-motion:reduce){#like-login-dialog[open]{animation:none;}#like-login-dialog .like-modal-login{transition:none;}}

/*=========================================================
주제: 로그인 회원 찜 저장·취소 알림
설명: 실제 서버 저장 성공 뒤 화면 아래에 상태 안내 표시
=========================================================*/
#food-like-toast{position:fixed;left:50%;bottom:32px;z-index:9999;display:flex;align-items:center;gap:12px;box-sizing:border-box;max-width:calc(100vw - 32px);padding:14px 20px;border:1px solid rgba(125,216,229,.35);border-radius:15px;background:linear-gradient(125deg,rgba(24,42,58,.97),rgba(30,56,68,.97));box-shadow:0 12px 35px rgba(6,22,37,.35);color:#f8fcff;font-size:14px;font-weight:700;line-height:1.45;letter-spacing:-.02em;opacity:0;transform:translate(-50%,14px);transition:opacity .22s ease,transform .22s ease;pointer-events:none;word-break:keep-all;}
#food-like-toast.is-visible{opacity:1;transform:translate(-50%,0);}
#food-like-toast .food-like-toast-icon{display:inline-flex;align-items:center;justify-content:center;width:32px;height:32px;flex:0 0 32px;border-radius:10px;background:rgba(255,129,118,.16);color:#ff897f;}
#food-like-toast.is-removed .food-like-toast-icon{background:rgba(77,207,218,.14);color:#69e1e9;}
#food-like-toast .food-like-toast-icon .material-symbols-outlined{font-size:20px;}
@media(max-width:600px){#food-like-toast{bottom:22px;padding:12px 15px;font-size:13px;}}
@media(prefers-reduced-motion:reduce){#food-like-toast{transition:none;}}
</style>
<dialog id="like-login-dialog" aria-labelledby="like-login-title" aria-describedby="like-login-desc">
 <div class="like-modal-body">
  <button type="button" class="like-modal-close" data-like-login-close aria-label="팝업 닫기"><span class="material-symbols-outlined">close</span></button>
  <div class="like-modal-icon" aria-hidden="true"><span class="material-symbols-outlined">favorite</span><span class="like-modal-lock"><span class="material-symbols-outlined">lock</span></span></div>
  <p class="like-modal-kicker">MY TRAVEL FAVORITES</p>
  <h2 class="like-modal-title" id="like-login-title">찜하기는 로그인 후<br>이용할 수 있어요.</h2>
  <p class="like-modal-desc" id="like-login-desc">마음에 드는 맛집과 관광지를 저장하고,<br>마이페이지에서 한 번에 확인해 보세요.</p>
  <a class="like-modal-login" href="${pageContext.request.contextPath}/Login" autofocus><span class="material-symbols-outlined" style="font-size:19px;">login</span> 로그인하기 <span class="material-symbols-outlined" style="font-size:18px;">arrow_forward</span></a>
  <button class="like-modal-later" type="button" data-like-login-close>다음에 할게요</button>
  <p class="like-modal-note">로그인 전에는 찜한 장소가 저장되지 않습니다.</p>
 </div>
</dialog>

<!--=========================================================
주제: 맛집·관광지 찜 처리 성공 안내
설명: 로그인 후 찜 저장·취소가 DB에 반영되면 표시
=========================================================-->
<div id="food-like-toast" role="status" aria-live="polite" aria-atomic="true">
 <span class="food-like-toast-icon" aria-hidden="true"><span class="material-symbols-outlined">favorite</span></span>
 <span class="food-like-toast-text"></span>
</div>

<script>



let autoSelectingDetail=false;

//=========================================================
//주제: 지도 중심 마커 저장
//설명: 검색/지역변경/지도클릭 시 지도 중심과 파란 마커를 같이 이동
//=========================================================
let selectedCenterMarker=null;

	//=========================================================
	//주제: 지도 마커 상태 저장
	//설명: 목록 클릭 시 해당 마커만 강조하기 위해 마커를 cardId 기준으로 저장
	//=========================================================
	let mapPlaceMarkers={};
	let activeMarkerCardId="";
	
		//=========================================================
		//주제: 지도 마커용 맛집 데이터
		//설명: FoodDto의 위도/경도로 네이버 지도에 맛집 마커 표시
		//=========================================================
		let foodMapPlaces=[
		<%
		if(foodList!=null&&!foodList.isEmpty()){
		int markerRank=1;
		for(FoodDto food:foodList){
		double lat=food.getLatitude();
		double lng=food.getLongitude();
		if(lat!=0&&lng!=0){
		 String markerName=food.getF_name()!=null?food.getF_name():"맛집";
		 String markerCardId=markerRank==1?"first-food-card":"food-card-"+markerRank;
		%>
		{name:"<%=attr(markerName)%>",lat:<%=lat%>,lng:<%=lng%>,type:"food",cardId:"<%=markerCardId%>"},
		<%
		}
		markerRank++;
		}
		}
		%>
		];
		
		//=========================================================
		//주제: 지도 마커용 관광지 데이터
		//설명: TourDto의 위도/경도로 네이버 지도에 관광지 마커 표시
		//=========================================================
		let tourMapPlaces=[
		<%
		if(tourList!=null&&!tourList.isEmpty()){
		int markerRank=1;
		for(TourDto tour:tourList){
		double lat=tour.getLatitude();
		double lng=tour.getLongitude();
		if(lat!=0&&lng!=0){
		 String markerName=tour.getTitle()!=null?tour.getTitle():"관광지";
		 String markerCardId=markerRank==1?"first-tour-card":"tour-card-"+markerRank;
		%>
		{name:"<%=attr(markerName)%>",lat:<%=lat%>,lng:<%=lng%>,type:"tour",cardId:"<%=markerCardId%>"},
		<%
		}
		markerRank++;
		}
		}
		%>
		];

		//=========================================================
		//주제: 마커 이름 안전 처리
		//설명: &amp; 같은 HTML 문자를 먼저 원래 문자로 되돌린 뒤 마커에 표시
		//=========================================================
		function decodeHtmlText(text){
		  const box=document.createElement("textarea");
		  box.innerHTML=text||"";
		  return box.value;
		}
		function safeMarkerText(text){
		  const decoded=decodeHtmlText(text);
		  return decoded.replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;").replace(/"/g,"&quot;").replace(/'/g,"&#039;");
		}

		//=========================================================
		//주제: 지도 마커 모양 생성
		//설명: 원을 hover하면 확대되고 장소명 표시, 목록에서 선택한 마커는 이름 유지
		//=========================================================
		function makePlaceMarkerIcon(place,active){
		  const color=place.type==="food"?"#087a72":"#bf791a";
		  const cls="food-place-map-marker"+(active?" is-selected":"");
		  const html='<div class="'+cls+'" style="--marker-color:'+color+'"><span class="food-place-map-marker__name">'+safeMarkerText(place.name)+'</span><span class="food-place-map-marker__dot"></span></div>';
		  return {content:html,size:new naver.maps.Size(26,26),anchor:new naver.maps.Point(13,13)};
		}
		
		//=========================================================
		//주제: 선택한 카드의 지도 마커 강조
		//설명: 목록 클릭 시 해당 마커만 이름을 보여주고 깜빡이게 표시
		//=========================================================
		function focusMapMarkerByCardId(cardId){
		  if(!cardId||!mapPlaceMarkers[cardId])return;
		  Object.keys(mapPlaceMarkers).forEach(function(id){
		    const item=mapPlaceMarkers[id];
		    item.marker.setIcon(makePlaceMarkerIcon(item.place,id===cardId));
		    item.marker.setZIndex(id===cardId?500:100);
		  });
		  activeMarkerCardId=cardId;
		  const target=mapPlaceMarkers[cardId];
		  if(window.foodMap)window.foodMap.panTo(target.marker.getPosition());
		}
		
		//=========================================================
		//주제: 맛집 페이지 좌표 이동
		//설명: 페이지 새로고침 없이 fetch로 목록/지도/상세정보를 갱신
		//=========================================================
		function goFoodLocation(lat,lng,region){
		  loadFoodByLocation(lat,lng,region||"지도 선택 위치");
		}
		
		//=========================================================
		//주제: 지도 중심 이동
		//설명: fetch로 목록이 바뀔 때 지도 중심과 파란 중심 마커를 같이 이동
		//=========================================================
		function moveMapCenter(lat,lng){
		  if(!window.foodMap||typeof naver==="undefined"||!naver.maps)return;
		  const center=new naver.maps.LatLng(Number(lat),Number(lng));
		  window.foodMap.setCenter(center);
		  window.foodMap.setZoom(15);
		  if(selectedCenterMarker)selectedCenterMarker.setPosition(center);
		  else selectedCenterMarker=new naver.maps.Marker({position:center,map:window.foodMap,title:"선택한 위치"});
		}
		
		//=========================================================
		//주제: 검색 결과 지도 범위 맞추기
		//설명: 부산 국밥 검색 결과 마커들이 보이도록 지도 화면 조정
		//=========================================================
		function fitMapToResults(list){
		  if(!window.foodMap||typeof naver==="undefined"||!naver.maps||!list||list.length===0)return;
		  const valid=list.filter(function(item){return Number(item.lat)!==0&&Number(item.lng)!==0;});
		  if(valid.length===0)return;
		  if(valid.length===1){
		    moveMapCenter(valid[0].lat,valid[0].lng);
		    return;
		  }
		  const bounds=new naver.maps.LatLngBounds();
		  valid.forEach(function(item){
		    bounds.extend(new naver.maps.LatLng(Number(item.lat),Number(item.lng)));
		  });
		  window.foodMap.fitBounds(bounds);
		}
		
		//=========================================================
		//주제: 지도 마커 전체 삭제
		//설명: fetch로 새 위치를 불러오기 전 기존 마커 제거
		//=========================================================
		function clearPlaceMarkers(){
		  Object.keys(mapPlaceMarkers).forEach(function(id){
		    if(mapPlaceMarkers[id]&&mapPlaceMarkers[id].marker)mapPlaceMarkers[id].marker.setMap(null);
		  });
		  mapPlaceMarkers={};
		  activeMarkerCardId="";
		}

		//=========================================================
		//주제: 지도 마커 데이터 갱신
		//설명: 새 맛집/관광지 목록으로 지도 마커를 다시 생성
		//=========================================================
		function updateMapPlaces(foods,tours){
		  foodMapPlaces=(foods||[]).map(function(f,i){
		    return {name:f.name,lat:Number(f.lat),lng:Number(f.lng),type:"food",cardId:i===0?"first-food-card":"food-card-"+(i+1)};
		  }).filter(function(f){return Number.isFinite(f.lat)&&Number.isFinite(f.lng)&&f.lat!==0&&f.lng!==0;});

		  tourMapPlaces=(tours||[]).map(function(t,i){
		    return {name:t.name,lat:Number(t.lat),lng:Number(t.lng),type:"tour",cardId:i===0?"first-tour-card":"tour-card-"+(i+1)};
		  }).filter(function(t){return Number.isFinite(t.lat)&&Number.isFinite(t.lng)&&t.lat!==0&&t.lng!==0;});

		  clearPlaceMarkers();

		  if(window.foodMap){
		    addPlaceMarkers(window.foodMap,foodMapPlaces);
		    addPlaceMarkers(window.foodMap,tourMapPlaces);
		  }
		}

		//=========================================================
		//주제: 위치 기준 맛집/관광지 다시 불러오기
		//설명: 지도 클릭/지역변경/현재 지도 위치 버튼 클릭 시 목록 갱신
		//=========================================================
		function loadFoodByLocation(lat,lng,region){
		  setFoodLoading(true);
		  const url="${pageContext.request.contextPath}/food/listapi?lat="+encodeURIComponent(lat)+"&lng="+encodeURIComponent(lng)+"&region="+encodeURIComponent(region||"지도 선택 위치");
		  fetch(url)
		    .then(function(res){return res.text().then(function(text){return {ok:res.ok,text:text};});})
		    .then(function(result){
		      if(!result.ok)throw new Error("listapi 서버 오류");
		      const data=JSON.parse(result.text);
		      renderFoodList(data.foodList||[]);
		      renderTourList(data.tourList||[]);
		      updateMapPlaces(data.foodList||[],data.tourList||[]);
		      updateSelectedRegion(data.region||region||"지도 선택 위치");
		      moveMapCenter(lat,lng);
		      history.pushState(null,"","${pageContext.request.contextPath}/food/?lat="+encodeURIComponent(lat)+"&lng="+encodeURIComponent(lng)+"&region="+encodeURIComponent(data.region||region||"지도 선택 위치"));
		      const firstFood=document.getElementById("first-food-card");
		      const firstTour=document.getElementById("first-tour-card");
		      const target=firstFood||firstTour;
		      if(target){autoSelectingDetail=true;openMapPlaceDetailByCard(target);autoSelectingDetail=false;}
		    })
		    .catch(function(error){
		      console.log("[listapi 오류]",error);
		      alert("주변 맛집 정보를 불러오지 못했습니다.");
		    })
		    .finally(function(){
		      setFoodLoading(false);
		    });
		}

		//=========================================================
		//주제: 선택 지역명 변경
		//설명: 오른쪽 선택구역과 지도 왼쪽 상단 탐색 문구를 함께 갱신
		//=========================================================
		function updateSelectedRegion(region){
		  const name=region||"지도 선택 위치";
		  const title=document.getElementById("selected-region-text");
		  const badge=document.getElementById("map-region-badge");
		  if(title)title.innerText=name;
		  if(badge)badge.innerText="📍 탐색 중: "+name;
		}
		
		//=========================================================
		//주제: 맛집 목록 다시 그리기
		//설명: TourAPI 거리(m)를 km로 변환해 출력. 이름/주소/전화번호/이미지도 카드에 저장
		//=========================================================
		function renderFoodList(list){
		 const box=document.getElementById("list-food-container");
		 const tabText=document.querySelector("#tab-food span:last-child");
		 if(tabText)tabText.innerText="맛집 ("+list.length+")";
		 if(!box)return;
		 if(list.length===0){box.innerHTML='<div class="p-6 text-center text-on-surface-variant">주변에 등록된 맛집이 없습니다.</div>';return;}
		 box.innerHTML=list.map(function(f,i){
		  const id=i===0?"first-food-card":"food-card-"+(i+1);
		  const img=f.image&&f.image.trim()!==""?f.image:getFoodFallbackImage(f.category,f.name);
		  return '<div id="'+id+'" class="venue-card cursor-pointer group bg-surface-container-lowest p-3.5 rounded-2xl shadow-sm hover:shadow-md transition-all duration-200 relative" data-food-id="'+Number(f.foodId||0)+'" data-category="'+escapeHtml(f.category||"맛집")+'" data-lat="'+Number(f.lat||0)+'" data-lng="'+Number(f.lng||0)+'" data-content-id="'+escapeHtml(f.contentId||"")+'" data-name="'+escapeHtml(f.name)+'" data-address="'+escapeHtml(f.address)+'" data-phone="'+escapeHtml(f.phone)+'" data-image="'+escapeHtml(img)+'" onclick="selectPlaceFromList(this)"><div class="flex gap-3.5"><div class="relative w-24 h-24 sm:w-28 sm:h-28 rounded-xl overflow-hidden shrink-0 bg-surface-container"><img class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300" src="'+escapeHtml(img)+'" onerror="this.src=\'${pageContext.request.contextPath}/resources/images/food/no-image.png\';this.onerror=null;"><span class="absolute top-1.5 left-1.5 px-2 py-0.5 rounded-md bg-on-surface/75 text-surface font-label-sm text-label-sm font-semibold">'+(i+1)+'위</span></div><div class="flex flex-col justify-between flex-1 min-w-0"><div><div class="flex items-start justify-between gap-1"><span class="inline-block px-2 py-0.5 rounded bg-surface-container text-on-surface-variant font-label-sm text-label-sm font-semibold">'+escapeHtml(f.category||"맛집")+'</span><button aria-label="찜하기" class="favorite-btn text-secondary-container hover:scale-110 active:scale-95 transition-transform p-1 -mr-1" type="button" onclick="event.stopPropagation();"><span class="material-symbols-outlined text-[20px]">favorite</span></button></div><h3 class="font-title-md text-title-md font-bold text-on-surface truncate mt-1">'+escapeHtml(f.name)+'</h3></div><div class="flex items-center justify-between text-on-surface-variant font-body-sm text-body-sm pt-2"><span class="truncate max-w-[170px]" title="'+escapeHtml(f.address)+'">'+escapeHtml(f.address)+'</span></div><div class="flex items-center gap-1 text-on-surface-variant font-body-sm text-body-sm mt-1"><span class="material-symbols-outlined text-[16px]">location_on</span>'+formatDistance(Number(f.distance)/1000)+'</div></div></div></div>';
		 }).join("");
		}
		
		//=========================================================
		//주제: 관광지 목록 렌더링
		//설명: fetch로 받은 관광지 JSON으로 오른쪽 관광지 카드 목록 갱신
		//=========================================================
		function renderTourList(list){
		  const box=document.getElementById("list-tour-container");
		  const tabText=document.querySelector("#tab-tour span:last-child");
		  if(tabText)tabText.innerText="관광지 ("+list.length+")";
		  if(!box)return;
		  if(list.length===0){box.innerHTML='<div class="p-6 text-center text-on-surface-variant">주변에 표시할 관광지가 없습니다.</div>';return;}
		  box.innerHTML=list.map(function(t,i){
		    const id=i===0?"first-tour-card":"tour-card-"+(i+1);
		    const img=t.image&&t.image.trim()!==""?t.image:"${pageContext.request.contextPath}/resources/images/food/fallback_tour.png";
		    const distance=formatDistance(t.distance);
		    return '<div id="'+id+'" data-content-id="'+escapeHtml(t.contentId||"")+'" data-name="'+escapeHtml(t.name||"")+'" data-address="'+escapeHtml(t.address||"")+'" data-category="'+escapeHtml(t.category||"관광지")+'" data-image="'+escapeHtml(img)+'" data-content-type-id="'+escapeHtml(t.contentTypeId||"12")+'" data-distance="'+escapeHtml(distance)+'" class="venue-card bg-surface-container-lowest p-3.5 rounded-2xl shadow-sm hover:shadow-md transition-all cursor-pointer" onclick="selectPlaceFromList(this)"><div class="flex gap-3.5"><div class="relative w-24 h-24 sm:w-28 sm:h-28 rounded-xl overflow-hidden shrink-0 bg-surface-container"><img class="w-full h-full object-cover" src="'+escapeHtml(img)+'" onerror="this.src=\'${pageContext.request.contextPath}/resources/images/food/fallback_tour.png\';this.onerror=null;"><span class="absolute top-1.5 left-1.5 px-2 py-0.5 rounded-md bg-on-surface/75 text-surface font-label-sm text-label-sm font-semibold">'+(i+1)+'위</span></div><div class="flex flex-col justify-between flex-1 min-w-0"><div><span class="inline-block px-2 py-0.5 rounded bg-secondary-fixed text-on-secondary-fixed font-label-sm text-label-sm font-semibold">'+escapeHtml(t.category||"관광지")+'</span><h3 class="font-title-md text-title-md font-bold text-on-surface truncate mt-1">'+escapeHtml(t.name)+'</h3><p class="font-body-sm text-body-sm text-on-surface-variant line-clamp-1 mt-0.5">'+escapeHtml(t.address)+'</p></div><div class="flex items-center gap-1 text-on-surface-variant font-body-sm text-body-sm mt-1"><span class="material-symbols-outlined text-[16px]">location_on</span>'+distance+'</div></div></div></div>';
		  }).join("");
		}
		
		//=========================================================
		//주제: fetch 렌더링 보조 함수
		//설명: HTML/JS 깨짐 방지와 거리 표시 처리
		//=========================================================
		function escapeHtml(text){
		  return String(text||"").replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;").replace(/"/g,"&quot;").replace(/'/g,"&#039;");
		}
		function escapeJs(text){
		  return String(text||"").replace(/\\/g,"\\\\").replace(/'/g,"\\'").replace(/\r/g," ").replace(/\n/g," ");
		}
		function formatDistance(distance){
		  const d=Number(distance||0);
		  if(d>0&&d<1)return Math.round(d*1000)+"m";
		  if(d>=1)return d.toFixed(1)+"km";
		  return "거리 정보 없음";
		}
		
		//=========================================================
		//주제: 음식 이미지 fallback
		//설명: fetch 검색 결과에 이미지가 없을 때 음식 종류별 기본 이미지 사용
		//=========================================================
		function getFoodFallbackImage(category,name,menu){
		  const text=((category||"")+" "+(name||"")+" "+(menu||"")).toLowerCase();
		  if(text.includes("치킨")||text.includes("chicken")||text.includes("닭")||text.includes("통닭")||text.includes("후라이드")||text.includes("양념치킨")||text.includes("닭강정"))return "${pageContext.request.contextPath}/resources/images/food/fallback_chicken.png";
		  if(text.includes("라멘")||text.includes("ramen")||text.includes("우동")||text.includes("국수")||text.includes("누들")||text.includes("noodle")||text.includes("냉면")||text.includes("밀면")||text.includes("쌀국수")||text.includes("칼국수")||text.includes("소바")||text.includes("면"))return "${pageContext.request.contextPath}/resources/images/food/fallback_noodle.png";
		  if(text.includes("초밥")||text.includes("스시")||text.includes("sushi")||text.includes("사시미")||text.includes("회")||text.includes("롤"))return "${pageContext.request.contextPath}/resources/images/food/fallback_sushi.png";
		  if(text.includes("햄버거")||text.includes("버거")||text.includes("burger")||text.includes("치즈버거"))return "${pageContext.request.contextPath}/resources/images/food/fallback_burger.png";
		  if(text.includes("피자")||text.includes("pizza"))return "${pageContext.request.contextPath}/resources/images/food/fallback_pizza.png";
		  if(text.includes("디저트")||text.includes("케이크")||text.includes("브런치")||text.includes("베이커리")||text.includes("마카롱")||text.includes("타르트"))return "${pageContext.request.contextPath}/resources/images/food/fallback_dessert.png";
		  if(text.includes("카페")||text.includes("cafe")||text.includes("커피")||text.includes("라떼"))return "${pageContext.request.contextPath}/resources/images/food/fallback_cafe.png";
		  if(text.includes("국밥")||text.includes("한식")||text.includes("백반")||text.includes("찌개")||text.includes("불고기")||text.includes("비빔밥")||text.includes("삼겹살")||text.includes("갈비")||text.includes("곰탕")||text.includes("설렁탕")||text.includes("돼지국밥")||text.includes("순대국")||text.includes("보쌈")||text.includes("족발"))return "${pageContext.request.contextPath}/resources/images/food/fallback_korean.png";
		  if(text.includes("중식")||text.includes("짜장")||text.includes("짬뽕")||text.includes("탕수육")||text.includes("마라")||text.includes("딤섬")||text.includes("중국집"))return "${pageContext.request.contextPath}/resources/images/food/fallback_chinese.png";
		  if(text.includes("양식")||text.includes("파스타")||text.includes("스테이크")||text.includes("리조또"))return "${pageContext.request.contextPath}/resources/images/food/fallback_western.png";
		  return "${pageContext.request.contextPath}/resources/images/food/no-image.png";
		}
		
		//=========================================================
		//주제: 지역/음식 검색 경로 분리
		//설명: 첫 화면은 주소 이동, 지도 화면은 현재 지도/목록을 갱신
		//=========================================================
		const FOOD_REGION_COORDS={
		 "서울":{lat:37.5665,lng:126.9780,region:"서울"},"부산":{lat:35.1150,lng:129.0420,region:"부산"},
		 "대구":{lat:35.8714,lng:128.6014,region:"대구"},"인천":{lat:37.4563,lng:126.7052,region:"인천"},
		 "광주":{lat:35.1595,lng:126.8526,region:"광주"},"대전":{lat:36.3504,lng:127.3845,region:"대전"},
		 "울산":{lat:35.5384,lng:129.3114,region:"울산"},"세종":{lat:36.4800,lng:127.2890,region:"세종"},
		 "경기":{lat:37.2636,lng:127.0286,region:"경기"},"강원":{lat:37.8813,lng:127.7298,region:"강원"},
		 "충북":{lat:36.6357,lng:127.4917,region:"충북"},"충남":{lat:36.6588,lng:126.6728,region:"충남"},
		 "전북":{lat:35.8242,lng:127.1480,region:"전북"},"전남":{lat:34.8118,lng:126.3922,region:"전남"},
		 "경북":{lat:36.5760,lng:128.5056,region:"경북"},"경남":{lat:35.2383,lng:128.6924,region:"경남"},
		 "제주":{lat:33.4996,lng:126.5312,region:"제주"},"해운대":{lat:35.1631,lng:129.1635,region:"해운대"},
		 "초량":{lat:35.1150,lng:129.0420,region:"부산"},"부산역":{lat:35.1151,lng:129.0415,region:"부산"},
		 "서면":{lat:35.1577,lng:129.0592,region:"부산"},"범내골":{lat:35.1463,lng:129.0598,region:"부산"}
		};
		function navigateToFoodSearch(region,keyword){
		 const pos=FOOD_REGION_COORDS[region]||FOOD_REGION_COORDS["부산"];
		 const query=new URLSearchParams({lat:String(pos.lat),lng:String(pos.lng),region:pos.region});
		 if(keyword)query.set("keyword",keyword);
		 window.location.assign("${pageContext.request.contextPath}/food/?"+query.toString());
		}
		function searchFoodLocation(value){
		 const input=document.getElementById("global-food-search");
		 const keyword=String(value||(input?input.value:"")).trim().replace(/\s+/g," ");
		 if(!keyword){if(input){input.focus();highlightFoodSearch();}return;}
		 const aliases={"부산광역시":"부산","서울특별시":"서울","서울시":"서울","제주도":"제주","제주특별자치도":"제주"};
		 const normalized=aliases[keyword]||keyword;
		 const words=normalized.split(" ");
		 // 첫 단어가 지역이면: '부산' / '부산 밀면' / '초량 국밥' 모두 처리
		 const region=FOOD_REGION_COORDS[words[0]]?words[0]:null;
		 const foodKeyword=region?words.slice(1).join(" "):normalized;
		 if(!document.getElementById("naver-map")){
		   // 첫 화면엔 지도가 없으므로 반드시 서버에서 지도 화면을 먼저 출력
		   navigateToFoodSearch(region||"부산",foodKeyword);
		   return;
		 }
		 if(region){
		   if(foodKeyword){navigateToFoodSearch(region,foodKeyword);return;}
		   const pos=FOOD_REGION_COORDS[region];
		   goFoodLocation(pos.lat,pos.lng,pos.region);
		   return;
		 }
		 searchFoodKeywordInCurrentRegion(normalized);
		}
		//=========================================================
		//주제: 음식 검색 API 장애 시 현재 DB/API 목록으로 대체
		//설명: 검색 요청이 실패해도 현재 지역 내 일치하는 맛집 카드를 보여줌
		//=========================================================
		function fallbackFoodKeywordSearch(keyword,region){
		 const previousNotice=document.getElementById("food-keyword-no-results");
		 if(previousNotice)previousNotice.remove();
		 const cards=Array.from(document.querySelectorAll("#list-food-container .venue-card"));
		 const matched=cards.filter(function(card){
		   const name=(card.dataset.name||"").toLowerCase();
		   const category=(card.dataset.category||"").toLowerCase();
		   return name.includes(keyword.toLowerCase())||category.includes(keyword.toLowerCase());
		 });
		 cards.forEach(function(card){card.style.display=matched.includes(card)?"":"none";});
		 const tabText=document.querySelector("#tab-food span:last-child");
		 if(tabText)tabText.textContent="맛집 ("+matched.length+")";
		 switchTab("food");
		 updateSelectedRegion(region+" · "+keyword);
		 Object.keys(mapPlaceMarkers).forEach(function(id){
		   const record=mapPlaceMarkers[id];
		   if(record&&record.marker)record.marker.setMap(matched.some(function(c){return c.id===id;})?window.foodMap:null);
		 });
		 if(matched.length){
		   const results=matched.map(function(card){return {lat:Number(card.dataset.lat),lng:Number(card.dataset.lng)};});
		   fitMapToResults(results);
		   autoSelectingDetail=true;openMapPlaceDetailByCard(matched[0]);autoSelectingDetail=false;
		 }else{
		   const box=document.getElementById("list-food-container");
		   if(box){const notice=document.createElement("p");notice.id="food-keyword-no-results";notice.className="p-4 text-center text-on-surface-variant";notice.textContent="현재 표시된 "+region+" 맛집에서 ‘"+keyword+"’ 결과를 찾지 못했어요. 다른 위치를 선택해 보세요.";box.prepend(notice);}
		 }
		}
		//=========================================================
		//주제: 음식 검색 (부산 밀면 등)
		//설명: 검색 API 결과 우선, 실패 시 현재 지역의 목록에서 이름 검색
		//=========================================================
		function searchFoodKeywordInCurrentRegion(keyword){
		 if(!document.getElementById("list-food-container")){navigateToFoodSearch("부산",keyword);return;}
		 setFoodLoading(true);
		 const regionEl=document.getElementById("selected-region-text");
		 const region=(regionEl?regionEl.textContent:"").split("·")[0].trim()||"부산";
		 const params=new URLSearchParams(window.location.search);
		 let lat=Number(params.get("lat"))||35.1150,lng=Number(params.get("lng"))||129.0420;
		 if(window.foodMap){const center=window.foodMap.getCenter();lat=center.lat();lng=center.lng();}
		 const url="${pageContext.request.contextPath}/food/searchapi?"+new URLSearchParams({region:region,keyword:keyword,lat:String(lat),lng:String(lng)}).toString();
		 fetch(url,{credentials:"same-origin"})
		  .then(async function(res){
		   const body=await res.text();
		   if(!res.ok)throw Error("searchapi HTTP "+res.status);
		   let data;try{data=JSON.parse(body);}catch(e){throw Error("searchapi JSON 응답 아님");}
		   if(data.success===false)throw Error(data.message||"searchapi 실패");
		   if(!Array.isArray(data.foodList))throw Error("searchapi 음식 목록 없음");
		   return data;
		  })
		  .then(function(data){
		   const foods=data.foodList.filter(function(f){
		    const local=!f.address||String(f.address).includes(region)||(region==="부산"&&String(f.address).includes("부산"));
		    const relevant=String((f.name||"")+" "+(f.category||"")).includes(keyword);
		    return local&&relevant;
		   });
		   if(!foods.length){fallbackFoodKeywordSearch(keyword,region);return;}
		   renderFoodList(foods);renderTourList([]);updateMapPlaces(foods,[]);
		   updateSelectedRegion(region+" · "+keyword);switchTab("food");fitMapToResults(foods);
		   const first=document.getElementById("first-food-card");
		   if(first){autoSelectingDetail=true;openMapPlaceDetailByCard(first);autoSelectingDetail=false;}
		  })
		  .catch(function(error){
		   console.warn("[맛집 검색 API 실패, 기존 목록에서 검색]",error);
		   fallbackFoodKeywordSearch(keyword,region);
		  })
		  .finally(function(){setFoodLoading(false);});
		}
		//=========================================================
		//주제: 검색창 위치 및 강조 애니메이션
		//=========================================================
		function highlightFoodSearch(){
		 const form=document.querySelector(".food-hero-search-form");
		 if(!form)return;
		 form.classList.remove("food-search-highlight");
		 void form.offsetWidth;
		 form.classList.add("food-search-highlight");
		}

		//=========================================================
		//주제: 현재 목록 필터링
		//설명: 국밥/밀면/카페 같은 음식명 검색 시 현재 보이는 카드만 필터
		//=========================================================
		function filterCurrentVenueList(keyword){
		  const foodCards=document.querySelectorAll("#list-food-container .venue-card");
		  const tourCards=document.querySelectorAll("#list-tour-container .venue-card");
		  const cards=[...foodCards,...tourCards];
		  if(cards.length===0)return false;

		  let matched=0;
		  cards.forEach(function(card){
		    const text=card.innerText.replace(/\s+/g," ");
		    const ok=text.includes(keyword);
		    card.style.display=ok?"":"none";
		    if(ok)matched++;
		  });

		  if(matched>0){
		    const first=[...cards].find(function(card){return card.style.display!== "none";});
		    if(first)first.click();
		    return true;
		  }

		  cards.forEach(function(card){card.style.display="";});
		  return false;
		}
		
		//=========================================================
		//주제: 검색어 후보 순차 검색
		//설명: 초량처럼 짧은 검색어가 실패하면 부산 초량 식으로 다시 검색
		//=========================================================
		function searchLocationCandidates(candidates,index,originalKeyword){
			if(index>=candidates.length){alert("지역으로 찾을 수 없습니다. 먼저 지역을 선택한 뒤 음식명으로 검색해주세요.");return;}
		  if(typeof naver==="undefined"||!naver.maps||!naver.maps.Service){alert("지도 검색 기능을 불러오는 중입니다.");return;}

		  naver.maps.Service.geocode({query:candidates[index]},function(status,response){
		    if(status===naver.maps.Service.Status.OK&&response.v2.addresses&&response.v2.addresses.length>0){
		      const item=response.v2.addresses[0];
		      goFoodLocation(item.y,item.x,originalKeyword);
		      return;
		    }
		    searchLocationCandidates(candidates,index+1,originalKeyword);
		  });
		}

		//=========================================================
		//주제: 지역 변경 목록 열기/닫기
		//설명: prompt 대신 서울/부산/제주 같은 버튼 목록 표시
		//=========================================================
		function toggleRegionMenu(event){
		  if(event)event.stopPropagation();
		  const menu=document.getElementById("region-menu");
		  if(menu)menu.classList.toggle("hidden");
		}

		//=========================================================
		//주제: 빠른 지역 이동
		//설명: 지역 목록 클릭 시 fetch로 해당 지역 맛집/관광지 갱신
		//=========================================================
		function selectRegionQuick(lat,lng,region){
		  const menu=document.getElementById("region-menu");
		  if(menu)menu.classList.add("hidden");
		  goFoodLocation(lat,lng,region);
		}

		//=========================================================
		//주제: 지역 목록 바깥 클릭 처리
		//설명: 다른 곳을 누르면 지역 목록 닫기
		//=========================================================
		document.addEventListener("click",function(e){
		  const menu=document.getElementById("region-menu");
		  const btn=document.getElementById("btn-region-change");
		  if(menu&&btn&&!menu.contains(e.target)&&!btn.contains(e.target))menu.classList.add("hidden");
		});

		//=========================================================
		//주제: 클릭한 좌표의 지역명 가져오기
		//설명: 지도 빈 곳 클릭 시 선택 위치 이름을 URL region 값으로 사용
		//=========================================================
		function getRegionNameFromCoord(lat,lng,callback){
		  if(!naver.maps.Service||!naver.maps.Service.reverseGeocode){callback("지도 선택 위치");return;}
		  naver.maps.Service.reverseGeocode({coords:new naver.maps.LatLng(lat,lng),orders:[naver.maps.Service.OrderType.ADDR,naver.maps.Service.OrderType.ROAD_ADDR].join(",")},function(status,response){
		    if(status!==naver.maps.Service.Status.OK||!response.v2.results||response.v2.results.length===0){callback("지도 선택 위치");return;}
		    const r=response.v2.results[0].region;
		    const name=[r.area1&&r.area1.name,r.area2&&r.area2.name,r.area3&&r.area3.name].filter(Boolean).join(" ");
		    callback(name||"지도 선택 위치");
		  });
		}
		
		//=========================================================
		//주제: 네이버 지도 초기화
		//설명: URL의 lat/lng 위치를 중심으로 지도 표시 후 맛집/관광지 마커 생성
		//=========================================================
		function initNaverMap(){
		  const mapEl=document.getElementById("naver-map");
		  if(!mapEl)return;
		  if(typeof naver==="undefined"||!naver.maps){console.log("네이버 지도 API가 로드되지 않았습니다.");return;}

		  const params=new URLSearchParams(window.location.search);
		  const lat=parseFloat(params.get("lat"))||35.1150;
		  const lng=parseFloat(params.get("lng"))||129.0420;
		  const center=new naver.maps.LatLng(lat,lng);

		  const map=new naver.maps.Map("naver-map",{center:center,zoom:15,minZoom:8,zoomControl:true,zoomControlOptions:{position:naver.maps.Position.TOP_RIGHT}});
		  window.foodMap=map;

		  selectedCenterMarker=new naver.maps.Marker({position:center,map:map,title:"선택한 위치"});

		  addPlaceMarkers(map,foodMapPlaces);
		  addPlaceMarkers(map,tourMapPlaces);

		  naver.maps.Event.addListener(map,"click",function(e){
			  const point=e.coord||e.latlng;
			  if(!point)return;
			  const clickedLat=point.lat();
			  const clickedLng=point.lng();
			  getRegionNameFromCoord(clickedLat,clickedLng,function(region){
			    goFoodLocation(clickedLat,clickedLng,region);
			  });
			});
		}

		//=========================================================
		//주제: 오른쪽 목록에서 장소 선택
		//설명: 지도 마커를 강조하고 상세정보를 갱신하되 화면은 스크롤하지 않음
		//=========================================================
		function selectPlaceFromList(card){
		  if(!card)return;
		  const wasAutoSelecting=autoSelectingDetail;
		  autoSelectingDetail=true;
		  try{openMapPlaceDetailByCard(card);}
		  finally{autoSelectingDetail=wasAutoSelecting;}
		}

		//=========================================================
		//주제: 지도 마커 클릭 시 상세정보 열기
		//설명: 지도 마커 클릭은 기존처럼 내용 변경 후 상세정보로 스크롤
		//=========================================================
		function openMapPlaceDetailByCard(card){
		  if(!card)return;
		  focusMapMarkerByCardId(card.id);
		  const d=card.dataset;
		  if(card.closest("#list-food-container"))showFoodDetailFromCard(card);
		  else if(card.closest("#list-tour-container"))showTourDetail(d.contentId||"",d.contentTypeId||"12",d.name||"",d.address||"",d.image||"",d.category||"관광지",d.distance||"");
		}
		//=========================================================
		//주제: 맛집/관광지 마커 생성
		//설명: hover 시 이름과 확대 효과, 클릭 시 지도 아래 상세정보 이동
		//=========================================================
		function addPlaceMarkers(map,places){
		  if(!places||places.length===0)return;
		  places.forEach(function(place){
		    const marker=new naver.maps.Marker({position:new naver.maps.LatLng(place.lat,place.lng),map:map,icon:makePlaceMarkerIcon(place,false),zIndex:100});
		    mapPlaceMarkers[place.cardId]={marker:marker,place:place};
		    naver.maps.Event.addListener(marker,"mouseover",function(){marker.setZIndex(1000);});
		    naver.maps.Event.addListener(marker,"mouseout",function(){marker.setZIndex(activeMarkerCardId===place.cardId?500:100);});
		    naver.maps.Event.addListener(marker,"click",function(){openMapPlaceDetailByCard(document.getElementById(place.cardId));});
		  });
		}
	
  // Tab Switching Logic between 맛집 and 관광지

  function switchTab(type) {

    const tabFood = document.getElementById('tab-food');

    const tabTour = document.getElementById('tab-tour');

    const listFood = document.getElementById('list-food-container');

    const listTour = document.getElementById('list-tour-container');



    if (type === 'food') {

      tabFood.className = "flex items-center justify-center gap-2 py-2.5 rounded-lg font-label-lg text-label-lg font-bold transition-all bg-primary text-on-primary shadow-sm";

      tabTour.className = "flex items-center justify-center gap-2 py-2.5 rounded-lg font-label-lg text-label-lg font-semibold transition-all text-on-surface-variant hover:text-on-surface hover:bg-surface-container-lowest/60";

      listFood.classList.remove('hidden');

      listTour.classList.add('hidden');

    } else {

      tabTour.className = "flex items-center justify-center gap-2 py-2.5 rounded-lg font-label-lg text-label-lg font-bold transition-all bg-primary text-on-primary shadow-sm";

      tabFood.className = "flex items-center justify-center gap-2 py-2.5 rounded-lg font-label-lg text-label-lg font-semibold transition-all text-on-surface-variant hover:text-on-surface hover:bg-surface-container-lowest/60";

      listTour.classList.remove('hidden');

      listFood.classList.add('hidden');

    }

  }



//=========================================================

//주제: 줄바꿈 태그를 보기 좋게 정리

//설명: <br>, <br/>, 줄바꿈 문자를 실제 줄바꿈으로 보여줌

//=========================================================

function formatHtmlText(text) {

   if (!text || text.trim() === "") {

       return "정보 없음";

   }



   return text

       .replace(/<br\s*\/?>/gi, "<br>")

       .replace(/\r\n|\r|\n/g, "<br>");

}





	//=========================================================

	//주제: 휴무일/추가 설명 텍스트 정리

	//설명: / 기준으로 나눠서 더 읽기 쉽게 표시

	//=========================================================

	function formatNoteText(text) {

	   if (!text || text.trim() === "") {

	       return "정보 없음";

	   }



	   return text

	       .replace(/<br\s*\/?>/gi, "<br>")

	       .replace(/\s*\/\s*/g, "<br>")

	       .replace(/\r\n|\r|\n/g, "<br>");

	}

/* fetch('/Travel_project/food/foodapi?lat=35.115&lng=129.042')

  .then(response => response.json())

  .then(data => {

      console.log(data);

  })

  .catch(error => {

      console.error('오류:', error);

  });

*/

  // Card Selection & Highlight

  function selectVenue(id) {

    // 1. Remove highlight classes from all cards

    for (let i = 1; i <= 4; i++) {

      const card = document.getElementById("card-venue-"+i);

      const marker = document.getElementById("marker-"+i);

      if (card) {

        card.classList.remove('bg-surface-container-low');

      }

      if (marker) {

        // Reset marker appearance

        const pill = marker.querySelector('div > div');

        if (pill) {

          pill.className = "flex items-center gap-1.5 px-2.5 py-1 rounded-full bg-surface-container-lowest text-on-surface shadow-md font-label-sm text-label-sm font-semibold whitespace-nowrap";

        }

      }

    }



    // 2. Highlight clicked card

    const targetCard = document.getElementById("card-venue-"+id);

    if (targetCard) {

      targetCard.classList.add('bg-surface-container-low');

    }



    // 3. Highlight corresponding map marker

    const activeMarker = document.getElementById("marker-"+id);

    if (activeMarker) {

      const activePill = activeMarker.querySelector('div > div');

      if (activePill) {

        activePill.className = "flex items-center gap-1.5 px-3 py-1.5 rounded-full bg-primary text-on-primary shadow-lg font-label-md text-label-md font-bold whitespace-nowrap scale-105";

      }

    }



    // 4. Update the Selected Detail Section

    const data = venueData[id];

    if (data) {

      const titleEl = document.getElementById('detail-title');

      const saveBtnText = document.getElementById('save-btn-text');

      if (titleEl) titleEl.innerText = data.title;

    }

  }



//=========================================================
//주제: 맛집·관광지 서버 찜 기능
//설명: 미로그인 안내, FoodLike/TourLike 연동, 하트 동기화
//=========================================================
const FOODLIKE_URL="${pageContext.request.contextPath}/FoodLike";
const TOURLIKE_URL="${pageContext.request.contextPath}/TourLike";
const LIKE_LOGGED_IN=<%=session.getAttribute("loginId")!=null%>;
const dbLikeState=new Map(),dbLikeLoading=new Set(),dbLikePending=new Set(),dbLikeVersion=new Map();
const tourLikeState=new Map(),tourLikeLoading=new Set(),tourLikePending=new Set(),tourLikeVersion=new Map();
let selectedFavoriteType="",selectedFoodId=0,selectedTourContentId="",selectedTourData=null,selectedFoodData=null;
let foodLikeLoginRequired=false,tourLikeLoginRequired=false;

//=========================================================
//주제: 비로그인 찜하기 안내 팝업
//설명: 로그인 안내를 중앙 모달로 표시 (닫기/Esc/바깥 클릭 가능)
//=========================================================
//주제: 로그인 필요/DB 미등록 안내를 같은 디자인의 팝업으로 표시
function showLikeNotice(type){
 const dialog=document.getElementById("like-login-dialog");
 if(!dialog)return;
 const title=dialog.querySelector(".like-modal-title");
 const desc=dialog.querySelector(".like-modal-desc");
 const kicker=dialog.querySelector(".like-modal-kicker");
 const login=dialog.querySelector(".like-modal-login");
 const later=dialog.querySelector(".like-modal-later");
 const note=dialog.querySelector(".like-modal-note");
 if(type==="login"){
  kicker.textContent="MY TRAVEL FAVORITES";
  title.innerHTML="찜하기는 로그인 후<br>이용할 수 있어요.";
  desc.innerHTML="마음에 드는 맛집과 관광지를 저장하고,<br>마이페이지에서 한 번에 확인해 보세요.";
  //=========================================================
  //주제: 로그인 후 음식 페이지 복귀
  //설명: 음식 페이지의 현재 좌표·검색어를 로그인 컨트롤러에 전달
  //=========================================================
  login.href="${pageContext.request.contextPath}/Login?returnTo="+encodeURIComponent(window.location.pathname+window.location.search);
  login.style.display="flex";later.textContent="다음에 할게요";
  note.textContent="로그인 전에는 찜한 장소가 저장되지 않습니다.";
 }else{
  const tour=type==="tour";
  kicker.textContent="FAVORITES NOTICE";
  title.innerHTML=tour?"이 관광지는 아직<br>찜할 수 없어요.":"이 맛집은 아직<br>찜할 수 없어요.";
  desc.innerHTML=tour?"관광지 식별 정보가 없어 저장할 수 없어요.<br>다른 관광지를 선택해 주세요.":"이 음식점의 이름이나 장소 식별 정보가 부족해요.<br>다른 음식점을 선택해 주세요.";
  login.style.display="none";later.textContent="확인";
  note.textContent="이름과 위치를 확인할 수 있는 장소만 저장할 수 있습니다.";
 }
 if(typeof dialog.showModal==="function"){
  if(!dialog.open)dialog.showModal();
 }else{dialog.setAttribute("open","");}
}
function showLikeLoginNotice(){showLikeNotice("login");}
function showLikeUnavailableNotice(type){showLikeNotice(type);}

//=========================================================
//주제: 찜 저장·취소 성공 안내
//설명: 서버가 성공을 반환한 후에만 2.8초 동안 문구 표시
//=========================================================
let foodLikeToastTimer=null;
function showFoodLikeToast(liked){
 const toast=document.getElementById("food-like-toast");if(!toast)return;
 const text=toast.querySelector(".food-like-toast-text");
 const icon=toast.querySelector(".food-like-toast-icon .material-symbols-outlined");
 if(text)text.textContent=liked?"찜 목록에 저장되었습니다.":"찜 목록에서 삭제되었습니다.";
 if(icon)icon.textContent=liked?"favorite":"favorite_border";
 toast.classList.toggle("is-removed",!liked);
 toast.classList.remove("is-visible");
 void toast.offsetWidth; // 같은 장소를 연속 클릭해도 등장 애니메이션 다시 실행
 toast.classList.add("is-visible");
 if(foodLikeToastTimer!==null)clearTimeout(foodLikeToastTimer);
 foodLikeToastTimer=setTimeout(function(){toast.classList.remove("is-visible");foodLikeToastTimer=null;},2800);
}


document.addEventListener("DOMContentLoaded",function(){
 const dialog=document.getElementById("like-login-dialog");
 if(!dialog)return;
 dialog.querySelectorAll("[data-like-login-close]").forEach(function(btn){
  btn.addEventListener("click",function(){
   if(typeof dialog.close==="function")dialog.close();
   else dialog.removeAttribute("open");
  });
 });
 dialog.addEventListener("click",function(e){
  if(e.target===dialog){
   if(typeof dialog.close==="function")dialog.close();
   else dialog.removeAttribute("open");
  }
 });
});

//=========================================================
//주제: 화면의 맛집 정보 수집
//설명: foodId=0인 API 결과도 이름/주소/contentId로 찾을 수 있게 구성
//=========================================================
const foodExternalLikeState=new Map(),foodExternalLikeLoading=new Set(),foodExternalLikePending=new Set(),foodExternalLikeVersion=new Map();
function getCardFoodId(card){return Number(card.dataset.foodId||0);}
function getCardFoodInfo(card){
 const d=card.dataset;
 return {foodId:Number(d.foodId||0),contentId:d.contentId||"",name:d.name||"",address:d.address||"",
  category:d.category||"맛집",image:d.image||"",phone:d.phone||"",lat:d.lat||"",lng:d.lng||""};
}
function foodInfoKey(info){
 const cid=String(info?.contentId||"").trim();
 return cid?"c:"+cid:"n:"+String(info?.name||"").trim().toLowerCase()+"|"+String(info?.address||"").trim().toLowerCase();
}
function foodInfoValid(info){return !!(info?.name&&(info.contentId||info.address));}
function getCardTourData(card){
 return {contentId:card.dataset.contentId||"",name:card.dataset.name||card.querySelector("h3")?.textContent||"",
  address:card.dataset.address||card.querySelector("p")?.textContent||"",
  category:card.dataset.category||"관광지",image:card.dataset.image||card.querySelector("img")?.src||""};
}
//=========================================================
//주제: 카드와 상세정보의 하트 표시 통일
//설명: 아이콘 글꼴의 FILL 지원 여부와 무관하게 SVG로 채움/비움 표시
//=========================================================
function paintFavorite(btn,saved){
 if(!btn)return;
 let icon=btn.querySelector(".favorite-heart-svg");
 if(!icon){
  const old=btn.querySelector(".material-symbols-outlined");
  const svg='<svg class="favorite-heart-svg shrink-0" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78Z"/></svg>';
  if(old)old.outerHTML=svg;else btn.insertAdjacentHTML("afterbegin",svg);
  icon=btn.querySelector(".favorite-heart-svg");
 }
 if(icon){
  icon.setAttribute("fill",saved?"currentColor":"none");
  icon.setAttribute("stroke-width",saved?"1.6":"2.2");
  icon.style.color=btn.id==="btn-detail-save"?"":"#ff6b5b";
 }
 btn.dataset.saved=saved?"true":"false";
 btn.setAttribute("aria-pressed",String(saved));
 btn.setAttribute("aria-label",saved?"찜 취소":"찜하기");
 btn.title=saved?"찜 취소":"찜하기";
}
function updateResolvedFoodId(info,id,liked){
 if(!Number.isInteger(id)||id<=0)return;
 const key=foodInfoKey(info);
 dbLikeState.set(id,!!liked);foodExternalLikeState.set(key,!!liked);
 document.querySelectorAll("#list-food-container .venue-card").forEach(card=>{
  if(foodInfoKey(getCardFoodInfo(card))===key)card.dataset.foodId=String(id);
 });
 if(selectedFoodData&&foodInfoKey(selectedFoodData)===key){selectedFoodId=id;selectedFoodData.foodId=id;}
}
//주제: 기존 DB 맛집 찜 상태 조회
function loadDbLike(foodId){
 if(!LIKE_LOGGED_IN||!Number.isInteger(foodId)||foodId<=0||foodLikeLoginRequired||dbLikeState.has(foodId)||dbLikeLoading.has(foodId))return;
 dbLikeLoading.add(foodId);const version=dbLikeVersion.get(foodId)||0;
 fetch(FOODLIKE_URL+"?foodId="+encodeURIComponent(foodId),{credentials:"same-origin"})
 .then(async r=>{if(r.status===401){foodLikeLoginRequired=true;return null;}if(!r.ok)throw Error("조회 오류: "+r.status);return r.json();})
 .then(data=>{if(!data||!data.success||dbLikePending.has("id:"+foodId)||(dbLikeVersion.get(foodId)||0)!==version)return;
  dbLikeState.set(foodId,!!data.liked);refreshFavoriteButtons();refreshDetailFavorite();})
 .catch(e=>console.warn("[FoodLike 조회]",e)).finally(()=>dbLikeLoading.delete(foodId));
}
//주제: foodId가 없는 API 맛집 DB 조회 (조회 중 자동 등록하지 않음)
function loadDbExternalFoodLike(info){
 if(!LIKE_LOGGED_IN||foodLikeLoginRequired||!foodInfoValid(info))return;
 const key=foodInfoKey(info);
 if(foodExternalLikeState.has(key)||foodExternalLikeLoading.has(key)||foodExternalLikePending.has(key))return;
 foodExternalLikeLoading.add(key);const version=foodExternalLikeVersion.get(key)||0;
 const params=new URLSearchParams({foodId:"0",contentId:info.contentId||"",name:info.name||"",address:info.address||""});
 fetch(FOODLIKE_URL+"?"+params.toString(),{credentials:"same-origin"})
 .then(async r=>{if(r.status===401){foodLikeLoginRequired=true;return null;}if(!r.ok)throw Error("조회 오류: "+r.status);return r.json();})
 .then(data=>{
  if(!data||!data.success||foodExternalLikePending.has(key)||(foodExternalLikeVersion.get(key)||0)!==version)return;
  foodExternalLikeState.set(key,!!data.liked);
  if(Number(data.foodId)>0)updateResolvedFoodId(info,Number(data.foodId),data.liked);
  refreshFavoriteButtons();refreshDetailFavorite();
 })
 .catch(e=>console.warn("[FoodLike API 맛집 조회]",e)).finally(()=>foodExternalLikeLoading.delete(key));
}
//주제: 관광지 DB 찜 상태 조회
function loadDbTourLike(contentId){
 if(!LIKE_LOGGED_IN||!contentId||tourLikeLoginRequired||tourLikeState.has(contentId)||tourLikeLoading.has(contentId))return;
 tourLikeLoading.add(contentId);const version=tourLikeVersion.get(contentId)||0;
 fetch(TOURLIKE_URL+"?contentId="+encodeURIComponent(contentId),{credentials:"same-origin"})
 .then(async r=>{if(r.status===401){tourLikeLoginRequired=true;return null;}if(!r.ok)throw Error("조회 오류: "+r.status);return r.json();})
 .then(data=>{if(!data||!data.success||tourLikePending.has(contentId)||(tourLikeVersion.get(contentId)||0)!==version)return;
  tourLikeState.set(contentId,!!data.liked);refreshFavoriteButtons();refreshDetailFavorite();})
 .catch(e=>console.warn("[TourLike 조회]",e)).finally(()=>tourLikeLoading.delete(contentId));
}
//주제: 카드 하트 표시와 조회
function refreshFavoriteButtons(){
 document.querySelectorAll("#list-food-container .venue-card,#list-tour-container .venue-card").forEach(card=>{
  const tour=!!card.closest("#list-tour-container");let btn=card.querySelector(".favorite-btn");
  if(tour&&!btn){
   card.style.position="relative";btn=document.createElement("button");btn.type="button";
   btn.className="favorite-btn absolute top-3 right-3 z-10 p-1 hover:scale-110 transition-transform";
   btn.innerHTML='<span class="material-symbols-outlined text-[20px]">favorite</span>';card.appendChild(btn);
  }
  if(!btn)return;
  if(tour){
   const cid=getCardTourData(card).contentId;
   paintFavorite(btn,LIKE_LOGGED_IN&&tourLikeState.get(cid)===true);
   if(cid)loadDbTourLike(cid);else btn.title="관광지 ID가 없어 찜할 수 없습니다.";
  }else{
   const info=getCardFoodInfo(card),key=foodInfoKey(info);
   const saved=info.foodId>0?dbLikeState.get(info.foodId)===true:foodExternalLikeState.get(key)===true;
   paintFavorite(btn,LIKE_LOGGED_IN&&saved);
   if(info.foodId>0)loadDbLike(info.foodId);else if(foodInfoValid(info))loadDbExternalFoodLike(info);
   else btn.title="음식점 이름/주소 정보가 없어 찜할 수 없습니다.";
  }
 });
}
//주제: 상세정보 하트 표시
//=========================================================
//주제: 상세정보 저장 버튼 표시 갱신
//설명: 카드 하트와 동일한 SVG 사용
//=========================================================
function refreshDetailFavorite(){
 const btn=document.getElementById("btn-detail-save"),label=document.getElementById("save-btn-text");
 if(!btn||!label)return;
 const saved=selectedFavoriteType==="food"?(selectedFoodId>0?dbLikeState.get(selectedFoodId)===true:
  !!selectedFoodData&&foodExternalLikeState.get(foodInfoKey(selectedFoodData))===true):
  tourLikeState.get(selectedTourContentId)===true;
 const active=LIKE_LOGGED_IN&&saved;
 btn.classList.toggle("bg-secondary",active);btn.classList.toggle("text-on-secondary",active);
 btn.classList.toggle("bg-surface-container",!active);btn.classList.toggle("text-on-surface",!active);
 paintFavorite(btn,active);
 label.textContent=active?"찜 완료":"찜하기";
}
//주제: 선택한 장소 정보 저장
function selectFavorite(type,name,address,foodId,contentId){
 selectedFavoriteType=type;selectedFoodId=type==="food"?Number(foodId||0):0;
 selectedTourContentId=type==="tour"?String(contentId||""):"";
 refreshDetailFavorite();
 if(selectedFoodId>0)loadDbLike(selectedFoodId);
 else if(type==="food"&&selectedFoodData)loadDbExternalFoodLike(selectedFoodData);
 if(selectedTourContentId)loadDbTourLike(selectedTourContentId);
}
//주제: 모든 맛집 찜 저장/취소 (foodId가 0이면 서버에서 자동 등록)
async function toggleDbLike(input){
 if(!LIKE_LOGGED_IN||foodLikeLoginRequired){showLikeLoginNotice();return;}
 const info=typeof input==="number"?{foodId:input}:input;
 if(!info||(!info.foodId&&!foodInfoValid(info))){showLikeUnavailableNotice("food");return;}
 const fid=Number(info.foodId||0),key=foodInfoKey(info),pendingKey=fid>0?"id:"+fid:"api:"+key;
 if(dbLikePending.has(pendingKey)||foodExternalLikePending.has(key))return;
 dbLikePending.add(pendingKey);foodExternalLikePending.add(key);
 foodExternalLikeVersion.set(key,(foodExternalLikeVersion.get(key)||0)+1);
 if(fid>0)dbLikeVersion.set(fid,(dbLikeVersion.get(fid)||0)+1);
 // 클릭 직후 하트 표시부터 바꾸고, 저장 실패 시 원래 상태로 복구
 const previous=fid>0?dbLikeState.get(fid):foodExternalLikeState.get(key);
 if(fid>0)dbLikeState.set(fid,!previous);
 foodExternalLikeState.set(key,!previous);
 refreshFavoriteButtons();refreshDetailFavorite();
 let completed=false;
 try{
  const r=await fetch(FOODLIKE_URL,{method:"POST",credentials:"same-origin",
   headers:{"Content-Type":"application/x-www-form-urlencoded;charset=UTF-8"},
   body:new URLSearchParams({foodId:String(fid),contentId:info.contentId||"",name:info.name||"",
    address:info.address||"",category:info.category||"맛집",image:info.image||"",phone:info.phone||"",
    lat:String(info.lat||""),lng:String(info.lng||"")})});
  if(r.status===401){foodLikeLoginRequired=true;showLikeLoginNotice();return;}
  const data=await r.json();if(!r.ok||!data.success)throw Error(data.message||"DB 저장 실패");
  if(Number(data.foodId)>0)updateResolvedFoodId(info,Number(data.foodId),data.liked);
  else if(fid>0)dbLikeState.set(fid,!!data.liked);
  foodExternalLikeState.set(key,!!data.liked);
  completed=true;
  showFoodLikeToast(!!data.liked);
 }catch(e){console.error("[FoodLike 저장]",e);alert("맛집 찜 처리 실패: "+e.message);}
 finally{
  if(!completed){
   if(fid>0){if(previous===undefined)dbLikeState.delete(fid);else dbLikeState.set(fid,previous);}
   if(previous===undefined)foodExternalLikeState.delete(key);else foodExternalLikeState.set(key,previous);
  }
  dbLikePending.delete(pendingKey);foodExternalLikePending.delete(key);
  refreshFavoriteButtons();refreshDetailFavorite();
 }
}
//주제: 관광지 찜/취소 (기존 연동 유지)
async function toggleTourLike(info){
 if(!LIKE_LOGGED_IN||tourLikeLoginRequired){showLikeLoginNotice();return;}
 const cid=String(info?.contentId||"");if(!cid){showLikeUnavailableNotice("tour");return;}
 if(tourLikePending.has(cid))return;tourLikePending.add(cid);
 tourLikeVersion.set(cid,(tourLikeVersion.get(cid)||0)+1);
 const previous=tourLikeState.get(cid);tourLikeState.set(cid,!previous);
 refreshFavoriteButtons();refreshDetailFavorite();
 let completed=false;
 try{
  const r=await fetch(TOURLIKE_URL,{method:"POST",credentials:"same-origin",
   headers:{"Content-Type":"application/x-www-form-urlencoded;charset=UTF-8"},
   body:new URLSearchParams({contentId:cid,name:info.name||"",address:info.address||"",category:info.category||"관광지",image:info.image||""})});
  if(r.status===401){tourLikeLoginRequired=true;showLikeLoginNotice();return;}
  const data=await r.json();if(!r.ok||!data.success)throw Error(data.message||"DB 저장 실패");
  tourLikeState.set(cid,!!data.liked);completed=true;
  showFoodLikeToast(!!data.liked);
 }catch(e){console.error("[TourLike 저장]",e);alert("관광지 찜 처리에 실패했습니다.");}
 finally{
  if(!completed){if(previous===undefined)tourLikeState.delete(cid);else tourLikeState.set(cid,previous);}
  tourLikePending.delete(cid);refreshFavoriteButtons();refreshDetailFavorite();
 }
}
//주제: 상세정보 하트 클릭
function toggleDetailSave(btn){
 if(selectedFavoriteType==="food")toggleDbLike(selectedFoodData||{foodId:selectedFoodId});
 else if(selectedFavoriteType==="tour")toggleTourLike(selectedTourData||{contentId:selectedTourContentId});
}
//주제: 카드 하트 클릭 (상세영역 이동 방지)
document.addEventListener("click",function(e){
 const btn=e.target.closest(".favorite-btn");if(!btn)return;
 const card=btn.closest(".venue-card");if(!card||!card.closest("#list-food-container,#list-tour-container"))return;
 e.preventDefault();e.stopPropagation();
 if(card.closest("#list-tour-container"))toggleTourLike(getCardTourData(card));else toggleDbLike(getCardFoodInfo(card));
},true);
//주제: 검색·지역 변경시 하트 상태 복구
document.addEventListener("DOMContentLoaded",function(){
 ["list-food-container","list-tour-container"].forEach(id=>{
  const box=document.getElementById(id);if(box)new MutationObserver(refreshFavoriteButtons).observe(box,{childList:true});
 });
 refreshFavoriteButtons();refreshDetailFavorite();
});

  // Map Filter Legend Click Toggles

  const mapLegendFood=document.getElementById('map-legend-food');
if(mapLegendFood){mapLegendFood.addEventListener('click',function(){
  const markers=[document.getElementById('marker-1'),document.getElementById('marker-2'),document.getElementById('marker-3'),document.getElementById('marker-4')];
  markers.forEach(m=>{if(m)m.classList.toggle('hidden');});
  this.classList.toggle('opacity-50');
});}

  const mapLegendTour=document.getElementById('map-legend-tour');
if(mapLegendTour){mapLegendTour.addEventListener('click',function(){
  const tourMarkers=[document.getElementById('marker-tour-1'),document.getElementById('marker-tour-2'),document.getElementById('marker-tour-3')];
  tourMarkers.forEach(m=>{if(m)m.classList.toggle('hidden');});
  this.classList.toggle('opacity-50');
});}

	//=========================================================
	//주제: 첫 화면 지역 검색 및 지도 중심으로 다시 조회
	//설명: 첫 화면은 검색창으로 이동하고, 지도가 열린 뒤에는 지도 중심으로 목록 갱신
	//=========================================================
	//첫 화면: 검색창으로 이동해 지역명을 입력하도록 안내 (GPS 요청 없음)
	const introRegionSearchBtn=document.getElementById("introRegionSearchBtn");
	if(introRegionSearchBtn)introRegionSearchBtn.addEventListener("click",function(){
	 const input=document.getElementById("global-food-search");
	 if(!input)return;
	 input.focus({preventScroll:true});
	 input.scrollIntoView({behavior:"smooth",block:"center"});
	 highlightFoodSearch();
	});
	//지도 화면: 현재 화면에 표시된 지도 중심 좌표를 기준으로 다시 조회
	const nearbyFoodBtn=document.getElementById("nearbyFoodBtn");
	if(nearbyFoodBtn)nearbyFoodBtn.addEventListener("click",function(){
	 if(!window.foodMap){alert("지도를 불러오는 중입니다. 잠시 후 다시 시도해주세요.");return;}
	 const center=window.foodMap.getCenter();
	 const regionEl=document.getElementById("selected-region-text");
	 const region=regionEl?regionEl.innerText:"지도 선택 위치";
	 goFoodLocation(center.lat(),center.lng(),region);
	});

	//=========================================================
	//주제: 조회중 메시지 표시
	//설명: 목록 fetch 중일 때 지역변경 옆에 안내 문구 표시
	//=========================================================
	function setFoodLoading(isLoading){
	  const msg=document.getElementById("food-loading-msg");
	  if(!msg)return;
	  msg.classList.toggle("hidden",!isLoading);
	}

//=========================================================
//주제: 상세 평점 표시
//설명: 맛집은 평점이 있을 때만 ★ 표시, 없으면 숨김
//=========================================================
function updateDetailRatingForFood(rating){
const wrap=document.getElementById("detail-rating-wrap");
const ratingEl=document.getElementById("detail-rating");
const reviewEl=document.getElementById("detail-review");
const score=Number(rating||0);
if(reviewEl){reviewEl.innerText="";reviewEl.style.display="none";}
if(!wrap||!ratingEl)return;
if(score>0){wrap.style.display="inline-flex";ratingEl.innerText=score.toFixed(1);}
else{wrap.style.display="none";ratingEl.innerText="";}
}

//=========================================================
//주제: 관광지 평점 숨김
//설명: 관광지는 평점 데이터를 쓰지 않으므로 ★ 영역 제거
//=========================================================
function hideDetailRatingForTour(){
const wrap=document.getElementById("detail-rating-wrap");
const reviewEl=document.getElementById("detail-review");
if(wrap)wrap.style.display="none";
if(reviewEl){reviewEl.innerText="";reviewEl.style.display="none";}
}

//=========================================================
//주제: API 텍스트 정리
//설명: API에서 온 <br>, &nbsp; 같은 값을 화면용 텍스트로 정리
//=========================================================

function cleanApiText(text, fallback) {
   if (!text || text.trim() === "" || text === "null" || text === "undefined") {
       return fallback;
   }

   return text
       .replace(/&lt;br\s*\/?&gt;/gi, "\n")
       .replace(/<br\s*\/?>/gi, "\n")
       .replace(/\\\n/g, "\n")
       .replace(/&nbsp;/gi, " ")
       .replace(/&amp;/gi, "&")
       .replace(/&lt;/gi, "<")
       .replace(/&gt;/gi, ">")
       .trim();
}

//=========================================================
//주제: 보조설명 텍스트 정리
//설명: 휴무일처럼 / 로 이어진 문장을 줄바꿈 처리
//=========================================================
function cleanNoteText(text, fallback) {
   const cleaned =
       cleanApiText(text, fallback);
   
   if (!cleaned || cleaned === fallback) {
       return fallback;
   }
   return cleaned
       .replace(/\s*\/\s*/g, "\n")
       .trim();
}

//=========================================================

//주제: 여러 줄 텍스트 넣기

//설명: 줄바꿈이 화면에 보이도록 white-space 설정

//=========================================================

function setMultilineText(elementId, text, fallback) {
   const element =
       document.getElementById(elementId);
   if (!element) {
	 return;
   }
   element.innerText =
       cleanApiText(text, fallback);
   element.style.whiteSpace =
       "pre-line";
}

//=========================================================
//주제: 여러 줄 보조설명 넣기
//설명: 휴무일/브레이크타임/메뉴 설명을 깔끔하게 표시
//=========================================================

function setMultilineNote(elementId, text, fallback) {
   const element =
       document.getElementById(elementId);
   
   if (!element) {
       return;
   }
   element.innerText =
       cleanNoteText(text, fallback);
   element.style.whiteSpace =
       "pre-line";
}

	//=========================================================
	//주제: 상세 설명 한 줄 요약
	//설명: 설명이 너무 길면 상단에는 짧게 자르고, 없으면 기본 문구 표시
	//=========================================================
	function makeShortText(text,fallback){
		const cleaned=cleanApiText(text,fallback);
		if(!cleaned||cleaned===fallback) return fallback;
		return cleaned.length>85?cleaned.substring(0,85)+"...":cleaned;
		}

	// =========================================================
	// 주제: 음식점 기본 설명 생성
	// 설명: DB/API 설명이 없을 때 이름/주소/대표메뉴로 자연스러운 안내문 생성
	// =========================================================
	function makeFoodFallbackText(name,address,menu){
	  const foodName=name||"선택한 맛집";
	  const foodAddress=address&&address.trim()!==""?address:"선택한 지역 주변";
	  const cleanMenu=menu&&menu.trim()!==""?cleanApiText(menu,""):"";
	  return foodName+"은(는) "+foodAddress+"에 위치한 맛집입니다."+(cleanMenu?"\n대표 메뉴는 "+cleanMenu+"입니다.":"");
	}

	// =========================================================
	// 주제: 대표 메뉴 첫 번째 항목 추출
	// 설명: 메뉴가 여러 개일 때 이름 밑에는 첫 번째 대표 메뉴만 짧게 표시
	// =========================================================
	function getFirstMenuText(menu){
	  const cleaned=cleanApiText(menu||"","").replace(/\s+/g," ").trim();
	  if(!cleaned)return "";
	  return cleaned.split(/\s*\/\s*|\n|,/)[0].trim();
	}

	// =========================================================
	// 주제: 음식점 상단 요약문 생성
	// 설명: 긴 설명 대신 "가게명 · 대표 메뉴 OOO" 형태로 통일
	// =========================================================
	function makeFoodSubtitle(name,address,menu){
	  const foodName=name||"선택한 맛집";
	  const firstMenu=getFirstMenuText(menu);
	  if(firstMenu)return foodName+" · 대표 메뉴 "+firstMenu;
	  return foodName+" · "+(address&&address.trim()!==""?address:"주변 맛집");
	}
	
	//=========================================================
	//주제: 네이버 지도 링크 설정
	//설명: 외부 사이트/인스타로 빠지지 않게 항상 네이버 지도 검색으로 연결
	//=========================================================
	function updateNaverReviewLink(name,address,naverLink){
	  const btn=document.getElementById("btn-naver-review");
	  if(!btn)return;
	  const keyword=((name||"")+" "+(address||"")).trim();
	  btn.href="https://map.naver.com/p/search/"+encodeURIComponent(keyword||"맛집");
	}
	
	//=========================================================
	//주제: 지도 마커 클릭 시 상세정보 스크롤 위치
	//설명: 캡처처럼 상단 메뉴 아래로 여백을 두고 상세정보 카드 표시
	//=========================================================
	function scrollToSelectedDetailAtOffset(){
	 if(autoSelectingDetail)return;
	 const section=document.getElementById("selected-detail-section");
	 if(!section)return;
	 requestAnimationFrame(function(){
	  // 1. 고정/스티키 헤더가 있으면 현재 화면상의 하단 좌표를 반영
	  const header=Array.from(document.querySelectorAll("header")).find(function(el){
	   const pos=getComputedStyle(el).position;
	   return pos==="fixed"||pos==="sticky";
	  });
	  const rect=header?header.getBoundingClientRect():null;
	  const headerBottom=rect&&rect.top<=5&&rect.bottom>0?Math.min(100,rect.bottom):(window.innerWidth<=767?60:70);
	  // 2. PC: 헤더 아래 약 95px / 모바일: 약 24px 여백
	  const idealGap=headerBottom+(window.innerWidth<=767?24:95);
	  // 3. 상세 카드가 화면 아래로 밀리지 않도록 화면 높이에 맞춰 보정
	  const detailHeight=section.getBoundingClientRect().height;
	  const availableGap=window.innerHeight-detailHeight-(window.innerWidth<=767?16:35);
	  const topGap=Math.max(headerBottom+16,Math.min(idealGap,availableGap));
	  const absoluteTop=window.scrollY+section.getBoundingClientRect().top;
	  const maxScroll=Math.max(0,document.documentElement.scrollHeight-window.innerHeight);
	  const targetTop=Math.max(0,Math.min(maxScroll,absoluteTop-topGap));
	  window.scrollTo({top:targetTop,behavior:window.matchMedia("(prefers-reduced-motion: reduce)").matches?"auto":"smooth"});
	 });
	}

	//=========================================================
	//주제: 맛집 선택 및 API 상세정보 조회
	//설명: 카드 선택 후 운영시간, 전화번호, 메뉴를 추가로 불러옴
	//=========================================================
	let foodIntroRequestSeq=0;
	function showFoodDetailFromCard(card){
	 const d=card.dataset;
	 selectedFoodData=getCardFoodInfo(card);
	 const seq=++foodIntroRequestSeq;
	 showFoodDetail(d.name,d.address,d.phone,d.image,Number(d.foodId)||0);
	 if(!d.contentId)return;
	 fetch("${pageContext.request.contextPath}/food/foodintroapi?contentId="+encodeURIComponent(d.contentId))
	 .then(r=>{if(!r.ok)throw new Error("상세정보 조회 실패");return r.json();})
	 .then(data=>{
	  if(seq!==foodIntroRequestSeq)return;
	  const hours=data.opentimefood||"";
	  const menu=data.firstmenu||data.treatmenu||"";
	  const phone=data.infocenterfood||d.phone||"";
	  setMultilineText("detail-hours",hours,"네이버 지도에서 최신 운영시간을 확인하세요.");
	  setMultilineNote("detail-hours-note",data.restdatefood||"","");
	  setMultilineText("detail-menu",menu,"네이버 지도에서 최신 메뉴 정보를 확인하세요.");
	  setMultilineNote("detail-menu-note",data.firstmenu?data.treatmenu||"":"","");
	  document.getElementById("detail-phone").innerText=cleanApiText(phone,"전화번호 정보 없음");
	  const mainMenu=document.getElementById("detail-main-menu");
	  if(mainMenu&&menu)mainMenu.innerText=cleanApiText(menu,"");
	 })
	 .catch(e=>console.error("[음식점 상세 API]",e));
	}
	
	//=========================================================
	//주제: 선택한 맛집 상세정보 변경
	//설명: 현재 food 테이블에 남은 이름/주소/전화번호/이미지만 표시
	//=========================================================
	function showFoodDetail(name,address,phone,image,foodId){
     if(!selectedFoodData||selectedFoodData.name!==name||selectedFoodData.address!==address){
      selectedFoodData={foodId:Number(foodId||0),name:name||"",address:address||"",image:image||"",phone:phone||""};
     }
	 selectFavorite("food",name,address,foodId);
	 if(document.getElementById("detail-hours-label"))document.getElementById("detail-hours-label").innerText="운영시간";
	 if(document.getElementById("detail-phone-label"))document.getElementById("detail-phone-label").innerText="전화번호";
	 if(document.getElementById("detail-menu-label"))document.getElementById("detail-menu-label").innerText="대표 메뉴";
	 if(document.getElementById("detail-main-label"))document.getElementById("detail-main-label").innerText="대표 이미지";

	 document.getElementById("detail-title").innerText=name||"이름 정보 없음";
	 if(document.getElementById("detail-subtitle"))document.getElementById("detail-subtitle").innerText=(name||"선택한 맛집")+" · "+(address||"주소 정보 없음");
	 document.getElementById("detail-address").innerText=address||"주소 정보 없음";
	 updateNaverReviewLink(name,address,"");

	 document.getElementById("detail-phone").innerText=phone&&phone.trim()!==""?cleanApiText(phone,"전화번호 정보 없음"):"전화번호 정보 없음";
	 if(document.getElementById("detail-phone-note"))document.getElementById("detail-phone-note").innerText=phone&&phone.trim()!==""?"매장 연락처 정보입니다.":"네이버 지도에서 최신 업체 정보를 확인하세요.";

	 setMultilineText("detail-hours","","네이버 지도에서 최신 운영시간을 확인하세요.");
	 setMultilineNote("detail-hours-note","","");
	 setMultilineText("detail-menu","","네이버 지도에서 최신 메뉴 정보를 확인하세요.");
	 setMultilineNote("detail-menu-note","","");

	 setMultilineText("detail-comment",(name||"선택한 맛집")+"은(는) "+(address||"선택한 지역 주변")+"에 위치한 맛집입니다. 자세한 운영시간과 메뉴는 네이버 지도에서 확인해보세요.","");

	 const detailImg=document.getElementById("detail-main-img");
	 const fallbackImg="${pageContext.request.contextPath}/resources/images/food/no-image.png";
	 if(detailImg){
	  detailImg.src=image&&image.trim()!==""?image:fallbackImg;
	  detailImg.onerror=function(){this.onerror=null;this.src=fallbackImg;};
	 }

	 const detailMainMenu=document.getElementById("detail-main-menu");
	 if(detailMainMenu)detailMainMenu.innerText=name||"맛집 이미지";

	 if(typeof hideDetailRatingForTour==="function")hideDetailRatingForTour();

	 scrollToSelectedDetailAtOffset();
	}
	// =========================================================
	// 주제: 관광지 기본 설명 생성
	// 설명: API overview가 없을 때 이름/주소/분류/거리로 자연스러운 안내문 생성
	// =========================================================
	function makeTourFallbackText(name,address,category,distanceText){
	  const placeName=name||"선택한 장소";
	  const placeCategory=category||"관광지";
	  const placeAddress=address&&address.trim()!==""?address:"선택한 지역 주변";
	  const distance=distanceText&&distanceText.trim()!==""&&distanceText!=="거리 정보 없음"?"\n현재 선택한 위치에서 약 "+distanceText+" 거리에 있습니다.":"";
	  return placeName+"은(는) "+placeAddress+"에 위치한 "+placeCategory+"입니다."+distance;
	}

	// =========================================================
	// 주제: 관광지 상단 요약문 생성
	// 설명: overview가 없어도 이름/분류/거리로 자연스러운 한 줄 설명 표시
	// =========================================================
	function makeTourSubtitle(name,address,category,distanceText){
	  const placeName=name||"선택한 관광지";
	  const placeCategory=category||"관광지";
	  const distance=distanceText&&distanceText.trim()!==""&&distanceText!=="거리 정보 없음"?" · 선택 위치에서 약 "+distanceText:"";
	  return placeName+" · "+placeCategory+distance;
	}

	// =========================================================
	// 주제: 관광지 상세 설명 불러오기
	// 설명: overview가 있으면 API 설명을 쓰고, 없으면 이름/주소/거리로 만든 기본 설명을 표시
	// =========================================================
	function loadTourOverview(contentId,contentTypeId,category){
	  const subtitle=document.getElementById("detail-subtitle");
	  const comment=document.getElementById("detail-comment");
	  const fallbackSubtitle=makeTourSubtitle(window.currentTourName,window.currentTourAddress,category,window.currentTourDistance);
	  const fallbackComment=makeTourFallbackText(window.currentTourName,window.currentTourAddress,category,window.currentTourDistance);

	  if(subtitle)subtitle.innerText=fallbackSubtitle;
	  if(comment){comment.innerText=fallbackComment;comment.style.whiteSpace="pre-line";}

	  if(!contentId)return;

	  fetch("${pageContext.request.contextPath}/food/detailapi?contentId="+encodeURIComponent(contentId)+"&contentTypeId="+encodeURIComponent(contentTypeId||"12"))
	    .then(function(response){return response.json();})
	    .then(function(data){
	      const overview=cleanApiText(data.overview||"","");
	      if(subtitle)subtitle.innerText=overview?makeShortText(overview,fallbackSubtitle):fallbackSubtitle;
	      if(comment){comment.innerText=overview||fallbackComment;comment.style.whiteSpace="pre-line";}
	    })
	    .catch(function(error){
	      console.log(error);
	      if(subtitle)subtitle.innerText=fallbackSubtitle;
	      if(comment){comment.innerText=fallbackComment;comment.style.whiteSpace="pre-line";}
	    });
	}
//=========================================================
//주제: 선택한 관광지 상세정보 변경
//설명: 관광지 카드 클릭 시 기본 정보 표시 후 detailCommon2 API로 상세 설명을 불러옴
//=========================================================
	function showTourDetail(contentId,contentTypeId,name,address,image,category,distanceText){
	selectFavorite("tour",name,address,0,contentId);
 selectedTourData={contentId:contentId||"",name:name||"",address:address||"",image:image||"",category:category||"관광지"};
	document.getElementById("detail-title").innerText=name||"관광지 이름 정보 없음";
	document.getElementById("detail-address").innerText=address||"주소 정보 없음";
	hideDetailRatingForTour();
	
	updateNaverReviewLink(name,address);
	
	window.currentTourName=name||"";
	window.currentTourAddress=address||"";
	window.currentTourDistance=distanceText||"";
	
	const reviewEl=document.getElementById("detail-review");
	if(reviewEl){
	 reviewEl.innerText="";
	 reviewEl.style.display="none";
	}
	
	if(document.getElementById("detail-subtitle"))document.getElementById("detail-subtitle").innerText=makeTourSubtitle(name,address,category,distanceText);
	if(document.getElementById("detail-main-label"))document.getElementById("detail-main-label").innerText="관광지";
	if(document.getElementById("detail-hours-label"))document.getElementById("detail-hours-label").innerText="운영시간";
	if(document.getElementById("detail-phone-label"))document.getElementById("detail-phone-label").innerText="현재 위치";
	if(document.getElementById("detail-menu-label"))document.getElementById("detail-menu-label").innerText="관광지 분류";
	if(document.getElementById("detail-phone-note"))document.getElementById("detail-phone-note").innerText="선택 지점 기준 거리";
	
	document.getElementById("detail-phone").innerText=distanceText||"거리 정보 없음";
	setMultilineText("detail-hours","네이버 지도 클릭 시 확인 가능","네이버 지도 클릭 시 확인 가능");
	setMultilineNote("detail-hours-note","네이버 지도에서 최신 운영시간을 확인하세요","");
	setMultilineText("detail-menu",category||"관광지","관광지");
	setMultilineNote("detail-menu-note","주변 관광지 정보","");
	
	const detailImg=document.getElementById("detail-main-img");
	const tourFallback="${pageContext.request.contextPath}/resources/images/food/fallback_tour.png";
	if(detailImg){
	 detailImg.src=image&&image.trim()!==""?image:tourFallback;
	 detailImg.onerror=function(){
	   this.src=tourFallback;
	   this.onerror=null;
	 };
	}
	
	let detailMainMenu=document.getElementById("detail-main-menu");
	if(!detailMainMenu&&detailImg&&detailImg.parentElement)detailMainMenu=detailImg.parentElement.querySelector("p.font-title-md");
	if(detailMainMenu)detailMainMenu.innerText=name||"관광지";
	
	loadTourOverview(contentId,contentTypeId,category);
	
	scrollToSelectedDetailAtOffset();
	}


	//=========================================================
	//주제: 페이지 로딩 후 기본 실행
	//설명: 지도는 잠깐 끄고, 맛집/관광지 상세 선택 기능만 먼저 복구
	//=========================================================
	document.addEventListener("DOMContentLoaded",function(){
	  initNaverMap();

	  const firstFood=document.getElementById("first-food-card");
	  const firstTour=document.getElementById("first-tour-card");
	  const target=firstFood||firstTour;
	  if(target){autoSelectingDetail=true;openMapPlaceDetailByCard(target);autoSelectingDetail=false;}
	  // 검색한 단어를 URL로 넘겨 첫 진입 시에도 부산 밀면 결과를 보여줌
	  const foodParams=new URLSearchParams(window.location.search);
	  const initialKeyword=(foodParams.get("keyword")||"").trim();
	  if(initialKeyword&&document.getElementById("list-food-container")){
	   const input=document.getElementById("global-food-search");
	   if(input)input.value=(foodParams.get("region")||"부산")+" "+initialKeyword;
	   searchFoodKeywordInCurrentRegion(initialKeyword);
	  }
	});
	
	
	
  </script>
</div>
