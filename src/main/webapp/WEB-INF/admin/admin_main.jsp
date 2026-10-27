<%-- =========================================================
     주제: 파일 역할
     설명: admin 영역의 admin_main 화면을 구성하고 서버에서 전달받은 데이터를 표시
========================================================= --%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"  isELIgnored="false"%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<div class="admin-wrap">

<%-- ===================== 제목 ===================== --%>
<div class="admin-title">
  <span class="admin-badge">
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
      <path d="M12 3l8 3v6c0 4.5-3.4 8-8 9-4.6-1-8-4.5-8-9V6z"/>
      <path d="M9 12l2 2 4-4"/>
    </svg>
    ADMIN
  </span>
  <h1>관리자페이지</h1>
  <p class="admin-desc">회원, 예약, 게시글을 한눈에 관리하세요</p>
</div>


<%-- ===================== 메뉴 ===================== --%>
<nav class="menu">
  <%-- 회원목록 : tab 이 없거나 member 일 때 강조 --%>
<a href="${ctx}/Admin/admin_main" class="${empty tab or tab == 'member' ? 'active' : ''}">
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="9" cy="8" r="4"/><path d="M2 21c0-4 3-6 7-6s7 2 7 6"/><path d="M17 4a4 4 0 0 1 0 8M22 21c0-3-2-5-4-5.5"/></svg>
    회원목록
  </a>

  <%-- 예약자현황 : POST --%>
  <form action="${ctx}/Admin/adminGuideReservationAll" method="post">
    <input type="hidden" name="year" value="${setyear}">
    <input type="hidden" name="month" value="${setmonth}">
    <button type="submit" class="${tab == 'reservation' ? 'active' : ''}">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="5" width="18" height="16" rx="2"/><path d="M3 10h18M8 3v4M16 3v4M9 15l2 2 4-4"/></svg>
      예약자현황
    </button>
  </form>

  <%-- 리뷰 : POST --%>
  <form action="${ctx}/Admin/adminReviewAll" method="post">
    <input type="hidden" name="year" value="${setyear}">
    <input type="hidden" name="month" value="${setmonth}">
    <button type="submit" class="${tab == 'review' ? 'active' : ''}">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 3H6a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V9z"/><path d="M14 3v6h6M8 13h8M8 17h5"/></svg>
      새 리뷰글목록
    </button>
  </form>
</nav>


<%-- ===================== 년/월 조회 =====================
     조회하기를 누르면 지금 보고 있는 메뉴의 서블릿으로 이동
     (리뷰 → adminReviewAll, 회원 → admin_main)            --%>
     
<c:if test="${tab != 'memberAll'}">
<form class="filter" action="${ctx}/Admin/${empty listAction ? 'admin_main' : listAction}" method="post">

  <div class="filter-row">
    <span class="filter-title">📅 년도</span>
    <select name="year">
      <c:forEach var="y" begin="${empty minYear ? 2026 : minYear}" end="${empty maxYear ? 2027 : maxYear}">
        <option value="${y}" ${y == setyear ? 'selected' : ''}>${y}</option>
      </c:forEach>
    </select>
  </div>

  <div class="filter-row">
    <span class="filter-title">🗓️ 월</span>

    <div class="month-list">
      <c:forEach var="m" begin="1" end="12">
        <label class="month">
          <input type="radio" name="month" value="${m}" ${m == setmonth ? 'checked' : ''}>
          <span class="chip"><span class="radio-mark"></span>${m}월</span>
        </label>
      </c:forEach>
    </div>

    <button type="submit" class="btn-search">조회하기</button>

  </div>
</form>
</c:if>

<%-- 잘못된 년/월 값이 들어왔을 때 안내 --%>
<c:if test="${not empty errorMsg}">
  <p class="error-msg">${errorMsg}</p>
</c:if>

<%-- DB 오류 안내 --%>
<c:if test="${not empty dbError}">
  <p class="error-msg">${dbError}</p>
</c:if>


<%-- ===================== 목록 ===================== --%>
<c:choose>

  <%-- ---------- 가이드 예약자현황 ---------- --%>
  <c:when test="${tab == 'reservation'}">

    <%-- ===== 상태 필터 버튼 =====
         누른 버튼의 name=status, value 가 년/월과 같이 전송됨
         pick : 전체가 아닌 상태를 골랐을 때 → 나머지 버튼 흐리게 --%>
    <form class="status-grid ${empty selStatus ? '' : 'pick'}"
          action="${ctx}/Admin/adminGuideReservationAll" method="post">
      <input type="hidden" name="year" value="${setyear}">
      <input type="hidden" name="month" value="${setmonth}">

      <button type="submit" name="status" value=""
              class="status-btn all ${empty selStatus ? 'on' : ''}">
        <span class="status-name">전체</span>
        <span class="status-num">${empty statusCount['ALL'] ? 0 : statusCount['ALL']}<small>건</small></span>
      </button>

      <button type="submit" name="status" value="PAID"
              class="status-btn paid ${selStatus == 'PAID' ? 'on' : ''}">
        <span class="status-name">결제완료</span>
        <span class="status-num">${empty statusCount['PAID'] ? 0 : statusCount['PAID']}<small>건</small></span>
      </button>

      <button type="submit" name="status" value="PENDING"
              class="status-btn pending ${selStatus == 'PENDING' ? 'on' : ''}">
        <span class="status-name">결제대기</span>
        <span class="status-num">${empty statusCount['PENDING'] ? 0 : statusCount['PENDING']}<small>건</small></span>
      </button>

      <button type="submit" name="status" value="CANCELED"
              class="status-btn cancel ${selStatus == 'CANCELED' ? 'on' : ''}">
        <span class="status-name">결제취소</span>
        <span class="status-num">${empty statusCount['CANCELED'] ? 0 : statusCount['CANCELED']}<small>건</small></span>
      </button>
    </form>

    <%-- 버튼과 목록 사이 구분선 --%>
    <hr class="res-divider">

    <c:choose>
      <c:when test="${not empty reservationList}">
        <%-- 요일을 (토), (일) 처럼 한글로 --%>
        <fmt:setLocale value="ko_KR"/>

		 <%-- ===== 목록 안내 줄 : 왼쪽 건수 · 페이지 / 오른쪽 정렬 기준 ===== --%>
        <div class="list-info">
          <span class="list-count">
            총 <b>${totalCount}</b>건
            <span class="list-page">${page} / ${totalPages} 페이지</span>
          </span>
          <span class="list-sort">투어일 빠른 순</span>
        </div>
        
        <div class="res-list">
          <c:forEach var="res" items="${reservationList}">

            <%-- 상태 → class 이름 (줄 색, 뱃지 색) --%>
            <c:choose>
              <c:when test="${res.status == 'PAID'}">    <c:set var="stCls" value="paid"/>    <c:set var="stName" value="결제완료"/></c:when>
              <c:when test="${res.status == 'PENDING'}"> <c:set var="stCls" value="pending"/> <c:set var="stName" value="결제대기"/></c:when>
              <c:when test="${res.status == 'CANCELED'}"><c:set var="stCls" value="cancel"/>  <c:set var="stName" value="결제취소"/></c:when>
              <c:otherwise>                              <c:set var="stCls" value="etc"/>     <c:set var="stName" value="${res.status}"/></c:otherwise>
            </c:choose>

            <%-- details : 클릭하면 아래로 펼쳐짐 (JS 필요 없음) --%>
            <details class="res-item ${stCls}">
              <summary class="res-head">
                <span class="res-badge">${stName}</span>
                <span class="res-date"><fmt:formatDate value="${res.reservationDate}" pattern="MM-dd (E)"/></span>
                <span class="res-title">
                  <c:out value="${empty res.memberName ? res.memberId : res.memberName}"/>
                  · <c:out value="${res.regionName}"/> / 가이드 <c:out value="${res.guideName}"/>
                </span>
                <span class="res-people">${res.peopleCount}명</span>
                <span class="res-price"><fmt:formatNumber value="${res.amount}" pattern="#,###"/>원</span>
              </summary>

              <div class="res-body">
                <div class="res-grid">
                  <div><span>주문번호</span>${res.orderId}</div>
                  <div><span>예약자 ID</span>${res.memberId}</div>
                  <div><span>연락처</span>${empty res.memberHp ? '-' : res.memberHp}</div>
                  <div><span>결제수단</span>${empty res.paymentMethod ? '-' : res.paymentMethod}</div>
                  <div><span>가이드 지역</span><c:out value="${res.regionName}"/></div>
                  <div><span>가이드 연락처</span>${empty res.guidePhone ? '-' : res.guidePhone}</div>
                  <%-- approved_at 은 "2026-10-12T14:22:10+09:00" 형태 → 앞 16자리, T 를 공백으로 --%>
                  <div><span>결제일시</span>${empty res.approvedAt ? '-' : fn:replace(fn:substring(res.approvedAt, 0, 16), 'T', ' ')}</div>
                  <div><span>예약일시</span><fmt:formatDate value="${res.createdAt}" pattern="yyyy-MM-dd HH:mm"/></div>
                </div>
              </div>
            </details>

          </c:forEach>
        </div>
        
          <%-- ===== 페이지 버튼 (2페이지 이상일 때만) ===== --%>
        <c:if test="${totalPages > 1}">
          <form class="pager" action="${ctx}/Admin/adminGuideReservationAll" method="post">
            <input type="hidden" name="year" value="${setyear}">
            <input type="hidden" name="month" value="${setmonth}">
            <input type="hidden" name="status" value="${selStatus}">

            <%-- 이전 묶음 --%>
            <c:if test="${startPage > 1}">
              <button type="submit" name="page" value="${startPage - 1}" class="pager-btn">‹</button>
            </c:if>

            <%-- 페이지 번호 --%>
            <c:forEach var="p" begin="${startPage}" end="${endPage}">
              <button type="submit" name="page" value="${p}"
                      class="pager-btn ${p == page ? 'on' : ''}">${p}</button>
            </c:forEach>

            <%-- 다음 묶음 --%>
            <c:if test="${endPage < totalPages}">
              <button type="submit" name="page" value="${endPage + 1}" class="pager-btn">›</button>
            </c:if>
          </form>
        </c:if>
        
      </c:when>
      <c:otherwise>
        <c:if test="${empty dbError}"><p class="empty">예약 내역이 없습니다.</p></c:if>
      </c:otherwise>
    </c:choose>
  </c:when>

  <%-- ---------- 리뷰글 목록 ---------- --%>
  <c:when test="${tab == 'review'}">
      <%-- ===== 카테고리 탭 =====
         누른 탭의 name=category, value 가 년/월과 같이 전송됨
         탭 목록은 DB 에 있는 카테고리로 자동 생성 (categoryCount) --%>
    <form class="cat-tabs" action="${ctx}/Admin/adminReviewAll" method="post">
      <input type="hidden" name="year" value="${setyear}">
      <input type="hidden" name="month" value="${setmonth}">

      <button type="submit" name="category" value=""
              class="cat-tab ${empty selCategory ? 'on' : ''}">
        전체<b>${empty reviewTotal ? 0 : reviewTotal}</b>
      </button>

      <c:forEach var="cat" items="${categoryCount}">
        <button type="submit" name="category" value="${cat.key}"
                class="cat-tab ${selCategory == cat.key ? 'on' : ''}">
          <c:out value="${cat.key}"/><b>${cat.value}</b>
        </button>
      </c:forEach>
    </form>
    
      <div class="list-info review-info">
	  <span class="list-sort desc">작성일 최신 순</span>
	  <span class="list-page">${page} / ${totalPages} 페이지</span>
	</div>

    <c:choose>
      <c:when test="${not empty reviewList}">
        <div class="review-list">
          <c:forEach var="review" items="${reviewList}">

            <%-- details : 클릭하면 아래로 펼쳐짐 (JS 필요 없음) --%>
            <details class="review-item">
              <summary class="review-head">
                <span class="review-cat"><c:out value="${review.category}"/></span>
                <span class="review-title"><c:out value="${review.title}"/></span>
                <span class="review-cmt">💬 ${fn:length(review.commentList)}</span>
                <span class="review-meta"><c:out value="${review.authorNickname}"/> (${review.memberId})</span>
                <span class="review-meta">조회 ${review.viewCount}</span>
                <span class="review-date"><fmt:formatDate value="${review.createdAt}" pattern="yyyy-MM-dd HH:mm"/></span>
              </summary>

              <div class="review-body">
                <p class="review-content"><c:out value="${review.content}"/></p>

                <div class="comment-box">
                  <p class="comment-label">댓글 ${fn:length(review.commentList)}개</p>

                  <%-- 댓글 있음 / 없음 --%>
                  <c:choose>
                    <c:when test="${not empty review.commentList}">
                      <c:forEach var="cmt" items="${review.commentList}">
                        <div class="comment-item">
                          <div class="comment-top">
                            <strong><c:out value="${cmt.authorNickname}"/></strong>
                            <span class="comment-id">${cmt.memberId}</span>
                            <span class="comment-date"><fmt:formatDate value="${cmt.createdAt}" pattern="yyyy-MM-dd HH:mm"/></span>
                          </div>
                          <p class="comment-content"><c:out value="${cmt.content}"/></p>
                        </div>
                      </c:forEach>
                    </c:when>
                    <c:otherwise>
                      <p class="comment-empty">등록된 댓글이 없습니다.</p>
                    </c:otherwise>
                  </c:choose>
                </div>
              </div>
            </details>

          </c:forEach>
        </div>

        <%-- ===== 페이지 버튼 (2페이지 이상일 때만)
             리뷰 목록 전체 아래에 한 번만 --%>
        <c:if test="${totalPages > 1}">
          <form class="pager" action="${ctx}/Admin/adminReviewAll" method="post">
            <input type="hidden" name="year" value="${setyear}">
            <input type="hidden" name="month" value="${setmonth}">
            <input type="hidden" name="category" value="${selCategory}">

            <%-- 이전 묶음 --%>
            <c:if test="${startPage > 1}">
              <button type="submit" name="page" value="${startPage - 1}" class="pager-btn">‹</button>
            </c:if>

            <%-- 페이지 번호 --%>
            <c:forEach var="p" begin="${startPage}" end="${endPage}">
              <button type="submit" name="page" value="${p}"
                      class="pager-btn ${p == page ? 'on' : ''}">${p}</button>
            </c:forEach>

            <%-- 다음 묶음 --%>
            <c:if test="${endPage < totalPages}">
              <button type="submit" name="page" value="${endPage + 1}" class="pager-btn">›</button>
            </c:if>
          </form>
        </c:if>
      </c:when>

      <%-- 리뷰글이 없을 때 (DB 오류일 때는 위 error-msg 가 안내) --%>
      <c:otherwise>
        <c:if test="${empty dbError}"><p class="empty">등록된 리뷰글이 없습니다.</p></c:if>
      </c:otherwise>
    </c:choose>
  </c:when>

  <%-- ---------- 회원목록 ---------- --%>
    <%-- ---------- 회원목록 (월별 / 전체) ---------- --%>
  <c:otherwise>

    <%-- ===== 표 위 줄 : 왼쪽 월별/전체 토글, 오른쪽 인원 · 페이지 ===== --%>
    <div class="member-bar">
      <div class="view-toggle">
        <a href="${ctx}/Admin/admin_main" class="${tab != 'memberAll' ? 'on' : ''}">월별 조회</a>
        <a href="${ctx}/Admin/memberAll"  class="${tab == 'memberAll' ? 'on' : ''}">전체 회원</a>
      </div>

      <c:if test="${empty dbError}">
        <span class="list-count">
          <c:choose>
            <c:when test="${tab == 'memberAll'}">
              총 <b>${totalCount}</b>명
              <span class="list-page">${page} / ${totalPages} 페이지</span>
            </c:when>
            <c:otherwise>
              ${setyear}년 ${setmonth}월 &nbsp;&nbsp; 가입 <b>${fn:length(memberList)}</b>명
            </c:otherwise>
          </c:choose>
        </span>
      </c:if>
    </div>

    <c:choose>
      <c:when test="${not empty memberList}">
        <div class="table-wrap">
          <table class="member-table">
            <thead>
              <tr>
                <c:if test="${tab == 'memberAll'}"><th>번호</th></c:if>
                <th>회원아이디</th>
                <th>이름</th>
                <th>나이</th>
                <th>성별</th>
                <th>이메일</th>
                <th>주소</th>
                <th>핸드폰</th>
                <th>등록일자</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="member" items="${memberList}" varStatus="st">
                <tr>
                  <%-- 전체 번호 : 최신 회원이 가장 큰 번호 --%>
                  <c:if test="${tab == 'memberAll'}">
                    <td class="num">${totalCount - (page - 1) * pageSize - st.index}</td>
                  </c:if>
                  <td class="num"><c:out value="${member.id}"/></td>
                  <td class="name"><c:out value="${member.name}"/></td>
                  <td>${member.age}</td>
                  <td><c:out value="${member.gender}"/></td>
                  <td><c:out value="${member.email}"/></td>
                  <td><c:out value="${member.address}"/></td>
                  <td><c:out value="${member.hp}"/></td>
                  <td class="date">${fn:substring(member.reg_date, 0, 10)}</td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>

        <%-- ===== 페이지 버튼 (전체 회원 + 2페이지 이상일 때만)
             예약자현황과 같은 .pager 모양. 보기만 하는 요청이라 GET --%>
        <c:if test="${tab == 'memberAll' && totalPages > 1}">
          <form class="pager" action="${ctx}/Admin/memberAll" method="get">

            <c:if test="${startPage > 1}">
              <button type="submit" name="page" value="${startPage - 1}" class="pager-btn">‹</button>
            </c:if>

            <c:forEach var="p" begin="${startPage}" end="${endPage}">
              <button type="submit" name="page" value="${p}"
                      class="pager-btn ${p == page ? 'on' : ''}">${p}</button>
            </c:forEach>

            <c:if test="${endPage < totalPages}">
              <button type="submit" name="page" value="${endPage + 1}" class="pager-btn">›</button>
            </c:if>
          </form>
        </c:if>
      </c:when>

      <c:otherwise>
        <c:if test="${empty dbError}"><p class="empty">등록된 회원이 없습니다.</p></c:if>
      </c:otherwise>
    </c:choose>
  </c:otherwise>
  
  </c:choose>

</div>
