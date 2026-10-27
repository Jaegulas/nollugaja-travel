<%-- =========================================================
     주제: 파일 역할
     설명: accommodation 영역의 acco_main 화면을 구성하고 서버에서 전달받은 데이터를 표시
========================================================= --%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%-- c:set을 통해 contextPath를 짧은 변수명(ctx)으로 저장 --%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />


<main class="page-wrap">
<header class="page-header">
  <span class="page-eyebrow">🏨 STAY KOREA</span>
  <h1 class="page-title">전국숙박</h1>
  <p class="page-subtitle">지역을 고르거나 숙박업체를 검색해 보세요</p>
</header>


<%-- 지역 선택 --%>
<section class="filter-col">
  <div class="filter-banner">
    <div class="box-icon"><i class="ti ti-map-pin" aria-hidden="true"></i></div>
    <h4 class="box-title">지역 선택</h4>
  </div>
  <div class="region-tiles">
<c:forTokens var="item" items="서울:building-skyscraper,인천:plane,경기:home,강원:mountain,대전:atom,세종:building-bank,충청북도:plant,충청남도:ripple,전남:leaf,전북:bowl,부산:anchor,대구:apple,울산:fish,경상북도:building-castle,경상남도:sailboat,제주:beach" delims=",">      <c:set var="parts" value="${fn:split(item, ':')}" />
      <c:set var="r" value="${parts[0]}" />

      <c:set var="label" value="${r}" />
      <c:if test="${r == '충청북도'}"><c:set var="label" value="충북" /></c:if>
      <c:if test="${r == '충청남도'}"><c:set var="label" value="충남" /></c:if>
      <c:if test="${r == '경상북도'}"><c:set var="label" value="경북" /></c:if>
      <c:if test="${r == '경상남도'}"><c:set var="label" value="경남" /></c:if>

      <%-- 처음 들어와서 region이 없으면 서울을 선택 상태로 --%>
      <c:set var="isActive" value="${param.region == r or (empty param.region and r == '서울')}" />

      <a href="${ctx}/Accommodation/acco_main?region=${r}"
           class="region-tile ${isActive ? 'active' : ''}">
		  <i class="ti ti-${parts[1]} region-icon" aria-hidden="true"></i>
		  <span class="region-name">${label}</span>
      </a>
    </c:forTokens>
  </div>
</section>
<%-- 숙박업체 검색 --%>
<section class="filter-col">
  <div class="filter-banner">
    <div class="box-icon"><i class="ti ti-search" aria-hidden="true"></i></div>
    <h4 class="box-title">숙박업체 검색</h4>
  </div>
  <div class="search-row">
    <input type="text" name="searchlodging" id="searchlodging"
           class="search-input" placeholder="숙박업체명을 입력하세요"
           onkeydown="if(event.key === 'Enter') searchhotel();">
    <button type="button" class="search-btn" onclick="searchhotel();">찾기</button>
  </div>
</section>

<div class="section-banner">
  <div class="section-banner-icon">⭐</div>
  <div>
    <h3 class="section-title">대표숙박</h3>
    <p class="hint-text">💡 업체명을 클릭하면 지도에 위치가 보입니다.</p>
  </div>
</div>


<c:if test="${not empty volist}">
  <c:set var="first" value="${volist[0]}" />

  <%-- ===== 위쪽: 대표 숙소 1개 + 지도 + AI 요약 ===== --%>
<section class="top-section">
    <div class="top-left" id="topLeft">
        <c:choose>
            <c:when test="${not empty volist}">
                <c:set var="first" value="${volist[0]}" />
                <div class="featured-card">
					<c:choose>
						<c:when test="${first.type == '콘도'}">
							<c:set var="noImg" value="no-image-condo.svg" />
						</c:when>
						<c:when test="${first.type == '모텔'}">
							<c:set var="noImg" value="no-image-motel.svg" />
						</c:when>
						<c:when test="${first.type == '캠핑'}">
							<c:set var="noImg" value="no-image-camping.svg" />
						</c:when>
						<c:when test="${first.type == '호스텔'}">
							<c:set var="noImg" value="no-image-hostel.svg" />
						</c:when>
						<c:when test="${first.type == '펜션민박'}">
							<c:set var="noImg" value="no-image-pension.svg" />
						</c:when>
						<c:otherwise>
							<c:set var="noImg" value="no-image-hotel.svg" />
						</c:otherwise>
					</c:choose>
					<c:set var="noImgUrl"
						value="${ctx}/resources/images/accommodation/${noImg}" />
	
					<c:set var="addrParts" value="${fn:split(first.addr, ' ')}" />
	
					<div class="card-thumb">
					  <img class="stay-img" src="${empty first.picpath ? noImgUrl : first.picpath}"
					       alt="${first.name}"
					       onerror="this.onerror=null; this.src='${noImgUrl}';">
					
					  <c:if test="${not empty first.type}">
					    <span class="thumb-badge">${first.type}</span>
					  </c:if>
					
					  <button type="button" class="like-btn ${first.isliked ? 'liked' : ''}"
					          onclick="toggleLike(this, '${first.accommodation_id}')" aria-label="좋아요">
					    ${first.isliked ? '♥' : '♡'}
					  </button>
					</div>
				
				<div class="card-body">
				  <div class="card-meta">
				    <span>${addrParts[0]} ${addrParts[1]}</span>
				  </div>
				
				  <h3 class="card-name">
				    <span class="name" data-lat="${first.lat}" data-lon="${first.lon}"
				          data-name="${first.name}" data-id="${first.accommodation_id}"
				          onclick="showmap(this.dataset.lat, this.dataset.lon, this.dataset.name, this.dataset.id);">
				      ${first.name}
				    </span>
				  </h3>
				
				  <p class="stay-addr">📍 ${first.addr}</p>
				  <c:if test="${not empty first.tel}">
				    <p class="stay-tel">📞 ${first.tel}</p>
				  </c:if>
				
				  <c:if test="${not empty first.placeid}">
				    <div class="card-foot">
				      <a class="stay-link" href="${first.kakaoPlaceUrl}" target="_blank" rel="noopener noreferrer">카카오맵으로 길찾기 & 정보 →</a>
				    </div>
				  </c:if>
				</div>			
			</div>				
			
			<div id="aiSummary" class="ai-summary">
				<p class="loading">AI가 리뷰를 분석하고 있어요…</p>
			</div>
		  </c:when>
            <c:otherwise>
                <p class="empty-text">이 지역에 등록된 숙소가 없습니다. 업체명으로 검색해 보세요.</p>
            </c:otherwise>
          </c:choose>
	</div><%--top left 끝 --%>
	
	<%-- 오른쪽: 지도 --%>
	<div id="map"></div>
</section>

	<%-- ===== 아래쪽: 나머지 숙소 카드 ===== --%>
  <section class="card-grid">
    <c:forEach var="vo" items="${volist}" begin="1">
      <c:choose>
        <c:when test="${vo.type == '콘도'}"><c:set var="noImg" value="no-image-condo.svg" /></c:when>
        <c:when test="${vo.type == '모텔'}"><c:set var="noImg" value="no-image-motel.svg" /></c:when>
        <c:when test="${vo.type == '캠핑'}"><c:set var="noImg" value="no-image-camping.svg" /></c:when>
        <c:when test="${vo.type == '호스텔'}"><c:set var="noImg" value="no-image-hostel.svg" /></c:when>
        <c:when test="${vo.type == '펜션민박'}"><c:set var="noImg" value="no-image-pension.svg" /></c:when>
        <c:otherwise><c:set var="noImg" value="no-image-hotel.svg" /></c:otherwise>
      </c:choose>
      <c:set var="noImgUrl" value="${ctx}/resources/images/accommodation/${noImg}" />
	  <c:set var="addrParts" value="${fn:split(vo.addr, ' ')}" /> 
		
	  <div class="card">
		  <div class="card-thumb">
		    <img class="stay-img" src="${empty vo.picpath ? noImgUrl : vo.picpath}"
		         alt="${vo.name}"
		         onerror="this.onerror=null; this.src='${noImgUrl}';">
		
		    <c:if test="${not empty vo.type}">
		      <span class="thumb-badge">${vo.type}</span>
		    </c:if>
		
		    <button type="button" class="like-btn ${vo.isliked ? 'liked' : ''}"
		            onclick="toggleLike(this, '${vo.accommodation_id}')" aria-label="좋아요">
		      ${vo.isliked ? '♥' : '♡'}
		    </button>
		  </div>

		  <div class="card-body">
		    <div class="card-meta">
		      <span>${addrParts[0]} ${addrParts[1]}</span>
		    </div>
		
		    <h3 class="card-name">
		      <span class="name" data-lat="${vo.lat}" data-lon="${vo.lon}"
		            data-name="${vo.name}" data-id="${vo.accommodation_id}"
		            onclick="showmap(this.dataset.lat, this.dataset.lon, this.dataset.name, this.dataset.id);">
		        ${vo.name}
		      </span>
		    </h3>
		
		    <p class="stay-addr">📍 ${vo.addr}</p>
		    <c:if test="${not empty vo.tel}">
		      <p class="stay-tel">📞 ${vo.tel}</p>
		    </c:if>
		
		    <c:if test="${not empty vo.placeid}">
		      <div class="card-foot">
		        <a class="stay-link" href="${vo.kakaoPlaceUrl}" target="_blank" rel="noopener noreferrer">카카오맵으로 길찾기 & 정보 →</a>
		      </div>
		    </c:if>
		  </div>
		<%-- 2026.10.26 추가: 대표 숙소 목록 AI 장단점 (페이지 열릴 때 loadGridReviews()가 채움) --%>
		<div class="around-review grid-review" id="grid-review-${vo.accommodation_id}"
		     data-id="${vo.accommodation_id}" data-name="${fn:escapeXml(vo.name)}">
		  <p class="around-review-loading">리뷰 불러오는 중…</p>
		</div>
		</div>  
	</c:forEach>
  </section>
</c:if>

<br>


<br/><br/><br/>

<span id="around_title"></span> 
<div class="radius-bar">
  <button class="radius-btn active" data-radius="500" onclick="changeRadius(500, currentStayId)">500m</button>
  <button class="radius-btn" data-radius="1000" onclick="changeRadius(1000, currentStayId)">1km</button>
  <button class="radius-btn" data-radius="3000" onclick="changeRadius(3000, currentStayId)">3km</button>

  <button class="map-all-btn" onclick="showAllNearby()">🗺 지도로 한번에 보기</button>
</div>
<br>

<h6 id="around_result"></h6>
<br>

<section class="card-grid" id="add_distance_list">
</section>

</main>

<!--=========================================================
주제: 공통 찜 저장·취소 안내창
=========================================================-->
<div id="common-favorite-toast" role="status" aria-live="polite" aria-atomic="true">
 <span class="favorite-toast-heart" aria-hidden="true">♥</span>
 <span class="favorite-toast-text"></span>
</div>


<!--=========================================================
주제: 로그인 후 이용하세요 공통
=========================================================-->
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


<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script type="text/javascript" src="https://dapi.kakao.com/v2/maps/sdk.js?appkey=234376aaa52f550d6e16b505ad3621b9&libraries=services,clusterer&autoload=false"></script>

<script>

var map, ps, clusterer;

let mapMarkers = [];      // 지금 지도에 찍힌 마커들
let mapLabels = [];       // 지금 지도에 찍힌 이름표들

let currentAccommodation = null;
let currentAccommodationId = null;

let originalTopLeftHtml = null;   // 검색 전 왼쪽 칸 내용 (복원용)
let searchResults = [];           // 검색된 숙박업체 목록
let searchKeyword = "";

//주변 숙소 보여주기위해 추가
let currentStayId = null;

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
	 console.log("window.location.pathname : ",window.location.pathname);
	 console.log("window.location.search : ",window.location.search);
	 login.href="${pageContext.request.contextPath}/Login?returnTo="+encodeURIComponent(window.location.pathname+window.location.search);
	 login.style.display="flex";later.textContent="다음에 할게요";
	 note.textContent="로그인 전에는 찜한 장소가 저장되지 않습니다.";
	 if(typeof dialog.showModal==="function"){
		  if(!dialog.open)
			  dialog.showModal();
	 	  }else{
		 	  dialog.setAttribute("open","");
		 }
	}
}
	  
document.addEventListener("DOMContentLoaded",function(){
	 const dialog=document.getElementById("like-login-dialog");

	 if(!dialog)
		 return;
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

function showLikeLoginNotice(){showLikeNotice("login");}
function showLikeUnavailableNotice(type){showLikeNotice(type);}



kakao.maps.load(function() {
    
    var mapContainer = document.getElementById('map');
    var mapOption = {
        center: new kakao.maps.LatLng(36.5, 127.5), // 전국 중심 좌표
        level: 13
    };

    // 1. 지도 생성
    map = new kakao.maps.Map(mapContainer, mapOption);

    // 2. 장소 검색 객체 생성
    ps = new kakao.maps.services.Places();

    // 3. 마커 클러스터러 생성
    clusterer = new kakao.maps.MarkerClusterer({
        map: map,
        averageCenter: true,
        minLevel: 6
    });

    // 지도 컨트롤 추가
    map.addControl(new kakao.maps.MapTypeControl(), kakao.maps.ControlPosition.TOPRIGHT);
    map.addControl(new kakao.maps.ZoomControl(), kakao.maps.ControlPosition.RIGHT);
    setTimeout(refreshMapSize, 0);   // 레이아웃이 다 잡힌 뒤 지도 크기 다시 맞추기
});


document.addEventListener('DOMContentLoaded', function() {
    console.log("카카오 지도 SDK 로딩 완료 숙박업체 배열 처음 지도에 보여주자");
    originalTopLeftHtml = document.getElementById("topLeft").innerHTML;

    const first = document.querySelector('#topLeft .name');
    
    if (first) {
        showmap(first.dataset.lat, first.dataset.lon, first.dataset.name, first.dataset.id);
    }
  //2026.10.26 추가: 아래 대표 숙소 목록 장단점 채우기
    loadGridReviews();

    
});



//지도 위 마커와 이름표 모두 지우기
function clearMapObjects() {
    mapMarkers.forEach(m => m.setMap(null));
    mapLabels.forEach(l => l.setMap(null));
    mapMarkers = [];
    mapLabels = [];
}

//현재 호텔명
let currentHotelName = '';


//첫페이지 처음 호텔만 지도에 보여주기위해사용한다.
function showmap(lat, lon, hotelname, accommodationId){
	
	console.log("showup() 진입"); 
	currentStayId = accommodationId;   
	//근처숙소 보여줄때 사용
	currentHotelName = hotelname;
	currentLat = null;   // 추가
	currentLon = null;   // 추가
	
	const latitude = parseFloat(lat);
    const longitude = parseFloat(lon);
    
    console.log(latitude);
	console.log(longitude);
    
	if (isNaN(latitude) || isNaN(longitude)) {
        alert("유효한 좌표 정보가 없습니다.");
        return;
    }
    
	kakao.maps.load(function() {
	
		drawMap(latitude, longitude, hotelname, accommodationId);

		loadSummary(hotelname, accommodationId);
		loadNearby(accommodationId, 500, hotelname);
	});	
}//showmap

//지도 그리는 부분만 따로
function drawMap(latitude, longitude, hotelname, accommodationId) {
	const coords = new kakao.maps.LatLng(latitude, longitude);
	//주변업체 한번에 보기위해 추가 아래줄하나
	mainCoords = coords;
	clearMapObjects();

	const marker = new kakao.maps.Marker({ map: map, position: coords, clickable: true });
	mapMarkers.push(marker);
	mapLabels.push(addNameLabel(map, coords, hotelname));

	map.setLevel(3);
	map.setCenter(coords);

	registerMarkerClickEvent(map, marker, hotelname, accommodationId, null);
}//drawMap


<%-- 근처업체 클릭용 기존 --%>
/* function focusNearby(lat, lon) {
	const latitude = parseFloat(lat);
	const longitude = parseFloat(lon);
	if (isNaN(latitude) || isNaN(longitude)) return;

	const coords = new kakao.maps.LatLng(latitude, longitude);
	map.setLevel(3);
	map.panTo(coords);   // 부드럽게 이동

	// 지도가 화면 위쪽에 있으면 지도로 스크롤
	document.getElementById("map").scrollIntoView({ behavior: "smooth" });
}//focusNearby
 */
 
 <%-- 근처업체 클릭용 수정 --%>

let focusMarker = null;   // 근처 업체 클릭용 마커 (항상 1개만 유지)
let focusLabel = null;

function focusNearby(lat, lon, name) {
	const latitude = parseFloat(lat);
	const longitude = parseFloat(lon);
	if (isNaN(latitude) || isNaN(longitude)) return;

	const coords = new kakao.maps.LatLng(latitude, longitude);

	// 이전에 찍은 근처 업체 마커만 지우기 (메인 숙소 마커는 그대로)
	//if (focusMarker) focusMarker.setMap(null);
	//if (focusLabel) focusLabel.setMap(null);
	//위 2줄 주변업체 한번에 보기위해 주석처리
	clearAllNearby();
	
	// 새 마커 + 이름표
	focusMarker = new kakao.maps.Marker({ map: map, position: coords });
	focusLabel = addNameLabel(map, coords, name);

	// 메인 숙소가 바뀔 때 clearMapObjects()가 같이 지우도록 배열에도 넣기
	mapMarkers.push(focusMarker);
	mapLabels.push(focusLabel);

	map.setLevel(3);
	map.panTo(coords);

	document.getElementById("map").scrollIntoView({ behavior: "smooth" });
}//focusNearby


function searchhotel() {
	console.log("searchhotel() 진입");
	const keyword = $("#searchlodging").val().trim();
	 
	console.log(keyword);
	
    if (!keyword) {
        alert("숙박업체명을 입력해 주세요.");
        return;
    }

    // 처음 검색할 때 원래 왼쪽 칸 내용 저장
    if (originalTopLeftHtml === null) {
        originalTopLeftHtml = document.getElementById("topLeft").innerHTML;
    }
    
    const places = new kakao.maps.services.Places();

    ps.keywordSearch(keyword, function (data, status) {
        if (status === kakao.maps.services.Status.ERROR) {
            alert("장소 검색 중 오류가 발생했습니다.");
            return;
        }

        // 숙박(AD5)만 남기기
        const lodgings = (status === kakao.maps.services.Status.OK)
            ? data.filter(p => p.category_group_code === "AD5")
            : [];
        searchResults = lodgings; 
        searchKeyword = keyword;
        
        console.log(lodgings);
        // 지도 새로 그리기 (기존 마커 정리)
        map = new kakao.maps.Map(document.getElementById('map'), {
            center: new kakao.maps.LatLng(36.5, 127.5),
            level: 13
        });
        map.addControl(new kakao.maps.MapTypeControl(), kakao.maps.ControlPosition.TOPRIGHT);
        map.addControl(new kakao.maps.ZoomControl(), kakao.maps.ControlPosition.RIGHT);

        const bounds = new kakao.maps.LatLngBounds();

        searchResults.forEach(function (place, i) {
            const coords = new kakao.maps.LatLng(place.y, place.x);
            const marker = new kakao.maps.Marker({ map: map, position: coords, clickable: true });
            addNameLabel(map, coords, place.place_name);
            bounds.extend(coords);

            // 마커 클릭 → 왼쪽 칸에 상세 표시 (AI 호출은 이때만)
            kakao.maps.event.addListener(marker, 'click', function () {
                showSearchDetail(i);
            });
        });

        if (searchResults.length > 0) {
            map.setBounds(bounds);  // 모든 마커가 화면에 들어오게 자동 확대/축소
        }

        showSearchList();
    });
}//searchhotel

function backToSearchList() {
    refreshMapSize();
}



//AI 요약 불러오기 (지도 표시 때 + 마커 클릭 때 공통 사용)
function loadSummary(accommodationName, accommodationId, kakaoPlace) { // 없으면 undefined

	console.log("loadSummary 인수확인 accommodationName : "+ accommodationName + ", accommodationId : "+accommodationId);
	currentAccommodation = accommodationName;
	currentAccommodationId = accommodationId;
	const box = document.getElementById("aiSummary");
	box.innerHTML = '<p class="loading">AI가 리뷰를 분석하고 있어요…</p>';
	
	let url, body;
	
	if (kakaoPlace) {
		console.log("loadSummary() kakaoPlace 존재");
	
        url = "${ctx}/Accommodation/openrouter_summary_search"; //검색으로 들어온경우
    	// accommodationName kakaoPlaceId addr lat lon tel category
        body = {
            accommodationName: kakaoPlace.place_name,
            kakaoPlaceId: kakaoPlace.id,
            addr: kakaoPlace.road_address_name || kakaoPlace.address_name,
            lat: kakaoPlace.y,
            lon: kakaoPlace.x,
            tel: kakaoPlace.phone,
            category: getLodgingType(kakaoPlace.category_group_name)
        };
    } else {
		console.log("loadSummary() kakaoPlace 미존재");
    
        url = "${ctx}/Accommodation/openrouter_summary";
        body = { accommodationName: accommodationName, accommodationId: accommodationId };
    }

	
	
	fetch(url, {
		 method: "POST",
		 headers: { "Content-Type": "application/json; charset=UTF-8" },
		 body: JSON.stringify(body)
		})
		.then(response => {
		 if (!response.ok) throw new Error("서버 응답 오류: " + response.status);
		 return response.json();
		})
		.then(data => {
			 // 그사이 다른 숙소를 눌렀으면 이전 응답은 무시
			 if (currentAccommodation !== accommodationName) return;
			 if (currentAccommodationId !== accommodationId) return;
			
			 if (data.success !== true) {                 // 수정 전 : if(data.error){
			    console.log(data.message);               // 수정 전 : console.log(data.error);
			    box.innerHTML = '<p class="error-text">요약정보가 없습니다.</p>';
			    return;
			 }
			
			 // 검색 숙소: 서버가 알려준 DB id로 좋아요 버튼 연결
			 const likeBtn = document.getElementById("searchLikeBtn");
			 if (kakaoPlace && likeBtn && data.accommodationId) {
			     setLikeState(likeBtn, data.isliked === true);
			     //showFavoriteToast(data.liked === true);
			     likeBtn.onclick = function () { toggleLike(this, data.accommodationId); };
			 }
			 
			 
			// 검색으로 들어온 경우 리뷰도 같이 표시
	        if (Array.isArray(data.reviews)) {
	            renderReviews(data.reviews);
	        }
			 const textContent = data.result;
			 if (!textContent) throw new Error("서버로부터 빈 응답을 받았습니다.");
			
			 const start = textContent.indexOf('{');
	         const end = textContent.lastIndexOf('}');
	         if (start === -1 || end === -1) throw new Error("JSON 형식이 아닙니다: " + textContent);

	         const summaryData = JSON.parse(textContent.substring(start, end + 1));
	         console.log(summaryData);
	         box.innerHTML = renderProsAndCons(summaryData, accommodationName);
	         refreshMapSize();
	        
	         if (Array.isArray(data.reviews)) {
	            showReviews(data.reviews);
	         }
		})
		.catch(error => {
			 if (currentAccommodation !== accommodationName) return;
			 if (currentAccommodationId !== accommodationId) return;
			 console.error("AI 요약 로드 실패:", error);
			 box.innerHTML = '<p class="error-text">요약을 불러오지 못했습니다. 잠시 후 다시 시도해주세요.</p>';
	});//fetch
}//loadSummary

//리뷰 필드명(writer, rating, content, createdAt)은 실제 리뷰 VO에 맞게 바꾸세요
function showReviews(reviews) {
    const box = document.getElementById("reviewBox");
    if (!box) return;

    if (reviews.length === 0) {
        box.innerHTML = '<h4>리뷰</h4><p class="empty-text">아직 등록된 리뷰가 없습니다.</p>';
        return;
    }

    let html = `<h4>리뷰 (\${reviews.length})</h4><ul class="review-list">`;
    reviews.forEach(function (r) {
        html += `
            <li class="review-item">
                <div class="review-top">
                    <span class="review-writer">\${safeText(r.writer)}</span>
                    <span>⭐ \${safeText(r.rating)}</span>
                    <span>\${safeText(r.createdAt)}</span>
                </div>
                <div class="review-content">\${safeText(r.content)}</div>
            </li>`;
    });
    html += `</ul>`;
    box.innerHTML = html;
}//showReviews

//리뷰 목록 그리기 (컬럼명은 실제 리뷰 VO에 맞게 바꾸세요)
function renderReviews(reviews) {
	 const box = document.getElementById("reviewBox");
	 if (!box) return;
	
	 if (reviews.length === 0) {
	     box.innerHTML = '<h4>리뷰</h4><p class="empty-text">아직 등록된 리뷰가 없습니다.</p>';
	     return;
	 }
	
	 let html = `<h4>리뷰 (\${reviews.length})</h4><ul class="review-list">`;
	 reviews.forEach(r => {
	     html += `
	         <li class="review-item">
	             <div class="review-top">
	                 <span class="review-writer">\${escapeHtml(r.writer)}</span>
	                 <span>⭐ \${escapeHtml(r.rating)}</span>
	                 <span>\${escapeHtml(r.createdAt)}</span>
	             </div>
	             <div class="review-content">\${escapeHtml(r.content)}</div>
	         </li>`;
	 });
	 html += `</ul>`;
	 box.innerHTML = html;
}

function registerMarkerClickEvent(map, marker, accommodationName, accommodationId, kakaoPlace) {
    kakao.maps.event.addListener(marker, 'click', function () {
        loadSummary(accommodationName, accommodationId, kakaoPlace);
    });
}

//지도 칸 크기가 바뀌었을 때 다시 맞추고 중심 유지
function refreshMapSize() {
    if (!map) return;
    const center = map.getCenter();
    map.relayout();
    map.setCenter(center);
}

//마커 위에 숙소 이름표 표시
function addNameLabel(map, position, name) {
    const label = document.createElement('div');
    label.className = 'marker-label';
    label.textContent = name;

    const overlay = new kakao.maps.CustomOverlay({
        map: map,
        position: position,
        content: label,
        yAnchor: 1
    });
    return overlay;
}

//AI 장단점 요약 HTML 만들기
function renderProsAndCons(summary, accommodationName) {

    // 장점 목록
    const prosHtml = (summary.pros && summary.pros.length > 0)
        ? summary.pros.map(item => `
            <li class="pro-item">
                <span class="icon">✅</span>
                <span class="text">\${escapeHtml(item)}</span>
            </li>
          `).join('')
        : '<li class="empty-item">언급된 장점이 없습니다.</li>';

    // 단점 / 아쉬운 점 목록
    const consHtml = (summary.cons && summary.cons.length > 0)
        ? summary.cons.map(item => `
            <li class="con-item">
                <span class="icon">⚠️</span>
                <span class="text">\${escapeHtml(item)}</span>
            </li>
          `).join('')
        : '<li class="empty-item">특별한 아쉬운 점이 언급되지 않았습니다.</li>';

    return `
	   <div class="ai-summary-header">
	    <span class="ai-badge">
	        🤖 AI 리뷰 요약
	        <span class="ai-badge-name">\${escapeHtml(accommodationName)}</span>
	    </span>
		</div>

        <div class="ai-summary-body">
            <div class="summary-section pros-section">
                <h4>\${escapeHtml(accommodationName)} 방문자가 꼽은 장점</h4>
                <ul class="summary-list">
                    \${prosHtml}
                </ul>
            </div>

            <div class="summary-section cons-section">
                <h4>\${escapeHtml(accommodationName)} 아쉬운 점 및 참고사항</h4>
                <ul class="summary-list">
                    \${consHtml}
                </ul>
            </div>
        </div>

        <div class="ai-summary-footer">
            <p>※ AI가 자동 요약한 정보로, 실제와 다를 수 있습니다.</p>
        </div>
    `;
}


// XSS 방지용 간단한 HTML 이스케이프 유틸리티
function escapeHtml(str) {
    str = String(str ?? '');
    return str
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

//왼쪽 칸: 검색 결과 목록
function showSearchList() {
/*     let html = `
        <div class="search-header">
            <span>‘\${safeText(searchKeyword)}’ 검색 결과 \${searchResults.length}건</span>
            <button type="button" class="back-btn" onclick="restoreTopLeft()">원래 화면으로</button>
        </div>`;
 */
 
	 let html = `
	     <div class="seg-tabs">
	         <span class="seg-btn active">‘\${safeText(searchKeyword)}’ 검색 결과 \${searchResults.length}건</span>
	         <button type="button" class="seg-btn" onclick="restoreTopLeft()">원래 화면으로</button>
	     </div>`;
     
    if (searchResults.length === 0) {
        html += `<p class="empty-text">검색된 숙박업체가 없습니다.</p>`;
    } else {
        html += `<div class="search-list">`;
        searchResults.forEach(function (place, i) {
            html += `
                <div class="search-card" onclick="showSearchDetail(\${i})">
                    <div class="search-name">\${safeText(place.place_name)}</div>
                    <div class="search-addr">\${safeText(place.road_address_name || place.address_name)}</div>
                    <div class="search-tel">\${safeText(place.phone)}</div>
                </div>`;
        });
        html += `</div>`;
    }

    document.getElementById("topLeft").innerHTML = html;
}

// 왼쪽 칸: 검색한 업체 상세 + AI 요약
function showSearchDetail(index) {
    const place = searchResults[index];
    const noImgUrl = "${ctx}/resources/images/accommodation/no-image-hotel.svg";

    document.getElementById("topLeft").innerHTML = `
        <div class="seg-tabs">
        	<span class="seg-btn active">검색한 숙소</span>
        	<button type="button" class="seg-btn" onclick="showSearchList()">검색 결과로</button>
    	</div>
        <div class="featured-card">
	        <div class="card-thumb">
	            <img class="stay-img" src="\${noImgUrl}" alt="">
	            <button type="button" class="like-btn" id="searchLikeBtn" aria-label="좋아요"
	                    onclick="alert('숙소 정보를 불러오는 중이에요. 잠시 후 눌러주세요.')">♡</button>
	        </div>
	        
	        <div class="card-body">
	            <h3 class="card-name"><span class="name">\${safeText(place.place_name)}</span></h3>
	            <p class="stay-addr">📍 \${safeText(place.road_address_name || place.address_name)}</p>
	            \${place.phone ? '<p class="stay-tel">📞 ' + safeText(place.phone) + '</p>' : ''}
	            <div class="card-foot">
	                <a class="stay-link" href="\${place.place_url}" target="_blank" rel="noopener noreferrer">카카오맵으로 길찾기 & 정보 →</a>
	            </div>
	        </div>
    	</div>

        <div id="aiSummary" class="ai-summary">
            <p class="loading">AI가 리뷰를 분석하고 있어요…</p>
        </div>

        <div id="reviewBox"></div>
    `;

    map.setLevel(4);
    map.panTo(new kakao.maps.LatLng(place.y, place.x));

    // 기존 loadSummary 그대로 사용 (kakaoPlace가 있으면 검색용 서블릿으로 감)
   	
    currentHotelName = place.place_name;
    mainCoords = new kakao.maps.LatLng(place.y, place.x);   // "지도로 한번에 보기"에서 중심으로 쓰임
    loadNearbyByCoords(place.y, place.x, 500, place.place_name, null);

    loadSummary(place.place_name, null, place);
}

// 원래 화면으로 복원
function restoreTopLeft() {
    if (originalTopLeftHtml === null) return;

    document.getElementById("topLeft").innerHTML = originalTopLeftHtml;
    $("#searchlodging").val("");

    const first = document.querySelector('#topLeft .name');
    if (first) {
        showmap(first.dataset.lat, first.dataset.lon, first.dataset.name, first.dataset.id);
    }
}


//숙박 다음의 데이터가져오기
function getLodgingType(categoryName) {
    if (!categoryName) return "";

    const parts = categoryName.split(">").map(s => s.trim());
    // "여행 > 숙박 > 호텔 > 신라호텔" → ["여행", "숙박", "호텔", "신라호텔"]

    const idx = parts.indexOf("숙박");
    if (idx === -1 || idx + 1 >= parts.length) return "";   // 숙박이 없거나 그 뒤가 없으면 빈 값

    return parts[idx + 1];
}

// 숫자나 null이 와도 안전한 글자 변환 (검색 화면 전용)
function safeText(str) {
    return String(str ?? '')
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

//주변숙소
function loadNearby(stayid, distance, hotelname) {
	let body = {
		accommodation_id: stayid, distance: distance
	};

	fetch(`${ctx}/Accommodation/acco_distance`, {
		method: "POST",
		headers: { "Content-Type": "application/json; charset=UTF-8" },
		body: JSON.stringify(body)
	})
	.then(res => res.json())
	.then(data => {
		if (data.success !== true) throw new Error(data.message);
		make_add_distance_list(data.list, distance, hotelname);
	})
	.catch(err => {
	    console.error('주변 업체 조회 실패', err);
	    document.getElementById("around_result").innerHTML =
   	    '<span class="around-result-empty">주변 숙박업체를 불러오지 못했어요</span>';
	});
}

function make_add_distance_list(aroundvolist, distance, hotelname){
	//주번업체 한번에 보기위해 저장
	lastAroundList = aroundvolist;
	let add_distance_list = document.getElementById("add_distance_list");

	const ctx = '${ctx}';   // 이건 서버에서 미리 채워져도 되는 값이라 EL 그대로 OK

	// 타입별 기본 이미지 (c:choose 대신)
	const noImgMap = {
	  '콘도': 'no-image-condo.svg',
	  '모텔': 'no-image-motel.svg',
	  '캠핑': 'no-image-camping.svg',
	  '호스텔': 'no-image-hostel.svg',
	  '펜션민박': 'no-image-pension.svg'
	};

	let html = '';	
	let staycount = aroundvolist.length;
	
	
	aroundvolist.forEach(stay => {
	  const noImg = noImgMap[stay.type] || 'no-image-hotel.svg';
	  const noImgUrl = `\${ctx}/resources/images/accommodation/\${noImg}`;
	  const imgSrc = stay.picpath ? stay.picpath : noImgUrl;

	  const region = (stay.addr || '').split(' ').slice(0, 2).join(' ');
	  const liked = stay.isliked;

	  const typeHtml = stay.type ? `<span class="thumb-badge">\${stay.type}</span>` : '';
	  const telHtml = stay.tel ? `<p class="stay-tel">📞 \${stay.tel}</p>` : '';
	  const kakaoHtml = stay.placeid
	    ? `<div class="card-foot"><a class="stay-link" href="https://place.map.kakao.com/\${stay.placeid}" target="_blank" rel="noopener noreferrer">카카오맵으로 길찾기 & 정보 →</a></div>` : '';

	  html += `
	    <div class="card">
	      <div class="card-info">
	        <div class="card-thumb">
	          <img class="stay-img" src="\${imgSrc}" alt="\${stay.name}"
	               onerror="this.onerror=null; this.src='\${noImgUrl}';">
	          \${typeHtml}
	          <button type="button" class="like-btn \${liked ? 'liked' : ''}"
	                  onclick="toggleLike(this, \${stay.accommodation_id})" aria-label="좋아요">\${liked ? '♥' : '♡'}</button>
	        </div>

	        <div class="card-body">
	          <div class="card-meta"><span>\${region}</span></div>
	          <h3 class="card-name">
	            <span class="name"
	                  data-lat="\${stay.lat}" data-lon="\${stay.lon}" data-name="\${stay.name}"
	                  onclick="focusNearby(this.dataset.lat, this.dataset.lon, this.dataset.name);">
	              \${stay.name}
	            </span>
	          </h3>
	          <p class="stay-addr">📍 \${stay.addr}</p>
	          \${telHtml}
	          \${kakaoHtml}
	        </div>
	      </div>

	      <div class="around-review" id="around-review-\${stay.accommodation_id}">
	        <p class="around-review-loading">리뷰 불러오는 중…</p>
	      </div>
	    </div>`;
    });//aroundvolist.forEach
    
	add_distance_list.innerHTML = html;
	
	const titleName = hotelname || currentHotelName || '선택한 숙소';
	const distanceText = distance >= 1000 ? (distance / 1000) + 'km' : distance + 'm';

	document.getElementById("around_title").innerHTML = `
		<span class="around-title-name">\${escapeHtml(titleName)}</span>
		<span class="around-title-text">주변</span>
		<span class="around-title-distance">\${distanceText}</span>
		<span class="around-title-text">숙박업체</span>`;

	document.getElementById("around_result").innerHTML = staycount == 0
		? `<span class="around-result-empty">\${distanceText} 안에 숙박업체가 없어요</span>`
		: `검색결과 <span class="around-result-count">\${staycount}</span>개`;
		
		
	//aroundvolist.forEach(stay => loadAroundReview(stay.accommodation_id));
	//검색결과 안에서 리뷰 처리
	loadAroundReviews(aroundvolist); 
	
	// 현재 반경 버튼 표시
	document.querySelectorAll('.radius-btn').forEach(btn => {
		btn.classList.toggle('active', btn.dataset.radius == distance);
	});
}

<%-- 주변 숙소 리뷰보여주기 --%>
let aroundLoadToken = 0;   // 반경 버튼 다시 누르면 이전 목록 로딩 중단용

//검색 결과 안에서 카드들을 하나씩 순서대로 처리
async function loadAroundReviews(list) {
	const myToken = ++aroundLoadToken;

	for (const stay of list) {
		if (myToken !== aroundLoadToken) return;   // 그 사이 목록이 바뀌었으면 중단
		
		//개별 업체 리뷰 
		await loadAroundReview(stay.name, stay.accommodation_id, myToken);
	}
}

//검색결과 안에서 카드 하나의 AI 요약 불러오기
async function loadAroundReview(name, id, token) {
	const box = document.getElementById(`around-review-\${id}`);
	if (!box) return;

	box.innerHTML = '<p class="around-review-loading">리뷰 분석 중…</p>';

	try {
		const response = await fetch("${ctx}/Accommodation/openrouter_summary", {
			method: "POST",
			headers: { "Content-Type": "application/json; charset=UTF-8" },
			body: JSON.stringify({ accommodationName: name, accommodationId: id })
		});
		if (!response.ok) {
			throw new Error("서버 응답 오류: " + response.status);
		}

		const data = await response.json();
		if (token !== aroundLoadToken) return;   // 목록이 바뀌었으면 화면에 안 넣음

		if (data.success !== true) {                 // 수정 전 : if (data.error) {
		    box.innerHTML = '<p class="around-review-empty">요약정보가 없습니다.</p>';
		    return;
		}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		//검색에서 선택한 목록에서 한 업체 클릭시 아래 주변숙소까지 보이기
		// kakaoPlace.x = 경도(lon), kakaoPlace.y = 위도(lat)
/* 		currentStayId = null;   // DB id가 없으면 비우기 (있으면 그 id로)
		currentHotelName = kakaoPlace.place_name;
		loadNearbyByCoords(kakaoPlace.y, kakaoPlace.x, 500, kakaoPlace.place_name, id);
		
 */		
		const summaryData = parseSummaryResult(data.result);
		console.log(summaryData);
		box.innerHTML = renderAroundProsCons(summaryData);

	} catch (error) {
		if (token !== aroundLoadToken) return;
		console.error("주변 숙소 요약 실패:", id, error);
		box.innerHTML = '<p class="around-review-empty">요약정보가 없습니다.</p>';
	}
}

<%-- 2026.10.26 추가: 아래 대표 숙소 목록 장단점 보여주기 --%>
//카드 3장씩 동시에 처리 (저장된 요약은 바로 뜨고, AI 호출은 최대 3개까지만 동시에)
async function loadGridReviews() {
	const boxes = Array.from(document.querySelectorAll('.grid-review'));
	const MAX_AT_ONCE = 3;   // 동시에 보낼 요청 수
	let next = 0;            // 다음에 처리할 카드 번호

	async function worker() {
		while (next < boxes.length) {
			const box = boxes[next];
			next++;
			await loadGridReview(box);
		}
	}

	const workers = [];
	for (let i = 0; i < MAX_AT_ONCE; i++) {
		workers.push(worker());
	}
	await Promise.all(workers);
}

//카드 하나의 AI 요약 불러오기 (주변 숙소용 loadAroundReview와 같은 서버 주소 사용)
async function loadGridReview(box) {
	const id = Number(box.dataset.id);
	const name = box.dataset.name;

	box.innerHTML = '<p class="around-review-loading">리뷰 분석 중…</p>';

	try {
		const response = await fetch("${ctx}/Accommodation/openrouter_summary", {
			method: "POST",
			headers: { "Content-Type": "application/json; charset=UTF-8" },
			body: JSON.stringify({ accommodationName: name, accommodationId: id })
		});
		if (!response.ok) {
			throw new Error("서버 응답 오류: " + response.status);
		}

		const data = await response.json();
		if (data.success !== true) {
			box.innerHTML = '<p class="around-review-empty">요약정보가 없습니다.</p>';
			return;
		}

		const summaryData = parseSummaryResult(data.result);
		box.innerHTML = renderAroundProsCons(summaryData);

	} catch (error) {
		console.error("대표 숙소 요약 실패:", id, error);
		box.innerHTML = '<p class="around-review-empty">요약정보가 없습니다.</p>';
	}
}
<%-- 주변목록 불러오기 --%>
let currentLat = null;
let currentLon = null;

function loadNearbyByCoords(lat, lon, distance, hotelname, accommodation_id) {
	console.log("loadNearbyByCoords()  accommodation_id 전달값 : "+accommodation_id);
	currentLat = lat;
	currentLon = lon;

	let body ={
			lat :lat, lon: lon, distance: distance , accommodation_id: accommodation_id
	}
	
	fetch(`${ctx}/Accommodation/acco_distance_lon_lat`, {
			 method: "POST",
			 headers: { "Content-Type": "application/json; charset=UTF-8" },
			 body: JSON.stringify(body)
	})
	.then(res => res.json())
	.then(data => {
		if (data.success !== true) throw new Error(data.message);
		make_add_distance_list(data.list, distance, hotelname);
	})
	.catch(err => {
	    console.error('주변 업체 조회 실패', err);
	    document.getElementById("around_result").innerHTML =
	        '<span class="around-result-empty">주변 숙박업체를 불러오지 못했어요</span>';
	});
}


//AI 응답 문자열에서 JSON만 꺼내기 (loadSummary와 같은 로직)
function parseSummaryResult(textContent) {
	if (!textContent) throw new Error("빈 응답");
	const start = textContent.indexOf('{');
	const end = textContent.lastIndexOf('}');
	if (start === -1 || end === -1) throw new Error("JSON 형식이 아닙니다");
	return JSON.parse(textContent.substring(start, end + 1));
}

//카드용 간단한 장단점 표시 (기존 renderProsAndCons와 다른 디자인)
function renderAroundProsCons(summaryData) {
	const pros = (summaryData.pros || []).filter(p => p && p.trim());
	const cons = (summaryData.cons || []).filter(c => c && c.trim());

	if (pros.length == 0 && cons.length == 0) {
		return '<p class="around-review-empty">요약정보가 없습니다.</p>';
	}
	
	let html = '<div class="around-pc">';

	html += '<div class="around-pros"><strong>👍 장점</strong><ul>';
	pros.slice(0, 3).forEach(p => {
		html += `<li><span class="around-icon">✔</span><span>\${escapeHtml(p)}</span></li>`;
	});
	html += '</ul></div>';

	html += '<div class="around-cons"><strong>👎 단점</strong><ul>';
	cons.slice(0, 3).forEach(c => {
		html += `<li><span class="around-icon">✖</span><span>\${escapeHtml(c)}</span></li>`;
	});
	html += '</ul></div>';

	html += '</div>';
	return html;
}

/* function loadAroundReview(accommodation_id) {
  const box = document.getElementById(`around-review-\${accommodation_id}`);

  fetch(`\${ctx}/review/summary?id=\${accommodation_id}`)   // ← 기존 loadSummary()의 주소로 맞춰주세요
    .then(res => res.json())
    .then(data => {
      const reviews = data.reviews || [];

      if (reviews.length == 0) {
        box.innerHTML = `<p class="around-review-empty">아직 리뷰가 없어요</p>`;
        return;
      }

      // 상단 요약 (평균 별점 + 리뷰 수)
      let html = `
        <div class="around-review-head">
          <span class="around-review-star">★ \${data.avg_rating}</span>
          <span class="around-review-count">리뷰 \${data.review_count}개</span>
        </div>`;

      // 최근 리뷰 3개만 짧게
      reviews.slice(0, 3).forEach(r => {
        html += `
          <div class="around-review-item">
            <div class="around-review-meta">
              <span>\${escapeHtml(r.nickname)}</span>
              <span>\${'★'.repeat(r.rating)}</span>
            </div>
            <p>\${escapeHtml(r.content)}</p>
          </div>`;
      });

      box.innerHTML = html;
    })
    .catch(err => {
      console.error('리뷰 조회 실패', err);
      box.innerHTML = `<p class="around-review-empty">리뷰를 불러오지 못했어요</p>`;
    });
}
 */

<%-- ------------------------  주변 숙소목록 보여주기 끝    ----------------------------------  --%>

<%-- 반경 한번에 지도로 보여주기 --%>
let lastAroundList = [];   // 마지막으로 불러온 근처 업체 목록
let mainCoords = null;     // 메인 숙소 위치
let allNearbyMarkers = []; // "한번에 보기"로 찍은 마커/이름표들

function showAllNearby() {
	if (lastAroundList.length == 0) {
		alert("표시할 주변 숙박업체가 없습니다.");
		return;
	}

	// 이전에 찍은 근처 마커들 지우기 (메인 숙소 마커는 그대로)
	clearAllNearby();

	// 지도 범위 계산용 (메인 숙소 + 근처 업체 전부)
	const bounds = new kakao.maps.LatLngBounds();
	if (mainCoords) bounds.extend(mainCoords);

	lastAroundList.forEach(stay => {
		const lat = parseFloat(stay.lat);
		const lon = parseFloat(stay.lon);
		if (isNaN(lat) || isNaN(lon)) return;

		const coords = new kakao.maps.LatLng(lat, lon);
		const marker = new kakao.maps.Marker({ map: map, position: coords });
		const label = addNameLabel(map, coords, stay.name);

		allNearbyMarkers.push(marker, label);
		mapMarkers.push(marker);   // 메인 숙소 바뀔 때 clearMapObjects()가 같이 지우도록
		mapLabels.push(label);

		bounds.extend(coords);
	});

	map.setBounds(bounds);   // 모든 마커가 화면에 들어오게 자동 확대/축소
	document.getElementById("map").scrollIntoView({ behavior: "smooth" });
}

function clearAllNearby() {
	allNearbyMarkers.forEach(m => m.setMap(null));
	allNearbyMarkers = [];

	// 업체명 클릭으로 찍은 마커도 같이 정리
	if (focusMarker) focusMarker.setMap(null);
	if (focusLabel) focusLabel.setMap(null);
}

function changeRadius(radius, currentStayId) {
	console.log("changeRadius() 전달값  accommdation_id: "+currentStayId);
	if (currentLat && currentLon) {
		loadNearbyByCoords(currentLat, currentLon, radius, currentHotelName, currentStayId);
	} else {
		loadNearby(currentStayId, radius, currentHotelName);
	}
}

const loginId = "${sessionScope.loginId}";


// 숙소 찜: 실제 로그인 회원 ID로 accommodation_like 저장/취소
async function toggleLike(btn, accommodationId) {
    if (!loginId || loginId === 'null') {
    	//통일된 디자인으로 보여주기
    	showLikeLoginNotice();
    	//alert('로그인 후 이용해주세요.');
        return;
    }

    if (btn.disabled) return;
    const willLike = !btn.classList.contains('liked');
    btn.disabled = true;
    try {
        const res = await fetch('${ctx}/ExtraLikes', {
            method: 'POST',
            credentials: 'same-origin',
            headers: {'Content-Type':'application/x-www-form-urlencoded;charset=UTF-8'},
            body: new URLSearchParams({type:'acc', id:String(accommodationId), liked:String(willLike)})
        });
        const data = await res.json();
        if (!res.ok || data.success !== true) {
            throw new Error(data.message || '숙소 찜 저장 실패 ('+res.status+')');
        }
        
        showFavoriteToast(data.liked === true);
        setLikeState(btn, data.liked === true); // 서버 저장 성공 후에만 하트 변경
    } catch (err) {
        console.error('[숙소 찜 오류]', err);
        alert('숙소 찜 저장에 실패했습니다. 새로고침 후 다시 시도해 주세요.');
    } finally {
        btn.disabled = false;
    }

/* 	
 //권성애 주석처리 충돌나서 2026.10.19월
 const willLike = !btn.classList.contains('liked');
	setLikeState(btn, willLike);   // 화면 먼저 변경

	fetch("${ctx}/Accommodation/acco_like", {
		method: "POST",
		headers: { "Content-Type": "application/json; charset=UTF-8" },
		body: JSON.stringify({ accommodationId: accommodationId, liked: willLike })
	})
	.then(res => {
		if (res.status == 401) {
			alert("로그인 후 이용해주세요.");
			throw new Error("로그인 필요");
		}
		if (!res.ok) throw new Error("서버 응답 오류: " + res.status);
		
		return res.json();
	})
	.then(data => {
		if (data.success !== true) throw new Error(data.message); 
	})
	.catch(err => {
		console.error("좋아요 처리 실패", err);
		setLikeState(btn, !willLike);   // 실패하면 되돌리기
	}); */
}

function setLikeState(btn, liked) {
	btn.classList.toggle('liked', liked);
	btn.textContent = liked ? '♥' : '♡';
} 
 
//=========================================================
//주제: 찜 성공 알림 공통 함수
//설명: liked=true 저장, false 취소 / 2.8초 뒤 자동 닫힘
//=========================================================
let commonFavoriteToastTimer=null;


function showFavoriteToast(liked){
	const toast=document.getElementById('common-favorite-toast');if(!toast)return;
	const msg=toast.querySelector('.favorite-toast-text');
	if(msg)
		msg.textContent=liked?'찜 목록에 저장되었습니다.':'찜 목록에서 삭제되었습니다.';
	toast.classList.toggle('removed',!liked);
	
	
	toast.classList.add('show');
	clearTimeout(commonFavoriteToastTimer);
	commonFavoriteToastTimer=setTimeout(function(){toast.classList.remove('show');},2800);
}

</script>
