<%-- =========================================================
     주제: 파일 역할
     설명: paymentInfo 영역의 paymentComplete 화면을 구성하고 서버에서 전달받은 데이터를 표시
========================================================= --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<link rel="preconnect" href="https://fonts.googleapis.com" />
<link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@500;600;700;800&family=Noto+Sans+KR:wght@400;500;600;700;800&family=Material+Symbols+Outlined&display=swap" rel="stylesheet" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/payment/paymentComplete.css?v=20261019-3" />


<main class="ps-page">
  <div class="ps-shell">
    <div class="ps-progress" aria-label="결제 진행 상태">
      <span class="ps-progress-label">GUIDE TOUR <span class="ps-dot">·</span> PAYMENT</span>
      <span class="ps-progress-step"><span class="material-symbols-outlined" aria-hidden="true">verified</span> 결제 승인 완료</span>
    </div>

    <section class="ps-hero" aria-labelledby="ps-title">
      <div class="ps-success-icon" aria-hidden="true"><span class="material-symbols-outlined">check</span></div>
      <div class="ps-kicker">PAYMENT SUCCESSFUL</div>
      <h1 id="ps-title">결제가 정상적으로 승인되었습니다</h1>
      <p class="ps-subtitle">선택하신 가이드 투어의 결제 처리가 완료되었습니다.<br class="ps-desktop-only" /> 아래에서 결제 내역을 확인해 주세요.</p>
      <c:if test="${not empty paymentMessage}"><div class="ps-message" role="status"><span class="material-symbols-outlined" aria-hidden="true">info</span><span><c:out value="${paymentMessage}" /></span></div></c:if>
    </section>

    <section class="ps-detail" aria-labelledby="ps-detail-title">
      <div class="ps-detail-head"><div><span class="ps-section-label">PAYMENT DETAILS</span><h2 id="ps-detail-title">결제 상세 내역</h2></div><span class="ps-status"><span class="ps-status-dot"></span> 승인 완료</span></div>
      <div class="ps-detail-body">
        <div class="ps-row"><span class="ps-row-label"><span class="material-symbols-outlined" aria-hidden="true">receipt_long</span> 주문번호</span><strong class="ps-order-id"><c:out value="${paidOrder.orderId}" /></strong></div>
        <div class="ps-row"><span class="ps-row-label"><span class="material-symbols-outlined" aria-hidden="true">calendar_month</span> 투어 날짜</span><strong><c:out value="${paidOrder.reservationDate}" /></strong></div>
        <div class="ps-row"><span class="ps-row-label"><span class="material-symbols-outlined" aria-hidden="true">groups</span> 예약 인원</span><strong><c:out value="${paidOrder.peopleCount}" />명</strong></div>
        <div class="ps-total"><div><span class="ps-total-label">승인 금액</span><small>토스페이먼츠 테스트 결제</small></div><div class="ps-total-price"><fmt:formatNumber value="${paidOrder.amount}" pattern="#,##0" /><span>원</span></div></div>
      </div>
    </section>

    <div class="ps-info"><span class="material-symbols-outlined" aria-hidden="true">info</span><p>현재 테스트 결제 환경으로 실제 금액은 청구되지 않습니다. 결제 승인 결과는 <strong>toss_test_order</strong>에 기록되며, 실제 투어 예약 확정 및 정산 기능은 아직 연동되지 않았습니다.</p></div>
    <a class="ps-home" href="${pageContext.request.contextPath}/GuideList"><span class="material-symbols-outlined" aria-hidden="true">explore</span> 다른 가이드 둘러보기 <span class="material-symbols-outlined ps-arrow" aria-hidden="true">arrow_forward</span></a>
    <p class="ps-footer">놀러가자 <span>·</span> 당신의 여행을 더 특별하게</p>
  </div>
</main>
