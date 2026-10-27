// =========================================================
// 주제: 파일 역할
// 설명: tossTestPayment 화면의 사용자 인터랙션과 비동기 요청을 처리
// =========================================================


(function () {
    "use strict";

    // 결제 기능 초기화
    // =========================================================
    // 주제: initialize 처리
    // 설명: initialize 기능에 필요한 처리 로직을 수행
    // =========================================================
    function initialize() {

        console.log("[Toss] JS 초기화 시작");

        var cfg = window.travelTossConfig;
        var button = document.getElementById("piPay");

        console.log("[Toss] 설정 객체 존재:", !!cfg);
        console.log("[Toss] 결제 버튼 존재:", !!button);
        console.log("[Toss] SDK 로딩:", typeof window.TossPayments);

        if (!cfg) {
            console.error("[Toss] travelTossConfig가 없습니다.");
            return;
        }

        if (!button) {
            console.error("[Toss] piPay 버튼을 찾을 수 없습니다.");
            return;
        }

        // 중복 이벤트 등록 방지
        if (button.dataset.tossInitialized === "true") {
            return;
        }

        button.dataset.tossInitialized = "true";

        button.addEventListener("click", function () {

            console.log("[Toss] 결제 버튼 클릭 이벤트 실행!");

            // 1. 필수 동의 확인
            var terms1 = document.getElementById("piTerms1");
            var terms2 = document.getElementById("piTerms2");

            if (!terms1 || !terms2 ||
                !terms1.checked || !terms2.checked) {

                alert("필수 확인 사항에 동의해 주세요.");
                return;
            }

            // 2. SDK 확인
            if (typeof window.TossPayments !== "function") {
                alert("토스페이먼츠 SDK를 불러오지 못했습니다.");
                return;
            }

            // 3. 결제 설정 확인
            if (!cfg.clientKey || !cfg.customerKey ||
                !cfg.orderId || !Number(cfg.amount) ||
                !cfg.contextPath) {

                console.error("[Toss] 결제 설정값이 누락되었습니다.");
                alert("결제 정보를 불러오지 못했습니다.");
                return;
            }

            // 4. 중복 결제 요청 방지
            button.disabled = true;

            try {

                var tossPayments = window.TossPayments(
                    cfg.clientKey
                );

                var payment = tossPayments.payment({
                    customerKey: cfg.customerKey
                });

                var base =
                    window.location.origin + cfg.contextPath;

                console.log("[Toss] 결제창 요청 시작");

                // 5. 토스 테스트 결제창 호출
                Promise.resolve(
                    payment.requestPayment({

                        method: "CARD",

                        amount: {
                            currency: "KRW",
                            value: Number(cfg.amount)
                        },

                        orderId: cfg.orderId,

                        orderName: "여행 가이드 투어 예약",

                        successUrl:
                            base + "/TossPaymentSuccess",

                        failUrl:
                            base + "/TossPaymentFail"

                    })
                )
                .then(function () {

                    console.log("[Toss] 결제 요청 처리 완료");

                })
                .catch(function (error) {

                    console.error(
                        "[Toss] 결제 요청 실패:",
                        error
                    );

                    if (!error || error.code !== "USER_CANCEL") {

                        alert(
                            "결제창 호출 실패: " +
                            (error && error.message
                                ? error.message
                                : "알 수 없는 오류")
                        );
                    }

                })
                .then(function () {

                    button.disabled = false;

                });

            } catch (error) {

                console.error(
                    "[Toss] 결제 초기화 오류:",
                    error
                );

                alert(
                    "결제 초기화 실패: " +
                    (error.message || "알 수 없는 오류")
                );

                button.disabled = false;
            }
        });

        console.log("[Toss] 결제 버튼 이벤트 등록 완료");
    }

    // HTML 로딩 완료 후 실행
    if (document.readyState === "loading") {

        document.addEventListener(
            "DOMContentLoaded",
            initialize
        );

    } else {

        initialize();

    }

})();
