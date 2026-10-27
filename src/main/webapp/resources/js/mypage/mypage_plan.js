// =========================================================
// 주제: 파일 역할
// 설명: 마이페이지 여행일정 조회·지도·공유 관련 동작을 처리
// =========================================================

/* =========================================
   mypage-plan.js  -  내 여행일정 (itineraryjson 테이블)
   목록은 JSP(JSTL)로 그리고, 이 파일은
   상세보기 / PDF / 카톡공유 / 삭제 를 담당합니다.

   JSP <body> 에 아래 두 값을 넣어두세요 (.js 파일에서는 ${...} 가 동작하지 않음)
   <body data-ctx="${pageContext.request.contextPath}"
         data-kakao-key="${applicationScope.kakaoJsKey}">

   카카오 SDK(kakao.min.js), 카카오 지도 SDK, html2pdf 는 이 파일보다 먼저 불러오기
   ========================================= */

// mypage-common.js 에 CTX 가 이미 있으면 그걸 쓰고, 없으면 body 에서 읽기
var CTX = window.CTX || document.body.getAttribute('data-ctx') || '';
var KAKAO_JS_KEY = document.body.getAttribute('data-kakao-key') || '';

initKakao();

// 지금 화면에 보이는 일정 데이터 (공유 문구 만들 때 사용)
let planData = null;

const loadedPlans = {};   // planid별로 받아온 데이터 보관


/* ============================================== 일정 불러오기 / 보여주기 ============================================== */

// 서버에서 일정 1개 받아오기 (한 번 받은 건 재사용)
// =========================================================
// 주제: fetchPlan 처리
// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
// =========================================================
function fetchPlan(planid) {
	console.log("mypage-plan.js 마이페이지 여행일정표 목록에서 클릭 : "+planid);
  if (loadedPlans[planid]) {
    return Promise.resolve(loadedPlans[planid]);
  }
  return fetch(CTX + '/Mypage/getOnlyItineraryJsonContent?planid=' + planid)
  .then(checkLogin) 
    .then(res => res.json())
    .then(data => {
		 if (data.success === false) {
		    alert(data.message);
		    throw new Error(data.message);
		  }
		  
	      loadedPlans[planid] = data;
	      return data;
    });
}

// 제목 클릭
// =========================================================
// 주제: showPlan 처리
// 설명: showPlan 기능에 필요한 처리 로직을 수행
// =========================================================
function showPlan(planid) {
  fetchPlan(planid).then(data => {
    renderPlan(data);
    // 일정 영역으로 스크롤
    document.getElementById('plan').scrollIntoView({ behavior: 'smooth' });
  });
}

// PDF 버튼
// =========================================================
// 주제: downloadPlanPdf 처리
// 설명: downloadPlanPdf 기능에 필요한 처리 로직을 수행
// =========================================================
function downloadPlanPdf(planid) {
	console.log("mypage-plan.js 마이페이지에서 일정표 pdf다운로드 onclick 이벤트 진입 fetch 요청 후 json데이터만 추출해서 카카오지도와 직선노선도 생성 ");
  fetchPlan(planid).then(data => {
    renderPlan(data);
    // 지도 타일이 다 그려질 시간을 조금 준 뒤 PDF 생성
    setTimeout(() => downloadPDF(), 800);
  });
}


/* ============================================== 카카오 SDK 준비 ============================================== */

// =========================================================
// 주제: initKakao 처리
// 설명: initKakao 기능에 필요한 처리 로직을 수행
// =========================================================
function initKakao() {
  if (window.Kakao && !Kakao.isInitialized()) {
    Kakao.init(KAKAO_JS_KEY);
  }
}


//=============================================
//일정표 전체 그리기
//=============================================
// =========================================================
// 주제: renderPlan 처리
// 설명: renderPlan 기능에 필요한 처리 로직을 수행
// =========================================================
function renderPlan(data) {

	// 다른 일정이 들어왔을 때만 저장 번호 초기화
	 if (planData !== data) {
	   savedPlanId = null;
	 }
	 
  planData = data;   // 공유할 때 쓰려고 저장

  // 숨겨져 있던 일정 영역 보이기
  document.getElementById('plan').style.display = 'block';

  const box = document.getElementById("aiSummaryBox");
  box.innerHTML =
    '<div class="plan-banner">' +
      '<span class="plan-badge">✈ MY TRIP PLAN</span>' +
      '<h2 id="planTitle" class="plan-title"></h2>' +
      '<p id="planSummary" class="plan-summary"></p>' +
    '</div>' +
    '<div id="planDays"></div>';

  document.getElementById("planTitle").textContent = data.title;
  document.getElementById("planSummary").textContent = data.summary;
  document.title = data.title;

  const daysBox = document.getElementById("planDays");

  // PDF 가로 배치 때 몇 칸으로 나눌지 (최대 3칸)
  const cols = Math.min(data.days.length, 3);
  daysBox.style.setProperty("--pdf-cols", cols);

  data.days.forEach(function (day) { // 일차(day)마다 한 번씩 반복
    const section = el("section", "day"); // 하루치 전체를 담을 큰 상자

    const head = el("div", "day-head");  // "1일차 ---- 2026-10-11" 줄
    head.appendChild(el("span", "day-num", day.day + "일차")); // 왼쪽: 1일차
    head.appendChild(el("span", "day-date", day.date)); // 오른쪽: 날짜
    section.appendChild(head);
    section.appendChild(el("p", "day-theme", day.theme));  // 그날 테마 문장

    // [지도 | 세로 노선도] 를 담는 2칸 상자
    const dayBody = el("div", "day-body");
    const mapBox = el("div", "day-map");   // 카카오 지도가 그려질 빈 칸
    dayBody.appendChild(mapBox);           // ① 왼쪽 칸

    const list = el("ol", "timeline");     // 오른쪽 세로 노선도 목록

    day.items.forEach(function (item) {    // 그날 장소(item)마다 반복
      const li = el("li", "stop");
      li.appendChild(el("div", "stop-time", item.time));
      li.appendChild(el("div", "stop-line"));

      const body = el("div", "stop-body");
      body.appendChild(el("h3", "stop-place", item.place));
      body.appendChild(el("p", "stop-activity", item.activity));

      if (item.tip) {
        const tip = el("p", "stop-tip");
        tip.appendChild(el("strong", "", "TIP"));
        tip.appendChild(document.createTextNode(item.tip));
        body.appendChild(tip);
      }

      li.appendChild(body);
      list.appendChild(li);
    }); // items.forEach

    dayBody.appendChild(list);        // ② 오른쪽 칸
    section.appendChild(dayBody);
    daysBox.appendChild(section);
  }); // data.days.forEach

  // 모든 일차가 화면에 붙은 뒤에 지도 그리기
  kakao.maps.load(function () {
    const mapBoxes = document.querySelectorAll(".day-map");
    data.days.forEach(function (day, i) {
      //drawDayMap(mapBoxes[i], day.items);
	  drawDayMap(mapBoxes[i], data, day);
	  });
  }); // kakao.maps.load

} // ★ renderPlan 끝 (이 괄호가 빠져 있었어요)


// 태그 만들기 도우미 (textContent를 써서 AI 응답에 HTML이 섞여도 안전)
// =========================================================
// 주제: el 처리
// 설명: el 기능에 필요한 처리 로직을 수행
// =========================================================
function el(tag, className, text) {
  const node = document.createElement(tag);
  if (className) node.className = className;
  if (text !== undefined) node.textContent = text;
  return node;
}


/* ============================================== 카카오지도 그리기 ============================================== */

// 장소 이름 → 좌표 찾기 (못 찾으면 null)
// =========================================================
// 주제: searchPlace 처리
// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
// =========================================================
function searchPlace(ps, keyword) {
  return new Promise(function (resolve) {
    ps.keywordSearch(keyword, function (data, status) {
      if (status === kakao.maps.services.Status.OK) {
        resolve(new kakao.maps.LatLng(data[0].y, data[0].x));
      } else {
        resolve(null);
      }
    });
  });
}

// 하루치 일정을 지도에 그리기
// 181~192번째 줄
// 하루치 일정을 지도에 그리기
// =========================================================
// 주제: drawDayMap 처리
// 설명: drawDayMap 기능에 필요한 처리 로직을 수행
// =========================================================
async function drawDayMap(mapBox, data, day) {
  if (!mapBox) return;
  const items = day.items;

  const map = new kakao.maps.Map(mapBox, {
    center: new kakao.maps.LatLng(36.5, 127.8),  // 우리나라 가운데 (장소 찾으면 자동으로 옮겨짐)
    level: 12
  });

  const ps = new kakao.maps.services.Places();   // ← 이 줄이 빠져서 에러가 났어요
  const bounds = new kakao.maps.LatLngBounds();
  const path = [];

  for (let i = 0; i < items.length; i++) {
    const region = getRegionHint(data, day, items[i]);
    const pos = await findPosition(ps, items[i].place, region);
    if (!pos) continue; // 못 찾은 장소는 건너뜀

    path.push(pos);
    bounds.extend(pos);

    // 번호 마커
    new kakao.maps.CustomOverlay({
      map: map,
      position: pos,
      content: '<div class="map-pin">' + (i + 1) + '</div>'
    });
  }

  // 장소들을 순서대로 잇는 선
  new kakao.maps.Polyline({
    map: map,
    path: path,
    strokeWeight: 4,
    strokeColor: "#b85c38",
    strokeOpacity: 0.8,
    strokeStyle: "shortdash"
  });

  // 모든 마커가 보이게 지도 범위 자동 조절
  if (path.length > 0) map.setBounds(bounds);
}


// 애매한 단어를 지워서 동네 이름만 남기기
// 예: "남포동 인근 맛집" → "남포동"
// =========================================================
// 주제: cleanPlaceName 처리
// 설명: 입력 데이터를 화면/API/DB에서 사용하기 좋은 형태로 변환 또는 가공
// =========================================================
function cleanPlaceName(place) {
  return place
    .replace(/인근|근처|주변|내|저녁|점심|아침|맛집|카페/g, "")
    .replace(/\s+/g, " ")
    .trim();
}

// "맛집"이 들어간 이름은 처음부터 동네 이름으로 검색
// =========================================================
// 주제: findPosition 처리
// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
// =========================================================
async function findPosition(ps, place, region) {
  const prefix = region ? region + " " : "";
  const keyword = place.includes("맛집") ? cleanPlaceName(place) : place;
  return await searchPlace(ps, prefix + keyword);
}

// 새로 추가
// =========================================================
// 주제: getRegionHint 처리
// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
// =========================================================
function getRegionHint(data, day, item) {
  if (item && item.area) return item.area;
  if (day && day.region) return day.region;
  if (data.region) return data.region;
  return "";
}

/* ============================================== PDF 저장 ============================================== */

// =========================================================
// 주제: downloadPDF 처리
// 설명: downloadPDF 기능에 필요한 처리 로직을 수행
// =========================================================
async function downloadPDF() {
  const box = document.getElementById("aiSummaryBox");
  //window.scrollTo(0, 0);              // 스크롤 맨 위로 (캡처 어긋남 방지)
  box.classList.add("pdf-mode");      // PDF 전용 스타일 켜기

  const opt = {
    margin: [10, 10, 10, 10],
    filename:  makeFileName(),
	//(document.title || "여행일정표") + ".pdf",
    image: { type: "jpeg", quality: 0.95 },
    html2canvas: {
      scale: 2,
      scrollX: 0,
      scrollY: 0,
      // 카카오 지도는 캡처에서 제외 → CORS 에러 방지
      ignoreElements: function (el) {
        return el.classList && el.classList.contains("day-map");
      }
    },
    jsPDF: { unit: "mm", format: "a4", orientation: "landscape" },
    pagebreak: { mode: ["css", "legacy"], avoid: [".stop", ".day-head", ".plan-banner", ".day-mini"] }
  };

  html2pdf().set(opt).from(box).save()
    .finally(function () {
      box.classList.remove("pdf-mode");
    });
}


function makeFileName() {
  const title = document.title || "여행일정표";
  const d = new Date();
  const today = d.getFullYear()
      + String(d.getMonth() + 1).padStart(2, "0")
      + String(d.getDate()).padStart(2, "0");
  const safeTitle = title.replace(/[\\/:*?"<>|]/g, "_").trim();   // 못 쓰는 문자 → _
  return safeTitle + "_" + today + ".pdf";
}


/* ============================================== 카톡 공유 ============================================== */

// 목록의 [카톡 공유] 버튼: onclick="sharePlanFromList(this)" + data-planid 필요
// =========================================================
// 주제: sharePlanFromList 처리
// 설명: sharePlanFromList 기능에 필요한 처리 로직을 수행
// =========================================================
function sharePlanFromList(btn) {
  var planid = btn.dataset.planid;
  console.log("mypage-plan.js 마이페이지에서 카톡공유버튼 클릭 여행일정표 번호 확인 : "+planid)

    fetchPlan(planid).then(function (data) {
		console.log("fetch 처리후 받은 데이터 확인 후 shareKakaoPlan() 실행 : ",data)
    shareKakaoPlan(planid, data);
  });
}

/*
  1) 서버에서 공유키 받기   (처음 누를 때 만들어지고, 다음부턴 같은 키)
  2) 공유 주소 만들기       : http://내사이트/컨텍스트/share/plan?key=…
  3) 카카오 공유 창 띄우기
*/
// =========================================================
// 주제: shareKakaoPlan 처리
// 설명: shareKakaoPlan 기능에 필요한 처리 로직을 수행
// =========================================================
function shareKakaoPlan(planid, data) {
	
  if (!window.Kakao || !Kakao.isInitialized()) {
    alert("카카오 SDK를 불러오지 못했습니다.");
    return;
  }

  const params = new URLSearchParams();
  params.append("planid", planid);

  fetch(CTX + "/Mypage/itineraryJsonSharekeyPro", { method: "POST", body: params })
  .then(checkLogin) 
    .then(function (res) { return res.json(); })
    .then(function (result) {
      if (!result.success) {
        alert(result.message || "공유 링크를 만들지 못했습니다.");
        return;
      }

      const shareUrl = location.origin + CTX + "/Mypage/findItineraryJsonFromSharekey?key=" + result.sharekey;
	  console.log("mypage-plan.js shareKakaoPlan()shareUrl : "+shareUrl);
      Kakao.Share.sendDefault({
        objectType: "text",
        text: makeShareText(data),
        link: {
          // ★ 카카오 개발자 사이트 [플랫폼 > Web] 에 등록한 도메인이어야 열림
          mobileWebUrl: shareUrl,
          webUrl: shareUrl
        },
        buttonTitle: "일정표 보기"
      });
    })
    .catch(function (err) {
      console.error(err);
	  if (err.message === 'LOGIN_REQUIRED') return;
      alert("공유 중 오류가 발생했습니다.");
    });
}

// 공유 문구 : 제목 + 일차별 장소 (text 템플릿은 200자까지)
// =========================================================
// 주제: makeShareText 처리
// 설명: 입력 데이터를 화면/API/DB에서 사용하기 좋은 형태로 변환 또는 가공
// =========================================================
function makeShareText(data) {
  if (!data) return "여행 일정표를 공유했어요!";

  let text = "[" + data.title + "]\n";
  (data.days || []).forEach(function (day) {
    const places = (day.items || []).map(function (item) { return item.place; });
    text += day.day + "일차 : " + places.join(" → ") + "\n";
  });

  if (text.length > 200) {
    text = text.substring(0, 197) + "...";
  }
  return text;
}


/* ============================================== 삭제 ============================================== */

// 삭제 버튼: onclick="itineraryDelete(this)"
// =========================================================
// 주제: itineraryDelete 처리
// 설명: itineraryDelete 기능에 필요한 처리 로직을 수행
// =========================================================
function itineraryDelete(btn) {
  if (!confirm('이 일정을 삭제할까요?\n삭제하면 공유한 링크도 열리지 않아요.')) return;

  var planid = btn.dataset.planid;
  btn.disabled = true;   // 연속 클릭 막기

  fetch(CTX + '/Mypage/deleteItineraryJsonPlan', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: 'planid=' + encodeURIComponent(planid)
  })
  .then(checkLogin)
    .then(function (res) { return res.json(); })
    .then(function (data) {
      if (!data.success) {
        alert(data.message || '삭제하지 못했습니다.');
        btn.disabled = false;
        return;
      }

      // 1. 표에서 그 줄 지우기
      var row = btn.closest('tr');
      var table = btn.closest('table');
      row.remove();

      // 2. 받아둔 데이터와 열려있던 일정 영역 비우기
      delete loadedPlans[planid];
      document.getElementById('aiSummaryBox').innerHTML = '';
      document.getElementById('plan').style.display = 'none';

      // 3. 남은 개수 다시 세기 (첫 줄은 제목 줄이라 -1)
      var left = table.querySelectorAll('tr').length - 1;
      var countEl = document.getElementById('plan-count');
      if (countEl) countEl.textContent = left + ' / 15';

      // 4. 다 지웠으면 "없습니다" 문구로 바꾸기
      if (left === 0) {
        table.outerHTML = '<p class="empty">저장된 일정이 없습니다.</p>';
      }
    })
    .catch(function (err) {
      console.error(err);
	  if (err.message === 'LOGIN_REQUIRED') return;  
      alert('삭제 중 오류가 발생했습니다.');
      btn.disabled = false;
    });
}
