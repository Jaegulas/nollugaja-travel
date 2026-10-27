<%-- =========================================================
     주제: 파일 역할
     설명: guideList 영역의 guideModal 화면을 구성하고 서버에서 전달받은 데이터를 표시
========================================================= --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<style>
/* Guide modal - isolated gm- classes to avoid conflict with top.jsp */
.gm-backdrop{position:fixed;inset:0;z-index:99990;display:none;align-items:center;justify-content:center;padding:20px;background:rgba(10,27,37,.68);backdrop-filter:blur(7px)}
.gm-backdrop.open{display:flex}
.gm-dialog{width:min(940px,100%);max-height:min(92vh,860px);overflow:auto;position:relative;border-radius:26px;background:#fff;color:#17283b;box-shadow:0 30px 90px rgba(0,20,32,.3);font-family:'Noto Sans KR',sans-serif;outline:none}
.gm-backdrop [hidden]{display:none!important}
.gm-header{display:flex;align-items:center;justify-content:space-between;padding:18px 27px;border-bottom:1px solid #edf0f4}
.gm-brand{display:flex;align-items:center;gap:9px;font-size:13px;font-weight:800;color:#006574}
.gm-brand-mark{width:28px;height:28px;border-radius:9px;background:#e4f4f3;display:grid;place-items:center}
.gm-close{width:38px;height:38px;border:0;border-radius:50%;background:#f2f4f8;color:#243448;font-size:24px;cursor:pointer;line-height:1}
.gm-close:hover{background:#e3e9ef}
.gm-layout{display:grid;grid-template-columns:minmax(0,40%) minmax(0,60%);gap:0;padding:20px;min-height:510px}
.gm-visual{position:relative;min-height:470px;border-radius:20px;overflow:hidden;background:linear-gradient(145deg,#d9edf0,#a6cbd0);display:grid;place-items:center}
.gm-visual img{position:absolute;inset:0;width:100%;height:100%;object-fit:cover}
.gm-fallback{font-size:90px}
.gm-location{position:absolute;left:18px;bottom:18px;padding:9px 14px;border-radius:999px;background:rgba(0,101,116,.94);color:#fff;font-size:13px;font-weight:800}
.gm-content{display:flex;flex-direction:column;padding:24px 28px 14px 34px;min-width:0}
.gm-eyebrow{font-size:12px;font-weight:800;letter-spacing:.07em;color:#006574}
.gm-name{font-size:30px;font-weight:800;letter-spacing:-.05em;line-height:1.3;margin:12px 0 8px}
.gm-specialty{font-size:15px;font-weight:700;color:#006574;line-height:1.6}
.gm-intro-label{font-size:15px;font-weight:800;margin:26px 0 8px}
.gm-intro{font-size:14px;line-height:1.85;color:#536171;margin:0;white-space:pre-line;overflow-wrap:anywhere}
.gm-tags{display:flex;flex-wrap:wrap;gap:7px;margin:18px 0 8px}
.gm-tag{padding:7px 11px;border-radius:999px;background:#eff6f7;color:#006574;font-size:12px;font-weight:700}
.gm-stats{display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:9px;margin-top:22px}
.gm-stat{background:#f5f7fb;border:1px solid #edf0f6;border-radius:15px;padding:13px 10px;text-align:center}
.gm-stat-label{font-size:11px;color:#6b7888;margin-bottom:5px}
.gm-stat-value{font-size:16px;font-weight:800;color:#21354b}
/* Guide details: database-backed region, specialty and price */
.gm-stat{min-width:0;padding:17px 12px}
.gm-stat-icon{display:block;font-size:21px;line-height:1.3;margin-bottom:7px;color:#006574}
.gm-stat-value{overflow-wrap:anywhere;line-height:1.5}
.gm-stat-value.specialty{font-size:13px}
.gm-stat-value.price{font-size:16px;color:#006574}
.gm-bottom{display:flex;justify-content:space-between;align-items:center;gap:15px;margin-top:auto;padding-top:22px}
.gm-price-label{font-size:12px;color:#6b7888}
.gm-price{font-size:27px;font-weight:900;letter-spacing:-.05em;color:#142638;margin-top:3px}
.gm-book{display:inline-flex;align-items:center;justify-content:center;min-height:50px;padding:0 24px;background:#006574;border-radius:13px;color:#fff!important;font-size:14px;font-weight:800;text-decoration:none;box-shadow:0 8px 20px rgba(0,101,116,.18);white-space:nowrap;transition:transform .15s,background .15s}
.gm-book:hover{background:#004f5b;transform:translateY(-2px)}
.gm-error{margin:22px;padding:22px;border-radius:14px;background:#fff4f2;color:#a52d22;font-size:14px}
@media(max-width:720px){.gm-backdrop{padding:10px}.gm-dialog{max-height:95vh;border-radius:19px}.gm-header{padding:13px 18px}.gm-layout{display:block;padding:12px;min-height:0}.gm-visual{min-height:0;height:260px;border-radius:15px}.gm-content{padding:22px 10px 12px}.gm-name{font-size:25px}.gm-stats{margin-top:17px}.gm-bottom{margin-top:20px}.gm-price{font-size:23px}}
@media(max-width:480px){.gm-stats{grid-template-columns:1fr}.gm-stat{padding:12px}.gm-stat-icon{margin-bottom:3px}}
@media(max-width:390px){.gm-bottom{flex-wrap:wrap}.gm-book{width:100%}}
.gm-book-note{font-size:12px;color:#6b7888;line-height:1.6}
.gm-stat-value.specialty{font-size:13px;line-height:1.45;overflow-wrap:anywhere}
@media(max-width:480px){.gm-stats{grid-template-columns:1fr}.gm-stat{padding:12px}.gm-bottom{align-items:stretch;flex-direction:column}.gm-book{width:100%}}
</style>

<div id="guideModal" class="gm-backdrop" aria-hidden="true">
  <div class="gm-dialog" role="dialog" aria-modal="true" aria-labelledby="gmName" tabindex="-1">
    <header class="gm-header">
      <div class="gm-brand"><span class="gm-brand-mark">✦</span><span class="translate">놀러가자 · 로컬 가이드</span></div>
      <button type="button" class="gm-close" id="gmClose" aria-label="모달 닫기">×</button>
    </header>
    <div class="gm-error" id="gmError" role="alert" hidden></div>
    <div class="gm-layout" id="gmLayout" hidden>
      <div class="gm-visual">
        <span class="gm-fallback" id="gmFallback">🧭</span>
        <img id="gmImage" alt="가이드 프로필 사진" hidden>
      </div>
      <div class="gm-content">
        <div class="gm-eyebrow translate">당신의 여행을 함께할 로컬 가이드</div>
        <h2 class="gm-name translate" id="gmName"></h2>
        <div class="gm-specialty translate" id="gmSpecialty"></div>
        <div class="gm-stats" aria-label="가이드 상세 정보">
          <div class="gm-stat">
            <span class="gm-stat-icon" aria-hidden="true">⌖</span>
            <div class="gm-stat-label translate">활동 지역</div>
            <div class="gm-stat-value translate" id="gmActivityRegion"></div>
          </div>
          <div class="gm-stat">
            <span class="gm-stat-icon" aria-hidden="true">✧</span>
            <div class="gm-stat-label translate">전문 분야</div>
            <div class="gm-stat-value specialty translate" id="gmActivitySpecialty"></div>
          </div>
          <div class="gm-stat">
            <span class="gm-stat-icon" aria-hidden="true">▣</span>
            <div class="gm-stat-label translate">1인 투어 요금</div>
            <div class="gm-stat-value price" id="gmActivityPrice"></div>
          </div>
        </div>
        <div class="gm-intro-label translate">이 가이드와 함께하는 여행</div>
        <p class="gm-intro translate" id="gmIntro"></p>
        <div class="gm-tags" id="gmTags"></div>
        <div class="gm-bottom">
          <div class="gm-book-note translate">가이드 정보를 확인하고 예약을 진행해 주세요.</div>
          <a class="gm-book translate" id="gmBook" href="#">이 가이드 예약하기 →</a>
        </div>
      </div>
    </div>
  </div>
</div>

<script>
(function () {
  'use strict';
  const contextPath = '${pageContext.request.contextPath}';
  const modal = document.getElementById('guideModal');
  if (!modal || modal.dataset.initialized === 'true') return;
  modal.dataset.initialized = 'true';

  const dialog = modal.querySelector('.gm-dialog');
  const layout = document.getElementById('gmLayout');
  const error = document.getElementById('gmError');
  const photo = document.getElementById('gmImage');
  const fallback = document.getElementById('gmFallback');
  const get = id => document.getElementById(id);
  const put = (id, value) => { get(id).textContent = value == null ? '' : String(value); };
  let lastFocus = null, oldOverflow = '', requestToken = 0;

  function closeModal() {
    requestToken++;
    modal.classList.remove('open');
    modal.setAttribute('aria-hidden', 'true');
    document.body.style.overflow = oldOverflow;
    if (lastFocus && typeof lastFocus.focus === 'function') lastFocus.focus();
  }

  function openModal() {
    if (!modal.classList.contains('open')) {
      lastFocus = document.activeElement;
      oldOverflow = document.body.style.overflow;
    }
    modal.classList.add('open');
    modal.setAttribute('aria-hidden', 'false');
    document.body.style.overflow = 'hidden';
    dialog.focus();
  }

  function safeImageUrl(raw) {
    if (typeof raw !== 'string' || !raw.trim()) return '';
    const url = raw.trim();
    if (/^https?:\/\//i.test(url)) return url;
    if (url.startsWith('/resources/')) return contextPath + url;
    if (url.startsWith(contextPath + '/resources/')) return url;
    return '';
  }

  function renderTags(raw) {
    const box = get('gmTags');
    box.replaceChildren();
    const tags = String(raw || '').split(/[\s,]+/).map(t => t.trim()).filter(Boolean);
    tags.slice(0, 8).forEach(tag => {
      const item = document.createElement('span');
      item.className = 'gm-tag translate';
      item.textContent = tag.startsWith('#') ? tag : '#' + tag;
      box.appendChild(item);
    });
  }

  async function showGuideDetail(id) {
    if (!/^\d+$/.test(String(id || ''))) return;
    const token = ++requestToken;
    openModal();
    layout.hidden = true;
    error.hidden = false;
    error.textContent = '가이드 정보를 불러오는 중입니다...';

    try {
      const response = await fetch(contextPath + '/GuideDetail?guideId=' + encodeURIComponent(id), {
        headers: { 'Accept': 'application/json' }
      });
      if (!response.ok) throw new Error('가이드 정보를 불러오지 못했습니다. (HTTP ' + response.status + ')');
      const guide = await response.json();
      if (token !== requestToken) return;
      if (!guide || !guide.guideId) throw new Error('가이드 정보가 존재하지 않습니다.');

      put('gmName', (guide.guideName || '') + ' 가이드');
      put('gmSpecialty', guide.specialty);
      put('gmActivityRegion', guide.regionName || '지역 정보 없음');
      put('gmActivitySpecialty', guide.specialty || '전문 분야 정보 없음');
      put('gmActivityPrice', guide.price == null ? '요금 문의' : Number(guide.price).toLocaleString('ko-KR') + '원');
      put('gmIntro', guide.guideIntro);
      renderTags(guide.tags);
      get('gmBook').href = contextPath + '/GuideReservation?guideId=' + encodeURIComponent(guide.guideId);

      photo.hidden = true;
      photo.removeAttribute('src');
      fallback.hidden = false;
      photo.onload = function () {
        if (token !== requestToken) return;
        photo.hidden = false;
        fallback.hidden = true;
      };
      photo.onerror = function () {
        photo.hidden = true;
        fallback.hidden = false;
      };
      const src = safeImageUrl(guide.guideImage);
      if (src) photo.src = src;

      error.hidden = true;
      layout.hidden = false;
    } catch (e) {
      if (token !== requestToken) return;
      console.error('가이드 모달 조회 오류:', e);
      error.textContent = e.message || '알 수 없는 오류가 발생했습니다.';
      error.hidden = false;
      layout.hidden = true;
    }
  }

  document.addEventListener('click', function (event) {
    const trigger = event.target.closest('[data-guide-detail]');
    if (!trigger) return;
    event.preventDefault();
    showGuideDetail(trigger.getAttribute('data-guide-detail'));
  });
  get('gmClose').addEventListener('click', closeModal);
  modal.addEventListener('click', event => { if (event.target === modal) closeModal(); });
  document.addEventListener('keydown', function (event) {
    if (!modal.classList.contains('open')) return;
    if (event.key === 'Escape') { closeModal(); return; }
    if (event.key !== 'Tab') return;
    const nodes = Array.from(modal.querySelectorAll('button:not([disabled]),a[href]')).filter(n => n.getClientRects().length);
    if (!nodes.length) return;
    const first = nodes[0], last = nodes[nodes.length - 1];
    if (event.shiftKey && document.activeElement === first) { event.preventDefault(); last.focus(); }
    else if (!event.shiftKey && document.activeElement === last) { event.preventDefault(); first.focus(); }
  });
})();
</script>
