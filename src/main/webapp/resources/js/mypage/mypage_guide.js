// =========================================================
// 주제: 파일 역할
// 설명: 마이페이지 가이드 예약 목록과 관련 동작을 처리
// =========================================================

/* =========================================================
 * 주제: 마이페이지 가이드 예약/테스트 결제 내역
 * 설명: 기존 DUMMY_GUIDE_LIST / DUMMY_GUIDE_DETAIL 사용 중단
 *       공통 USE_DUMMY는 다른 팀원 기능 보호를 위해 수정하지 않음
 * API: /mypage/guide/list, /detail, /cancel
 * ========================================================= */
var guideList = [];
var guideFilter = 'ALL';
var guideDetailCache = {};
var guideBusy = false;
var GUIDE_MYPAGE_VERSION = '20261019-guide-layout-3';
var GUIDE_STATES = {
  PENDING:    { text: '결제대기', css: 'status-wait' },
  CONFIRMING: { text: '결제확인중', css: 'status-wait' },
  PAID:       { text: '결제완료', css: 'status-ok' },
  REVIEW:     { text: '결제확인중', css: 'status-wait' },
  CANCELED:   { text: '취소', css: 'status-cancel' }
};

// =========================================================
// 주제: guideCtx 처리
// 설명: guideCtx 기능에 필요한 처리 로직을 수행
// =========================================================
function guideCtx() {
  var bodyContext = document.body ? (document.body.getAttribute('data-ctx') || '') : '';
  if (bodyContext) return bodyContext.replace(/\/$/, '');
  if (typeof CTX === 'string' && CTX) return CTX.replace(/\/$/, '');
  return '/' + (location.pathname.split('/')[1] || '');
}
// =========================================================
// 주제: guideEsc 처리
// 설명: guideEsc 기능에 필요한 처리 로직을 수행
// =========================================================
function guideEsc(x) { return esc(x == null ? '' : x); }
function guideStatus(s) { return GUIDE_STATES[s] || {text: String(s || '상태 미확인'), css:'status-wait'}; }
function guideGroup(s) { return s === 'PAID' ? 'PAID' : (s === 'CANCELED' ? 'CANCELED' : (s === 'PENDING' ? 'PENDING' : 'PENDING')); }
function guideMoney(n) { return Number(n || 0).toLocaleString('ko-KR') + '원'; }
// 표에는 짧은 날짜를 표시하되 전체 정보는 title에 보관합니다.
function guideShortDate(value) {
  var s = String(value == null ? '' : value).trim();
  var hit = s.match(/^(\d{4}-\d{2}-\d{2})/);
  return hit ? hit[1] : (s || '-');
}
// =========================================================
// 주제: guideShortOrder 처리
// 설명: guideShortOrder 기능에 필요한 처리 로직을 수행
// =========================================================
function guideShortOrder(value) {
  var s = String(value == null ? '' : value);
  return s.length > 22 ? s.slice(0, 13) + '…' + s.slice(-7) : s;
}

function guideImage(src) {
  src = String(src || '').trim();
  if (/^https:\/\//i.test(src)) return src;
  if (src.startsWith(guideCtx() + '/')) return src;
  if (src.startsWith('/')) return guideCtx() + src;
  return typeof NO_IMG !== 'undefined' ? NO_IMG : '';
}
// =========================================================
// 주제: guideStatusTag 처리
// 설명: guideStatusTag 기능에 필요한 처리 로직을 수행
// =========================================================
function guideStatusTag(status) {
  var s = guideStatus(status);
  return '<span class="status ' + s.css + '">' + guideEsc(s.text) + '</span>';
}
function guideApi(path, params) {
  var url = guideCtx() + '/mypage/guide/' + path;
  return fetch(url, {
    method: params ? 'POST' : 'GET',
    credentials: 'same-origin',
    cache: 'no-store',
    headers: params ? {'Content-Type':'application/x-www-form-urlencoded;charset=UTF-8'} : {},
    body: params ? new URLSearchParams(params) : undefined
  }).then(async function (response) {
    var data;
    try { data = await response.json(); }
    catch (e) { throw new Error('응답 형식이 올바르지 않습니다. (HTTP ' + response.status + ')'); }
    if (!response.ok || !data || data.success !== true) {
      throw new Error((data && data.message) || ('서버 오류 ' + response.status));
    }
    return data;
  });
}

// =========================================================
// 주제: loadGuide 처리
// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
// =========================================================
async function loadGuide() {
  var area = document.getElementById('guide-area');
  if (!area || guideBusy) return;
  guideBusy = true;
  area.innerHTML = '<p class="empty">가이드 예약 내역을 불러오는 중...</p>';
  try {
    var data = await guideApi('list');
    guideList = Array.isArray(data.list) ? data.list : [];
    guideDetailCache = {};
    renderGuide();
    console.info('[가이드 예약 DB 조회]', guideList.length + '건', GUIDE_MYPAGE_VERSION);
  } catch (error) {
    console.error('[가이드 예약 조회 실패]', error);
    area.innerHTML = '<p class="empty">' + guideEsc(error.message) +
      ' <button class="btn guide-retry" type="button">다시 불러오기</button></p>';
    var count = document.getElementById('guide-count');
    if (count) count.textContent = '-';
  } finally {
    guideBusy = false;
  }
}

// =========================================================
// 주제: renderGuide 처리
// 설명: renderGuide 기능에 필요한 처리 로직을 수행
// =========================================================
function renderGuide() {
  var area = document.getElementById('guide-area');
  var count = document.getElementById('guide-count');
  if (!area) return;
  if (count) count.textContent = guideList.length + '건';
  var groups = [
    ['ALL', '전체'], ['PENDING', '결제대기'],
    ['PAID', '결제완료'], ['CANCELED', '취소']
  ];
  var summary = '<p class="guide-notice" style="margin:0 0 14px;color:#58667c;font-size:13px">' +
    '※ 토스 <b>테스트 결제</b> 내역입니다. 결제완료가 실제 가이드 예약 확정을 뜻하지는 않습니다.</p>' +
    '<div class="summary-boxes">';
  groups.forEach(function (group) {
    var n = guideList.filter(function (r) {
      return group[0] === 'ALL' || guideGroup(r.status) === group[0];
    }).length;
    summary += '<button type="button" class="summary-box' +
      (guideFilter === group[0] ? ' active' : '') + '" data-filter="' + group[0] + '">' +
      '<span>' + group[1] + '</span><strong>' + n + '</strong></button>';
  });
  summary += '</div>';

  var rows = guideList.filter(function (r) {
    return guideFilter === 'ALL' || guideGroup(r.status) === guideFilter;
  });
  if (!rows.length) {
    area.innerHTML = summary + '<p class="empty">해당하는 예약/결제 내역이 없습니다.</p>';
    return;
  }
  var html = summary + '<div class="guide-table-scroll" role="region" aria-label="가이드 예약 결제 내역" tabindex="0">' +
    '<table class="rv-table guide-table"><colgroup>' +
    '<col class="col-order"><col class="col-guide"><col class="col-date"><col class="col-count">' +
    '<col class="col-price"><col class="col-status"><col class="col-approved"><col class="col-action">' +
    '</colgroup><thead><tr>' +
    '<th>주문번호</th><th>가이드</th><th>투어일</th>' +
    '<th class="hide-mobile">인원</th><th class="hide-mobile">결제금액</th>' +
    '<th>상태</th><th class="hide-mobile">승인일</th><th>상세</th>' +
    '</tr></thead><tbody>';
  rows.forEach(function (r) {
    var id = String(r.reservationId || '');
    var idDom = encodeURIComponent(id).replace(/%/g, '_');
    html += '<tr>' +
      '<td class="rv-no guide-order" title="' + guideEsc(id) + '"><span>' + guideEsc(guideShortOrder(id)) + '</span></td>' +
      '<td class="guide-guide"><div class="guide-cell"><img src="' + guideEsc(guideImage(r.guideImage)) +
      '" alt="' + guideEsc(r.guideName || '가이드') + '" loading="lazy">' +
      '<div><b>' + guideEsc(r.guideName || '정보 없음') + '</b><span>' + guideEsc(r.regionName || '') + '</span></div></div></td>' +
      '<td class="guide-date">' + guideEsc(guideShortDate(r.reservationDate)) + '</td>' +
      '<td class="hide-mobile guide-people">' + Number(r.peopleCount || 0) + '명</td>' +
      '<td class="hide-mobile guide-money">' + guideMoney(r.amount) + '</td>' +
      '<td class="guide-state">' + guideStatusTag(r.status) + '</td>' +
      '<td class="hide-mobile guide-approved" title="' + guideEsc(r.approvedAt || '-') + '">' + guideEsc(guideShortDate(r.approvedAt)) + '</td>' +
      '<td class="guide-action"><button class="btn btn-detail" type="button" data-id="' + guideEsc(id) +
      '" data-row="guide-detail-' + idDom + '">상세 ▾</button></td>' +
      '</tr><tr class="detail-row" id="guide-detail-' + idDom + '" style="display:none">' +
      '<td colspan="8"><div class="detail-box">불러오는 중...</div></td></tr>';
  });
  area.innerHTML = html + '</tbody></table></div>';
}

// =========================================================
// 주제: toggleGuideDetail 처리
// 설명: 입력 데이터를 화면/API/DB에서 사용하기 좋은 형태로 변환 또는 가공
// =========================================================
async function toggleGuideDetail(btn) {
  var id = btn.dataset.id;
  var rowId = btn.dataset.row;
  var row = document.getElementById(rowId);
  if (!row) return;
  var wasOpen = row.style.display !== 'none';
  document.querySelectorAll('#guide-area .detail-row').forEach(function (x) { x.style.display = 'none'; });
  document.querySelectorAll('#guide-area .btn-detail').forEach(function (x) { x.textContent = '상세 ▾'; });
  if (wasOpen) return;
  row.style.display = '';
  btn.textContent = '닫기 ▴';
  var detailBox = row.querySelector('.detail-box');
  try {
    var detail = guideDetailCache[id];
    if (!detail) {
      var result = await guideApi('detail?reservationId=' + encodeURIComponent(id));
      detail = result.data;
      guideDetailCache[id] = detail;
    }
    if (row.isConnected) detailBox.innerHTML = guideDetailHtml(id, detail);
  } catch (e) {
    detailBox.textContent = e.message || '상세 정보를 불러오지 못했습니다.';
  }
}

// =========================================================
// 주제: guideDetailHtml 처리
// 설명: guideDetailHtml 기능에 필요한 처리 로직을 수행
// =========================================================
function guideDetailHtml(id, d) {
  var r = d.reservation || {};
  var g = d.guide || {};
  var t = d.tour || {};
  var p = d.payment || {};
  var courses = '<ol class="course-list">';
  (t.courses || []).forEach(function (c) {
    courses += '<li><b>' + guideEsc(c.courseName) + '</b>' +
      (Number(c.durationMinutes) > 0 ? ' <small>(' + Number(c.durationMinutes) + '분)</small>' : '') + '</li>';
  });
  courses += '</ol>';
  var actions = r.status === 'PENDING'
    ? '<button class="btn btn-danger btn-cancel" type="button" data-id="' + guideEsc(id) + '">결제 대기 취소</button>'
    : '<p style="font-size:12px;color:#64748b">결제 완료 건은 별도 취소/환불 절차가 필요합니다.</p>';
  return '<div class="detail-grid">' +
    '<div class="detail-card"><h4>가이드 소개</h4><p class="detail-text">' + guideEsc(g.guideIntro) + '</p>' +
    '<dl class="info-list"><div><dt>전문분야</dt><dd>' + guideEsc(g.specialty) + '</dd></div>' +
    '<div><dt>평점</dt><dd>' + guideEsc(g.rating == null ? '-' : g.rating) + ' (' + Number(g.reviewCount || 0) + ')</dd></div>' +
    '<div><dt>연락처</dt><dd>' + guideEsc(g.guidePhone) + '</dd></div></dl></div>' +
    '<div class="detail-card"><h4>' + guideEsc(t.tourTitle || '기본 가이드 투어') +
    ' <small>· ' + guideEsc(t.duration) + '</small></h4>' + courses +
    '<dl class="info-list"><div><dt>포함</dt><dd>' + guideEsc(t.included) + '</dd></div>' +
    '<div><dt>불포함</dt><dd>' + guideEsc(t.excluded) + '</dd></div></dl>' +
    '<p class="detail-text">안내된 취소 규정: ' + guideEsc(t.cancellationPolicy || '별도 문의') + '</p></div>' +
    '<div class="detail-card"><h4>테스트 결제 정보</h4><dl class="info-list">' +
    '<div><dt>결제금액</dt><dd>' + guideMoney(p.amount) + '</dd></div>' +
    '<div><dt>1인 금액</dt><dd>' + guideMoney(g.price) + ' × ' + Number(r.peopleCount || 0) + '명</dd></div>' +
    '<div><dt>결제수단</dt><dd>' + guideEsc(p.paymentMethod || '-') + '</dd></div>' +
    '<div><dt>결제상태</dt><dd>' + guideEsc(guideStatus(p.status).text) + '</dd></div>' +
    '<div><dt>승인일시</dt><dd>' + guideEsc(p.approvedAt || '-') + '</dd></div>' +
    '<div><dt>주문번호</dt><dd>' + guideEsc(p.orderId || id) + '</dd></div>' +
    '</dl><div class="detail-actions">' + actions + '</div></div></div>';
}

// =========================================================
// 주제: guideCancel 처리
// 설명: guideCancel 기능에 필요한 처리 로직을 수행
// =========================================================
async function guideCancel(btn) {
  if (!confirm('결제 대기 중인 주문을 취소할까요?')) return;
  btn.disabled = true;
  try {
    var out = await guideApi('cancel', {reservationId: btn.dataset.id});
    alert(out.message || '취소되었습니다.');
    await loadGuide();
  } catch (error) {
    alert(error.message || '취소에 실패했습니다.');
    btn.disabled = false;
  }
}

var guideArea = document.getElementById('guide-area');
if (guideArea) guideArea.addEventListener('click', function (event) {
  var target = event.target;
  var retry = target.closest('.guide-retry');
  if (retry) { loadGuide(); return; }
  var chip = target.closest('.summary-box');
  if (chip) { guideFilter = chip.dataset.filter; renderGuide(); return; }
  var detail = target.closest('.btn-detail');
  if (detail) { toggleGuideDetail(detail); return; }
  var cancel = target.closest('.btn-cancel');
  if (cancel && !cancel.disabled) guideCancel(cancel);
});

console.info('[마이페이지 가이드 실DB JS 로드]', GUIDE_MYPAGE_VERSION);

window.GUIDE_MYPAGE_VERSION = GUIDE_MYPAGE_VERSION;

