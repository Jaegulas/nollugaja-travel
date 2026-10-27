<%-- =========================================================
     주제: 파일 역할
     설명: mypage 영역의 mypage_main 화면을 구성하고 서버에서 전달받은 데이터를 표시
========================================================= --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<%-- JS 파일들이 여기서 컨텍스트 경로와 카카오 키를 읽어갑니다 (.js 파일 안에서는 ${...} 가 동작하지 않음) --%>
<body data-ctx="${pageContext.request.contextPath}" data-kakao-key="${applicationScope.kakaoJsKey}">

 <!-- 상단 제목 -->
 <div class="mypage-header">
  <span class="mypage-badge">
   <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="8" r="4"/><path d="M4 21c0-4 4-6 8-6s8 2 8 6"/></svg>
   MY PAGE
  </span>
  <h1 class="mypage-title">마이페이지</h1>
  <p class="mypage-desc">찜한 장소, 예약, 여행일정을 한눈에 확인하세요</p>

  <div class="mypage-tabs">
   <button class="mypage-tab" data-tab="tab-like">
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.7l-1-1.1a5.5 5.5 0 0 0-7.8 7.8L12 21l8.8-8.6a5.5 5.5 0 0 0 0-7.8z"/></svg>
    찜 목록
   </button>
   <button class="mypage-tab" data-tab="tab-guide">
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><path d="M16 2v4M8 2v4M3 10h18"/></svg>
    가이드 예약
   </button>
   <button class="mypage-tab" data-tab="tab-plan">
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 4 3 6v14l6-2 6 2 6-2V4l-6 2-6-2z"/><path d="M9 4v14M15 6v14"/></svg>
    내 여행일정
   </button>
   <button class="mypage-tab" data-tab="tab-review">
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 3l2.8 5.7 6.2.9-4.5 4.4 1 6.2L12 17.3 6.5 20.2l1-6.2L3 9.6l6.2-.9z"/></svg>
    내 리뷰
   </button>
  </div>
 </div>

 <!-- 탭 내용 : 각 탭 = 배너 + 내용 상자 -->
 <div class="mypage-content">

  <%-- ==================== 찜 목록 ==================== --%>
<div class="tab-panel" id="tab-like">

  <%-- =========================================================
       주제: 회원 탈퇴 버튼 영역
       설명: 찜 목록 배너 바깥 오른쪽 위에 회원 탈퇴 버튼 표시
  ========================================================= --%>
  <div class="mypage-withdraw-area">
    <button type="button" class="mypage-withdraw-open" onclick="requestWithdraw()">
      회원 탈퇴
    </button>
  </div>

  <%-- =========================================================
       주제: 찜 목록 배너
       설명: 찜 목록 제목과 현재 저장된 찜 개수를 표시
  ========================================================= --%>
  <div class="panel-banner">
    <div>
      <h3>찜 목록</h3>
      <p>마음에 담아둔 숙소, 여행지, 맛집을 모아봤어요</p>
    </div>

    <span class="panel-count" id="like-count">0곳</span>
  </div>

  <div class="panel-box">
    <div id="like-area"><p class="empty">불러오는 중...</p></div>
  </div>
</div>


  <%-- ==================== 가이드 예약 ==================== --%>
  <div class="tab-panel" id="tab-guide">
   <div class="panel-banner">
    <div><h3>가이드 예약</h3><p>예약한 투어와 결제 내역을 확인하세요</p></div>
    <span class="panel-count" id="guide-count">0건</span>
   </div>
   <div class="panel-box">
    <div id="guide-area"><p class="empty">불러오는 중...</p></div>
   </div>
	</div>
    <%-- ==================== 내 여행일정 ==================== --%>
    <div class="tab-panel" id="tab-plan">
      <div class="panel-banner">
        <div>
          <h3>내 여행일정</h3>
          <p>AI로 만든 일정표를 다시 보고 공유할 수 있어요</p>
        </div>
        <%-- id="plan-count" : 삭제하면 itineraryDelete() 가 숫자를 바꿔줌 --%>
        <span class="panel-count" id="plan-count">${fn:length(volist)} / 15</span>
      </div>

      <div class="panel-box">
        <c:choose>
          <c:when test="${not empty volist}">
           <table class="rv-table">
			  <tr>
			    <th>제목</th>
			    <th class="hide-mobile">생성일자</th>
			    <th>관리</th>
			  </tr>
			  <c:forEach var="vo" items="${volist}">
			    <tr>
			      <td>
			        <a href="#" class="plan-link" onclick="showPlan(${vo.planid}); return false;">
			          <c:out value="${vo.title}" />
			        </a>
			      </td>
			      <td class="hide-mobile">${vo.createdDate}</td>
			      <td>
			        <div class="plan-acts">
			          <button type="button" class="btn plan-btn btn-pdf"
			                  onclick="downloadPlanPdf(${vo.planid})">
			            <span class="material-symbols-outlined">download</span>PDF 저장
			          </button>
			
			          <button type="button" class="btn plan-btn kakao-share-btn"
			                  data-planid="${vo.planid}" onclick="sharePlanFromList(this)">
			            <span class="material-symbols-outlined">chat</span>카톡 공유
			          </button>
			
			          <button type="button" class="btn plan-btn btn-danger"
			                  data-planid="${vo.planid}" onclick="itineraryDelete(this)">
			            <span class="material-symbols-outlined">delete</span>삭제
			          </button>
			        </div>
			      </td>
			    </tr>
			  </c:forEach>
			</table>
          </c:when>
          <c:otherwise>
            <p class="empty">저장된 일정이 없습니다.</p>
          </c:otherwise>
        </c:choose>

        <%-- 제목 / PDF 버튼을 누르면 mypage-plan.js 의 renderPlan() 이 여기에 지도와 일정을 그림 --%>
        <section class="plan-section" id="plan" style="display:none">
          <div id="aiSummaryBox" class="plan-box"></div>
        </section>
      </div>
    </div>

    <%-- ==================== 내 리뷰 ==================== --%>
    <div class="tab-panel" id="tab-review">
      <div class="panel-banner">
        <div>
          <h3>내 리뷰</h3>
          <p>내가 쓴 여행 리뷰 모아보기</p>
        </div>
        <span class="panel-count" id="review-count">0개</span>
      </div>
      <div class="panel-box">
        <div id="review-area"><p class="empty">불러오는 중...</p></div>
      </div>
    </div>
  </div>



<%-- =========================================================
     주제: 회원 탈퇴 비밀번호 확인창
     설명: 외부 CSS와 분리된 dialog 팝업에서 비밀번호를 다시 입력받아 탈퇴 요청
========================================================= --%>
<style>
/* =========================================================
   주제: 회원 탈퇴 버튼 바깥 배치
   설명: 찜 목록 배너 바로 위 오른쪽에 회원 탈퇴 버튼 표시
   ========================================================= */
.mypage-withdraw-area{
    width:100%;
    display:flex;
    justify-content:flex-end;
    margin-bottom:10px;
    padding-right:4px;
}
.mypage-withdraw-open{min-width:88px;padding:7px 15px;border:1px solid #dc2626;border-radius:999px;background:#fff1f2;color:#dc2626;font-size:13px;font-weight:800;font-family:inherit;cursor:pointer;transition:all .18s ease;}
.mypage-withdraw-open:hover{background:#dc2626;color:#fff;transform:translateY(-1px);box-shadow:0 5px 14px rgba(220,38,38,.22);}
#withdraw-dialog{width:min(420px,calc(100vw - 40px));padding:0;border:0;border-radius:20px;background:#fff;color:#1f2937;box-shadow:0 24px 80px rgba(0,0,0,.28);overflow:hidden;}
#withdraw-dialog::backdrop{background:rgba(15,23,42,.55);backdrop-filter:blur(3px);}
.withdraw-dialog-inner{padding:28px;}
.withdraw-dialog-icon{display:flex;align-items:center;justify-content:center;width:44px;height:44px;margin-bottom:14px;border-radius:50%;background:#fee2e2;color:#dc2626;font-size:23px;font-weight:900;}
.withdraw-dialog-title{margin:0 0 8px;font-size:22px;font-weight:800;color:#111827;}
.withdraw-dialog-text{margin:0 0 20px;color:#6b7280;font-size:14px;line-height:1.65;}
.withdraw-dialog-label{display:block;margin-bottom:7px;font-size:13px;font-weight:700;color:#374151;}
.withdraw-dialog-input{box-sizing:border-box;width:100%;height:46px;padding:0 13px;border:1px solid #d1d5db;border-radius:11px;background:#f9fafb;color:#111827;font-size:14px;font-family:inherit;outline:none;}
.withdraw-dialog-input:focus{border-color:#ef4444;box-shadow:0 0 0 3px rgba(239,68,68,.12);}
.withdraw-dialog-buttons{display:grid;grid-template-columns:1fr 1fr;gap:10px;margin-top:20px;}
.withdraw-dialog-cancel,.withdraw-dialog-submit{height:44px;border-radius:11px;font-size:14px;font-weight:800;font-family:inherit;cursor:pointer;}
.withdraw-dialog-cancel{border:1px solid #d1d5db;background:#fff;color:#374151;}
.withdraw-dialog-submit{border:1px solid #dc2626;background:#dc2626;color:#fff;}
.withdraw-dialog-submit:hover{background:#b91c1c;border-color:#b91c1c;}
@media(max-width:600px){.mypage-withdraw-open{min-width:78px;padding:6px 12px;font-size:12px}.withdraw-dialog-inner{padding:24px 20px}}
</style>

<dialog id="withdraw-dialog">
  <div class="withdraw-dialog-inner">
    <div class="withdraw-dialog-icon">!</div>
    <h2 class="withdraw-dialog-title">회원 탈퇴</h2>
    <p class="withdraw-dialog-text">탈퇴 후에는 현재 계정으로 로그인할 수 없습니다.<br>계속하려면 비밀번호를 다시 입력해주세요.</p>

    <form method="post" action="${ctx}/Mypage/withdraw" onsubmit="return confirmWithdrawSubmit();">
      <label for="withdraw-passwd" class="withdraw-dialog-label">비밀번호</label>
      <input type="password" id="withdraw-passwd" name="passwd" class="withdraw-dialog-input"
             placeholder="비밀번호를 입력해주세요" autocomplete="current-password" required>

      <div class="withdraw-dialog-buttons">
        <button type="button" class="withdraw-dialog-cancel" onclick="closeWithdrawDialog()">취소</button>
        <button type="submit" class="withdraw-dialog-submit">회원 탈퇴</button>
      </div>
    </form>
  </div>
</dialog>

<script>
// =========================================================
// 주제: 회원 탈퇴 확인창 열기
// 설명: 마이페이지 위에 비밀번호 재입력 dialog를 띄우고 입력칸에 포커스
// =========================================================
function requestWithdraw(){
  const dialog=document.getElementById('withdraw-dialog');
  const passwd=document.getElementById('withdraw-passwd');
  if(!dialog)return;
  if(passwd)passwd.value='';
  dialog.showModal();
  setTimeout(function(){if(passwd)passwd.focus();},50);
}

// =========================================================
// 주제: 회원 탈퇴 확인창 닫기
// 설명: 취소 시 서버 요청 없이 dialog만 닫음
// =========================================================
function closeWithdrawDialog(){
  const dialog=document.getElementById('withdraw-dialog');
  if(dialog&&dialog.open)dialog.close();
}

// =========================================================
// 주제: 회원 탈퇴 최종 확인
// 설명: 비밀번호 입력 후 실수로 탈퇴되는 것을 방지하기 위해 한 번 더 확인
// =========================================================
function confirmWithdrawSubmit(){
  const passwd=document.getElementById('withdraw-passwd');
  if(!passwd||passwd.value.trim()===''){
    alert('비밀번호를 입력해주세요.');
    return false;
  }
  return confirm('정말 회원 탈퇴를 진행하시겠습니까?');
}
</script>

<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<%-- ① 외부 라이브러리 먼저 (mypage_plan.js 가 Kakao를 사용함) --%>
<script src="https://cdnjs.cloudflare.com/ajax/libs/html2pdf.js/0.10.1/html2pdf.bundle.min.js"></script>
<script src="https://t1.kakaocdn.net/kakao_js_sdk/2.7.2/kakao.min.js" crossorigin="anonymous"></script>
<script src="https://dapi.kakao.com/v2/maps/sdk.js?appkey=${applicationScope.kakaoJsKey}&libraries=services&autoload=false"></script>

<%-- =========================================================
주제: 마이페이지 JS 연결
설명: 팀원 기존 스크립트는 유지, 찜 목록만 mypage_like.js로 통일
주의: 기존 mypage-like-v3.js / mypage-like.js를 중복 로딩하지 말 것
========================================================= --%>
<script src="${pageContext.request.contextPath}/resources/js/mypage/mypage_common.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/mypage/mypage_like.js?v=20261019-2"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/mypage/mypage_guide_layout.css?v=20261019-3" />
<script src="${pageContext.request.contextPath}/resources/js/mypage/mypage_guide.js?v=20261019-guide-layout-4"></script>
<script src="${pageContext.request.contextPath}/resources/js/mypage/mypage_plan.js?v=20261019-2"></script>
<script src="${pageContext.request.contextPath}/resources/js/mypage/mypage_review.js?v=20261019-2"></script>
<script src="${pageContext.request.contextPath}/resources/js/mypage/mypage_tab.js?v=20261019-2"></script>



<style>
  /* ===== 내 여행일정 표 : 제목 / 생성일자 / 관리 가운데 정렬 ===== */
  #tab-plan .rv-table th,
  #tab-plan .rv-table td {
    text-align: center;         /* 글자 가로 가운데 */
    vertical-align: middle;     /* 칸 안에서 세로 가운데 */
  }

  /* 관리 칸 : 버튼 3개를 가운데에 나란히 */
  #tab-plan .plan-acts {
    display: flex;
    justify-content: center;    /* 가로 가운데 */
    align-items: center;
    gap: 8px;                   /* 버튼 사이 간격 */
    flex-wrap: wrap;            /* 좁은 화면에선 다음 줄로 */
  }

  /* 버튼 공통 : 회색 테두리 + 아이콘 + 글자 */
  #tab-plan .btn.plan-btn {
    display: inline-flex;
    align-items: center;
    gap: 5px;                   /* 아이콘과 글자 사이 */
    padding: 7px 12px;
    font-size: 15px;
    font-family: inherit;
    color: #5f5f6d;
    background: #ffffff;
    border: 1px solid #e3e3ea;
    border-radius: 8px;
    white-space: nowrap;        /* 글자가 두 줄로 안 깨지게 */
    cursor: pointer;
    transition: background 0.15s, border-color 0.15s, color 0.15s;
  }

  /* 아이콘 크기 */
  #tab-plan .plan-btn .material-symbols-outlined {
    font-size: 18px;
  }

  /* 삭제는 조금 떨어뜨리기 (실수로 누르지 않게) */
  #tab-plan .btn.plan-btn.btn-danger {
    margin-left: 12px;
  }

  /* ----- 아이콘 색 (평소에도 보임) ----- */
  #tab-plan .btn-pdf .material-symbols-outlined         { color: #5b4fe0; } /* 보라 */
  #tab-plan .kakao-share-btn .material-symbols-outlined { color: #c9a000; } /* 노랑 */
  #tab-plan .btn-danger .material-symbols-outlined      { color: #e5484d; } /* 빨강 */

  /* ----- 마우스 올리면 버튼 전체가 그 색으로 ----- */
  #tab-plan .btn.plan-btn.btn-pdf:hover {
    background: #f0eeff;
    border-color: #5b4fe0;
    color: #5b4fe0;
  }
  #tab-plan .btn.plan-btn.kakao-share-btn:hover {
    background: #fff8d6;
    border-color: #e9c400;
    color: #3c1e1e;
  }
  #tab-plan .btn.plan-btn.btn-danger:hover {
    background: #fff1f1;
    border-color: #e5484d;
    color: #e5484d;
  }
</style>