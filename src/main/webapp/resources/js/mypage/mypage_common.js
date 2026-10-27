// =========================================================
// 주제: 파일 역할
// 설명: 마이페이지 공통 경로와 데이터 처리에 필요한 보조 기능을 제공
// =========================================================

/* =========================================
   mypage-common.js
   모든 탭에서 같이 쓰는 함수 모음 (제일 먼저 불러오기)
   ========================================= */

// true  : 가짜 데이터로 화면만 확인
// false : 실제 서블릿에서 fetch로 받아오기
var USE_DUMMY = true;

// JSP에서 <body data-ctx="${pageContext.request.contextPath}"> 로 넣어둔 값
var CTX = document.body.getAttribute('data-ctx') || '';

// 로그인 만료(401)면 안내 후 로그인 페이지로 이동
// =========================================================
// 주제: checkLogin 처리
// 설명: 입력값 또는 현재 상태가 조건에 맞는지 검사
// =========================================================
function checkLogin(res) {
  if (res.status === 401) {
    alert('로그인이 필요합니다. 로그인 페이지로 이동합니다.');
    location.href = CTX + '/Login';
    throw new Error('LOGIN_REQUIRED');   // 다음 then 실행 안 되게 멈춤
  }
  return res;
}

// 데이터 가져오기 (가짜 데이터 / 실제 fetch 자동 선택)
// =========================================================
// 주제: getData 처리
// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
// =========================================================
function getData(url, dummy, callback) {
  if (USE_DUMMY) {
    callback(dummy);
    return;
  }
  fetch(CTX + url)
  .then(checkLogin)
    .then(function (res) {
      if (!res.ok) throw new Error('서버 오류 ' + res.status);
      return res.json();
    })
    .then(callback)
    .catch(function (err) {
	  if (err.message === 'LOGIN_REQUIRED') return;
      console.error(err);
      alert('데이터를 불러오지 못했습니다.');
    });
}

// 서버로 요청 보내기 (삭제, 취소 등)
// =========================================================
// 주제: postData 처리
// 설명: postData 기능에 필요한 처리 로직을 수행
// =========================================================
function postData(url, params, callback) {
  if (USE_DUMMY) {
    callback({ result: 'ok' });
    return;
  }
  fetch(CTX + url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: new URLSearchParams(params)
  })
  .then(checkLogin)
    .then(function (res) { return res.json(); })
    .then(callback)
    .catch(function (err) {
	  if (err.message === 'LOGIN_REQUIRED') return;
      console.error(err);
      alert('요청을 처리하지 못했습니다.');
    });
}

// innerHTML에 글자를 넣을 때 태그가 실행되지 않게 바꿔주기
// (리뷰 제목에 <script> 같은 걸 써도 안전)
// =========================================================
// 주제: esc 처리
// 설명: esc 기능에 필요한 처리 로직을 수행
// =========================================================
function esc(text) {
  if (text === null || text === undefined) return '';
  return String(text)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}

// 50000 → 50,000원
// =========================================================
// 주제: won 처리
// 설명: won 기능에 필요한 처리 로직을 수행
// =========================================================
function won(num) {
  return Number(num || 0).toLocaleString() + '원';
}

// 사진이 없거나 깨질 때 보여줄 회색 이미지
var NO_IMG = 'data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 width=%22200%22 height=%22150%22%3E%3Crect width=%22100%25%22 height=%22100%25%22 fill=%22%23e5e7eb%22/%3E%3C/svg%3E';

// =========================================================
// 주제: imgTag 처리
// 설명: imgTag 기능에 필요한 처리 로직을 수행
// =========================================================
function imgTag(src, alt) {
  return '<img src="' + esc(src || NO_IMG) + '" alt="' + esc(alt) +
         '" onerror="this.src=NO_IMG">';
}

// 예약 상태 코드 → 화면 글자 + 색
// DB에 실제로 쓰는 상태값에 맞게 고쳐주세요
var STATUS = {
  REQUEST:   { text: '예약대기', css: 'status-wait' },
  CONFIRMED: { text: '예약확정', css: 'status-ok' },
  DONE:      { text: '이용완료', css: 'status-done' },
  CANCELED:  { text: '예약취소', css: 'status-cancel' }
};

// =========================================================
// 주제: statusBadge 처리
// 설명: statusBadge 기능에 필요한 처리 로직을 수행
// =========================================================
function statusBadge(code) {
  var s = STATUS[code] || { text: code, css: 'status-done' };
  return '<span class="status ' + s.css + '">' + s.text + '</span>';
}

