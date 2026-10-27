<%-- =========================================================
     주제: 파일 역할
     설명: guideList 영역의 guideList 화면을 구성하고 서버에서 전달받은 데이터를 표시
========================================================= --%>
<%@ page import="java.io.InputStream,java.util.Properties" %>
<%
    // Java Resources/config.properties: deployed classpath -> WEB-INF/classes/config.properties
    String guideKakaoJsKeyValue = "";
    try {
        InputStream guideKakaoConfigStream = Thread.currentThread()
                .getContextClassLoader().getResourceAsStream("config.properties");
        if (guideKakaoConfigStream == null) {
            guideKakaoConfigStream = application.getResourceAsStream("/WEB-INF/classes/config.properties");
        }
        if (guideKakaoConfigStream != null) {
            try (InputStream guideKakaoInput = guideKakaoConfigStream) {
                Properties guideKakaoProperties = new Properties();
                guideKakaoProperties.load(guideKakaoInput);
                String guideKakaoConfigured = guideKakaoProperties
                        .getProperty("kakao.api.key1", "").trim();
                // Validate characters before placing the value into a JS string.
                if (guideKakaoConfigured.matches("[A-Za-z0-9_-]{10,128}")) {
                    guideKakaoJsKeyValue = guideKakaoConfigured;
                } else {
                    application.log("Kakao Maps: kakao.api.key is missing or has an invalid format");
                }
            }
        } else {
            application.log("Kakao Maps: config.properties not found on webapp classpath");
        }
    } catch (Exception guideKakaoException) {
        application.log("Kakao Maps: failed to read config.properties", guideKakaoException);
    }
    pageContext.setAttribute("guideKakaoJsKey", guideKakaoJsKeyValue);
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500;600;700;800;900&display=swap" rel="stylesheet">
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet">
<style>
.guide-wrap,.guide-wrap *{box-sizing:border-box;font-family:'Noto Sans KR',sans-serif}
.guide-wrap{max-width:1280px;margin:0 auto;padding:40px 24px 70px;color:#131b2e}
.guide-hero{min-height:320px;padding:55px 35px;margin-bottom:42px;border-radius:24px;background:linear-gradient(rgba(5,20,35,.55),rgba(5,20,35,.65)),url('${pageContext.request.contextPath}/resources/images/banner/travel-banner.png');background-size:cover;background-position:center;display:flex;flex-direction:column;align-items:center;justify-content:center;text-align:center}
.guide-hero h1{font-size:36px;font-weight:800;color:#fff;margin:18px 0 12px;line-height:1.4}
.guide-hero p{color:#f1f5f9;font-size:15px;margin:0}
.guide-pill{display:inline-block;background:rgba(255,255,255,.93);color:#006574;padding:7px 12px;border-radius:50px;font-size:12px;font-weight:700}
/* 여행지 페이지와 통일한 섹션 제목 */
.guide-section-title{display:flex;align-items:center;gap:9px;margin:0 0 20px;font-size:22px;font-weight:800;letter-spacing:-.7px;line-height:1.4;color:#131b2e}
.guide-section-title::before{content:"";display:block;width:5px;height:22px;border-radius:4px;background:#006574;flex-shrink:0}
.guide-section-desc{font-size:13px;color:#667085;margin:-10px 0 18px 14px}
.guide-filter{margin-bottom:46px}
.guide-regions{display:flex;flex-wrap:wrap;gap:8px;padding-bottom:20px;border-bottom:1px solid #e5eaf1}
.guide-region{display:inline-flex;align-items:center;justify-content:center;border:1px solid #e5eaf1;border-radius:30px;background:#f2f3ff;color:#3f484b;padding:10px 18px;text-decoration:none;font-size:14px;font-weight:600;transition:.2s}
.guide-region.active,.guide-region:hover{background:#006574;border-color:#006574;color:white}
.guide-search{display:flex;gap:10px;margin-top:18px}
.guide-search input{flex:1;min-width:0;padding:13px;border:1px solid #dae2fc;border-radius:10px;outline:none}
.guide-search input:focus{border-color:#006574;box-shadow:0 0 0 3px rgba(0,101,116,.1)}
.guide-search button,.guide-book{background:#006574;color:white;border:0;padding:12px 17px;border-radius:10px;cursor:pointer;text-decoration:none;font-size:13px;font-weight:600}
.guide-meta{display:flex;justify-content:space-between;align-items:center;gap:12px;margin:0 0 20px}
.guide-meta .guide-section-title{margin:0}
.guide-meta span{color:#006574;font-size:14px;white-space:nowrap}
.guide-grid{display:grid;grid-template-columns:repeat(4,minmax(0,1fr));gap:16px}
.guide-card{background:white;border:1px solid #e7eaf3;border-radius:20px;overflow:hidden;box-shadow:0 3px 14px #142b3610;display:flex;flex-direction:column;min-width:0;transition:transform .2s,box-shadow .2s}
.guide-card:hover{transform:translateY(-3px);box-shadow:0 10px 25px #142b3620}
.guide-photo{position:relative;aspect-ratio:4/3;background:#dae2fc;overflow:hidden}
.guide-photo img{width:100%;height:100%;object-fit:cover}
.guide-like-btn{position:absolute;right:12px;top:12px;z-index:4;width:32px;height:32px;display:flex;align-items:center;justify-content:center;border:0;border-radius:50%;background:rgba(255,255,255,.8);color:#131b2e;box-shadow:0 2px 8px #0002;cursor:pointer;transition:background .2s,color .2s}
.guide-like-btn:hover{background:#fff;color:#ef4444}
.guide-like-btn svg{width:20px;height:20px;display:block;fill:none;stroke:currentColor;stroke-width:2;stroke-linecap:round;stroke-linejoin:round;pointer-events:none}
.guide-like-btn[aria-pressed="true"] svg{fill:#ef4444;stroke:#ef4444}
.guide-like-btn[aria-pressed="true"]{color:#ef4444}
.guide-like-btn:focus-visible{outline:3px solid #006574;outline-offset:2px}
.guide-photo .placeholder{height:100%;display:grid;place-items:center;font-size:65px}
.guide-photo .place{position:absolute;bottom:0;left:0;right:0;color:white;padding:24px 14px 12px;background:linear-gradient(transparent,#0009);font-size:13px}
.guide-body{padding:20px;display:flex;flex-direction:column;flex:1}
.guide-heading{display:flex;justify-content:space-between;align-items:center;gap:10px}
.guide-heading h3{margin:0;font-size:19px;font-weight:700;letter-spacing:-.5px}
.guide-region-badge{font-size:12px;font-weight:700;color:#006574;background:#e9f5f3;padding:5px 9px;border-radius:20px;white-space:nowrap}
.guide-specialty-label{font-weight:700;color:#006574;margin-right:7px}
.guide-specialty{color:#006574;font-size:13px;margin:7px 0}
.guide-intro{font-size:14px;color:#56606d;min-height:44px;line-height:1.6;letter-spacing:-.3px}
.guide-tags{font-size:12px;color:#475467;background:#f2f3ff;border-radius:7px;padding:8px;margin:14px 0;overflow-wrap:anywhere}
.guide-footer{border-top:1px solid #eaedff;padding-top:16px;display:flex;justify-content:space-between;align-items:center;gap:8px;margin-top:auto}
.guide-footer small{display:block;color:#6e797c}.guide-footer strong{font-size:16px;font-weight:800;letter-spacing:-.4px}
.guide-actions{display:flex;gap:7px;align-items:center;flex-shrink:0}
.guide-detail{background:#f2f3ff;color:#131b2e;border:0;padding:12px;border-radius:10px;font-size:13px;text-decoration:none;cursor:pointer;font-weight:600}
.guide-detail,.guide-book{display:inline-flex;align-items:center;justify-content:center;white-space:nowrap;flex-shrink:0;min-width:72px;line-height:1.4}
.guide-empty{padding:50px;text-align:center;color:#667085}
.guide-pagination{display:flex;justify-content:center;align-items:center;gap:8px;margin:40px 0 10px}
.guide-pagination a{display:inline-flex;align-items:center;justify-content:center;min-width:42px;height:42px;padding:0 12px;border:1px solid #e1e7ed;border-radius:10px;text-decoration:none;color:#344054;background:white;font-size:14px;font-weight:600;transition:.2s}
.guide-pagination a:hover{border-color:#006574;color:#006574}
.guide-pagination a.active{background:#006574;border-color:#006574;color:white}
@media(max-width:1150px){.guide-grid{grid-template-columns:repeat(3,minmax(0,1fr))}}
@media(max-width:950px){.guide-grid{grid-template-columns:repeat(2,minmax(0,1fr))}}
@media(max-width:640px){.guide-grid{grid-template-columns:1fr}.guide-wrap{padding:20px 14px 50px}.guide-hero{padding:28px 18px;min-height:240px}.guide-hero h1{font-size:26px}.guide-section-title{font-size:19px}.guide-search{flex-direction:column}.guide-pagination{flex-wrap:wrap}.guide-footer{flex-wrap:wrap}}

/* Guide discovery map */
.guide-map-section{margin:-12px 0 48px}
.guide-map-heading{display:flex;justify-content:space-between;align-items:center;gap:12px;flex-wrap:wrap}
.guide-map-note{color:#667085;font-size:13px;margin:0 0 16px 14px}
.guide-map-layout{display:grid;grid-template-columns:minmax(0,62%) minmax(0,38%);gap:16px;align-items:stretch}
.guide-map-shell{position:relative;border:1px solid #e7eaf3;border-radius:20px;overflow:hidden;background:#edf3f3}
#guideKakaoMap{width:100%;height:600px}
.guide-map-message{position:absolute;top:14px;left:14px;right:14px;z-index:3;background:rgba(255,255,255,.95);padding:12px 16px;border-radius:10px;color:#344054;font-size:13px;box-shadow:0 3px 12px #0001}
.guide-map-message[hidden]{display:none}
.guide-side-panel{height:600px;overflow-y:auto;display:flex;flex-direction:column;border:1px solid #d5dcd9;border-radius:20px;background:#242724;color:#f6f4ed;padding:24px;box-sizing:border-box}
.guide-side-empty{height:100%;display:flex;flex-direction:column;align-items:center;justify-content:center;text-align:center;gap:12px;color:#c8d1cb}
.guide-side-empty strong{font-size:20px;color:#fff}
.guide-side-photo{width:108px;height:108px;object-fit:cover;border-radius:50%;border:3px solid #558a7c}
.guide-side-name{font-size:23px;font-weight:800;margin:14px 0 6px}
.guide-side-region{color:#a8d6c5;font-weight:700}
.guide-side-intro{line-height:1.8;color:#d7ddd9;white-space:pre-line;overflow-wrap:anywhere}
.guide-side-price{font-size:19px;font-weight:800;margin:20px 0}
.guide-side-actions{display:flex;gap:10px;flex-wrap:wrap;    margin-top: 45px;}
.guide-side-actions a{flex:1;text-align:center;padding:12px 8px;border-radius:10px;background:#345c51;color:#fff;text-decoration:none;font-weight:700}
 .guide-side-actions a:first-child{background:#363d39;border:1px solid #64736c}
/* Guide-specific facts sourced from existing guide cards */
.guide-side-facts{margin-top:auto;padding-top:18px;display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:8px}
.guide-side-fact{min-width:0;min-height:118px;padding:13px 7px;box-sizing:border-box;border:1px solid #414640;border-radius:15px;background:#242724;display:flex;flex-direction:column;align-items:center;justify-content:center;text-align:center;gap:7px}
.guide-side-fact-icon{font-size:21px;color:#d4e5d5;line-height:1}
.guide-side-fact-label{font-size:11px;color:#a8a69b}
.guide-side-fact-value{font-size:13px;line-height:1.35;font-weight:800;color:#e0e9dd;overflow-wrap:anywhere;word-break:keep-all}
@media(max-width:440px){.guide-side-facts{gap:5px}.guide-side-fact{padding:10px 4px}.guide-side-fact-value{font-size:11px}}
@media(max-width:800px){.guide-map-layout{grid-template-columns:1fr}.guide-side-panel{height:auto;min-height:260px}.guide-side-empty{min-height:210px}}
.guide-map-profile{display:flex;flex-direction:column;align-items:center;gap:3px;cursor:pointer;background:transparent;border:0;padding:0;filter:drop-shadow(0 3px 5px #0004)}
.guide-map-avatar{width:56px;height:56px;border-radius:50%;border:3px solid #006574;background:#fff;display:grid;place-items:center;overflow:hidden;font-size:26px}
.guide-map-avatar img{width:100%;height:100%;object-fit:cover}
.guide-map-label{max-width:110px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;background:#fff;border-radius:20px;padding:4px 9px;font-size:12px;font-weight:800;color:#17283b}
.guide-map-profile:hover .guide-map-avatar,.guide-map-profile:focus-visible .guide-map-avatar{border-color:#f59e0b;transform:scale(1.07)}
@media(max-width:640px){#guideKakaoMap{height:430px}.guide-map-section{margin-bottom:32px}}

/* 비로그인 찜하기 안내 모달: 팀 공통 디자인 */
#like-login-dialog{box-sizing:border-box;border:1px solid rgba(255,255,255,.11);padding:0;margin:auto;width:420px;max-width:calc(100vw - 32px);max-height:calc(100dvh - 32px);border-radius:24px;overflow:auto;background:#202329;color:#f7f9fb;box-shadow:0 28px 90px rgba(0,0,0,.5);font-family:inherit;}
#like-login-dialog::backdrop{background:rgba(6,9,14,.76);backdrop-filter:blur(6px);-webkit-backdrop-filter:blur(6px);}
#like-login-dialog[open]{animation:likeModalIn .2s ease-out;}
#like-login-dialog .like-modal-body{position:relative;padding:34px 30px 28px;text-align:center;background:radial-gradient(circle at 50% 0%,rgba(0,163,169,.16),transparent 58%),#202329;}
#like-login-dialog .like-modal-close{position:absolute;right:15px;top:15px;display:flex;align-items:center;justify-content:center;width:34px;height:34px;border:0;border-radius:11px;background:rgba(255,255,255,.065);color:#c7ccd3;cursor:pointer;transition:background .2s,color .2s;}
#like-login-dialog .like-modal-close:hover{background:rgba(255,255,255,.15);color:white;}
#like-login-dialog .like-modal-icon{position:relative;display:flex;align-items:center;justify-content:center;margin:9px auto 20px;width:75px;height:75px;border-radius:24px;background:linear-gradient(140deg,rgba(255,111,97,.2),rgba(255,111,97,.065));border:1px solid rgba(255,132,115,.21);color:#ff7567;}
#like-login-dialog .like-modal-icon .material-symbols-outlined{font-size:36px;font-variation-settings:'FILL' 1;}
#like-login-dialog .like-modal-lock{position:absolute;right:-6px;bottom:-4px;display:flex;align-items:center;justify-content:center;width:28px;height:28px;background:#007e88;color:#fff;border:3px solid #202329;border-radius:10px;}
#like-login-dialog .like-modal-lock .material-symbols-outlined{font-size:15px;font-variation-settings:'FILL' 0;}
#like-login-dialog .like-modal-kicker{font-size:11px;letter-spacing:.12em;font-weight:800;color:#44ccd3;margin:0 0 8px;}
#like-login-dialog .like-modal-title{font-size:23px;font-weight:800;line-height:1.45;letter-spacing:-.035em;color:#fff;margin:0;word-break:keep-all;}
#like-login-dialog .like-modal-desc{font-size:13px;line-height:1.75;color:#b4bdc7;margin:12px 0 25px;word-break:keep-all;}
#like-login-dialog .like-modal-login{display:flex;align-items:center;justify-content:center;gap:9px;min-height:49px;width:100%;box-sizing:border-box;border-radius:13px;background:#007e88;color:white;text-decoration:none;font-size:14px;font-weight:800;transition:background .18s,transform .18s;}
#like-login-dialog .like-modal-login:hover{background:#0096a1;transform:translateY(-1px);}
#like-login-dialog .like-modal-later{display:block;margin:8px auto 0;width:100%;min-height:42px;border:0;border-radius:11px;background:transparent;color:#aeb6c0;font-size:13px;font-weight:600;cursor:pointer;}
#like-login-dialog .like-modal-later:hover{background:rgba(255,255,255,.045);color:#fff;}
#like-login-dialog .like-modal-note{font-size:11px;line-height:1.5;color:#88929e;margin:11px 0 0;}
#like-login-dialog a:focus-visible,#like-login-dialog button:focus-visible{outline:2px solid #61dfe4;outline-offset:3px;}
@keyframes likeModalIn{from{opacity:0;transform:translateY(10px) scale(.985);}to{opacity:1;transform:translateY(0) scale(1);}}
@media(max-width:420px){#like-login-dialog .like-modal-body{padding:30px 22px 24px;}#like-login-dialog .like-modal-title{font-size:21px;}}
@media(prefers-reduced-motion:reduce){#like-login-dialog[open]{animation:none;}#like-login-dialog .like-modal-login{transition:none;}}
</style>

<main class="guide-wrap">
    <section class="guide-hero">
        <span class="guide-pill translate">로컬 가이드 탐색</span>
        <h1 class="translate">당신의 여행을 특별하게 만들어 줄 로컬 가이드</h1>
        <p class="translate">지역별 가이드를 찾고, 나에게 맞는 투어를 선택해보세요.</p>
    </section>

    <section class="guide-filter">
        <h2 class="guide-section-title translate">어디서 가이드를 만나볼까요?</h2>
        <nav class="guide-regions" aria-label="가이드 지역 필터">
            <c:forEach var="r" items="전체,서울,부산,제주,경주,강릉,전주">
                <c:url var="regionUrl" value="/GuideList">
                    <c:param name="region" value="${r}" />
                    <c:param name="keyword" value="${keyword}" />
                </c:url>
                <a class="guide-region ${selectedRegion eq r ? 'active' : ''}" href="${regionUrl}"><c:out value="${r}" /></a>
            </c:forEach>
        </nav>
        <form class="guide-search" action="${pageContext.request.contextPath}/GuideList" method="get">
            <input type="hidden" name="region" value="<c:out value='${selectedRegion}'/>" />
            <input type="text" name="keyword" value="<c:out value='${keyword}'/>" placeholder="가이드 이름, 전문 분야, 여행지 검색" aria-label="가이드 검색" />
            <button type="submit" class="translate">조건 검색</button>
        </form>
    </section>


    <section class="guide-map-section" aria-labelledby="guideMapTitle">
        <div class="guide-map-heading"><h2 class="guide-section-title translate" id="guideMapTitle">지도에서 로컬 가이드 찾기</h2></div>
        <p class="guide-map-note translate">지도 속 가이드 프로필을 누르면 상세 정보를 확인할 수 있습니다. 마커는 지역 대표 위치를 나타냅니다.</p>
        <div class="guide-map-layout">
            <div class="guide-map-shell">
                <div id="guideKakaoMap" aria-label="지역별 가이드 위치 지도"></div>
                <div class="guide-map-message" id="guideMapMessage" hidden></div>
            </div>
            <aside class="guide-side-panel" id="guideSidePanel" aria-live="polite">
                <div class="guide-side-empty"><span style="font-size:40px">🧭</span><strong>가이드를 선택해 주세요</strong><span>지도 위 가이드의 얼굴을 누르면<br>여기에 상세 정보가 표시됩니다.</span></div>
            </aside>
        </div>
        <div id="guideMapData" hidden>
            <c:forEach var="g" items="${guideList}">
                <span class="guide-map-data" data-id="${g.guideId}" data-name="<c:out value='${g.guideName}'/>" data-region="<c:out value='${g.regionName}'/>" data-image="<c:out value='${g.guideImage}'/>"></span>
            </c:forEach>
        </div>
    </section>

    <section class="guide-results">
        <div class="guide-meta">
            <h2 class="guide-section-title translate">나에게 맞는 로컬 가이드</h2>
            <span>총 <c:out value="${totalCount}" default="0" />명</span>
        </div>
        <div class="guide-grid">
            <c:forEach var="g" items="${guideList}">
                <article class="guide-card">
                    <div class="guide-photo">
                        <button type="button" class="guide-like-btn" data-guide-like="${g.guideId}" aria-label="가이드 좋아요" aria-pressed="false" title="좋아요"><svg viewBox="0 0 24 24" aria-hidden="true" focusable="false"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78L12 21.23l8.84-8.84a5.5 5.5 0 0 0 0-7.78Z"/></svg></button>
                        <c:choose>
                            <c:when test="${not empty g.guideImage}">
                                <c:choose>
                                    <c:when test="${g.guideImage.startsWith('http://') or g.guideImage.startsWith('https://')}">
                                        <img src="<c:out value='${g.guideImage}'/>" alt="<c:out value='${g.guideName}'/> 가이드 프로필" loading="lazy" />
                                    </c:when>
                                    <c:otherwise>
                                        <img src="${pageContext.request.contextPath}<c:out value='${g.guideImage}'/>" alt="<c:out value='${g.guideName}'/> 가이드 프로필" loading="lazy" />
                                    </c:otherwise>
                                </c:choose>
                            </c:when>
                            <c:otherwise><div class="placeholder">🧭</div></c:otherwise>
                        </c:choose>
                    </div>
                    <div class="guide-body">
                        <div class="guide-heading">
                            <h3 class="translate"><c:out value="${g.guideName}" /> 가이드</h3>
                            <span class="guide-region-badge"><c:out value="${g.regionName}" /></span>
                        </div>
                        <p class="guide-specialty"><span class="guide-specialty-label translate">전문 분야</span><span class="translate"><c:out value="${g.specialty}" /></span></p>
                        <p class="guide-intro translate"><c:out value="${g.guideIntro}" /></p>
                        <div class="guide-tags translate"><c:out value="${g.tags}" /></div>
                        <div class="guide-footer">
                            <div><small class="translate">1인 투어 요금</small><strong><fmt:formatNumber value="${g.price}" pattern="#,##0" />원</strong></div>
                            <div class="guide-actions">
                                <a class="guide-detail" data-guide-detail="${g.guideId}" href="${pageContext.request.contextPath}/GuideDetail?guideId=${g.guideId}">상세보기</a>
                                <a class="guide-book" href="${pageContext.request.contextPath}/GuideReservation?guideId=${g.guideId}">예약하기</a>
                            </div>
                        </div>
                    </div>
                </article>
            </c:forEach>
        </div>
        <c:if test="${empty guideList}"><div class="guide-empty translate">선택한 조건에 맞는 가이드가 없습니다.</div></c:if>
        <c:if test="${totalPages gt 1}">
            <nav class="guide-pagination" aria-label="가이드 목록 페이지 이동">
                <c:forEach var="p" begin="1" end="${totalPages}">
                    <c:url var="pageUrl" value="/GuideList">
                        <c:param name="region" value="${selectedRegion}" />
                        <c:param name="keyword" value="${keyword}" />
                        <c:param name="page" value="${p}" />
                    </c:url>
                    <a href="${pageUrl}" class="${currentPage eq p ? 'active' : ''}" ${currentPage eq p ? 'aria-current="page"' : ''}>${p}</a>
                </c:forEach>
            </nav>
        </c:if>
    </section>
</main>

<!-- 로그인 전 가이드 찜하기 안내 -->
<dialog id="like-login-dialog" aria-labelledby="like-login-title" aria-describedby="like-login-desc">
 <div class="like-modal-body">
  <button type="button" class="like-modal-close" data-like-login-close aria-label="팝업 닫기"><span class="material-symbols-outlined">close</span></button>
  <div class="like-modal-icon" aria-hidden="true"><span class="material-symbols-outlined">favorite</span><span class="like-modal-lock"><span class="material-symbols-outlined">lock</span></span></div>
  <p class="like-modal-kicker">MY TRAVEL FAVORITES</p>
  <h2 class="like-modal-title" id="like-login-title">찜하기는 로그인 후<br>이용할 수 있어요.</h2>
  <p class="like-modal-desc" id="like-login-desc">마음에 드는 맛집과 관광지를 저장하고,<br>마이페이지에서 한 번에 확인해 보세요.</p>
  <a class="like-modal-login" href="${pageContext.request.contextPath}/Login" autofocus><span class="material-symbols-outlined" style="font-size:19px;">login</span> 로그인하기 <span class="material-symbols-outlined" style="font-size:18px;">arrow_forward</span></a>
  <button class="like-modal-later" type="button" data-like-login-close>다음에 할게요</button>
  <p class="like-modal-note">로그인 전에는 찜한 장소가 저장되지 않습니다.</p>
 </div>
</dialog>
<script>
(function(){
  const dialog = document.getElementById('like-login-dialog');
  if (!dialog) return;
  dialog.querySelectorAll('[data-like-login-close]').forEach(button => {
    button.addEventListener('click', () => dialog.close());
  });
})();
</script>

<jsp:include page="/WEB-INF/guideList/guideModal.jsp" />
<script>
(function(){
  'use strict';
  const api = '${pageContext.request.contextPath}/GuideLike';
  const buttons = Array.from(document.querySelectorAll('[data-guide-like]'));
  if (!buttons.length) return;
  function paint(button, liked){
    button.setAttribute('aria-pressed', String(liked));
    button.title = liked ? '좋아요 취소' : '좋아요';
  }
  fetch(api, {credentials:'same-origin',headers:{'Accept':'application/json'}})
    .then(r => { if(!r.ok) throw new Error('좋아요 조회 실패'); return r.json(); })
    .then(data => { const liked = new Set((data.guideIds || []).map(String));
      buttons.forEach(b => paint(b, liked.has(b.dataset.guideLike))); })
    .catch(err => console.warn(err));
  buttons.forEach(button => button.addEventListener('click', async function(event){
    event.preventDefault(); event.stopPropagation();
    if(button.disabled) return;
    const liked = button.getAttribute('aria-pressed') === 'true';
    const params = new URLSearchParams({guideId:button.dataset.guideLike, action:liked?'remove':'add'});
    button.disabled = true;
    try {
      const r = await fetch(api, {method:'POST',credentials:'same-origin',headers:{'Content-Type':'application/x-www-form-urlencoded;charset=UTF-8','Accept':'application/json'},body:params.toString()});
      if(r.status === 401){ document.getElementById('like-login-dialog').showModal(); return; }
      if(!r.ok) throw new Error('HTTP '+r.status);
      const data = await r.json();
      paint(button, !!data.liked);
    } catch(err){console.error(err);alert('좋아요 처리에 실패했습니다. 잠시 후 다시 시도해 주세요.');}
    finally{button.disabled=false;}
  }));
})();
</script>


<script>
(function(){
  'use strict';
  const mapNode=document.getElementById('guideKakaoMap');
  if(!mapNode || mapNode.dataset.initialized==='true')return;
  mapNode.dataset.initialized='true';
  const message=document.getElementById('guideMapMessage');
  const contextPath='${pageContext.request.contextPath}';
  const sidePanel=document.getElementById('guideSidePanel');
  function showGuideSide(id){
    const cards=Array.from(document.querySelectorAll('.guide-card'));
    const card=cards.find(c=>c.querySelector('[data-guide-detail]')?.getAttribute('data-guide-detail')===id);
    if(!card){sidePanel.textContent='선택한 가이드의 정보를 현재 목록에서 찾을 수 없습니다.';return;}
    const heading=card.querySelector('.guide-heading h3')?.textContent.trim()||'로컬 가이드';
    const region=card.querySelector('.guide-region-badge')?.textContent.trim()||'';
    const specialty=card.querySelector('.guide-specialty')?.textContent.trim()||'';
    const intro=card.querySelector('.guide-intro')?.textContent.trim()||'';
    const tags=card.querySelector('.guide-tags')?.textContent.trim()||'';
    const price=card.querySelector('.guide-footer strong')?.textContent.trim()||'';
    const photo=card.querySelector('.guide-photo img');
    const detail=card.querySelector('.guide-detail');
    const book=card.querySelector('.guide-book');
    sidePanel.replaceChildren();
    if(photo){const img=document.createElement('img');img.className='guide-side-photo';img.src=photo.src;img.alt=heading;sidePanel.appendChild(img);}
    function line(tag,cls,value){if(!value)return;const el=document.createElement(tag);el.className=cls;el.textContent=value;sidePanel.appendChild(el);}
    line('div','guide-side-name',heading);
    line('div','guide-side-region',region);
    line('p','guide-side-intro',specialty);
    line('p','guide-side-intro',intro);
    line('p','guide-side-intro',tags);
    
    const actions=document.createElement('div');actions.className='guide-side-actions';
    if(detail){const a=document.createElement('a');a.href=detail.href;a.textContent='상세보기';a.addEventListener('click',function(e){e.preventDefault();detail.click();});actions.appendChild(a);}
    if(book){const a=document.createElement('a');a.href=book.href;a.textContent='예약하기';actions.appendChild(a);}
    sidePanel.appendChild(actions);
    // Values are read from the selected guide card, not hard-coded per guide.
    const facts=document.createElement('section');
    facts.className='guide-side-facts';
    facts.setAttribute('aria-label','선택한 가이드 주요 정보');
    function addFact(icon,label,value){
      const box=document.createElement('div');box.className='guide-side-fact';
      const ico=document.createElement('span');ico.className='guide-side-fact-icon';ico.textContent=icon;
      const lab=document.createElement('span');lab.className='guide-side-fact-label';lab.textContent=label;
      const val=document.createElement('strong');val.className='guide-side-fact-value';val.textContent=value||'정보 없음';
      box.append(ico,lab,val);facts.appendChild(box);
    }
    const specialtyValue=card.querySelector('.guide-specialty > .translate')?.textContent.trim() || specialty.replace(/^전문 분야\s*/, '').trim();
    addFact('⌖','활동 지역',region);
    addFact('✧','전문 분야',specialtyValue);
    addFact('▣','1인 투어 요금',price);
    sidePanel.appendChild(facts);
  }
  // Capture the marker click before the existing modal's delegated click handler.
  mapNode.addEventListener('click',function(e){
    const trigger=e.target.closest('[data-guide-detail]');
    if(!trigger || !mapNode.contains(trigger))return;
    e.preventDefault();e.stopPropagation();e.stopImmediatePropagation();
    showGuideSide(trigger.getAttribute('data-guide-detail'));
  },true);
  const regionPoints={
    '서울':[37.5665,126.9780],'부산':[35.1796,129.0756],
    '제주':[33.4996,126.5312],'경주':[35.8562,129.2247],
    '강릉':[37.7519,128.8761],'전주':[35.8242,127.1480]
  };
  function showMessage(text){message.textContent=text;message.hidden=false;}
  function safeImage(raw){
    if(!raw)return '';
    if(/^https?:\/\//i.test(raw))return raw;
    if(raw.startsWith('/resources/'))return contextPath+raw;
    if(raw.startsWith(contextPath+'/resources/'))return raw;
    return '';
  }
  function initMap(){
    if(!window.kakao || !kakao.maps){showMessage('지도를 불러오지 못했습니다. 카카오맵 JavaScript 키와 등록 도메인을 확인해 주세요.');return;}
    kakao.maps.load(function(){
      const selected=document.querySelector('.guide-region.active');
      const region=selected?selected.textContent.trim():'전체';
      const center=regionPoints[region]||[36.35,127.9];
      const map=new kakao.maps.Map(mapNode,{center:new kakao.maps.LatLng(center[0],center[1]),level:regionPoints[region]?8:13});
      const rows=Array.from(document.querySelectorAll('.guide-map-data'));
      const regionCounts={};let displayed=0;
      rows.forEach(function(row){
        const id=row.dataset.id, name=row.dataset.name||'로컬 가이드', regionName=(row.dataset.region||'').trim();
        const point=regionPoints[regionName];
        if(!point || !/^\d+$/.test(id||''))return;
        const index=regionCounts[regionName]||0;regionCounts[regionName]=index+1;
        // Nearby representative points prevent identical-region markers from completely overlapping.
        const angle=index*2.3999632297, distance=0.008*Math.sqrt(index);
        const lat=point[0]+Math.sin(angle)*distance;
        const lng=point[1]+Math.cos(angle)*distance;
        const button=document.createElement('button');
        button.type='button';button.className='guide-map-profile';
        button.dataset.guideDetail=id;
        button.setAttribute('aria-label',name+' 가이드 상세보기');
        const avatar=document.createElement('span');avatar.className='guide-map-avatar';
        const imageUrl=safeImage(row.dataset.image);
        if(imageUrl){const img=document.createElement('img');img.src=imageUrl;img.alt='';img.onerror=function(){img.remove();avatar.textContent='🧭';};avatar.appendChild(img);}
        else avatar.textContent='🧭';
        const label=document.createElement('span');label.className='guide-map-label';label.textContent=name;
        button.append(avatar,label);
        new kakao.maps.CustomOverlay({position:new kakao.maps.LatLng(lat,lng),content:button,yAnchor:1,zIndex:2}).setMap(map);
        displayed++;
      });
      if(!displayed)showMessage('현재 목록에 지도에 표시할 가이드가 없습니다.');
      setTimeout(function(){map.relayout();map.setCenter(new kakao.maps.LatLng(center[0],center[1]));},100);
    });
  }
  // Key is read server-side from classpath config.properties (kakao.api.key).
  const appKey='${guideKakaoJsKey}';
  if(!appKey){
    showMessage('config.properties의 kakao.api.key 설정 또는 클래스패스 배치를 확인해 주세요.');
    return;
  }
  if(window.kakao && window.kakao.maps){initMap();return;}
  const existingSdk=document.querySelector('script[src*="dapi.kakao.com/v2/maps/sdk.js"]');
  if(existingSdk){
    if(window.kakao && window.kakao.maps){initMap();return;}
    existingSdk.addEventListener('load',initMap,{once:true});
    existingSdk.addEventListener('error',function(){showMessage('카카오맵 SDK 로딩 오류: 개발자 도구 Network에서 sdk.js 응답을 확인해 주세요.');},{once:true});
    setTimeout(function(){
      if(!window.kakao || !window.kakao.maps){
        showMessage('카카오맵 SDK를 사용할 수 없습니다. 기존 SDK의 키와 로딩 상태를 확인해 주세요.');
      }
    },5000);
    return;
  }
  const sdk=document.createElement('script');
  sdk.src='https://dapi.kakao.com/v2/maps/sdk.js?autoload=false&appkey='+encodeURIComponent(appKey);
  sdk.onload=function(){
    if(window.kakao && window.kakao.maps){initMap();}
    else{showMessage('카카오맵 SDK 응답은 왔지만 지도 객체가 없습니다. Console 오류를 확인해 주세요.');}
  };
  sdk.onerror=function(){showMessage('카카오맵 SDK 요청이 실패했습니다. F12 → Network → sdk.js 상태 코드 및 카카오맵 사용 설정/허용 도메인을 확인해 주세요.');};
  document.head.appendChild(sdk);
})();
</script>

