<%-- =========================================================
     주제: 파일 역할
     설명: common 영역의 login-return-script 화면을 구성하고 서버에서 전달받은 데이터를 표시
========================================================= --%>
<%-- =========================================================
     주제: 공통 로그인 복귀
     설명: 로그인하기 전 현재 페이지를 저장해 로그인 성공 후 다시 돌아옴
========================================================= --%>
<script>
(function(){
    const contextPath="${pageContext.request.contextPath}";

    //=========================================================
    //주제: 공통 로그인 이동
    //설명: 현재 주소를 returnTo로 전달하여 로그인 후 원래 페이지로 복귀
    //=========================================================
    window.moveToLogin=function(){
        const returnTo=location.pathname+location.search;
        location.href=contextPath+"/Login?returnTo="+encodeURIComponent(returnTo);
    };

    //=========================================================
    //주제: 기존 로그인 링크 공통 처리
    //설명: /Login 링크에 복귀 주소가 없으면 현재 페이지를 자동으로 추가
    //=========================================================
    document.addEventListener("click",function(event){
        const link=event.target.closest("a[href]");
        if(!link)return;

        try{
            const url=new URL(link.href,location.href);
            const loginPath=contextPath+"/Login";

            if(url.origin===location.origin&&url.pathname===loginPath&&
               !url.searchParams.has("returnTo")&&!url.searchParams.has("redirect")){
                event.preventDefault();
                window.moveToLogin();
            }
        }catch(e){
            console.error("로그인 링크 처리 오류",e);
        }
    },true);
})();
</script>
