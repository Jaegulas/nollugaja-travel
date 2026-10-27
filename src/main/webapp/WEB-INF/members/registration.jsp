<%-- =========================================================
     주제: 파일 역할
     설명: members 영역의 registration 화면을 구성하고 서버에서 전달받은 데이터를 표시
========================================================= --%>
<%-- 이 페이지의 글자 인코딩을 UTF-8 로 정한다. 빠뜨리면 한글이 깨진다 --%>



<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>







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



<link
	href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap"
	rel="stylesheet" />



<style>
@
layer base {
	html ,body{margin: *0*;
	padding: *0*;
	font-family: * 'Plus Jakarta Sans', 'Noto Sans KR', sans-serif *;
}

body {
	overscroll-behavior: *none*;
}

main>:first-child {
	margin-top: *0 !important*;
}

main>:last-child {
	margin-bottom: *0 !important*;
}

}
::-webkit-scrollbar {
	display: *none*;
}
</style>



<script src="https://cdn.tailwindcss.com"></script>



<!-- 카카오 우편번호 검색 API -->



<script
	src="https://t1.kakaocdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>



<script id="tailwind-config">tailwind.config={darkMode:"class",theme:{extend:{"colors":{"tertiary-fixed-dim":"#4edea3","on-secondary":"#ffffff","on-error-container":"#93000a","on-secondary-fixed":"#400100","secondary":"#ac3323","on-surface":"#131b2e","on-primary-fixed":"#001f25","surface-container-low":"#f2f3ff","on-primary-fixed-variant":"#004e5a","on-secondary-container":"#6c0300","on-tertiary-fixed-variant":"#005236","on-background":"#131b2e","primary-fixed-dim":"#73d4e9","secondary-container":"#fd6e58","on-tertiary":"#ffffff","secondary-fixed-dim":"#ffb4a7","on-secondary-fixed-variant":"#8a1a0e","outline":"#6e797c","surface-dim":"#d2d9f4","surface-variant":"#dae2fd","on-primary":"#ffffff","primary-container":"#008092","inverse-on-surface":"#eef0ff","surface-container-highest":"#dae2fd","primary":"#006574","on-tertiary-fixed":"#002113","background":"#faf8ff","tertiary":"#006947","on-tertiary-container":"#f5fff6","inverse-surface":"#283044","error":"#ba1a1a","error-container":"#ffdad6","on-error":"#ffffff","tertiary-container":"#00855b","inverse-primary":"#73d4e9","on-primary-container":"#f8fdff","surface-bright":"#faf8ff","surface-container":"#eaedff","secondary-fixed":"#ffdad4","surface":"#faf8ff","primary-fixed":"#a4eeff","tertiary-fixed":"#6ffbbe","surface-container-lowest":"#ffffff","surface-container-high":"#e2e7ff","outline-variant":"#bdc8cb","surface-tint":"#006877","on-surface-variant":"#3e494b"},"borderRadius":{"DEFAULT":"0.25rem","lg":"0.5rem","xl":"0.75rem","full":"9999px"},"spacing":{"gutter":"1.5rem","space-sm":"0.5rem","space-xl":"2.5rem","margin":"3rem","space-xs":"0.25rem","space-lg":"1.5rem","margin-sm":"1.25rem","space-md":"1rem","gutter-sm":"1rem"},"fontFamily":{"label-sm":["Plus Jakarta Sans"],"body-md":["Plus Jakarta Sans"],"label-md":["Plus Jakarta Sans"],"body-sm":["Plus Jakarta Sans"],"display-mobile":["Plus Jakarta Sans"],"display":["Plus Jakarta Sans"],"headline-md":["Plus Jakarta Sans"],"label-lg":["Plus Jakarta Sans"],"headline-xl":["Plus Jakarta Sans"],"headline-xl-mobile":["Plus Jakarta Sans"],"headline-sm":["Plus Jakarta Sans"],"body-lg":["Plus Jakarta Sans"],"headline-lg":["Plus Jakarta Sans"]},"fontSize":{"label-sm":["11px",{"lineHeight":"14px","letterSpacing":"0.04em","fontWeight":"700"}],"body-md":["15px",{"lineHeight":"24px","letterSpacing":"-0.005em","fontWeight":"400"}],"label-md":["12px",{"lineHeight":"16px","letterSpacing":"0.02em","fontWeight":"600"}],"body-sm":["13px",{"lineHeight":"20px","letterSpacing":"0em","fontWeight":"400"}],"display-mobile":["38px",{"lineHeight":"46px","letterSpacing":"-0.025em","fontWeight":"800"}],"display":["56px",{"lineHeight":"64px","letterSpacing":"-0.03em","fontWeight":"800"}],"headline-md":["22px",{"lineHeight":"30px","letterSpacing":"-0.015em","fontWeight":"600"}],"label-lg":["14px",{"lineHeight":"20px","letterSpacing":"0.01em","fontWeight":"600"}],"headline-xl":["40px",{"lineHeight":"48px","letterSpacing":"-0.025em","fontWeight":"700"}],"headline-xl-mobile":["28px",{"lineHeight":"36px","letterSpacing":"-0.02em","fontWeight":"700"}],"headline-sm":["18px",{"lineHeight":"26px","letterSpacing":"-0.01em","fontWeight":"600"}],"body-lg":["18px",{"lineHeight":"28px","letterSpacing":"-0.01em","fontWeight":"400"}],"headline-lg":["28px",{"lineHeight":"36px","letterSpacing":"-0.02em","fontWeight":"700"}]}}}};</script>







</head>

<body>

	<jsp:include page="/WEB-INF/top.jsp" />







	<main class="w-full bg-surface">



		<div class="flex flex-col w-full">



			<!-- Subtle Ambient Glow Orbs -->



			<div
				class="relative w-full overflow-hidden py-12 md:py-20 flex justify-center items-center">



				<div
					class="absolute -top-32 left-1/2 -translate-x-1/2 w-[700px] h-[340px] bg-gradient-to-r from-primary-fixed-dim/30 via-secondary-fixed-dim/20 to-tertiary-fixed-dim/25 blur-3xl pointer-events-none rounded-full"></div>



				<div
					class="absolute top-1/2 right-10 w-[380px] h-[380px] bg-primary/5 blur-2xl pointer-events-none rounded-full"></div>



				<!-- Centered Editorial Registration Card Container -->



				<div class="relative w-full max-w-[680px] mx-margin-sm md:mx-auto">



					<!-- Main Form Surface Card -->



					<div
						class="bg-surface-container-lowest rounded-xl shadow-xl p-6 sm:p-10 relative">



						<!-- Header Section -->



						<div class="text-center pb-8">



							<div
								class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-surface-container-high text-primary font-label-md text-label-md mb-3">



								<span class="material-symbols-outlined text-[16px]">local_mall</span>



								<span>놀러가자 멤버십 가입</span>



							</div>



							<h1
								class="font-headline-xl text-headline-xl text-on-surface tracking-tight">회원가입</h1>



							<p
								class="font-body-md text-body-md text-on-surface-variant mt-2 max-w-md mx-auto">



								놀러가자와 함께 대한민국 방방곡곡 특별한 여정을 시작해보세요.</p>



						</div>

						<%
						String registerError = (String) request.getAttribute("registerError");

						if (registerError != null) {
						%>
						<div class="mb-4 p-4 rounded-lg bg-red-50 border border-red-200"
							role="alert">
							<p class="text-red-600 font-body-sm">
								<%=registerError.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
									.replace("'", "&#39;")%>
							</p>
						</div>
						<%
						}
						%>


						<form id="signupForm" onsubmit="return prepareRegistration(this)"
							action="${pageContext.request.contextPath}/Register"
							method="post">






							<!-- SECTION 1: 기본 계정 정보 -->



							<div class="space-y-4">



								<div class="flex items-center gap-2 pb-2">



									<span class="w-2 h-4 rounded-full bg-primary inline-block"></span>



									<h2
										class="font-headline-sm text-headline-sm text-on-surface font-bold">기본



										계정 정보</h2>



									<span class="font-label-sm text-label-sm text-error ml-auto">*



										필수 입력 항목</span>



								</div>



								<!-- Field 1: ID (아이디) with Duplicate Check -->



								<div class="space-y-1.5">



									<label
										class="block font-label-lg text-label-lg text-on-surface"
										for="memberId"> 아이디 <span class="text-error">*</span>



									</label>



									<div class="flex gap-2">



										<div class="relative flex-1">



											<span
												class="absolute left-3.5 top-1/2 -translate-y-1/2 text-outline material-symbols-outlined text-[20px]">account_circle</span>



											<input
												class="w-full pl-11 pr-3 py-3 rounded-lg bg-surface-container-low text-on-surface placeholder:text-outline-variant font-body-md text-body-md focus:outline-none focus:bg-surface-container-lowest transition-all"
												id="memberId" maxlength="12" name="id"
												oninput="resetIdDuplicateCheck()"
												placeholder="아이디 입력 (4~12자 영문/숫자)" required="" type="text" />



										</div>



										<button
											class="px-5 py-3 rounded-lg bg-surface-container-high hover:bg-surface-container text-primary font-label-lg text-label-lg transition-colors flex-shrink-0 flex items-center gap-1"
											onclick="checkDuplicateId()" type="button">



											<span class="material-symbols-outlined text-[18px]">verified_user</span>



											<span>중복확인</span>



										</button>



									</div>



									<p
										class="font-body-sm text-body-sm text-on-surface-variant pl-1"
										id="idFeedback">영문 소문자 및 숫자를 혼용하여 4~12자 이내로 입력하세요.</p>



								</div>



								<!-- Field 2: PASS (비밀번호) -->



								<div class="space-y-1.5">



									<label
										class="block font-label-lg text-label-lg text-on-surface"
										for="memberPass"> 비밀번호 <span class="text-error">*</span>



									</label>



									<div class="relative">



										<span
											class="absolute left-3.5 top-1/2 -translate-y-1/2 text-outline material-symbols-outlined text-[20px]">lock</span>



										<input
											class="w-full pl-11 pr-11 py-3 rounded-lg bg-surface-container-low text-on-surface placeholder:text-outline-variant font-body-md text-body-md focus:outline-none focus:bg-surface-container-lowest transition-all"
											id="memberPass" name="pass"
											oninput="calculatePasswordStrength(this.value)"
											placeholder="8~16자 영문, 숫자, 특수문자 조합" required=""
											type="password" />



										<button aria-label="비밀번호 보기 토글"
											class="absolute right-3.5 top-1/2 -translate-y-1/2 text-outline hover:text-on-surface transition-colors"
											onclick="togglePasswordVisibility('memberPass', this)"
											type="button">



											<span class="material-symbols-outlined text-[20px]">visibility_off</span>



										</button>



									</div>



									<!-- Password Strength Bar -->



									<div class="space-y-1 pt-1">



										<div
											class="w-full bg-surface-container h-1.5 rounded-full overflow-hidden flex">



											<div
												class="h-full bg-outline-variant transition-all duration-300 w-0"
												id="strengthBar"></div>



										</div>



										<div class="flex justify-between items-center px-1">



											<span class="font-label-sm text-label-sm text-outline"
												id="strengthText">보안 강도: 미입력</span> <span
												class="font-label-sm text-label-sm text-outline-variant">특수문자



												포함 권장</span>



										</div>



									</div>



								</div>



								<!-- Field 2-2: 비밀번호 확인 -->



								<div class="space-y-1.5">



									<label
										class="block font-label-lg text-label-lg text-on-surface"
										for="memberPassConfirm"> 비밀번호 확인 <span
										class="text-error">*</span>



									</label>



									<div class="relative">



										<span
											class="absolute left-3.5 top-1/2 -translate-y-1/2 text-outline material-symbols-outlined text-[20px]">check_circle</span>



										<input
											class="w-full pl-11 pr-11 py-3 rounded-lg bg-surface-container-low text-on-surface placeholder:text-outline-variant font-body-md text-body-md focus:outline-none focus:bg-surface-container-lowest transition-all"
											id="memberPassConfirm" name="passConfirm" oninput="validatePassConfirm()"
											placeholder="비밀번호를 한 번 더 입력해주세요" required="" type="password" />



										<button aria-label="비밀번호 확인 토글"
											class="absolute right-3.5 top-1/2 -translate-y-1/2 text-outline hover:text-on-surface transition-colors"
											onclick="togglePasswordVisibility('memberPassConfirm', this)"
											type="button">



											<span class="material-symbols-outlined text-[20px]">visibility_off</span>



										</button>



									</div>



									<p class="font-body-sm text-body-sm text-outline-variant pl-1"
										id="passMatchFeedback">입력하신 비밀번호와 동일하게 입력해주세요.</p>



								</div>



							</div>



							<!-- SECTION 2: 회원 기본 정보 (이름, 나이, 성별) -->



							<div class="space-y-4 pt-4">



								<div class="flex items-center gap-2 pb-2">



									<span class="w-2 h-4 rounded-full bg-primary inline-block"></span>



									<h2
										class="font-headline-sm text-headline-sm text-on-surface font-bold">회원



										인적 정보</h2>



								</div>



								<div class="grid grid-cols-1 sm:grid-cols-2 gap-4">



									<!-- Field 3: NAME (이름) -->



									<div class="space-y-1.5">



										<label
											class="block font-label-lg text-label-lg text-on-surface"
											for="memberName"> 이름 <span class="text-error">*</span>



										</label>



										<div class="relative">



											<span
												class="absolute left-3.5 top-1/2 -translate-y-1/2 text-outline material-symbols-outlined text-[20px]">badge</span>



											<input
												class="w-full pl-11 pr-3 py-3 rounded-lg bg-surface-container-low text-on-surface placeholder:text-outline-variant font-body-md text-body-md focus:outline-none focus:bg-surface-container-lowest transition-all"
												id="memberName" name="name" placeholder="이름을 입력하세요"
												required="" type="text" />



										</div>



									</div>



									<!-- Field 4: AGE (나이) -->



									<div class="space-y-1.5">



										<label
											class="block font-label-lg text-label-lg text-on-surface"
											for="memberAge"> 나이 (만 나이) <span class="text-error">*</span>



										</label>



										<div class="relative">



											<span
												class="absolute left-3.5 top-1/2 -translate-y-1/2 text-outline material-symbols-outlined text-[20px]">cake</span>



											<input
												class="w-full pl-11 pr-3 py-3 rounded-lg bg-surface-container-low text-on-surface placeholder:text-outline-variant font-body-md text-body-md focus:outline-none focus:bg-surface-container-lowest transition-all"
												id="memberAge" max="120" min="14" name="age"
												placeholder="만 나이 입력 (예: 28)" required="" type="number" />



										</div>



									</div>



								</div>



								<!-- Field 5: GENDER (성별) -->



								<div class="space-y-1.5">



									<label
										class="block font-label-lg text-label-lg text-on-surface">



										성별 <span class="text-error">*</span>



									</label>



									<div
										class="grid grid-cols-3 gap-2 bg-surface-container-low p-1.5 rounded-lg">



										<label class="cursor-pointer"> <input checked=""
											class="peer sr-only" name="gender" type="radio" value="M" />



											<div
												class="text-center py-2.5 rounded-lg font-label-md text-label-md text-on-surface-variant transition-all peer-checked:bg-surface-container-lowest peer-checked:text-primary peer-checked:font-bold peer-checked:shadow-sm">



												남성 (Male)</div>



										</label> <label class="cursor-pointer"> <input
											class="peer sr-only" name="gender" type="radio" value="F" />



											<div
												class="text-center py-2.5 rounded-lg font-label-md text-label-md text-on-surface-variant transition-all peer-checked:bg-surface-container-lowest peer-checked:text-primary peer-checked:font-bold peer-checked:shadow-sm">



												여성 (Female)</div>



										</label> <label class="cursor-pointer"> <input
											class="peer sr-only" name="gender" type="radio" value="N" />



											<div
												class="text-center py-2.5 rounded-lg font-label-md text-label-md text-on-surface-variant transition-all peer-checked:bg-surface-container-lowest peer-checked:text-primary peer-checked:font-bold peer-checked:shadow-sm">



												선택안함</div>



										</label>



									</div>



								</div>



							</div>



							<!-- SECTION 3: 연락처 및 주소 (휴대폰, 이메일, 주소, 일반전화) -->



							<div class="space-y-4 pt-4">



								<div class="flex items-center gap-2 pb-2">



									<span class="w-2 h-4 rounded-full bg-primary inline-block"></span>



									<h2
										class="font-headline-sm text-headline-sm text-on-surface font-bold">연락처



										및 주소</h2>



								</div>



								<!-- Field 9: HP (휴대폰번호) with SMS verify button -->



								<div class="space-y-1.5">



									<label
										class="block font-label-lg text-label-lg text-on-surface"
										for="memberHp"> 휴대폰 번호 <span class="text-error">*</span>



									</label>



									<div class="flex gap-2">



										<div class="relative flex-1">



											<span
												class="absolute left-3.5 top-1/2 -translate-y-1/2 text-outline material-symbols-outlined text-[20px]">phone_android</span>



											<input
												class="w-full pl-11 pr-3 py-3 rounded-lg bg-surface-container-low text-on-surface placeholder:text-outline-variant font-body-md text-body-md focus:outline-none focus:bg-surface-container-lowest transition-all"
												id="memberHp" maxlength="13" name="hp"
												oninput="formatPhoneNumber(this)"
												placeholder="010-1234-5678" required="" type="tel" />



										</div>




									</div>



									<div
										class="hidden mt-2 p-3 bg-surface-container-low rounded-lg space-y-2"
										id="smsVerifyContainer">



										<div class="flex gap-2 items-center">



											<input
												class="flex-1 px-3 py-2 rounded-lg bg-surface-container-lowest text-on-surface font-body-md text-body-md focus:outline-none"
												id="smsCodeInput" maxlength="6" placeholder="인증번호 6자리 입력"
												type="text" /> <span
												class="font-label-md text-label-md text-secondary font-bold px-2"
												id="smsTimer">05:00</span>



											<button
												class="px-4 py-2 rounded-lg bg-primary text-on-primary font-label-md text-label-md"
												onclick="confirmSmsCode()" type="button">확인</button>



										</div>



										<p class="font-body-sm text-body-sm text-outline">입력하신



											휴대폰으로 발송된 6자리 번호를 입력해주세요.</p>



									</div>



								</div>


								<!-- Field 7: EMAIL (이메일) -->
								<div class="space-y-1.5">

									<label
										class="block font-label-lg text-label-lg text-on-surface"
										for="memberEmailId"> 이메일 주소 <span class="text-error">*</span>
									</label>

									<!-- 이메일 입력 + 인증번호 발송 -->
									<div
										class="flex flex-col sm:flex-row gap-2 items-stretch sm:items-center">

										<!-- 이메일 아이디 -->
										<div class="relative flex-1">

											<span
												class="absolute left-3.5 top-1/2 -translate-y-1/2
                       								  text-outline material-symbols-outlined text-[20px]">
												mail </span> <input
												class="w-full pl-11 pr-3 py-3 rounded-lg
								                       bg-surface-container-low text-on-surface
								                       placeholder:text-outline-variant
								                       font-body-md text-body-md
								                       focus:outline-none
								                       focus:bg-surface-container-lowest
								                       transition-all"
												id="memberEmailId" placeholder="이메일 아이디"
												oninput="resetEmailVerification()" required type="text" />

										</div>

										<span
											class="hidden sm:inline font-headline-sm
                  								   text-headline-sm text-outline">
											@ </span>

										<!-- 이메일 도메인 -->
										<div class="flex-1 flex gap-2">

											<input
												class="w-full px-3 py-3 rounded-lg
								                       bg-surface-container-low text-on-surface
								                       placeholder:text-outline-variant
								                       font-body-md text-body-md
								                       focus:outline-none
								                       focus:bg-surface-container-lowest
								                       transition-all"
												id="memberEmailDomain" placeholder="도메인 입력"
												oninput="resetEmailVerification()" required type="text" />

											<select id="memberEmailDomainSelect"
												class="px-3 py-3 rounded-lg
								                       bg-surface-container text-on-surface
								                       font-body-sm text-body-sm
								                       focus:outline-none"
												onchange="handleDomainSelect(this); resetEmailVerification();">

												<option value="">직접입력</option>
												<option value="naver.com">naver.com</option>
												<option value="gmail.com">gmail.com</option>
												<option value="kakao.com">kakao.com</option>
												<option value="daum.net">daum.net</option>

											</select>

										</div>

									</div>


									<!-- 인증번호 발송 버튼 -->
									<button id="emailSendBtn" type="button"
										onclick="sendEmailCode()"
										class="w-full mt-2 px-5 py-3 rounded-lg
								               bg-surface-container-high
								               hover:bg-surface-container
								               text-primary
								               font-label-lg text-label-lg
								               transition-colors
								               flex items-center justify-center gap-1">

										<span class="material-symbols-outlined text-[18px]">
											outgoing_mail </span> <span id="emailSendBtnText"> 이메일 인증번호
											발송 </span>

									</button>


									<!-- 인증번호 입력 영역 -->
									<div id="emailVerifyContainer"
										class="hidden mt-2 p-3
								               bg-surface-container-low
								               rounded-lg space-y-2">

										<div class="flex gap-2 items-center">

											<input id="emailCodeInput" maxlength="6" inputmode="numeric"
												autocomplete="one-time-code"
												class="flex-1 px-3 py-2 rounded-lg
								                       bg-surface-container-lowest
								                       text-on-surface
								                       font-body-md text-body-md
								                       focus:outline-none"
												placeholder="인증번호 6자리 입력" type="text" /> <span
												id="emailTimer"
												class="font-label-md text-label-md
                       								  text-secondary font-bold px-2">
												05:00 </span>

											<button id="emailVerifyBtn" type="button"
												onclick="confirmEmailCode()"
												class="px-4 py-2 rounded-lg
								                       bg-primary text-on-primary
								                       font-label-md text-label-md">
												확인</button>

										</div>

										<p id="emailVerifyMessage"
											class="font-body-sm text-body-sm text-outline">입력하신 이메일로
											발송된 6자리 인증번호를 입력해주세요.</p>

									</div>


									<!-- 실제 RegisterController로 넘어가는 이메일 -->
									<input id="memberEmailFull" name="email" type="hidden" />

								</div>


								<!-- Field 6: ADDRESS (주소) -->



								<div class="space-y-2">



									<label
										class="block font-label-lg text-label-lg text-on-surface"
										for="memberZipcode"> 주소지 <span class="text-error">*</span>



									</label>



									<div class="flex gap-2">



										<div class="relative w-40">



											<span
												class="absolute left-3.5 top-1/2 -translate-y-1/2 text-outline material-symbols-outlined text-[20px]">pin_drop</span>



											<input
												class="w-full pl-11 pr-3 py-3 rounded-lg bg-surface-container text-on-surface placeholder:text-outline font-body-md text-body-md focus:outline-none"
												id="memberZipcode" placeholder="우편번호" readonly=""
												required="" type="text" />



										</div>



										<button
											class="px-5 py-3 rounded-lg bg-surface-container-high hover:bg-surface-container text-primary font-label-lg text-label-lg transition-colors flex items-center gap-1 flex-shrink-0"
											onclick="openAddressSearchModal()" type="button">



											<span class="material-symbols-outlined text-[18px]">search</span>



											<span>우편번호/주소 검색</span>



										</button>



									</div>



									<input
										class="w-full px-3.5 py-3 rounded-lg bg-surface-container text-on-surface placeholder:text-outline font-body-md text-body-md focus:outline-none"
										id="memberAddressBase" name="address_base"
										placeholder="기본주소 (검색 버튼 클릭 시 자동 입력)" readonly="" required=""
										type="text" /> <input
										class="w-full px-3.5 py-3 rounded-lg bg-surface-container-low text-on-surface placeholder:text-outline-variant font-body-md text-body-md focus:outline-none focus:bg-surface-container-lowest transition-all"
										id="memberAddressDetail" name="address_detail"
										placeholder="상세주소를 입력해주세요 (동, 호수, 건물명 등)" required=""
										type="text" /> <input id="memberAddressFull" name="address"
										type="hidden" />



								</div>



								<!-- Field 8: TEL (일반 전화번호) (Optional) -->



								<div class="space-y-1.5">



									<div class="flex items-center justify-between">



										<label
											class="block font-label-lg text-label-lg text-on-surface"
											for="memberTel"> 일반 전화번호 </label> <span
											class="font-label-sm text-label-sm text-outline">선택 항목</span>



									</div>



									<div class="relative">



										<span
											class="absolute left-3.5 top-1/2 -translate-y-1/2 text-outline material-symbols-outlined text-[20px]">call</span>



										<input
											class="w-full pl-11 pr-3 py-3 rounded-lg bg-surface-container-low text-on-surface placeholder:text-outline-variant font-body-md text-body-md focus:outline-none focus:bg-surface-container-lowest transition-all"
											id="memberTel" name="tel"
											placeholder="지역번호 포함 유선전화 (예: 02-123-4567)" type="tel" />



									</div>



								</div>



							</div>



							<!-- SECTION 4: 이용약관 및 개인정보 동의 -->



							<div class="space-y-3 pt-4">



								<div class="flex items-center gap-2 pb-2">



									<span class="w-2 h-4 rounded-full bg-primary inline-block"></span>



									<h2
										class="font-headline-sm text-headline-sm text-on-surface font-bold">약관



										동의</h2>



								</div>



								<div class="p-4 rounded-xl bg-surface-container-low space-y-3">



									<!-- Agree All -->



									<label class="flex items-center gap-3 cursor-pointer pb-2.5">



										<input class="w-5 h-5 rounded accent-primary cursor-pointer"
										id="agreeAll" onchange="toggleAllAgreements(this)"
										type="checkbox" /> <span
										class="font-headline-sm text-headline-sm text-on-surface font-bold">



											전체 동의하기 (선택 동의 포함) </span>



									</label>



									<!-- Term 1: Required -->



									<div class="flex items-center justify-between py-1">



										<label class="flex items-center gap-2.5 cursor-pointer">



											<input
											class="agree-sub w-4 h-4 rounded accent-primary cursor-pointer"
											name="agree_terms" required="" type="checkbox" /> <span
											class="font-body-md text-body-md text-on-surface">

												[필수] 놀러가자 서비스 이용약관 동의 </span>



										</label>



										<button
											class="font-label-sm text-label-sm text-outline hover:text-primary underline"
											onclick="showTermModal('terms')" type="button">내용보기</button>



									</div>



									<!-- Term 2: Required -->



									<div class="flex items-center justify-between py-1">



										<label class="flex items-center gap-2.5 cursor-pointer">



											<input
											class="agree-sub w-4 h-4 rounded accent-primary cursor-pointer"
											name="agree_privacy" required="" type="checkbox" /> <span
											class="font-body-md text-body-md text-on-surface">

												[필수] 개인정보 수집 및 이용 동의 </span>



										</label>



										<button
											class="font-label-sm text-label-sm text-outline hover:text-primary underline"
											onclick="showTermModal('privacy')" type="button">내용보기</button>



									</div>



									<!-- Term 3: Optional -->



									<div class="flex items-center justify-between py-1">



										<label class="flex items-center gap-2.5 cursor-pointer">



											<input
											class="agree-sub w-4 h-4 rounded accent-primary cursor-pointer"
											name="agree_marketing" type="checkbox" /> <span
											class="font-body-md text-body-md text-on-surface-variant">



												[선택] 마케팅 정보 수신 및 여행 특가 혜택 알림 </span>



										</label>



										<button
											class="font-label-sm text-label-sm text-outline hover:text-primary underline"
											onclick="showTermModal('marketing')" type="button">내용보기</button>



									</div>



								</div>



							</div>



							<!-- SECTION 5: CTA Action Button -->



							<div class="pt-4 space-y-4">



								<button
									class="w-full py-4 rounded-xl bg-primary hover:bg-primary-container text-on-primary font-headline-sm text-headline-sm font-bold shadow-lg transition-all transform active:scale-[0.99] flex items-center justify-center gap-2"
									id="submitBtn" type="submit">



									<span class="material-symbols-outlined">how_to_reg</span> <span>회원가입



										완료하고 웰컴 혜택 받기</span>



								</button>



								<!-- Bottom Auxiliary Links -->



								<div class="text-center pt-2">



									<span class="font-body-md text-body-md text-on-surface-variant">이미



										놀러가자 계정이 있으신가요?</span> <a
										class="font-label-lg text-label-lg font-bold text-secondary hover:underline ml-2 inline-flex items-center gap-0.5"
										data-path="login"
										href="${pageContext.request.contextPath}/Login"> <span>로그인하기</span>



										<span class="material-symbols-outlined text-[16px]">arrow_forward</span>



									</a>



								</div>



							</div>



						</form>



					</div>



					<!-- Quick Trust Badges -->



					<div class="mt-8 grid grid-cols-3 gap-4 text-center">



						<div
							class="p-3 bg-surface-container-lowest/80 rounded-xl shadow-xs">



							<span class="material-symbols-outlined text-primary text-[24px]">verified</span>



							<p
								class="font-label-md text-label-md font-bold text-on-surface mt-1">안심



								데이터 암호화</p>



							<p class="font-body-sm text-body-sm text-outline">개인정보 완벽 보호</p>



						</div>



						<div
							class="p-3 bg-surface-container-lowest/80 rounded-xl shadow-xs">



							<span
								class="material-symbols-outlined text-secondary-container text-[24px]">loyalty</span>



							<p
								class="font-label-md text-label-md font-bold text-on-surface mt-1">가이드
								예약까지 가능</p>



							<p class="font-body-sm text-body-sm text-outline">즐거운 여행을 위한
								선택</p>



						</div>



						<div
							class="p-3 bg-surface-container-lowest/80 rounded-xl shadow-xs">



							<span class="material-symbols-outlined text-tertiary text-[24px]">map</span>



							<p
								class="font-label-md text-label-md font-bold text-on-surface mt-1">숨은



								로컬 명소 큐레이션</p>



							<p class="font-body-sm text-body-sm text-outline">전국 맞춤 루트 제안</p>



						</div>



					</div>



				</div>



			</div>



			<!-- Micro Interaction Toast / Notification Feedback -->



			<div
				class="fixed bottom-8 left-1/2 -translate-x-1/2 z-50 transform translate-y-24 opacity-0 transition-all duration-300 pointer-events-none"
				id="toastNotification">



				<div
					class="bg-inverse-surface text-inverse-on-surface px-6 py-3 rounded-full shadow-2xl flex items-center gap-2">



					<span
						class="material-symbols-outlined text-[20px] text-tertiary-fixed"
						id="toastIcon">check_circle</span> <span
						class="font-label-lg text-label-lg font-medium" id="toastMessage">안내



						메시지입니다.</span>



				</div>



			</div>



			<script>



    // Utility Toast



    function showToast(msg, isSuccess = true) {



      const toast = document.getElementById('toastNotification');



      const toastMsg = document.getElementById('toastMessage');



      const toastIcon = document.getElementById('toastIcon');







      toastMsg.textContent = msg;



      toastIcon.textContent = isSuccess ? 'check_circle' : 'error';



      toastIcon.className = isSuccess 



        ? 'material-symbols-outlined text-[20px] text-tertiary-fixed' 



        : 'material-symbols-outlined text-[20px] text-secondary-container';







      toast.classList.remove('translate-y-24', 'opacity-0');



      toast.classList.add('translate-y-0', 'opacity-100');







      setTimeout(() => {



        toast.classList.add('translate-y-24', 'opacity-0');



        toast.classList.remove('translate-y-0', 'opacity-100');



      }, 3000);



    }







    // Toggle Password Visibility



    function togglePasswordVisibility(inputId, btn) {



      const input = document.getElementById(inputId);



      const icon = btn.querySelector('.material-symbols-outlined');



      if (input.type === 'password') {



        input.type = 'text';



        icon.textContent = 'visibility';



      } else {



        input.type = 'password';



        icon.textContent = 'visibility_off';



      }



    }







    // ======================================================
   // ID Duplicate Check - 실제 DB 조회
   // ======================================================
   let idDuplicateChecked = false;
   let checkedId = "";

   function checkDuplicateId() {
     const idInput = document.getElementById('memberId');
     const feedback = document.getElementById('idFeedback');
     const val = idInput.value.trim();

     idDuplicateChecked = false;
     checkedId = "";

     if (!val) {
       feedback.textContent = '아이디를 입력해주세요.';
       feedback.className = 'font-body-sm text-body-sm text-error pl-1';
       showToast('아이디를 먼저 입력해주세요.', false);
       idInput.focus();
       return;
     }

     if (!/^[a-z0-9]{4,12}$/.test(val)) {
       feedback.textContent = '아이디는 영문 소문자/숫자 4~12자로 입력해주세요.';
       feedback.className = 'font-body-sm text-body-sm text-error pl-1';
       showToast('아이디 형식을 확인해주세요.', false);
       idInput.focus();
       return;
     }

     fetch('${pageContext.request.contextPath}/Register?action=checkId&id=' + encodeURIComponent(val))
       .then(response => {
         if (!response.ok) throw new Error('중복확인 요청 실패');
         return response.json();
       })
       .then(data => {
         if (!data.success) {
           feedback.textContent = data.message;
           feedback.className = 'font-body-sm text-body-sm text-error pl-1';
           showToast(data.message, false);
           return;
         }

         if (!data.available) {
           feedback.textContent = '이미 사용 중인 아이디입니다.';
           feedback.className = 'font-body-sm text-body-sm text-error font-bold pl-1';
           showToast('이미 사용 중인 아이디입니다.', false);
           idInput.focus();
           return;
         }

         idDuplicateChecked = true;
         checkedId = val;
         feedback.textContent = '사용 가능한 아이디입니다!';
         feedback.className = 'font-body-sm text-body-sm text-tertiary font-bold pl-1';
         showToast('사용 가능한 아이디입니다.', true);
       })
       .catch(error => {
         console.error('아이디 중복확인 오류:', error);
         feedback.textContent = '아이디 중복확인 중 오류가 발생했습니다.';
         feedback.className = 'font-body-sm text-body-sm text-error pl-1';
         showToast('아이디 중복확인 중 오류가 발생했습니다.', false);
       });
   }

   function resetIdDuplicateCheck() {
     idDuplicateChecked = false;
     checkedId = "";
     const feedback = document.getElementById('idFeedback');
     feedback.textContent = '영문 소문자 및 숫자를 혼용하여 4~12자 이내로 입력하세요.';
     feedback.className = 'font-body-sm text-body-sm text-on-surface-variant pl-1';
   }

   // Password Strength Meter



    function calculatePasswordStrength(pass) {



      const bar = document.getElementById('strengthBar');



      const text = document.getElementById('strengthText');



      if (!pass) {



        bar.style.width = '0%';



        bar.className = 'h-full bg-outline-variant transition-all duration-300';



        text.textContent = '보안 강도: 미입력';



        text.className = 'font-label-sm text-label-sm text-outline';



        return;



      }







      let score = 0;



      if (pass.length >= 8) score++;



      if (/[A-Z]/.test(pass) || /[a-z]/.test(pass)) score++;



      if (/[0-9]/.test(pass)) score++;



      if (/[^A-Za-z0-9]/.test(pass)) score++;







      if (score <= 1) {



        bar.style.width = '25%';



        bar.className = 'h-full bg-error transition-all duration-300';



        text.textContent = '보안 강도: 위험 (매우 약함)';



        text.className = 'font-label-sm text-label-sm text-error font-semibold';



      } else if (score === 2 || score === 3) {



        bar.style.width = '65%';



        bar.className = 'h-full bg-secondary-container transition-all duration-300';



        text.textContent = '보안 강도: 보통 (적정)';



        text.className = 'font-label-sm text-label-sm text-secondary font-semibold';



      } else {



        bar.style.width = '100%';



        bar.className = 'h-full bg-tertiary transition-all duration-300';



        text.textContent = '보안 강도: 매우 안전';



        text.className = 'font-label-sm text-label-sm text-tertiary font-semibold';



      }



      validatePassConfirm();



    }







    // Password Match Validation



    function validatePassConfirm() {



      const p1 = document.getElementById('memberPass').value;



      const p2 = document.getElementById('memberPassConfirm').value;



      const feedback = document.getElementById('passMatchFeedback');







      if (!p2) {



        feedback.textContent = '입력하신 비밀번호와 동일하게 입력해주세요.';



        feedback.className = 'font-body-sm text-body-sm text-outline-variant pl-1';



        return;



      }



      if (p1 === p2) {



        feedback.textContent = '비밀번호가 일치합니다.';



        feedback.className = 'font-body-sm text-body-sm text-tertiary font-bold pl-1';



      } else {



        feedback.textContent = '비밀번호가 일치하지 않습니다.';



        feedback.className = 'font-body-sm text-body-sm text-error pl-1';



      }



    }







    // Phone Format Auto Masking



    function formatPhoneNumber(el) {



      let num = el.value.replace(/[^0-9]/g, '');



      if (num.length < 4) {



        el.value = num;



      } else if (num.length < 8) {



        el.value = num.substr(0, 3) + '-' + num.substr(3);



      } else if (num.length < 11) {



        el.value = num.substr(0, 3) + '-' + num.substr(3, 3) + '-' + num.substr(6);



      } else {



        el.value = num.substr(0, 3) + '-' + num.substr(3, 4) + '-' + num.substr(7, 4);



      }



    }







 // ======================================================

 // SMS 인증

 // ======================================================



 let timerInterval = null;

 let smsVerified = false;





 // ======================================================

 // 인증번호 발송

 // ======================================================



 function sendSmsCode() {



     const hpInput = document.getElementById("memberHp");

     const hp = hpInput.value.trim();



     // 휴대폰 번호 검사

     if (!hp || hp.length < 12) {



         showToast(

             "올바른 휴대폰 번호를 입력해주세요.",

             false

         );



         hpInput.focus();



         return;

     }





     // --------------------------------------------------

     // SmsController로 인증번호 발송 요청

     // --------------------------------------------------



     fetch(

         "${pageContext.request.contextPath}/Sms",

         {

             method: "POST",



             headers: {

                 "Content-Type":

                     "application/x-www-form-urlencoded; charset=UTF-8"

             },



             body:

                 "action=send"

                 + "&hp="

                 + encodeURIComponent(hp)

         }

     )



     .then(response => response.json())



     .then(data => {



         // ----------------------------------------------

         // 발송 실패

         // ----------------------------------------------



         if (!data.success) {



             showToast(

                 data.message,

                 false

             );



             return;

         }





         // ----------------------------------------------

         // 발송 성공

         // ----------------------------------------------



         showToast(

             data.message,

             true

         );





         // 인증번호 입력창 표시

         const container =

             document.getElementById(

                 "smsVerifyContainer"

             );



         container.classList.remove("hidden");





         // 새로운 인증번호를 발송했으므로

         // 인증상태 초기화

         smsVerified = false;





         // 인증번호 입력창 초기화

         document.getElementById(

             "smsCodeInput"

         ).value = "";





         // 타이머 시작

         startSmsTimer();

     })



     .catch(error => {



         console.error(

             "SMS 발송 오류:",

             error

         );



         showToast(

             "인증번호 발송 중 오류가 발생했습니다.",

             false

         );

     });

 }







 // ======================================================

 // 인증번호 확인

 // ======================================================



 function confirmSmsCode() {



     const hp =

         document.getElementById(

             "memberHp"

         ).value.trim();





     const code =

         document.getElementById(

             "smsCodeInput"

         ).value.trim();





     // 인증번호 입력 검사

     if (!code) {



         showToast(

             "인증번호를 입력해주세요.",

             false

         );



         document.getElementById(

             "smsCodeInput"

         ).focus();



         return;

     }





     // 6자리 검사

     if (!/^[0-9]{6}$/.test(code)) {



         showToast(

             "인증번호 6자리를 입력해주세요.",

             false

         );



         return;

     }





     // --------------------------------------------------

     // SmsController 인증번호 확인 요청

     // --------------------------------------------------



     fetch(

         "${pageContext.request.contextPath}/Sms",

         {

             method: "POST",



             headers: {

                 "Content-Type":

                     "application/x-www-form-urlencoded; charset=UTF-8"

             },



             body:

                 "action=verify"

                 + "&hp="

                 + encodeURIComponent(hp)

                 + "&code="

                 + encodeURIComponent(code)

         }

     )



     .then(response => response.json())



     .then(data => {



         // ----------------------------------------------

         // 인증 실패

         // ----------------------------------------------



         if (!data.success) {



             smsVerified = false;



             showToast(

                 data.message,

                 false

             );



             return;

         }





         // ----------------------------------------------

         // 인증 성공

         // ----------------------------------------------



         smsVerified = true;





         showToast(

             "휴대폰 인증이 완료되었습니다.",

             true

         );





         // 타이머 중지

         if (timerInterval) {



             clearInterval(

                 timerInterval

             );



             timerInterval = null;

         }





         // 인증번호 입력창 비활성화

         document.getElementById(

             "smsCodeInput"

         ).disabled = true;





         // 휴대폰 번호 변경 방지

         document.getElementById(

             "memberHp"

         ).readOnly = true;





         // 타이머 글자 변경

         document.getElementById(

             "smsTimer"

         ).textContent = "인증완료";

     })



     .catch(error => {



         console.error(

             "SMS 인증 오류:",

             error

         );



         smsVerified = false;



         showToast(

             "인증번호 확인 중 오류가 발생했습니다.",

             false

         );

     });

 }







 // ======================================================

 // SMS 5분 타이머

 // ======================================================



 function startSmsTimer() {



     let timeLeft = 300;



     const timerEl =

         document.getElementById(

             "smsTimer"

         );





     // 기존 타이머가 있으면 제거

     if (timerInterval) {



         clearInterval(

             timerInterval

         );

     }





     timerEl.textContent = "05:00";





     timerInterval = setInterval(() => {



         timeLeft--;





         const minutes =

             Math.floor(

                 timeLeft / 60

             );





         const seconds =

             timeLeft % 60;





         timerEl.textContent =

             String(minutes).padStart(2, "0")

             + ":"

             + String(seconds).padStart(2, "0");





         // ----------------------------------------------

         // 시간 만료

         // ----------------------------------------------



         if (timeLeft <= 0) {



             clearInterval(

                 timerInterval

             );



             timerInterval = null;



             smsVerified = false;



             timerEl.textContent =

                 "시간만료";





             showToast(

                 "인증시간이 만료되었습니다. 인증번호를 다시 발송해주세요.",

                 false

             );

         }



     }, 1000);

 }





    // Email Domain Select Helper



    function handleDomainSelect(sel) {



      const domainInput = document.getElementById('memberEmailDomain');



      if (sel.value) {



        domainInput.value = sel.value;



        domainInput.readOnly = true;



      } else {



        domainInput.value = '';



        domainInput.readOnly = false;



        domainInput.focus();



      }



    }







 // 실제 카카오 우편번호 API



    function openAddressSearchModal() {







        new daum.Postcode({







            oncomplete: function(data) {







                // 우편번호



                const zipcode = data.zonecode;







                // 도로명주소 우선 사용



                const address = data.roadAddress || data.address;







                // 회원가입 입력창에 자동 입력



                document.getElementById('memberZipcode').value = zipcode;







                document.getElementById('memberAddressBase').value = address;







                // 상세주소 초기화



                document.getElementById('memberAddressDetail').value = '';







                // 상세주소 입력창으로 커서 이동



                document.getElementById('memberAddressDetail').focus();







                showToast('주소가 입력되었습니다. 상세주소를 입력해주세요.');







            }







        }).open();







    }







    // Agreement Controls



    function toggleAllAgreements(master) {



      const subs = document.querySelectorAll('.agree-sub');



      subs.forEach(cb => cb.checked = master.checked);



    }







    function showTermModal(type) {



      const titles = {



        terms: '놀러가자 서비스 이용약관',



        privacy: '개인정보 수집 및 이용 동의',



        marketing: '마케팅 정보 수신 동의'



      };



      showToast(`${titles[type] || '약관'} 상세 전문이 준비되었습니다.`);



    }







    // 폼 전송 직전에 이메일과 주소를 반드시 조합한다.

    function prepareRegistration(form) {

      const value = id => document.getElementById(id).value.trim();

      const currentId = value('memberId');

      if (!idDuplicateChecked || checkedId !== currentId) {
        showToast('아이디 중복확인을 완료해주세요.', false);
        document.getElementById('memberId').focus();
        return false;
      }

      const emailId = value('memberEmailId');

      const domain = value('memberEmailDomain');

      const zip = value('memberZipcode');

      const base = value('memberAddressBase');

      const detail = value('memberAddressDetail');

	   // ========================================
	   // 비밀번호 형식 검사 (서버 규칙과 동일)
	   // ========================================
	   const password = document.getElementById('memberPass').value;
	
	   const passwordPattern =
	       /^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[^A-Za-z0-9\s])\S{8,16}$/;
	
	   if (!passwordPattern.test(password)) {
	
	       showToast(
	           '비밀번호는 8~16자의 영문, 숫자, 특수문자를 포함해야 합니다.',
	           false
	       );
	
	       document.getElementById('memberPass').focus();
	
	       return false;
	   }
      

      if (document.getElementById('memberPass').value !==

          document.getElementById('memberPassConfirm').value) {

        alert('비밀번호가 일치하지 않습니다.');

        return false;

      }


      if (!emailId || !domain || !/^[^\s@]+\.[^\s@]+$/.test(domain)) {
    	  
          alert('이메일을 정확히 입력해주세요.');
          
          return false;
          
      }
        

      if (!zip || !base || !detail) {

        alert('주소 검색 후 상세주소까지 입력해주세요.');

        return false;

      }



      document.getElementById('memberEmailFull').value = emailId + '@' + domain;

      document.getElementById('memberAddressFull').value =

        '(' + zip + ') ' + base + ' ' + detail;



      console.log('최종 전송값', {

        email: new FormData(form).get('email'),

        address: new FormData(form).get('address')

      });

   // ======================================================
   // 이메일 인증 확인
   // ======================================================

   const currentEmail = getFullEmail();

   if (!emailVerified) {

       showToast(
           '이메일 인증을 완료해주세요.',
           false
       );

       return false;
   }


   if (verifiedEmail !== currentEmail) {

       showToast(
           '인증받은 이메일과 현재 이메일이 다릅니다.',
           false
       );

       return false;
   }


   // RegisterController로 보낼 최종 이메일
   document
       .getElementById('memberEmailFull')
       .value = currentEmail;
      
      return true; // 브라우저의 정상 POST를 허용

    }
    
 // ======================================================
 // 이메일 인증
 // ======================================================

 let emailVerified = false;
 let verifiedEmail = "";
 let emailTimerInterval = null;


 // ------------------------------------------------------
 // 현재 입력된 전체 이메일 만들기
 // ------------------------------------------------------
 function getFullEmail() {

     const emailId =
         document.getElementById('memberEmailId').value.trim();

     const emailDomain =
         document.getElementById('memberEmailDomain').value.trim();

     if (!emailId || !emailDomain) {
         return "";
     }

     return emailId + '@' + emailDomain;
 }


 // ------------------------------------------------------
 // 인증번호 발송
 // ------------------------------------------------------
 function sendEmailCode() {

     const email = getFullEmail();

     if (!email) {

         showToast(
             '이메일 주소를 모두 입력해주세요.',
             false
         );

         return;
     }


     // 이메일 형식 검사
     const emailPattern =
         /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

     if (!emailPattern.test(email)) {

         showToast(
             '올바른 이메일 주소를 입력해주세요.',
             false
         );

         return;
     }


     const sendBtn =
         document.getElementById('emailSendBtn');

     const sendBtnText =
         document.getElementById('emailSendBtnText');


     sendBtn.disabled = true;
     sendBtnText.textContent = '발송 중...';


     fetch(
         '${pageContext.request.contextPath}/EmailSend',
         {
             method: 'POST',

             headers: {
                 'Content-Type':
                     'application/x-www-form-urlencoded'
             },

             body:
                 'email=' +
                 encodeURIComponent(email)
         }
     )

     .then(response => response.json())

     .then(data => {

         if (!data.success) {

             showToast(
                 data.message || '이메일 발송에 실패했습니다.',
                 false
             );

             return;
         }


         // 인증 상태 초기화
         emailVerified = false;
         verifiedEmail = "";


         // 인증번호 입력창 표시
         document
             .getElementById('emailVerifyContainer')
             .classList.remove('hidden');


         document
             .getElementById('emailCodeInput')
             .value = "";


         document
             .getElementById('emailCodeInput')
             .focus();


         showToast(
             '인증번호를 이메일로 발송했습니다.',
             true
         );


         sendBtnText.textContent =
             '인증번호 재발송';


         startEmailTimer();
     })

     .catch(error => {

         console.error(
             '이메일 인증번호 발송 오류:',
             error
         );

         showToast(
             '이메일 인증번호 발송 중 오류가 발생했습니다.',
             false
         );

     })

     .finally(() => {

         sendBtn.disabled = false;

         if (sendBtnText.textContent === '발송 중...') {
             sendBtnText.textContent =
                 '이메일 인증번호 발송';
         }

     });
 }


 // ------------------------------------------------------
 // 인증번호 확인
 // ------------------------------------------------------
 function confirmEmailCode() {

     const email =
         getFullEmail();

     const code =
         document
             .getElementById('emailCodeInput')
             .value
             .trim();


     if (!code) {

         showToast(
             '인증번호를 입력해주세요.',
             false
         );

         return;
     }


     if (!/^\d{6}$/.test(code)) {

         showToast(
             '인증번호 6자리를 입력해주세요.',
             false
         );

         return;
     }


     fetch(
         '${pageContext.request.contextPath}/EmailVerify',
         {
             method: 'POST',

             headers: {
                 'Content-Type':
                     'application/x-www-form-urlencoded'
             },

             body:
                 'email=' +
                 encodeURIComponent(email) +
                 '&code=' +
                 encodeURIComponent(code)
         }
     )

     .then(response => response.json())

     .then(data => {

         if (!data.success) {

             document
                 .getElementById('emailVerifyMessage')
                 .textContent =
                 data.message;

             showToast(
                 data.message,
                 false
             );

             return;
         }


         // ==============================================
         // 인증 성공
         // ==============================================

         emailVerified = true;
         verifiedEmail = email;


         document
             .getElementById('memberEmailFull')
             .value = email;


         document
             .getElementById('emailVerifyMessage')
             .textContent =
             '이메일 인증이 완료되었습니다.';


         document
             .getElementById('emailVerifyMessage')
             .className =
             'font-body-sm text-body-sm text-tertiary font-bold';


         // 인증 완료 후 수정 방지
         document
             .getElementById('memberEmailId')
             .readOnly = true;

         document
             .getElementById('memberEmailDomain')
             .readOnly = true;

         document
             .getElementById('memberEmailDomainSelect')
             .disabled = true;

         document
             .getElementById('emailCodeInput')
             .readOnly = true;

         document
             .getElementById('emailVerifyBtn')
             .disabled = true;

         document
             .getElementById('emailSendBtn')
             .disabled = true;


         clearInterval(emailTimerInterval);

         document
             .getElementById('emailTimer')
             .textContent = '완료';


         showToast(
             '이메일 인증이 완료되었습니다.',
             true
         );
     })

     .catch(error => {

         console.error(
             '이메일 인증 오류:',
             error
         );

         showToast(
             '이메일 인증 중 오류가 발생했습니다.',
             false
         );
     });
 }


 // ------------------------------------------------------
 // 5분 타이머
 // ------------------------------------------------------
 function startEmailTimer() {

     clearInterval(emailTimerInterval);

     let remainingSeconds = 300;

     const timer =
         document.getElementById('emailTimer');


     function updateTimer() {

         const minutes =
             Math.floor(remainingSeconds / 60);

         const seconds =
             remainingSeconds % 60;


         timer.textContent =
             String(minutes).padStart(2, '0')
             + ':'
             + String(seconds).padStart(2, '0');


         if (remainingSeconds <= 0) {

             clearInterval(emailTimerInterval);

             timer.textContent = '만료';

             showToast(
                 '이메일 인증번호가 만료되었습니다. 다시 발송해주세요.',
                 false
             );

             return;
         }


         remainingSeconds--;
     }


     updateTimer();

     emailTimerInterval =
         setInterval(
             updateTimer,
             1000
         );
 }


 // ------------------------------------------------------
 // 이메일을 수정하면 인증 상태 초기화
 // ------------------------------------------------------
 function resetEmailVerification() {

     if (emailVerified) {
         return;
     }

     verifiedEmail = "";

     document
         .getElementById('memberEmailFull')
         .value = "";
 }

</script>



		</div>



	</main>







	<jsp:include page="/WEB-INF/bottom.jsp" />



	<jsp:include page="/WEB-INF/liveChat/chat.jsp" />







</body>



</html>