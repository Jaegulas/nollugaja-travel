<%-- =========================================================
     주제: 파일 역할
     설명: mypage 영역의 shareItinerary 화면을 구성하고 서버에서 전달받은 데이터를 표시
========================================================= --%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isELIgnored="false" %>

    

<div class="share-wrap">

  <div class="share-top">
    <a href="${pageContext.request.contextPath}/">← 사이트 둘러보기</a>
    <button type="button" class="share-btn" onclick="sharePdf()">PDF 저장</button>
  </div>

  <!-- mypage-plan.js 의 renderPlan() 이 이 안을 그립니다 -->
  <section class="plan-section" id="plan" style="display:none">
    <div id="aiSummaryBox" class="plan-box"></div>
  </section>

</div>
    
    
    
<!-- SDK 먼저, 그 다음 mypage-plan.js -->

<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="https://t1.kakaocdn.net/kakao_js_sdk/2.7.2/kakao.min.js"></script>
<script src="https://dapi.kakao.com/v2/maps/sdk.js?appkey=${applicationScope.kakaoJsKey}&libraries=services&autoload=false"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/html2pdf.js/0.10.1/html2pdf.bundle.min.js"></script>
<script>
  // main.jsp 의 <body> 에 값 넣어주기 (mypage_plan.js 가 여기서 읽어감)
  document.body.setAttribute('data-ctx', '${pageContext.request.contextPath}');
  document.body.setAttribute('data-kakao-key', '234376aaa52f550d6e16b505ad3621b9');
</script>
<script src="${pageContext.request.contextPath}/resources/js/mypage/mypage_plan.js"></script>

<script>


  // 서블릿이 넣어준 일정 JSON 을 그대로 그리기
  var sharedPlan = ${planjson};
  renderPlan(sharedPlan);
  
  
//카카오톡 안의 브라우저인지 확인
  var isKakaoInApp = navigator.userAgent.indexOf('KAKAOTALK') > -1;

  // PDF 버튼: 카톡 안이면 바깥 브라우저로 열기
  function sharePdf() {
    if (isKakaoInApp) {
      alert('카카오톡에서는 PDF 저장이 안 돼요.\n바깥 브라우저로 열어드릴게요.');
      location.href = 'kakaotalk://web/openExternal?url=' + encodeURIComponent(location.href);
      return;
    }
    downloadPDF();
  }
</script>
