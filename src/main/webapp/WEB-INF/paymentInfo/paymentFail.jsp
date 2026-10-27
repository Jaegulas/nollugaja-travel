<%-- =========================================================
     주제: 파일 역할
     설명: paymentInfo 영역의 paymentFail 화면을 구성하고 서버에서 전달받은 데이터를 표시
========================================================= --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/payment/paymentInfo.css" />
<main class="pi-page"><section class="pi-card" style="max-width:720px;margin:32px auto;text-align:center">
<h1>결제 인증이 완료되지 않았습니다.</h1>
<p>결제가 취소되었거나 실패했습니다. 테스트 주문은 결제 완료 상태로 변경되지 않았습니다.</p>
<a class="pi-edit" href="${pageContext.request.contextPath}/PaymentInfo">결제 다시 시도하기</a>
</section></main>
