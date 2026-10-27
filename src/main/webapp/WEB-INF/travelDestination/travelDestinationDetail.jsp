<%-- =========================================================
     주제: 파일 역할
     설명: travelDestination 영역의 travelDestinationDetail 화면을 구성하고 서버에서 전달받은 데이터를 표시
========================================================= --%>
<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    isELIgnored="false" %>

<!DOCTYPE html>
<html lang="ko">

<head>

<meta charset="UTF-8">

<meta name="viewport"
      content="width=device-width, initial-scale=1.0">

<title>여행지 상세정보</title>

<!-- Tailwind -->
<script src="https://cdn.tailwindcss.com"></script>

<!-- Material Icons -->
<link rel="stylesheet"
      href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined" />


<!-- =========================================================
     Kakao Map SDK

     config.properties
     kakao.api.js.key=XXXXX

              ↓

     AppConfigListener

              ↓

     applicationScope.kakaoJsKey
========================================================= -->

<script
    type="text/javascript"
    src="https://dapi.kakao.com/v2/maps/sdk.js?appkey=${applicationScope.kakaoJsKey}&libraries=services&autoload=false">
</script>

</head>


<body class="bg-[#f7f9fa] text-[#131b2e]">


<main class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-10">


    <!-- =====================================================
         목록으로 돌아가기
    ====================================================== -->

    <div class="mb-6">

        <button
            type="button"
            onclick="goBackToList()"
            class="
                inline-flex
                items-center
                gap-1
                text-sm
                text-[#3e494b]
                hover:text-[#006574]
                transition-colors
            ">

            <span class="material-symbols-outlined text-[20px]">
                arrow_back
            </span>

            여행지 목록으로

        </button>

    </div>



    <!-- =====================================================
         로딩 화면
    ====================================================== -->

    <div
        id="loadingArea"
        class="
            bg-white
            rounded-3xl
            border
            border-[#e2e5eb]
            py-24
            text-center
        ">

        <span
            class="
                material-symbols-outlined
                text-5xl
                text-[#006574]
                animate-pulse
            ">
            travel_explore
        </span>

        <p class="mt-4 text-[#3e494b]">
            여행지 정보를 불러오고 있습니다.
        </p>

    </div>



    <!-- =====================================================
         상세페이지 전체
    ====================================================== -->

    <div
        id="detailArea"
        class="hidden space-y-8">


        <!-- =================================================
             대표 이미지
        ================================================== -->

        <section
            class="
                overflow-hidden
                rounded-3xl
                bg-white
                border
                border-[#e2e5eb]
                shadow-sm
            ">


            <div
                class="
                    relative
                    w-full
                    h-[280px]
                    sm:h-[380px]
                    lg:h-[480px]
                    bg-[#eef2f3]
                ">


                <img
                    id="destinationImage"
                    src=""
                    alt="여행지 이미지"
                    class="
                        w-full
                        h-full
                        object-cover
                    ">


                <div
                    id="destinationRegion"
                    class="
                        absolute
                        bottom-5
                        left-5
                        px-4
                        py-2
                        rounded-full
                        bg-black/60
                        text-white
                        text-sm
                        backdrop-blur-sm
                    ">
                </div>

            </div>



            <!-- 제목 / 주소 -->

            <div class="p-6 sm:p-8">


                <h1
                    id="destinationTitle"
                    class="
                        text-3xl
                        sm:text-4xl
                        font-extrabold
                        tracking-tight
                        text-[#131b2e]
                    ">
                </h1>


                <div
                    class="
                        flex
                        items-start
                        gap-2
                        mt-4
                        text-[#3e494b]
                    ">

                    <span
                        class="
                            material-symbols-outlined
                            text-[#006574]
                        ">
                        location_on
                    </span>


                    <span id="destinationAddress">
                    </span>

                </div>


            </div>

        </section>



        <!-- =================================================
             여행지 소개
        ================================================== -->

        <section
            class="
                bg-white
                rounded-3xl
                border
                border-[#e2e5eb]
                p-6
                sm:p-8
                shadow-sm
            ">


            <div class="flex items-center gap-2 mb-5">

                <span
                    class="
                        w-1.5
                        h-6
                        bg-[#006574]
                        rounded-full
                    ">
                </span>


                <h2
                    class="
                        text-xl
                        sm:text-2xl
                        font-bold
                    ">
                    여행지 소개
                </h2>

            </div>


            <p
                id="destinationOverview"
                class="
                    text-[#3e494b]
                    leading-8
                    whitespace-pre-line
                ">
            </p>

        </section>



        <!-- =================================================
             기본 정보
        ================================================== -->

        <section
            class="
                bg-white
                rounded-3xl
                border
                border-[#e2e5eb]
                p-6
                sm:p-8
                shadow-sm
            ">


            <div class="flex items-center gap-2 mb-6">

                <span
                    class="
                        w-1.5
                        h-6
                        bg-[#006574]
                        rounded-full
                    ">
                </span>


                <h2
                    class="
                        text-xl
                        sm:text-2xl
                        font-bold
                    ">
                    기본 정보
                </h2>

            </div>



            <div
                class="
                    grid
                    grid-cols-1
                    md:grid-cols-2
                    gap-7
                ">


                <!-- 주소 -->

                <div class="flex gap-3">

                    <span
                        class="
                            material-symbols-outlined
                            text-[#006574]
                        ">
                        location_on
                    </span>

                    <div>

                        <p class="font-semibold">
                            주소
                        </p>

                        <p
                            id="infoAddress"
                            class="
                                mt-1
                                text-sm
                                text-[#3e494b]
                            ">
                        </p>

                    </div>

                </div>



                <!-- 전화번호 -->

                <div class="flex gap-3">

                    <span
                        class="
                            material-symbols-outlined
                            text-[#006574]
                        ">
                        call
                    </span>

                    <div>

                        <p class="font-semibold">
                            전화번호
                        </p>

                        <p
                            id="destinationTel"
                            class="
                                mt-1
                                text-sm
                                text-[#3e494b]
                            ">
                        </p>

                    </div>

                </div>



                <!-- 운영시간 -->

                <div class="flex gap-3">

                    <span
                        class="
                            material-symbols-outlined
                            text-[#006574]
                        ">
                        schedule
                    </span>

                    <div>

                        <p class="font-semibold">
                            운영시간
                        </p>

                        <p
                            id="destinationUseTime"
                            class="
                                mt-1
                                text-sm
                                text-[#3e494b]
                                leading-6
                            ">
                        </p>

                    </div>

                </div>



                <!-- 홈페이지 -->

                <div class="flex gap-3">

                    <span
                        class="
                            material-symbols-outlined
                            text-[#006574]
                        ">
                        language
                    </span>

                    <div>

                        <p class="font-semibold">
                            홈페이지
                        </p>

                        <div
                            id="destinationHomepage"
                            class="
                                mt-1
                                text-sm
                                text-[#006574]
                                break-all
                            ">
                        </div>

                    </div>

                </div>


            </div>

        </section>



        <!-- =================================================
             Kakao Map
        ================================================== -->

        <section
            class="
                bg-white
                rounded-3xl
                border
                border-[#e2e5eb]
                p-6
                sm:p-8
                shadow-sm
            ">


            <div
                class="
                    flex
                    items-center
                    justify-between
                    gap-4
                    mb-6
                ">


                <div class="flex items-center gap-2">

                    <span
                        class="
                            w-1.5
                            h-6
                            bg-[#006574]
                            rounded-full
                        ">
                    </span>


                    <h2
                        class="
                            text-xl
                            sm:text-2xl
                            font-bold
                        ">
                        위치
                    </h2>

                </div>


                <div
                    class="
                        flex
                        items-center
                        gap-1
                        text-xs
                        text-[#3e494b]
                    ">

                    <span
                        class="
                            material-symbols-outlined
                            text-[17px]
                            text-[#006574]
                        ">
                        map
                    </span>

                    Kakao Map

                </div>

            </div>



            <!-- 주소 -->

            <div
                class="
                    flex
                    items-start
                    gap-2
                    mb-4
                    text-sm
                    text-[#3e494b]
                ">

                <span
                    class="
                        material-symbols-outlined
                        text-[20px]
                        text-[#006574]
                    ">
                    location_on
                </span>


                <span id="mapAddress">
                    여행지 위치를 불러오는 중입니다.
                </span>

            </div>



            <!-- 실제 카카오맵 -->

            <div
                id="map"
                class="
                    w-full
                    h-[350px]
                    sm:h-[420px]
                    rounded-2xl
                    overflow-hidden
                    bg-[#eef2f3]
                    border
                    border-[#e2e5eb]
                ">
            </div>



            <!-- 좌표 없을 경우 -->

            <div
                id="mapError"
                class="
                    hidden
                    mt-3
                    text-sm
                    text-[#3e494b]
                ">

                해당 여행지의 위치 정보가 없습니다.

            </div>


        </section>


    </div>



    <!-- =====================================================
         오류 화면
    ====================================================== -->

    <div
        id="errorArea"
        class="
            hidden
            bg-white
            rounded-3xl
            border
            border-[#e2e5eb]
            py-24
            text-center
        ">


        <span
            class="
                material-symbols-outlined
                text-5xl
                text-red-400
            ">
            error
        </span>


        <p class="mt-4 text-[#3e494b]">

            여행지 정보를 불러오지 못했습니다.

        </p>


        <button
            type="button"
            onclick="goBackToList()"
            class="
                mt-6
                px-5
                py-2.5
                rounded-xl
                bg-[#006574]
                text-white
            ">

            목록으로 돌아가기

        </button>


    </div>


</main>



<script>

/* =====================================================
   기본 설정
===================================================== */

const contextPath =
    "${pageContext.request.contextPath}";


const contentId =
    "${contentId}";


console.log(
    "===== 여행지 상세페이지 ====="
);

console.log(
    "contentId :",
    contentId
);



/* =====================================================
   페이지 시작
===================================================== */

document.addEventListener(
    "DOMContentLoaded",
    function() {

        loadDestinationDetail();

    }
);



/* =====================================================
   TourAPI 상세정보 요청
===================================================== */

function loadDestinationDetail() {


    if (!contentId) {

        console.error(
            "contentId가 없습니다."
        );

        showError();

        return;
    }


    const url =
        contextPath
        + "/TravelDestination/detailApi"
        + "?contentId="
        + encodeURIComponent(contentId);


    console.log(
        "상세 API 요청 :",
        url
    );


    fetch(url)

        .then(function(response) {


            console.log(
                "상세 API HTTP 상태 :",
                response.status
            );


            if (!response.ok) {

                throw new Error(
                    "상세 API 요청 실패 : "
                    + response.status
                );

            }


            return response.json();

        })


        .then(function(data) {


            console.log(
                "상세 API 데이터 :",
                data
            );


            const body =
                data.response
                && data.response.body;


            if (!body
                    || !body.items
                    || !body.items.item) {

                throw new Error(
                    "상세 데이터가 없습니다."
                );

            }


            const items =
                body.items.item;


            const item =
                Array.isArray(items)
                    ? items[0]
                    : items;


            renderDestinationDetail(
                item
            );

        })


        .catch(function(error) {


            console.error(
                "상세정보 조회 실패 :",
                error
            );


            showError();

        });

}



/* =====================================================
   상세정보 출력
===================================================== */

function renderDestinationDetail(item) {


    console.log(
        "===== 상세 여행지 ====="
    );

    console.log(item);



    /* =================================================
       제목
    ================================================= */

    document.getElementById(
        "destinationTitle"
    ).textContent =
        item.title
        || "여행지 정보";



    /* =================================================
       주소
    ================================================= */

    const address =
        [
            item.addr1,
            item.addr2
        ]
        .filter(Boolean)
        .join(" ");


    const displayAddress =
        address
        || "주소 정보 없음";


    document.getElementById(
        "destinationAddress"
    ).textContent =
        displayAddress;


    document.getElementById(
        "infoAddress"
    ).textContent =
        displayAddress;


    document.getElementById(
        "mapAddress"
    ).textContent =
        displayAddress;



    /* =================================================
       대표 이미지
    ================================================= */

    const image =
        document.getElementById(
            "destinationImage"
        );


    if (item.firstimage) {

        image.src =
            item.firstimage;

    }
    else if (item.firstimage2) {

        image.src =
            item.firstimage2;

    }
    else {

        image.style.display =
            "none";

    }



    /* =================================================
       소개
    ================================================= */

    document.getElementById(
        "destinationOverview"
    ).textContent =
        removeHtml(
            item.overview
            || "등록된 여행지 소개가 없습니다."
        );



    /* =================================================
       전화번호
    ================================================= */

    document.getElementById(
        "destinationTel"
    ).textContent =
        removeHtml(
            item.tel
            || "전화번호 정보 없음"
        );



    /* =================================================
       운영시간
    ================================================= */

    const useTime =
        item.usetime
        || item.usetimeculture
        || item.opentime
        || item.openperiod
        || "";


    document.getElementById(
        "destinationUseTime"
    ).textContent =
        useTime
            ? removeHtml(useTime)
            : "운영시간 정보 없음";



    /* =================================================
       홈페이지
    ================================================= */

    const homepage =
        document.getElementById(
            "destinationHomepage"
        );


    if (item.homepage) {

        homepage.innerHTML =
            item.homepage;

    }
    else {

        homepage.textContent =
            "홈페이지 정보 없음";

    }



    /* =================================================
       지역
    ================================================= */

    const region =
        document.getElementById(
            "destinationRegion"
        );


    if (item.addr1) {

        region.textContent =
            item.addr1.split(" ")[0];

    }
    else {

        region.textContent =
            "대한민국";

    }



    /* =================================================
       중요

       먼저 상세페이지를 화면에 보여준다.

       map이 hidden 상태에서 Kakao Map을 생성하면
       지도 크기 계산이 제대로 안 될 수 있다.
    ================================================= */

    document.getElementById(
        "loadingArea"
    ).classList.add(
        "hidden"
    );


    document.getElementById(
        "detailArea"
    ).classList.remove(
        "hidden"
    );



    /* =================================================
       Kakao Map 실행

       TourAPI:
       mapx = 경도
       mapy = 위도
    ================================================= */

    initKakaoMap(
        item.mapx,
        item.mapy,
        item.title
    );

}



/* =====================================================
   Kakao Map 생성
===================================================== */

function initKakaoMap(
        mapX,
        mapY,
        title) {


    console.log(
        "===== Kakao Map 생성 ====="
    );


    console.log(
        "경도 mapx :",
        mapX
    );


    console.log(
        "위도 mapy :",
        mapY
    );



    /* =================================================
       문자열 → 숫자
    ================================================= */

    const longitude =
        parseFloat(mapX);


    const latitude =
        parseFloat(mapY);



    /* =================================================
       좌표 확인
    ================================================= */

    if (!Number.isFinite(longitude)
            || !Number.isFinite(latitude)) {


        console.error(
            "여행지 좌표가 없습니다."
        );


        document.getElementById(
            "mapError"
        ).classList.remove(
            "hidden"
        );


        return;
    }



    /* =================================================
       Kakao SDK 확인
    ================================================= */

    if (typeof kakao === "undefined"
            || !kakao.maps) {


        console.error(
            "Kakao Maps SDK가 로드되지 않았습니다."
        );


        document.getElementById(
            "mapError"
        ).textContent =
            "카카오맵을 불러오지 못했습니다.";


        document.getElementById(
            "mapError"
        ).classList.remove(
            "hidden"
        );


        return;
    }



    /* =================================================
       autoload=false이기 때문에
       kakao.maps.load 사용
    ================================================= */

    kakao.maps.load(function() {


        console.log(
            "Kakao Maps SDK 로딩 완료"
        );



        /* ---------------------------------------------
           지도 DIV
        --------------------------------------------- */

        const mapContainer =
            document.getElementById(
                "map"
            );



        /* ---------------------------------------------
           TourAPI 좌표 → Kakao LatLng

           주의

           LatLng(
               위도,
               경도
           )

           즉

           mapy 먼저
           mapx 두번째
        --------------------------------------------- */

        const position =
            new kakao.maps.LatLng(
                latitude,
                longitude
            );



        /* ---------------------------------------------
           지도 옵션
        --------------------------------------------- */

        const mapOption = {

            center:
                position,

            level:
                3
        };



        /* ---------------------------------------------
           지도 생성
        --------------------------------------------- */

        const map =
            new kakao.maps.Map(
                mapContainer,
                mapOption
            );



        /* =================================================
           지도 / 스카이뷰 컨트롤
        ================================================= */

        const mapTypeControl =
            new kakao.maps.MapTypeControl();


        map.addControl(
            mapTypeControl,
            kakao.maps.ControlPosition.TOPRIGHT
        );



        /* =================================================
           확대 / 축소 컨트롤
        ================================================= */

        const zoomControl =
            new kakao.maps.ZoomControl();


        map.addControl(
            zoomControl,
            kakao.maps.ControlPosition.RIGHT
        );



        /* =================================================
           여행지 위치 마커
        ================================================= */

        const marker =
            new kakao.maps.Marker({

                position:
                    position

            });


        marker.setMap(
            map
        );



        /* =================================================
           마커 위 여행지 이름
        ================================================= */

        const infoContent =
            document.createElement(
                "div"
            );


        infoContent.style.padding =
            "8px 12px";


        infoContent.style.fontSize =
            "13px";


        infoContent.style.fontWeight =
            "600";


        infoContent.style.whiteSpace =
            "nowrap";


        infoContent.textContent =
            title
            || "여행지";



        const infoWindow =
            new kakao.maps.InfoWindow({

                content:
                    infoContent

            });



        /* =================================================
           처음부터 여행지 이름 표시
        ================================================= */

        infoWindow.open(
            map,
            marker
        );



        /* =================================================
           마커 클릭하면 정보창 다시 표시
        ================================================= */

        kakao.maps.event.addListener(
            marker,
            "click",
            function() {

                infoWindow.open(
                    map,
                    marker
                );

            }
        );



        /* =================================================
           지도 크기 다시 계산

           detailArea가 처음에 hidden이었기 때문에
           안전하게 relayout
        ================================================= */

        setTimeout(
            function() {

                map.relayout();

                map.setCenter(
                    position
                );

            },
            100
        );


        console.log(
            "Kakao Map 생성 완료"
        );

    });

}



/* =====================================================
   오류 화면
===================================================== */

function showError() {


    document.getElementById(
        "loadingArea"
    ).classList.add(
        "hidden"
    );


    document.getElementById(
        "detailArea"
    ).classList.add(
        "hidden"
    );


    document.getElementById(
        "errorArea"
    ).classList.remove(
        "hidden"
    );

}



/* =====================================================
   목록으로 이동
===================================================== */

function goBackToList() {


    window.location.href =
        contextPath
        + "/TravelDestination/list";

}



/* =====================================================
   HTML 태그 제거
===================================================== */

function removeHtml(value) {


    const temp =
        document.createElement(
            "div"
        );


    temp.innerHTML =
        value;


    return temp.textContent
        || temp.innerText
        || "";

}

</script>


</body>
</html>