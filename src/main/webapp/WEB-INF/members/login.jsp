<%-- =========================================================
     주제: 파일 역할
     설명: members 영역의 login 화면을 구성하고 서버에서 전달받은 데이터를 표시
========================================================= --%>
<%-- 이 페이지의 글자 인코딩을 UTF-8 로 정한다. 빠뜨리면 한글이 깨진다 --%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>



<!DOCTYPE html>



<html lang="ko">

<head>

<meta charset="utf-8" />

<meta content="width=device-width, initial-scale=1.0" name="viewport" />

<meta content="web_standard" name="shell-type" />

<title>놀러가자 - 대한민국 감성 여행 큐레이션</title>

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

<link
	href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap"
	rel="stylesheet" />

<style>
@
layer base {
	html ,body{margin: 0;
	padding: 0;
	font-family: 'Plus Jakarta Sans', 'Noto Sans KR', sans-serif;
}

body {
	overscroll-behavior: none;
}

main>:first-child {
	margin-top: 0 !important;
}

main>:last-child {
	margin-bottom: 0 !important;
}

}
::-webkit-scrollbar {
	display: none;
}
</style>

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

<body class="bg-surface font-body-md text-on-surface antialiased">



	<jsp:include page="/WEB-INF/top.jsp" />



	<main class="w-full bg-surface">

		<div class="flex flex-col w-full">

			<div
				class="relative w-full py-space-xl flex items-center justify-center min-h-[calc(100vh-5rem)] overflow-hidden">

				<!-- Subtle Ambient Graphic Elements -->

				<div
					class="absolute -top-24 -left-24 w-96 h-96 bg-primary-fixed/30 rounded-full blur-3xl pointer-events-none"></div>

				<div
					class="absolute top-1/2 -right-32 w-80 h-80 bg-secondary-fixed/40 rounded-full blur-3xl pointer-events-none"></div>

				<div
					class="absolute -bottom-20 left-1/3 w-72 h-72 bg-tertiary-fixed/20 rounded-full blur-3xl pointer-events-none"></div>

				<div class="w-full max-w-[460px] mx-auto px-margin-sm relative z-10">

					<!-- Main Glassmorphic Card Container -->

					<div
						class="bg-surface-container-lowest/95 backdrop-blur-xl rounded-[20px] shadow-[0_12px_40px_-8px_rgba(19,27,46,0.08)] px-space-lg pb-space-lg pt-space-sm">

						<!-- Header Branding & Subtext -->

						<div class="flex flex-col items-center text-center mb-space-lg">

							<div class="w-44 mb-space-sm flex justify-center items-center">

								<img
									src="${pageContext.request.contextPath}/resources/images/logo.svg"
									alt="놀러가자 로고" class="w-30 h-30 object-contain" />

							</div>

							<div
								class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-surface-container text-primary font-label-sm text-label-sm mb-space-xs">

								<span class="material-symbols-outlined text-[14px]">explore</span>

								<span>대한민국 구석구석, 여행의 시작</span>

							</div>

							<h1
								class="font-headline-lg text-headline-lg font-bold text-on-surface tracking-tight mt-1">

								로그인</h1>

							<p class="font-body-sm text-body-sm text-on-surface-variant mt-1">

								나만의 감성 가득한 여정을 계속해보세요</p>

						</div>

						<!-- Controller에서 전달한 로그인 실패 메시지 -->
						<%
						if (request.getAttribute("loginError") != null) {
						%>
						<div role="alert"
							class="mb-4 rounded-xl bg-red-50 px-4 py-3 text-sm text-red-700">
							<%=String.valueOf(request.getAttribute("loginError")).replace("&", "&amp;").replace("<", "&lt;")
		.replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;")%>
						</div>
						<%
						}
						%>

						<!-- Semantic Login Form -->

						<form action="${pageContext.request.contextPath}/Login"
							class="space-y-space-md" id="join" method="post"
							onsubmit="return handleFormSubmit(event)">

							<!-- 권성애 추가 2026.10.14 로그인 후 이전 페이지 이동위해 -->
							<input type="hidden" name="redirect"
								value="${fn:escapeXml(param.redirect)}">

							<!-- User ID Input -->

							<div class="space-y-1.5">

								<label
									class="block font-label-md text-label-md font-semibold text-on-surface"
									for="member_id"> 아이디 </label>

								<div class="relative flex items-center">

									<span
										class="material-symbols-outlined absolute left-3.5 text-on-surface-variant/70 text-[20px] pointer-events-none transition-colors group-focus-within:text-primary">

										alternate_email </span> <input
										class="w-full pl-11 pr-4 py-3.5 bg-surface-container-low/70 hover:bg-surface-container-low text-on-surface font-body-md text-body-md rounded-xl transition-all duration-200 outline-none placeholder:text-outline/70 focus:bg-surface-container-lowest focus:shadow-[0_0_0_2px_#008092,0_4px_16px_rgba(0,101,116,0.12)]"
										id="member_id" name="id" placeholder="아이디를 입력해주세요" required=""
										type="text" />

								</div>

							</div>

							<!-- Password Input -->

							<div class="space-y-1.5">

								<label
									class="block font-label-md text-label-md font-semibold text-on-surface"
									for="member_pw"> 비밀번호 </label>

								<div class="relative flex items-center">

									<span
										class="material-symbols-outlined absolute left-3.5 text-on-surface-variant/70 text-[20px] pointer-events-none transition-colors">

										lock </span> <input
										class="w-full pl-11 pr-11 py-3.5 bg-surface-container-low/70 hover:bg-surface-container-low text-on-surface font-body-md text-body-md rounded-xl transition-all duration-200 outline-none placeholder:text-outline/70 focus:bg-surface-container-lowest focus:shadow-[0_0_0_2px_#008092,0_4px_16px_rgba(0,101,116,0.12)]"
										id="member_pw" name="passwd" placeholder="비밀번호를 입력해주세요"
										required="" type="password" />

									<button aria-label="비밀번호 표시/숨기기"
										class="absolute right-3 p-1 rounded-lg text-on-surface-variant/70 hover:text-on-surface hover:bg-surface-container transition-colors flex items-center justify-center"
										id="pwToggleBtn" onclick="togglePasswordVisibility()"
										type="button">

										<span class="material-symbols-outlined text-[20px]"
											id="pwToggleIcon"> visibility_off </span>

									</button>

								</div>

							</div>

							<!-- Form Extras: Remember ID & Recovery Links -->

							<div
								class="flex items-center justify-between pt-1 font-body-sm text-body-sm">

								<div
									class="flex items-center justify-between pt-1 font-body-sm text-body-sm">

									<label
										class="inline-flex items-center gap-2 cursor-pointer select-none group">

										<input class="w-5 h-5 rounded accent-primary cursor-pointer"
										id="remember_id" name="remember_id" type="checkbox" /> <span
										class="text-on-surface-variant
							                   group-hover:text-on-surface
							                   transition-colors
							                   font-label-md
							                   text-label-md">아이디
											저장 </span>
									</label>

								</div>
								<div
									class="flex items-center gap-1.5 text-on-surface-variant font-label-sm text-label-sm">

									<a class="hover:text-primary transition-colors py-0.5"
										href="<%=request.getContextPath()%>/FindId"> 아이디 찾기 </a> <span
										class="text-outline-variant">·</span> <a
										class="hover:text-primary transition-colors py-0.5"
										href="<%=request.getContextPath()%>/FindPassword"> 비밀번호 찾기
									</a>

								</div>

							</div>

							<!-- Primary Submit Button -->

							<div class="pt-2">

								<button
									class="w-full h-12 bg-primary hover:bg-primary-container text-on-primary font-headline-sm text-headline-sm font-bold rounded-xl shadow-[0_6px_20px_rgba(0,101,116,0.25)] hover:shadow-[0_8px_24px_rgba(0,101,116,0.35)] transition-all duration-200 transform active:scale-[0.99] flex items-center justify-center gap-2 cursor-pointer"
									type="submit">

									<span>로그인</span> <span
										class="material-symbols-outlined text-[20px]">arrow_forward</span>

								</button>

							</div>

						</form>

						<!-- Divider -->

						<div class="relative my-space-lg flex items-center justify-center">

							<div class="absolute inset-0 flex items-center">

								<div class="w-full h-px bg-surface-container-high"></div>

							</div>

							<div
								class="relative px-3 bg-surface-container-lowest text-on-surface-variant/70 font-label-sm text-label-sm uppercase tracking-wider">

								또는</div>

						</div>

						<!-- Kakao Quick Login -->

						<div>

							<a
								class="w-full h-12 bg-[#FEE500] hover:bg-[#FADA0A]
						               text-[#191919]
						               font-label-lg text-label-lg font-bold
						               rounded-xl
						               transition-all duration-200
						               flex items-center justify-center gap-2.5
						               shadow-[0_3px_12px_rgba(254,229,0,0.35)]
						               active:scale-[0.99]"
								href="${pageContext.request.contextPath}/KakaoLogin?redirect=${fn:escapeXml(param.redirect)}">
								<!-- 카카오 말풍선 아이콘 --> <svg class="w-5 h-5 flex-shrink-0"
									fill="currentColor" viewBox="0 0 24 24">

           		    <path
										d="M12 3C6.477 3 2 6.477 2 10.772c0 2.723 1.782 5.12 4.502 6.472l-1.15 4.223c-.104.382.284.708.621.523l4.982-2.732c.343.037.69.056 1.045.056 5.523 0 10-3.477 10-7.772C22 6.477 17.523 3 12 3z"></path>
        			</svg> <span>카카오로 3초 만에 시작하기</span>

							</a>

						</div>
						<!-- Sign Up Guide -->

						<div
							class="mt-space-lg text-center font-body-sm text-body-sm text-on-surface-variant flex items-center justify-center gap-1.5">

							<span>아직 놀러가자 회원이 아니신가요?</span> <a
								class="font-bold text-secondary-container hover:text-secondary inline-flex items-center gap-0.5 transition-colors"
								href="${pageContext.request.contextPath}/Register"> <span>회원가입하기</span>

								<span class="material-symbols-outlined text-[16px]">chevron_right</span>

							</a>

						</div>

						<!-- Welcome Benefit Micro Banner -->

						<div
							class="mt-space-md p-3.5 rounded-xl bg-surface-container-low flex items-center gap-3">

							<div
								class="w-9 h-9 rounded-lg bg-secondary-container/15 flex items-center justify-center flex-shrink-0 text-secondary-container">

								<span class="material-symbols-outlined text-[20px]">loyalty</span>

							</div>

							<div class="min-w-0">

								<div class="flex items-center gap-1.5">

									<span
										class="font-label-sm text-label-sm font-bold text-secondary-container">신규회원

										환영</span> <span class="font-label-sm text-label-sm text-outline">놀러가자</span>

								</div>

								<p
									class="font-body-sm text-body-sm text-on-surface font-medium truncate">

									국내 숙박 & 감성 액티비티</p>

							</div>

						</div>

					</div>

					<!-- Trust Badges & Safe Browsing Notice -->

					<div class="mt-space-md text-center">

						<div
							class="inline-flex items-center gap-2 text-outline font-label-sm text-label-sm">

							<span class="material-symbols-outlined text-[16px]">verified_user</span>

							<span>안전한 256-bit SSL 암호화 데이터 보호</span>

						</div>

					</div>

				</div>

			</div>

			<script>
				// ========================================
				// 아이디 저장 기능
				// ========================================

				document.addEventListener('DOMContentLoaded', function() {

					const idInput = document.getElementById('member_id');
					const rememberCheckbox = document
							.getElementById('remember_id');

					if (!idInput || !rememberCheckbox) {
						return;
					}

					// 이전에 저장한 아이디 가져오기
					const savedId = localStorage.getItem('noleogaja_saved_id');

					if (savedId) {
						idInput.value = savedId;
						rememberCheckbox.checked = true;
					}

					// 아이디 저장 체크 해제 시 삭제
					rememberCheckbox.addEventListener('change', function() {

						if (!rememberCheckbox.checked) {
							localStorage.removeItem('noleogaja_saved_id');
						}

					});

				});

				function togglePasswordVisibility() {

					const pwInput = document.getElementById('member_pw');

					const pwIcon = document.getElementById('pwToggleIcon');

					if (!pwInput || !pwIcon)

						return;

					if (pwInput.type === 'password') {

						pwInput.type = 'text';

						pwIcon.textContent = 'visibility';

					} else {

						pwInput.type = 'password';

						pwIcon.textContent = 'visibility_off';

					}

				}

				function handleFormSubmit(event) {

					const idInput = document.getElementById('member_id');
					const pwInput = document.getElementById('member_pw');
					const rememberCheckbox = document
							.getElementById('remember_id');

					// 아이디 입력 확인
					if (!idInput.value.trim()) {
						idInput.focus();
						return false;
					}

					// 비밀번호 입력 확인
					if (!pwInput.value.trim()) {
						pwInput.focus();
						return false;
					}

					// ========================================
					// 아이디 저장 처리
					// ========================================

					if (rememberCheckbox.checked) {

						// 체크된 경우 아이디 저장
						localStorage.setItem('noleogaja_saved_id',
								idInput.value.trim());

					} else {

						// 체크하지 않은 경우 저장된 아이디 삭제
						localStorage.removeItem('noleogaja_saved_id');

					}

					return true;
				}
			</script>

		</div>

	</main>



	<jsp:include page="/WEB-INF/bottom.jsp" />

	<jsp:include page="/WEB-INF/liveChat/chat.jsp" />



</body>

</html>