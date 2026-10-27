<%-- =========================================================
     주제: 파일 역할
     설명: error 영역의 500 화면을 구성하고 서버에서 전달받은 데이터를 표시
========================================================= --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%
  // 에러 페이지가 200(정상)으로 응답되지 않도록 상태코드 유지
  response.setStatus(500);
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>일시적인 오류 | 놀러가자</title>
<style>
/* =========================================
   색상 모음 : 여기 값만 바꾸면 전체 색이 바뀝니다
   ========================================= */
:root {
  --bg: #fdf6ef;      /* 페이지 배경 */
  --green: #0f4c3a;   /* 포인트 (진초록) */
  --point: #a0401a;   /* 500 페이지 포인트 (갈색) */
  --chip: #efebe7;    /* 회색 버튼 배경 */
  --line: #e2dcd5;    /* 테두리 */
  --gray: #8a7f75;    /* 설명 글자 */
}

* { box-sizing: border-box; }
body {
  margin: 0;
  background: var(--bg);
  font-family: 'Pretendard', 'Malgun Gothic', sans-serif;
  color: #333;
}

/* 에러 상자를 가운데에 놓는 영역 (헤더와 푸터 사이) */
.error-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  padding: 60px 24px;
}
/* 가운데 흰 상자 */
.error-box {
  width: 100%;
  max-width: 480px;
  padding: 48px 32px 40px;
  text-align: center;
  background: #fff;
  border: 1px solid var(--line);
  border-radius: 24px;
}

/* 위쪽 동그란 아이콘 */
.error-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 72px;
  height: 72px;
  margin: 0 auto 20px;
  border-radius: 50%;
  background: #f6e9e2;        /* 연한 갈색 */
  color: var(--point);
}

.error-icon svg {
  width: 36px;
  height: 36px;
}

/* 큰 숫자 */
.error-code {
  margin: 0;
  font-size: 64px;
  font-weight: 800;
  line-height: 1;
  color: var(--point);
  letter-spacing: 4px;
}

.error-title {
  margin: 16px 0 8px;
  font-size: 20px;
}

.error-text {
  margin: 0 0 28px;
  font-size: 14px;
  line-height: 1.7;
  color: var(--gray);
}

/* 버튼 */
.error-buttons {
  display: flex;
  justify-content: center;
  gap: 8px;
}

.btn {
  display: inline-flex;
  align-items: center;
  height: 40px;
  padding: 0 22px;
  border: none;
  border-radius: 999px;
  font-size: 14px;
  font-weight: 700;
  text-decoration: none;
  cursor: pointer;
  font-family: inherit;
}

.btn-main {
  background: var(--green);
  color: #fff;
}

.btn-sub {
  background: var(--chip);
  color: #4a4038;
}

.btn:hover {
  opacity: 0.85;
}
</style>


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


</head>
<body>
<jsp:include page="../top.jsp"/>

<div class="error-wrap">
	<div class="error-box">
	  <div class="error-icon">
	    <!-- 텐트 + 느낌표 아이콘 -->
	    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
	      <path d="M3 20h18"/>
	      <path d="M12 4L4 20M12 4l8 16"/>
	      <path d="M12 11v3"/>
	      <circle cx="12" cy="17" r=".6" fill="currentColor"/>
	    </svg>
	  </div>
	
	  <p class="error-code">500</p>
	  <h1 class="error-title">잠시 문제가 생겼어요</h1>
	  <p class="error-text">
	    서버에서 요청을 처리하는 중 오류가 발생했어요.<br>
	    잠시 후 다시 시도해 주세요.
	  </p>
	
	  <div class="error-buttons">
	    <a href="${pageContext.request.contextPath}/Main" class="btn btn-main">홈으로 가기</a>
	    <button type="button" class="btn btn-sub" onclick="location.reload()">다시 시도</button>
	  </div>
	</div>
</div>
<jsp:include page="../bottom.jsp"/>

</body>
</html>
