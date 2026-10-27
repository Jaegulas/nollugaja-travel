<%-- =========================================================
     주제: 파일 역할
     설명: members 영역의 findPassword 화면을 구성하고 서버에서 전달받은 데이터를 표시
========================================================= --%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE html>

<html lang="ko">

<head>

<meta charset="UTF-8">

<!-- 모바일에서도 PC 화면 전체를 축소해서 표시 -->

<meta name="viewport" content="width=1320, initial-scale=1.0">

<title>비밀번호 찾기 | 놀러가자</title>

<script src="https://cdn.tailwindcss.com"></script>

<script type="text/javascript">

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

<link
	href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined"
	rel="stylesheet">

<link
	href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;600;700;800&family=Noto+Sans+KR:wght@400;500;600;700;800&display=swap"
	rel="stylesheet">

<style>
html, body {
	margin: 0;
	padding: 0;
	min-width: 1320px;
	font-family: 'Plus Jakarta Sans', 'Noto Sans KR', sans-serif;
	background: #faf8ff;
	color: #131b2e;
}

.find-id-wrapper {
	width: 100%;
	min-height: 600px;
	padding: 40px 20px;
	display: flex;
	align-items: center;
	justify-content: center;
	box-sizing: border-box;
}

.find-id-card {
	width: 460px;
	flex-shrink: 0;
	box-sizing: border-box;
	background: #ffffff;
	padding: 40px;
	border-radius: 20px;
	box-shadow: 0 12px 40px rgba(19, 27, 46, 0.08);
}

.find-id-input {
	width: 100%;
	box-sizing: border-box;
	padding: 14px 16px;
	background: #f2f3ff;
	border: 1px solid transparent;
	border-radius: 12px;
	outline: none;
	margin-top: 8px;
}

.find-id-input:focus {
	border-color: #008092;
	background: white;
}

.find-id-input:disabled {
	opacity: .65;
	cursor: not-allowed;
}

.find-id-button {
	width: 100%;
	padding: 14px;
	border-radius: 12px;
	font-weight: bold;
	background: #006574;
	color: white;
	cursor: pointer;
}

.find-id-button:disabled {
	opacity: .5;
	cursor: not-allowed;
}

.find-id-message {
	margin-top: 14px;
	font-size: 13px;
	text-align: center;
	color: #006574;
	min-height: 20px;
}

/* 비밀번호 찾기 안내 모달 */
.pw-modal-overlay {
	position: fixed;
	inset: 0;
	z-index: 99999;
	display: none;
	align-items: center;
	justify-content: center;
	background: rgba(19, 27, 46, .48);
}

.pw-modal-overlay.is-open {
	display: flex;
}

.pw-modal-card {
	width: 390px;
	box-sizing: border-box;
	padding: 32px 30px;
	background: #fff;
	border-radius: 20px;
	text-align: center;
	box-shadow: 0 20px 60px rgba(19, 27, 46, .20);
}

.pw-modal-icon {
	font-size: 44px;
	color: #ba1a1a;
}

.pw-modal-title {
	margin: 10px 0;
	font-size: 21px;
	font-weight: 800;
}

.pw-modal-description {
	font-size: 14px;
	color: #566071;
	line-height: 1.7;
}

.pw-modal-confirm {
	margin-top: 24px;
	width: 100%;
	padding: 13px;
	border-radius: 12px;
	background: #006574;
	color: #fff;
	font-weight: 700;
	cursor: pointer;
}
</style>

</head>

<body class="bg-surface font-body-md text-on-surface antialiased">

	<jsp:include page="/WEB-INF/top.jsp" />

	<main class="find-id-wrapper">

		<section class="find-id-card">

			<div class="text-center mb-8">

				<div class="flex items-center justify-center gap-3">

					<span class="material-symbols-outlined"
						style="font-size: 32px; color: #006574;">lock_reset</span>

					<h1 class="text-3xl font-bold">비밀번호 찾기</h1>

				</div>

				<p class="text-sm text-gray-500 mt-3">가입할 때 입력한 아이디와 이메일을

					입력해주세요.</p>

			</div>



			<div id="accountArea">

				<div class="mb-5">

					<label for="findPwId" class="font-semibold">아이디</label> <input
						id="findPwId" class="find-id-input" type="text" maxlength="50"
						autocomplete="username" placeholder="아이디를 입력해주세요">

				</div>

				<div class="mb-5">

					<label for="findPwEmail" class="font-semibold">이메일</label> <input
						id="findPwEmail" class="find-id-input" type="email"
						maxlength="254" autocomplete="email" placeholder="가입한 이메일을 입력해주세요">

				</div>

				<button id="sendButton" type="button" class="find-id-button"
					onclick="sendFindPasswordCode()">인증번호 발송</button>

			</div>



			<!-- 인증번호 발송 전에는 숨김 -->

			<div id="verifyArea" class="mt-6" style="display: none;">

				<label for="findPwCode" class="font-semibold">이메일 인증번호</label>

				<div style="position: relative;">

					<input id="findPwCode" class="find-id-input" type="text"
						inputmode="numeric" maxlength="6" autocomplete="one-time-code"
						placeholder="6자리 인증번호" style="padding-right: 90px;"> <span
						id="findPwTimer"
						style="position: absolute; right: 16px; top: 50%; transform: translateY(-35%); font-size: 14px; font-weight: 700; color: #ba1a1a; display: none;">05:00</span>

				</div>

				<button id="verifyButton" type="button" class="find-id-button mt-4"
					onclick="verifyFindPasswordCode()">인증번호 확인</button>

				<p class="text-xs text-gray-500 mt-3">인증번호는 발송 후 5분 동안 유효합니다.</p>

			</div>



			<!-- 이메일 인증 성공 전에는 숨김 -->

			<div id="resetArea" class="mt-6" style="display: none;">

				<div class="mb-5">

					<label for="newPassword" class="font-semibold">새 비밀번호</label> <input
						id="newPassword" class="find-id-input" type="password"
						minlength="8" maxlength="72" autocomplete="new-password"
						placeholder="8~72자 비밀번호">

				</div>

				<div class="mb-5">

					<label for="confirmPassword" class="font-semibold">새 비밀번호

						확인</label> <input id="confirmPassword" class="find-id-input"
						type="password" minlength="8" maxlength="72"
						autocomplete="new-password" placeholder="새 비밀번호를 다시 입력해주세요">

				</div>

				<button id="resetButton" type="button" class="find-id-button"
					onclick="submitNewPassword()">비밀번호 변경</button>

			</div>



			<p id="findPwMessage" class="find-id-message" role="status"
				aria-live="polite"></p>



			<div id="resetResult" class="hidden mt-6 p-5 rounded-xl text-center"
				style="background: #f2f3ff;">

				<span class="material-symbols-outlined"
					style="font-size: 36px; color: #006574;">check_circle</span>

				<p class="text-lg font-bold mt-2" style="color: #006574;">비밀번호

					변경 완료</p>

				<p class="text-sm text-gray-500 mt-2">새 비밀번호로 로그인해주세요.</p>

			</div>



			<div class="text-center mt-8">

				<a href="${pageContext.request.contextPath}/Login"
					class="text-sm font-semibold" style="color: #006574;">로그인 화면으로

					돌아가기</a>

			</div>

		</section>

	</main>

	<jsp:include page="/WEB-INF/bottom.jsp" />

	<jsp:include page="/WEB-INF/liveChat/chat.jsp" />




	<!-- 아이디·이메일 불일치 등 오류 안내 모달 -->
	<div id="pwErrorModal" class="pw-modal-overlay" role="dialog"
		aria-modal="true" aria-labelledby="pwErrorTitle"
		aria-describedby="pwErrorDescription">
		<div class="pw-modal-card">
			<span class="material-symbols-outlined pw-modal-icon"
				aria-hidden="true">error</span>
			<h2 id="pwErrorTitle" class="pw-modal-title">확인해주세요</h2>
			<p id="pwErrorDescription" class="pw-modal-description"></p>
			<button id="pwModalConfirm" type="button" class="pw-modal-confirm"
				onclick="closeFindPwModal()">확인</button>
		</div>
	</div>

	<script>

const FIND_PASSWORD_URL = '${pageContext.request.contextPath}/FindPassword';

let findPwTimerInterval = null;

let findPwExpireTime = 0;

let pwVerified = false;



function showFindPwMessage(message, error) {
    const el = document.getElementById('findPwMessage');
    el.textContent = error ? '' : message;
    el.style.color = '#006574';
    if (error) {
        document.getElementById('pwErrorDescription').textContent = message;
        document.getElementById('pwErrorModal').classList.add('is-open');
        document.getElementById('pwModalConfirm').focus();
    }
}

function closeFindPwModal() {
    document.getElementById('pwErrorModal').classList.remove('is-open');
    document.getElementById('sendButton').focus();
}

document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape' && document.getElementById('pwErrorModal').classList.contains('is-open')) {
        closeFindPwModal();
    }
});



function stopFindPwTimer() {

    if (findPwTimerInterval !== null) {

        clearInterval(findPwTimerInterval);

        findPwTimerInterval = null;

    }

}



function startFindPwTimer() {

    stopFindPwTimer();

    findPwExpireTime = Date.now() + 5 * 60 * 1000;

    const timer = document.getElementById('findPwTimer');

    timer.style.display = 'block';



    function updateTimer() {

        const remaining = Math.max(0, Math.ceil((findPwExpireTime - Date.now()) / 1000));

        const minutes = Math.floor(remaining / 60);

        const seconds = remaining % 60;

        timer.textContent = String(minutes).padStart(2, '0') + ':' + String(seconds).padStart(2, '0');

        if (remaining <= 0) {

            stopFindPwTimer();

            document.getElementById('verifyButton').disabled = true;

            showFindPwMessage('인증시간이 만료되었습니다. 인증번호를 다시 발송해주세요.', true);

        }

    }

    updateTimer();

    findPwTimerInterval = setInterval(updateTimer, 1000);

}



async function postFindPassword(params) {

    const response = await fetch(FIND_PASSWORD_URL, {

        method: 'POST',

        credentials: 'same-origin',

        headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },

        body: params.toString()

    });

    return response.json();

}



async function sendFindPasswordCode() {

    const id = document.getElementById('findPwId').value.trim();

    const email = document.getElementById('findPwEmail').value.trim();

    const button = document.getElementById('sendButton');

    if (!id || !email) {

        showFindPwMessage('아이디와 이메일을 입력해주세요.', true);

        return;

    }

    if (!document.getElementById('findPwEmail').checkValidity()) {

        showFindPwMessage('올바른 이메일 형식을 입력해주세요.', true);

        return;

    }

    button.disabled = true;

    try {

        const params = new URLSearchParams();

        params.set('action', 'send');

        params.set('id', id);

        params.set('email', email);

        const result = await postFindPassword(params);

        showFindPwMessage(result.message, !result.success);

        if (result.success) {

            pwVerified = false;

            document.getElementById('verifyArea').style.display = 'block';

            document.getElementById('resetArea').style.display = 'none';

            document.getElementById('resetResult').classList.add('hidden');

            document.getElementById('findPwCode').value = '';

            document.getElementById('verifyButton').disabled = false;

            document.getElementById('findPwId').disabled = true;

            document.getElementById('findPwEmail').disabled = true;

            startFindPwTimer();

        }

    } catch (error) {

        showFindPwMessage('서버 연결에 실패했습니다. 다시 시도해주세요.', true);

    } finally {

        button.disabled = false;

    }

}



async function verifyFindPasswordCode() {

    const code = document.getElementById('findPwCode').value.trim();

    const button = document.getElementById('verifyButton');

    if (!/^\d{6}$/.test(code)) {

        showFindPwMessage('6자리 인증번호를 입력해주세요.', true);

        return;

    }

    button.disabled = true;

    try {

        const params = new URLSearchParams();

        params.set('action', 'verify');

        params.set('code', code);

        const result = await postFindPassword(params);

        showFindPwMessage(result.message, !result.success);

        if (result.success) {

            pwVerified = true;

            stopFindPwTimer();

            document.getElementById('findPwTimer').style.display = 'none';

            document.getElementById('verifyArea').style.display = 'none';

            document.getElementById('resetArea').style.display = 'block';

            document.getElementById('sendButton').disabled = true;

        } else {

            button.disabled = false;

        }

    } catch (error) {

        showFindPwMessage('서버 연결에 실패했습니다. 다시 시도해주세요.', true);

        button.disabled = false;

    }

}



async function submitNewPassword() {

    if (!pwVerified) {

        showFindPwMessage('이메일 인증을 먼저 완료해주세요.', true);

        return;

    }

    const password = document.getElementById('newPassword').value;

    const confirm = document.getElementById('confirmPassword').value;

    const button = document.getElementById('resetButton');

    if (password.length < 8 || password.length > 72) {

        showFindPwMessage('비밀번호는 8~72자로 입력해주세요.', true);

        return;

    }

    if (password !== confirm) {

        showFindPwMessage('비밀번호 확인이 일치하지 않습니다.', true);

        return;

    }

    button.disabled = true;

    try {

        const params = new URLSearchParams();

        params.set('action', 'reset');

        params.set('password', password);

        params.set('confirmPassword', confirm);

        const result = await postFindPassword(params);

        showFindPwMessage(result.message, !result.success);

        if (result.success) {

            pwVerified = false;

            document.getElementById('accountArea').style.display = 'none';

            document.getElementById('verifyArea').style.display = 'none';

            document.getElementById('resetArea').style.display = 'none';

            document.getElementById('resetResult').classList.remove('hidden');

            document.getElementById('newPassword').value = '';

            document.getElementById('confirmPassword').value = '';

        } else {

            button.disabled = false;

        }

    } catch (error) {

        showFindPwMessage('서버 연결에 실패했습니다. 다시 시도해주세요.', true);

        button.disabled = false;

    }

}

</script>

</body>

</html>
