<%-- =========================================================
     주제: 파일 역할
     설명: cultural 영역의 cultural 화면을 구성하고 서버에서 전달받은 데이터를 표시
========================================================= --%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>



<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>



<!DOCTYPE html>



<html lang="ko">



<head>

<meta charset="utf-8" />

<meta name="viewport" content="width=1200, initial-scale=1.0">

<meta content="web_standard" name="shell-type" />

<title>놀러가자 - 문화유산 탐색</title>



<link href="https://fonts.googleapis.com" rel="preconnect" />

<link crossorigin="" href="https://fonts.gstatic.com" rel="preconnect" />

<link
	href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;600;700;800&family=Noto+Sans+KR:wght@400;500;600;700;800&display=swap"
	rel="stylesheet" />

<link
	href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200"
	rel="stylesheet" />

<link
	href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap"
	rel="stylesheet" />

<script src="https://cdn.tailwindcss.com"></script>

<script id="tailwind-config">

tailwind.config = {

    darkMode : "class",

    theme : {

        extend : {

            colors : {

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

            borderRadius : {

                "DEFAULT" : "0.25rem",

                "lg" : "0.5rem",

                "xl" : "0.75rem",

                "full" : "9999px"

            },

            spacing : {

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

            fontFamily : {

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

            fontSize : {

                "label-sm" : [ "11px", { "lineHeight":"14px", "letterSpacing":"0.04em", "fontWeight":"700" } ],

                "body-md" : [ "15px", { "lineHeight":"24px", "letterSpacing":"-0.005em", "fontWeight":"400" } ],

                "label-md" : [ "12px", { "lineHeight":"16px", "letterSpacing":"0.02em", "fontWeight":"600" } ],

                "body-sm" : [ "13px", { "lineHeight":"20px", "letterSpacing":"0em", "fontWeight":"400" } ],

                "display-mobile" : [ "38px", { "lineHeight":"46px", "letterSpacing":"-0.025em", "fontWeight":"800" } ],

                "display" : [ "56px", { "lineHeight":"64px", "letterSpacing":"-0.03em", "fontWeight":"800" } ],

                "headline-md" : [ "22px", { "lineHeight":"30px", "letterSpacing":"-0.015em", "fontWeight":"600" } ],

                "label-lg" : [ "14px", { "lineHeight":"20px", "letterSpacing":"0.01em", "fontWeight":"600" } ],

                "headline-xl" : [ "40px", { "lineHeight":"48px", "letterSpacing":"-0.025em", "fontWeight":"700" } ],

                "headline-xl-mobile" : [ "28px", { "lineHeight":"36px", "letterSpacing":"-0.02em", "fontWeight":"700" } ],

                "headline-sm" : [ "18px", { "lineHeight":"26px", "letterSpacing":"-0.01em", "fontWeight":"600" } ],

                "body-lg" : [ "18px", { "lineHeight":"28px", "letterSpacing":"-0.01em", "fontWeight":"400" } ],

                "headline-lg" : [ "28px", { "lineHeight":"36px", "letterSpacing":"-0.02em", "fontWeight":"700" } ]

            }

        }

    }

};

</script>



<style>
html, body {
	margin: 0;
	padding: 0;
	min-width: 1200px;
	font-family: 'Plus Jakarta Sans', 'Noto Sans KR', sans-serif;
}

body {
	background: #faf8ff;
	color: #131b2e;
}

::-webkit-scrollbar {
	display: none;
}

/
* 문화유산 좋아요 하트 * /

.heritage-like-svg {
	width: 20px;
	height: 20px;
	display: block;
	overflow: visible;
}

.heritage-like-path {
	fill: transparent;
	stroke: currentColor;
	stroke-width: 2.4;
	stroke-linecap: round;
	stroke-linejoin: round;
	transition: fill .18s ease, stroke .18s ease;
}

button.heritage-like-button.liked .heritage-like-path {
	fill: #ef4444;
	stroke: #ef4444;
}

.filter-active {
	background: #006574 !important;
	color: #fff !important;
	font-weight: 700;
}

/* 문화유산 찜 저장/취소 알림 */
#common-favorite-toast {
	position: fixed;
	left: 50%;
	bottom: 32px;
	z-index: 99999;
	display: flex;
	align-items: center;
	gap: 10px;
	min-width: 240px;
	max-width: calc(100vw - 32px);
	padding: 14px 20px;
	border-radius: 14px;
	background: rgba(25, 42, 57, .96);
	color: #fff;
	box-shadow: 0 12px 30px rgba(0, 0, 0, .22);
	font-size: 14px;
	font-weight: 700;
	opacity: 0;
	transform: translate(-50%, 16px);
	pointer-events: none;
	transition: opacity .22s ease, transform .22s ease;
}

#common-favorite-toast.show {
	opacity: 1;
	transform: translate(-50%, 0);
}

#common-favorite-toast .favorite-toast-heart {
	color: #ff756b;
	font-size: 19px;
}

#common-favorite-toast.removed .favorite-toast-heart {
	color: #91b4c6;
}
</style>

</head>



<body class="bg-surface font-body-md text-on-surface antialiased">



	<jsp:include page="/WEB-INF/top.jsp" />

	<!-- 현재 페이지: 문화유산 메뉴 강조 -->
	<script>
		document.addEventListener('DOMContentLoaded', function () {
			const heritageNav = document.querySelector('nav a[data-path="cultural-heritage"]');
			if (heritageNav) {
				heritageNav.classList.remove('text-on-surface-variant');
				heritageNav.classList.add('bg-surface-container-high', 'text-primary', 'font-bold');
			}
		});
	</script>




	<main class="w-full bg-surface">



		<div class="flex flex-col w-full">




			<!-- HEADER -->



			<section
				class="w-full bg-gradient-to-b from-surface-container-low/80 via-surface to-surface pt-6 pb-2">



				<div
					class="max-w-[1320px] mx-auto px-margin-sm lg:px-margin space-y-4">



					<nav
						class="flex items-center gap-1 text-on-surface-variant text-sm">



						<a href="${pageContext.request.contextPath}/Main"
							class="hover:text-primary flex items-center gap-1 font-semibold">
							<span class="material-symbols-outlined text-[16px]">home</span>홈
						</a> <span class="material-symbols-outlined text-[14px]">chevron_right</span>
						<span class="text-primary font-bold font-semibold">문화유산</span>

					</nav>

					<div
						class="bg-surface-container-lowest rounded-3xl p-6 lg:p-8 shadow-sm border border-surface-container space-y-6">



						<div
							class="flex flex-col lg:flex-row lg:items-end justify-between gap-4">



							<div class="space-y-2">



								<div
									class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-surface-container text-primary font-label-sm text-label-sm font-semibold">
									<span class="">🏛️</span><span class="">대한민국 역사와 숨결이 살아
										숨 쉬는 공간</span>
								</div>



								<h1
									class="font-headline-xl text-headline-xl font-bold tracking-tight text-on-surface">문화유산
									탐색</h1>



								<p
									class="font-body-md text-body-md text-on-surface-variant whitespace-nowrap leading-relaxed">천년의
									세월을 간직한 국보·사적부터 찬란한 유네스코 세계유산까지 대한민국 대표 문화유산을 한눈에 살펴보세요.</p>



							</div>


							<div
								class="flex items-center gap-2 bg-surface-container-low/70 px-4 py-2.5 rounded-2xl text-sm">


								<span class="text-outline">검색 결과</span> <strong
									class="text-primary">${totalCount}</strong> <span
									class="text-outline">건</span>



							</div>



						</div>







						<!-- SEARCH -->



						<form action="${pageContext.request.contextPath}/Cultural"
							method="get" id="searchForm">

							<input type="hidden" name="sort" value="<c:out value='${sort}'/>">

							<div
								class="bg-surface-container-low rounded-2xl p-1.5 flex items-center gap-2 border border-surface-container">



								<div class="flex items-center gap-2 flex-1 px-3 py-1.5">



									<span
										class="material-symbols-outlined text-outline text-[22px]">search</span>



									<input
										class="w-full bg-transparent text-on-surface placeholder:text-outline focus:outline-none"
										id="heritageSearchInput" name="keyword"
										value="<c:out value='${keyword}'/>"
										placeholder="문화유산 이름 또는 키워드를 검색해보세요 (예: 경복궁, 불국사, 수원화성)"
										type="text">



									<button type="button" id="clearSearchBtn"
										class="text-outline hover:text-on-surface transition-colors"
										aria-label="검색어 지우기">

										<span class="material-symbols-outlined text-[24px]">
											close </span>

									</button>


								</div>



								<button
									class="inline-flex items-center justify-center gap-1.5 px-6 py-3 rounded-xl bg-primary text-on-primary font-bold shadow-sm hover:bg-primary-container transition-all"
									id="searchTriggerBtn" type="submit">



									<span class="material-symbols-outlined text-[18px]">search</span>검색



								</button>



							</div>



						</form>



					</div>



				</div>



			</section>







			<!-- FILTER -->

			<section
				class="w-full max-w-[1320px] mx-auto px-margin-sm lg:px-margin pt-4">
				<div
					class="bg-surface-container-lowest rounded-2xl p-4 shadow-sm border border-surface-container space-y-3">
					<div
						class="flex flex-col md:flex-row md:items-center justify-between gap-3">
						<div
							class="flex items-center gap-2 text-label-md font-label-md text-on-surface font-semibold shrink-0 ">
							<span class="material-symbols-outlined text-primary text-[18px] ">location_on</span><span
								class="text-[16px] font-bold">지역</span>
						</div>
						<div class="flex flex-wrap items-center gap-1.5 flex-1"
							id="regionFilters">

							<!-- 전체 -->
							<a
								href="${pageContext.request.contextPath}/Cultural?region=&sort=${sort}&pageNo=1"
								class="px-3 py-1.5 rounded-full
						       ${empty region ? 'bg-primary text-on-primary font-bold' : 'bg-surface-container-low text-on-surface-variant'}">
								전체 </a>

							<!-- 서울 -->
							<a
								href="${pageContext.request.contextPath}/Cultural?region=서울&sort=${sort}&pageNo=1"
								class="px-3 py-1.5 rounded-full
								       ${region == '서울'
								       ? 'bg-primary text-on-primary font-bold'
								       : 'bg-surface-container-low text-on-surface-variant'}">
								서울 </a>

							<!-- 경기·인천 -->
							<a
								href="${pageContext.request.contextPath}/Cultural?region=경기인천&sort=${sort}&pageNo=1"
								class="px-3 py-1.5 rounded-full
						       ${region == '경기인천' ? 'bg-primary text-on-primary font-bold' : 'bg-surface-container-low text-on-surface-variant'}">
								경기·인천 </a>

							<!-- 강원 -->
							<a
								href="${pageContext.request.contextPath}/Cultural?region=강원&sort=${sort}&pageNo=1"
								class="px-3 py-1.5 rounded-full
								       ${region == '강원'
								       ? 'bg-primary text-on-primary font-bold'
								       : 'bg-surface-container-low text-on-surface-variant'}">
								강원 </a>

							<!-- 충청·세종·대전 -->
							<a
								href="${pageContext.request.contextPath}/Cultural?region=충청&sort=${sort}&pageNo=1"
								class="px-3 py-1.5 rounded-full
						       ${region == '충청' ? 'bg-primary text-on-primary font-bold' : 'bg-surface-container-low text-on-surface-variant'}">
								충청·세종·대전 </a>

							<!-- 전라·광주 -->
							<a
								href="${pageContext.request.contextPath}/Cultural?region=전라&sort=${sort}&pageNo=1"
								class="px-3 py-1.5 rounded-full
						       ${region == '전라' ? 'bg-primary text-on-primary font-bold' : 'bg-surface-container-low text-on-surface-variant'}">
								전라·광주 </a>

							<!-- 경상·대구·부산·울산 -->
							<a
								href="${pageContext.request.contextPath}/Cultural?region=경상&sort=${sort}&pageNo=1"
								class="px-3 py-1.5 rounded-full
						       ${region == '경상' ? 'bg-primary text-on-primary font-bold' : 'bg-surface-container-low text-on-surface-variant'}">
								경상·대구·부산·울산 </a>

							<!-- 제주 -->
							<a
								href="${pageContext.request.contextPath}/Cultural?region=제주&sort=${sort}&pageNo=1"
								class="px-3 py-1.5 rounded-full
								       ${region == '제주'
								       ? 'bg-primary text-on-primary font-bold'
								       : 'bg-surface-container-low text-on-surface-variant'}">
								제주 </a>

						</div>
					</div>
				</div>
			</section>

			<!-- 지역/구분 클릭시 표시 코드 -->
			<script> 
				    const regionButtons = document.querySelectorAll('#regionFilters button');
				    regionButtons.forEach(btn => {
				      btn.addEventListener('click', () => {
				        regionButtons.forEach(b => {
				          b.className = 'filter-chip px-3.5 py-1.5 rounded-full font-label-md text-label-md transition-all bg-surface-container-low text-on-surface-variant hover:bg-surface-container hover:text-on-surface';
				        });
				        btn.className = 'filter-chip-active px-3.5 py-1.5 rounded-full font-label-md text-label-md transition-all bg-primary text-on-primary font-bold shadow-sm';
				      });
				    });
				
				    const categoryButtons = document.querySelectorAll('#categoryFilters button');
				    categoryButtons.forEach(btn => {
				      btn.addEventListener('click', () => {
				        categoryButtons.forEach(b => {
				          b.className = 'cat-chip px-3.5 py-1.5 rounded-full font-label-md text-label-md transition-all bg-surface-container-low text-on-surface-variant hover:bg-surface-container hover:text-on-surface flex items-center gap-1';
				        });
				        btn.className = 'cat-chip-active px-3.5 py-1.5 rounded-full font-label-md text-label-md transition-all bg-on-surface text-surface-bright font-bold shadow-sm flex items-center gap-1';
				      });
				    });
			   </script>

			<section
				class="w-full max-w-[1320px] mx-auto px-margin-sm lg:px-margin pt-6">



				<div
					class="flex flex-col sm:flex-row sm:items-center justify-between gap-3 pb-1">



					<div class="flex items-center gap-2 text-on-surface-variant">



						<span>문화유산 목록</span> <strong class="text-lg text-primary">${heritageList.size()}</strong>
						<span class="text-outline text-sm">(총 ${totalCount}건)</span>



					</div>







					<form action="${pageContext.request.contextPath}/Cultural"
						method="get" class="relative">

						<!-- 현재 검색어 유지 -->
						<c:if test="${not empty keyword}">
							<input type="hidden" name="keyword"
								value="<c:out value='${keyword}'/>">
						</c:if>

						<!-- 현재 지역 유지 -->
						<c:if test="${not empty region}">
							<input type="hidden" name="region"
								value="<c:out value='${region}'/>">
						</c:if>

						<!-- 정렬을 바꾸면 항상 1페이지 -->
						<input type="hidden" name="pageNo" value="1"> <select
							id="sortSelect" name="sort" onchange="this.form.submit()"
							class="appearance-none bg-surface-container-lowest
				                   border border-surface-container
				                   pl-3.5 pr-10 py-2
				                   rounded-xl text-sm
				                   focus:outline-none cursor-pointer">

							<option value="default" ${sort == 'default' ? 'selected' : ''}>
								기본순</option>

							<option value="name" ${sort == 'name' ? 'selected' : ''}>
								가나다순</option>

							<option value="liked" ${sort == 'liked' ? 'selected' : ''}>
								좋아요순</option>

						</select> <span
							class="material-symbols-outlined
				                  absolute right-3 top-1/2
				                  -translate-y-1/2
				                  pointer-events-none
				                  text-outline text-[18px]">
							expand_more </span>

					</form>

				</div>

			</section>



			<!-- API CARDS -->



			<section
				class="w-full max-w-[1320px] mx-auto px-margin-sm lg:px-margin pt-3">



				<div class="grid grid-cols-4 gap-gutter" id="heritageCardsContainer">







					<c:choose>



						<c:when test="${not empty heritageList}">



							<c:forEach var="heritage" items="${heritageList}">



								<article
									class="heritage-card group bg-surface-container-lowest rounded-2xl overflow-hidden shadow-sm hover:shadow-md border border-surface-container transition-all duration-300 flex flex-col hover:-translate-y-1"
									data-title="<c:out value='${heritage.title}'/>"
									data-address="<c:out value='${heritage.addr1}'/>">







									<div
										class="relative w-full aspect-[4/3] overflow-hidden bg-surface-container">



										<%-- 이미지 경로 구분: TourAPI / 프로젝트 내부 / 이미지 없음 --%>
										<c:choose>

											<%-- 이미지가 있는 경우 --%>
											<c:when test="${not empty heritage.firstImage}">

												<c:choose>

													<%-- TourAPI 외부 이미지 --%>
													<c:when test="${heritage.firstImage.startsWith('http')}">
														<c:set var="cardImageUrl" value="${heritage.firstImage}" />
													</c:when>

													<%-- DB에 저장한 프로젝트 내부 이미지 --%>
													<c:otherwise>
														<c:set var="cardImageUrl"
															value="${pageContext.request.contextPath}${heritage.firstImage}" />
													</c:otherwise>

												</c:choose>

											</c:when>

											<%-- 이미지 정보가 없는 경우 --%>
											<c:otherwise>
												<c:set var="cardImageUrl"
													value="${pageContext.request.contextPath}/resources/images/cultural/heritage_image_coming_soon.png" />
											</c:otherwise>

										</c:choose>

										<img src="<c:out value='${cardImageUrl}'/>"
											alt="<c:out value='${heritage.title}'/>"
											class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
											onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/resources/images/cultural/heritage_image_coming_soon.png';">







										<div class="absolute top-2.5 left-2.5">



											<span
												class="px-2 py-0.5 rounded-full text-[11px] bg-white/90 text-primary font-bold shadow-xs">



												문화유산 </span>



										</div>


										<c:set var="isLiked" value="false" />

										<c:forEach var="likedId" items="${likedHeritageIds}">

											<c:if test="${likedId == heritage.contentId}">
												<c:set var="isLiked" value="true" />
											</c:if>

										</c:forEach>


										<button type="button"
											class="heritage-like-button
										           ${isLiked ? 'liked' : ''}
										           absolute top-2.5 right-2.5
										           w-8 h-8 rounded-full
										           bg-white/85 backdrop-blur-md
										           flex items-center justify-center
										           text-on-surface-variant
										           hover:text-secondary
										           transition-colors"
											data-heritage-id="<c:out value='${heritage.contentId}'/>"
											onclick="toggleHeritageLike(
											        '<c:out value="${heritage.contentId}"/>',this)"
											aria-label="${isLiked ? '좋아요 취소' : '좋아요'}"
											aria-pressed="${isLiked}">

											<svg class="heritage-like-svg" width="20" height="20"
												viewBox="0 0 24 24" preserveAspectRatio="xMidYMid meet"
												aria-hidden="true">

        <path class="heritage-like-path"
													d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5C2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3C19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z">
        </path>

    </svg>

										</button>



									</div>


									<div class="p-4 flex-1 flex flex-col justify-between space-y-3">



										<div class="space-y-1.5">



											<div class="flex items-center gap-1 text-outline text-xs">



												<span
													class="material-symbols-outlined text-[14px] text-primary">location_on</span>



												<span class="truncate"> <c:choose>



														<c:when test="${not empty heritage.addr1}">



															<c:out value="${heritage.addr1}" />



														</c:when>



														<c:otherwise>주소 정보 없음</c:otherwise>



													</c:choose>



												</span>



											</div>







											<h3
												class="heritage-title text-lg font-bold group-hover:text-primary transition-colors">



												<c:out value="${heritage.title}" />



											</h3>

											<%-- ==========================================
												     문화유산 이용정보
												     휴무일 / 주차 / 문의
												========================================== --%>

											<div class="mt-3 grid grid-cols-3">

												<%-- ======================================
												         휴무일
												    ======================================= --%>
												<div
													class="flex flex-col items-center justify-center
              											   px-2 py-3 text-center">

													<%-- 아이콘 --%>
													<span
														class="material-symbols-outlined
                  											   text-[22px] text-primary leading-none mb-2">
														event_busy </span>

													<%-- 제목 --%>
													<span class="text-[11px] font-bold text-on-surface mb-1">
														휴무일 </span>

													<%-- 내용 --%>
													<span
														class="text-[11px] text-on-surface-variant
                   												w-full truncate">

														<c:choose>

															<c:when test="${not empty heritage.restDate}">
																<c:out value="${heritage.restDate}" />
															</c:when>

															<c:otherwise>
											                    정보 없음
											                </c:otherwise>

														</c:choose>

													</span>

												</div>


												<%-- ======================================
												         주차
												    ======================================= --%>
												<div
													class="flex flex-col items-center justify-center
											               px-2 py-3 text-center
											               border-l border-surface-container">

													<%-- 아이콘 --%>
													<span
														class="material-symbols-outlined
                   											   text-[22px] text-primary leading-none mb-2">
														local_parking </span>

													<%-- 제목 --%>
													<span class="text-[11px] font-bold text-on-surface mb-1">
														주차 </span>

													<%-- 내용 --%>
													<span
														class="text-[11px] text-on-surface-variant
                  											   w-full truncate">

														<c:choose>

															<c:when test="${not empty heritage.parking}">
																<c:out value="${heritage.parking}" />
															</c:when>

															<c:otherwise>
											                    정보 없음
											                </c:otherwise>

														</c:choose>

													</span>

												</div>


												<%-- ======================================
												         문의
												    ======================================= --%>
												<div
													class="flex flex-col items-center justify-center
											               px-2 py-3 text-center
											               border-l border-surface-container">

													<%-- 아이콘 --%>
													<span
														class="material-symbols-outlined
                   											   text-[22px] text-primary leading-none mb-2">
														call </span>

													<%-- 제목 --%>
													<span class="text-[11px] font-bold text-on-surface mb-1">
														문의 </span>

													<%-- 전화번호 --%>
													<span
														class="text-[11px] text-on-surface-variant
                  											   w-full whitespace-nowrap">

														<c:choose>

															<c:when test="${not empty heritage.infoCenter}">
																<c:out value="${heritage.infoCenter}" />
															</c:when>

															<c:otherwise>
											                    정보 없음
											                </c:otherwise>

														</c:choose>

													</span>

												</div>

											</div>







											<div
												class="pt-2 flex items-center justify-between gap-2 border-t border-surface-container">

												<%-- 운영시간 : 왼쪽 --%>
												<div
													class="min-w-0 flex items-center gap-1 text-xs text-on-surface-variant">

													<span
														class="material-symbols-outlined text-[17px] text-primary shrink-0">
														schedule </span>

													<c:choose>

														<c:when test="${not empty heritage.useTime}">
															<span class="whitespace-nowrap"> <c:out
																	value="${heritage.useTime}" />
															</span>
														</c:when>

														<c:otherwise>
															<span class="text-outline whitespace-nowrap"> 운영시간
																정보 없음 </span>
														</c:otherwise>

													</c:choose>

												</div>


												<%-- 자세히 보기 : 오른쪽 --%>
												<button type="button"
													class="heritage-detail-btn shrink-0 inline-flex items-center gap-0.5 text-sm font-bold text-primary hover:text-primary-container"
													data-content-id="<c:out value='${heritage.contentId}'/>"
													data-title="<c:out value='${heritage.title}'/>"
													data-address="<c:out value='${heritage.addr1}'/>"
													data-image="<c:out value='${heritage.firstImage}'/>"
													data-map-x="<c:out value='${heritage.mapX}'/>"
													data-map-y="<c:out value='${heritage.mapY}'/>">

													자세히 보기 <span class="material-symbols-outlined text-[14px]">
														arrow_forward </span>

												</button>

											</div>


										</div>
								</article>



							</c:forEach>



						</c:when>







						<c:otherwise>



							<div
								class="col-span-full py-20 text-center text-on-surface-variant">



								<span class="material-symbols-outlined text-[52px] text-outline">search_off</span>



								<p class="mt-3 font-bold">검색 결과가 없습니다.</p>



								<p class="mt-1 text-sm">다른 문화유산 이름이나 키워드로 검색해보세요.</p>



							</div>



						</c:otherwise>



					</c:choose>
				</div>



			</section>







			<!-- PAGINATION -->

			<c:if test="${totalPages > 1}">

				<section
					class="w-full max-w-[1320px] mx-auto px-margin-sm lg:px-margin py-10">

					<div class="flex items-center justify-center gap-1.5">


						<!-- ========================================== -->
						<!-- 맨 처음 페이지 -->
						<!-- ========================================== -->
						<c:if test="${pageNo > 1}">

							<a
								href="${pageContext.request.contextPath}/Cultural?keyword=${encodedKeyword}&region=${region}&sort=${sort}&pageNo=1"
								class="w-10 h-10 rounded-full flex items-center justify-center
                           						  text-outline hover:bg-surface-container"
								aria-label="첫 페이지"> <span class="material-symbols-outlined">
									keyboard_double_arrow_left </span>

							</a>

						</c:if>


						<!-- ========================================== -->
						<!-- 이전 페이지 -->
						<!-- ========================================== -->
						<c:if test="${pageNo > 1}">

							<a
								href="${pageContext.request.contextPath}/Cultural?keyword=${encodedKeyword}&region=${region}&sort=${sort}&pageNo=${pageNo - 1}"
								class="w-10 h-10 rounded-full flex items-center justify-center
                           						  text-outline hover:bg-surface-container"
								aria-label="이전 페이지"> <span class="material-symbols-outlined">
									chevron_left </span>

							</a>

						</c:if>


						<!-- ========================================== -->
						<!-- 페이지 번호 -->
						<!-- ========================================== -->
						<c:forEach begin="${startPage}" end="${endPage}" var="p">

							<a
								href="${pageContext.request.contextPath}/Cultural?keyword=${encodedKeyword}&region=${region}&sort=${sort}&pageNo=${p}"
								class="w-10 h-10 rounded-full flex items-center justify-center font-bold
						                           ${p == pageNo
						                           ? 'bg-primary text-on-primary'
						                           : 'text-on-surface-variant hover:bg-surface-container'}">
								${p} </a>

						</c:forEach>


						<!-- ========================================== -->
						<!-- 다음 페이지 -->
						<!-- ========================================== -->
						<c:if test="${pageNo < totalPages}">

							<a
								href="${pageContext.request.contextPath}/Cultural?keyword=${encodedKeyword}&region=${region}&sort=${sort}&pageNo=${pageNo + 1}"
								class="w-10 h-10 rounded-full flex items-center justify-center
                          						   text-outline hover:bg-surface-container"
								aria-label="다음 페이지"> <span class="material-symbols-outlined">
									chevron_right </span>

							</a>

						</c:if>


						<!-- ========================================== -->
						<!-- 맨 마지막 페이지 -->
						<!-- ========================================== -->
						<c:if test="${pageNo < totalPages}">

							<a
								href="${pageContext.request.contextPath}/Cultural?keyword=${encodedKeyword}&region=${region}&sort=${sort}&pageNo=${totalPages}"
								class="w-10 h-10 rounded-full flex items-center justify-center
                           						  text-outline hover:bg-surface-container"
								aria-label="마지막 페이지"> <span
								class="material-symbols-outlined">
									keyboard_double_arrow_right </span>

							</a>

						</c:if>


					</div>

				</section>

			</c:if>







		</div>
	</main>







	<!-- DETAIL MODAL -->



	<div id="heritageModal"
		class="fixed inset-0 z-50 hidden items-center justify-center p-4 bg-on-surface/50 backdrop-blur-sm">



		<div
			class="bg-surface-container-lowest w-full max-w-3xl
		           max-h-[90vh]
		           rounded-3xl shadow-2xl overflow-hidden
		           flex flex-col">







			<div
				class="flex items-center justify-between px-6 py-4 border-b border-surface-container"shrink-0">



				<div>



					<h2 id="modalTitle" class="text-xl font-bold mt-1">문화유산</h2>



				</div>



				<button type="button" id="heritageModalCloseBtn"
					class="w-9 h-9 rounded-full flex items-center justify-center hover:bg-surface-container"
					aria-label="상세보기 닫기">

					<span class="material-symbols-outlined">close</span>

				</button>



			</div>







			<div class="p-6 space-y-5 overflow-y-auto min-h-0">



				<div
					class="rounded-2xl overflow-hidden bg-surface-container aspect-[16/9]">



					<img id="modalImage" alt="문화유산 이미지"
						class="w-full h-full object-cover hidden">



					<div id="modalImageEmpty"
						class="w-full h-full flex items-center justify-center">



						<span class="material-symbols-outlined text-outline text-[60px]">account_balance</span>



					</div>



				</div>







				<div>



					<div class="flex items-start gap-2 text-on-surface-variant">



						<span class="material-symbols-outlined text-primary">location_on</span>



						<span id="modalAddress">주소 정보 없음</span>



					</div>

					<!-- 문화유산 상세 설명 -->
					<div class="mt-5">

						<div class="flex items-center gap-2 mb-2">

							<span class="material-symbols-outlined text-primary text-[20px]">
								history_edu </span>

							<h3 class="font-bold text-on-surface">문화유산 소개</h3>

						</div>

						<div id="modalOverview"
							class="text-sm text-on-surface-variant leading-7
					                bg-surface-container-low rounded-2xl p-4">

							문화유산 설명을 불러오는 중입니다...</div>

					</div>


				</div>


			</div>


		</div>



	</div>







	<jsp:include page="/WEB-INF/bottom.jsp" />



	<jsp:include page="/WEB-INF/liveChat/chat.jsp" />







	<script>



	document.addEventListener('DOMContentLoaded', function () {

	    // ==========================================
	    // 검색창
	    // ==========================================

	    const searchInput =
	        document.getElementById('heritageSearchInput');

	    const clearBtn =
	        document.getElementById('clearSearchBtn');


	    function updateClearButton() {

	        if (!searchInput || !clearBtn) {
	            return;
	        }

	        clearBtn.classList.toggle(
	            'hidden',
	            searchInput.value.length === 0
	        );
	    }


	    updateClearButton();


	    searchInput?.addEventListener(
	        'input',
	        updateClearButton
	    );


	    clearBtn?.addEventListener(
	        'click',
	        function () {

	            searchInput.value = '';

	            updateClearButton();

	            searchInput.focus();
	        }
	    );



 /* 지역 필터 */



document.querySelectorAll('.region-btn').forEach(function (btn) {



    btn.addEventListener('click', function () {



        document.querySelectorAll('.region-btn').forEach(function (b) {



            b.classList.remove('filter-active');



        });



        btn.classList.add('filter-active');







        const pattern = btn.dataset.region;



        const cards = document.querySelectorAll('.heritage-card');







        cards.forEach(function (card) {



            const address = card.dataset.address || '';



            if (!pattern) {



                card.style.display = '';



                return;



            }







            const regions = pattern.split('|');



            const matched = regions.some(function (region) {



                return address.includes(region);



            });







            card.style.display = matched ? '' : 'none';



        });



    });



});



/* 그리드 / 리스트 */



const gridBtn = document.getElementById('viewGridBtn');



const listBtn = document.getElementById('viewListBtn');



const cardsContainer = document.getElementById('heritageCardsContainer');







gridBtn?.addEventListener('click', function () {



    cardsContainer.className =



        'grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-gutter';



});







listBtn?.addEventListener('click', function () {



    cardsContainer.className = 'grid grid-cols-1 gap-gutter';



});



/* ==========================================
자세히 보기 버튼 클릭
========================================== */

const detailButtons =
 document.querySelectorAll('.heritage-detail-btn');

console.log(
 '[문화유산] 자세히 보기 버튼 개수 :',
 detailButtons.length
);

detailButtons.forEach(function(button) {

 button.addEventListener('click', function() {

     console.log(
         '[문화유산] 자세히 보기 클릭 :',
         button.dataset.contentId
     );

     openDetailModal(
         button.dataset.contentId,
         button.dataset.title,
         button.dataset.address,
         button.dataset.image,
         button.dataset.mapX,
         button.dataset.mapY
     );
 });

});

function openDetailModal(
 contentId,
 title,
 address,
 image,
 mapX,
 mapY
) {

    console.log("================================");
    console.log("[상세보기 클릭]");
    console.log("contentId :", contentId);
    console.log("title :", title);
    console.log("================================");
	
	
 const modal =
     document.getElementById('heritageModal');

 const overview =
     document.getElementById('modalOverview');
 
 const modalImage =
	    document.getElementById('modalImage');

	const emptyImage =
	    document.getElementById('modalImageEmpty');
 
 
 // ==========================================
 // 기본 정보 출력
 // ==========================================

 document.getElementById('modalTitle').textContent =
     title || '문화유산';

 document.getElementById('modalAddress').textContent =
     address || '주소 정보 없음';

 
 // ==========================================
 // 이미지
 // ==========================================

 const placeholderImage =
    '${pageContext.request.contextPath}/resources/images/cultural/heritage_image_coming_soon.png';

const contextPath = '${pageContext.request.contextPath}';

let imageUrl = image ? image.trim() : '';

if (!imageUrl) {
    imageUrl = placeholderImage;
} else if (imageUrl.startsWith('/resources/')) {
    imageUrl = contextPath + imageUrl;
}

// 이미지 로딩에 실패해도 대체 이미지 출력
modalImage.onerror = function() {
    this.onerror = null;
    this.src = placeholderImage;
};

modalImage.src = imageUrl;
modalImage.classList.remove('hidden');
emptyImage.classList.add('hidden');


 // ==========================================
 // 설명 로딩 문구
 // ==========================================

 overview.textContent =
     '문화유산 설명을 불러오는 중입니다...';


 // ==========================================
 // 모달 열기
 // ==========================================

 modal.classList.remove('hidden');

 modal.classList.add('flex');

 document.body.style.overflow = 'hidden';


 console.log(
     '[상세정보 요청] contentId :',
     contentId
 );


 // ==========================================
 // CulturalHeritageController에 상세정보 요청
 // ==========================================

 fetch(
     '${pageContext.request.contextPath}/Cultural'
     + '?action=detail'
     + '&contentId='
     + encodeURIComponent(contentId)
 )

 .then(async function(response) {

     console.log(
         '[상세정보 응답] status :',
         response.status
     );


     if (!response.ok) {

         const errorText =
             await response.text();

         throw new Error(
             '상세정보 조회 실패 : '
             + response.status
             + ' / '
             + errorText
         );
     }


     return response.json();
 })

 .then(function(data) {

     console.log(
         '[상세정보 결과]',
         data
     );


     if (data.success === true &&
         data.overview) {

         overview.textContent =
             data.overview;

     } else {

         overview.textContent =
             '등록된 문화유산 설명이 없습니다.';
     }

 })

 .catch(function(error) {

     console.error(
         '[문화유산 상세정보 오류]',
         error
     );


     overview.textContent =
         '문화유산 설명을 불러오지 못했습니다.';
 });
 
}
 
/* ==========================================
   문화유산 상세 모달(close)
========================================== */

function closeDetailModal() {

    const modal = document.getElementById('heritageModal');

    if (!modal) {
        console.log('heritageModal을 찾지 못했습니다.');
        return;
    }

    console.log('상세 모달 닫기');

    modal.classList.add('hidden');
    modal.classList.remove('flex');

    document.body.style.overflow = '';
}

/* ==========================================
X 버튼 클릭 → 상세 모달 닫기
========================================== */

const heritageModalCloseBtn =
 		document.getElementById('heritageModalCloseBtn');

heritageModalCloseBtn?.addEventListener(
 'click',
 function () {

     closeDetailModal();

 }
);

}); // DOMContentLoaded 끝

</script>

	<%-- ==========================================
    문화유산 좋아요
========================================== --%>

	<script>

	// 찜 저장 / 취소 알림
	let commonFavoriteToastTimer = null;

	function showFavoriteToast(liked) {

	    const toast = document.getElementById('common-favorite-toast');

	    if (!toast) return;

	    const message = toast.querySelector('.favorite-toast-text');

	    message.textContent = liked
	        ? '찜 목록에 저장되었습니다.'
	        : '찜 목록에서 삭제되었습니다.';

	    toast.classList.toggle('removed', !liked);
	    toast.classList.add('show');

	    clearTimeout(commonFavoriteToastTimer);

	    commonFavoriteToastTimer = setTimeout(function() {
	        toast.classList.remove('show');
	    }, 2800);
	}
	
function setHeritageHeartState(button, liked) {

   if (liked) {
       button.classList.add('liked');
       button.setAttribute('aria-label', '좋아요 취소');
       button.setAttribute('aria-pressed', 'true');
   } else {
       button.classList.remove('liked');
       button.setAttribute('aria-label', '좋아요');
       button.setAttribute('aria-pressed', 'false');
   }
}


function toggleHeritageLike(heritageId, button) {

   console.log("[좋아요 클릭] contentId :", heritageId);

   const wasLiked = button.classList.contains('liked');

   // 일단 화면에서 하트 변경
   setHeritageHeartState(button, !wasLiked);

   fetch('${pageContext.request.contextPath}/ExtraLikes', {

       method: 'POST',

       headers: {
           'Content-Type':
               'application/x-www-form-urlencoded;charset=UTF-8'
       },

       body: new URLSearchParams({type:'heritage', id:String(heritageId), liked:String(!wasLiked)})

   })
   .then(async function(response) {

       console.log(
           "[좋아요 응답] status :",
           response.status
       );

       if (response.status === 401) {

    	    setHeritageHeartState(button, wasLiked);

    	    openHeritageLoginModal();

    	    return null;
    	}

       if (!response.ok) {

           const errorText =
               await response.text();

           throw new Error(
               '좋아요 처리 실패 : ' +
               response.status +
               ' / ' +
               errorText
           );
       }

       return response.json();
   })

   .then(function(data) {

       if (data === null) {
           return;
       }

       console.log(
    		    "[좋아요 결과] liked :",
    		    data.liked
    		);

    		// 서버 응답이 정상일 때 하트 상태 변경
    		if (data.success === false || typeof data.liked !== 'boolean') {
    		    throw new Error('좋아요 처리 결과가 올바르지 않습니다.');
    		}

    		setHeritageHeartState(
    		    button,
    		    data.liked === true
    		);

    		// DB 저장 또는 삭제 성공 후 알림 표시
    		showFavoriteToast(data.liked === true);
   })

   .catch(function(error) {

       console.error(
           '문화유산 좋아요 오류:',
           error
       );

       // 실패하면 원래 하트 상태로 복구
       setHeritageHeartState(
           button,
           wasLiked
       );

       alert('좋아요 저장에 실패했습니다.');
   });
}

//비로그인 찜하기 안내 팝업 열기
function openHeritageLoginModal() {
    const dialog = document.getElementById('like-login-dialog');

    if (dialog && !dialog.open) {
        dialog.showModal();
    }
}

// 팝업 닫기 버튼
document.addEventListener('DOMContentLoaded', function() {
    const dialog = document.getElementById('like-login-dialog');

    if (!dialog) return;

    dialog.querySelectorAll('[data-like-login-close]').forEach(function(button) {
        button.addEventListener('click', function() {
            dialog.close();
        });
    });
});

</script>

	<!-- 문화유산 찜 저장/취소 알림 -->
	<div id="common-favorite-toast" role="status" aria-live="polite"
		aria-atomic="true">

		<span class="favorite-toast-heart">♥</span> <span
			class="favorite-toast-text"></span>

	</div>


	<%-- =========================================================
     주제: 비로그인 찜하기 안내 팝업
     설명: 하트를 클릭하면 로그인 안내 표시 (기존 찜 API 기능 변경 없음)
========================================================= --%>

	<!-- 문화유산 찜 저장/취소 알림 -->
	<div id="common-favorite-toast" role="status" aria-live="polite"
		aria-atomic="true">
		<span class="favorite-toast-heart">♥</span> <span
			class="favorite-toast-text"></span>
	</div>


	<%-- 비로그인 찜하기 안내 팝업 --%>

	<style>
#like-login-dialog {
	box-sizing: border-box;
	border: 1px solid rgba(255, 255, 255, .11);
	padding: 0;
	margin: auto;
	width: 420px;
	max-width: calc(100vw - 32px);
	max-height: calc(100dvh - 32px);
	border-radius: 24px;
	overflow: auto;
	background: #202329;
	color: #f7f9fb;
	box-shadow: 0 28px 90px rgba(0, 0, 0, .5);
	font-family: inherit;
}

#like-login-dialog::backdrop {
	background: rgba(6, 9, 14, .76);
	backdrop-filter: blur(6px);
	-webkit-backdrop-filter: blur(6px);
}

#like-login-dialog[open] {
	animation: likeModalIn .2s ease-out;
}

#like-login-dialog .like-modal-body {
	position: relative;
	padding: 34px 30px 28px;
	text-align: center;
	background: radial-gradient(circle at 50% 0%, rgba(0, 163, 169, .16),
		transparent 58%), #202329;
}

#like-login-dialog .like-modal-close {
	position: absolute;
	right: 15px;
	top: 15px;
	display: flex;
	align-items: center;
	justify-content: center;
	width: 34px;
	height: 34px;
	border: 0;
	border-radius: 11px;
	background: rgba(255, 255, 255, .065);
	color: #c7ccd3;
	cursor: pointer;
	transition: background .2s, color .2s;
}

#like-login-dialog .like-modal-close:hover {
	background: rgba(255, 255, 255, .15);
	color: white;
}

#like-login-dialog .like-modal-icon {
	position: relative;
	display: flex;
	align-items: center;
	justify-content: center;
	margin: 9px auto 20px;
	width: 75px;
	height: 75px;
	border-radius: 24px;
	background: linear-gradient(140deg, rgba(255, 111, 97, .2),
		rgba(255, 111, 97, .065));
	border: 1px solid rgba(255, 132, 115, .21);
	color: #ff7567;
}

#like-login-dialog .like-modal-icon .material-symbols-outlined {
	font-size: 36px;
	font-variation-settings: 'FILL' 1;
}

#like-login-dialog .like-modal-lock {
	position: absolute;
	right: -6px;
	bottom: -4px;
	display: flex;
	align-items: center;
	justify-content: center;
	width: 28px;
	height: 28px;
	background: #007e88;
	color: #fff;
	border: 3px solid #202329;
	border-radius: 10px;
}

#like-login-dialog .like-modal-lock .material-symbols-outlined {
	font-size: 15px;
	font-variation-settings: 'FILL' 0;
}

#like-login-dialog .like-modal-kicker {
	font-size: 11px;
	letter-spacing: .12em;
	font-weight: 800;
	color: #44ccd3;
	margin: 0 0 8px;
}

#like-login-dialog .like-modal-title {
	font-size: 23px;
	font-weight: 800;
	line-height: 1.45;
	letter-spacing: -.035em;
	color: #fff;
	margin: 0;
	word-break: keep-all;
}

#like-login-dialog .like-modal-desc {
	font-size: 13px;
	line-height: 1.75;
	color: #b4bdc7;
	margin: 12px 0 25px;
	word-break: keep-all;
}

#like-login-dialog .like-modal-login {
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 9px;
	min-height: 49px;
	width: 100%;
	box-sizing: border-box;
	border-radius: 13px;
	background: #007e88;
	color: white;
	text-decoration: none;
	font-size: 14px;
	font-weight: 800;
	transition: background .18s, transform .18s;
}

#like-login-dialog .like-modal-login:hover {
	background: #0096a1;
	transform: translateY(-1px);
}

#like-login-dialog .like-modal-later {
	display: block;
	margin: 8px auto 0;
	width: 100%;
	min-height: 42px;
	border: 0;
	border-radius: 11px;
	background: transparent;
	color: #aeb6c0;
	font-size: 13px;
	font-weight: 600;
	cursor: pointer;
}

#like-login-dialog .like-modal-later:hover {
	background: rgba(255, 255, 255, .045);
	color: #fff;
}

#like-login-dialog .like-modal-note {
	font-size: 11px;
	line-height: 1.5;
	color: #88929e;
	margin: 11px 0 0;
}

#like-login-dialog a:focus-visible, #like-login-dialog button:focus-visible
	{
	outline: 2px solid #61dfe4;
	outline-offset: 3px;
}

@
keyframes likeModalIn {
	from {opacity: 0;
	transform: translateY(10px) scale(.985);
}

to {
	opacity: 1;
	transform: translateY(0) scale(1);
}

}
@media ( max-width :420px) {
	#like-login-dialog .like-modal-body {
		padding: 30px 22px 24px;
	}
	#like-login-dialog .like-modal-title {
		font-size: 21px;
	}
}

@media ( prefers-reduced-motion :reduce) {
	#like-login-dialog[open] {
		animation: none;
	}
	#like-login-dialog .like-modal-login {
		transition: none;
	}
}
</style>

	<dialog id="like-login-dialog" 
			aria-labelledby="like-login-title"
			aria-describedby="like-login-desc">

	<div class="like-modal-body">
		<button type="button" class="like-modal-close" data-like-login-close
			aria-label="팝업 닫기">
			<span class="material-symbols-outlined">close</span>
		</button>
		<div class="like-modal-icon" aria-hidden="true">
			<span class="material-symbols-outlined">favorite</span><span
				class="like-modal-lock"><span
				class="material-symbols-outlined">lock</span></span>
		</div>
		<p class="like-modal-kicker">MY TRAVEL FAVORITES</p>
		<h2 class="like-modal-title" id="like-login-title">
			찜하기는 로그인 후<br>이용할 수 있어요.
		</h2>
		<p class="like-modal-desc" id="like-login-desc">
			마음에 드는 믄화유산를 저장하고,<br>마이페이지에서 한 번에 확인해 보세요.
		</p>
		<a class="like-modal-login"
			href="${pageContext.request.contextPath}/Login" autofocus><span
			class="material-symbols-outlined" style="font-size: 19px;">login</span>
			로그인하기 <span class="material-symbols-outlined"
			style="font-size: 18px;">arrow_forward</span></a>
		<button class="like-modal-later" type="button" data-like-login-close>다음에
			할게요</button>
		<p class="like-modal-note">로그인 전에는 찜한 장소가 저장되지 않습니다.</p>
	</div>

	</dialog>

</body>
</html>