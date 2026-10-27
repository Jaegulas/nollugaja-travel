<%-- =========================================================
     주제: 파일 역할
     설명: 공통 레이아웃에서 상단/본문/하단 영역을 조합해 페이지 전체 화면을 구성
========================================================= --%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isELIgnored="false"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    
<%-- c:set을 통해 contextPath를 짧은 변수명(ctx)으로 저장 --%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html>
<head>
<script>
  // 배포할 때는 true → false로 바꾸면 모든 console.log가 출력되지 않음
  const DEBUG = false;
  if (!DEBUG) {
	  console.log = function () {};
	  console.info  = function () {};
	  console.debug = function () {};
	  console.warn = function () {};
  }
</script>
<!-- 이건 다른 스크립트보다 먼저 실행돼야 하니 <head>의 맨 위쪽에 두세요. 발표나 제출 전에는 false로, 개발할 때는 true로 바꾸기만 하면 돼요. -->
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Insert title here</title>

<link href="https://fonts.googleapis.com" rel="preconnect" />
<link crossorigin="" href="https://fonts.gstatic.com" rel="preconnect" />
<link
	href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;600;700;800&amp;family=Noto+Sans+KR:wght@400;500;600;700;800&amp;display=swap"
	rel="stylesheet" />
<link
	href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200"
	rel="stylesheet" />
<link
	href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap"
	rel="stylesheet" />
	
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Schibsted+Grotesk:ital,wght@0,400..900;1,400..900&family=Noto+Sans+KR:wght@400;700&display=swap" rel="stylesheet">

<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@latest/dist/tabler-icons.min.css">

<!-- 브라우저 탭 아이콘 -->
<link rel="icon" type="image/png" sizes="32x32" href="${pageContext.request.contextPath}/resources/images/favicon-32.png" />

<!-- 아이폰 / 안드로이드 홈 화면 아이콘 -->
<link rel="apple-touch-icon" sizes="180x180" href="${pageContext.request.contextPath}/resources/images/apple-touch-icon.png" />
<link rel="icon" type="image/png" sizes="180x180" href="${pageContext.request.contextPath}/resources/images/apple-touch-icon.png" />

<c:if test="${not empty pageCss}">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/${pageCss}">
 </c:if>
  
<script src="https://cdn.tailwindcss.com"></script>
<script id="tailwind-config">
	tailwind.config = {
		darkMode : "class",
		theme : {
			extend : {
				"colors" : {
					"tertiary-fixed-dim" : "#4edea3",
					"on-secondary" : "#ffffff",
					"on-error-container" : "#93000a",
					"on-secondary-fixed" : "#400100",
					"secondary" : "#ac3323",
					"on-surface" : "#131b2e",
					"on-primary-fixed" : "#001f25",
					"surface-container-low" : "#f2f3ff",
					"on-primary-fixed-variant" : "#004e5a",
					"on-secondary-container" : "#6c0300",
					"on-tertiary-fixed-variant" : "#005236",
					"on-background" : "#131b2e",
					"primary-fixed-dim" : "#73d4e9",
					"secondary-container" : "#fd6e58",
					"on-tertiary" : "#ffffff",
					"secondary-fixed-dim" : "#ffb4a7",
					"on-secondary-fixed-variant" : "#8a1a0e",
					"outline" : "#6e797c",
					"surface-dim" : "#d2d9f4",
					"surface-variant" : "#dae2fd",
					"on-primary" : "#ffffff",
					"primary-container" : "#008092",
					"inverse-on-surface" : "#eef0ff",
					"surface-container-highest" : "#dae2fd",
					"primary" : "#006574",
					"on-tertiary-fixed" : "#002113",
					"background" : "#faf8ff",
					"tertiary" : "#006947",
					"on-tertiary-container" : "#f5fff6",
					"inverse-surface" : "#283044",
					"error" : "#ba1a1a",
					"error-container" : "#ffdad6",
					"on-error" : "#ffffff",
					"tertiary-container" : "#00855b",
					"inverse-primary" : "#73d4e9",
					"on-primary-container" : "#f8fdff",
					"surface-bright" : "#faf8ff",
					"surface-container" : "#eaedff",
					"secondary-fixed" : "#ffdad4",
					"surface" : "#faf8ff",
					"primary-fixed" : "#a4eeff",
					"tertiary-fixed" : "#6ffbbe",
					"surface-container-lowest" : "#ffffff",
					"surface-container-high" : "#e2e7ff",
					"outline-variant" : "#bdc8cb",
					"surface-tint" : "#006877",
					"on-surface-variant" : "#3e494b"
				},
				"borderRadius" : {
					"DEFAULT" : "0.25rem",
					"lg" : "0.5rem",
					"xl" : "0.75rem",
					"full" : "9999px"
				},
				"spacing" : {
					"gutter" : "1.5rem",
					"space-sm" : "0.5rem",
					"space-xl" : "2.5rem",
					"margin" : "3rem",
					"space-xs" : "0.25rem",
					"space-lg" : "1.5rem",
					"margin-sm" : "1.25rem",
					"space-md" : "1rem",
					"gutter-sm" : "1rem"
				},
				"fontFamily" : {
					"label-sm" : [ "Plus Jakarta Sans" ],
					"body-md" : [ "Plus Jakarta Sans" ],
					"label-md" : [ "Plus Jakarta Sans" ],
					"body-sm" : [ "Plus Jakarta Sans" ],
					"display-mobile" : [ "Plus Jakarta Sans" ],
					"display" : [ "Plus Jakarta Sans" ],
					"headline-md" : [ "Plus Jakarta Sans" ],
					"label-lg" : [ "Plus Jakarta Sans" ],
					"headline-xl" : [ "Plus Jakarta Sans" ],
					"headline-xl-mobile" : [ "Plus Jakarta Sans" ],
					"headline-sm" : [ "Plus Jakarta Sans" ],
					"body-lg" : [ "Plus Jakarta Sans" ],
					"headline-lg" : [ "Plus Jakarta Sans" ]
				},
				"fontSize" : {
					"label-sm" : [ "11px", {
						"lineHeight" : "14px",
						"letterSpacing" : "0.04em",
						"fontWeight" : "700"
					} ],
					"body-md" : [ "15px", {
						"lineHeight" : "24px",
						"letterSpacing" : "-0.005em",
						"fontWeight" : "400"
					} ],
					"label-md" : [ "12px", {
						"lineHeight" : "16px",
						"letterSpacing" : "0.02em",
						"fontWeight" : "600"
					} ],
					"body-sm" : [ "13px", {
						"lineHeight" : "20px",
						"letterSpacing" : "0em",
						"fontWeight" : "400"
					} ],
					"display-mobile" : [ "38px", {
						"lineHeight" : "46px",
						"letterSpacing" : "-0.025em",
						"fontWeight" : "800"
					} ],
					"display" : [ "56px", {
						"lineHeight" : "64px",
						"letterSpacing" : "-0.03em",
						"fontWeight" : "800"
					} ],
					"headline-md" : [ "22px", {
						"lineHeight" : "30px",
						"letterSpacing" : "-0.015em",
						"fontWeight" : "600"
					} ],
					"label-lg" : [ "14px", {
						"lineHeight" : "20px",
						"letterSpacing" : "0.01em",
						"fontWeight" : "600"
					} ],
					"headline-xl" : [ "40px", {
						"lineHeight" : "48px",
						"letterSpacing" : "-0.025em",
						"fontWeight" : "700"
					} ],
					"headline-xl-mobile" : [ "28px", {
						"lineHeight" : "36px",
						"letterSpacing" : "-0.02em",
						"fontWeight" : "700"
					} ],
					"headline-sm" : [ "18px", {
						"lineHeight" : "26px",
						"letterSpacing" : "-0.01em",
						"fontWeight" : "600"
					} ],
					"body-lg" : [ "18px", {
						"lineHeight" : "28px",
						"letterSpacing" : "-0.01em",
						"fontWeight" : "400"
					} ],
					"headline-lg" : [ "28px", {
						"lineHeight" : "36px",
						"letterSpacing" : "-0.02em",
						"fontWeight" : "700"
					} ]
				}
			}
		}
	};
</script>
<!-- 리뷰 게시판 아이콘 -->
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

<!-- 리뷰 게시판 손글씨 폰트 -->
<link
	href="https://fonts.googleapis.com/css2?family=Nanum+Pen+Script&display=swap"
	rel="stylesheet">


<style>
/* 문화유산 페이지와 동일한 기본 글꼴 */
html, body {
	margin: 0;
	padding: 0;
	font-family: 'Plus Jakarta Sans', 'Noto Sans KR', sans-serif;
}

body {
	background: #faf8ff;
	color: #131b2e;
}

/* 리뷰 게시판 손글씨 폰트 */
.font-handwriting {
	font-family: "Nanum Pen Script", cursive;
}

/* 리뷰 게시판 배지 */
.badge-pill {
	font-size: 0.68rem;
	letter-spacing: -0.02em;
}

/* 모바일에서 상단메뉴 처리 2026.10.19토 권성애 */
@media (max-width: 768px) {
  .noleo-desktop-nav {
    max-width: 100%;
    overflow-x: auto;          /* 넘치면 옆으로 밀어서 보기 */
    flex-wrap: nowrap;
  }
  .noleo-desktop-nav a {
    white-space: nowrap;       /* 메뉴 글자 줄바꿈 안 함 */
    word-break: keep-all;
  }
}
</style>



</head>

<body class="bg-surface font-body-md text-on-surface antialiased">

	<jsp:include page="/WEB-INF/top.jsp" />

	<jsp:include page="${center}" />

  	<jsp:include page="/WEB-INF/bottom.jsp" />
  	
  	<jsp:include page="/WEB-INF/liveChat/chat.jsp" />


	<jsp:include page="/WEB-INF/common/login-return-script.jsp" />
</body>

</html>