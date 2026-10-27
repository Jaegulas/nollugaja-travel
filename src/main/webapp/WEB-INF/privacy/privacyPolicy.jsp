<%-- =========================================================
     주제: 파일 역할
     설명: privacy 영역의 privacyPolicy 화면을 구성하고 서버에서 전달받은 데이터를 표시
========================================================= --%>
<%-- 이 페이지의 글자 인코딩을 UTF-8 로 정한다. 빠뜨리면 한글이 깨진다 --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>

<html lang="ko">
<head>
<meta charset="utf-8" />
<meta content="width=device-width, initial-scale=1.0" name="viewport" />
<title>놀러가자 - 대한민국 국내여행 탐색 메인 홈</title>
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
<style>
@layer base {html , body { margin:0;
	padding: 0;
	font-family: 'Plus Jakarta Sans', 'Noto Sans KR', sans-serif;
}

body {
	overscroll-behavior: none;
}

}
::-webkit-scrollbar {
	display: none;
}

/* 지도 호버 & 툴팁 액션 스타일 */
.map-path {
	transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
	cursor: pointer;
}

.map-path:hover {
	filter: drop-shadow(0 6px 12px rgba(0, 101, 116, 0.25));
	transform: translateY(-2px);
}

.action-btn-hover {
	transition: all 0.2s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.action-btn-hover:hover {
	transform: translateY(-2px) scale(1.03);
}

.action-btn-hover:active {
	transform: translateY(0) scale(0.97);
}

/* 토스트 알림 */
#action-toast {
	position: fixed;
	bottom: 32px;
	left: 50%;
	transform: translateX(-50%) translateY(100px);
	opacity: 0;
	pointer-events: none;
	transition: all 0.35s cubic-bezier(0.16, 1, 0.3, 1);
	z-index: 2000;
}

#action-toast.show {
	transform: translateX(-50%) translateY(0);
	opacity: 1;
	pointer-events: auto;
}
</style>
<script
	src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
<script id="tailwind-config">
tailwind.config={
  darkMode:"class",
  theme:{
    extend:{
      colors:{
        "primary":"#006574",
        "primary-container":"#008092",
        "primary-fixed":"#a4eeff",
        "primary-fixed-dim":"#73d4e9",
        "secondary":"#ac3323",
        "secondary-container":"#fd6e58",
        "secondary-fixed":"#ffdad4",
        "secondary-fixed-dim":"#ffb4a7",
        "tertiary":"#006947",
        "tertiary-container":"#00855b",
        "surface":"#faf8ff",
        "surface-container":"#eaedff",
        "surface-container-low":"#f2f3ff",
        "surface-container-high":"#e2e7ff",
        "surface-container-highest":"#dae2fd",
        "surface-container-lowest":"#ffffff",
        "on-surface":"#131b2e",
        "on-surface-variant":"#3e494b",
        "on-primary":"#ffffff",
        "on-secondary":"#ffffff",
        "outline":"#6e797c",
        "outline-variant":"#bdc8cb"
      },
      spacing:{
        "gutter":"1.5rem",
        "margin":"3rem",
        "margin-sm":"1.25rem",
        "space-xs":"0.25rem",
        "space-sm":"0.5rem",
        "space-md":"1rem",
        "space-lg":"1.5rem",
        "space-xl":"2.5rem"
      }
    }
  }
};
</script>
<meta content="web_standard" name="shell-type" />
</head>
<body class="bg-surface text-on-surface antialiased">

	<jsp:include page="/WEB-INF/top.jsp" />

	<!-- 토스트 안내 팝업 -->
	<div
		class="flex items-center gap-3 px-5 py-3 rounded-2xl bg-on-surface text-surface-container-lowest shadow-2xl border border-surface-container-highest/20"
		id="action-toast">
		<span
			class="material-symbols-outlined text-secondary-container text-[22px]"
			id="toast-icon">check_circle</span>
		<div>
			<p class="font-bold text-[14px]" id="toast-title">안내</p>
			<p class="text-[12px] opacity-80" id="toast-msg">메시지 내용</p>
		</div>
	</div>

	<main class="w-full pt-20 bg-surface">
	
		<!-- ===== PRIVACY POLICY CONTENT START ===== -->
		<div class="flex flex-col w-full">
			<!-- Top Background Ambient Gradient & Header Area -->
			<div class="w-full bg-surface-container-low/50 py-6 lg:py-8">
				<div class="max-w-[1100px] mx-auto px-margin-sm lg:px-margin">
					<!-- Breadcrumb Navigation -->
					<nav aria-label="Breadcrumb"
						class="flex items-center gap-2 mb-4 text-on-surface-variant font-label-md text-label-md">
						<a
							class="hover:text-primary transition-colors flex items-center gap-1"
							href="${pageContext.request.contextPath}/Main"> <span class="material-symbols-outlined text-[16px]">home</span>
							홈
						</a> <span class="text-outline-variant">/</span> <span
							class="hover:text-primary transition-colors cursor-pointer">고객지원
							· 정책</span> <span class="text-outline-variant">/</span> <span
							aria-current="page" class="text-primary font-semibold">개인정보처리방침</span>
					</nav>
					<!-- Badge & Title -->
					<div class="flex flex-wrap items-center gap-2.5 mb-3">
						<span
							class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-primary/10 text-primary font-label-sm text-label-sm">
							<span class="material-symbols-outlined text-[14px]">verified_user</span>
							개인정보 보호 규정 준수
						</span> <span
							class="inline-flex items-center gap-1 px-2.5 py-1 rounded-full bg-surface-container-high text-on-surface-variant font-label-sm text-label-sm">
							버전 v2.3 </span>
					</div>
					<h1
						class="font-headline-xl text-headline-xl text-on-surface mb-3 tracking-tight">
						개인정보처리방침</h1>
					<p
						class="font-body-lg text-body-lg text-on-surface-variant max-w-2xl leading-relaxed">
						놀러가자는 이용자의 소중한 개인정보를 안전하게 보호하며, 관련 법령을 철저히 준수하여 안심하고 여행을 탐색할 수 있는
						신뢰 환경을 조성합니다.</p>
					<!-- Meta Row & Version Selector -->
					<div
						class="mt-6 pt-5 border-t border-surface-container-highest/60 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
						<div
							class="flex items-center gap-4 text-on-surface-variant font-body-sm text-body-sm">
							<span class="flex items-center gap-1.5"> <span
								class="material-symbols-outlined text-[16px] text-primary">calendar_today</span>
								시행일: <strong class="text-on-surface font-semibold">2025.
									03. 15</strong>
							</span> <span class="text-outline-variant">|</span> <span>최종 변경일:
								<strong class="text-on-surface font-semibold">2025. 03.
									15</strong>
							</span>
						</div>
						<div class="relative inline-flex items-center">
							<select aria-label="이전 버전 개인정보처리방침 보기"
								class="appearance-none bg-surface-container-lowest text-on-surface font-label-md text-label-md pl-3.5 pr-8 py-2 rounded-xl shadow-sm focus:outline-none focus:ring-2 focus:ring-primary/40 cursor-pointer transition-all"
								onchange="if(this.value) alert(this.options[this.selectedIndex].text + ' 약관 아카이브로 이동합니다.');">
								<option selected="" value="2025-03-15">현재 버전 (2025. 03.
									15 시행)</option>
								<option value="2024-09-01">이전 버전 (2024. 09. 01 ~ 2025.
									03. 14)</option>
								<option value="2024-01-10">이전 버전 (2024. 01. 10 ~ 2024.
									08. 31)</option>
							</select> <span
								class="material-symbols-outlined text-[18px] text-outline pointer-events-none absolute right-2.5">
								expand_more </span>
						</div>
					</div>
				</div>
			</div>
			<!-- Main Container: Sticky Sidebar TOC + Policy Content -->
			<div
				class="w-full max-w-[1100px] mx-auto px-margin-sm lg:px-margin py-8 lg:py-12">
				<div
					class="flex flex-col lg:flex-row gap-8 lg:gap-12 items-start relative">
					<!-- Mobile/Tablet Quick Jump Dropdown Accordion -->
					<aside
						class="w-full lg:hidden bg-surface-container-lowest rounded-2xl p-4 shadow-sm">
						<details class="group">
							<summary
								class="flex items-center justify-between font-label-lg text-label-lg text-primary cursor-pointer list-none select-none">
								<span class="flex items-center gap-2"> <span
									class="material-symbols-outlined text-[20px]">format_list_bulleted</span>
									목차 바로가기 (전체 12개 조항)
								</span> <span
									class="material-symbols-outlined transition-transform duration-200 group-open:rotate-180">expand_more</span>
							</summary>
							<div
								class="grid grid-cols-1 sm:grid-cols-2 gap-2 mt-4 pt-4 border-t border-surface-container">
								<a
									class="text-on-surface-variant hover:text-primary font-body-sm text-body-sm py-1.5 px-2 rounded-lg hover:bg-surface-container-low transition-colors"
									href="#section-1">1. 수집하는 개인정보 항목</a> <a
									class="text-on-surface-variant hover:text-primary font-body-sm text-body-sm py-1.5 px-2 rounded-lg hover:bg-surface-container-low transition-colors"
									href="#section-2">2. 개인정보의 수집 및 이용 목적</a> <a
									class="text-on-surface-variant hover:text-primary font-body-sm text-body-sm py-1.5 px-2 rounded-lg hover:bg-surface-container-low transition-colors"
									href="#section-3">3. 개인정보의 보유 및 이용기간</a> <a
									class="text-on-surface-variant hover:text-primary font-body-sm text-body-sm py-1.5 px-2 rounded-lg hover:bg-surface-container-low transition-colors"
									href="#section-4">4. 개인정보의 제3자 제공</a> <a
									class="text-on-surface-variant hover:text-primary font-body-sm text-body-sm py-1.5 px-2 rounded-lg hover:bg-surface-container-low transition-colors"
									href="#section-5">5. 개인정보 처리업무의 위탁</a> <a
									class="text-on-surface-variant hover:text-primary font-body-sm text-body-sm py-1.5 px-2 rounded-lg hover:bg-surface-container-low transition-colors"
									href="#section-6">6. 개인정보의 파기 절차 및 방법</a> <a
									class="text-on-surface-variant hover:text-primary font-body-sm text-body-sm py-1.5 px-2 rounded-lg hover:bg-surface-container-low transition-colors"
									href="#section-7">7. 이용자의 권리와 행사방법</a> <a
									class="text-on-surface-variant hover:text-primary font-body-sm text-body-sm py-1.5 px-2 rounded-lg hover:bg-surface-container-low transition-colors"
									href="#section-8">8. 개인정보의 안전성 확보조치</a> <a
									class="text-on-surface-variant hover:text-primary font-body-sm text-body-sm py-1.5 px-2 rounded-lg hover:bg-surface-container-low transition-colors"
									href="#section-9">9. 쿠키 및 유사 기술에 관한 사항</a> <a
									class="text-on-surface-variant hover:text-primary font-body-sm text-body-sm py-1.5 px-2 rounded-lg hover:bg-surface-container-low transition-colors"
									href="#section-10">10. 개인정보 보호책임자 및 문의처</a> <a
									class="text-on-surface-variant hover:text-primary font-body-sm text-body-sm py-1.5 px-2 rounded-lg hover:bg-surface-container-low transition-colors"
									href="#section-11">11. 개인정보처리방침의 변경</a> <a
									class="text-on-surface-variant hover:text-primary font-body-sm text-body-sm py-1.5 px-2 rounded-lg hover:bg-surface-container-low transition-colors"
									href="#section-12">12. 부칙 (시행일)</a>
							</div>
						</details>
					</aside>
					<!-- Desktop Sticky Left TOC Sidebar (~260px) -->
					<aside
						class="hidden lg:block w-[260px] shrink-0 sticky top-28 self-start">
						<div class="bg-surface-container-lowest rounded-2xl p-5 shadow-sm">
							<div
								class="flex items-center gap-2 pb-3 mb-3 border-b border-surface-container">
								<span class="material-symbols-outlined text-[20px] text-primary">menu_book</span>
								<span class="font-headline-sm text-headline-sm text-on-surface">목차
									바로가기</span>
							</div>
							<nav class="flex flex-col space-y-1" id="toc-nav">
								<a
									class="toc-item flex items-center justify-between px-3 py-2 rounded-xl text-on-surface-variant font-label-md text-label-md hover:bg-surface-container-low hover:text-primary transition-all"
									href="#section-1"> <span class="truncate">1. 수집하는
										개인정보 항목</span>
								</a> <a
									class="toc-item flex items-center justify-between px-3 py-2 rounded-xl text-on-surface-variant font-label-md text-label-md hover:bg-surface-container-low hover:text-primary transition-all"
									href="#section-2"> <span class="truncate">2. 수집 및 이용
										목적</span>
								</a> <a
									class="toc-item flex items-center justify-between px-3 py-2 rounded-xl text-on-surface-variant font-label-md text-label-md hover:bg-surface-container-low hover:text-primary transition-all"
									href="#section-3"> <span class="truncate">3. 보유 및
										이용기간</span>
								</a> <a
									class="toc-item flex items-center justify-between px-3 py-2 rounded-xl text-on-surface-variant font-label-md text-label-md hover:bg-surface-container-low hover:text-primary transition-all"
									href="#section-4"> <span class="truncate">4. 개인정보의
										제3자 제공</span>
								</a> <a
									class="toc-item flex items-center justify-between px-3 py-2 rounded-xl text-on-surface-variant font-label-md text-label-md hover:bg-surface-container-low hover:text-primary transition-all"
									href="#section-5"> <span class="truncate">5. 처리업무의
										위탁</span>
								</a> <a
									class="toc-item flex items-center justify-between px-3 py-2 rounded-xl text-on-surface-variant font-label-md text-label-md hover:bg-surface-container-low hover:text-primary transition-all"
									href="#section-6"> <span class="truncate">6. 파기 절차 및
										방법</span>
								</a> <a
									class="toc-item flex items-center justify-between px-3 py-2 rounded-xl text-on-surface-variant font-label-md text-label-md hover:bg-surface-container-low hover:text-primary transition-all"
									href="#section-7"> <span class="truncate">7. 이용자의
										권리와 행사방법</span>
								</a> <a
									class="toc-item flex items-center justify-between px-3 py-2 rounded-xl text-on-surface-variant font-label-md text-label-md hover:bg-surface-container-low hover:text-primary transition-all"
									href="#section-8"> <span class="truncate">8. 안전성
										확보조치</span>
								</a> <a
									class="toc-item flex items-center justify-between px-3 py-2 rounded-xl text-on-surface-variant font-label-md text-label-md hover:bg-surface-container-low hover:text-primary transition-all"
									href="#section-9"> <span class="truncate">9. 쿠키 등 유사
										기술</span>
								</a> <a
									class="toc-item flex items-center justify-between px-3 py-2 rounded-xl text-on-surface-variant font-label-md text-label-md hover:bg-surface-container-low hover:text-primary transition-all"
									href="#section-10"> <span class="truncate">10. 보호책임자
										및 문의처</span>
								</a> <a
									class="toc-item flex items-center justify-between px-3 py-2 rounded-xl text-on-surface-variant font-label-md text-label-md hover:bg-surface-container-low hover:text-primary transition-all"
									href="#section-11"> <span class="truncate">11. 처리방침의
										변경</span>
								</a> <a
									class="toc-item flex items-center justify-between px-3 py-2 rounded-xl text-on-surface-variant font-label-md text-label-md hover:bg-surface-container-low hover:text-primary transition-all"
									href="#section-12"> <span class="truncate">12. 부칙
										(시행일)</span>
								</a>
							</nav>
							<!-- Quick Customer Support Contact Snippet -->
							<div
								class="mt-6 pt-4 border-t border-surface-container bg-surface-container-low/70 rounded-xl p-3">
								<div
									class="flex items-center gap-1.5 text-primary font-label-sm text-label-sm mb-1">
									<span class="material-symbols-outlined text-[16px]">support_agent</span>
									개인정보 보호 전담창구
								</div>
								<p
									class="font-body-sm text-body-sm text-on-surface-variant leading-snug">
									privacy@example.com<br /> 전화: 1588-0000 (평일 9-18시)
								</p>
							</div>
						</div>
					</aside>
					<!-- Main Policy Editorial Content Area (~800px) -->
					<main
						class="flex-1 w-full min-w-0 bg-surface-container-lowest rounded-3xl p-6 sm:p-10 lg:p-12 shadow-sm">
						<!-- Preamble / Introduction Callout -->
						<div class="bg-surface-container-low p-6 rounded-2xl mb-12">
							<p
								class="font-body-md text-body-md text-on-surface leading-relaxed">
								주식회사 놀러가자(이하 “회사”)는 ⌈개인정보 보호법⌉ 제30조에 따라 정보주체의 개인정보를 보호하고 이와 관련한
								고충을 신속하고 원활하게 처리할 수 있도록 하기 위하여 다음과 같이 개인정보 처리방침을 수립·공개합니다.</p>
						</div>
						<!-- SECTION 1 -->
						<section class="scroll-mt-24 mb-16" id="section-1">
							<div class="flex items-center gap-3 mb-4">
								<span
									class="w-8 h-8 rounded-lg bg-primary/10 text-primary flex items-center justify-center font-headline-sm text-headline-sm">1</span>
								<h2 class="font-headline-lg text-headline-lg text-on-surface">수집하는
									개인정보 항목</h2>
							</div>
							<p
								class="font-body-md text-body-md text-on-surface-variant mb-6 leading-relaxed">
								회사는 회원가입, 원활한 고객상담, 맞춤형 국내여행 콘텐츠 추천 및 예약 서비스 제공을 위해 최소한의 개인정보를
								수집하고 있습니다.</p>
							<div class="space-y-4">
								<!-- Item 1: 회원가입 시 -->
								<div class="bg-surface-container-low/60 rounded-2xl p-5">
									<div class="flex items-center justify-between mb-2">
										<span
											class="font-headline-sm text-headline-sm text-on-surface flex items-center gap-2">
											<span
											class="material-symbols-outlined text-primary text-[20px]">person_add</span>
											일반 회원가입 시 수집 항목
										</span>
									</div>
									<div class="grid grid-cols-1 md:grid-cols-2 gap-3 mt-3">
										<div
											class="bg-surface-container-lowest p-3.5 rounded-xl shadow-sm">
											<span
												class="inline-block px-2 py-0.5 rounded font-label-sm text-label-sm bg-primary/10 text-primary mb-1.5 font-bold">필수
												항목</span>
											<p class="font-body-sm text-body-sm text-on-surface">아이디(이메일),
												비밀번호, 이름, 휴대전화번호</p>
										</div>
										<div
											class="bg-surface-container-lowest p-3.5 rounded-xl shadow-sm">
											<span
												class="inline-block px-2 py-0.5 rounded font-label-sm text-label-sm bg-surface-container-highest text-on-surface-variant mb-1.5 font-bold">선택
												항목</span>
											<p class="font-body-sm text-body-sm text-on-surface">일반
												전화번호, 거주지 주소, 여행 선호 테마(자연·역사·미식 등)</p>
										</div>
									</div>
								</div>
								<!-- Item 2: 이메일 인증 시 -->
								<div class="bg-surface-container-low/60 rounded-2xl p-5">
									<div
										class="flex items-center gap-2 mb-2 font-headline-sm text-headline-sm text-on-surface">
										<span
											class="material-symbols-outlined text-primary text-[20px]">mark_email_read</span>
										이메일 인증 및 본인확인 시
									</div>
									<p
										class="font-body-sm text-body-sm text-on-surface-variant mt-1">
										이메일 주소, 인증코드, CI(연계정보), DI(중복가입확인정보), 통신사 정보(휴대폰 본인인증 진행 시)</p>
								</div>
								<!-- Item 3: 서비스 이용 시 자동 생성 정보 -->
								<div class="bg-surface-container-low/60 rounded-2xl p-5">
									<div
										class="flex items-center gap-2 mb-2 font-headline-sm text-headline-sm text-on-surface">
										<span
											class="material-symbols-outlined text-primary text-[20px]">devices</span>
										서비스 이용 과정에서 자동 생성 및 수집되는 항목
									</div>
									<ul
										class="list-disc list-inside space-y-1 font-body-sm text-body-sm text-on-surface-variant mt-2">
										<li>서비스 이용 기록, 방문 일시, 불량 이용 기록, 다운로드 내역</li>
										<li>접속 IP 주소, 브라우저 종류 및 OS 정보, 기기 고유식별값(UUID)</li>
										<li>쿠키(Cookie), 위치 정보(이용자가 위치기반 여행지 검색에 동의한 경우에 한함)</li>
									</ul>
								</div>
							</div>
						</section>
						<!-- SECTION 2 -->
						<section class="scroll-mt-24 mb-16" id="section-2">
							<div class="flex items-center gap-3 mb-4">
								<span
									class="w-8 h-8 rounded-lg bg-primary/10 text-primary flex items-center justify-center font-headline-sm text-headline-sm">2</span>
								<h2 class="font-headline-lg text-headline-lg text-on-surface">개인정보의
									수집 및 이용 목적</h2>
							</div>
							<p
								class="font-body-md text-body-md text-on-surface-variant mb-6 leading-relaxed">
								수집된 개인정보는 다음의 구체적 목적을 위해서만 이용되며, 목적이 변경될 경우에는 사전에 정보주체의 동의를
								구합니다.</p>
							<div class="grid grid-cols-1 md:grid-cols-3 gap-4">
								<div class="p-5 rounded-2xl bg-surface-container-low">
									<div
										class="w-10 h-10 rounded-xl bg-primary text-on-primary flex items-center justify-center mb-3">
										<span class="material-symbols-outlined text-[22px]">badge</span>
									</div>
									<h3
										class="font-headline-sm text-headline-sm text-on-surface mb-2">회원
										가입 및 관리</h3>
									<p
										class="font-body-sm text-body-sm text-on-surface-variant leading-relaxed">
										회원제 서비스 가입의사 확인, 회원제 서비스 제공에 따른 본인 식별·인증, 회원자격 유지·관리, 서비스 부정이용
										방지 및 제한</p>
								</div>
								<div class="p-5 rounded-2xl bg-surface-container-low">
									<div
										class="w-10 h-10 rounded-xl bg-primary-container text-on-primary-container flex items-center justify-center mb-3">
										<span class="material-symbols-outlined text-[22px]">map</span>
									</div>
									<h3
										class="font-headline-sm text-headline-sm text-on-surface mb-2">국내여행
										서비스 제공</h3>
									<p
										class="font-body-sm text-body-sm text-on-surface-variant leading-relaxed">
										지역별 맞춤 여행 콘텐츠 추천, 문화유산·축제 북마크 및 찜 목록 보관, 로컬 가이드 투어 예약 중개 및 일정
										알림 발송</p>
								</div>
								<div class="p-5 rounded-2xl bg-surface-container-low">
									<div
										class="w-10 h-10 rounded-xl bg-secondary-container text-on-secondary flex items-center justify-center mb-3">
										<span class="material-symbols-outlined text-[22px]">headset_mic</span>
									</div>
									<h3
										class="font-headline-sm text-headline-sm text-on-surface mb-2">고객지원
										및 분쟁처리</h3>
									<p
										class="font-body-sm text-body-sm text-on-surface-variant leading-relaxed">
										문의사항 접수 및 사실확인, 처리결과 통보, 서비스 개선을 위한 통계학적 분석, 약관 개정 등 중요 공지사항
										고지</p>
								</div>
							</div>
						</section>
						<!-- SECTION 3 -->
						<section class="scroll-mt-24 mb-16" id="section-3">
							<div class="flex items-center gap-3 mb-4">
								<span
									class="w-8 h-8 rounded-lg bg-primary/10 text-primary flex items-center justify-center font-headline-sm text-headline-sm">3</span>
								<h2 class="font-headline-lg text-headline-lg text-on-surface">개인정보의
									보유 및 이용기간</h2>
							</div>
							<p
								class="font-body-md text-body-md text-on-surface-variant mb-6 leading-relaxed">
								회사는 법령에 따른 개인정보 보유·이용기간 또는 정보주체로부터 수집 시에 동의받은 기간 내에서 개인정보를 처리 및
								보유하며, 회원 탈퇴 시 원칙적으로 지체 없이 파기합니다.</p>
							<div
								class="overflow-x-auto rounded-2xl bg-surface-container-low/40 p-1">
								<table class="w-full text-left font-body-sm text-body-sm">
									<thead class="bg-surface-container-high/60 text-on-surface">
										<tr>
											<th class="py-3 px-4 rounded-l-xl font-bold">보존 근거 법령</th>
											<th class="py-3 px-4 font-bold">보존 항목</th>
											<th class="py-3 px-4 rounded-r-xl font-bold">보유 기간</th>
										</tr>
									</thead>
									<tbody class="divide-y divide-surface-container">
										<tr
											class="hover:bg-surface-container-lowest/80 transition-colors">
											<td class="py-3.5 px-4 font-medium text-on-surface">전자상거래
												등에서의 소비자보호에 관한 법률</td>
											<td class="py-3.5 px-4 text-on-surface-variant">계약 또는
												청약철회 등에 관한 기록</td>
											<td class="py-3.5 px-4 font-bold text-primary">5년</td>
										</tr>
										<tr
											class="hover:bg-surface-container-lowest/80 transition-colors">
											<td class="py-3.5 px-4 font-medium text-on-surface">전자상거래
												등에서의 소비자보호에 관한 법률</td>
											<td class="py-3.5 px-4 text-on-surface-variant">대금결제 및
												재화 등의 공급에 관한 기록</td>
											<td class="py-3.5 px-4 font-bold text-primary">5년</td>
										</tr>
										<tr
											class="hover:bg-surface-container-lowest/80 transition-colors">
											<td class="py-3.5 px-4 font-medium text-on-surface">전자상거래
												등에서의 소비자보호에 관한 법률</td>
											<td class="py-3.5 px-4 text-on-surface-variant">소비자의 불만
												또는 분쟁처리에 관한 기록</td>
											<td class="py-3.5 px-4 font-bold text-primary">3년</td>
										</tr>
										<tr
											class="hover:bg-surface-container-lowest/80 transition-colors">
											<td class="py-3.5 px-4 font-medium text-on-surface">통신비밀보호법</td>
											<td class="py-3.5 px-4 text-on-surface-variant">서비스 접속
												로그기록, IP 주소</td>
											<td class="py-3.5 px-4 font-bold text-primary">3개월</td>
										</tr>
									</tbody>
								</table>
							</div>
						</section>
						<!-- SECTION 4 -->
						<section class="scroll-mt-24 mb-16" id="section-4">
							<div class="flex items-center gap-3 mb-4">
								<span
									class="w-8 h-8 rounded-lg bg-primary/10 text-primary flex items-center justify-center font-headline-sm text-headline-sm">4</span>
								<h2 class="font-headline-lg text-headline-lg text-on-surface">개인정보의
									제3자 제공</h2>
							</div>
							<p
								class="font-body-md text-body-md text-on-surface-variant mb-4 leading-relaxed">
								회사는 이용자의 사전 동의 없이 개인정보를 외부에 제공하지 않습니다. 단, 다음의 경우에는 예외로 합니다:</p>
							<ul
								class="space-y-2 font-body-sm text-body-sm text-on-surface-variant pl-2 mb-4">
								<li class="flex items-start gap-2"><span
									class="material-symbols-outlined text-primary text-[18px] shrink-0 mt-0.5">check</span>
									<span><strong>이용자가 사전에 제3자 제공에 직접 동의한 경우:</strong> 로컬
										해설사 가이드 투어 예약 시 예약자 이름, 연락처를 해당 해설사/가이드에게 전달</span></li>
								<li class="flex items-start gap-2"><span
									class="material-symbols-outlined text-primary text-[18px] shrink-0 mt-0.5">check</span>
									<span><strong>법령의 규정에 의거하거나:</strong> 수사 목적으로 법령에 정해진
										절차와 방법에 따라 수사기관의 요구가 있는 경우</span></li>
							</ul>
						</section>
						<!-- SECTION 5 -->
						<section class="scroll-mt-24 mb-16" id="section-5">
							<div class="flex items-center gap-3 mb-4">
								<span
									class="w-8 h-8 rounded-lg bg-primary/10 text-primary flex items-center justify-center font-headline-sm text-headline-sm">5</span>
								<h2 class="font-headline-lg text-headline-lg text-on-surface">개인정보
									처리업무의 위탁</h2>
							</div>
							<p
								class="font-body-md text-body-md text-on-surface-variant mb-4 leading-relaxed">
								회사는 원활한 서비스 제공과 전문성 강화를 위해 일부 개인정보 처리업무를 아래와 같이 외부 전문기관에 위탁하여
								운영하고 있습니다:</p>
							<div class="grid grid-cols-1 sm:grid-cols-3 gap-3">
								<div class="p-4 rounded-xl bg-surface-container-low">
									<span
										class="font-label-sm text-label-sm text-primary font-bold">본인
										확인 및 인증</span>
									<p
										class="font-headline-sm text-headline-sm text-on-surface mt-1">NICE평가정보(주)</p>
									<p
										class="font-body-sm text-body-sm text-on-surface-variant mt-0.5">휴대폰
										본인인증 및 본인식별</p>
								</div>
								<div class="p-4 rounded-xl bg-surface-container-low">
									<span
										class="font-label-sm text-label-sm text-primary font-bold">결제
										서비스 대행</span>
									<p
										class="font-headline-sm text-headline-sm text-on-surface mt-1">토스페이먼츠(주)</p>
									<p
										class="font-body-sm text-body-sm text-on-surface-variant mt-0.5">가이드
										투어 결제 및 에스크로</p>
								</div>
								<div class="p-4 rounded-xl bg-surface-container-low">
									<span
										class="font-label-sm text-label-sm text-primary font-bold">클라우드
										인프라 보관</span>
									<p
										class="font-headline-sm text-headline-sm text-on-surface mt-1">네이버클라우드(주)</p>
									<p
										class="font-body-sm text-body-sm text-on-surface-variant mt-0.5">데이터베이스
										저장 및 전산 관리</p>
								</div>
							</div>
						</section>
						<!-- SECTION 6 -->
						<section class="scroll-mt-24 mb-16" id="section-6">
							<div class="flex items-center gap-3 mb-4">
								<span
									class="w-8 h-8 rounded-lg bg-primary/10 text-primary flex items-center justify-center font-headline-sm text-headline-sm">6</span>
								<h2 class="font-headline-lg text-headline-lg text-on-surface">개인정보의
									파기 절차 및 방법</h2>
							</div>
							<div
								class="space-y-3 font-body-sm text-body-sm text-on-surface-variant leading-relaxed">
								<p>회사는 개인정보 보유기간의 경과, 처리목적 달성 등 개인정보가 불필요하게 되었을 때에는 지체없이 해당
									개인정보를 복구할 수 없는 방법으로 파기합니다.</p>
								<div class="grid grid-cols-1 sm:grid-cols-2 gap-4 mt-2">
									<div class="p-4 rounded-xl bg-surface-container-low">
										<span
											class="font-headline-sm text-headline-sm text-on-surface flex items-center gap-1.5 mb-1">
											<span
											class="material-symbols-outlined text-primary text-[18px]">delete_forever</span>
											전자적 파일 형태
										</span>
										<p class="text-on-surface-variant">재생할 수 없도록 기술적 방법(로우레벨
											포맷, 오버라이트 등)을 사용하여 영구 삭제합니다.</p>
									</div>
									<div class="p-4 rounded-xl bg-surface-container-low">
										<span
											class="font-headline-sm text-headline-sm text-on-surface flex items-center gap-1.5 mb-1">
											<span
											class="material-symbols-outlined text-primary text-[18px]">save_as</span>
											인쇄물 및 종이 문서
										</span>
										<p class="text-on-surface-variant">분쇄기로 완전 분쇄하거나 전문 소각 업체를
											통해 파쇄 및 소각 처리합니다.</p>
									</div>
								</div>
							</div>
						</section>
						<!-- SECTION 7 -->
						<section class="scroll-mt-24 mb-16" id="section-7">
							<div class="flex items-center gap-3 mb-4">
								<span
									class="w-8 h-8 rounded-lg bg-primary/10 text-primary flex items-center justify-center font-headline-sm text-headline-sm">7</span>
								<h2 class="font-headline-lg text-headline-lg text-on-surface">이용자의
									권리와 행사방법</h2>
							</div>
							<div
								class="font-body-md text-body-md text-on-surface-variant space-y-3 leading-relaxed">
								<p>정보주체는 회사에 대해 언제든지 개인정보 열람·정정·삭제·처리정지 요구 등의 권리를 행사할 수
									있습니다.</p>
								<div class="p-5 rounded-2xl bg-surface-container-low">
									<ul class="space-y-2 font-body-sm text-body-sm text-on-surface">
										<li class="flex items-start gap-2"><span
											class="material-symbols-outlined text-primary text-[18px] shrink-0">check_circle</span>
											<span><strong>마이페이지 내 직접 수정:</strong> [마이페이지] &gt;
												[회원정보 수정] 메뉴에서 언제든지 열람 및 정정이 가능합니다.</span></li>
										<li class="flex items-start gap-2"><span
											class="material-symbols-outlined text-primary text-[18px] shrink-0">check_circle</span>
											<span><strong>회원 탈퇴:</strong> [마이페이지] &gt; [회원 탈퇴]를 통해
												본인 확인 후 즉각 처리가 가능합니다.</span></li>
										<li class="flex items-start gap-2"><span
											class="material-symbols-outlined text-primary text-[18px] shrink-0">check_circle</span>
											<span><strong>서면 및 전자우편 신청:</strong> 개인정보 보호책임자 또는
												고객센터로 서면, 전화, 이메일로 요청 시 지체 없이 조치합니다.</span></li>
									</ul>
								</div>
							</div>
						</section>
						<!-- SECTION 8: KEY HIGHLIGHT SECTION (안전성 확보조치) -->
						<section class="scroll-mt-24 mb-16" id="section-8">
							<div class="flex items-center gap-3 mb-3">
								<span
									class="w-8 h-8 rounded-lg bg-primary text-on-primary flex items-center justify-center font-headline-sm text-headline-sm">8</span>
								<h2 class="font-headline-lg text-headline-lg text-on-surface">개인정보의
									안전성 확보조치</h2>
							</div>
							<!-- Highlight Banner -->
							<div
								class="p-6 rounded-2xl bg-gradient-to-r from-primary/10 via-primary/5 to-transparent mb-6">
								<div
									class="flex items-center gap-2 text-primary font-headline-sm text-headline-sm mb-1">
									<span class="material-symbols-outlined text-[24px]">security</span>
									놀러가자는 철저한 개인정보 보호를 실천합니다
								</div>
								<p class="font-body-sm text-body-sm text-on-surface-variant">
									개인정보의 분실, 도난, 유출, 위조 또는 변조를 방지하기 위해 법정 기준 이상의 기술적·관리적·물리적 보호
									조치를 취하고 있습니다.</p>
							</div>
							<!-- 4 Feature Cards Grid -->
							<div class="grid grid-cols-1 md:grid-cols-2 gap-4">
								<!-- Card 1 -->
								<div class="p-5 rounded-2xl bg-surface-container-low shadow-sm">
									<div
										class="w-10 h-10 rounded-xl bg-primary/15 text-primary flex items-center justify-center mb-3">
										<span class="material-symbols-outlined text-[22px]">admin_panel_settings</span>
									</div>
									<h3
										class="font-headline-sm text-headline-sm text-on-surface mb-1">1)
										접근 권한 최소화 관리</h3>
									<p
										class="font-body-sm text-body-sm text-on-surface-variant leading-relaxed">
										개인정보를 처리하는 담당 직원을 최소한으로 한정하고, 접근 권한 부여·변경·말소에 대한 전산 이력을 최소 2년간
										철저히 보관합니다.</p>
								</div>
								<!-- Card 2 -->
								<div class="p-5 rounded-2xl bg-surface-container-low shadow-sm">
									<div
										class="w-10 h-10 rounded-xl bg-primary/15 text-primary flex items-center justify-center mb-3">
										<span class="material-symbols-outlined text-[22px]">lock_reset</span>
									</div>
									<h3
										class="font-headline-sm text-headline-sm text-on-surface mb-1">2)
										안전한 비밀번호 일방향 암호화</h3>
									<p
										class="font-body-sm text-body-sm text-on-surface-variant leading-relaxed">
										회원의 비밀번호는 복호화가 불가능한 일방향 솔트(Salt) 해시 알고리즘으로 암호화되어 관리자조차 열람할 수
										없도록 격리 저장됩니다.</p>
								</div>
								<!-- Card 3 -->
								<div class="p-5 rounded-2xl bg-surface-container-low shadow-sm">
									<div
										class="w-10 h-10 rounded-xl bg-primary/15 text-primary flex items-center justify-center mb-3">
										<span class="material-symbols-outlined text-[22px]">vpn_lock</span>
									</div>
									<h3
										class="font-headline-sm text-headline-sm text-on-surface mb-1">3)
										전송구간 SSL/TLS 암호화</h3>
									<p
										class="font-body-sm text-body-sm text-on-surface-variant leading-relaxed">
										네트워크를 통한 데이터 전송 시 256비트 SSL/TLS 보안 프로토콜을 적용하여 데이터 유출 위험을 원천
										차단합니다.</p>
								</div>
								<!-- Card 4 -->
								<div class="p-5 rounded-2xl bg-surface-container-low shadow-sm">
									<div
										class="w-10 h-10 rounded-xl bg-primary/15 text-primary flex items-center justify-center mb-3">
										<span class="material-symbols-outlined text-[22px]">monitoring</span>
									</div>
									<h3
										class="font-headline-sm text-headline-sm text-on-surface mb-1">4)
										24시간 침입 탐지 및 감시</h3>
									<p
										class="font-body-sm text-body-sm text-on-surface-variant leading-relaxed">
										해킹 및 컴퓨터 바이러스에 대응하기 위해 침입 차단 시스템(WAF)과 실시간 보안 관제 체계를 24시간 365일
										운영하고 있습니다.</p>
								</div>
							</div>
						</section>
						<!-- SECTION 9 -->
						<section class="scroll-mt-24 mb-16" id="section-9">
							<div class="flex items-center gap-3 mb-4">
								<span
									class="w-8 h-8 rounded-lg bg-primary/10 text-primary flex items-center justify-center font-headline-sm text-headline-sm">9</span>
								<h2 class="font-headline-lg text-headline-lg text-on-surface">쿠키
									및 유사 기술에 관한 사항</h2>
							</div>
							<div
								class="font-body-sm text-body-sm text-on-surface-variant space-y-3 leading-relaxed">
								<p>회사는 이용자에게 편리한 맞춤 서비스를 제공하기 위해 이용정보를 저장하고 수시로 불러오는
									‘쿠키(Cookie)’를 사용합니다.</p>
								<div class="p-5 rounded-2xl bg-surface-container-low">
									<h3
										class="font-headline-sm text-headline-sm text-on-surface mb-2">쿠키
										설치·운영 및 거부 방법</h3>
									<ul
										class="list-disc list-inside space-y-1 text-on-surface-variant">
										<li><strong>Chrome:</strong> 웹브라우저 우측 상단 설정 &gt; 개인정보 보호
											및 보안 &gt; 인터넷 사용 기록 삭제 또는 쿠키 설정</li>
										<li><strong>Safari:</strong> 환경설정 &gt; 개인정보 보호 &gt; 모든 쿠키
											차단</li>
										<li><strong>Edge:</strong> 설정 &gt; 쿠키 및 사이트 권한 &gt; 쿠키 및
											사이트 데이터 관리 및 삭제</li>
									</ul>
									<p class="mt-3 text-outline font-label-sm text-label-sm">*
										쿠키 저장을 거부할 경우 맞춤 여행지 추천 등 일부 기능 이용에 어려움이 있을 수 있습니다.</p>
								</div>
							</div>
						</section>
						<!-- SECTION 10: PRIVACY OFFICER & INQUIRY -->
						<section class="scroll-mt-24 mb-16" id="section-10">
							<div class="flex items-center gap-3 mb-4">
								<span
									class="w-8 h-8 rounded-lg bg-primary text-on-primary flex items-center justify-center font-headline-sm text-headline-sm">10</span>
								<h2 class="font-headline-lg text-headline-lg text-on-surface">개인정보
									보호책임자 및 문의처</h2>
							</div>
							<p
								class="font-body-md text-body-md text-on-surface-variant mb-6 leading-relaxed">
								회사는 개인정보 처리에 관한 업무를 총괄해서 책임지고, 정보주체의 고충처리 및 피해구제 등을 위하여 아래와 같이
								개인정보 보호책임자를 지정하고 있습니다.</p>
							<!-- Contact Highlight Box -->
							<div
								class="p-6 sm:p-8 rounded-3xl bg-surface-container-low shadow-sm">
								<div
									class="flex items-center gap-2 mb-4 pb-3 border-b border-surface-container-highest">
									<span
										class="material-symbols-outlined text-primary text-[24px]">verified</span>
									<h3 class="font-headline-md text-headline-md text-on-surface">개인정보
										관련 전담 창구</h3>
								</div>
								<div class="grid grid-cols-1 sm:grid-cols-2 gap-y-4 gap-x-8">
									<div>
										<span class="font-label-sm text-label-sm text-outline">서비스명</span>
										<p
											class="font-body-md text-body-md text-on-surface font-semibold mt-0.5">놀러가자
											(Noleogaja)</p>
									</div>
									<div>
										<span class="font-label-sm text-label-sm text-outline">개인정보
											보호책임자</span>
										<p
											class="font-body-md text-body-md text-on-surface font-semibold mt-0.5">홍길동
											CISO / 정보보호팀장</p>
									</div>
									<div>
										<span class="font-label-sm text-label-sm text-outline">대표
											전자우편</span>
										<p
											class="font-body-md text-body-md text-primary font-semibold mt-0.5">privacy@example.com</p>
									</div>
									<div>
										<span class="font-label-sm text-label-sm text-outline">고객행복센터
											전화</span>
										<p
											class="font-body-md text-body-md text-on-surface font-semibold mt-0.5">1588-0000</p>
									</div>
									<div class="sm:col-span-2">
										<span class="font-label-sm text-label-sm text-outline">업무
											상담 시간</span>
										<p
											class="font-body-md text-body-md text-on-surface font-medium mt-0.5">평일
											09:00 ~ 18:00 (점심시간 12:00~13:00 / 토·일·공휴일 휴무)</p>
									</div>
								</div>
								<div
									class="mt-6 pt-5 border-t border-surface-container-highest/80">
									<p class="font-body-sm text-body-sm text-on-surface-variant">
										기타 개인정보 침해에 대한 신고나 상담이 필요한 경우 정부 산하 전문기관에 문의하실 수 있습니다:</p>
									<div
										class="flex flex-wrap gap-x-6 gap-y-2 mt-2 font-label-md text-label-md text-primary">
										<a class="hover:underline" href="https://privacy.kisa.or.kr"
											rel="noopener noreferrer" target="_blank">개인정보침해신고센터
											(118)</a> <a class="hover:underline"
											href="https://www.kopico.go.kr" rel="noopener noreferrer"
											target="_blank">개인정보분쟁조정위원회 (1833-6972)</a> <a
											class="hover:underline" href="https://spo.go.kr"
											rel="noopener noreferrer" target="_blank">대검찰청 사이버수사과
											(1301)</a> <a class="hover:underline" href="https://police.go.kr"
											rel="noopener noreferrer" target="_blank">경찰청 사이버수사국
											(182)</a>
									</div>
								</div>
							</div>
						</section>
						<!-- SECTION 11 -->
						<section class="scroll-mt-24 mb-16" id="section-11">
							<div class="flex items-center gap-3 mb-4">
								<span
									class="w-8 h-8 rounded-lg bg-primary/10 text-primary flex items-center justify-center font-headline-sm text-headline-sm">11</span>
								<h2 class="font-headline-lg text-headline-lg text-on-surface">개인정보처리방침의
									변경</h2>
							</div>
							<p
								class="font-body-md text-body-md text-on-surface-variant leading-relaxed">
								이 개인정보처리방침은 법령·정책 또는 보안기술의 변경에 따라 내용의 추가·삭제 및 수정이 있을 시에는 변경사항의
								시행 7일 전부터 웹사이트 공지사항을 통하여 고지할 것입니다. 단, 이용자의 권리에 중대한 변경이 발생하는 경우에는
								최소 30일 전에 사전 공지합니다.</p>
						</section>
						<!-- SECTION 12 -->
						<section class="scroll-mt-24" id="section-12">
							<div class="flex items-center gap-3 mb-4">
								<span
									class="w-8 h-8 rounded-lg bg-primary/10 text-primary flex items-center justify-center font-headline-sm text-headline-sm">12</span>
								<h2 class="font-headline-lg text-headline-lg text-on-surface">부칙</h2>
							</div>
							<div
								class="p-5 rounded-2xl bg-surface-container-low font-body-sm text-body-sm text-on-surface-variant space-y-2">
								<p>
									<strong>제1조 (시행일)</strong> 본 개인정보처리방침은 2025년 3월 15일부터 시행됩니다.
								</p>
								<p>
									<strong>제2조 (경과조치)</strong> 2024년 9월 1일부터 적용되던 이전 개인정보처리방침은 본
									방침의 시행으로 대체됩니다.
								</p>
							</div>
						</section>
					</main>
				</div>
			</div>
		</div>
		<!-- ===== PRIVACY POLICY CONTENT END ===== -->
		<script>
  // Pure Vanilla JS for smooth active TOC scroll observer
  document.addEventListener('DOMContentLoaded', () => {
    const sections = document.querySelectorAll('section[id^="section-"]');
    const navLinks = document.querySelectorAll('.toc-item');

    if (!sections.length || !navLinks.length) return;

    const observerOptions = {
      root: null,
      rootMargin: '-100px 0px -60% 0px',
      threshold: 0
    };

    const observer = new IntersectionObserver((entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          const id = entry.target.getAttribute('id');
          navLinks.forEach((link) => {
            const href = link.getAttribute('href');
            if (href === '#' + id) {
              link.classList.add('bg-primary', 'text-white', 'font-semibold', 'shadow-sm');
              link.classList.remove('text-on-surface-variant', 'hover:bg-surface-container-low');
            } else {
              link.classList.remove('bg-primary', 'text-white', 'font-semibold', 'shadow-sm');
              link.classList.add('text-on-surface-variant', 'hover:bg-surface-container-low');
            }
          });
        }
      });
    }, observerOptions);

    sections.forEach((sec) => observer.observe(sec));
  });
</script>
	</main>
	

<jsp:include page="/WEB-INF/liveChat/chat.jsp" />
<jsp:include page="/WEB-INF/bottom.jsp" />

</body>
</html>