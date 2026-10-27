<%-- =========================================================
     주제: 파일 역할
     설명: qna 영역의 qna 화면을 구성하고 서버에서 전달받은 데이터를 표시
========================================================= --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%--=========================================================
주제: 놀러가자 Q&A 게시판
설명: 기존 main.jsp의 헤더·푸터를 재사용하는 중앙 콘텐츠
=========================================================--%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/qna/qna.css" />
<main class="qna-page" id="qna-page" data-open="${qnaOpen}" data-logged-in="${not empty qnaMemberId}">
 <div class="qna-shell">
  <!-- 상단 히어로 -->
  <section class="qna-hero" aria-labelledby="qna-hero-title">
   <div class="qna-hero-glow" aria-hidden="true"></div>
   <div class="qna-hero-main">
    <div class="qna-eyebrow"><span class="qna-eyebrow-point"></span> 여행자들의 질문과 답변 <span class="qna-eyebrow-en">COMMUNITY / Q&A</span></div>
    <h1 id="qna-hero-title">여행의 궁금증,<br/><em>함께 해결해요.</em></h1>
    <p>어디로 갈지, 무엇을 먹을지, 어떻게 이동할지.<br/>궁금한 순간마다 여행자들의 경험을 나눠보세요.</p>
    <button class="qna-primary-btn" id="qna-open-write" type="button"><span>＋</span> 질문 작성하기 <span aria-hidden="true">↗</span></button>
   </div>
   <div class="qna-hero-side" aria-label="질문 현황">
    <div class="qna-hero-illustration" aria-hidden="true"><span class="qna-big-question">?</span><span class="qna-orbit orbit-one"></span><span class="qna-orbit orbit-two"></span><span class="qna-small-star star-one">✦</span><span class="qna-small-star star-two">✦</span></div>
    <div class="qna-hero-stats"><div><strong>${qnaStats[0]}</strong><span>전체 질문</span></div><i></i><div><strong>${qnaStats[1]}</strong><span>답변 완료</span></div><i></i><div><strong>${qnaStats[0] - qnaStats[1]}</strong><span>답변 대기</span></div></div>
   </div>
  </section>
  <!-- 분류 / 검색 -->
  <section class="qna-toolbar" aria-label="질문 검색 및 필터">
   <div class="qna-tabs" id="qna-status-tabs">
    <button type="button" data-qna-status="all" class="qna-tab ${qnaStatus eq 'all'?'active':''}">전체 질문</button>
    <button type="button" data-qna-status="waiting" class="qna-tab ${qnaStatus eq 'waiting'?'active':''}"><span class="qna-dot waiting"></span>답변 대기</button>
    <button type="button" data-qna-status="answered" class="qna-tab ${qnaStatus eq 'answered'?'active':''}"><span class="qna-dot answered"></span>답변 완료</button>
   </div>
   <div class="qna-filters" id="qna-category-tabs">
    <button type="button" data-qna-category="" class="qna-chip ${empty qnaCategory?'active':''}">전체</button>
    <c:forEach var="cat" items="${qnaCategories}"><button type="button" data-qna-category="${fn:escapeXml(cat)}" class="qna-chip ${qnaCategory eq cat?'active':''}"><c:out value="${cat}"/></button></c:forEach>
   </div>
   <form class="qna-searchline" action="${pageContext.request.contextPath}/Qna" method="get" id="qna-filter-form">
    <input type="hidden" name="status" id="qna-hidden-status" value="${fn:escapeXml(qnaStatus)}"/>
    <input type="hidden" name="category" id="qna-hidden-category" value="${fn:escapeXml(qnaCategory)}"/>
    <label class="qna-search"><svg viewBox="0 0 24 24" aria-hidden="true"><circle cx="10.8" cy="10.8" r="6.4"/><path d="m16 16 4.5 4.5"/></svg><input type="search" name="keyword" maxlength="80" placeholder="여행 질문이나 작성자 검색" value="${fn:escapeXml(qnaKeyword)}" aria-label="질문 검색"/><button type="submit">검색</button></label>
    <select name="sort" id="qna-sort" aria-label="정렬 기준"><option value="latest" ${qnaSort eq 'latest'?'selected':''}>최신순</option><option value="popular" ${qnaSort eq 'popular'?'selected':''}>조회순</option><option value="answers" ${qnaSort eq 'answers'?'selected':''}>답변 많은 순</option></select>
   </form>
  </section>
  <div class="qna-list-heading"><div><span class="qna-section-kicker">TRAVEL QUESTIONS</span><h2>여행자들의 질문 <span><c:out value="${qnaTotal}"/></span></h2></div><p>질문을 누르면 내용과 답변을 볼 수 있어요.</p></div>
  <c:if test="${not empty param.notice}"><div class="qna-alert" id="qna-notice" role="status"><c:choose>
   <c:when test="${param.notice eq 'created'}">질문이 등록됐어요.</c:when><c:when test="${param.notice eq 'edited'}">질문이 수정됐어요.</c:when>
   <c:when test="${param.notice eq 'deleted'}">질문을 삭제했어요.</c:when><c:when test="${param.notice eq 'answered'}">답변이 등록됐어요.</c:when>
   <c:when test="${param.notice eq 'answerDeleted'}">답변을 삭제했어요.</c:when><c:when test="${param.notice eq 'denied'}">수정하거나 삭제할 권한이 없어요.</c:when>
   <c:when test="${param.notice eq 'invalid'}">제목·내용·카테고리를 확인해주세요.</c:when><c:otherwise>요청을 처리하지 못했어요. 잠시 후 다시 시도해주세요.</c:otherwise>
  </c:choose></div></c:if>
  <c:if test="${qnaDbError}"><div class="qna-alert qna-error" role="alert">Q&A 테이블을 읽을 수 없어요. MySQL에서 <code>sql/01_qna_tables.sql</code> 실행 여부를 확인해주세요.</div></c:if>
  <!-- 질문 목록: DB에서 실제로 가져온 데이터만 렌더링 -->
  <section class="qna-list" id="qna-list" aria-label="질문 목록">
   <c:forEach var="q" items="${qnaQuestions}" varStatus="loop">
    <article class="qna-item" id="qna-${q.questionId}" data-question-id="${q.questionId}">
     <button class="qna-item-main" type="button" aria-expanded="false" aria-controls="qna-content-${q.questionId}">
      <span class="qna-item-number"><c:out value="${(qnaPage-1)*8+loop.count}"/></span>
      <span class="qna-item-text"><span class="qna-meta"><span class="qna-cat"><c:out value="${q.category}"/></span><span class="qna-status ${q.answered?'done':'wait'}"><span class="qna-dot ${q.answered?'answered':'waiting'}"></span>${q.answered?'답변 완료':'답변 대기'}</span></span>
       <strong><c:out value="${q.title}"/></strong><span class="qna-excerpt"><c:out value="${q.preview}"/></span></span>
      <span class="qna-item-side"><span class="qna-author"><span class="qna-avatar"><c:out value="${fn:substring(q.memberId,0,1)}"/></span><c:out value="${q.memberId}"/></span><span class="qna-item-date"><c:out value="${q.createdAt}"/></span><span class="qna-views">조회 <b><c:out value="${q.viewCount}"/></b></span><span class="qna-reply-count">답변 <b><c:out value="${q.answerCount}"/></b></span></span>
      <span class="qna-chevron" aria-hidden="true">⌄</span>
     </button>
     <div class="qna-expand" id="qna-content-${q.questionId}">
      <div class="qna-expand-inner">
       <div class="qna-question-body"><div class="qna-answer-heading"><span class="qna-q-icon">Q</span><b>질문 내용</b></div><div class="qna-long-text"><c:out value="${q.content}"/></div>
        <c:if test="${qnaAdmin or (not empty qnaMemberId and q.memberId eq qnaMemberId)}"><div class="qna-owner-actions"><button type="button" class="qna-edit-btn" data-id="${q.questionId}" data-title="${fn:escapeXml(q.title)}" data-category="${fn:escapeXml(q.category)}">수정</button><form method="post" action="${pageContext.request.contextPath}/Qna" class="qna-delete-form"><input type="hidden" name="csrf" value="${qnaCsrf}"/><input type="hidden" name="action" value="delete"/><input type="hidden" name="id" value="${q.questionId}"/><button type="submit">질문 삭제</button></form></div></c:if>
       </div>
       <div class="qna-answers"><div class="qna-answer-heading"><span class="qna-a-icon">A</span><b>여행자 답변 <em>${q.answerCount}</em></b></div>
        <c:choose><c:when test="${empty q.answers}"><p class="qna-answer-empty">아직 답변이 없어요. 여행 경험이 있다면 첫 답변을 남겨주세요!</p></c:when>
        <c:otherwise><c:forEach var="a" items="${q.answers}"><div class="qna-answer-card"><div class="qna-answer-card-head"><span class="qna-avatar"><c:out value="${fn:substring(a.memberId,0,1)}"/></span><strong><c:out value="${a.memberId}"/></strong><time><c:out value="${a.createdAt}"/></time>
         <c:if test="${qnaAdmin or (not empty qnaMemberId and a.memberId eq qnaMemberId)}"><form method="post" action="${pageContext.request.contextPath}/Qna" class="qna-delete-form"><input type="hidden" name="csrf" value="${qnaCsrf}"/><input type="hidden" name="action" value="deleteAnswer"/><input type="hidden" name="id" value="${q.questionId}"/><input type="hidden" name="answerId" value="${a.answerId}"/><button type="submit" class="qna-delete-answer">삭제</button></form></c:if>
         </div><p class="qna-long-text"><c:out value="${a.content}"/></p></div></c:forEach></c:otherwise></c:choose>
        <c:choose><c:when test="${not empty qnaMemberId}"><form method="post" action="${pageContext.request.contextPath}/Qna" class="qna-answer-form"><input type="hidden" name="csrf" value="${qnaCsrf}"/><input type="hidden" name="action" value="answer"/><input type="hidden" name="id" value="${q.questionId}"/><label for="qna-answer-${q.questionId}">답변 남기기</label><textarea name="content" id="qna-answer-${q.questionId}" required maxlength="3000" placeholder="알고 있는 여행 정보를 친절하게 알려주세요."></textarea><div><span>서로 존중하는 말로 여행 정보를 나눠요.</span><button type="submit">답변 등록 ↗</button></div></form></c:when>
        <c:otherwise><p class="qna-login-message">답변 작성은 로그인 후 가능합니다. <a href="${pageContext.request.contextPath}/Login">로그인하기 →</a></p></c:otherwise></c:choose>
       </div>
      </div>
     </div>
    </article>
   </c:forEach>
   <c:if test="${empty qnaQuestions and not qnaDbError}"><div class="qna-empty"><span class="qna-empty-icon">?</span><h3>아직 해당하는 질문이 없어요.</h3><p>궁금한 내용을 직접 질문하고 첫 번째 대화를 시작해 보세요.</p><button type="button" class="qna-empty-write">질문 작성하기 ↗</button></div></c:if>
  </section>
  <!-- 페이지 이동 -->
  <nav class="qna-pagination" aria-label="Q&A 페이지 이동">
   <button type="button" data-qna-page="1" ${qnaPage eq 1?'disabled':''} aria-label="첫 페이지">«</button><button type="button" data-qna-page="${qnaPage-1}" ${qnaPage eq 1?'disabled':''} aria-label="이전 페이지">‹</button>
   <c:forEach var="p" items="${qnaPages}"><button type="button" data-qna-page="${p}" class="${p eq qnaPage?'active':''}" ${p eq qnaPage?'aria-current="page"':''}>${p}</button></c:forEach>
   <button type="button" data-qna-page="${qnaPage+1}" ${qnaPage ge qnaTotalPages?'disabled':''} aria-label="다음 페이지">›</button><button type="button" data-qna-page="${qnaTotalPages}" ${qnaPage ge qnaTotalPages?'disabled':''} aria-label="마지막 페이지">»</button>
  </nav>
 </div>
 <!-- 새 질문/수정 모달 -->
 <div class="qna-modal-backdrop" id="qna-write-modal" aria-hidden="true">
  <div class="qna-modal" role="dialog" aria-modal="true" aria-labelledby="qna-modal-title">
   <div class="qna-modal-top"><div><span>TRAVEL COMMUNITY</span><h2 id="qna-modal-title">질문 작성하기</h2></div><button type="button" class="qna-modal-close" id="qna-modal-close" aria-label="닫기">×</button></div>
   <form method="post" action="${pageContext.request.contextPath}/Qna" id="qna-write-form">
    <input type="hidden" name="csrf" value="${qnaCsrf}"/><input type="hidden" name="action" id="qna-write-action" value="ask"/><input type="hidden" name="id" id="qna-edit-id"/>
    <label for="qna-write-category">질문 분야 <em>*</em></label><select id="qna-write-category" name="category" required><option value="">분야를 선택해주세요</option><c:forEach var="cat" items="${qnaCategories}"><option value="${fn:escapeXml(cat)}"><c:out value="${cat}"/></option></c:forEach></select>
    <label for="qna-write-title">질문 제목 <em>*</em></label><input id="qna-write-title" name="title" type="text" maxlength="160" placeholder="예: 부산역 근처 가족 여행 맛집 추천해주세요" required/>
    <label for="qna-write-content">질문 내용 <em>*</em></label><textarea id="qna-write-content" name="content" maxlength="5000" rows="7" placeholder="여행 일정이나 궁금한 점을 자세히 적어주세요." required></textarea>
    <p>※ 개인정보나 연락처는 게시글에 작성하지 마세요.</p><div class="qna-modal-actions"><button type="button" class="qna-cancel" id="qna-modal-cancel">취소</button><button type="submit" class="qna-submit">질문 등록하기 →</button></div>
   </form>
  </div>
 </div>
 <div class="qna-toast" id="qna-toast" role="status" aria-live="polite"></div>
</main>
<script src="${pageContext.request.contextPath}/resources/js/qna/qna.js" defer></script>
