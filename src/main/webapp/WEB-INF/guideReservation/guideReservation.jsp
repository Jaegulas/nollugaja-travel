<%-- =========================================================
     주제: 파일 역할
     설명: guideReservation 영역의 guideReservation 화면을 구성하고 서버에서 전달받은 데이터를 표시
========================================================= --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<link rel="preconnect" href="https://fonts.googleapis.com" />

<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />

<link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&family=Noto+Sans+KR:wght@400;500;600;700;800&family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..24,400,0,0&display=swap" rel="stylesheet" />

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/guide/guideReservation.css?v=20261014" />



<main class="gr-page">

  <a class="gr-back" href="${pageContext.request.contextPath}/GuideList"><span class="material-symbols-outlined" aria-hidden="true">arrow_back</span> 가이드 목록으로 돌아가기</a>

  <div class="gr-head">

    <div>

      <span class="gr-pill"><c:out value="${guide.regionName}" default="지역 정보 없음" /></span>

      <span class="gr-pill gr-pill-green"><span class="material-symbols-outlined" aria-hidden="true">verified</span> 전문 가이드</span>

      <h1><c:out value="${guide.guideName}" /> 가이드 <span class="gr-title-accent">예약하기</span></h1>

      <p class="gr-muted"><c:out value="${guide.specialty}" /></p>

    </div>

    <div class="gr-price-top"><small>1인 기준가</small><strong><fmt:formatNumber value="${guide.price}" pattern="#,##0"/>원</strong></div>

  </div>



  <div class="gr-columns">

    <div class="gr-main">

      <section class="gr-card gr-profile gr-profile-premium">

        <div class="gr-profile-top">

          <c:choose>

            <c:when test="${not empty guide.guideImage}">

              <img class="gr-avatar" src="${pageContext.request.contextPath}${guide.guideImage}" alt="가이드 프로필" onerror="this.style.display='none'" />

            </c:when>

          </c:choose>

          <div>

            <h2><c:out value="${guide.guideName}" /> <span class="gr-guide-label">LOCAL GUIDE</span></h2>

            <p class="gr-muted"><c:out value="${guide.regionName}" /> · <c:out value="${guide.specialty}" /></p>

            <p><c:out value="${guide.guideIntro}" /></p>

          </div>

        </div>

        <%-- 기존 .gr-stats 블록을 아래 코드로 교체하세요. --%>
<div class="gr-stats gr-stats-facts" aria-label="가이드 예약 정보">
  <div class="gr-stat-fact">
    <span class="material-symbols-outlined" aria-hidden="true">location_on</span>
    <small>활동 지역</small>
    <strong><c:out value="${guide.regionName}" default="지역 정보 준비 중" /></strong>
  </div>
  <div class="gr-stat-fact">
    <span class="material-symbols-outlined" aria-hidden="true">explore</span>
    <small>전문 분야</small>
    <strong><c:out value="${guide.specialty}" default="로컬 투어" /></strong>
  </div>
  <div class="gr-stat-fact">
    <span class="material-symbols-outlined" aria-hidden="true">payments</span>
    <small>1인 투어 요금</small>
    <strong><fmt:formatNumber value="${guide.price}" pattern="#,##0" />원</strong>
  </div>
</div>


      </section>



      <section class="gr-card">

        <div class="gr-section-title"><span class="material-symbols-outlined gr-heading-icon" aria-hidden="true">workspace_premium</span><h2>전문 분야 및 핵심 역량</h2></div>

        <p class="gr-specialty"><span class="material-symbols-outlined" aria-hidden="true">verified</span><c:out value="${guide.specialty}" /></p>

        <c:if test="${not empty guide.tags}">

          <p class="gr-tags"><c:out value="${guide.tags}" /></p>

        </c:if>

      </section>



      <section class="gr-card gr-tour-section">

        <div class="gr-section-title"><span class="material-symbols-outlined gr-heading-icon" aria-hidden="true">route</span><h2>대표 투어 코스 안내</h2></div>

        <c:choose>

          <c:when test="${not empty tour}">

            <div class="gr-tour-intro">

              <div class="gr-tour-title"><h3><c:out value="${tour.tourTitle}" /></h3><span class="gr-duration"><span class="material-symbols-outlined" aria-hidden="true">schedule</span> <c:out value="${tour.duration}" /></span></div>

              <p><c:out value="${tour.description}" /></p>

            </div>

            <c:choose>

              <c:when test="${not empty tourCourses}">

                <div class="gr-course-list">

                  <c:forEach var="course" items="${tourCourses}">

                    <div class="gr-course-item">

                      <span class="gr-course-badge">코스 <c:out value="${course.courseOrder}" /></span>

                      <div class="gr-course-body">

                        <div class="gr-course-top"><strong><c:out value="${course.courseName}" /></strong><small><span class="material-symbols-outlined" aria-hidden="true">schedule</span> 약 <c:out value="${course.durationMinutes}" />분</small></div>

                        <p><c:out value="${course.courseDescription}" /></p>

                      </div>

                    </div>

                  </c:forEach>

                </div>

              </c:when>

              <c:otherwise><p class="gr-muted">등록된 세부 코스가 없습니다.</p></c:otherwise>

            </c:choose>

          </c:when>

          <c:otherwise><p class="gr-muted">등록된 투어 정보가 없습니다.</p></c:otherwise>

        </c:choose>

      </section>



      <c:if test="${not empty tour}">

        <section class="gr-card gr-inclusions-section">

          <div class="gr-section-title"><span class="material-symbols-outlined gr-heading-icon" aria-hidden="true">checklist</span><h2>포함 및 불포함 사항</h2></div>

          <div class="gr-inclusion-grid">

            <div class="gr-inclusion gr-included"><h3><span class="material-symbols-outlined" aria-hidden="true">check_circle</span> 포함 사항</h3><p><c:out value="${tour.included}" /></p></div>

            <div class="gr-inclusion gr-excluded"><h3><span class="material-symbols-outlined" aria-hidden="true">cancel</span> 불포함 사항</h3><p><c:out value="${tour.excluded}" /></p></div>

          </div>

        </section>

      </c:if>



      <section class="gr-card gr-policy-section">

        <div class="gr-section-title"><span class="material-symbols-outlined gr-heading-icon" aria-hidden="true">info</span><h2>예약 유의사항 및 취소 규정</h2></div>

        <p>예약 신청 후 확정 여부를 확인해 주세요. 예약 신청만으로 결제가 완료되지 않습니다.</p>

        <c:if test="${not empty tour}"><div class="gr-policy-note"><c:out value="${tour.cancellationPolicy}" /></div></c:if>

        <p class="gr-muted">실제 취소·환불 기준은 팀에서 확정한 정책을 적용해야 합니다. 결제 금액은 서버에서 다시 검증합니다.</p>

      </section>

    </div>



    <aside class="gr-card gr-booking">

      <span class="gr-eyebrow"><span class="material-symbols-outlined" aria-hidden="true">event_available</span> RESERVATION APPLICATION</span>

      <h2>투어 일정 및 인원 선택</h2>

      <p class="gr-muted"><c:out value="${guide.guideName}" /> 가이드 · <c:out value="${guide.regionName}" /></p>

      <form action="${pageContext.request.contextPath}/GuideReservation" method="post" id="grForm">

        <input type="hidden" name="guideId" value="${guide.guideId}" />

        <div class="gr-field">

          <label><span class="gr-step">01</span> 예약 옵션</label>

          <div class="gr-option"><span><c:choose><c:when test="${not empty tour}"><c:out value="${tour.tourTitle}" /></c:when><c:otherwise>기본 가이드 투어</c:otherwise></c:choose> <small>상세 시작 시간은 예약 확정 시 안내</small></span><strong><fmt:formatNumber value="${guide.price}" pattern="#,##0"/>원</strong></div>

        </div>

        <div class="gr-field">

          <label for="grDate"><span class="gr-step">02</span> 예약 날짜</label>

          <input type="date" id="grDate" name="reservationDate" min="${today}" value="${savedDate}" required />

        </div>

        <div class="gr-field">

          <label for="grPeople"><span class="gr-step">03</span> 예약 인원 (1~15명)</label>

          <div class="gr-counter">

            <button type="button" id="grMinus" aria-label="인원 감소">−</button>

            <input type="number" id="grPeople" name="peopleCount" min="1" max="15" value="${empty savedPeople ? 2 : savedPeople}" required />

            <button type="button" id="grPlus" aria-label="인원 증가">+</button>

          </div>

        </div>

        <div class="gr-total">

          <div><span>기본 요금</span><span id="grFormula"></span></div>

          <div class="gr-total-line"><strong>예상 결제 금액</strong><strong id="grTotal"></strong></div>

        </div>

        <button class="gr-submit" type="submit">예약 신청하기 <span class="material-symbols-outlined" aria-hidden="true">arrow_forward</span></button>

        <p class="gr-footnote">예약 신청만으로 결제가 완료되지 않습니다.</p>

      </form>

    </aside>

  </div>

</main>

<script>

(function () {

  const price = Number('${guide.price}');

  const people = document.getElementById('grPeople');

  const formula = document.getElementById('grFormula');

  const total = document.getElementById('grTotal');

  const fmt = n => n.toLocaleString('ko-KR') + '원';

  function update() {

    let n = Math.max(1, Math.min(15, Number(people.value) || 1));

    people.value = n;

    formula.textContent = fmt(price) + ' × ' + n + '명';

    total.textContent = fmt(price * n);

  }

  document.getElementById('grMinus').addEventListener('click', () => { people.value = Number(people.value) - 1; update(); });

  document.getElementById('grPlus').addEventListener('click', () => { people.value = Number(people.value) + 1; update(); });

  people.addEventListener('input', update);

  document.getElementById('grForm').addEventListener('submit', e => {

    update();

    if (!document.getElementById('grDate').value) { e.preventDefault(); alert('예약 날짜를 선택해 주세요.'); }

  });

  update();

})();

</script>
