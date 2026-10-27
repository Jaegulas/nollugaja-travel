<%-- =========================================================
     주제: 파일 역할
     설명: WEB-INF 영역의 bottom 화면을 구성하고 서버에서 전달받은 데이터를 표시
========================================================= --%>
<%-- 이 페이지의 글자 인코딩을 UTF-8 로 정한다. 빠뜨리면 한글이 깨진다 --%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<footer class="w-full bg-white shadow-[0_-1px_6px_rgba(0,0,0,0.02)]"
	style="background-color: #ffffff !important; width: 100%; display: block;">

		<div class="max-w-[1320px] mx-auto px-margin-sm lg:px-margin py-space-xl">

		<div
			class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-12 gap-gutter mb-space-xl">
			<div class="lg:col-span-5 space-y-space-md">
				<div class="flex items-center gap-space-sm">
					<span
						class="font-headline-lg text-headline-lg font-bold tracking-tight text-primary">놀러가자</span>
				</div>
				<p
					class="font-body-md text-body-md text-on-surface-variant max-w-sm">대한민국
					방방곡곡, 나만의 특별한 여행을 만나보세요. 숨은 명소와 감성 숙소, 취향 맞춤형 여정을 함께 엮어갑니다.</p>
				<div class="pt-space-xs">
					<p
						class="font-label-sm text-label-sm text-on-surface-variant mb-space-xs uppercase tracking-wider">고객센터
						안내</p>
					<a
						class="font-headline-md text-headline-md font-bold text-primary hover:text-primary-container transition-colors"
						href="tel:1588-0000">1588-0000</a>
					<p class="font-body-sm text-body-sm text-outline mt-space-xs">운영시간
						09:00 - 18:00 (주말 및 공휴일 휴무)</p>
				</div>
			</div>
			<div class="lg:col-span-2 space-y-space-sm">
				<p class="font-label-lg text-label-lg font-bold text-on-surface">서비스
					안내</p>
				<ul class="space-y-space-xs font-body-sm text-body-sm">
					<li class="py-0.5"><a
						class="text-on-surface-variant hover:text-primary transition-colors"
						data-path="about-service" href="#">서비스 소개</a></li>
					<li class="py-0.5"><a
						class="text-on-surface-variant hover:text-primary transition-colors"
						data-path="destinations" href="#">추천 여행지</a></li>
					<li class="py-0.5"><a
						class="text-on-surface-variant hover:text-primary transition-colors"
						data-path="creator-apply" href="#">여행작가 지원</a></li>
					<li class="py-0.5"><a
						class="text-on-surface-variant hover:text-primary transition-colors"
						data-path="partnership" href="#">제휴 / 입점 문의</a></li>
				</ul>
			</div>
			<div class="lg:col-span-2 space-y-space-sm">
				<p class="font-label-lg text-label-lg font-bold text-on-surface">고객
					지원</p>
				<ul class="space-y-space-xs font-body-sm text-body-sm">
					<li class="py-0.5"><a
						class="text-on-surface-variant hover:text-primary transition-colors"
						data-path="faq" href="#">자주 묻는 질문</a></li>
					<li class="py-0.5"><a
						class="text-on-surface-variant hover:text-primary transition-colors"
						data-path="notices" href="#">공지사항</a></li>
					<li class="py-0.5"><a
						class="text-on-surface-variant hover:text-primary transition-colors"
						data-path="inquiry" href="#">1:1 문의하기</a></li>
				</ul>
			</div>
			<div class="lg:col-span-3 space-y-space-sm">
				<p class="font-label-lg text-label-lg font-bold text-on-surface">소셜
					미디어</p>
				<p class="font-body-sm text-body-sm text-on-surface-variant">놀러가자의
					새로운 테마 코스와 실시간 여행 소식을 만나보세요.</p>
				<div class="flex items-center gap-space-sm pt-space-xs">
					<a aria-label="공식 블로그"
						class="w-9 h-9 rounded-full bg-surface-container flex items-center justify-center text-on-surface-variant hover:bg-surface-container-high hover:text-primary transition-colors"
						href="#"><span class="material-symbols-outlined text-[18px]">rss_feed</span></a><a
						aria-label="영상 채널"
						class="w-9 h-9 rounded-full bg-surface-container flex items-center justify-center text-on-surface-variant hover:bg-surface-container-high hover:text-primary transition-colors"
						href="#"><span class="material-symbols-outlined text-[18px]">play_circle</span></a><a
						aria-label="사진 갤러리"
						class="w-9 h-9 rounded-full bg-surface-container flex items-center justify-center text-on-surface-variant hover:bg-surface-container-high hover:text-primary transition-colors"
						href="#"><span class="material-symbols-outlined text-[18px]">photo_camera</span></a><a
						aria-label="메일 구독"
						class="w-9 h-9 rounded-full bg-surface-container flex items-center justify-center text-on-surface-variant hover:bg-surface-container-high hover:text-primary transition-colors"
						href="#"><span class="material-symbols-outlined text-[18px]">mail</span></a>
				</div>
			</div>
		</div>
		<div
			class="pt-space-md border-t border-surface-container flex flex-col md:flex-row items-center justify-between gap-space-md">
			<div
				class="flex flex-wrap items-center gap-space-md font-body-sm text-body-sm">
				<a
					class="text-on-surface-variant hover:text-on-surface transition-colors"
					data-path="terms-of-service" href="#">이용약관</a><a
					class="font-bold text-on-surface hover:text-primary transition-colors"
					data-path="privacy-policy"
					href="${pageContext.request.contextPath}/PrivacyPolicy">개인정보처리방침</a><a
					class="text-on-surface-variant hover:text-on-surface transition-colors"
					data-path="location-terms" href="#">위치기반서비스 이용약관</a><a
					class="text-on-surface-variant hover:text-on-surface transition-colors"
					data-path="consumer-safety" href="#">소비자 안전수칙</a>
			</div>
			<p class="font-body-sm text-body-sm text-outline">© 2026
				Noleogaja Inc. All rights reserved.</p>
		</div>
	</div>
</footer>