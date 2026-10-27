<%-- =========================================================
     주제: 파일 역할
     설명: review 영역의 review 화면을 구성하고 서버에서 전달받은 데이터를 표시
========================================================= --%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!-- 제발되어라 -->
<head>
<meta charset="utf-8" />
<meta content="width=device-width, initial-scale=1.0" name="viewport" />
<title>놀러가자 - 여행 리뷰 게시판</title>
<!-- Google Fonts: Plus Jakarta Sans, Noto Sans KR & Handwriting Nanum Pen Script -->
<link href="https://fonts.googleapis.com" rel="preconnect" />
<link crossorigin="" href="https://fonts.gstatic.com" rel="preconnect" />
<link
	href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&amp;family=Noto+Sans+KR:wght@300;400;500;600;700;800&amp;family=Nanum+Pen+Script&amp;display=swap"
	rel="stylesheet" />
<!-- Google Material Symbols Outlined -->
<link
	href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap"
	rel="stylesheet" />
<!-- Font Awesome CDN for Icons -->
<link
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css"
	rel="stylesheet" />
<!-- Tailwind CSS v3 with Plugins -->
<script src="https://cdn.tailwindcss.com"></script>
<!-- Tailwind Design Tokens Configuration matching Noleogaja Design System -->
<script id="tailwind-config">
  tailwind.config = {
    darkMode: "class",
    theme: {
      extend: {
        colors: {
          surface: "#faf8ff",
          "on-primary-fixed-variant": "#004e5a",
          "surface-variant": "#dae2fd",
          "surface-container-lowest": "#ffffff",
          "surface-tint": "#006877",
          background: "#faf8ff",
          "on-tertiary-fixed-variant": "#005236",
          "tertiary-fixed": "#6ffbbe",
          "primary-fixed": "#a4eeff",
          "on-secondary": "#ffffff",
          "surface-container-low": "#f2f3ff",
          tertiary: "#006947",
          "inverse-on-surface": "#eef0ff",
          "surface-container-highest": "#dae2fd",
          error: "#ba1a1a",
          "surface-container": "#eaedff",
          "on-background": "#131b2e",
          "on-surface-variant": "#3e494b",
          "on-secondary-fixed": "#400100",
          "error-container": "#ffdad6",
          "tertiary-fixed-dim": "#4edea3",
          "surface-dim": "#d2d9f4",
          outline: "#6e797c",
          "secondary-container": "#fd6e58",
          "primary-container": "#008092",
          "surface-bright": "#faf8ff",
          "primary-fixed-dim": "#73d4e9",
          "inverse-primary": "#73d4e9",
          "on-secondary-fixed-variant": "#8a1a0e",
          "on-tertiary-fixed": "#002113",
          "tertiary-container": "#00855b",
          "outline-variant": "#bdc8cb",
          secondary: "#ac3323",
          "inverse-surface": "#283044",
          "on-surface": "#131b2e",
          "on-error": "#ffffff",
          "secondary-fixed": "#ffdad4",
          "on-primary": "#ffffff",
          "on-secondary-container": "#6c0300",
          "on-error-container": "#93000a",
          "on-primary-fixed": "#001f25",
          "on-tertiary-container": "#f5fff6",
          "surface-container-high": "#e2e7ff",
          "on-tertiary": "#ffffff",
          primary: "#006574",
          "secondary-fixed-dim": "#ffb4a7",
          "on-primary-container": "#f8fdff",
          brand: {
            50: '#f0f9fa',
            100: '#d7f0f3',
            200: '#b2e2e8',
            500: '#1e90a3',
            600: '#167b8d',
            700: '#006574',
            800: '#0b4d59',
            900: '#073942'
          }
        },
        borderRadius: {
          DEFAULT: "0.25rem",
          lg: "0.5rem",
          xl: "0.75rem",
          "2xl": "1rem",
          full: "9999px",
          custom: "8px",
          card: "14px"
        },
        spacing: {
            "gutter": "1.5rem",
            "space-sm": "0.5rem",
            "space-xl": "2.5rem",
            "margin": "3rem",
            "space-xs": "0.25rem",
            "space-lg": "1.5rem",
            "margin-sm": "1.25rem",
            "space-md": "1rem",
            "gutter-sm": "1rem"
        },

        fontFamily: {
            sans: ['"Noto Sans KR"', '"Plus Jakarta Sans"', 'sans-serif'],
            handwriting: ['"Nanum Pen Script"', 'cursive'],

            "label-sm": ["Plus Jakarta Sans"],
            "body-md": ["Plus Jakarta Sans"],
            "label-md": ["Plus Jakarta Sans"],
            "body-sm": ["Plus Jakarta Sans"],
            "display-mobile": ["Plus Jakarta Sans"],
            "display": ["Plus Jakarta Sans"],
            "headline-md": ["Plus Jakarta Sans"],
            "label-lg": ["Plus Jakarta Sans"],
            "headline-xl": ["Plus Jakarta Sans"],
            "headline-xl-mobile": ["Plus Jakarta Sans"],
            "headline-sm": ["Plus Jakarta Sans"],
            "body-lg": ["Plus Jakarta Sans"],
            "headline-lg": ["Plus Jakarta Sans"]
        },

        fontSize: {
            "label-sm": ["11px", {
                lineHeight: "14px",
                letterSpacing: "0.04em",
                fontWeight: "700"
            }],
            "body-md": ["15px", {
                lineHeight: "24px",
                letterSpacing: "-0.005em",
                fontWeight: "400"
            }],
            "label-md": ["12px", {
                lineHeight: "16px",
                letterSpacing: "0.02em",
                fontWeight: "600"
            }],
            "body-sm": ["13px", {
                lineHeight: "20px",
                letterSpacing: "0em",
                fontWeight: "400"
            }],
            "display-mobile": ["38px", {
                lineHeight: "46px",
                letterSpacing: "-0.025em",
                fontWeight: "800"
            }],
            "display": ["56px", {
                lineHeight: "64px",
                letterSpacing: "-0.03em",
                fontWeight: "800"
            }],
            "headline-md": ["22px", {
                lineHeight: "30px",
                letterSpacing: "-0.015em",
                fontWeight: "600"
            }],
            "label-lg": ["14px", {
                lineHeight: "20px",
                letterSpacing: "0.01em",
                fontWeight: "600"
            }],
            "headline-xl": ["40px", {
                lineHeight: "48px",
                letterSpacing: "-0.025em",
                fontWeight: "700"
            }],
            "headline-xl-mobile": ["28px", {
                lineHeight: "36px",
                letterSpacing: "-0.02em",
                fontWeight: "700"
            }],
            "headline-sm": ["18px", {
                lineHeight: "26px",
                letterSpacing: "-0.01em",
                fontWeight: "600"
            }],
            "body-lg": ["18px", {
                lineHeight: "28px",
                letterSpacing: "-0.01em",
                fontWeight: "400"
            }],
            "headline-lg": ["28px", {
                lineHeight: "36px",
                letterSpacing: "-0.02em",
                fontWeight: "700"
            }]
        }
      }
    }
  }
</script>
<style data-purpose="custom-styling">
@import
	url('https://cdn.jsdelivr.net/gh/orioncactus/pretendard/dist/web/static/pretendard.css')
	;

.review-page {
	font-family: "Noto Sans KR", "Pretendard", "Plus Jakarta Sans",
		sans-serif;
	background-color: #f5f8fa;
	color: #131b2e;
}

.review-page .accordion-content-wrapper {
	max-height: 0;
	opacity: 0;
	overflow: hidden;
	transition: max-height 0.35s cubic-bezier(0.4, 0, 0.2, 1), opacity 0.25s
		ease;
}

.review-page .accordion-content-wrapper.open {
	opacity: 1;
}

.review-page ::-webkit-scrollbar {
	width: 6px;
	height: 6px;
}

.review-page ::-webkit-scrollbar-track {
	background: #f1f5f9;
}

.review-page ::-webkit-scrollbar-thumb {
	background: #cbd5e1;
	border-radius: 9999px;
}

.review-page ::-webkit-scrollbar-thumb:hover {
	background: #94a3b8;
}

/* 리뷰 게시판 전용 페이지네이션 */
.review-page .review-pagination .page-num-btn {
	width: 40px;
	height: 40px;
	display: flex;
	align-items: center;
	justify-content: center;
	border-radius: 50%;
	background: transparent;
	color: #131b2e;
	font-size: 15px;
	font-weight: 600;
	transition: background-color 0.2s, color 0.2s;
}

.review-page .review-pagination .page-num-btn:hover:not(.active) {
	background-color: #e2e7ff;
}

.review-page .review-pagination .page-num-btn.active {
	background-color: #006574;
	color: #ffffff;
	font-weight: 700;
}

.review-page .review-pagination .pagination-arrow {
	width: 36px;
	height: 40px;
	display: flex;
	align-items: center;
	justify-content: center;
	background: transparent;
	color: #648095;
	border: none;
	cursor: pointer;
	transition: color 0.2s;
}

.review-page .review-pagination .pagination-arrow:hover {
	color: #006574;
}

.review-page .review-pagination .pagination-arrow .material-symbols-outlined
	{
	font-size: 23px;
}
</style>
</head>



<body
	class="min-h-screen flex flex-col bg-[#f5f8fa] text-on-surface antialiased">

	<jsp:include page="/WEB-INF/top.jsp" />

	<main
		class="review-page flex-1 py-8 px-4 lg:px-8 max-w-7xl mx-auto w-full">
		<div class="space-y-6">
			<!-- BEGIN: HeroBanner -->
			<section
				class="relative overflow-hidden rounded-2xl p-7 sm:p-9 border border-[#c3e3eb]/60 shadow-sm bg-cover bg-center bg-no-repeat"
				style="background-image: url('${pageContext.request.contextPath}/resources/images/review/review.png');"
				data-purpose="banner-header">
				<div
					class="absolute right-0 bottom-0 top-0 w-2/5 pointer-events-none opacity-40 sm:opacity-90 overflow-hidden flex items-end justify-end">
					<svg class="h-full max-h-48 text-[#1b7e90]/20" fill="currentColor"
						viewbox="0 0 320 180">
						<path d="M260,180 L275,50 L285,50 L300,180 Z" opacity="0.6"></path>
						<rect height="14" opacity="0.8" rx="2" width="16" x="272" y="38"></rect>
						<circle cx="280" cy="45" fill="#fef08a" r="4"></circle>
						<path d="M120,45 C125,40 132,40 137,45 C142,40 149,40 154,45"
							fill="none" opacity="0.7" stroke="currentColor" stroke-width="2"></path>
						<path d="M190,30 C194,26 200,26 204,30 C208,26 214,26 218,30"
							fill="none" opacity="0.6" stroke="currentColor"
							stroke-width="1.8"></path>
						<path d="M0,180 C80,150 180,190 320,165 L320,180 Z" fill="#b9e4ec"
							opacity="0.5"></path>
				</svg>
				</div>
				<div
					class="relative z-10 flex flex-row items-center justify-between gap-6">
					<div class="space-y-1.5">
						<div
							class="inline-flex items-center space-x-1.5 text-xs font-semibold text-[#167b8d] tracking-tight">
							<i class="fa-solid fa-map-location-dot"></i> <span>여행자들의
								생생한 이야기</span>
						</div>
						<h1
							class="text-3xl sm:text-[34px] font-extrabold text-slate-900 tracking-tight leading-tight">
							<span class="text-[#008092]">여행 리뷰</span> <span
								class="text-[#131B2E]">게시판</span>
						</h1>
						<p
							class="text-sm sm:text-[15px] text-slate-600 font-normal pt-0.5">
							직접 다녀온 여행자의 경험과 감동을 다른 여행자들과 함께 나눠보세요.</p>
					</div>
					<div
						class="flex flex-col sm:flex-row items-start sm:items-end md:items-center gap-4 sm:gap-6 self-start md:self-center">
						<div
							class="font-handwriting text-2xl sm:text-[26px] text-[#1e788c] tracking-normal -rotate-2 select-none pr-1">
							"좋은 여행은 좋은 이야기로 남아요."</div>
						<button
							class="inline-flex items-center space-x-2 bg-[#1e90a3] hover:bg-[#167b8d] text-white px-5 py-2.5 rounded-xl font-semibold text-sm shadow-md shadow-[#1e90a3]/25 hover:shadow-lg transition-all transform active:scale-95 cursor-pointer"
							id="open-write-modal-btn">
							<i class="fa-solid fa-pen-to-square text-xs"></i> <span>리뷰
								작성하기</span>
						</button>
					</div>
				</div>
			</section>
			<!-- END: HeroBanner -->
			<!-- BEGIN: FilterAndSearchToolbar -->
			<section
				class="flex flex-row items-center justify-between gap-4 pt-1"
				data-purpose="category-and-search">
				<!-- Categories Pill Filter -->
				<div
					class="flex items-center space-x-2 overflow-x-auto pb-1.5 lg:pb-0 scrollbar-none"
					id="category-tabs-container">
					<button
						class="cat-pill-btn flex items-center space-x-1.5 px-4 py-2 bg-[#006574] text-white rounded-full text-xs font-semibold whitespace-nowrap shadow-sm hover:opacity-95 transition-all"
						data-cat="전체">
						<i class="fa-solid fa-border-all text-[11px]"></i> <span>전체</span>
					</button>
					<button
						class="cat-pill-btn flex items-center space-x-1.5 px-3.5 py-2 bg-white hover:bg-slate-50 text-slate-700 border border-slate-200/90 rounded-full text-xs font-medium whitespace-nowrap transition-colors"
						data-cat="자연">
						<i class="fa-solid fa-leaf text-emerald-600 text-[11px]"></i> <span>자연</span>
					</button>
					<button
						class="cat-pill-btn flex items-center space-x-1.5 px-3.5 py-2 bg-white hover:bg-slate-50 text-slate-700 border border-slate-200/90 rounded-full text-xs font-medium whitespace-nowrap transition-colors"
						data-cat="맛집">
						<i class="fa-solid fa-utensils text-amber-600 text-[11px]"></i> <span>맛집</span>
					</button>
					<button
						class="cat-pill-btn flex items-center space-x-1.5 px-3.5 py-2 bg-white hover:bg-slate-50 text-slate-700 border border-slate-200/90 rounded-full text-xs font-medium whitespace-nowrap transition-colors"
						data-cat="문화·역사">
						<i class="fa-solid fa-landmark-dome text-blue-600 text-[11px]"></i>
						<span>문화·역사</span>
					</button>
					<button
						class="cat-pill-btn flex items-center space-x-1.5 px-3.5 py-2 bg-white hover:bg-slate-50 text-slate-700 border border-slate-200/90 rounded-full text-xs font-medium whitespace-nowrap transition-colors"
						data-cat="체험·액티비티">
						<i class="fa-solid fa-person-hiking text-purple-600 text-[11px]"></i>
						<span>체험·액티비티</span>
					</button>
					<button
						class="cat-pill-btn flex items-center space-x-1.5 px-3.5 py-2 bg-white hover:bg-slate-50 text-slate-700 border border-slate-200/90 rounded-full text-xs font-medium whitespace-nowrap transition-colors"
						data-cat="숙소">
						<i class="fa-solid fa-bed text-rose-500 text-[11px]"></i> <span>숙소</span>
					</button>
				</div>
				<!-- Search Input and Sorting Dropdown -->
				<div class="flex items-center space-x-2.5">
					<div class="relative flex-1 sm:w-80">
						<div
							class="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-400">
							<i class="fa-solid fa-magnifying-glass text-xs"></i>
						</div>
						<input
							class="w-full pl-9 pr-8 py-2 text-xs bg-white rounded-full border border-slate-200 focus:outline-none focus:border-[#006574] focus:ring-1 focus:ring-[#006574] transition-all placeholder:text-slate-400"
							id="search-input" placeholder="여행지, 제목, 내용으로 검색해보세요." type="text" />
						<button
							class="hidden absolute inset-y-0 right-0 pr-3 flex items-center text-slate-400 hover:text-slate-600"
							id="search-clear-btn">
							<i class="fa-solid fa-xmark text-xs"></i>
						</button>
					</div>
					<div class="relative min-w-[95px]">
						<select
							class="appearance-none w-full bg-white border border-slate-200 rounded-full text-xs font-medium text-slate-700 py-2 pl-3.5 pr-7 focus:outline-none focus:border-[#006574] cursor-pointer"
							id="sort-select">
							<option value="latest">최신순</option>
							<option value="likes">좋아요순</option>
							<option value="views">조회순</option>
						</select>
						<div
							class="absolute inset-y-0 right-0 pr-2.5 flex items-center pointer-events-none text-slate-400">
							<i class="fa-solid fa-angle-down text-[10px]"></i>
						</div>
					</div>
				</div>
			</section>
			<!-- END: FilterAndSearchToolbar -->
			<!-- BEGIN: ReviewCardGrid / Accordion List Container -->
			<div class="space-y-3.5" id="review-accordion-list">
				<!-- Dynamic Reviews will be rendered here -->
			</div>
			<!-- END: ReviewCardGrid -->
			<!-- Empty State Container -->
			<div
				class="hidden py-16 text-center bg-white rounded-2xl border border-slate-200/80"
				id="no-results-view">
				<div
					class="w-12 h-12 mx-auto rounded-full bg-slate-100 flex items-center justify-center text-slate-400 mb-3">
					<i class="fa-solid fa-compass text-xl"></i>
				</div>
				<h4 class="font-bold text-slate-700 text-sm">일치하는 리뷰가 없습니다</h4>
				<p class="text-xs text-slate-500 mt-1">검색어를 변경하거나 다른 카테고리를
					선택해보세요.</p>
			</div>
			<!-- BEGIN: Pagination -->
			<!-- BEGIN: Pagination -->
			<section id="review-pagination"
				class="review-pagination hidden flex items-center justify-center gap-2 sm:gap-3 pt-2 pb-6"
				data-purpose="pagination">

				<button type="button" class="pagination-arrow"
					data-page-action="first" aria-label="첫 페이지">
					<span class="material-symbols-outlined">
						keyboard_double_arrow_left </span>
				</button>

				<button type="button" class="pagination-arrow"
					data-page-action="prev" aria-label="이전 페이지">
					<span class="material-symbols-outlined"> chevron_left </span>
				</button>

				<!-- 페이지 번호가 자동으로 들어갈 영역 -->
				<div id="review-page-numbers" class="flex items-center gap-2"></div>

				<button type="button" class="pagination-arrow"
					data-page-action="next" aria-label="다음 페이지">
					<span class="material-symbols-outlined"> chevron_right </span>
				</button>

				<button type="button" class="pagination-arrow"
					data-page-action="last" aria-label="마지막 페이지">
					<span class="material-symbols-outlined">
						keyboard_double_arrow_right </span>
				</button>

			</section>
			<!-- END: Pagination -->
			<!-- END: Pagination -->
		</div>
	</main>
	<!-- Lightbox Modal for Photo Enlargement -->
	<div
		class="fixed inset-0 z-50 hidden bg-black/85 backdrop-blur-sm flex items-center justify-center p-4 transition-opacity duration-300"
		id="lightbox-modal">
		<div class="relative max-w-4xl w-full flex flex-col items-center"
			id="lightbox-content-box">
			<button aria-label="닫기"
				class="absolute -top-10 right-0 text-white/80 hover:text-white text-2xl transition-colors cursor-pointer"
				id="lightbox-close">
				<i class="fa-solid fa-xmark"></i>
			</button>
			<div class="relative w-full flex items-center justify-center">
				<button aria-label="이전 사진"
					class="absolute left-2 sm:-left-12 w-10 h-10 rounded-full bg-white/20 hover:bg-white/40 text-white flex items-center justify-center text-sm transition-colors cursor-pointer"
					id="lightbox-prev">
					<i class="fa-solid fa-chevron-left"></i>
				</button>
				<img alt="확대된 사진"
					class="max-h-[75vh] w-auto max-w-full rounded-xl object-contain shadow-2xl select-none"
					id="lightbox-image" src="" />
				<button aria-label="다음 사진"
					class="absolute right-2 sm:-right-12 w-10 h-10 rounded-full bg-white/20 hover:bg-white/40 text-white flex items-center justify-center text-sm transition-colors cursor-pointer"
					id="lightbox-next">
					<i class="fa-solid fa-chevron-right"></i>
				</button>
			</div>
			<div
				class="mt-3 text-xs text-white/90 font-medium bg-black/40 px-3 py-1 rounded-full"
				id="lightbox-counter">1 / 1</div>
		</div>
	</div>
	<!-- Modal: 리뷰 작성하기 -->
	<div
		class="fixed inset-0 z-50 hidden bg-black/50 backdrop-blur-xs flex items-center justify-center p-4"
		id="write-modal">
		<div
			class="bg-white rounded-2xl max-w-lg w-full p-6 shadow-2xl border border-slate-100 transform transition-all animate-in fade-in zoom-in-95">
			<div
				class="flex items-center justify-between pb-4 border-b border-slate-100">
				<div class="flex items-center space-x-2">
					<div
						class="w-8 h-8 rounded-lg bg-teal-50 text-[#006574] flex items-center justify-center">
						<i class="fa-solid fa-pen-fancy text-sm"></i>
					</div>
					<h3 class="font-bold text-slate-900 text-base">여행 리뷰 작성</h3>
				</div>
				<button
					class="text-slate-400 hover:text-slate-600 text-lg cursor-pointer"
					id="close-write-modal">
					<i class="fa-solid fa-xmark"></i>
				</button>
			</div>
			<form class="mt-4 space-y-4" id="write-review-form">
				<div>
					<label class="block text-xs font-semibold text-slate-700 mb-1">카테고리</label>
					<select
						class="w-full text-xs rounded-xl border-slate-200 focus:border-[#006574] focus:ring-[#006574]"
						id="review-cat-input">
						<option value="자연">자연</option>
						<option value="맛집">맛집</option>
						<option value="문화·역사">문화·역사</option>
						<option value="체험·액티비티">체험·액티비티</option>
						<option value="숙소">숙소</option>
					</select>
				</div>
				<div>
					<label class="block text-xs font-semibold text-slate-700 mb-1">제목</label>
					<input
						class="w-full text-xs rounded-xl border-slate-200 focus:border-[#006574] focus:ring-[#006574]"
						id="review-title-input" placeholder="여행지의 느낌을 담은 멋진 제목을 작성하세요"
						required="" type="text" />
				</div>
				<div>
					<label class="block text-xs font-semibold text-slate-700 mb-1">작성자
						닉네임</label> <input
						class="w-full text-xs rounded-xl border-slate-200 focus:border-[#006574] focus:ring-[#006574]"
						id="review-author-input" placeholder="닉네임을 입력하세요" required=""
						type="text" value="" />
				</div>
				<div>
					<label class="block text-xs font-semibold text-slate-700 mb-1">후기
						내용</label>
					<textarea
						class="w-full text-xs rounded-xl border-slate-200 focus:border-[#006574] focus:ring-[#006574]"
						id="review-content-input"
						placeholder="여행지에서의 소중한 추억과 유용한 팁을 들려주세요." required="" rows="4"></textarea>
				</div>

				<!-- 리뷰 사진 첨부 -->

				<!-- 수정창에서 기존 사진 표시 -->
				<div id="existing-review-images"
					class="hidden grid grid-cols-3 sm:grid-cols-5 gap-2 mt-3 mb-3">
				</div>

				<!-- 새 사진 첨부 영역 -->
				<div id="review-image-section">

					<label class="block text-xs font-semibold text-slate-700 mb-2">
						여행 사진 첨부 <span class="font-normal text-slate-400">(최대 5장)</span>
					</label> <label for="review-image-input"
						class="flex flex-col items-center justify-center w-full
               border-2 border-dashed border-slate-200 rounded-xl
               bg-slate-50 hover:bg-teal-50 hover:border-[#006574]
               transition-colors cursor-pointer p-5">

						<i class="fa-regular fa-images text-2xl text-[#006574] mb-2"></i>

						<span class="text-xs font-semibold text-slate-700"> 사진을 선택해
							주세요 </span> <span class="text-[11px] text-slate-400 mt-1"> JPG,
							PNG, WEBP · 사진당 최대 5MB </span>
					</label> <input type="file" id="review-image-input"
						accept="image/jpeg,image/png,image/webp" multiple class="hidden">

					<div id="review-image-preview"
						class="grid grid-cols-3 sm:grid-cols-5 gap-2 mt-3"></div>

					<p id="review-image-error" class="hidden text-xs text-red-500 mt-2"></p>
				</div>

				<div
					class="flex items-center justify-end space-x-2 pt-3 border-t border-slate-100">
					<button
						class="px-4 py-2 text-xs font-medium text-slate-600 hover:bg-slate-100 rounded-xl transition-colors"
						id="cancel-write-modal" type="button">취소</button>
					<button
						class="px-5 py-2 text-xs font-bold text-white bg-[#006574] hover:bg-[#004e5a] rounded-xl shadow-xs transition-colors"
						type="submit">리뷰 등록</button>
				</div>
			</form>
		</div>
	</div>

	<!-- DB에서 전달받은 리뷰 JSON -->
	<script type="application/json" id="review-likes-data">${reviewLikesJson}</script>
	<script type="application/json" id="review-liked-data">${reviewLikedJson}</script>
	<script type="application/json" id="review-db-data">${reviewListJson}</script>
	<!-- JavaScript Implementation -->
	<script data-purpose="full-interactive-logic">
	// 현재 로그인한 회원 정보
	const currentMemberId = '${sessionScope.loginId}';

	// 관리자 여부: 실제 로그인 세션의 role 값 기준
	const isReviewAdmin =
	    ${sessionScope.role eq 'ADMIN' ? 'true' : 'false'};
	
  // State and Data Stores
const dbReviews = JSON.parse(
    document.getElementById("review-db-data").textContent
);

  const reviewLikeCounts = JSON.parse(document.getElementById('review-likes-data').textContent || '{}');
  const reviewLikedIds = new Set(JSON.parse(document.getElementById('review-liked-data').textContent || '[]'));
  const reviewsData = dbReviews.map(review => {

    const author = review.authorNickname || "여행자";

    const date = review.createdAt
        ? new Date(review.createdAt).toLocaleDateString("ko-KR")
        : "";

    return {
        id: review.reviewId,
        memberId: review.memberId,
        category: review.category || "자연",
        title: review.title || "",
        author: author,

        avatarType: "initial",
        avatarVal: author.charAt(0),
        avatarBg: "bg-teal-100 text-teal-800",

        date: date,

        views: String(review.viewCount || 0),
        viewsNum: review.viewCount || 0,


        likes: Number(reviewLikeCounts[review.reviewId] || 0),
        isLiked: reviewLikedIds.has(Number(review.reviewId)),


        content: [review.content || ""],

        images: (review.images || []).map(path =>
        '${pageContext.request.contextPath}' + path
    ),
        comments: []
    };
});

let currentCategory = "전체";
  let currentSearchQuery = "";
  let currentSort = "latest";
  let activeAccordionId = null;
  let currentPage = 1;
  const reviewsPerPage = 10;
  
  // Lightbox State
  let lightboxImages = [];
  let currentLightboxIndex = 0;

  function escapeHtml(value) {
	    return String(value ?? "").replace(/[&<>"']/g, function(ch) {
	        return {
	            "&": "&amp;",
	            "<": "&lt;",
	            ">": "&gt;",
	            '"': "&quot;",
	            "'": "&#39;"
	        }[ch];
	    });
	}
  
  // Category Badge Styles
  function getCategoryBadge(cat) {
    switch (cat) {
      case "자연":
        return `<span class="shrink-0 bg-emerald-50 text-emerald-700 font-semibold px-2.5 py-0.5 rounded-full text-xs border border-emerald-200/60">자연</span>`;
      case "맛집":
        return `<span class="shrink-0 bg-orange-50 text-orange-700 font-semibold px-2.5 py-0.5 rounded-full text-xs border border-orange-200/60">맛집</span>`;
      case "문화·역사":
        return `<span class="shrink-0 bg-amber-50 text-amber-700 font-semibold px-2.5 py-0.5 rounded-full text-xs border border-amber-200/60">문화·역사</span>`;
      case "체험·액티비티":
        return `<span class="shrink-0 bg-purple-50 text-purple-700 font-semibold px-2.5 py-0.5 rounded-full text-xs border border-purple-200/60">체험·액티비티</span>`;
      case "숙소":
        return `<span class="shrink-0 bg-rose-50 text-rose-700 font-semibold px-2.5 py-0.5 rounded-full text-xs border border-rose-200/60">숙소</span>`;
      default:
        return `<span class="shrink-0 bg-slate-50 text-slate-700 font-semibold px-2.5 py-0.5 rounded-full text-xs border border-slate-200/60">\${cat}</span>`;
    }
  }

  // Gallery HTML Generator
  function renderGallery(item) {
    const count = item.images ? item.images.length : 0;
    if (count === 0) return "";

    if (count === 1) {
      return `
        <div class="relative rounded-2xl overflow-hidden aspect-[21/9] sm:aspect-[24/9] max-h-80 shadow-sm bg-slate-100 cursor-pointer group" onclick="openLightbox(\${item.id}, 0, event)">
          <img alt="\${escapeHtml(item.title)}" class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500" src="\${item.images[0]}"/>
          <div class="absolute bottom-3 right-3 bg-black/60 text-white text-[11px] px-3 py-1 rounded-full backdrop-blur-xs flex items-center gap-1">
            <i class="fa-solid fa-expand text-[10px]"></i>클릭하여 큰 사진 보기
          </div>
        </div>
      `;
    }

    if (count === 2) {
      return `
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
          <div class="relative aspect-[16/10] rounded-xl overflow-hidden bg-slate-100 cursor-pointer group shadow-2xs" onclick="openLightbox(\${item.id}, 0, event)">
            <img alt="\${escapeHtml(item.title)} 1" class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300" src="\${item.images[0]}"/>
          </div>
          <div class="relative aspect-[16/10] rounded-xl overflow-hidden bg-slate-100 cursor-pointer group shadow-2xs" onclick="openLightbox(\${item.id}, 1, event)">
            <img alt="\${escapeHtml(item.title)} 2" class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300" src="\${item.images[1]}"/>
          </div>
        </div>
      `;
    }

    if (count === 3) {
      return `
        <div class="grid grid-cols-1 sm:grid-cols-3 gap-3">
          <div class="sm:col-span-2 relative aspect-[16/10] sm:aspect-auto sm:h-full rounded-xl overflow-hidden bg-slate-100 cursor-pointer group shadow-2xs" onclick="openLightbox(\${item.id}, 0, event)">
            <img alt="\${escapeHtml(item.title)} 1" class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300" src="\${item.images[0]}"/>
            <span class="absolute bottom-2 left-2 bg-black/60 text-white text-[11px] px-2.5 py-0.5 rounded-md">대표 사진</span>
          </div>
          <div class="grid grid-cols-2 sm:grid-cols-1 gap-3">
            <div class="relative aspect-[16/10] rounded-xl overflow-hidden bg-slate-100 cursor-pointer group shadow-2xs" onclick="openLightbox(\${item.id}, 1, event)">
              <img alt="\${escapeHtml(item.title)} 2" class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300" src="\${item.images[1]}"/>
            </div>
            <div class="relative aspect-[16/10] rounded-xl overflow-hidden bg-slate-100 cursor-pointer group shadow-2xs" onclick="openLightbox(\${item.id}, 2, event)">
              <img alt="\${escapeHtml(item.title)} 3" class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300" src="\${item.images[2]}"/>
            </div>
          </div>
        </div>
      `;
    }

    // 4 or more photos: 제주 올레길 style (5-Grid or 4-Grid with overlay)
    const extraCount = count - 4;
    return `
      <div class="space-y-2">
        <div class="flex items-center justify-between">
          <span class="text-xs font-bold text-slate-700 flex items-center gap-1.5"><i class="fa-regular fa-images text-[#006574]"></i>첨부 사진 (\${count}장)</span>
          <span class="text-[11px] text-slate-400">클릭 시 확대 보기</span>
        </div>
        <div class="grid grid-cols-2 sm:grid-cols-4 gap-2.5">
          <div class="relative aspect-square rounded-xl overflow-hidden bg-slate-100 cursor-pointer group shadow-2xs" onclick="openLightbox(\${item.id}, 0, event)">
            <img alt="\${escapeHtml(item.title)} 1" class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300" src="\${item.images[0]}"/>
          </div>
          <div class="relative aspect-square rounded-xl overflow-hidden bg-slate-100 cursor-pointer group shadow-2xs" onclick="openLightbox(\${item.id}, 1, event)">
            <img alt="\${escapeHtml(item.title)} 2" class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300" src="\${item.images[1]}"/>
          </div>
          <div class="relative aspect-square rounded-xl overflow-hidden bg-slate-100 cursor-pointer group shadow-2xs" onclick="openLightbox(\${item.id}, 2, event)">
            <img alt="\${escapeHtml(item.title)} 3" class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300" src="\${item.images[2]}"/>
          </div>
          <div class="relative aspect-square rounded-xl overflow-hidden bg-slate-800 cursor-pointer group shadow-2xs" onclick="openLightbox(\${item.id}, 3, event)">
            <img alt="\${escapeHtml(item.title)} 4" class="w-full h-full object-cover \${extraCount > 0 ? 'opacity-60' : ''} group-hover:scale-105 transition-transform duration-300" src="\${item.images[3]}"/>
           	\${extraCount > 0 ? `
       		    <div class="absolute inset-0 flex flex-col items-center justify-center text-white font-bold text-sm bg-black/40 hover:bg-black/50 transition-colors">
       		        <span>+\${extraCount}</span>
       		        <span class="text-[10px] font-normal opacity-90">더보기</span>
       		    </div>
       		` : ''}
          </div>
        </div>
      </div>
    `;
  }

  // Filter & Sort Logic
  function getFilteredAndSortedReviews() {
    let list = [...reviewsData];

    if (currentCategory !== "전체") {
      list = list.filter(item => item.category === currentCategory);
    }

    if (currentSearchQuery.trim()) {
      const q = currentSearchQuery.trim().toLowerCase();
      list = list.filter(item => {
        const titleMatch = item.title.toLowerCase().includes(q);
        const contentMatch = (item.content || []).join(" ").toLowerCase().includes(q);
        const authorMatch = item.author.toLowerCase().includes(q);
        return titleMatch || contentMatch || authorMatch;
      });
    }

    if (currentSort === "latest") {
      list.sort((a, b) => b.id - a.id);
    } else if (currentSort === "likes") {
        list.sort((a, b) => b.likes - a.likes || b.id - a.id);
    } else if (currentSort === "views") {
      list.sort((a, b) => b.viewsNum - a.viewsNum);
    }

    return list;
  }

  function renderPagination(totalReviews) {

	    const pagination = document.getElementById("review-pagination");
	    const pageNumbers = document.getElementById("review-page-numbers");

	    const totalPages = Math.ceil(totalReviews / reviewsPerPage);

	    // 리뷰가 없으면 페이지네이션 전체 숨기기
	    if (totalPages === 0) {
	        pagination.classList.add("hidden");
	        return;
	    }

	    pagination.classList.remove("hidden");

	    // 현재 페이지가 마지막 페이지보다 크면 보정
	    currentPage = Math.max(1, Math.min(currentPage, totalPages));

	    pageNumbers.innerHTML = "";

	    for (let page = 1; page <= totalPages; page++) {

	        const button = document.createElement("button");

	        button.type = "button";
	        button.className = "page-num-btn" +
	            (page === currentPage ? " active" : "");

	        button.textContent = page;

	        button.addEventListener("click", function() {
	            currentPage = page;
	            renderReviews();
	        });

	        pageNumbers.appendChild(button);
	    }

	    pagination.querySelectorAll("[data-page-action]").forEach(button => {

	        button.onclick = function() {

	            const action = this.dataset.pageAction;

	            if (action === "first") currentPage = 1;
	            if (action === "prev") currentPage = Math.max(1, currentPage - 1);
	            if (action === "next") currentPage = Math.min(totalPages, currentPage + 1);
	            if (action === "last") currentPage = totalPages;

	            renderReviews();
	        };
	    });
	}
  
  // Render All Review Items
  function renderReviews() {

    const list = getFilteredAndSortedReviews();

    const container = document.getElementById("review-accordion-list");
    const emptyView = document.getElementById("no-results-view");

    renderPagination(list.length);

    // 리뷰가 없을 때
    if (list.length === 0) {

        container.innerHTML = "";

        emptyView.classList.remove("hidden");

        const isSearching =
            currentSearchQuery.trim() !== "" ||
            currentCategory !== "전체";

        emptyView.querySelector("h4").textContent = isSearching
            ? "일치하는 리뷰가 없습니다"
            : "아직 등록된 리뷰가 없습니다";

        emptyView.querySelector("p").textContent = isSearching
            ? "검색어를 변경하거나 다른 카테고리를 선택해보세요."
            : "첫 번째 여행 이야기를 남겨보세요!";

        return;
    }

    // 리뷰가 있을 때
    emptyView.classList.add("hidden");

    const startIndex = (currentPage - 1) * reviewsPerPage;
    const endIndex = startIndex + reviewsPerPage;
    const pagedList = list.slice(startIndex, endIndex);

    // 아래 기존 코드는 그대로 유지
    // 현재 페이지의 리뷰 카드 출력
	container.innerHTML = pagedList.map((item, idx) => {
    	const seq = String(startIndex + idx + 1).padStart(2, '0');
    	const photoBadge = item.images.length > 0
        ? `<span class="shrink-0 hidden md:inline-flex items-center space-x-1 text-[11px] text-slate-400 bg-slate-100 px-2 py-0.5 rounded-md">
             <i class="fa-regular fa-images text-[10px]"></i>
             <span>\${item.images.length}장</span>
           </span>`
        : `<span class="shrink-0 hidden md:inline-flex items-center space-x-1 text-[11px] text-slate-400 bg-slate-100 px-2 py-0.5 rounded-md">
             <i class="fa-solid fa-align-left text-[10px]"></i>
             <span>텍스트 리뷰</span>
           </span>`;
    	
    	
      const avatarHtml = item.avatarType === 'img'
        ? `<img alt="\${item.author}" class="w-5 h-5 rounded-full object-cover" src="\${item.avatarVal}"/>`
        : `<span class="w-5 h-5 rounded-full \${item.avatarBg || 'bg-slate-100 text-slate-600'} flex items-center justify-center font-bold text-[10px]">\${item.avatarVal}</span>`;

      const isOpen = activeAccordionId === item.id;
      const borderClass = isOpen ? 'border-[#006574] ring-1 ring-[#006574]/20 bg-[#fbfdff]' : 'border-slate-200/80 bg-white hover:border-[#006574]/40';
      const chevronClass = isOpen ? 'rotate-180 bg-[#006574] text-white' : 'text-slate-600 bg-slate-100';

      const commentsListHtml = item.comments.length > 0
      ? item.comments.map(c => {

          // 댓글 작성자 또는 관리자만 수정·삭제 가능
          const canManage =
              currentMemberId &&
              (currentMemberId === c.userId || isReviewAdmin);

          return `
          <div class="flex items-start gap-2 text-xs text-slate-700 bg-slate-50/70 px-3 py-2 rounded-lg">

              <div class="w-6 h-6 rounded-full bg-slate-200 text-slate-600 font-bold flex items-center justify-center text-[10px] shrink-0 mt-0.5">
                  \${escapeHtml((c.author || "?").slice(0, 1))}
              </div>

              <div class="flex-1 min-w-0">

                  <div class="flex items-center justify-between gap-2">

                      <span class="font-bold text-slate-800">
                          \${escapeHtml(c.author)}
                      </span>

                      <div class="flex items-center gap-2">

                          <span class="text-[10px] text-slate-400">
                              \${escapeHtml(c.date)}
                          </span>

                          \${canManage ? `
                              <button type="button"
                                  onclick="handleEditComment(\${item.id}, \${c.id}, event)"
                                  class="text-[11px] text-[#006574] hover:underline">
                                  수정
                              </button>

                              <button type="button"
                                  onclick="handleDeleteComment(\${item.id}, \${c.id}, event)"
                                  class="text-[11px] text-red-500 hover:underline">
                                  삭제
                              </button>
                          ` : ''}

                      </div>
                  </div>

                  <p class="text-slate-600 mt-0.5 leading-snug whitespace-pre-wrap break-words">\${escapeHtml(c.text)}</p>

              </div>
          </div>
          `;
      }).join('')
      : `<p class="text-xs text-slate-400 py-2 text-center">
          첫 번째 댓글을 남겨보세요!
         </p>`;

      return `
        <article class="accordion-item rounded-2xl border \${borderClass} shadow-xs overflow-hidden transition-all duration-300" data-id="\${item.id}">
          <header class="accordion-header p-4 sm:px-6 cursor-pointer flex items-center justify-between gap-4 select-none hover:bg-slate-50/70 transition-colors" onclick="toggleAccordion(\${item.id})">
            <div class="flex items-center space-x-3.5 flex-1 min-w-0">
              <span class="shrink-0 font-bold text-xs text-slate-400 w-5 text-center">\${seq}</span>
              \${getCategoryBadge(item.category)}
              <h3 class="font-bold text-sm sm:text-base text-slate-900 truncate hover:text-[#006574] transition-colors">\${escapeHtml(item.title)}</h3>
              \${photoBadge}
            </div>
            <div class="flex items-center space-x-4 sm:space-x-6 text-xs text-slate-500 shrink-0">
              <div class="hidden sm:flex items-center space-x-2">
                \${avatarHtml}
                <span class="font-medium text-slate-700">\${item.author}</span>
              </div>
              <span class="text-[11px] text-slate-400 hidden md:inline">\${item.date}</span>
              <div class="flex items-center space-x-2.5 text-[11px] text-slate-400">
                <span class="flex items-center space-x-1"><i class="fa-regular fa-eye text-[10px]"></i><span>\${item.views}</span></span>
                <span class="flex items-center space-x-1 text-rose-500"><i class="\${item.isLiked ? 'fa-solid' : 'fa-regular'} fa-heart text-[10px]"></i><span class="like-num-\${item.id}">\${item.likes}</span></span>
                <span class="flex items-center space-x-1"><i class="fa-regular fa-comment text-[10px]"></i><span class="comment-count-badge-\${item.id}">\${item.comments.length}</span></span>
              </div>
              <button aria-label="펼치기" class="w-7 h-7 rounded-full flex items-center justify-center transition-all duration-300 transform accordion-chevron \${chevronClass}">
                <i class="fa-solid fa-angle-down text-xs"></i>
              </button>
            </div>
          </header>

          <div class="accordion-content-wrapper \${isOpen ? 'open' : ''}" id="accordion-content-\${item.id}">
            <div class="border-t border-slate-100 bg-[#fbfdff] px-4 sm:px-8 py-6 space-y-6">
              <!-- Top Meta for Mobile -->
              <div class="flex sm:hidden items-center justify-between text-xs text-slate-500 pb-2 border-b border-slate-100">
                <div class="flex items-center space-x-2">
                  \${avatarHtml}
                  <span class="font-medium text-slate-700">\${item.author}</span>
                </div>
                <span class="text-[11px] text-slate-400">\${item.date}</span>
              </div>

              \${item.quote ? `
                <div class="bg-amber-50/60 border-l-4 border-amber-400 p-4 rounded-r-xl">
                  <p class="font-handwriting text-lg text-amber-800 leading-snug">"\${item.quote}"</p>
                </div>
              ` : ''}

              <!-- Review Body Text -->
              <div class="prose max-w-none text-slate-700 leading-relaxed text-sm sm:text-[15px] space-y-2 bg-white/80 p-4 rounded-xl border border-slate-100 shadow-2xs">
           		\${item.content.map(p => '<p>' + escapeHtml(p) + '</p>').join('')}
              </div>

              <!-- Attached Gallery -->
              \${renderGallery(item)}

              <!-- Interactive Utility Bar -->
              <div class="flex flex-wrap items-center justify-between gap-3 pt-3 border-t border-slate-200/80">
                <div class="flex items-center space-x-2">
                  <button class="like-btn flex items-center space-x-1.5 px-3.5 py-1.5 \${item.isLiked ? 'bg-rose-50 text-rose-600 border border-rose-200' : 'bg-slate-50 text-slate-600 border border-slate-200'} rounded-lg text-xs font-semibold transition-all hover:bg-rose-100/70" onclick="handleLike(\${item.id}, event)" type="button">
                    <i class="\${item.isLiked ? 'fa-solid text-rose-500' : 'fa-regular text-slate-400'} fa-heart text-xs"></i>
                    <span>좋아요</span>
                    <span class="font-bold like-num-\${item.id}">\${item.likes}</span>
                  </button>
                  \${(isReviewAdmin ||
                		    (currentMemberId !== '' &&
                		     currentMemberId === item.memberId)) ? `

                		    <button
                		        type="button"
                		        onclick="handleEditAction(\${item.id}, event)"
                		        class="flex items-center space-x-1 px-3 py-1.5 border border-slate-200 rounded-lg text-xs font-medium">

                		        <i class="fa-regular fa-pen-to-square"></i>
                		        <span>수정</span>
                		    </button>

                		    <button
                		        type="button"
                		        onclick="handleDeleteAction(\${item.id}, event)"
                		        class="flex items-center space-x-1 px-3 py-1.5 border border-slate-200 rounded-lg text-xs font-medium">

                		        <i class="fa-regular fa-trash-can"></i>
                		        <span>삭제</span>
                		    </button>

                		` : ''}
      
                </div>
                <button class="text-xs text-slate-500 hover:text-[#006574] flex items-center space-x-1 font-medium transition-colors cursor-pointer" onclick="closeAccordion(\${item.id}, event)" type="button">
                  <span>상세 내용 접기</span>
                  <i class="fa-solid fa-chevron-up text-[10px]"></i>
                </button>
              </div>

              <!-- Comments Feed in Accordion -->
              <div class="bg-white rounded-xl p-4 sm:p-5 border border-slate-200/70 space-y-4" onclick="event.stopPropagation();">
                <div class="flex items-center justify-between pb-2 border-b border-slate-100">
                  <span class="text-xs font-bold text-slate-800 flex items-center gap-1.5"><i class="fa-regular fa-comment-dots text-[#006574]"></i>댓글 <span class="text-[#006574] comment-count-badge-\${item.id}">\${item.comments.length}</span></span>
                  <span class="text-[11px] text-slate-400">최신순</span>
                </div>
                <div class="space-y-3 max-h-56 overflow-y-auto pr-1" id="comments-box-\${item.id}">
                  \${commentsListHtml}
                </div>
                <!-- Comment Input -->
                <div class="flex items-center gap-2 pt-2 border-t border-slate-100">
                  <input id="comment-input-\${item.id}" class="flex-1 text-xs bg-slate-100 border-none rounded-full py-2 px-3.5 focus:bg-white focus:ring-1 focus:ring-[#006574]" placeholder="따뜻한 댓글을 남겨보세요..." type="text" onkeydown="if(event.key==='Enter'){handleAddComment(\${item.id});}"/>
                  <button class="px-3.5 py-2 bg-[#006574] hover:bg-[#004e5a] text-white rounded-full text-xs font-semibold shrink-0 transition-colors cursor-pointer" onclick="handleAddComment(\${item.id})" type="button">
                    <i class="fa-regular fa-paper-plane mr-1 text-[10px]"></i>등록
                  </button>
                </div>
              </div>
            </div>
          </div>
        </article>
      `;
    }).join('');

    // After updating DOM, recalculate open height if there is an active item
    if (activeAccordionId) {
      updateAccordionHeights();
    }
  }

  // Accordion Logic (Single exclusive accordion)
 // 리뷰 펼치기 / 닫기 + DB 댓글 조회
async function toggleAccordion(id) {

    if (activeAccordionId === id) {
        closeAccordion(id);
        return;
    }

    activeAccordionId = id;
    const selectedReview = reviewsData.find(r => r.id === id);
    if (selectedReview) {
      try {
        const response = await fetch('${pageContext.request.contextPath}/ReviewView', {
          method:'POST', headers:{'Content-Type':'application/x-www-form-urlencoded;charset=UTF-8'},
          body: new URLSearchParams({reviewId:String(id)})
        });
        if (response.ok) {
          const data = await response.json();
          selectedReview.viewsNum = data.views;
          selectedReview.views = String(data.views);
        }
      } catch(e) { console.error('조회수 갱신 실패',e); }
    }

    // 리뷰를 펼친 상태로 다시 출력
    renderReviews();

    // 해당 리뷰의 댓글을 DB에서 조회
    await loadReviewComments(id);
}

  function closeAccordion(id, event) {
    if (event) event.stopPropagation();
    if (activeAccordionId === id) {
      collapseElement(id);
      activeAccordionId = null;
      renderReviews();
    }
  }

  function expandElement(id) {
    const el = document.getElementById(`accordion-content-\${id}`);
    if (el) {
      el.classList.add('open');
      el.style.maxHeight = el.scrollHeight + 100 + "px";
    }
  }

  function collapseElement(id) {
    const el = document.getElementById(`accordion-content-\${id}`);
    if (el) {
      el.classList.remove('open');
      el.style.maxHeight = "0px";
    }
  }

  function updateAccordionHeights() {
    if (!activeAccordionId) return;
    const el = document.getElementById(`accordion-content-\${activeAccordionId}`);
    if (el) {
      el.classList.add('open');
      el.style.maxHeight = el.scrollHeight + 100 + "px";
    }
  }

  // Like Button Handler
  async function handleLike(id, event) {
    if (event) event.stopPropagation();
    if (!currentMemberId) { alert('로그인 후 좋아요를 누를 수 있습니다.'); return; }
    const item = reviewsData.find(r => r.id === id);
    if (!item) return;
    try {
      const response = await fetch('${pageContext.request.contextPath}/ReviewLike', {
        method: 'POST', headers: {'Content-Type':'application/x-www-form-urlencoded;charset=UTF-8'},
        body: new URLSearchParams({reviewId: String(id)})
      });
   // 로그인 세션이 만료되었거나 로그인하지 않은 경우
      if (response.status === 401) {
          alert('로그인이 필요한 서비스입니다.\n로그인 후 이용해주세요.');
          return;
      }

      // 그 외 오류
      if (!response.ok) {
          throw new Error('좋아요 저장 실패');
      }
      const data = await response.json();
      item.likes = data.likes; item.isLiked = data.liked;
      renderReviews();
    } catch(e) { console.error(e); alert('좋아요 처리 중 오류가 발생했습니다.'); }
  }

//댓글 등록 - MySQL DB 연동
  async function handleAddComment(id) {

      const input = document.getElementById(
          "comment-input-" + id
      );

      if (!input) return;

      const content = input.value.trim();

      if (!content) {
          alert("댓글 내용을 입력해 주세요.");
          return;
      }

      if (!currentMemberId) {
          alert("로그인 후 댓글을 작성할 수 있습니다.");
          return;
      }

      if (content.length > 1000) {
          alert("댓글은 1000자 이하로 입력해 주세요.");
          return;
      }

      try {
          const params = new URLSearchParams();

          params.append("action", "create");
          params.append("reviewId", id);
          params.append("content", content);

          const response = await fetch(
              "${pageContext.request.contextPath}/ReviewComment",
              {
                  method: "POST",
                  headers: {
                      "Content-Type":
                          "application/x-www-form-urlencoded;charset=UTF-8"
                  },
                  body: params.toString()
              }
          );
          
       	// 로그인 세션이 만료된 경우
          if (response.status === 401) {
              alert("로그인이 필요합니다.");
              return;
          }

          if (!response.ok) {

        	    const errorMessage = await response.text();

        	    console.error("[댓글 등록 실패]");
        	    console.error("HTTP 상태 코드:", response.status);
        	    console.error("서버 응답:", errorMessage);

        	    alert(
        	        "댓글 등록 실패\n" +
        	        "HTTP 상태 코드: " + response.status + "\n" +
        	        "서버 응답: " + errorMessage.substring(0, 200)
        	    );

        	    return;
        	}

          input.value = "";

          await loadReviewComments(id);

      } catch (error) {
          console.error("[댓글 등록 오류]", error);
          alert("댓글 등록 중 오류가 발생했습니다.");
      }
  }
  
//댓글 목록 - MySQL DB 조회
  async function loadReviewComments(id) {

      try {
          const response = await fetch(
              "${pageContext.request.contextPath}/ReviewComment?reviewId="
              + encodeURIComponent(id)
          );

          if (!response.ok) {
              throw new Error("댓글 조회 실패");
          }

          const data = await response.json();

          const item = reviewsData.find(
              review => review.id === id
          );

          if (!item) return;

          item.comments = data.map(comment => ({
              id: comment.commentId,
              userId: comment.memberId,
              author: comment.authorNickname || comment.memberId,
              text: comment.content,
              date: comment.createdAt
                  ? new Date(comment.createdAt).toLocaleDateString("ko-KR")
                  : "",
              avatar: ""
          }));

          renderReviews();
          updateAccordionHeights();

      } catch (error) {
          console.error("[댓글 조회 오류]", error);
          alert("댓글을 불러오지 못했습니다.");
      }
  }
  
//댓글 수정 - MySQL DB 연동
  async function handleEditComment(reviewId, commentId, event) {

      if (event) event.stopPropagation();

      const item = reviewsData.find(r => r.id === reviewId);
      if (!item) return;

      const comment = item.comments.find(c => c.id === commentId);
      if (!comment) return;

      if (currentMemberId !== comment.userId && !isReviewAdmin) {
          alert("댓글 수정 권한이 없습니다.");
          return;
      }

      const newContent = prompt("댓글을 수정해 주세요.", comment.text);

      if (newContent === null) return;

      const content = newContent.trim();

      if (!content || content.length > 1000) {
          alert("댓글은 1~1000자로 입력해 주세요.");
          return;
      }

      try {
          const params = new URLSearchParams();

          params.append("action", "update");
          params.append("commentId", commentId);
          params.append("content", content);

          const response = await fetch(
              "${pageContext.request.contextPath}/ReviewComment",
              {
                  method: "POST",
                  headers: {
                      "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
                  },
                  body: params.toString()
              }
          );

          if (response.status === 401) {
        	    alert("로그인이 필요합니다.");
        	    return;
        	}
          
          if (!response.ok) {
              alert("댓글 수정에 실패했습니다. 상태 코드: " + response.status);
              return;
          }

          await loadReviewComments(reviewId);

      } catch (error) {
          console.error("[댓글 수정 오류]", error);
          alert("댓글 수정 중 오류가 발생했습니다.");
      }
  }
  
//댓글 삭제 - MySQL DB 연동
  async function handleDeleteComment(reviewId, commentId, event) {

      if (event) event.stopPropagation();

      const item = reviewsData.find(r => r.id === reviewId);
      if (!item) return;

      const comment = item.comments.find(c => c.id === commentId);
      if (!comment) return;

      if (currentMemberId !== comment.userId && !isReviewAdmin) {
          alert("댓글 삭제 권한이 없습니다.");
          return;
      }

      if (!confirm("댓글을 삭제하시겠습니까?")) {
          return;
      }

      try {
          const params = new URLSearchParams();

          params.append("action", "delete");
          params.append("commentId", commentId);

          const response = await fetch(
              "${pageContext.request.contextPath}/ReviewComment",
              {
                  method: "POST",
                  headers: {
                      "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
                  },
                  body: params.toString()
              }
          );
          
          if (response.status === 401) {
        	    alert("로그인이 필요합니다.");
        	    return;
        	}

          if (!response.ok) {
              alert("댓글 삭제에 실패했습니다. 상태 코드: " + response.status);
              return;
          }

          await loadReviewComments(reviewId);

      } catch (error) {
          console.error("[댓글 삭제 오류]", error);
          alert("댓글 삭제 중 오류가 발생했습니다.");
      }
  }

  // Category Tabs Filter
  document.querySelectorAll('.cat-pill-btn').forEach(btn => {
    btn.addEventListener('click', () => {
      const cat = btn.getAttribute('data-cat');
      currentCategory = cat;

      // Update button styling
      document.querySelectorAll('.cat-pill-btn').forEach(b => {
        b.className = "cat-pill-btn flex items-center space-x-1.5 px-3.5 py-2 bg-white hover:bg-slate-50 text-slate-700 border border-slate-200/90 rounded-full text-xs font-medium whitespace-nowrap transition-colors";
      });
      btn.className = "cat-pill-btn flex items-center space-x-1.5 px-4 py-2 bg-[#006574] text-white rounded-full text-xs font-semibold whitespace-nowrap shadow-sm hover:opacity-95 transition-all";

      activeAccordionId = null;
      currentPage = 1;
      renderReviews();
    });
  });

  // Search Input Real-time Filtering
  const searchInput = document.getElementById('search-input');
  const searchClearBtn = document.getElementById('search-clear-btn');
  searchInput.addEventListener('input', (e) => {
    currentSearchQuery = e.target.value;
    if (currentSearchQuery) {
      searchClearBtn.classList.remove('hidden');
    } else {
      searchClearBtn.classList.add('hidden');
    }
    activeAccordionId = null;
    currentPage = 1;
    renderReviews();
  });

  searchClearBtn.addEventListener('click', () => {
	  searchInput.value = "";
	  currentSearchQuery = "";
	  searchClearBtn.classList.add('hidden');

	  activeAccordionId = null;
	  currentPage = 1;
	  renderReviews();
  });

  // Sort Dropdown
  // Sort Dropdown
document.getElementById('sort-select').addEventListener('change', (e) => {
    currentSort = e.target.value;

    activeAccordionId = null;
    currentPage = 1;
    renderReviews();
});

  // Lightbox Implementation
  const lightboxModal = document.getElementById('lightbox-modal');
  const lightboxImage = document.getElementById('lightbox-image');
  const lightboxCounter = document.getElementById('lightbox-counter');
  const lightboxClose = document.getElementById('lightbox-close');
  const lightboxPrev = document.getElementById('lightbox-prev');
  const lightboxNext = document.getElementById('lightbox-next');

  window.openLightbox = function(reviewId, imageIndex, event) {
    if (event) event.stopPropagation();
    const item = reviewsData.find(r => r.id === reviewId);
    if (!item || !item.images || item.images.length === 0) return;

    lightboxImages = item.images;
    currentLightboxIndex = imageIndex >= 0 && imageIndex < lightboxImages.length ? imageIndex : 0;
    updateLightboxUI();

    lightboxModal.classList.remove('hidden');
    document.body.style.overflow = 'hidden';
  };

  function updateLightboxUI() {
    if (lightboxImages.length === 0) return;
    lightboxImage.src = lightboxImages[currentLightboxIndex];
    lightboxCounter.innerText = `\${currentLightboxIndex + 1} / \${lightboxImages.length}`;

    if (lightboxImages.length <= 1) {
      lightboxPrev.classList.add('hidden');
      lightboxNext.classList.add('hidden');
    } else {
      lightboxPrev.classList.remove('hidden');
      lightboxNext.classList.remove('hidden');
    }
  }

  function closeLightbox() {
    lightboxModal.classList.add('hidden');
    document.body.style.overflow = '';
  }

  function nextLightbox() {
    if (lightboxImages.length === 0) return;
    currentLightboxIndex = (currentLightboxIndex + 1) % lightboxImages.length;
    updateLightboxUI();
  }

  function prevLightbox() {
    if (lightboxImages.length === 0) return;
    currentLightboxIndex = (currentLightboxIndex - 1 + lightboxImages.length) % lightboxImages.length;
    updateLightboxUI();
  }

  lightboxClose.addEventListener('click', (e) => {
    e.stopPropagation();
    closeLightbox();
  });
  lightboxNext.addEventListener('click', (e) => {
    e.stopPropagation();
    nextLightbox();
  });
  lightboxPrev.addEventListener('click', (e) => {
    e.stopPropagation();
    prevLightbox();
  });

  // Close lightbox on clicking dark backdrop
  lightboxModal.addEventListener('click', (e) => {
    if (e.target === lightboxModal) {
      closeLightbox();
    }
  });

  // Keyboard navigation for lightbox
  window.addEventListener('keydown', (e) => {
    if (lightboxModal.classList.contains('hidden')) return;
    if (e.key === 'Escape') closeLightbox();
    if (e.key === 'ArrowRight') nextLightbox();
    if (e.key === 'ArrowLeft') prevLightbox();
  });

//로그인한 회원인지 확인
  const isLoggedIn = ${not empty sessionScope.loginId ? 'true' : 'false'};
  const loginUrl = '${pageContext.request.contextPath}/Login';
  
  // Modal: Review Form
  const writeModal = document.getElementById('write-modal');
  const openWriteModalBtn = document.getElementById('open-write-modal-btn');
  const closeWriteModalBtn = document.getElementById('close-write-modal');
  const cancelWriteModalBtn = document.getElementById('cancel-write-modal');
  const writeReviewForm = document.getElementById('write-review-form');
  
//null이면 새 글 작성, 숫자이면 해당 리뷰 수정
  let editingReviewId = null;

//리뷰 사진 선택 및 미리보기
  const reviewImageInput = document.getElementById('review-image-input');
  const reviewImagePreview = document.getElementById('review-image-preview');
  const reviewImageError = document.getElementById('review-image-error');

  let selectedReviewImages = [];

  function showReviewImageError(message) {
      reviewImageError.textContent = message;
      reviewImageError.classList.remove('hidden');
  }

  function renderReviewImagePreview() {
      reviewImagePreview.innerHTML = '';

      selectedReviewImages.forEach((item, index) => {
          const box = document.createElement('div');
          box.className = 'relative aspect-square';

          const img = document.createElement('img');
          img.src = item.url;
          img.alt = '첨부 사진 ' + (index + 1);
          img.className = 'w-full h-full object-cover rounded-lg border border-slate-200';

          const removeBtn = document.createElement('button');
          removeBtn.type = 'button';
          removeBtn.textContent = '×';
          removeBtn.className =
              'absolute -top-1 -right-1 w-6 h-6 rounded-full ' +
              'bg-slate-800 text-white text-sm flex items-center justify-center';

          removeBtn.addEventListener('click', () => {
              URL.revokeObjectURL(item.url);
              selectedReviewImages.splice(index, 1);
              reviewImageInput.value = '';
              renderReviewImagePreview();
          });

          box.appendChild(img);
          box.appendChild(removeBtn);
          reviewImagePreview.appendChild(box);
      });
  }

  reviewImageInput.addEventListener('change', function () {
      reviewImageError.classList.add('hidden');

      for (const file of Array.from(this.files)) {
    	  if (	    remainingReviewImages.length +
    			    selectedReviewImages.length >= 5
    			) {
              showReviewImageError('사진은 최대 5장까지 첨부할 수 있습니다.');
              break;
          }

          if (!['image/jpeg', 'image/png', 'image/webp'].includes(file.type)) {
              showReviewImageError('JPG, PNG, WEBP 사진만 첨부할 수 있습니다.');
              continue;
          }

          if (file.size > 5 * 1024 * 1024) {
              showReviewImageError('사진 한 장의 크기는 5MB 이하여야 합니다.');
              continue;
          }

          selectedReviewImages.push({
              file: file,
              url: URL.createObjectURL(file)
          });
      }

      this.value = '';
      renderReviewImagePreview();
  });
  
  let remainingReviewImages = [];

  function showExistingReviewImages(review) {

      const container =
          document.getElementById('existing-review-images');

      container.innerHTML = '';

      // 기존 사진을 복사해서 수정용 목록 생성
      remainingReviewImages = [...(review.images || [])];

      renderExistingReviewImages();
  }

  function renderExistingReviewImages() {

      const container =
          document.getElementById('existing-review-images');

      container.innerHTML = '';

      if (remainingReviewImages.length === 0) {
          container.classList.add('hidden');
          return;
      }

      container.classList.remove('hidden');

      remainingReviewImages.forEach((imageUrl, index) => {

          const box = document.createElement('div');
          box.className = 'relative aspect-square';

          const img = document.createElement('img');
          img.src = imageUrl;
          img.alt = '기존 리뷰 사진';
          img.className =
              'w-full h-full object-cover rounded-lg border border-slate-200';

          const removeBtn = document.createElement('button');
          removeBtn.type = 'button';
          removeBtn.textContent = '×';
          removeBtn.title = '사진 삭제';

          removeBtn.className =
              'absolute -top-1 -right-1 w-6 h-6 rounded-full ' +
              'bg-red-500 hover:bg-red-600 text-white text-sm ' +
              'flex items-center justify-center shadow-md';

          removeBtn.addEventListener('click', function() {

              remainingReviewImages.splice(index, 1);

              renderExistingReviewImages();
          });

          box.appendChild(img);
          box.appendChild(removeBtn);

          container.appendChild(box);
      });
  }
	
  function handleEditAction(reviewId, event) {

	    if (event) {
	        event.preventDefault();
	        event.stopPropagation();
	    }

	    if (!currentMemberId) {
	        alert('로그인이 필요합니다.');
	        return;
	    }

	    const review = reviewsData.find(
	        item => String(item.id) === String(reviewId)
	    );

	    if (!review) {
	        alert('수정할 리뷰를 찾을 수 없습니다.');
	        return;
	    }

	    if (!isReviewAdmin &&
	        String(currentMemberId) !== String(review.memberId)) {
	        alert('본인이 작성한 리뷰만 수정할 수 있습니다.');
	        return;
	    }
	    
      // 수정 상태 저장
      editingReviewId = review.id;

      // 기존 내용 불러오기
      document.getElementById('review-cat-input').value =
          review.category;

      document.getElementById('review-title-input').value =
          review.title;

      document.getElementById('review-content-input').value =
          Array.isArray(review.content)
              ? review.content.join('\n')
              : review.content;
              
      document.getElementById('review-author-input').value =
    	    review.author || '';

      
   	  // 수정창에서도 새 사진 첨부 가능
      document.getElementById('review-image-section')
          .classList.remove('hidden');

      // 기존 사진 표시
      showExistingReviewImages(review);

      // 수정 시 새 사진 선택 초기화
      selectedReviewImages = [];

      // 모달 제목 및 버튼 변경
      writeModal.querySelector('h3').textContent = '여행 리뷰 수정';

      writeReviewForm.querySelector(
          'button[type="submit"]'
      ).textContent = '수정 완료';

      writeModal.classList.remove('hidden');
      document.body.style.overflow = 'hidden';
  }

  

  function openWriteModal() {

      if (!isLoggedIn) {
          if (confirm(
              '리뷰 작성은 로그인한 회원만 가능합니다.\n' +
              '로그인 페이지로 이동하시겠습니까?'
          )) {
              window.location.href = loginUrl;
          }
          return;
      }

      
       // 작성 모드로 초기화
	  editingReviewId = null;
       
	  remainingReviewImages = [];
	
	  // 작성창에서는 사진 첨부 영역 표시
	  document.getElementById('review-image-section')
	    .classList.remove('hidden');
	  
	  document.getElementById('existing-review-images')
	    .classList.add('hidden');
	
	  writeReviewForm.reset();

      selectedReviewImages = [];
      reviewImageInput.value = '';
      renderReviewImagePreview();

      reviewImageError.classList.add('hidden');

      writeModal.querySelector('h3').textContent = '여행 리뷰 작성';

      writeReviewForm.querySelector(
          'button[type="submit"]'
      ).textContent = '리뷰 등록';

      writeModal.classList.remove('hidden');
      document.body.style.overflow = 'hidden';
  }

	//리뷰 작성/수정 모달 닫기
 	 function closeWriteModal() {
      writeModal.classList.add('hidden');
      document.body.style.overflow = '';
      editingReviewId = null;
  }

  // 리뷰 작성하기 버튼
  openWriteModalBtn.addEventListener('click', openWriteModal);

  // 오른쪽 위 X 버튼
  closeWriteModalBtn.addEventListener('click', closeWriteModal);

  // 취소 버튼
  cancelWriteModalBtn.addEventListener('click', closeWriteModal);


  /**
   * 리뷰 작성 / 수정 폼 제출
   */

   writeReviewForm.addEventListener('submit', async function(e) {
       e.preventDefault();

       if (!isLoggedIn) {
           alert('로그인이 필요합니다.');
           return;
       }

       const isEditMode = editingReviewId !== null;

       const category = document.getElementById('review-cat-input').value;
       const title = document.getElementById('review-title-input').value.trim();
       const content = document.getElementById('review-content-input').value.trim();
       const authorNickname = document.getElementById('review-author-input').value.trim();

       if (!category || !title || !content || !authorNickname) {
           alert('카테고리, 제목, 닉네임, 내용을 모두 입력해 주세요.');
           return;
       }

       if (remainingReviewImages.length + selectedReviewImages.length > 5) {
           alert('사진은 최대 5장까지 첨부할 수 있습니다.');
           return;
       }

       const submitButton = writeReviewForm.querySelector('button[type="submit"]');
       submitButton.disabled = true;

       try {
           const formData = new FormData();

           formData.append('category', category);
           formData.append('title', title);
           formData.append('content', content);
           formData.append('authorNickname', authorNickname);

           let requestUrl;

           if (isEditMode) {
               requestUrl = '${pageContext.request.contextPath}/ReviewUpdate';

               formData.append('reviewId', editingReviewId);

               // 삭제하지 않고 유지할 기존 사진
               const contextPath = '${pageContext.request.contextPath}';

               remainingReviewImages.forEach(imageUrl => {
                   const imagePath = imageUrl.startsWith(contextPath + '/')
                       ? imageUrl.substring(contextPath.length)
                       : imageUrl;

                   formData.append('keepImages', imagePath);
               });

           } else {
               requestUrl = '${pageContext.request.contextPath}/ReviewWrite';
           }

           // 새롭게 선택한 사진
           selectedReviewImages.forEach(item => {
               formData.append('images', item.file);
           });

           const response = await fetch(requestUrl, {
               method: 'POST',
               body: formData
           });

           if (response.status === 401) {
               alert('로그인이 필요합니다.');
               return;
           }

           if (response.status === 403) {
               alert('작업 권한이 없습니다.');
               return;
           }

           if (!response.ok) {
               throw new Error('HTTP ' + response.status);
           }

           alert(isEditMode
               ? '리뷰가 수정되었습니다.'
               : '리뷰가 등록되었습니다.'
           );

           window.location.href =
               '${pageContext.request.contextPath}/Review';

       } catch (error) {
           console.error('[Review] 저장 오류:', error);
           alert('리뷰 저장 중 오류가 발생했습니다.');

       } finally {
           submitButton.disabled = false;
       }
   });


//========================================
//리뷰 삭제
//========================================
async function handleDeleteAction(reviewId, event) {

   if (event) {
       event.preventDefault();
       event.stopPropagation();
   }

   if (!currentMemberId) {
       alert('로그인이 필요합니다.');
       return;
   }

   const review = reviewsData.find(
       item => String(item.id) === String(reviewId)
   );

   if (!review) {
       alert('삭제할 리뷰를 찾을 수 없습니다.');
       return;
   }

   if (!isReviewAdmin &&
       String(currentMemberId) !== String(review.memberId)) {
       alert('본인이 작성한 리뷰만 삭제할 수 있습니다.');
       return;
   }

   if (!confirm('정말 이 리뷰를 삭제하시겠습니까?')) {
       return;
   }

   try {
       const params = new URLSearchParams();
       params.append('reviewId', reviewId);

       const response = await fetch(
           '${pageContext.request.contextPath}/ReviewDelete',
           {
               method: 'POST',
               headers: {
                   'Content-Type':
                       'application/x-www-form-urlencoded;charset=UTF-8'
               },
               body: params.toString()
           }
       );

       if (response.status === 401) {
           alert('로그인이 필요합니다.');
           return;
       }

       if (response.status === 403) {
           alert('삭제 권한이 없습니다.');
           return;
       }

       if (!response.ok) {
           throw new Error('HTTP ' + response.status);
       }

       alert('리뷰가 삭제되었습니다.');

       window.location.href =
           '${pageContext.request.contextPath}/Review';

   } catch (error) {
       console.error('[Review] 삭제 오류:', error);
       alert('리뷰 삭제 중 오류가 발생했습니다.');
   }
}
  
	//=========================================================
	//주제: 마이페이지에서 선택한 리뷰 자동 열기
	//설명: /Review?reviewId=번호 로 들어오면 해당 리뷰가 있는 페이지를 열고 상세내용을 펼침
	//=========================================================
	function openRequestedReview(){
	  const params=new URLSearchParams(window.location.search);
	  const requestedId=Number(params.get("reviewId"));
	  if(!requestedId)return;

	  const list=getFilteredAndSortedReviews();
	  const index=list.findIndex(item=>Number(item.id)===requestedId);
	  if(index<0)return;

	  currentPage=Math.floor(index/reviewsPerPage)+1;
	  activeAccordionId=requestedId;
	  renderReviews();

	  setTimeout(function(){
	    const card=document.querySelector('.accordion-item[data-id="'+requestedId+'"]');
	    if(card)card.scrollIntoView({behavior:"smooth",block:"center"});
	    loadReviewComments(requestedId);
	  },100);
	}

	//페이지 최초 진입 시 DB에서 조회한 리뷰 출력
	console.log("[Review] DB 리뷰 데이터:",dbReviews);
	console.log("[Review] 화면 출력 리뷰 개수:",reviewsData.length);

	renderReviews();
	openRequestedReview();
  
</script>

	<jsp:include page="/WEB-INF/liveChat/chat.jsp" />

	<jsp:include page="/WEB-INF/bottom.jsp" />

</body>
