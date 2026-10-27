<%-- =========================================================
     주제: 파일 역할
     설명: chatbot 영역의 newconversation 화면을 구성하고 서버에서 전달받은 데이터를 표시
========================================================= --%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isELIgnored="false"%>
    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath }"/>
 


<!-- =========================================================
     AI 여행 일정표 폼 (카드형 구조)
     - id, name, onclick, onsubmit 은 기존과 똑같이 유지했습니다
     - <head> 에 CSS 파일 하나만 연결하세요
       디자인1: <link rel="stylesheet" href="${ctx}/css/style-card.css">
       디자인2: <link rel="stylesheet" href="${ctx}/css/style-recommend.css">
     ========================================================= -->
    
<header class="page-hero">
  <span class="hero-eyebrow">✈ AI TRAVEL PLANNER</span>
  <h1 class="hero-title">AI와 <em>여행 일정표</em> 짜기</h1>
  <p class="hero-sub">날짜와 취향만 알려주세요. 나에게 꼭 맞는 일정을 AI가 만들어드려요.</p>
</header>

<div class="reset-bar">
  <button type="button" class="reset-btn" onclick="resetForm()">아래 입력값 초기화하기</button>
</div>



<form id="conversationForm" class="trip-form" onsubmit="conversationFetchAskChatbot(event);">

  <!-- 01 인원수 -->
  <div class="field-card short">
    <div class="field-head">
      <span class="field-badge">STEP 01</span>
      <label for="people" class="field-title">인원수</label>
      <span class="field-tag">선택 입력</span>
    </div>
    <p class="field-desc">함께 가는 인원을 숫자로 적어주세요.</p>
    <input type="text" name="people" id="people" inputmode="numeric" placeholder="예: 2" >
  </div>

  <!-- 02 시작날짜 -->
  <div class="field-card short is-required">
    <div class="field-head">
      <span class="field-badge">STEP 02</span>
      <label for="datestart" class="field-title">시작날짜</label>
      <span class="field-tag">필수</span>
    </div>
    <p class="field-desc">여행을 출발하는 날짜예요.</p>
    <input type="date" name="datestart" id="datestart" > <%--required --%>
  </div>

  <!-- 03 마지막날짜 -->
  <div class="field-card short is-required">
    <div class="field-head">
      <span class="field-badge">STEP 03</span>
      <label for="dateend" class="field-title">마지막날짜</label>
      <span class="field-tag">필수</span>
    </div>
    <p class="field-desc">집으로 돌아오는 날짜예요.</p>
    <input type="date" name="dateend" id="dateend" >  <%--required --%>
  </div>
  
<div class="field-card">
  <div class="field-head">
    <span class="field-badge">STEP 04</span>
    <span class="field-title">일차별 계획</span>
    <p class="field-warn">* 여행 일정은 최대 10일까지 만들 수 있어요</p> 
    <span class="field-tag">일차별 단일선택</span>
  </div>
  <p class="field-desc">일차마다 지역 하나, 숙박형태 하나, 하고 싶은 것을 적어주세요.</p>
  <p class="field-meta">* 일자를 변경하면 아래 내용은 비워집니다</p>

  <input type="hidden" id="dayCount" name="dayCount" value="0">
  <div id="dayList">
    <p class="dp-empty">시작날짜와 마지막날짜를 먼저 골라주세요.</p>
  </div>
</div>


  <!-- 07 여행스타일 -->
  <div class="field-card">
    <div class="field-head">
      <span class="field-badge">STEP 05</span>
      <span class="field-title">여행스타일</span>
      <span class="field-tag">다중선택</span>
    </div>
    <p class="field-desc">이번 여행에서 중요하게 생각하는 스타일을 골라주세요.</p>
    <div class="chip-group" id="style">
      <label class="chip"><input type="checkbox" name="style" value="휴양/힐링형"><span>휴양/힐링형</span></label>
      <label class="chip"><input type="checkbox" name="style" value="관광/랜드마크형"><span>관광/랜드마크형</span></label>
      <label class="chip"><input type="checkbox" name="style" value="미식형"><span>미식형</span></label>
      <label class="chip"><input type="checkbox" name="style" value="액티비티/체험형"><span>액티비티/체험형</span></label>
      <label class="chip"><input type="checkbox" name="style" value="문화/예술형"><span>문화/예술형</span></label>
      <label class="chip"><input type="checkbox" name="style" value="쇼핑형"><span>쇼핑형</span></label>
    </div>
  </div>

  <!-- 08 추가 요청 내용 -->
  <div class="field-card is-required">
    <div class="field-head">
      <span class="field-badge">STEP 06</span>
      <label for="content" class="field-title">추가하고 싶은 내용</label>
      <span class="field-tag">필수 · 최대 600자</span>
    </div>
    <p class="field-desc">구체적으로 적을수록 내 여행스타일에 맞는 일정표를 받을 수 있어요.</p>
    <!-- 주의: <textarea>와 </textarea> 사이에 공백/줄바꿈이 있으면 placeholder가 안 보이고 required도 통과돼버립니다 -->
    <textarea id="content" name="content" rows="8" maxlength="600"
      placeholder="AI에게 질문시 구체적일수록 자신의 여행스타일에 맞는 여행일정표를 짤 수 있습니다."></textarea>  <%--required --%>
    <div class="help-text">
      <p><b>여행취향</b> 가성비여행 · 배낭여행 · 럭셔리여행 · 호캉스여행 · 캠핑 · 차박</p>
      <p><b>동행형태</b> 나홀로여행 · 커플여행 · 우정여행 · 가족여행 · 효도여행</p>
      <p><b>계획스타일</b> J형 · P형</p>
      <p><b>트렌드</b> 한달살기 · 워케이션 · 맛집탐방 · 빵지순례</p>
    </div>
  </div>

  <input type="submit" class="submit-btn" value="챗봇요청하기">
</form>

<div class="plan-actions" data-html2canvas-ignore="true" style="display:none">

  <button type="button" class="act-btn act-download" onclick="downloadPDF();">
    <span class="act-main">PDF 바로 다운로드</span>
    <span class="act-sub">저장 없이 지금 받기</span>
  </button>

   <button type="button" class="act-btn act-save" onclick="saveItineraryJson();"  id="downloadbtn">
    <span class="act-main">마이페이지에 저장 <em class="act-badge">회원전용</em></span>
    <span class="act-sub">PDF로 최대 15개까지 보관</span>
  </button>
  
   <button type="button" class="act-btn act-kakao" onclick="chatbotpageKakaoShare();">
    <span class="act-main">카카오톡 공유하기 <em class="act-badge">회원전용</em></span>
    <span class="act-sub">만든 pdf파일을 링크로 전달</span>
  </button>
</div>

<section class="plan-section" id="plan" style="display:none">       
  <div id="aiSummaryBox" class="plan-box">...</div>
</section>

<!-- 
<form onsubmit="makePdfNow()" style="display:none;"  id="downloadbtn">
	여행일정을 저장할 파일명 <input type="text" name="pdfname" id="pdfname" />
	! 파일명은 한글, 영문, 숫자, 공백, 언더바(_), 하이픈(-), 괄호만 사용하여 1~20자 이내로 입력해주세요<br>
	!! 확장자 .pdf 생략가능합니다 <br>
</form>
 -->

<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<%-- pdf 라이브러리 --%>
<script src="https://cdnjs.cloudflare.com/ajax/libs/html2pdf.js/0.10.1/html2pdf.bundle.min.js"></script>
<%-- 카카오 내게공유하기 라이브러리 --%>
<script src="https://t1.kakaocdn.net/kakao_js_sdk/2.7.2/kakao.min.js" crossorigin="anonymous"></script>
<%-- 카카오 지도 라이브러리 --%>
<script src="https://dapi.kakao.com/v2/maps/sdk.js?appkey=234376aaa52f550d6e16b505ad3621b9&libraries=services&autoload=false"></script>
<script>

<%-- ============================================== 여행일정표 마이페이지에 PDF로 저장하기(최대 15개까지)[회원전용] ===========================--%>
var diffInDays;
const KAKAO_JS_KEY = "${applicationScope.kakaoJsKey}"; 
const CTX = "${pageContext.request.contextPath}";
const loginId = "${sessionScope.loginId}";   // 세션 속성 이름은 프로젝트에 맞게

const MAX_DAYS = 10;   // 최대 여행 일수 (바꾸고 싶으면 여기만)


/* ============================================== 카카오 SDK 준비 ============================================== */

function initKakao() {
  if (window.Kakao && !Kakao.isInitialized()) {
    Kakao.init(KAKAO_JS_KEY);
  }
}

initKakao();


console.log("로그인회원 확인 : ",loginId);

<%-- 서블릿에서 setAttribute 한 값 4개만 여기서 넘겨줌 --%>
const pStart  = '${startDate}';
const pEnd    = '${endDate}';
const pPeople = '${people}';
const pRegion = '${region}';

//파일 맨 위: 현재 일정 데이터를 담아둘 변수
//공유할 일정 데이터를 담아둘 변수
let planData = null;

//★ 내 파일의 입력칸 id 와 다르면 여기만 바꾸기
const startInput  = document.getElementById('datestart');
const endInput    = document.getElementById('dateend');
const peopleInput = document.getElementById('people');
const dayList     = document.getElementById('dayList');

//선택지 목록 (여기서 추가/삭제)
const REGIONS = ['서울','인천','경기','강원','대전','세종','충북','충남','광주',
              '전북','전남','부산','대구','울산','경북','경남','제주'];
const STAYS   = ['호텔','펜션','게스트하우스','콘도','모텔'];
const WEEK    = ['일','월','화','수','목','금','토'];


//지역별 중심 좌표 (못 찾았을 때 보여줄 위치)
const REGION_CENTER = {
  "서울": [37.5665, 126.9780], "인천": [37.4563, 126.7052], "경기": [37.2750, 127.0095],
  "강원": [37.8228, 128.1555], "대전": [36.3504, 127.3845], "세종": [36.4800, 127.2890],
  "충북": [36.6357, 127.4917], "충남": [36.6588, 126.6728], "광주": [35.1595, 126.8526],
  "전북": [35.8242, 127.1480], "전남": [34.8161, 126.4630], "부산": [35.1796, 129.0756],
  "대구": [35.8714, 128.6014], "울산": [35.5384, 129.3114], "경북": [36.5760, 128.5056],
  "경남": [35.2383, 128.6925], "제주": [33.4996, 126.5312]
};


//===== 1. 메인에서 넘어온 값 자동 선택 =====
if (pStart)  startInput.value = pStart;
if (pEnd)    endInput.value   = pEnd;
if (pPeople && peopleInput) peopleInput.value = pPeople;
if (pStart && pEnd) makeDays(pRegion);


//===== 날짜 선택 검사 =====
	
	// 시작~마지막 날짜가 10일을 넘으면 true
function isOverMaxDays() {
  if (!startInput.value || !endInput.value) return false;
  const dayCnt = Math.round((new Date(endInput.value) - new Date(startInput.value)) / 86400000) + 1;
  return dayCnt > MAX_DAYS;
}

//시작날짜를 고르면: 마지막날짜 달력에서 그 이전 날짜는 아예 못 고르게
function setEndMin() {
  endInput.min = startInput.value;   // 시작날짜 이전은 못 고름

  if (startInput.value) {
    const maxDate = new Date(startInput.value);
    maxDate.setDate(maxDate.getDate() + MAX_DAYS - 1);   // 시작일 포함 10일째
    endInput.max = toDateText(maxDate);
  } else {
    endInput.max = '';
  }
}

setEndMin();   // 메인에서 날짜가 넘어온 경우를 위해 처음에도 한 번 실행

//시작날짜를 바꿨을 때
startInput.addEventListener('change', function () {
	console.log("시작날짜 change 이벤트 함수 진입");
	setEndMin();
	
	// 이미 고른 마지막날짜가 새 시작날짜보다 앞이면 마지막날짜 비우기
	if (endInput.value && endInput.value < startInput.value) {
		 alert('시작날짜가 마지막날짜보다 늦어요. 마지막날짜를 다시 골라주세요.');
			 endInput.value = '';
		 	endInput.focus();
		}
		
		// 시작날짜를 바꿨더니 10일이 넘어가면 마지막날짜 비우기
		if (isOverMaxDays()) {
		  alert('여행 일정은 최대 ' + MAX_DAYS + '일까지 가능해요. 마지막날짜를 다시 골라주세요.');
		  endInput.value = '';
		  endInput.focus();
		}
		
		// 10일을 넘으면
		if (isOverMaxDays()) {
		  alert('여행 일정은 최대 ' + MAX_DAYS + '일까지 가능해요.');
		  endInput.value = '';
		  endInput.focus();
		  makeDays(pRegion);
		  return;
		}
	
		makeDays(pRegion);
	});
	
	//마지막날짜를 바꿨을 때
	endInput.addEventListener('change', function () {
	
	// 1) 시작날짜를 안 골랐으면
	if (!startInput.value) {
	 alert('시작날짜를 먼저 선택해주세요.');
	 endInput.value = '';
	 startInput.focus();
	 makeDays(pRegion);
	 return;
	}
	
	// 2) 마지막날짜가 시작날짜보다 앞이면
	if (endInput.value < startInput.value) {
		 alert('마지막날짜는 시작날짜보다 앞설 수 없어요.');
		 endInput.value = '';
		 endInput.focus();
		 makeDays(pRegion);
		 return;
	}
	
	makeDays(pRegion);
});


//===== 2. 일차 박스 만들기 =====
function makeDays(defaultRegion) {
	console.log("makeDays() 일자박스 만들기 진입");
	dayList.innerHTML = '';
	document.getElementById('dayCount').value = 0;
	
	if (!startInput.value || !endInput.value) {
	 dayList.innerHTML = '<p class="dp-empty">시작날짜와 마지막날짜를 먼저 골라주세요.</p>';
	 return;
	}
	
	const start = new Date(startInput.value);
	const end   = new Date(endInput.value);
	const dayCnt = Math.round((end - start) / 86400000) + 1;   // 86400000 = 하루(ms)
	
	if (dayCnt < 1) {
	 dayList.innerHTML = '<p class="dp-empty">마지막날짜가 시작날짜보다 빨라요.</p>';
	 return;
	}
	
	//최장 10일까지
	if (dayCnt > MAX_DAYS) {
	 dayList.innerHTML = '<p class="dp-empty field-warn">여행 일정은 최대 ' + MAX_DAYS + '일까지 가능해요. 날짜를 다시 골라주세요.</p>';
	 return;
	}
	
	document.getElementById('dayCount').value = dayCnt;
	
	for (let i = 1; i <= dayCnt; i++) {
	 const d = new Date(start);
	 d.setDate(d.getDate() + i - 1);
	 const isLast = (i === dayCnt);
	 dayList.insertAdjacentHTML('beforeend', dayHtml(i, d, isLast, defaultRegion));
	}
	
	dayList.querySelectorAll('.dp-day').forEach(updateSummary);
}


//===== 3. 일차 하나의 HTML =====//
function dayHtml(no, date, isLast, defaultRegion) {
  console.log("dayHtml() 진입 일차별 html 생성");
  const dateText = (date.getMonth() + 1) + '/' + date.getDate() + ' (' + WEEK[date.getDay()] + ')';

  // 지역 칩
  let regionHtml = '<div class="chip-group attached cols-6">';
  
  REGIONS.forEach(r => {
    const checked = (r === defaultRegion) ? 'checked' : '';
    regionHtml += `<label class="chip"><input type="radio" name="region_\${no}" value="\${r}" \${checked}><span>\${r}</span></label>`;
  });
  regionHtml += '</div>';

  // 숙박 칩 (마지막 날은 숙박 없음)
  let stayHtml = '';
  if (isLast) {
    stayHtml = '<p class="dp-no-stay">당일치기 또는 마지막 날은 숙박이 없어요.</p>';
  } else {
    stayHtml = '<div class="chip-group attached cols-5">';
    STAYS.forEach(s => {
      stayHtml += `<label class="chip"><input type="radio" name="stay_\${no}" value="\${s}"><span>\${s}</span></label>`;
    });
    stayHtml += '</div>';
  }

  const openAttr = (no === 1) ? 'open' : '';   // 1일차만 펼치기

  return `
  <details class="dp-day" \${openAttr}>
    <summary>
      <span class="dp-num">\${no}일차</span>
      <span class="dp-date">\${dateText}</span>
      <span class="dp-sum"></span>
    </summary>
    <div class="dp-body">
      <div class="dp-row">
        <div class="dp-label">지역</div>
        <div class="dp-content">\${regionHtml}</div>
      </div>
      <div class="dp-row">
        <div class="dp-label">숙박</div>
        <div class="dp-content">\${stayHtml}</div>
      </div>
      <div class="dp-row">
        <div class="dp-label">계획</div>
        <div class="dp-content">
          <input type="text" name="plan_\${no}" maxlength="100"
                 placeholder="예) 해운대 산책, 돼지국밥 먹기">
        </div>
      </div>
    </div>
  </details>`;
}


//===== 4. 접힌 줄에 보이는 요약 ("부산 · 호텔 · 해운대 산책") =====
function updateSummary(day) {
	console.log("updateSummary() 접힌줄 요악 ");
	const region = day.querySelector('input[name^="region_"]:checked');
	const stay   = day.querySelector('input[name^="stay_"]:checked');
	const plan   = day.querySelector('input[name^="plan_"]').value.trim();
	
	const parts = [];
	if (region) parts.push(region.value);
	if (stay)   parts.push(stay.value);
	if (plan)   parts.push(plan);
	
	day.querySelector('.dp-sum').textContent = parts.length ? parts.join(' · ') : '아직 선택 안 함';
}

//칩을 누르거나 계획을 쓰면 그 일차 요약 갱신
dayList.addEventListener('change', e => updateSummary(e.target.closest('.dp-day')));
dayList.addEventListener('input',  e => updateSummary(e.target.closest('.dp-day')));


<%-- ============================================== 챗봇대화 form 작성 =======================================================--%>
 

//data: AI가 준 일정 데이터 객체
let savedPlanId = null;   // 마이페이지에 저장된 이 일정의 번호 (저장 전엔 null)

/* ---------------------------------------------------------
   마이페이지에 저장만 하는 함수 (저장 버튼, 공유 버튼 둘 다 사용)
   - 성공하면 planid 를 돌려줌
   - 이미 저장했으면 다시 저장하지 않고 그 번호를 그대로 돌려줌
   --------------------------------------------------------- */
function savePlanToMypage() {
  if (savedPlanId) {
    return Promise.resolve(savedPlanId);   // 중복 저장 막기
  }

  const title = planData.title || '나의 여행 일정';

  const params = new URLSearchParams();
  params.append('title', title);
  params.append('planjson', JSON.stringify(planData));  // 객체 → 문자열

  return fetch(CTX + '/Chatbot/itineraryjson', {
    method: 'POST',
    body: params
  })
  .then(res => {
    // 서버가 JSON이 아닌 에러 페이지를 보내도 멈추지 않게 기본 메시지 준비
    return res.json()
      .catch(() => ({ success: false, message: '서버 오류가 발생했습니다.' }))
      .then(result => {

        // 401 : 로그인 풀림 → 로그인 페이지로
        if (res.status === 401) {
          alert(result.message);
          moveToLogin();
          throw new Error(result.message);
        }

        // 409(15개 초과), 400, 500 등 나머지 실패
        if (!res.ok || !result.success) {
          alert(result.message);
          throw new Error(result.message);
        }

        // 성공
        savedPlanId = result.planid;   // ★ 저장된 번호 기억
        return savedPlanId;
      });
  });

}//savePlanToMypage


/* ---------------------------------------------------------
   [마이페이지에 저장] 버튼
   --------------------------------------------------------- */
function saveItineraryJson() {
  if (!planData) {
    alert('먼저 일정을 생성해 주세요.');
    return;
  }
  if (!loginId) {
    alert('회원가입 후 이용가능합니다.');
    return;
  }
  if (savedPlanId) {
    alert('이미 마이페이지에 저장된 일정이에요.');
    return;
  }

  savePlanToMypage()
    .then(() => {
      alert('마이페이지 저장성공');
    })
    .catch(err => {
      console.error(err);
    });
}

/* ---------------------------------------------------------
   [카카오톡 공유] 버튼 : 저장(안 했으면) → 공유키 → 카톡
   --------------------------------------------------------- */
/* function chatbotpageKakaoShare() {
  if (!planData) {
    alert('먼저 일정을 생성해 주세요.');
    return;
  }
  if (!loginId) {
    alert('회원가입 후 이용가능합니다.');
    return;
  }
  
  initKakao();

  // 아직 저장 안 했으면 먼저 물어보기
  if (!savedPlanId) {
    if (!confirm('카카오톡 공유를 하려면 일정이 마이페이지에 저장돼요.\n저장하고 공유할까요?')) {
      return;
    }
  }

  savePlanToMypage()
    .then(planid => {
      shareKakaoPlan(planid, planData);    // mypage_plan.js 에서 복사한 함수
    })
    .catch(err => {
      console.error(err);
    });
}
 */
   /* ---------------------------------------------------------
   [카카오톡 공유] 버튼 : 저장(안 했으면) → 공유키 → 카톡
   --------------------------------------------------------- */
function chatbotpageKakaoShare() {
  if (!planData) {
    alert('먼저 일정을 생성해 주세요.');
    return;
  }
  if (!loginId) {
    alert('회원가입 후 이용가능합니다.');
    return;
  }

  initKakao();

  // 아직 저장 안 했으면 먼저 물어보기
  if (!savedPlanId) {
    if (!confirm('카카오톡 공유를 하려면 일정이 마이페이지에 저장돼요.\n저장하고 공유할까요?')) {
      return;
    }
  }

  savePlanToMypage()
    .then(planid => {
      shareKakaoPlan(planid, planData);    // mypage_plan.js 에서 복사한 함수
    })
    .catch(err => {
      console.error(err);
    });
}
   

<%-- ============================================== 챗봇대화 form 작성 =======================================================--%>

//날짜를 "2026-10-20" 형태 글자로 만들기
function toDateText(d) {
	return d.getFullYear() + '-'
	    + String(d.getMonth() + 1).padStart(2, '0') + '-'
	    + String(d.getDate()).padStart(2, '0');
}

//일차별 입력값 모으기 → [{day:1, date:'2026-10-20', region:'부산', stay:'호텔', plan:'...'}, ...]
function collectDays() {
	const dayCnt = Number(document.getElementById('dayCount').value);
	const start = new Date(startInput.value);
	const days = [];

	for (let i = 1; i <= dayCnt; i++) {
		 const regionEl = document.querySelector('input[name="region_' + i + '"]:checked');
		 const stayEl   = document.querySelector('input[name="stay_' + i + '"]:checked');
		 const planEl   = document.querySelector('input[name="plan_' + i + '"]');
		
		 const d = new Date(start);
		 d.setDate(d.getDate() + i - 1);
		
		 let makeday ={
			 day: i,
			 date: toDateText(d),
			 region: regionEl ? regionEl.value : '',
			 stay: stayEl ? stayEl.value : '',     // 마지막 날은 항상 ''
			 plan: planEl ? planEl.value.trim() : ''
		 }
		 
		 console.log("만든 개별일자 : ",makeday);
		 
		 days.push(makeday);
		}
	return days;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
function conversationFetchAskChatbot(e) {
	console.log("conversationFetchAskChatbot() 쳇봇요청 fetch 실행 함수 진입");
	 e.preventDefault();
	
	const people    = peopleInput.value.trim();
	const datestart = startInput.value;
	const dateend   = endInput.value;
	const content   = document.getElementById('content').value.trim();
	
	// 여행스타일 (다중선택 그대로)
	const styleValues = [];
	document.querySelectorAll('input[name="style"]:checked').forEach(function (box) {
	 styleValues.push(box.value);
	});
	
	// ---- 입력값 검사 ----
	if (!datestart || !dateend) {
	 alert('시작날짜와 마지막날짜를 골라주세요.');
	 return;
	}
	
	const days = collectDays();
	if (days.length === 0) {
	 alert('날짜를 다시 확인해주세요.');
	 return;
	}
	
	// 지역은 일차마다 꼭 고르게 (안 고른 일차를 펼쳐서 보여줌)
	for (let i = 0; i < days.length; i++) {
	 if (!days[i].region) {
	   alert(days[i].day + '일차 지역을 골라주세요.');
	   const dayBox = dayList.querySelectorAll('.dp-day')[i];
	   dayBox.open = true;
	   dayBox.scrollIntoView({ behavior: 'smooth', block: 'center' });
	   return;
	 }
	}
	
	if (!content) {
	 alert('추가하고 싶은 내용을 적어주세요.');
	 document.getElementById('content').focus();
	 return;
	}
	
	const sendData = {
	 people: people,
	 datestart: datestart,
	 dateend: dateend,
	 styleValues: styleValues,
	 content: content,
	 days: days
	};
	console.log('챗봇으로 보낼 값 : ', sendData);
	
	
	
	// 두 번 클릭 방지
	const submitBtn = document.querySelector('.submit-btn');
	submitBtn.disabled = true;
	submitBtn.value = '일정 만드는 중... 여행기간에 따라 1분 이상 소요될 수 있습니다'; 
	
/* 	const sendData=	        		    {
	        "people": "2",
	        "datestart": "2026-10-15",
	        "dateend": "2026-10-17",
	        "styleValues": [
	            "휴양/힐링형"
	        ],
	        "content": "가성비여행  우정여행",
	        "days": [
	            {
	                "day": 1,
	                "date": "2026-10-15",
	                "region": "부산",
	                "stay": "호텔",
	                "plan": "광안리 야경"
	            },
	            {
	                "day": 2,
	                "date": "2026-10-16",
	                "region": "부산",
	                "stay": "호텔",
	                "plan": "부평 깡통시장 점심 저녁 해결"
	            },
	            {
	                "day": 3,
	                "date": "2026-10-17",
	                "region": "부산",
	                "stay": "",
	                "plan": "해동 용궁사 방문"
	            }
	        ]
	    };
 */	
	fetch('${ctx}/Chatbot/newConversationPro', {
	 method: 'POST',
	 headers: { 'Content-Type': 'application/json; charset=UTF-8' },
	 body: JSON.stringify(sendData)
	})
	.then(function (response) {
		 // 실패(400·500·503)여도 서버가 보낸 message를 읽기 위해 먼저 json으로 바꿈
		 return response.json()
		   .catch(function () {
		     // JSON이 아닌 응답(에러 페이지 HTML 등)
		     throw new Error('요약을 불러오지 못했습니다. 잠시 후 다시 시도해주세요.');
		   })
		   .then(function (data) {
		     if (!response.ok || data.success !== true) {
		       throw new Error(data.message || '요약을 불러오지 못했습니다. 잠시 후 다시 시도해주세요.');
		     }
		     return data;
		   });
		})
		.then(function (data) {
		 console.log("fetch() 응답옴"); 
		 if (data.error) throw new Error(data.error);
		
		 const textContent = data.result;
		 if (!textContent) throw new Error('서버로부터 빈 응답을 받았습니다.');
		
		 const summaryData = JSON.parse(textContent);
		 console.log('서버로부터 받은 응답 js객체로 변환 : ', summaryData);
		
		 planData = summaryData;      // 카카오 공유·마이페이지 저장용
		 renderPlan(summaryData);
		
		 $('.plan-actions').show();
		 $('#plan').show();
		})
	.catch(function (error) {
		 console.error('AI 요약 로드 실패:', error);
		 // 우리가 만든 Error면 그 문구, 네트워크·JSON.parse 오류(영어 문구)면 기본 문구
		 const msg = (error.name === 'Error')
		     ? error.message
		     : '요약을 불러오지 못했습니다. 잠시 후 다시 시도해주세요.';

		 $('#plan').show();
		 document.getElementById('aiSummaryBox').innerHTML =
		 '<p style="text-align:center;color:#9f1239;">요약을 불러오지 못했습니다. 잠시 후 다시 시도해주세요.</p>';
		 box.querySelector('p').textContent = msg;   // 문구를 글자로만 넣기 (태그 실행 방지)
	})
	.finally(function () {
		 submitBtn.disabled = false;
		 submitBtn.value = '챗봇요청하기';
	});
}

function resetForm() {
  const form = document.getElementById('conversationForm');
  form.reset();        // 입력칸, 체크박스, radio 전부 처음 상태로
  makeDays();          // 날짜가 비었으니 "날짜를 먼저 골라주세요" 문구로 돌아감
}

 
<%-- ============================================== PDF 저장 (html2pdf.js) ===============================================--%>
 
//파일 이름 만들기: "부산 휴양_힐링 3일 일정_20261010.pdf"
function makeFileName() {
	console.log("makeFileName() 파일이름저장 함수 진입");
  const titleEl = document.getElementById("planTitle");
  const title = titleEl ? titleEl.textContent : "여행일정표";

  const d = new Date();
  const today = d.getFullYear()
      + String(d.getMonth() + 1).padStart(2, "0")
      + String(d.getDate()).padStart(2, "0");

  // 파일 이름에 못 쓰는 문자 \ / : * ? " < > | 를 _ 로 바꾸기
  const safeTitle = title.replace(/[\\/:*?"<>|]/g, "_").trim();

  return safeTitle + "_" + today + ".pdf";
}

// PDF 설정 (다운로드, 마이페이지 저장 둘 다 이걸 씀)
function getPdfOptions() {
	console.log("getPdfOptions() PDF 설정 진입");
  return {
    margin: [10, 10, 10, 10],                  // 위, 왼쪽, 아래, 오른쪽 여백 (mm)
    filename: makeFileName(),
    image: { type: "jpeg", quality: 0.95 },
    html2canvas: {
      scale: 2,             // 2배로 찍어서 글씨가 선명하게
      useCORS: true,        // 카카오 지도 이미지도 찍히도록 시도
      windowWidth: 1200     // 휴대폰에서 눌러도 PC 화면 배치(지도|노선도)로 찍기
    },
    jsPDF: { unit: "mm", format: "a4", orientation: "portrait" },
    //pagebreak: {
    //  mode: ["css", "legacy"],
    //  avoid: [".plan-banner", ".day-map", ".stop"]   // 이 요소들은 페이지 중간에서 안 잘리게
      
    pagebreak: { mode: ['css'], avoid: '.stop' },
  }
}


// 즉시 다운로드
async function downloadPDF() {
	console.log("downloadPDF() 즉시다운로드 onclick 함수 진입")
  const box = document.getElementById("aiSummaryBox");
  //window.scrollTo(0, 0);                 // 스크롤 맨 위로 (캡처 어긋남 방지)
  box.classList.add("pdf-mode");      // PDF 전용 스타일 켜기

  const opt = {
    margin: [10, 10, 10, 10],            // 위, 왼쪽, 아래, 오른쪽 (mm)
    filename: makeFileName(), 
    //(document.title || "여행일정표") + ".pdf",
    image: { type: "jpeg", quality: 0.95 },
    html2canvas: {
      scale: 2,                          // 글자 선명하게
      scrollX: 0,
      scrollY: 0,
      // 카카오 지도는 캡처에서 아예 제외 → CORS 에러 사라짐
      ignoreElements: function (el) {
        return el.classList && el.classList.contains("day-map");
      }
    },
    //가로방향
    jsPDF: { unit: "mm", format: "a4", orientation: "landscape" },   // portrait → landscape
    pagebreak: { mode: ["css", "legacy"], avoid: [".stop", ".day-head", ".plan-banner", ".day-mini"] }
  };

  html2pdf().set(opt).from(box).save()
    .finally(function () {
    	box.classList.remove("pdf-mode");   // 끝나면 화면 원래대로
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
 
//====================================================== 챗봇 일정표 처리 시작 ===================================================== //
 
//=============================================
//2. 화면 그리기
//- 각 일차 안에 [스케치 지도 | 세로 노선도] 나란히
//- 색은 전부 CSS :root 에서 바꿉니다 (JS 에는 색 없음)
//=============================================

//태그 만들기 도우미 (textContent를 써서 AI 응답에 HTML이 섞여도 안전)
function el(tag, className, text) {
const node = document.createElement(tag);
if (className) node.className = className;
if (text !== undefined) node.textContent = text;
return node;
}

//=============================================
//스케치 지도
//=============================================

//지역별 지도 위 위치 (x: 오른쪽으로 갈수록 커짐, y: 아래로 갈수록 커짐)
//핀 위치가 마음에 안 들면 이 숫자만 고치면 됩니다
const REGION_POS = {
"서울": { x: 96,  y: 92 },
"인천": { x: 66,  y: 98 },
"경기": { x: 112, y: 120 },
"강원": { x: 170, y: 88 },
"충북": { x: 145, y: 168 },
"세종": { x: 112, y: 176 },
"충남": { x: 78,  y: 172 },
"대전": { x: 122, y: 198 },
"경북": { x: 200, y: 192 },
"대구": { x: 186, y: 248 },
"전북": { x: 96,  y: 238 },
"울산": { x: 228, y: 276 },
"경남": { x: 162, y: 292 },
"광주": { x: 82,  y: 290 },
"부산": { x: 214, y: 312 },
"전남": { x: 98,  y: 322 },
"제주": { x: 92,  y: 398 }
};

//폼에서 체크한 지역 가져오기 (위에서 아래 순서)
function getCheckedRegions() {
const checked = document.querySelectorAll('input[name="region"]:checked');
const list = [];
checked.forEach(function (box) { list.push(box.value); });
return list;
}

function makeMap(regions) {
const wrap = el("div", "map-card");
wrap.appendChild(el("p", "map-label", "TRIP MAP"));

// 지도에 있는 지역만 사용 (REGION_POS 에 없는 이름은 무시)
const picked = regions.filter(function (name) { return REGION_POS[name]; });

// 1) 바탕 : 한반도 모양 + 제주도 + 울릉도
let svg =
 '<svg class="map-svg" viewBox="0 0 300 430" role="img" aria-label="여행 지역 지도">' +
 '<path class="map-land" d="M62,62 L90,58 L120,50 L150,42 L185,30 L198,55 L212,85' +
 ' L226,120 L238,160 L246,200 L250,240 L248,270 L240,300 L228,318 L212,326' +
 ' L192,331 L170,338 L150,333 L130,345 L110,342 L90,352 L70,345 L56,330' +
 ' L62,305 L52,285 L64,262 L56,240 L70,222 L58,200 L46,182 L62,170 L58,150' +
 ' L70,135 L60,118 L72,100 L62,82 Z"/>' +
 '<ellipse class="map-land" cx="92" cy="398" rx="32" ry="13"/>' +
 '<circle class="map-land" cx="282" cy="118" r="5"/>';

// 2) 모든 지역 위치에 흐린 점
Object.keys(REGION_POS).forEach(function (name) {
 const p = REGION_POS[name];
 svg += '<circle class="map-dot" cx="' + p.x + '" cy="' + p.y + '" r="3"/>';
});

// 3) 선택한 지역끼리 점선으로 잇기
if (picked.length > 1) {
 const points = picked.map(function (name) {
   return REGION_POS[name].x + "," + REGION_POS[name].y;
 }).join(" ");
 svg += '<polyline class="map-path" points="' + points + '"/>';
}

// 4) 점 핀 + 지역 이름표
picked.forEach(function (name) {
 const p = REGION_POS[name];
 svg +=
   '<rect class="map-tag" x="' + (p.x + 12) + '" y="' + (p.y - 11) + '" width="44" height="22" rx="11"/>' +
   '<text class="map-tag-text" x="' + (p.x + 34) + '" y="' + (p.y + 4) + '">' + name + '</text>' +
   '<circle class="map-pin" cx="' + p.x + '" cy="' + p.y + '" r="7"/>';
});

svg += '</svg>';

const area = el("div", "map-area");
area.innerHTML = svg;   // 지역 이름은 REGION_POS 에 있는 이름만 들어가서 안전
wrap.appendChild(area);

wrap.appendChild(el("p", "map-note",
 picked.length === 0 ? "선택한 지역이 없어요" : picked.join(" → ")));
return wrap;
}

//=============================================
//일정표 전체 그리기
//=============================================
function renderPlan(data) {
	
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
	
	// ▼ 여기에 추가 : PDF 가로 배치 때 몇 칸으로 나눌지 (최대 3칸)
	const cols = Math.min(data.days.length, 3);
	daysBox.style.setProperty("--pdf-cols", cols);
	
	data.days.forEach(function (day) { // 일차(day)마다 한 번씩 반복
	    const section = el("section", "day"); // 하루치 전체를 담을 큰 상자
	    // ... 기존 코드 ...
	
		const head = el("div", "day-head");  // "1일차 ---- 2026-10-11" 줄
		head.appendChild(el("span", "day-num", day.day + "일차")); // 왼쪽: 1일차
		head.appendChild(el("span", "day-date", day.date)); // 오른쪽: 날짜
		section.appendChild(head); // 제목 줄을 큰 상자에 넣기
		section.appendChild(el("p", "day-theme", day.theme));  // 그날 테마 문장
		
		// [지도 | 세로 노선도] 를 담는 2칸 상자
		const dayBody = el("div", "day-body"); // 2칸으로 나뉘는 상자 (CSS grid)
		const mapBox = el("div", "day-map");   // SVG 대신 이 div // 카카오 지도가 그려질 빈 칸
		dayBody.appendChild(mapBox); // ① 먼저 넣었으니 왼쪽 칸

	    // ... 타임라인 코드 ...
		const list = el("ol", "timeline"); // 오른쪽 세로 노선도 목록
		
		day.items.forEach(function (item) { // 그날 장소(item)마다 반복
		  const li = el("li", "stop"); // 장소 하나 = 목록 한 줄
		  li.appendChild(el("div", "stop-time", item.time)); // 시간 (09:00)
		  li.appendChild(el("div", "stop-line"));// 세로 선 + 동그라미
		
		  const body = el("div", "stop-body"); // 장소 내용 상자
		  body.appendChild(el("h3", "stop-place", item.place)); // 장소 이름
		  body.appendChild(el("p", "stop-activity", item.activity)); // 할 일 (회색 상자)
		
		  if (item.tip) { // tip이 있을 때만
		    const tip = el("p", "stop-tip"); // TIP 줄
		    tip.appendChild(el("strong", "", "TIP"));
		    tip.appendChild(document.createTextNode(item.tip)); // 팁 내용 글자
		    body.appendChild(tip); // 내용 상자에 TIP 넣기
		  }
		
		  li.appendChild(body); // 한 줄에 내용 상자 넣기
		  list.appendChild(li); // 목록에 한 줄 추가
		});//items.forEach
		
		dayBody.appendChild(list); // ② 두 번째로 넣었으니 오른쪽 칸
		section.appendChild(dayBody); // 2칸 상자를 하루 상자에 넣기
		daysBox.appendChild(section); // 하루 상자를 화면에 붙이기
		
/* 		section.day
		├─ div.day-head      (1일차 / 날짜)
		├─ p.day-theme       (테마)
		└─ div.day-body      ← 2칸
		   ├─ div.day-map    ← 왼쪽: 지도
		   └─ ol.timeline    ← 오른쪽: 노선도
 */	
	});//data.days.forEach

// 모든 일차가 화면에 붙은 뒤에 지도 그리기
	kakao.maps.load(function () {
	    const mapBoxes = document.querySelectorAll(".day-map");
	    data.days.forEach(function (day, i) {
	    drawDayMap(mapBoxes[i], day.items, day.region);
	});//kakao.maps.load
});
<%-- 카카오지도 끝 --%>

<%-- svg  지도 --%>
<%--
data.days.forEach(function (day) {
 const section = el("section", "day");

 const head = el("div", "day-head");
 head.appendChild(el("span", "day-num", day.day + "일차"));
 head.appendChild(el("span", "day-date", day.date));
 section.appendChild(head);
 section.appendChild(el("p", "day-theme", day.theme));

 // [지도 | 세로 노선도] 를 담는 2칸 상자
 const dayBody = el("div", "day-body");

 // 지도 : 그날 지역(day.regions) → 전체 지역(data.regions) → 폼에서 체크한 지역 순서로 사용
 const regions = day.regions || data.regions || getCheckedRegions();
 const mapBox = el("div", "day-map");
 mapBox.appendChild(makeMap(regions));
 dayBody.appendChild(mapBox);

 // 세로 노선도
 const list = el("ol", "timeline");

 day.items.forEach(function (item) {
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
 });

 dayBody.appendChild(list);
 section.appendChild(dayBody);
 daysBox.appendChild(section);
});
 --%>

}

//====================================================== 챗봇 일정표 처리 종료 ===================================================== //


//====================================================== 카카오지도맵이용 지도그리기 시작 ===================================================== //

// 장소 이름 → 좌표 찾기 (못 찾으면 null)
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
async function drawDayMap(mapBox, items, region) {
  
	 const c = REGION_CENTER[region] || REGION_CENTER["서울"];   // 지역이 없으면 서울
	  const map = new kakao.maps.Map(mapBox, {
	    center: new kakao.maps.LatLng(c[0], c[1]),
	    level: 9                                                  // 도 단위가 보이는 정도
	  });

	  const ps = new kakao.maps.services.Places();
	  const bounds = new kakao.maps.LatLngBounds();
	  const path = [];

	  for (let i = 0; i < items.length; i++) {
	    //구체적단어사용위해 아래코드사용
   		console.log("일정표 내 지도그리는 함수 drawDayMap() 내 장소: "+items[i].place + ", 지역 : "+region +" 얻고 이제 findPosition() 호출한다.");
	    const pos = await findPosition(ps, items[i].place, region);
	    if (!pos) continue;

	    path.push(pos);
	    bounds.extend(pos);

	    new kakao.maps.CustomOverlay({
	      map: map,
	      position: pos,
	      content: '<div class="map-pin">' + (i + 1) + '</div>'
	    });
	  }

	  new kakao.maps.Polyline({
	    map: map,
	    path: path,
	    strokeWeight: 4,
	    strokeColor: "#b85c38",
	    strokeOpacity: 0.8,
	    strokeStyle: "shortdash"
	  });

	  // 핀 개수에 따라 화면 맞추기
	  if (path.length === 1) {
	    map.setCenter(path[0]);   // 1개면 그 장소 중심으로
	    map.setLevel(5);          // 동네가 보이는 정도
	    //level은 숫자가 작을수록 확대예요. 너무 멀거나 가까우면 9, 5만 바꿔보세요.
	  } else if (path.length > 1) {
	    map.setBounds(bounds);    // 2개 이상이면 전부 보이게
	  }
	  // 0개면 처음 잡은 지역 중심 그대로
}

//애매한 단어를 지워서 동네 이름만 남기기
//예: "남포동 인근 맛집" → "남포동", "부평 깡통시장 내 저녁 맛집" → "부평 깡통시장"
function cleanPlaceName(place) {
	return place
	 .replace(/인근|근처|주변|내|저녁|점심|아침|맛집|카페/g, "")
	 .replace(/\s+/g, " ")
	 .trim();
}


 
<%-- "맛집"이 들어간 이름은 처음부터 동네로 찍기
1차 검색에서 아무 식당이나 잡히는 게 싫다면, "맛집"이 들어간 이름은 바로 정리한 이름으로 검색하게 할 수도 있어요. --%>
async function findPosition(ps, place, region) {
  const keyword = place.includes("맛집") ? cleanPlaceName(place) : place;
  return await searchPlace(ps, (region || "") + " " + keyword);
}

/* 
확인용
new kakao.maps.services.Places().keywordSearch("부산 남포동 인근 맛집", function (data, status) {
  console.log(status, data[0]);
});
status가 ZERO_RESULT면 결과가 없는 거고, data[0].place_name을 보면 어떤 장소가 잡혔는지 알 수 있어요.
*/
//====================================================== 카카오지도맵이용 지도그리기 종료 ===================================================== //


//===================================================== 다운로드시 파일명 지정 ===================================================== //
 
function validateUploadName() {
	console.log("conversation.jsp 파일 validateUploadName() ")
	const pdfname = document.getElementById('pdfname').value.trim();
  
	if(pdfname ==null){
		alert("pdf파일명을 입력헤주세요!");
		return;
	}else{
	  // 파일명 정규표현식 검사
	  const fileNameRegex = /^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ _\-\(\)]{1,20}$/;
	
	  if (!fileNameRegex.test(pdfname)) {
		console.log("pdfname 정규식 맞지않음");
	    alert('파일명은 한글, 영문, 숫자, 공백, 언더바(_), 하이픈(-), 괄호만 사용하여 1~20자 이내로 입력해주세요.');
	    return;
	  }
	
	  // 확장자(.pdf)를 중복으로 붙이는 것 방지 처리
	  const safeFileName = pdfname.endsWith('.pdf') 
	    ? inputFileName 
	    : inputFileName + '.pdf';
	  return safileName
  }//if-else
} 



//===================================================== 카톡공유처리 ===================================================== //

/*
  1) 서버에서 공유키 받기   (처음 누를 때 만들어지고, 다음부턴 같은 키)
  2) 공유 주소 만들기       : http://내사이트/컨텍스트/share/plan?key=…
  3) 카카오 공유 창 띄우기
*/

  
function shareKakaoPlan(planid, data) {
	
  if (!window.Kakao || !Kakao.isInitialized()) {
    alert("카카오 SDK를 불러오지 못했습니다.");
    return;
  }

  const params = new URLSearchParams();
  params.append("planid", planid);

  fetch(CTX + "/Mypage/itineraryJsonSharekeyPro", { method: "POST", body: params })
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



/* 
Kakao.init('234376aaa52f550d6e16b505ad3621b9');

function sendItineraryToKakao(){
	console.log("itineraryUpload.jsp 파일 sendItineraryToKakao() ")
	let elkakaobtn =document.getElementById("kakaobtn");
	let downladUrl = elkakaobtn.dataset.downloadUrl;
	if(downladUrl){
		Kakao.Share.sendDefault({
	        objectType: 'feed',
	        content: {
	            title: '회원님의 여행 일정',
	            description: '챗봇이 추천한 숙소 및 여행 일정표입니다.',
	            imageUrl: 'https://mysite.com/images/thumb.jpg', //// 공유 카드에 보일 대표 이미지
	            link: {
	                mobileWebUrl: downloadUrl,
	                webUrl: downloadUrl,
	            },
	        },
	    });	
		
	}else{
 	  alert("먼저 업로드해주세요!");
	  return;
	}	
}
 */
</script>
