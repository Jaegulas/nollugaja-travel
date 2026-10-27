<%-- =========================================================
     주제: 파일 역할
     설명: travelDestination 영역의 travelDestination 화면을 구성하고 서버에서 전달받은 데이터를 표시
========================================================= --%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>

<!DOCTYPE html>
<html lang="ko" style="">
<head>

<meta charset="UTF-8">

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<meta content="width=device-width, initial-scale=1.0" name="viewport">

<title>여행지 탐색 - 대한민국 구석구석 새로운 발견</title>

<!-- Google Fonts: Noto Sans KR, Plus Jakarta Sans & Material Symbols Outlined -->

<link href="https://fonts.googleapis.com" rel="preconnect">

<link crossorigin="" href="https://fonts.gstatic.com" rel="preconnect">

<link
	href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;600;700&family=Plus+Jakarta+Sans:wght@500;600;700;800&display=swap"
	rel="stylesheet">

<link
	href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200"
	rel="stylesheet">

<!-- Tailwind CSS CDN -->

<script src="https://cdn.tailwindcss.com"></script>

<script>

    tailwind.config = {

      theme: {

        extend: {

          colors: {

            brand: {

              primary: '#006574',

              'primary-container': '#008092',

              'primary-light': '#e0f4f7',

              bg: '#faf8ff',

              surface: '#ffffff',

              'surface-low': '#f2f3ff',

              text: '#131b2e',

              'sub-text': '#3e494b',

              border: '#e2e5eb'

            }

          },

          fontFamily: {

            sans: ['"Noto Sans KR"', '"Plus Jakarta Sans"', 'sans-serif'],

            display: ['"Plus Jakarta Sans"', '"Noto Sans KR"', 'sans-serif']

          },

          boxShadow: {

            'card-subtle': '0 4px 20px -2px rgba(19, 27, 46, 0.05), 0 2px 6px -1px rgba(19, 27, 46, 0.03)',

            'card-hover': '0 12px 30px -4px rgba(0, 101, 116, 0.12), 0 4px 12px -2px rgba(19, 27, 46, 0.06)'

          }

        }

      }

    }

  </script>

<style>

/* Material symbols styling */
.material-symbols-outlined {
	font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
	display: inline-block;
	vertical-align: middle;
	line-height: 1;
}

.material-symbols-outlined.fill-active {
	font-variation-settings: 'FILL' 1, 'wght' 400, 'GRAD' 0, 'opsz' 24;
}

/* Smooth card hover transitions */
.destination-card {
	transition: transform 0.25s cubic-bezier(0.16, 1, 0.3, 1), box-shadow
		0.25s cubic-bezier(0.16, 1, 0.3, 1), border-color 0.25s ease;
}

.destination-card:hover {
	transform: translateY(-4px);
}

.destination-card:hover .card-img {
	transform: scale(1.04);
}

/* Scrollbar hide for horizontal pill scrolling on mobile */
.no-scrollbar::-webkit-scrollbar {
	display: none;
}

.no-scrollbar {
	-ms-overflow-style: none;
	scrollbar-width: none;
}

/* 여행지 메인 배너 */

/* 여행지 메인 배너 */

/* 여행지 상단 배너: 프로젝트 컨텍스트 경로를 자동으로 사용 */
#hero-section.destination-hero {
	min-height: 350px;
	display: flex;
	align-items: center;
	justify-content: center;
	background-image: linear-gradient(rgba(5, 20, 35, .45),
		rgba(5, 20, 35, .65)),
		url('${pageContext.request.contextPath}/resources/images/banner/travel-banner.png')
		!important;
	background-position: center center !important;
	background-size: cover !important;
	background-repeat: no-repeat !important;
	border: 0;
}

#hero-section.destination-hero h1, #hero-section.destination-hero p,
	#hero-section.destination-hero h1 span, #hero-section.destination-hero p span
	{
	color: #fff !important;
}

#hero-section.destination-hero>.absolute {
	display: none;
}

#hero-section.destination-hero>.relative {
	width: 100%;
}

@media ( max-width : 640px) {
	#hero-section.destination-hero {
		min-height: 310px;
	}
}

/* 로그인 전 찜하기 공통 모달 */
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

</head>

<body
	class="bg-[#faf8ff] text-[#131b2e] font-sans antialiased min-h-screen">

	<!-- 상단 헤더 / 네비게이션 가상 include 표현 -->

	<!-- 메인 컨테이너 (최대 1320px) -->

	<main
		class="w-full max-w-[1320px] mx-auto px-4 sm:px-6 lg:px-8 py-6 lg:py-10 space-y-12 lg:space-y-16">

		<!-- 1. HERO / INTRO SECTION -->

		<section
			class="destination-hero relative rounded-3xl px-6 py-12 sm:px-10 text-center overflow-hidden"
			id="hero-section">

			<!-- 은은한 배경 데코 서클 -->

			<div
				class="absolute -top-24 -left-24 w-72 h-72 rounded-full bg-[#006574]/5 blur-3xl pointer-events-none"></div>

			<div
				class="absolute -bottom-20 -right-20 w-80 h-80 rounded-full bg-[#008092]/8 blur-3xl pointer-events-none"></div>

			<div class="relative z-10 max-w-2xl mx-auto space-y-4">

				<!-- 뱃지 -->

				<div
					class="inline-flex items-center gap-1.5 px-3.5 py-1.5 rounded-full bg-white/90 border border-[#006574]/20 shadow-sm text-xs font-semibold text-[#006574]">

					<span class="material-symbols-outlined text-[16px]">explore</span>

					<span class="translate tracking-wide">대한민국 구석구석 새로운 발견</span>

				</div>

				<!-- 큰 제목 -->

				<h1
					class="text-3xl sm:text-4xl lg:text-[42px] font-extrabold tracking-tight text-[#131b2e] leading-tight">

					<span class="translate">오늘은 어디로 떠나볼까요?</span>

				</h1>

				<!-- 설명 문구 -->

				<p class="text-sm sm:text-base text-[#3e494b] leading-relaxed">

					<span class="translate">지역과 테마를 선택하고 나에게 맞는 대한민국 여행지를
						발견해보세요.</span>

				</p>

				<!-- 넓은 검색창 UI -->

				<div class="pt-2 sm:pt-4">

					<form class="relative max-w-xl mx-auto" id="destinationSearchForm"
						onsubmit="event.preventDefault();">

						<div
							class="flex items-center bg-white rounded-2xl p-2 pl-4 shadow-card-subtle border border-[#006574]/25 focus-within:border-[#006574] focus-within:ring-4 focus-within:ring-[#006574]/10 transition-all">

							<span
								class="material-symbols-outlined text-[#3e494b] text-[22px] mr-2">search</span>

							<input
								class="w-full text-sm sm:text-base text-[#131b2e] placeholder-[#3e494b]/60 bg-transparent outline-none pr-3"
								id="keywordInput" name="keyword"
								placeholder="여행지, 지역 또는 테마를 검색해보세요" type="text">

							<button
								class="flex items-center justify-center gap-1 bg-[#006574] hover:bg-[#00525e] text-white px-5 sm:px-6 py-2.5 rounded-xl font-medium text-sm transition-colors duration-200 shadow-sm shrink-0"
								id="searchBtn" type="submit">

								<span class="material-symbols-outlined text-[18px]">search</span>

								<span class="translate hidden sm:inline">검색</span>

							</button>

						</div>

					</form>

				</div>

			</div>

		</section>

		<!-- 2. REGION EXPLORER (대한민국 주요 지역 선택) -->

		<section class="space-y-4" id="region-explorer-section">

			<div class="flex items-center justify-between">

				<div class="flex items-center gap-2">

					<span class="w-1.5 h-5 bg-[#006574] rounded-full"></span>

					<h2
						class="text-xl sm:text-2xl font-bold tracking-tight text-[#131b2e]">

						<span class="translate">어디로 떠나볼까요?</span>

					</h2>

				</div>

			</div>

			<!-- 지역 Pill 필터 리스트 -->

			<div
				class="flex items-center gap-2 overflow-x-auto no-scrollbar py-1 text-sm font-medium"
				id="regionPillContainer">

				<button
					class="region-pill px-5 py-2.5 rounded-full bg-[#006574] text-white shadow-sm hover:opacity-95 transition-all whitespace-nowrap flex items-center gap-1.5 font-semibold"
					data-area-code="all" type="button">

					<span class="material-symbols-outlined text-[16px]">travel_explore</span>

					<span class="translate">전체</span>

				</button>

				<button
					class="region-pill px-5 py-2.5 rounded-full bg-white border border-[#e2e5eb] text-[#3e494b] hover:border-[#006574]/40 hover:bg-[#f2f3ff] transition-all whitespace-nowrap"
					data-area-code="1" type="button">

					<span class="translate">서울</span>

				</button>

				<button
					class="region-pill px-5 py-2.5 rounded-full bg-white border border-[#e2e5eb] text-[#3e494b] hover:border-[#006574]/40 hover:bg-[#f2f3ff] transition-all whitespace-nowrap"
					data-area-code="31" type="button">

					<span class="translate">경기·인천</span>

				</button>

				<button
					class="region-pill px-5 py-2.5 rounded-full bg-white border border-[#e2e5eb] text-[#3e494b] hover:border-[#006574]/40 hover:bg-[#f2f3ff] transition-all whitespace-nowrap"
					data-area-code="32" type="button">

					<span class="translate">강원</span>

				</button>

				<button
					class="region-pill px-5 py-2.5 rounded-full bg-white border border-[#e2e5eb] text-[#3e494b] hover:border-[#006574]/40 hover:bg-[#f2f3ff] transition-all whitespace-nowrap"
					data-area-code="3" type="button">

					<span class="translate">충청·대전·세종</span>

				</button>

				<button
					class="region-pill px-5 py-2.5 rounded-full bg-white border border-[#e2e5eb] text-[#3e494b] hover:border-[#006574]/40 hover:bg-[#f2f3ff] transition-all whitespace-nowrap"
					data-area-code="5" type="button">

					<span class="translate">전라·광주</span>

				</button>

				<button
					class="region-pill px-5 py-2.5 rounded-full bg-white border border-[#e2e5eb] text-[#3e494b] hover:border-[#006574]/40 hover:bg-[#f2f3ff] transition-all whitespace-nowrap"
					data-area-code="6" type="button">

					<span class="translate">경상·부산·대구·울산</span>

				</button>

				<button
					class="region-pill px-5 py-2.5 rounded-full bg-white border border-[#e2e5eb] text-[#3e494b] hover:border-[#006574]/40 hover:bg-[#f2f3ff] transition-all whitespace-nowrap"
					data-area-code="39" type="button">

					<span class="translate">제주</span>

				</button>

			</div>

		</section>

		<!-- 3. FEATURED DESTINATIONS (지금 떠나기 좋은 여행지) -->

		<section class="space-y-4" id="featured-section">

			<div class="flex items-center justify-between">

				<div class="flex items-center gap-2">

					<span class="w-1.5 h-5 bg-[#006574] rounded-full"></span>

					<h2
						class="text-xl sm:text-2xl font-bold tracking-tight text-[#131b2e]">

						<span class="translate">지금 떠나기 좋은 여행지</span>

					</h2>

				</div>

			</div>

			<div class="grid grid-cols-1 lg:grid-cols-12 gap-5 lg:gap-6"
				id="featuredDestinationContainer">

				<!-- 고정 카드는 사용하지 않음 -->

				<!-- TourAPI 응답 후 renderFeaturedDestinations()가 실제 여행지 3개를 생성 -->

				<div class="lg:col-span-12 py-16 text-center" id="featuredLoading">

					<span
						class="material-symbols-outlined text-5xl text-[#006574]/30 animate-pulse">

						travel_explore </span>

					<p class="mt-3 text-sm text-[#3e494b]">추천 여행지를 불러오고 있습니다.</p>

				</div>

			</div>

		</section>

		<!-- 4. DESTINATION FILTER (실제 여행지 탐색 메인 필터바) -->

		<section class="pt-2 border-t border-[#e2e5eb]" id="filter-section">

			<div class="space-y-4">

				<!-- 상단 헤더: 제목 + 카운트 + 정렬 셀렉트 -->

				<div
					class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">

					<div>

						<div class="flex items-center gap-2">

							<span class="w-1.5 h-5 bg-[#006574] rounded-full"></span>

							<h2 class="text-2xl font-bold tracking-tight text-[#131b2e]">

								<span class="translate">대한민국 여행지</span>

							</h2>

						</div>

						<p class="text-xs sm:text-sm text-[#3e494b] mt-1 pl-3.5">

							<span class="translate">총 <strong
								class="text-[#006574] font-semibold" id="totalCount">1,248</strong>개의
								여행지를 만나보세요
							</span>

						</p>

					</div>

				</div>

				<!-- 하단 지역/테마 필터 Chips -->

				<div
					class="flex items-center gap-2 overflow-x-auto no-scrollbar py-1"
					id="themeChipsContainer">
					<button
						class="theme-chip px-5 py-2 rounded-full text-xs sm:text-sm font-semibold bg-[#006574] text-white border border-[#006574] shadow-sm transition-all whitespace-nowrap"
						data-area-code="all" type="button">
						<span class="translate">전체 지역</span>
					</button>
					<button
						class="theme-chip px-5 py-2 rounded-full text-xs sm:text-sm font-medium bg-white text-[#3e494b] border border-[#e2e5eb] hover:bg-[#f2f3ff] hover:text-[#006574] transition-all whitespace-nowrap"
						data-area-code="1" type="button">
						<span class="translate">서울</span>
					</button>
					<button
						class="theme-chip px-5 py-2 rounded-full text-xs sm:text-sm font-medium bg-white text-[#3e494b] border border-[#e2e5eb] hover:bg-[#f2f3ff] hover:text-[#006574] transition-all whitespace-nowrap"
						data-area-code="31" type="button">
						<span class="translate">경기·인천</span>
					</button>
					<button
						class="theme-chip px-5 py-2 rounded-full text-xs sm:text-sm font-medium bg-white text-[#3e494b] border border-[#e2e5eb] hover:bg-[#f2f3ff] hover:text-[#006574] transition-all whitespace-nowrap"
						data-area-code="32" type="button">
						<span class="translate">강원</span>
					</button>
					<button
						class="theme-chip px-5 py-2 rounded-full text-xs sm:text-sm font-medium bg-white text-[#3e494b] border border-[#e2e5eb] hover:bg-[#f2f3ff] hover:text-[#006574] transition-all whitespace-nowrap"
						data-area-code="3" type="button">
						<span class="translate">충청·대전·세종</span>
					</button>
					<button
						class="theme-chip px-5 py-2 rounded-full text-xs sm:text-sm font-medium bg-white text-[#3e494b] border border-[#e2e5eb] hover:bg-[#f2f3ff] hover:text-[#006574] transition-all whitespace-nowrap"
						data-area-code="5" type="button">
						<span class="translate">전라·광주</span>
					</button>
					<button
						class="theme-chip px-5 py-2 rounded-full text-xs sm:text-sm font-medium bg-white text-[#3e494b] border border-[#e2e5eb] hover:bg-[#f2f3ff] hover:text-[#006574] transition-all whitespace-nowrap"
						data-area-code="6" type="button">
						<span class="translate">경상·부산·대구·울산</span>
					</button>
					<button
						class="theme-chip px-5 py-2 rounded-full text-xs sm:text-sm font-medium bg-white text-[#3e494b] border border-[#e2e5eb] hover:bg-[#f2f3ff] hover:text-[#006574] transition-all whitespace-nowrap"
						data-area-code="39" type="button">
						<span class="translate">제주</span>
					</button>
				</div>

			</div>

		</section>

		<!-- 5. DESTINATION CARD GRID -->

		<section id="card-grid-section">

			<div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6"
				id="destinationListContainer">

				<!-- CARD 1: 제주 성산일출봉 -->

				<article
					class="destination-card bg-white rounded-[20px] border border-[#e2e5eb] overflow-hidden shadow-card-subtle hover:border-[#006574]/30 flex flex-col group cursor-pointer"
					data-content-id="126508">

					<div
						class="relative w-full aspect-[4/3] overflow-hidden bg-slate-100">

						<img alt="성산일출봉 전경"
							class="card-img w-full h-full object-cover transition-transform duration-500 ease-out"
							loading="lazy"
							src="https://lh3.googleusercontent.com/aida-public/AB6AXuD-DJ-p5C4lDBPQg4ZEFgw67V532qep7a8_x9Pa5biLOakIimHKx3S0RI_E64XaEYsfEdUF-cK5jRRDTA1jLiTvsLDqbfIgiIjy51Ny9B-JWNE_Fg4yfUEAlw_QPDib7MCr9TAWOTIvJIZu3lhOmvHeHICqpjVlEyf8-E26NAhvYiD59YxdlYUn2pn2TUHqOO9DbI3Hw7zGX84Lva00GFGEHTesPiAeOgo_1B7LPyW7NUNQz5L9kOqY0g">

						<div class="absolute top-3 left-3">

							<span
								class="px-2.5 py-1 rounded-full bg-white/90 backdrop-blur-sm text-xs font-semibold text-[#006574] shadow-sm translate">

								자연 </span>

						</div>

						<button
							class="like-btn absolute top-3 right-3 w-8 h-8 rounded-full bg-white/80 hover:bg-white backdrop-blur-sm text-[#131b2e] hover:text-red-500 flex items-center justify-center transition-colors shadow-sm"
							title="찜하기" type="button">

							<span class="material-symbols-outlined text-[18px]">favorite</span>

						</button>

					</div>

					<div class="p-5 flex-1 flex flex-col justify-between space-y-3">

						<div class="space-y-1.5">

							<div
								class="flex items-center gap-1 text-[11px] text-[#3e494b] font-medium">

								<span
									class="material-symbols-outlined text-[13px] text-[#006574]">location_on</span>

								<span class="translate truncate">제주특별자치도 서귀포시</span>

							</div>

							<h3
								class="text-base font-bold text-[#131b2e] group-hover:text-[#006574] transition-colors leading-snug">

								<span class="translate">성산일출봉</span>

							</h3>

							<p class="text-xs text-[#3e494b] line-clamp-2 leading-relaxed">

								<span class="translate">바다 위로 솟아오른 거대한 분화구와 푸른 초원이 어우러진
									일출 명소</span>

							</p>

						</div>

						<div
							class="pt-3 border-t border-[#f2f3ff] flex items-center justify-between text-xs">

							<div class="flex items-center gap-1 text-amber-500 font-semibold">

								<span class="material-symbols-outlined text-[15px] fill-active">star</span>

								<span class="">4.8</span> <span
									class="text-[#3e494b] font-normal text-[11px] ml-0.5">(1,245)</span>

							</div>

							<a
								class="text-[#006574] font-medium hover:underline inline-flex items-center gap-0.5 text-xs group-hover:translate-x-0.5 transition-transform"
								href="javascript:void(0)"> <span class="translate">자세히
									보기</span> →

							</a>

						</div>

					</div>

				</article>

				<!-- CARD 2: 서울 경복궁 -->

				<article
					class="destination-card bg-white rounded-[20px] border border-[#e2e5eb] overflow-hidden shadow-card-subtle hover:border-[#006574]/30 flex flex-col group cursor-pointer"
					data-content-id="126509">

					<div
						class="relative w-full aspect-[4/3] overflow-hidden bg-slate-100">

						<img alt="경복궁 향원정 가을 풍경"
							class="card-img w-full h-full object-cover transition-transform duration-500 ease-out"
							loading="lazy"
							src="https://lh3.googleusercontent.com/aida-public/AB6AXuDUeyAA9o755Nf6NotInmxkBy74Uylc0HRWDfcEszRwlOBZdC_0UY8G3Bn9rYOzaFKjxGz3u_clE6Aa6NEpGjRPEuKnL9MZWuyOAMCoAYHPPOqOV6idYNhiW12uCjdo2Zz_miLXeCusC_NqfMp8mVXwcYzbxsGlWbAIMlymwiAEg13XkwGEfRmzrzYNFrNLAcCCYB6oo0J9PYTfP_PwHR9f_hmXl_RFlVtX_ye6WfQGLbyPzVFMbRAmRw">

						<div class="absolute top-3 left-3">

							<span
								class="px-2.5 py-1 rounded-full bg-white/90 backdrop-blur-sm text-xs font-semibold text-[#006574] shadow-sm translate">

								역사 </span>

						</div>

						<button
							class="like-btn absolute top-3 right-3 w-8 h-8 rounded-full bg-white/80 hover:bg-white backdrop-blur-sm text-[#131b2e] hover:text-red-500 flex items-center justify-center transition-colors shadow-sm"
							title="찜하기" type="button">

							<span class="material-symbols-outlined text-[18px]">favorite</span>

						</button>

					</div>

					<div class="p-5 flex-1 flex flex-col justify-between space-y-3">

						<div class="space-y-1.5">

							<div
								class="flex items-center gap-1 text-[11px] text-[#3e494b] font-medium">

								<span
									class="material-symbols-outlined text-[13px] text-[#006574]">location_on</span>

								<span class="translate truncate">서울특별시 종로구</span>

							</div>

							<h3
								class="text-base font-bold text-[#131b2e] group-hover:text-[#006574] transition-colors leading-snug">

								<span class="translate">경복궁</span>

							</h3>

							<p class="text-xs text-[#3e494b] line-clamp-2 leading-relaxed">

								<span class="translate">조선 왕조 500년의 역사를 간직한 으뜸 궁궐이자 도심 속
									문화 휴식처</span>

							</p>

						</div>

						<div
							class="pt-3 border-t border-[#f2f3ff] flex items-center justify-between text-xs">

							<div class="flex items-center gap-1 text-amber-500 font-semibold">

								<span class="material-symbols-outlined text-[15px] fill-active">star</span>

								<span class="">4.9</span> <span
									class="text-[#3e494b] font-normal text-[11px] ml-0.5">(3,820)</span>

							</div>

							<a
								class="text-[#006574] font-medium hover:underline inline-flex items-center gap-0.5 text-xs group-hover:translate-x-0.5 transition-transform"
								href="javascript:void(0)"> <span class="translate">자세히
									보기</span> →

							</a>

						</div>

					</div>

				</article>

				<!-- CARD 3: 부산 감천문화마을 -->

				<article
					class="destination-card bg-white rounded-[20px] border border-[#e2e5eb] overflow-hidden shadow-card-subtle hover:border-[#006574]/30 flex flex-col group cursor-pointer"
					data-content-id="126510">

					<div
						class="relative w-full aspect-[4/3] overflow-hidden bg-slate-100">

						<img alt="부산 감천문화마을 전경"
							class="card-img w-full h-full object-cover transition-transform duration-500 ease-out"
							loading="lazy"
							src="https://lh3.googleusercontent.com/aida-public/AB6AXuDiJd3mSJQow7r8Tur_KFkxmjZtMnGV76QGBXLW9S_H1uhMsmVqUHwHhPgggc3XdSN8ZPs46St-Ph1HDUBOkNgLISqLaHgF-3b6e_sjQnRHmfAzto0wd98wxCHyKCrB6rGSjm3I8o_SgQsYfLoitYlruMEz2n7sJWYn5ttE05XxjYwgDuZNBn72KrvhX9Aif2-0aUREVIQNZ8BsYDdhOtP46Soi5OrpG8kQJArridKfZcYuf2_P2k3rfA">

						<div class="absolute top-3 left-3">

							<span
								class="px-2.5 py-1 rounded-full bg-white/90 backdrop-blur-sm text-xs font-semibold text-[#006574] shadow-sm translate">

								문화 </span>

						</div>

						<button
							class="like-btn absolute top-3 right-3 w-8 h-8 rounded-full bg-white/80 hover:bg-white backdrop-blur-sm text-[#131b2e] hover:text-red-500 flex items-center justify-center transition-colors shadow-sm"
							title="찜하기" type="button">

							<span class="material-symbols-outlined text-[18px]">favorite</span>

						</button>

					</div>

					<div class="p-5 flex-1 flex flex-col justify-between space-y-3">

						<div class="space-y-1.5">

							<div
								class="flex items-center gap-1 text-[11px] text-[#3e494b] font-medium">

								<span
									class="material-symbols-outlined text-[13px] text-[#006574]">location_on</span>

								<span class="translate truncate">부산광역시 사하구</span>

							</div>

							<h3
								class="text-base font-bold text-[#131b2e] group-hover:text-[#006574] transition-colors leading-snug">

								<span class="translate">감천문화마을</span>

							</h3>

							<p class="text-xs text-[#3e494b] line-clamp-2 leading-relaxed">

								<span class="translate">한국의 산토리니로 불리는 알록달록 계단식 벽화 골목 예술
									마을</span>

							</p>

						</div>

						<div
							class="pt-3 border-t border-[#f2f3ff] flex items-center justify-between text-xs">

							<div class="flex items-center gap-1 text-amber-500 font-semibold">

								<span class="material-symbols-outlined text-[15px] fill-active">star</span>

								<span class="">4.7</span> <span
									class="text-[#3e494b] font-normal text-[11px] ml-0.5">(2,110)</span>

							</div>

							<a
								class="text-[#006574] font-medium hover:underline inline-flex items-center gap-0.5 text-xs group-hover:translate-x-0.5 transition-transform"
								href="javascript:void(0)"> <span class="translate">자세히
									보기</span> →

							</a>

						</div>

					</div>

				</article>

				<!-- CARD 4: 순천만습지 -->

				<article
					class="destination-card bg-white rounded-[20px] border border-[#e2e5eb] overflow-hidden shadow-card-subtle hover:border-[#006574]/30 flex flex-col group cursor-pointer"
					data-content-id="126511">

					<div
						class="relative w-full aspect-[4/3] overflow-hidden bg-slate-100">

						<img alt="순천만습지 갈대밭"
							class="card-img w-full h-full object-cover transition-transform duration-500 ease-out"
							loading="lazy"
							src="https://lh3.googleusercontent.com/aida-public/AB6AXuBeqTYAn40wyoJQ3eJPS293Ug6rYOP2j0q3VmqEvez3CgDwfoVBmu_ouJ0qh2emEUzZoQRHd2NfLBYnZmFQsYfm-3ivMYxTmNUmAQvBpzUdFOgvIcxFhjyYP3Ud4OKowWgC53W_NQnYqSBakZYmYqREYltkwR31oxFuy7uabGQ94C2yLjz6S0229EUUM0IXL4hI-aZi3GmYL1wF4d9XuS__dz92tRejUStBbyCBTY9rR-G47Blf57-orw">

						<div class="absolute top-3 left-3">

							<span
								class="px-2.5 py-1 rounded-full bg-white/90 backdrop-blur-sm text-xs font-semibold text-[#006574] shadow-sm translate">

								자연 </span>

						</div>

						<button
							class="like-btn absolute top-3 right-3 w-8 h-8 rounded-full bg-white/80 hover:bg-white backdrop-blur-sm text-[#131b2e] hover:text-red-500 flex items-center justify-center transition-colors shadow-sm"
							title="찜하기" type="button">

							<span class="material-symbols-outlined text-[18px]">favorite</span>

						</button>

					</div>

					<div class="p-5 flex-1 flex flex-col justify-between space-y-3">

						<div class="space-y-1.5">

							<div
								class="flex items-center gap-1 text-[11px] text-[#3e494b] font-medium">

								<span
									class="material-symbols-outlined text-[13px] text-[#006574]">location_on</span>

								<span class="translate truncate">전라남도 순천시</span>

							</div>

							<h3
								class="text-base font-bold text-[#131b2e] group-hover:text-[#006574] transition-colors leading-snug">

								<span class="translate">순천만 국가정원 & 습지</span>

							</h3>

							<p class="text-xs text-[#3e494b] line-clamp-2 leading-relaxed">

								<span class="translate">황금빛 갈대밭과 흑두루미가 반기는 세계 5대 연안습지 생태
									보고</span>

							</p>

						</div>

						<div
							class="pt-3 border-t border-[#f2f3ff] flex items-center justify-between text-xs">

							<div class="flex items-center gap-1 text-amber-500 font-semibold">

								<span class="material-symbols-outlined text-[15px] fill-active">star</span>

								<span class="">4.8</span> <span
									class="text-[#3e494b] font-normal text-[11px] ml-0.5">(1,580)</span>

							</div>

							<a
								class="text-[#006574] font-medium hover:underline inline-flex items-center gap-0.5 text-xs group-hover:translate-x-0.5 transition-transform"
								href="javascript:void(0)"> <span class="translate">자세히
									보기</span> →

							</a>

						</div>

					</div>

				</article>

				<!-- CARD 5: 전주 한옥마을 -->

				<article
					class="destination-card bg-white rounded-[20px] border border-[#e2e5eb] overflow-hidden shadow-card-subtle hover:border-[#006574]/30 flex flex-col group cursor-pointer"
					data-content-id="126512">

					<div
						class="relative w-full aspect-[4/3] overflow-hidden bg-slate-100">

						<img alt="전주 한옥마을 골목길"
							class="card-img w-full h-full object-cover transition-transform duration-500 ease-out"
							loading="lazy"
							src="https://lh3.googleusercontent.com/aida-public/AB6AXuC4I5Tugp8FGbd2Ukw0rBNry7dtTZZhzPY0MZ6N65y61m3Cg6CiMDsIgMxbWmbztg8pfBi3s5a4MwdqrRnSw6PkBXzdQZPooK5TG7lYcN3ppfxtzAE3vwTM7VVm_kiuoWjIdKshwqFq106m9JL8PuczfKr0ROKy7XP6KjsSXVco0Mu11OVoME1iw8WOs7pEzD4AcP4ng1UIOoTNLBH18vb5VtJv1yvAxkpNJx0SW6IHJw9WG9eIBlvH-Q">

						<div class="absolute top-3 left-3">

							<span
								class="px-2.5 py-1 rounded-full bg-white/90 backdrop-blur-sm text-xs font-semibold text-[#006574] shadow-sm translate">

								문화 </span>

						</div>

						<button
							class="like-btn absolute top-3 right-3 w-8 h-8 rounded-full bg-white/80 hover:bg-white backdrop-blur-sm text-[#131b2e] hover:text-red-500 flex items-center justify-center transition-colors shadow-sm"
							title="찜하기" type="button">

							<span class="material-symbols-outlined text-[18px]">favorite</span>

						</button>

					</div>

					<div class="p-5 flex-1 flex flex-col justify-between space-y-3">

						<div class="space-y-1.5">

							<div
								class="flex items-center gap-1 text-[11px] text-[#3e494b] font-medium">

								<span
									class="material-symbols-outlined text-[13px] text-[#006574]">location_on</span>

								<span class="translate truncate">전북특별자치도 전주시</span>

							</div>

							<h3
								class="text-base font-bold text-[#131b2e] group-hover:text-[#006574] transition-colors leading-snug">

								<span class="translate">전주 한옥마을</span>

							</h3>

							<p class="text-xs text-[#3e494b] line-clamp-2 leading-relaxed">

								<span class="translate">한국 전통 기와지붕의 아름다움과 풍성한 전통 먹거리를 즐기는
									슬로시티</span>

							</p>

						</div>

						<div
							class="pt-3 border-t border-[#f2f3ff] flex items-center justify-between text-xs">

							<div class="flex items-center gap-1 text-amber-500 font-semibold">

								<span class="material-symbols-outlined text-[15px] fill-active">star</span>

								<span class="">4.6</span> <span
									class="text-[#3e494b] font-normal text-[11px] ml-0.5">(4,200)</span>

							</div>

							<a
								class="text-[#006574] font-medium hover:underline inline-flex items-center gap-0.5 text-xs group-hover:translate-x-0.5 transition-transform"
								href="javascript:void(0)"> <span class="translate">자세히
									보기</span> →

							</a>

						</div>

					</div>

				</article>

				<!-- CARD 6: 설악산 국립공원 -->

				<article
					class="destination-card bg-white rounded-[20px] border border-[#e2e5eb] overflow-hidden shadow-card-subtle hover:border-[#006574]/30 flex flex-col group cursor-pointer"
					data-content-id="126513">

					<div
						class="relative w-full aspect-[4/3] overflow-hidden bg-slate-100">

						<img alt="설악산 공룡능선과 울산바위"
							class="card-img w-full h-full object-cover transition-transform duration-500 ease-out"
							loading="lazy"
							src="https://lh3.googleusercontent.com/aida-public/AB6AXuBB2SwO4rZw79_nhKRfKfVkbQk7TMkVgZ1vHO63cVi--UHSXU-3v75UAO5qhxN19SC3mfhN6euN4t2XbxH0GkXpaM0RDjGPN0cCXZze5QoR1ec45a_8GvNON7CgvDWPHqi4Rbap78YumdWaz6xRZycGPuKFtCZN17jdIeF8M75Cuhnnw2iRrNh94WnbDTMYGBUPAtTGAH6fKQ_seJPXxZ__YPGTueMXHQ7hrFtEDbiw6zDJpbbK0l_xIQ">

						<div class="absolute top-3 left-3">

							<span
								class="px-2.5 py-1 rounded-full bg-white/90 backdrop-blur-sm text-xs font-semibold text-[#006574] shadow-sm translate">

								자연 </span>

						</div>

						<button
							class="like-btn absolute top-3 right-3 w-8 h-8 rounded-full bg-white/80 hover:bg-white backdrop-blur-sm text-[#131b2e] hover:text-red-500 flex items-center justify-center transition-colors shadow-sm"
							title="찜하기" type="button">

							<span class="material-symbols-outlined text-[18px]">favorite</span>

						</button>

					</div>

					<div class="p-5 flex-1 flex flex-col justify-between space-y-3">

						<div class="space-y-1.5">

							<div
								class="flex items-center gap-1 text-[11px] text-[#3e494b] font-medium">

								<span
									class="material-symbols-outlined text-[13px] text-[#006574]">location_on</span>

								<span class="translate truncate">강원특별자치도 속초시</span>

							</div>

							<h3
								class="text-base font-bold text-[#131b2e] group-hover:text-[#006574] transition-colors leading-snug">

								<span class="translate">설악산 국립공원</span>

							</h3>

							<p class="text-xs text-[#3e494b] line-clamp-2 leading-relaxed">

								<span class="translate">사계절 빼어난 기암괴석과 맑은 계곡을 자랑하는 한반도의 명산</span>

							</p>

						</div>

						<div
							class="pt-3 border-t border-[#f2f3ff] flex items-center justify-between text-xs">

							<div class="flex items-center gap-1 text-amber-500 font-semibold">

								<span class="material-symbols-outlined text-[15px] fill-active">star</span>

								<span class="">4.9</span> <span
									class="text-[#3e494b] font-normal text-[11px] ml-0.5">(2,740)</span>

							</div>

							<a
								class="text-[#006574] font-medium hover:underline inline-flex items-center gap-0.5 text-xs group-hover:translate-x-0.5 transition-transform"
								href="javascript:void(0)"> <span class="translate">자세히
									보기</span> →

							</a>

						</div>

					</div>

				</article>

				<!-- CARD 7: 여수 밤바다 & 케이블카 -->

				<article
					class="destination-card bg-white rounded-[20px] border border-[#e2e5eb] overflow-hidden shadow-card-subtle hover:border-[#006574]/30 flex flex-col group cursor-pointer"
					data-content-id="126514">

					<div
						class="relative w-full aspect-[4/3] overflow-hidden bg-slate-100">

						<img alt="여수 밤바다와 돌산대교 야경"
							class="card-img w-full h-full object-cover transition-transform duration-500 ease-out"
							loading="lazy"
							src="https://lh3.googleusercontent.com/aida-public/AB6AXuAbUEYW2MLu9Ey2O7RftuCqUqJfpCxTkpKUxyN2-p5PewahlP17ircuRTOTphWYKRv3RYEiTEI02Y_HHaJIHdejraIJSV6_zMH5JIgkVKmulFZCcaaNnnvwyHzBt0feQaOLc9DcWIFK1k0qGO1mJUTD3nroAiaC__qwZL1PcgFialTuP8nA7dBH4R2oASAOSQWOTNMx9AZvEuv7yEBi5yU1vNfC5pF9pbDIFeyTsRlrV7jQuwucBAW_wA">

						<div class="absolute top-3 left-3">

							<span
								class="px-2.5 py-1 rounded-full bg-white/90 backdrop-blur-sm text-xs font-semibold text-[#006574] shadow-sm translate">

								레저 </span>

						</div>

						<button
							class="like-btn absolute top-3 right-3 w-8 h-8 rounded-full bg-white/80 hover:bg-white backdrop-blur-sm text-[#131b2e] hover:text-red-500 flex items-center justify-center transition-colors shadow-sm"
							title="찜하기" type="button">

							<span class="material-symbols-outlined text-[18px]">favorite</span>

						</button>

					</div>

					<div class="p-5 flex-1 flex flex-col justify-between space-y-3">

						<div class="space-y-1.5">

							<div
								class="flex items-center gap-1 text-[11px] text-[#3e494b] font-medium">

								<span
									class="material-symbols-outlined text-[13px] text-[#006574]">location_on</span>

								<span class="translate truncate">전라남도 여수시</span>

							</div>

							<h3
								class="text-base font-bold text-[#131b2e] group-hover:text-[#006574] transition-colors leading-snug">

								<span class="translate">여수 밤바다 & 케이블카</span>

							</h3>

							<p class="text-xs text-[#3e494b] line-clamp-2 leading-relaxed">

								<span class="translate">낭만 가득한 바다 위 조명과 해상 케이블카에서 바라보는
									황홀한 야경</span>

							</p>

						</div>

						<div
							class="pt-3 border-t border-[#f2f3ff] flex items-center justify-between text-xs">

							<div class="flex items-center gap-1 text-amber-500 font-semibold">

								<span class="material-symbols-outlined text-[15px] fill-active">star</span>

								<span class="">4.8</span> <span
									class="text-[#3e494b] font-normal text-[11px] ml-0.5">(3,190)</span>

							</div>

							<a
								class="text-[#006574] font-medium hover:underline inline-flex items-center gap-0.5 text-xs group-hover:translate-x-0.5 transition-transform"
								href="javascript:void(0)"> <span class="translate">자세히
									보기</span> →

							</a>

						</div>

					</div>

				</article>

				<!-- CARD 8: 안동 하회마을 -->

				<article
					class="destination-card bg-white rounded-[20px] border border-[#e2e5eb] overflow-hidden shadow-card-subtle hover:border-[#006574]/30 flex flex-col group cursor-pointer"
					data-content-id="126515">

					<div
						class="relative w-full aspect-[4/3] overflow-hidden bg-slate-100">

						<img alt="안동 하회마을 부용대 전경"
							class="card-img w-full h-full object-cover transition-transform duration-500 ease-out"
							loading="lazy"
							src="https://lh3.googleusercontent.com/aida-public/AB6AXuCU9vwwChM6dK3DANgGalI2HbLHmip73wblvFb20RNdqInCfkkCR15DEDjVqXXwAVBSUdE-mmI6AudqO8C-CKwoKzTPZcxvejktI4phTrBgIRYt3wg7IqKlQ9t27A2GsCCk7-HNmLdXfco_sI8U4YPmdZUUGzvNwdzG7rXjOIEuQ_bvgv07d_9m5KY8wPlR9UmveLn_GZFAOh1mg1r6oluaz9I_5JBU3NfZWiOKDsb80wXbSIz5ezexdQ">

						<div class="absolute top-3 left-3">

							<span
								class="px-2.5 py-1 rounded-full bg-white/90 backdrop-blur-sm text-xs font-semibold text-[#006574] shadow-sm translate">

								역사 </span>

						</div>

						<button
							class="like-btn absolute top-3 right-3 w-8 h-8 rounded-full bg-white/80 hover:bg-white backdrop-blur-sm text-[#131b2e] hover:text-red-500 flex items-center justify-center transition-colors shadow-sm"
							title="찜하기" type="button">

							<span class="material-symbols-outlined text-[18px]">favorite</span>

						</button>

					</div>

					<div class="p-5 flex-1 flex flex-col justify-between space-y-3">

						<div class="space-y-1.5">

							<div
								class="flex items-center gap-1 text-[11px] text-[#3e494b] font-medium">

								<span
									class="material-symbols-outlined text-[13px] text-[#006574]">location_on</span>

								<span class="translate truncate">경상북도 안동시</span>

							</div>

							<h3
								class="text-base font-bold text-[#131b2e] group-hover:text-[#006574] transition-colors leading-snug">

								<span class="translate">안동 하회마을</span>

							</h3>

							<p class="text-xs text-[#3e494b] line-clamp-2 leading-relaxed">

								<span class="translate">낙동강이 S자형으로 감싸 안은 유서 깊은 전통 민속마을과
									부용대</span>

							</p>

						</div>

						<div
							class="pt-3 border-t border-[#f2f3ff] flex items-center justify-between text-xs">

							<div class="flex items-center gap-1 text-amber-500 font-semibold">

								<span class="material-symbols-outlined text-[15px] fill-active">star</span>

								<span class="">4.7</span> <span
									class="text-[#3e494b] font-normal text-[11px] ml-0.5">(1,620)</span>

							</div>

							<a
								class="text-[#006574] font-medium hover:underline inline-flex items-center gap-0.5 text-xs group-hover:translate-x-0.5 transition-transform"
								href="javascript:void(0)"> <span class="translate">자세히
									보기</span> →

							</a>

						</div>

					</div>

				</article>

			</div>

			<!-- 대한민국 여행지 페이지네이션 -->

			<div id="paginationContainer"
				class="hidden mt-10 flex items-center justify-center gap-2"></div>

		</section>

	</main>

	<!-- 푸터 -->

	<!-- 프론트엔드 인터랙션 + TourAPI 지역 필터 -->

	<script>

const contextPath = "${pageContext.request.contextPath}";

let allDestinations = [];

// 대한민국 여행지 페이지네이션

const ITEMS_PER_PAGE = 20;

let currentPage = 1;

let currentListItems = [];

/*

 * 기존 화면의 data-area-code 값을 그대로 사용합니다.

 * TourAPI에서 받아온 addr1 주소를 기준으로 권역을 묶어 필터링합니다.

 */

const regionGroups = {

    "1":  ["서울"],

    "31": ["경기", "인천"],

    "32": ["강원"],

    "3":  ["충북", "충남", "충청북도", "충청남도", "대전", "세종"],

    "5":  ["전북", "전남", "전북특별자치도", "전라북도", "전라남도", "광주"],

    "6":  ["경북", "경남", "경상북도", "경상남도", "부산", "대구", "울산"],

    "39": ["제주"]

};

document.addEventListener("DOMContentLoaded", function() {

    console.log("===== 여행지 화면 로딩 =====");

    setupRegionButtons();

    setupLikeButtons();

    setupCardClicks();

    setupSearch();

    loadDestinations();

});

/* ============================================
메인 페이지에서 전달받은 지역 자동 선택
============================================ */

document.addEventListener("DOMContentLoaded", function() {

 // 1. URL에서 region 값 가져오기
 const params = new URLSearchParams(window.location.search);
 const region = params.get("region");

 if (!region) {
     return;
 }

 // 2. 메인 지역 코드 → 여행지 지역 코드
 const regionMap = {
     gw: "32",   // 강원
     cap: "31",  // 경기·인천
     cc: "3",    // 충청·대전·세종
     hn: "5",    // 전라·광주
     yn: "6",    // 경상·부산·대구·울산
     jj: "39"   // 제주
 };

 const areaCode = regionMap[region];

 if (!areaCode) {
     return;
 }

 // 3. 여행지 상단 지역 버튼 찾기
 const targetButton = document.querySelector(
     '.region-pill[data-area-code="' + areaCode + '"]'
 );

 // 4. 기존 클릭 이벤트 실행
 if (targetButton) {
     targetButton.click();

     // 5. 지역 선택 영역으로 이동
     const section = document.getElementById(
         "region-explorer-section"
     );

     if (section) {
         section.scrollIntoView({
             behavior: "smooth",
             block: "start"
         });
     }
 }

});

/* ==============================

여행지 키워드 검색 이벤트

============================== */

function setupSearch() {

 const form =

     document.getElementById("destinationSearchForm");

 const input =

     document.getElementById("keywordInput");

 if (!form || !input) {

     console.error(

         "검색창 또는 검색 Form을 찾을 수 없습니다."

     );

     return;

 }

 form.addEventListener("submit", function(event) {

     event.preventDefault();

     const keyword =

         input.value.trim();

     console.log(

         "===== 검색 버튼 클릭 ====="

     );

     console.log(

         "검색어 :", keyword

     );

     if (keyword === "") {

         alert(

             "검색어를 입력해주세요."

         );

         input.focus();

         return;

     }

     searchDestinations(keyword);

 });

}

/* ==============================

TourAPI 키워드 검색

============================== */

function searchDestinations(keyword) {

 console.log(

     "TourAPI 검색 요청 :",

     keyword

 );

 fetch(

     contextPath

     + "/TravelDestination/searchApi?keyword="

     + encodeURIComponent(keyword)

 )

 .then(function(response) {

     console.log(

         "검색 HTTP 응답 코드 :",

         response.status

     );

     if (!response.ok) {

         throw new Error(

             "검색 API 요청 실패 : "

             + response.status

         );

     }

     return response.json();

 })

 .then(function(data) {

     console.log(

         "TourAPI 검색 전체 데이터 :",

         data

     );

     const body =

         data.response

         && data.response.body;

     // 검색 결과가 없는 경우

     if (!body

             || !body.items

             || !body.items.item) {

         console.log(

             "검색 결과 없음"

         );

         allDestinations = [];

         renderDestinations([]);

         return;

     }

     // TourAPI는 결과가 1개일 때

     // 배열이 아닌 객체로 올 수도 있기 때문에 처리

     const searchResults =

         Array.isArray(body.items.item)

             ? body.items.item

             : [body.items.item];

     console.log(

         "검색 결과 :",

         searchResults

     );

     // 기존 카드 렌더링 함수 그대로 재사용

     allDestinations =

         searchResults;

     renderDestinations(

         searchResults

     );

     // 결과 영역으로 이동

     const resultSection =

         document.getElementById(

             "filter-section"

         );

     if (resultSection) {

         resultSection.scrollIntoView({

             behavior: "smooth",

             block: "start"

         });

     }

 })

 .catch(function(error) {

     console.error(

         "여행지 검색 실패 :",

         error

     );

     alert(

         "여행지 검색 중 오류가 발생했습니다."

     );

 });

}

/* ==============================

   1. TourAPI 데이터 가져오기

   ============================== */

function loadDestinations() {

    console.log("TourAPI 요청 시작");

    fetch(contextPath + "/TravelDestination/api")

        .then(function(response) {

            console.log("HTTP 응답 코드 :", response.status);

            if (!response.ok) {

                throw new Error("API 요청 실패 : " + response.status);

            }

            return response.json();

        })

        .then(function(data) {

            console.log("TourAPI 전체 데이터 :", data);

            const body = data.response && data.response.body;

            if (!body || !body.items || !body.items.item) {

                allDestinations = [];

                renderDestinations([]);

                renderFeaturedDestinations([]);

                return;

            }

            allDestinations = Array.isArray(body.items.item)

                ? body.items.item

                : [body.items.item];

            console.log("받아온 전체 여행지 :", allDestinations);

            // 아래 "대한민국 여행지"

            renderDestinations(allDestinations);

            // 위 "지금 떠나기 좋은 여행지"

            // 고정 카드가 아니라 TourAPI 데이터 3개를 사용

            renderFeaturedDestinations(allDestinations);

        })

        .catch(function(error) {

            console.error("여행지 조회 실패 :", error);

            allDestinations = [];

            renderDestinations([]);

            renderFeaturedDestinations([]);

        });

}

/* ==============================

   2. 지역 버튼 연결

   위/아래 지역 버튼 모두 동작

   ============================== */

function setupRegionButtons() {

    // 1번: 위 지역 버튼 -> "지금 떠나기 좋은 여행지"만 변경

    const featuredButtons =

        document.querySelectorAll(".region-pill");

    featuredButtons.forEach(function(button) {

        button.addEventListener("click", function() {

            const areaCode = this.dataset.areaCode;

            setActiveButtonGroup(

                featuredButtons,

                this

            );

            filterFeaturedByRegion(areaCode);

        });

    });

    // 2번: 아래 지역 버튼 -> 대한민국 여행지 카드만 변경

    const listButtons =

        document.querySelectorAll(".theme-chip");

    listButtons.forEach(function(button) {

        button.addEventListener("click", function() {

            const areaCode = this.dataset.areaCode;

            setActiveButtonGroup(

                listButtons,

                this

            );

            filterByRegion(areaCode);

        });

    });

}

/* ==============================

   버튼 그룹 선택 디자인

   위/아래 필터를 서로 독립적으로 관리

   ============================== */

function setActiveButtonGroup(buttons, activeButton) {

    buttons.forEach(function(button) {

        const selected =

            button === activeButton;

        button.classList.toggle("bg-[#006574]", selected);

        button.classList.toggle("text-white", selected);

        button.classList.toggle("font-semibold", selected);

        button.classList.toggle("shadow-sm", selected);

        button.classList.toggle("bg-white", !selected);

        button.classList.toggle("text-[#3e494b]", !selected);

        button.classList.toggle("border", !selected);

        button.classList.toggle("border-[#e2e5eb]", !selected);

    });

}

/* ==============================

   3. 선택한 지역만 필터링

   ============================== */

function filterByRegion(areaCode) {

    if (areaCode === "all") {

        console.log("선택 지역 : 전체");

        renderDestinations(allDestinations);

        return;

    }

    const regions = regionGroups[areaCode];

    if (!regions) {

        console.warn("등록되지 않은 지역 코드 :", areaCode);

        renderDestinations(allDestinations);

        return;

    }

    const filteredItems = allDestinations.filter(function(item) {

        const address = item.addr1 || "";

        return regions.some(function(region) {

            return address.includes(region);

        });

    });

    console.log("선택 지역 :", regions);

    console.log("필터링 결과 :", filteredItems);

    renderDestinations(filteredItems);

}

/* ==============================

   5. 여행지 카드 목록 출력

   ============================== */

/* ==============================

   위 "지금 떠나기 좋은 여행지" 지역 필터

   ============================== */

function filterFeaturedByRegion(areaCode) {

    let filteredItems = allDestinations;

    if (areaCode !== "all") {

        const regions = regionGroups[areaCode];

        if (regions) {

            filteredItems =

                allDestinations.filter(function(item) {

                    const address =

                        item.addr1 || "";

                    return regions.some(function(region) {

                        return address.includes(region);

                    });

                });

        }

    }

    console.log(

        "추천 여행지 필터 결과 :",

        filteredItems

    );

    renderFeaturedDestinations(

        filteredItems

    );

}

/* ==============================

   추천 여행지 3개 출력

   API 데이터 사용

   ============================== */

function renderFeaturedDestinations(items) {

    const container =

        document.getElementById(

            "featuredDestinationContainer"

        );

    if (!container) {

        return;

    }

    const featuredItems =

        items

        .filter(function(item) {

            return item.contentid;

        })

        .slice(0, 3);

    if (featuredItems.length === 0) {

        container.innerHTML =

            '<div class="lg:col-span-12 py-14 text-center bg-white rounded-3xl border border-[#e2e5eb]">' +

                '<span class="material-symbols-outlined text-5xl text-[#006574]/30">travel_explore</span>' +

                '<p class="mt-3 text-sm text-[#3e494b]">해당 지역의 추천 여행지가 없습니다.</p>' +

            '</div>';

        return;

    }

    container.innerHTML = "";

    featuredItems.forEach(function(item, index) {

        const card =

            createFeaturedDestinationCard(

                item,

                index === 0

            );

        container.appendChild(card);

    });

}

/* ==============================

   추천 여행지 카드 생성

   첫 번째 = 큰 카드

   나머지 = 오른쪽 작은 카드 영역

   ============================== */

function createFeaturedDestinationCard(item, isMain) {

    if (isMain) {

        const article =

            document.createElement("article");

        article.className =

            "featured-destination-card lg:col-span-7 group relative rounded-3xl overflow-hidden " +

            "shadow-card-subtle hover:shadow-card-hover border border-black/5 bg-[#131b2e] " +

            "cursor-pointer min-h-[380px] sm:min-h-[440px] transition-all duration-300";

        article.dataset.contentId =

            item.contentid || "";

        article.dataset.name = String(item.title || "여행지").trim();
        article.dataset.address = String(item.addr1 || "").trim();
        article.dataset.category = "여행지";
        article.dataset.image = String(item.firstimage || item.firstimage2 || "").trim();

        article.innerHTML =

            createFeaturedCardHtml(

                item,

                true

            );

        return article;

    }

    // 작은 카드 두 장은 wrapper가 필요하므로

    // renderFeaturedDestinations에서 사용할 수 있게

    // wrapper 여부를 data 속성으로 구분

    const article =

        document.createElement("article");

    article.className =

        "featured-destination-card group relative rounded-3xl overflow-hidden " +

        "shadow-card-subtle hover:shadow-card-hover border border-black/5 bg-[#131b2e] " +

        "cursor-pointer h-[210px] sm:h-[220px] transition-all duration-300";

    article.dataset.contentId =

        item.contentid || "";

        article.dataset.name = String(item.title || "여행지").trim();
        article.dataset.address = String(item.addr1 || "").trim();
        article.dataset.category = "여행지";
        article.dataset.image = String(item.firstimage || item.firstimage2 || "").trim();

    article.innerHTML =

        createFeaturedCardHtml(

            item,

            false

        );

    // 두 번째 카드가 만들어질 때 오른쪽 wrapper 생성

    const container =

        document.getElementById(

            "featuredDestinationContainer"

        );

    let side =

        container.querySelector(

            ".featured-side-container"

        );

    if (!side) {

        side =

            document.createElement("div");

        side.className =

            "featured-side-container lg:col-span-5 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-1 gap-5 lg:gap-5";

        container.appendChild(side);

    }

    side.appendChild(article);

    // 이미 wrapper 안에 붙였으므로 빈 노드 반환용

    return document.createDocumentFragment();

}

function createFeaturedCardHtml(item, isMain) {

    const title =

        escapeHtml(

            item.title || "여행지"

        );

    const address =

        escapeHtml(

            item.addr1 || "주소 정보 없음"

        );

    const image =

        item.firstimage

        || item.firstimage2

        || "";

    const fallbackImage = "data:image/svg+xml,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22900%22%20height%3D%22600%22%20viewBox%3D%220%200%20900%20600%22%3E%3Cdefs%3E%3ClinearGradient%20id%3D%22sky%22%20x2%3D%220%22%20y2%3D%221%22%3E%3Cstop%20stop-color%3D%22%23d9f1f4%22%2F%3E%3Cstop%20offset%3D%221%22%20stop-color%3D%22%23f2f7fa%22%2F%3E%3C%2FlinearGradient%3E%3C%2Fdefs%3E%3Crect%20width%3D%22900%22%20height%3D%22600%22%20fill%3D%22url%28%23sky%29%22%2F%3E%3Ccircle%20cx%3D%22700%22%20cy%3D%22155%22%20r%3D%2260%22%20fill%3D%22%23f8d998%22%2F%3E%3Cpath%20d%3D%22M0%20420%20205%20230%20385%20410%20540%20290%20900%20490V600H0Z%22%20fill%3D%22%238ebec2%22%2F%3E%3Cpath%20d%3D%22M0%20485%20240%20350%20415%20470%20630%20340%20900%20485V600H0Z%22%20fill%3D%22%234f939d%22%2F%3E%3Crect%20y%3D%22515%22%20width%3D%22900%22%20height%3D%2285%22%20fill%3D%22%23246b78%22%2F%3E%3Ctext%20x%3D%22450%22%20y%3D%22555%22%20font-family%3D%22sans-serif%22%20font-size%3D%2232%22%20text-anchor%3D%22middle%22%20fill%3D%22white%22%3EIMAGE%20COMING%20SOON%3C%2Ftext%3E%3C%2Fsvg%3E";

    const imageHtml =
        '<img src="' + escapeHtml(image || fallbackImage) + '" alt="' + title + '" ' +
        'onerror="this.onerror=null;this.src=\'' + fallbackImage + '\';" ' +
        'class="w-full h-full object-cover object-center absolute inset-0 group-hover:scale-105 transition-transform duration-500 ease-out opacity-90">';

    const padding =

        isMain

        ? "p-6 sm:p-8"

        : "p-5";

    const titleClass =

        isMain

        ? "text-2xl sm:text-3xl"

        : "text-xl";

    return (

        imageHtml +

        '<div class="absolute inset-0 bg-gradient-to-t from-black/85 via-black/35 to-black/10"></div>' +

        '<div class="absolute top-4 left-4 z-10">' +

            '<span class="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-full bg-[#006574] text-white text-xs font-semibold shadow-md">' +

                '<span class="material-symbols-outlined text-[14px]">auto_awesome</span>' +

                '<span>추천 여행지</span>' +

            '</span>' +

        '</div>' +

        '<div class="absolute bottom-0 left-0 right-0 ' + padding + ' z-10 text-white space-y-2">' +

            '<div class="flex items-center gap-1 text-xs text-emerald-200">' +

                '<span class="material-symbols-outlined text-[15px]">location_on</span>' +

                '<span class="truncate">' + address + '</span>' +

            '</div>' +

            '<h3 class="' + titleClass + ' font-bold tracking-tight text-white">' +

                title +

            '</h3>' +

            '<div class="pt-2 flex justify-end text-xs text-white/90">' +

                '<span class="inline-flex items-center gap-1 font-medium group-hover:translate-x-1 transition-transform">' +

                    '자세히 보기' +

                    '<span class="material-symbols-outlined text-[16px]">arrow_forward</span>' +

                '</span>' +

            '</div>' +

        '</div>'

    );

}

/* ==============================

   5. 여행지 카드 목록 출력

   ============================== */

function renderDestinations(items) {

    currentListItems = Array.isArray(items) ? items : [];

    currentPage = 1;

    renderCurrentPage();

}

/* ==============================

   현재 페이지 20개 출력

   ============================== */

function renderCurrentPage() {

    const totalPages =

        Math.ceil(currentListItems.length / ITEMS_PER_PAGE);

    if (totalPages > 0 && currentPage > totalPages) {

        currentPage = totalPages;

    }

    if (currentPage < 1) {

        currentPage = 1;

    }

    const start =

        (currentPage - 1) * ITEMS_PER_PAGE;

    const end =

        start + ITEMS_PER_PAGE;

    const pageItems =

        currentListItems.slice(start, end);

    renderDestinationCards(pageItems);
    paintTravelLikes();

    // 전체 검색/필터 결과 개수 표시

    updateTotalCount(currentListItems.length);

    renderPagination();

}

/* ==============================

   페이지 이동

   ============================== */

function moveToPage(page) {

    const totalPages =

        Math.ceil(currentListItems.length / ITEMS_PER_PAGE);

    if (page < 1

            || page > totalPages

            || page === currentPage) {

        return;

    }

    currentPage = page;

    renderCurrentPage();

    const section =

        document.getElementById("card-grid-section");

    if (section) {

        section.scrollIntoView({

            behavior: "smooth",

            block: "start"

        });

    }

}

/* ==============================

   페이지네이션 출력

   ============================== */

function renderPagination() {

    const container =

        document.getElementById("paginationContainer");

    if (!container) {

        return;

    }

    container.innerHTML = "";

    const totalPages =

        Math.ceil(currentListItems.length / ITEMS_PER_PAGE);

    if (totalPages <= 1) {

        container.classList.add("hidden");

        return;

    }

    container.classList.remove("hidden");

    container.appendChild(

        createPaginationButton(

            "‹",

            currentPage - 1,

            currentPage === 1,

            false

        )

    );

    let startPage =

        Math.max(1, currentPage - 2);

    let endPage =

        Math.min(totalPages, startPage + 4);

    startPage =

        Math.max(1, endPage - 4);

    for (let page = startPage;

         page <= endPage;

         page++) {

        container.appendChild(

            createPaginationButton(

                String(page),

                page,

                false,

                page === currentPage

            )

        );

    }

    container.appendChild(

        createPaginationButton(

            "›",

            currentPage + 1,

            currentPage === totalPages,

            false

        )

    );

}

/* ==============================

   페이지 버튼 생성

   ============================== */

function createPaginationButton(label, page, disabled, active) {

    const button =

        document.createElement("button");

    button.type = "button";

    button.textContent = label;

    button.disabled = disabled;

    button.className =

        "min-w-[38px] h-[38px] px-3 rounded-xl border text-sm transition-all " +

        (active

            ? "bg-[#006574] border-[#006574] text-white font-bold shadow-sm "

            : "bg-white border-[#dfe4e8] text-[#3e494b] hover:border-[#006574] hover:text-[#006574] ") +

        (disabled

            ? "opacity-40 cursor-not-allowed"

            : "");

    if (!disabled) {

        button.addEventListener(

            "click",

            function() {

                moveToPage(page);

            }

        );

    }

    return button;

}

/* ==============================

   실제 카드 출력

   ============================== */

function renderDestinationCards(items) {

    const container =

        document.getElementById("destinationListContainer");

    if (!container) {

        console.error(

            "destinationListContainer를 찾을 수 없습니다."

        );

        return;

    }

    container.innerHTML = "";

    if (items.length === 0) {

        container.innerHTML =

            '<div class="col-span-full py-16 text-center">' +

                '<span class="material-symbols-outlined text-5xl text-[#006574]/30">' +

                    'travel_explore' +

                '</span>' +

                '<p class="mt-3 text-sm text-[#3e494b]">' +

                    '해당 지역의 여행지가 없습니다.' +

                '</p>' +

            '</div>';

        return;

    }

    items.forEach(function(item) {

        container.appendChild(

            createDestinationCard(item)

        );

    });

    console.log(

        "===== 여행지 카드 생성 완료 : "

        + items.length

        + "개 ====="

    );

}

/* ==============================

   6. 여행지 카드 하나 생성

   ============================== */

function createDestinationCard(item) {

    const title =

        item.title || "여행지";

    const address =

        item.addr1 || "주소 정보 없음";

    const fallbackImage = "data:image/svg+xml,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22900%22%20height%3D%22600%22%20viewBox%3D%220%200%20900%20600%22%3E%3Cdefs%3E%3ClinearGradient%20id%3D%22sky%22%20x2%3D%220%22%20y2%3D%221%22%3E%3Cstop%20stop-color%3D%22%23d9f1f4%22%2F%3E%3Cstop%20offset%3D%221%22%20stop-color%3D%22%23f2f7fa%22%2F%3E%3C%2FlinearGradient%3E%3C%2Fdefs%3E%3Crect%20width%3D%22900%22%20height%3D%22600%22%20fill%3D%22url%28%23sky%29%22%2F%3E%3Ccircle%20cx%3D%22700%22%20cy%3D%22155%22%20r%3D%2260%22%20fill%3D%22%23f8d998%22%2F%3E%3Cpath%20d%3D%22M0%20420%20205%20230%20385%20410%20540%20290%20900%20490V600H0Z%22%20fill%3D%22%238ebec2%22%2F%3E%3Cpath%20d%3D%22M0%20485%20240%20350%20415%20470%20630%20340%20900%20485V600H0Z%22%20fill%3D%22%234f939d%22%2F%3E%3Crect%20y%3D%22515%22%20width%3D%22900%22%20height%3D%2285%22%20fill%3D%22%23246b78%22%2F%3E%3Ctext%20x%3D%22450%22%20y%3D%22555%22%20font-family%3D%22sans-serif%22%20font-size%3D%2232%22%20text-anchor%3D%22middle%22%20fill%3D%22white%22%3EIMAGE%20COMING%20SOON%3C%2Ftext%3E%3C%2Fsvg%3E";

    const imageUrl =
        item.firstimage ||
        item.firstimage2 ||
        fallbackImage;

    const contentId =

        item.contentid || "-";

    const card =

        document.createElement("article");

    card.className =

        "destination-card " +

        "bg-white rounded-[20px] " +

        "border border-[#e2e5eb] " +

        "overflow-hidden shadow-card-subtle " +

        "hover:border-[#006574]/30 " +

        "flex flex-col group cursor-pointer";

    card.setAttribute(

        "data-content-id",

        contentId

    );

    // DB tour_like 저장에 필요한 여행지 정보 (화면 HTML에서 추측하지 않도록 보관)
    card.dataset.name = String(item.title || "여행지").trim();
    card.dataset.address = String(item.addr1 || "").trim();
    card.dataset.category = "여행지";
    card.dataset.image = String(item.firstimage || item.firstimage2 || "").trim();

    card.innerHTML =

        '<div class="relative w-full aspect-[4/3] overflow-hidden bg-slate-100">' +

            '<img ' +

                'src="' + escapeHtml(imageUrl) + '" ' +

                'onerror="this.onerror=null;this.src=\'' + fallbackImage + '\';" ' +

                'alt="' + escapeHtml(title) + '" ' +

                'loading="lazy" ' +

                'class="card-img w-full h-full object-cover transition-transform duration-500 ease-out">' +

            '<button type="button" ' +

                'class="like-btn absolute top-3 right-3 w-8 h-8 rounded-full bg-white/80 hover:bg-white backdrop-blur-sm text-[#131b2e] hover:text-red-500 flex items-center justify-center transition-colors shadow-sm" ' +

                'title="찜하기">' +

                '<span class="material-symbols-outlined text-[18px]">favorite</span>' +

            '</button>' +

        '</div>' +

        '<div class="p-5 flex-1 flex flex-col justify-between space-y-3">' +

            '<div class="space-y-1.5">' +

                '<div class="flex items-center gap-1 text-[11px] text-[#3e494b] font-medium">' +

                    '<span class="material-symbols-outlined text-[13px] text-[#006574]">' +

                        'location_on' +

                    '</span>' +

                    '<span class="truncate">' +

                        escapeHtml(address) +

                    '</span>' +

                '</div>' +

                '<h3 class="text-base font-bold text-[#131b2e] group-hover:text-[#006574] transition-colors leading-snug">' +

                    '<span class="translate">' +

                        escapeHtml(title) +

                    '</span>' +

                '</h3>' +

            '</div>' +

            '<div class="pt-3 border-t border-[#e2e5eb] flex items-center justify-end">' +

            '<span class="text-xs font-semibold text-[#006574]">' +

                '자세히 보기 →' +

            '</span>' +

        '</div>'

        '</div>';

    return card;

}

/* ==============================

   7. 총 여행지 개수 변경

   ============================== */

function updateTotalCount(count) {

    const totalCount =

        document.getElementById("totalCount");

    if (totalCount) {

        totalCount.textContent = count;

    }

}

/* ==============================

   8. 좋아요 버튼

   동적 생성 카드까지 처리

   ============================== */

// 2026-10-19 여행지 찜 통합: 여행지 페이지도 마이페이지와 같은 tour_like 사용
// 1) /MyFavorites → 로그인한 회원의 tour_like 목록 조회
// 2) /TourLike    → 한 건 찜 추가 또는 취소(서버에서 현재 상태를 확인한 뒤 토글)
const travelLikeUrl = "${pageContext.request.contextPath}/TourLike";
const travelFavoritesUrl = "${pageContext.request.contextPath}/MyFavorites";
let travelLikedIds = new Set();
let travelLikeLoggedIn = false;
let travelLikeReady = false;

function paintTravelLikes() {
    document.querySelectorAll(".destination-card, .featured-destination-card").forEach(card => {
        const button = card.querySelector(".like-btn");
        if (!button) return;
        const liked = travelLikedIds.has(String(card.dataset.contentId || ""));
        button.classList.toggle("text-red-500", liked);
        const icon = button.querySelector(".material-symbols-outlined");
        if (icon) icon.classList.toggle("fill-active", liked);
        button.setAttribute("aria-pressed", String(liked));
        button.title = liked ? "찜 취소" : "찜하기";
    });
}

async function refreshTravelLikes() {
    try {
        const response = await fetch(travelFavoritesUrl, {
            credentials: "same-origin", cache: "no-store"
        });
        if (response.status === 401) {
            travelLikeLoggedIn = false;
            travelLikeReady = true;
            travelLikedIds = new Set();
            paintTravelLikes();
            return;
        }
        if (!response.ok) throw new Error("찜 목록 조회 실패: HTTP " + response.status);
        const data = await response.json();
        if (data.success !== true) throw new Error(data.message || "찜 목록 조회 실패");
        travelLikedIds = new Set((Array.isArray(data.tours) ? data.tours : [])
            .map(t => String(t.contentId ?? t.content_id ?? ""))
            .filter(Boolean));
        travelLikeLoggedIn = true;
        travelLikeReady = true;
        paintTravelLikes();
    } catch (e) {
        // DB 오류 시 찜 여부를 모르는 상태에서 토글하여 원치 않는 찜 취소를 방지
        travelLikeReady = false;
        console.error("여행지 찜 상태 조회 실패", e);
    }
}

function setupLikeButtons() {
    refreshTravelLikes();
    document.addEventListener("click", async function(event) {
        const button = event.target.closest(".like-btn");
        if (!button) return;
        event.preventDefault();
        event.stopPropagation();
        const card = button.closest("[data-content-id]");
        const contentId = card && String(card.dataset.contentId || "").trim();
        if (!contentId || !/^\d{1,30}$/.test(contentId)) {
            alert("여행지 번호가 올바르지 않습니다.");
            return;
        }
        if (!travelLikeReady) {
            alert("찜 정보를 불러오지 못했습니다. 새로고침 후 다시 시도해 주세요.");
            refreshTravelLikes();
            return;
        }
        if (!travelLikeLoggedIn) {
            document.getElementById("like-login-dialog").showModal();
            return;
        }
        if (button.disabled) return;
        button.disabled = true;
        try {
            const name = (card.dataset.name || card.querySelector("h3")?.textContent || "여행지").trim();
            const address = (card.dataset.address || card.querySelector(".truncate")?.textContent || "").trim();
            const category = (card.dataset.category || "여행지").trim();
            const imageUrl = (card.dataset.image || card.querySelector("img.card-img")?.getAttribute("src") || "").trim();
            const image = /^https?:\/\//i.test(imageUrl) ? imageUrl : "";
            // TourLikeController는 신규 찜 저장 시 name, address, category, image를 받음
            const params = new URLSearchParams({
                contentId, name, address, category, image
            });
            const response = await fetch(travelLikeUrl, {
                method: "POST", credentials: "same-origin",
                headers: {"Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"},
                body: params.toString()
            });
            if (response.status === 401) {
                travelLikeLoggedIn = false;
                document.getElementById("like-login-dialog").showModal();
                return;
            }
            const data = await response.json();
            if (!response.ok || data.success !== true || typeof data.liked !== "boolean") {
                throw new Error(data.message || "찜 저장 실패: HTTP " + response.status);
            }
            if (data.liked) travelLikedIds.add(contentId);
            else travelLikedIds.delete(contentId);
            paintTravelLikes();
        } catch (e) {
            console.error("여행지 찜 처리 실패", e);
            alert("찜 처리에 실패했습니다. 다시 시도해 주세요.");
            await refreshTravelLikes();
        } finally {
            button.disabled = false;
        }
    });
}

/* ==============================

   9. 카드 클릭

   ============================== */

function setupCardClicks() {

    document.addEventListener("click", function(event) {

        const card =

            event.target.closest(

                ".destination-card, .featured-destination-card"

            );

        if (!card) {

            return;

        }

        if (event.target.closest(".like-btn")) {

            return;

        }

        const contentId =

            card.dataset.contentId;

        console.log(

        	    "[Navigate] Destination ContentId:",

        	    contentId

        	);

        	// contentId가 없으면 이동하지 않음

        	if (!contentId || contentId === "-") {

        	    console.warn("contentId가 없는 카드입니다.");

        	    return;

        	}

        	// 여행지 상세페이지로 이동

        	window.location.href =

        	    contextPath

        	    + "/TravelDestination/detail?contentId="

        	    + encodeURIComponent(contentId);

    });

}

/* ==============================

   10. API 문자열 HTML 안전 처리

   ============================== */

function escapeHtml(value) {

    return String(value)

        .replace(/&/g, "&amp;")

        .replace(/</g, "&lt;")

        .replace(/>/g, "&gt;")

        .replace(/"/g, "&quot;")

        .replace(/'/g, "&#039;");

}

</script>

	<script>
/* 메인 페이지에서 전달받은 지역을 위/아래 필터에 자동 적용 */
(function () {
    const regionMap = {
        gw: "32",   // 강원
        cap: "31",  // 경기·인천
        cc: "3",    // 충청
        hn: "5",    // 전라
        yn: "6",    // 경상
        jj: "39"   // 제주
    };

    const params = new URLSearchParams(window.location.search);
    const areaCode = regionMap[params.get("region")];

    if (!areaCode) return;

    function applyRegion() {
        const topButton = document.querySelector(
            '.region-pill[data-area-code="' + areaCode + '"]'
        );

        const bottomButton = document.querySelector(
            '.theme-chip[data-area-code="' + areaCode + '"]'
        );

        if (topButton) topButton.click();
        if (bottomButton) bottomButton.click();
    }

    document.addEventListener("DOMContentLoaded", function () {
        // 기존 페이지의 이벤트 등록 및 API 호출 이후 실행
        // 데이터가 아직 로딩 중이면 완료될 때 다시 적용
        const originalFetch = window.fetch;

        // 초기 버튼 선택
        applyRegion();

        // 기존 API 호출 완료를 감지하여 필터 재적용
        const list = document.getElementById(
            "destinationListContainer"
        );

        const featured = document.getElementById(
            "featuredDestinationContainer"
        );

        if (!list || !featured) return;

        let scheduled = false;

        const observer = new MutationObserver(function () {
            if (scheduled) return;

            scheduled = true;

            requestAnimationFrame(function () {
                scheduled = false;

                // API 로딩 후 전체 목록으로 다시 그려졌을 때만 재선택
                if (typeof allDestinations !== "undefined"
                    && allDestinations.length > 0) {

                    applyRegion();
                    observer.disconnect();
                }
            });
        });

        observer.observe(list, {
            childList: true
        });

        observer.observe(featured, {
            childList: true
        });

        // 데이터가 이미 로딩된 경우
        if (typeof allDestinations !== "undefined"
            && allDestinations.length > 0) {
            applyRegion();
            observer.disconnect();
        }
    });
})();

</script>

	<script>
document.addEventListener("DOMContentLoaded", function () {

    // 메인 페이지에서 전달한 검색어
    const params = new URLSearchParams(window.location.search);
    const keyword = params.get("keyword");

    if (!keyword) return;

    const input = document.getElementById("keywordInput");
    const form = document.getElementById("destinationSearchForm");

    if (!input || !form) return;

    // 검색창에 검색어 입력
    input.value = keyword;

    // 전체 여행지 API 로딩 완료 후 검색 실행
    const originalFetch = window.fetch;
    let searchStarted = false;

    function runSearch() {
        if (searchStarted) return;
        searchStarted = true;

        // 기존 검색 버튼 동작 그대로 실행
        form.requestSubmit();
    }

    // 전체 여행지 목록을 가져오는 API 응답 감지
    window.fetch = function (...args) {
        const request = originalFetch.apply(this, args);

        const url = String(
            args[0] instanceof Request ? args[0].url : args[0]
        );

        if (url.includes("/TravelDestination/api")
                && !url.includes("/searchApi")) {

            request.then(function (response) {
                // 응답 JSON 처리와 카드 렌더링 이후 검색 실행
                response.clone().json().then(function () {
                    setTimeout(runSearch, 100);
                }).catch(function () {
                    setTimeout(runSearch, 100);
                });
            }).catch(function () {
                setTimeout(runSearch, 100);
            });
        }

        return request;
    };

    // 기존 loadDestinations()가 이미 시작된 경우에도 적용
    // 화면 카드 갱신을 감지하여 검색 실행
    const container =
        document.getElementById("destinationListContainer");

    if (container) {
        const observer = new MutationObserver(function () {
            if (searchStarted) {
                observer.disconnect();
                return;
            }

            observer.disconnect();
            setTimeout(runSearch, 100);
        });

        observer.observe(container, {
            childList: true
        });
    }

});
</script>


<!-- 비로그인 상태에서 찜하기 클릭 시 표시 -->
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
document.querySelectorAll('[data-like-login-close]').forEach(function(button) {
    button.addEventListener('click', function() {
        document.getElementById('like-login-dialog').close();
    });
});
</script>

</body>
</html>
