<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<link rel="preconnect" href="https://fonts.googleapis.com" />
<link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&amp;family=Noto+Sans+KR:wght@400;500;600;700;800&amp;family=Material+Symbols+Outlined&amp;display=swap" rel="stylesheet" />
<link rel="stylesheet"
      href="${pageContext.request.contextPath}/resources/css/payment/paymentInfo.css?v=20261015" />

<main class="pi-page">
  <header class="pi-page-header">
    <div class="pi-eyebrow"><span class="pi-eyebrow-dot"></span> GUIDE TOUR · CHECKOUT</div>
    <div class="pi-header-row"><div><h1>예약 및 결제</h1><p>여행의 마지막 준비, 예약 내용을 확인하고 안전하게 결제하세요.</p></div><div class="pi-header-mark" aria-hidden="true"><i class="material-symbols-outlined">travel_explore</i></div></div>
  </header>
  <nav class="pi-stepper" aria-label="예약 진행 단계">
    <div class="pi-step done"><span class="pi-step-circle"><i class="material-symbols-outlined">check</i></span><span>가이드 선택</span></div>
    <div class="pi-step done"><span class="pi-step-circle"><i class="material-symbols-outlined">check</i></span><span>일정 선택</span></div>
    <div class="pi-step active"><span class="pi-step-circle">3</span><span>결제 정보</span></div>
    <div class="pi-step"><span class="pi-step-circle">4</span><span>결제 완료</span></div>
  </nav>

  <div class="pi-layout">
    <div class="pi-left">
      <section class="pi-notice">
        <i class="material-symbols-outlined">pending_actions</i>
        <div>
          <span class="pi-tag">예약 정보 확인</span>
          <h2>결제 전, 예약 내용을 확인해 주세요.</h2>
          <p>선택하신 투어 정보와 결제 금액을 마지막으로 확인해 주세요.</p>
        </div>
      </section>

      <section class="pi-card pi-reservation-card">
        <div class="pi-section-kicker">YOUR RESERVATION</div>
        <h2 class="pi-heading"><i class="material-symbols-outlined">event_available</i> 예약할 투어</h2>
        <div class="pi-guide">
          <c:if test="${not empty guide.guideImage}">
            <img src="${pageContext.request.contextPath}${guide.guideImage}" alt="가이드 사진" onerror="this.style.display='none'">
          </c:if>
          <div>
            <span class="pi-tag"><c:out value="${guide.regionName}"/></span>
            <h3><c:out value="${guide.guideName}"/> 가이드</h3>
            <p><c:out value="${guide.specialty}"/></p>
          </div>
        </div>
        <div class="pi-detail-grid">
          <div class="pi-detail"><i class="material-symbols-outlined">calendar_month</i><div><small>예약 날짜</small><strong><c:out value="${draft.reservationDate}"/></strong><span>투어 시작 시간은 예약 확정 후 안내됩니다.</span></div></div>
          <div class="pi-detail"><i class="material-symbols-outlined">group</i><div><small>예약 인원</small><strong><c:out value="${draft.peopleCount}"/>명</strong><span>선택한 인원에 따른 요금입니다.</span></div></div>
          <div class="pi-detail wide"><i class="material-symbols-outlined">location_on</i><div><small>투어 지역</small><strong><c:out value="${guide.regionName}"/></strong><span>상세 집결 장소는 예약 확정 후 안내됩니다.</span></div></div>
        </div>
      </section>

      <section class="pi-card pi-payment-card">
        <div class="pi-section-kicker">PAYMENT METHOD</div>
        <h2 class="pi-heading"><i class="material-symbols-outlined">credit_card</i> 결제 수단</h2>
        <p class="pi-muted">아래 결제 수단을 확인한 후 결제를 진행해 주세요.</p>
        <div class="pi-methods" id="piMethods">
          <label class="pi-method selected"><input type="radio" name="piMethod" value="CARD" checked><i class="material-symbols-outlined">credit_card</i><strong>신용 / 체크카드</strong><small>카드 결제</small></label>
        </div>
        <div class="pi-booking-note">
          <h3><i class="material-symbols-outlined">info</i> 예약 진행 안내</h3>
          <p>결제 완료 후 예약 내역을 확인할 수 있습니다. 투어 시작 시간과 집결 장소는 예약 확정 후 별도로 안내됩니다.</p>
        </div>
        <div class="pi-terms">
          <h3><i class="material-symbols-outlined">fact_check</i> 결제 전 확인 사항</h3>
          <label><input type="checkbox" id="piTerms1"> [필수] 예약 및 취소 정책을 확인했습니다.</label>
          <label><input type="checkbox" id="piTerms2"> [필수] 개인정보 처리 안내를 확인했습니다.</label>
          <p>예약·취소 정책과 개인정보 처리방침의 내용을 확인한 후 동의해 주세요.</p>
          <%-- 실제 정책 URL이 확인되면 아래에 해당 링크를 연결해야 합니다. --%>
        </div>
      </section>
    </div>

    <aside class="pi-right">
      <section class="pi-card pi-summary">
        <div class="pi-section-kicker">ORDER SUMMARY</div>
        <h2 class="pi-heading"><i class="material-symbols-outlined">receipt_long</i> 결제 금액</h2>
        <div class="pi-receipt">
          <div><span>1인 투어 요금</span><strong><fmt:formatNumber value="${guide.price}" pattern="#,##0"/>원</strong></div>
          <div><span>예약 인원</span><strong><c:out value="${draft.peopleCount}"/>명</strong></div>
          <div><span>투어 요금 합계</span><strong><fmt:formatNumber value="${amount}" pattern="#,##0"/>원</strong></div>
          <div class="pi-final"><strong>총 결제 금액</strong><strong><fmt:formatNumber value="${amount}" pattern="#,##0"/>원</strong></div>
        </div>
        <button type="button" class="pi-pay" id="piPay"><i class="material-symbols-outlined">lock</i> 테스트 결제 진행하기</button>
        <p id="piPaymentStatus" role="alert" style="display:none;color:#b91c1c;margin-top:10px;font-weight:600;"></p>
        <p class="pi-muted pi-center">현재 테스트 결제 환경으로 실제 금액은 청구되지 않습니다.</p>
        <a class="pi-edit" href="${pageContext.request.contextPath}/GuideReservation?guideId=${guide.guideId}"><i class="material-symbols-outlined">edit_calendar</i> 일정 및 인원 변경하기</a>
      </section>
      <div class="pi-support"><i class="material-symbols-outlined">verified_user</i><span>최종 결제 금액은 서버에서 확인한 가이드 요금과 예약 인원을 기준으로 산정됩니다.</span></div>
    </aside>
  </div>
</main>
<script src="https://js.tosspayments.com/v2/standard"></script>
<script>
window.travelTossConfig = {
  clientKey: "<c:out value='${clientKey}'/>",
  customerKey: "<c:out value='${customerKey}'/>",
  orderId: "<c:out value='${orderId}'/>",
  amount: ${amount},
  contextPath: "${pageContext.request.contextPath}"
};
</script>
<script>
(function () {
  "use strict";
  function init() {
    var cfg = window.travelTossConfig;
    var button = document.getElementById("piPay");
    if (!button) { console.error("[Toss] piPay 버튼 없음"); return; }
    if (button.dataset.tossInitialized === "true") return;
    button.dataset.tossInitialized = "true";
    var status = document.getElementById("piPaymentStatus");
    function show(message) {
      console.error("[Toss] " + message);
      if (status) { status.textContent = message; status.style.display = "block"; }
      else alert(message);
    }
    button.addEventListener("click", async function () {
      if (status) { status.textContent = ""; status.style.display = "none"; }
      var t1 = document.getElementById("piTerms1");
      var t2 = document.getElementById("piTerms2");
      if (!t1 || !t2 || !t1.checked || !t2.checked) {
        show("필수 확인 사항 두 가지에 동의해 주세요."); return;
      }
      if (!cfg || !cfg.clientKey || !cfg.customerKey || !cfg.orderId ||
          !Number.isFinite(Number(cfg.amount)) || Number(cfg.amount) <= 0) {
        show("결제 설정이 누락되었습니다. 페이지를 다시 열어 주세요."); return;
      }
      if (typeof window.TossPayments !== "function") {
        show("토스 결제 SDK를 불러오지 못했습니다. 네트워크 및 SDK 스크립트를 확인해 주세요."); return;
      }
      button.disabled = true;
      try {
        var toss = window.TossPayments(cfg.clientKey);
        var payment = toss.payment({customerKey: cfg.customerKey});
        var base = window.location.origin + (cfg.contextPath || "");
        console.log("[Toss] requestPayment 시작", {orderId:cfg.orderId, amount:cfg.amount});
        await payment.requestPayment({
          method: "CARD",
          amount: {currency: "KRW", value: Number(cfg.amount)},
          orderId: cfg.orderId,
          orderName: "여행 가이드 투어 예약",
          successUrl: base + "/TossPaymentSuccess",
          failUrl: base + "/TossPaymentFail"
        });
      } catch (e) {
        console.error("[Toss] 결제 요청 실패", e);
        if (!e || e.code !== "USER_CANCEL") show("결제창 실행 실패: " + (e && e.message ? e.message : "알 수 없는 오류"));
      } finally { button.disabled = false; }
    });
    console.log("[Toss] 결제 버튼 이벤트 연결 완료");
  }
  if (document.readyState === "loading") document.addEventListener("DOMContentLoaded", init);
  else init();
})();
</script>
<script>
(function(){
  var options = document.querySelectorAll('.pi-method');
  options.forEach(function(option){
    option.addEventListener('change', function(){
      options.forEach(function(item){
        item.classList.toggle('selected', item.querySelector('input').checked);
      });
    });
  });
})();
</script>
