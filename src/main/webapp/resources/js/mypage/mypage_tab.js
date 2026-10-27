// =========================================================
// 주제: 파일 역할
// 설명: 마이페이지 탭 전환과 선택 상태를 제어
// =========================================================

/* =========================================
   mypage-tab.js  -  탭 전환 (제일 마지막에 불러오기)
   탭을 처음 누를 때만 그 탭 데이터를 fetch 합니다
   ========================================= */

// 탭 id → 그 탭의 데이터를 불러오는 함수
var tabLoaders = {
  'tab-like': loadLike,
  'tab-guide': loadGuide,
  'tab-plan': function () {},   // 서블릿에서 이미 그려서 옴
  'tab-review': loadReview
};
var loadedTabs = {};   // 이미 불러온 탭 기록

var tabs = document.querySelectorAll('.mypage-tab');
var panels = document.querySelectorAll('.tab-panel');

// =========================================================
// 주제: openTab 처리
// 설명: openTab 기능에 필요한 처리 로직을 수행
// =========================================================
function openTab(tabId) {
  tabs.forEach(function (t) {
    t.classList.toggle('active', t.dataset.tab === tabId);
  });
  panels.forEach(function (p) {
    p.classList.toggle('active', p.id === tabId);
  });

  // 처음 여는 탭이면 데이터 가져오기
  if (!loadedTabs[tabId]) {
    loadedTabs[tabId] = true;
    tabLoaders[tabId]();
  }
}

tabs.forEach(function (tab) {
  tab.addEventListener('click', function () {
    openTab(tab.dataset.tab);
  });
});

// 주소에 ?tab=tab-guide 처럼 있으면 그 탭부터 열기 (결제 후 돌아올 때 편함)
var startTab = new URLSearchParams(location.search).get('tab');
openTab(tabLoaders[startTab] ? startTab : 'tab-like');
