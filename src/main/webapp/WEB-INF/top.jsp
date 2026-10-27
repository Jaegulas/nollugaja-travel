<%-- =========================================================
     주제: 파일 역할
     설명: 사이트 공통 상단 메뉴와 로그인 상태별 네비게이션을 표시
========================================================= --%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%!private String escapeHtml(String value) {
		if (value == null)
			return "";
		return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
				.replace("'", "&#39;");
	}%>
<%-- 로그인 상태는 LoginController에서 저장한 세션 속성명에 맞춰 확인합니다. --%>
<%
String contextPath = request.getContextPath();
String loginId = (String) session.getAttribute("loginId");
String loginName = (String) session.getAttribute("loginName");
String loginRole = (String) session.getAttribute("role");
boolean isAdmin = "ADMIN".equals(loginRole);
String displayName = (loginName != null && !loginName.trim().isEmpty()) ? loginName : loginId;
%>




<style>
/* 놀러가자 헤더 기본 스타일 */
#noleo-global-header {
	isolation: isolate;
	width: 100%;
	box-sizing: border-box;
	font-family: 'Plus Jakarta Sans', 'Noto Sans KR', sans-serif;
	font-size: 14px;
	line-height: 1.5;
	color: #131b2e;
}

#noleo-global-header, #noleo-global-header *, #noleo-global-header *::before,
	#noleo-global-header *::after {
	box-sizing: border-box;
}

#noleo-global-header a {
	text-decoration: none;
}

#noleo-global-header img {
	display: block;
	max-width: 100%;
}

#noleo-global-header button {
	font-family: inherit;
}

#noleo-global-header .material-symbols-outlined {
	font-family: 'Material Symbols Outlined';
	font-style: normal;
	font-weight: normal;
	line-height: 1;
	display: inline-block;
	white-space: nowrap;
	font-feature-settings: 'liga';
}

/* ==========================================
   놀러가자 헤더 반응형 정렬
   ========================================== */

/* PC: 로고 / 메뉴 / 챗봇 / 로그인 */
#noleo-global-header .noleo-header-inner {
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 16px;
}

#noleo-global-header .noleo-desktop-nav, #noleo-global-header .noleo-chatbot-link,
	#noleo-global-header .noleo-auth-area {
	position: static;
	left: auto;
}

#noleo-global-header .noleo-auth-area {
	margin-left: 0;
}

/* 태블릿 및 모바일 */
@media ( max-width : 767px) {
	#noleo-global-header .noleo-header-inner {
		display: grid;
		grid-template-columns: minmax(0, 1fr) auto;
		grid-template-areas: "logo auth" "chat chat";
		height: auto;
		min-height: 80px;
		padding-top: 12px;
		padding-bottom: 12px;
		gap: 10px;
		align-items: center;
	}
	#noleo-global-header .noleo-header-inner>div:first-child {
		grid-area: logo;
		min-width: 0;
	}
	#noleo-global-header .noleo-auth-area {
		grid-area: auth;
		justify-self: end;
		gap: 6px;
	}
	#noleo-global-header .noleo-chatbot-link {
		grid-area: chat;
		justify-self: center;
	}
	
	
/* 권성애추가 2026.10.21수*/
   	#noleo-global-header .noleo-desktop-nav {
   		display: none;
   	}
   	#noleo-global-header .noleo-mobile-nav {
   		display: block;
   	}
   	#noleo-global-header .noleo-mobile-nav nav > div {
   		max-width: 100%;
   		overflow-x: auto;
   		justify-content: flex-start;
   	}
}

/* 작은 휴대폰 화면 */
@media ( max-width : 480px) {
	#noleo-global-header .noleo-header-inner {
		padding-left: 10px;
		padding-right: 10px;
		column-gap: 6px;
	}
	#noleo-global-header .noleo-header-inner>div:first-child img {
		height: 34px;
	}
	#noleo-global-header .noleo-auth-area a {
		font-size: 12px;
		padding: 6px 8px;
	}
	#noleo-global-header .noleo-auth-area>span {
		font-size: 11px;
	}
}

/* 다국어 헤더: 긴 영어 문구가 로고/메뉴와 겹치지 않도록 분리 */
#noleo-global-header .noleo-header-inner {
    max-width: 1600px;
    padding-left: clamp(12px, 2vw, 32px);
    padding-right: clamp(12px, 2vw, 32px);
    gap: clamp(8px, 1vw, 16px);
}
#noleo-global-header .noleo-header-inner > div:first-child,
#noleo-global-header .noleo-header-inner > div:first-child a {
    flex: 0 0 auto;
    min-width: 0;
}
#noleo-global-header .noleo-header-inner > div:first-child a > span {
    white-space: nowrap;
    font-size: clamp(17px, 1.55vw, 24px);
    line-height: 1.2;
}
#noleo-global-header .noleo-desktop-nav {
    min-width: 0;
    flex: 0 1 auto;
    flex-wrap: nowrap;
    gap: 2px;
}
#noleo-global-header .noleo-desktop-nav > a,
#noleo-global-header .noleo-desktop-nav > div > a,
#noleo-global-header .noleo-desktop-nav > div > span {
    display: inline-flex;
    align-items: center;
    white-space: nowrap;
    line-height: 1.25;
    padding: 9px clamp(7px, .7vw, 12px);
    font-size: clamp(12px, .9vw, 14px);
}
#noleo-global-header .noleo-chatbot-link,
#noleo-global-header .noleo-auth-area,
#noleo-global-header .noleo-auth-area > a,
#noleo-global-header .noleo-auth-area > span {
    flex-shrink: 0;
    white-space: nowrap;
}
#noleo-global-header .noleo-auth-area { gap: 4px; }
#noleo-global-header .noleo-auth-area > a { padding-left: 7px; padding-right: 7px; }
/* 메뉴 공간이 부족해지면 겹치는 대신 두 번째 줄로 내림 */
@media (max-width: 1350px) {
    #noleo-global-header .noleo-header-inner {
        flex-wrap: wrap;
        justify-content: space-between;
        height: auto;
        min-height: 80px;
        padding-top: 10px;
        padding-bottom: 10px;
        row-gap: 10px;
    }
    #noleo-global-header .noleo-desktop-nav {
        order: 10;
        flex: 0 0 100%;
        width: 100%;
        justify-content: center;
        overflow: visible;
    }
}
@media (max-width: 767px) {
    #noleo-global-header .noleo-header-inner {
        display: flex;
        flex-wrap: wrap;
        align-items: center;
        justify-content: space-between;
    }
    #noleo-global-header .noleo-header-inner > div:first-child { order: 1; }
    #noleo-global-header .noleo-auth-area { order: 2; }
    #noleo-global-header .language-menu { order: 3; }
    #noleo-global-header .noleo-chatbot-link { order: 4; }
    #noleo-global-header .noleo-desktop-nav {
        order: 5;
        flex: 0 0 100%;
        justify-content: flex-start;
        overflow-x: auto;
        overflow-y: hidden;
        scrollbar-width: thin;
    }
    #noleo-global-header .noleo-desktop-nav > * { flex-shrink: 0; }
}
@media (max-width: 480px) {
    #noleo-global-header .noleo-header-inner > div:first-child a > span { font-size: 16px; }
    #noleo-global-header .noleo-header-inner > div:first-child img { height: 34px; }
    #noleo-global-header .noleo-auth-area { flex-wrap: wrap; justify-content: flex-end; }
}

/* 공통 언어 선택 메뉴 */
#noleo-global-header .language-menu {position:relative; flex-shrink:0; z-index:100;}
#noleo-global-header .language-toggle {display:inline-flex;align-items:center;gap:5px;padding:8px 10px;border:0;border-radius:999px;background:transparent;color:#006574;font-size:12px;font-weight:700;cursor:pointer;white-space:nowrap;}
#noleo-global-header .language-toggle:hover {background:#e8f5f6;}
#noleo-global-header .language-dropdown {display:none;position:absolute;right:0;top:calc(100% + 8px);min-width:130px;padding:6px;background:#fff;border:1px solid #e2e7ed;border-radius:12px;box-shadow:0 8px 24px rgba(19,27,46,.14);}
#noleo-global-header .language-dropdown.active {display:flex;flex-direction:column;}
#noleo-global-header .language-dropdown button {padding:9px 12px;border:0;border-radius:8px;background:transparent;text-align:left;font-size:13px;cursor:pointer;color:#131b2e;}
#noleo-global-header .language-dropdown button:hover,#noleo-global-header .language-dropdown button.selected {background:#e8f5f6;color:#006574;}

</style>


<header id="noleo-global-header"
	class="sticky top-0 z-50 bg-surface-container-lowest/90 backdrop-blur-md shadow-[0_1px_8px_rgba(0,0,0,0.04)]">

	<div
		class="noleo-header-inner h-20 w-full max-w-[1320px] mx-auto px-3 sm:px-4 lg:px-12 flex items-center justify-between gap-2">

		<div class="flex items-center gap-space-lg">

			<a class="flex items-center gap-2 lg:gap-space-sm group shrink-0"
				data-path="home" href="${pageContext.request.contextPath}/Main"><img
				alt="놀러가자 로고" class="w-auto h-12 object-contain shrink-0"
				src="${pageContext.request.contextPath}/resources/images/logo_main.svg" />
				<span
				class="hidden sm:inline font-headline-lg text-headline-lg font-bold tracking-tight text-primary transition-colors group-hover:text-primary-container">
					놀러가자 </span></a>

		</div>

		<!-- 네비게이션 메뉴 (여행지 호버 시 드롭다운 지원) -->

		<nav
			class="noleo-desktop-nav flex items-center gap-1 p-1 bg-surface-container-low/60 rounded-full">

			<!-- 1. '여행지' 항목: hover 시 아래로 '가이드' & '가이드 예약' 드롭다운 메뉴 오픈 -->

			<div class="relative group/nav py-1">

				<a
					class="px-space-md py-space-xs rounded-full font-label-lg text-label-lg text-on-surface-variant hover:text-on-surface hover:bg-surface-container transition-all"
					href="${pageContext.request.contextPath}/TravelDestination/list">
					여행지 </a>

				<!-- 호버 시 나타나는 드롭다운 메뉴 카드 -->

				<div
					class="absolute left-0 top-full pt-2 w-72 opacity-0 pointer-events-none group-hover/nav:opacity-100 group-hover/nav:pointer-events-auto transition-all duration-200 transform translate-y-1 group-hover/nav:translate-y-0 z-50">

					<div
						class="bg-surface-container-lowest rounded-2xl shadow-[0_12px_32px_rgba(0,101,116,0.16)] border border-surface-container-high/80 p-2.5 backdrop-blur-md">

						<div
							class="px-3 py-1.5 border-b border-surface-container/60 mb-1 flex items-center justify-between">

							<span
								class="text-[11px] font-bold text-outline tracking-wider uppercase">여행지

								맞춤 메뉴</span> <span
								class="text-[10px] bg-primary/10 text-primary font-bold px-1.5 py-0.5 rounded">HOT</span>

						</div>

						<!-- 가이드 서브 메뉴 버튼 -->

						<a
							class="w-full text-left p-2.5 rounded-xl hover:bg-surface-container-low transition-all duration-150 flex items-start gap-3 group/dest mb-1 cursor-pointer"
							href="${pageContext.request.contextPath}/TravelDestination/list"><div
								class="w-9 h-9 rounded-lg bg-primary/10 text-primary flex items-center justify-center shrink-0 group-hover/dest:bg-primary group-hover/dest:text-white transition-colors">
								<span class="material-symbols-outlined text-[20px]">explore</span>
							</div>
							<div class="flex-1 min-w-0">
								<div class="flex items-center justify-between">
									<span
										class="text-[14px] font-bold text-on-surface group-hover/dest:text-primary transition-colors">여행지</span><span
										class="material-symbols-outlined text-[16px] text-outline opacity-0 group-hover/dest:opacity-100 transition-opacity">arrow_forward</span>
								</div>
								<p class="text-[12px] text-on-surface-variant truncate mt-0.5">전국
									권역별 테마 여행지 &amp; 추천 명소</p>
							</div></a>


						<button
							class="w-full text-left p-2.5 rounded-xl hover:bg-secondary-container/10 transition-all duration-150 flex items-start gap-3 group/item"
							onclick="window.location.href='${pageContext.request.contextPath}/GuideList'"
							type="button">

							<div
								class="w-9 h-9 rounded-lg bg-secondary-container/15 text-secondary flex items-center justify-center shrink-0 group-hover/item:bg-secondary-container group-hover/item:text-white transition-colors">

								<span class="material-symbols-outlined text-[20px]">
									menu_book </span>

							</div>

							<div class="flex-1 min-w-0">

								<div class="flex items-center justify-between">

									<span
										class="text-[14px] font-bold text-on-surface group-hover/item:text-secondary transition-colors">
										가이드 / 예약 </span> <span
										class="material-symbols-outlined text-[16px] text-outline opacity-0 group-hover/item:opacity-100 transition-opacity">
										arrow_forward </span>

								</div>

								<p class="text-[12px] text-on-surface-variant truncate mt-0.5">
									지역별 여행 가이드북 및 맞춤 코스</p>

							</div>

						</button>


					</div>

				</div>

			</div>

			<a
				class="px-space-md py-space-xs rounded-full font-label-lg text-label-lg text-on-surface-variant hover:text-on-surface hover:bg-surface-container transition-all"
				data-path="stays"
				href="${pageContext.request.contextPath}/Accommodation/acco_main">숙박</a><a
				class="px-space-md py-space-xs rounded-full font-label-lg text-label-lg text-on-surface-variant hover:text-on-surface hover:bg-surface-container transition-all"
				data-path="dining"
				href="${pageContext.request.contextPath}/food/list">음식</a><a
				class="px-space-md py-space-xs rounded-full font-label-lg text-label-lg text-on-surface-variant hover:text-on-surface hover:bg-surface-container transition-all"
				data-path="cultural-heritage"
				href="${pageContext.request.contextPath}/Cultural">문화유산</a>

			<!-- 커뮤니티 메뉴 -->

			<div class="relative group/community py-1">



				<!-- 커뮤니티 메인 버튼 -->


				<!-- 커뮤니티 메인 버튼 : 클릭 불가, 호버 가능 -->
				<span
					class="px-space-md py-space-xs rounded-full font-label-lg text-label-lg text-on-surface-variant hover:text-on-surface hover:bg-surface-container transition-all inline-flex items-center cursor-default">
					커뮤니티 </span>



				<!-- 마우스 호버 시 나타나는 드롭다운 -->

				<div
					class="absolute left-0 top-full pt-2 w-72 opacity-0 pointer-events-none

			               group-hover/community:opacity-100

			               group-hover/community:pointer-events-auto

			               transition-all duration-200 transform translate-y-1

			               group-hover/community:translate-y-0 z-50">



					<div
						class="bg-surface-container-lowest rounded-2xl

		                   shadow-[0_12px_32px_rgba(0,101,116,0.16)]

		                   border border-surface-container-high/80

		                   p-2.5 backdrop-blur-md">



						<!-- 드롭다운 제목 -->

						<div
							class="px-3 py-1.5 border-b border-surface-container/60

                       	 	mb-1 flex items-center justify-between">



							<span
								class="text-[11px] font-bold text-outline tracking-wider uppercase">여행자
								소통 공간 </span> <span
								class="text-[10px] bg-secondary-container/15 text-secondary

            				 font-bold px-1.5 py-0.5 rounded">NEW</span>

						</div>



						<!-- Q&A -->

						<a href="${pageContext.request.contextPath}/Qna"
							class="w-full text-left p-2.5 rounded-xl

                        hover:bg-surface-container-low transition-all

                        duration-150 flex items-start gap-3 group/item">



							<div
								class="w-9 h-9 rounded-lg bg-primary/10 text-primary

	                        flex items-center justify-center shrink-0

	                        group-hover/item:bg-primary

	                        group-hover/item:text-white transition-colors">



								<span class="material-symbols-outlined text-[20px]">help_outline
								</span>

							</div>



							<div class="flex-1 min-w-0">

								<span
									class="text-[14px] font-bold text-on-surface

                             group-hover/item:text-primary transition-colors">Q&A
								</span>



								<p class="text-[12px] text-on-surface-variant truncate mt-0.5">

									질문 & 답변, 여행 팁 묻고 답하기</p>

							</div>
						</a>



						<!-- 리뷰게시판 -->

						<a href="${pageContext.request.contextPath}/Review"
							class="w-full text-left p-2.5 rounded-xl

                        hover:bg-secondary-container/10 transition-all

                        duration-150 flex items-start gap-3 group/res mt-1">



							<div
								class="w-9 h-9 rounded-lg bg-secondary-container/15

			                           text-secondary flex items-center justify-center
			
			                           shrink-0 group-hover/res:bg-secondary-container
			
			                           group-hover/res:text-white transition-colors">



								<span class="material-symbols-outlined text-[20px]">rate_review
								</span>

							</div>



							<div class="flex-1 min-w-0">



								<div class="flex items-center justify-between">

									<span
										class="text-[14px] font-bold text-on-surface

                             group-hover/res:text-secondary transition-colors">리뷰게시판
									</span> <span
										class="text-[10px] font-bold bg-secondary-container

                             text-white px-1.5 py-0.5 rounded-full">HOT
									</span>

								</div>

								<p class="text-[12px] text-on-surface-variant truncate mt-0.5">생생한
									여행자 실시간 후기 & 평점</p>



							</div>
						</a>

					</div>

				</div>

			</div>

		</nav>

		<a
			class="noleo-chatbot-link inline-flex shrink-0 w-max h-[26px] items-center justify-center gap-1 px-2.5 py-1 rounded-full whitespace-nowrap text-[11px] font-bold text-primary bg-primary/10 hover:bg-primary hover:text-white transition-all duration-200 group/chatbot shadow-sm cursor-pointer"
			href="${pageContext.request.contextPath}/Chatbot/newconversation">
			<span
			class="material-symbols-outlined text-[15px] text-primary group-hover/chatbot:text-white transition-colors">smart_toy</span>

			<span class="whitespace-nowrap shrink-0">AI 챗봇</span> <span
			class="shrink-0 whitespace-nowrap px-1.5 py-0.2 rounded-full text-[9px] font-extrabold bg-secondary-container text-white tracking-tight">AI</span>
		</a>



		<div class="noleo-auth-area flex items-center gap-1 sm:gap-2 shrink-0">


			<%
			if (loginId == null) {
			%>
			<%-- 비로그인: 로그인 / 회원가입 아래 redirect=${pageContext.request.requestURI} 권성애 추가 2026.10.14수 로그인 후 이전 페이지 이동위해 --%>
			<a
				class="inline-flex items-center px-space-md py-space-sm rounded-full font-label-md text-[16px] font-extrabold text-on-surface-variant hover:bg-surface-container transition-colors"
				data-path="login" href="<%=contextPath%>/Login"
				onclick="this.href='<%=contextPath%>/Login?redirect=' + encodeURIComponent(window.location.pathname + window.location.search);">로그인</a>
			<a
				class="inline-flex items-center justify-center px-3 sm:px-space-lg py-space-sm rounded-full font-label-lg text-label-lg text-on-secondary bg-secondary-container hover:bg-secondary transition-all shadow-[0_4px_16px_rgba(253,110,88,0.25)] whitespace-nowrap"
				data-path="signup" href="<%=contextPath%>/Register">회원가입</a>
			<%
			} else {
			%>
			<%-- 로그인: 이름 클릭 시 페이지 이동 / 로그아웃 --%>

			<span
				class="inline-flex items-center gap-1.5 text-[14px] font-bold text-black">

				<span class="material-symbols-outlined text-[20px]">
					account_circle </span> <%=escapeHtml(displayName)%>님

			</span>


			<%-- 관리자 / 일반 회원 메뉴 구분 --%>
			<%
			if (isAdmin) {
			%>

			<%-- 관리자 로그인 --%>
			<a href="<%=contextPath%>/Admin/admin_main"
				class="inline-flex items-center gap-1 px-space-md py-space-sm rounded-full text-[14px] font-bold text-primary hover:bg-surface-container transition-colors">

				<span class="material-symbols-outlined text-[18px]">
					admin_panel_settings </span> 관리자 페이지
			</a>

			<%
			} else {
			%>

			<%-- 일반 회원 로그인 --%>
			<a href="<%=contextPath%>/Mypage/mypage_main"
				class="inline-flex items-center gap-1 px-space-md py-space-sm rounded-full font-label-md text-[14px] font-bold text-primary hover:bg-surface-container transition-colors">

				<span class="material-symbols-outlined text-[18px]"> person </span>
				마이페이지
			</a>

			<%
			}
			%>


			<%-- 로그아웃 버튼 --%>
			<a href="<%=contextPath%>/Logout"
				class="inline-flex items-center gap-1.5 pl-1 pr-space-md py-space-sm rounded-full text-[13px] font-bold text-on-surface-variant hover:bg-surface-container transition-colors">

				<span class="material-symbols-outlined text-[18px]"> logout </span>

				로그아웃

			</a>
			<%
			}
			%>
		</div>
		<%-- 번역 기능: 로그인/비로그인 모두 사용 가능 --%>
		<div class="language-menu">
			<button type="button" id="languageToggle" class="language-toggle" aria-haspopup="true" aria-expanded="false" aria-controls="languageDropdown">
				<span class="material-symbols-outlined text-[19px]">language</span>
				<span id="currentLanguage">한국어</span>
				<span class="material-symbols-outlined text-[16px]">expand_more</span>
			</button>
			<div id="languageDropdown" class="language-dropdown">
				<button type="button" id="koBtn" class="selected">한국어</button>
				<button type="button" id="enBtn">English</button>
				<button type="button" id="jaBtn">日本語</button>
			</div>
		</div>
	</div>
	<!-- =====================================================
     화면이 작을 때 표시되는 2번째 줄 메뉴
     ===================================================== -->
<!-- 	<div
		class="hidden w-full bg-surface-container-lowest border-t border-surface-container-low/60">
권성애 2026.10.21목 아래로 변경 모바일 -->
 
 	<div
		class="noleo-mobile-nav hidden w-full bg-surface-container-lowest border-t border-surface-container-low/60">
		<nav class="flex items-center justify-center px-2 py-2">

			<div
				class="flex items-center justify-center gap-1 p-1
                    bg-surface-container-low/60 rounded-full
                    whitespace-nowrap">

				<a
					class="px-3 py-2 rounded-full font-label-md text-label-md
                       text-on-surface-variant hover:text-on-surface
                       hover:bg-surface-container transition-all"
					href="${pageContext.request.contextPath}/TravelDestination/list">
					여행지 </a> <a
					class="px-3 py-2 rounded-full font-label-md text-label-md
                       text-on-surface-variant hover:text-on-surface
                       hover:bg-surface-container transition-all"
					href="${pageContext.request.contextPath}/Accommodation/acco_main">
					숙박 </a> <a
					class="px-3 py-2 rounded-full font-label-md text-label-md
                       text-on-surface-variant hover:text-on-surface
                       hover:bg-surface-container transition-all"
					href="${pageContext.request.contextPath}/food"> 음식 </a> <a
					class="px-3 py-2 rounded-full font-label-md text-label-md
                       text-on-surface-variant hover:text-on-surface
                       hover:bg-surface-container transition-all"
					href="${pageContext.request.contextPath}/Cultural"> 문화유산 </a> <a
					class="px-3 py-2 rounded-full font-label-md text-label-md
                       text-on-surface-variant hover:text-on-surface
                       hover:bg-surface-container transition-all"
					href="${pageContext.request.contextPath}/Qna"> 커뮤니티 </a>

			</div>

		</nav>

	</div>
</header>
<%-- 공통 번역 스크립트: 실제 배포 경로에 맞춰 resources/js/translate.js 배치 --%>
<script>
window.TRANSLATE_CONTEXT_PATH = '<%=contextPath%>';
</script>
<script src="<%=contextPath%>/resources/js/translate/translate.js" defer></script>
