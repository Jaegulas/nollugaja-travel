<%-- =========================================================
     주제: 파일 역할
     설명: liveChat 영역의 chat 화면을 구성하고 서버에서 전달받은 데이터를 표시
========================================================= --%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- 회원 실시간 채팅: /WEB-INF/liveChat/chat.jsp -->



<link rel="stylesheet" href="<%= request.getContextPath() %>/resources/css/livechat/chat.css?v=20261020-1">
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;600;700;800&family=Noto+Sans+KR:wght@400;500;600;700;800&display=swap" rel="stylesheet">
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght@100..700&display=swap" rel="stylesheet">

<div class="noleo-chatbot-root" id="noleoChatbotContainer"
     data-context-path="<%= request.getContextPath() %>">
  <div class="noleo-chat-window" id="noleoChatWindow" role="dialog" aria-labelledby="noleoChatTitle">
    <div class="noleo-chat-header">
      <div class="noleo-chat-header-left">
        <div class="noleo-bot-avatar">
          <img src="<%= request.getContextPath() %>/resources/images/liveChat/live-chat.png"
               alt="놀러가자" style="width:100%;height:100%;object-fit:contain;">
        </div>
        <div>
          <h2 class="noleo-chat-title" id="noleoChatTitle">회원 실시간 채팅 <span class="noleo-live-badge">LIVE</span></h2>
          <p class="noleo-chat-subtitle" id="noleoChatStatus">채팅창을 열어 연결하세요.</p>
        </div>
      </div>
      <div class="noleo-header-buttons">
        <button class="noleo-icon-button" id="noleoChatCloseBtn" type="button" aria-label="채팅창 닫기">
          <span class="material-symbols-outlined">close</span>
        </button>
      </div>
    </div>
    <div class="noleo-chat-messages" id="noleoChatMessages" tabindex="0" aria-live="polite">
      <div class="noleo-system-note">로그인한 회원끼리 대화할 수 있습니다.</div>
    </div>
    <div class="noleo-chat-footer">
      <form class="noleo-chat-form" id="noleoChatForm">
        <div class="noleo-input-wrap">
          <input class="noleo-chat-input" id="noleoChatInput" type="text" maxlength="500"
                 placeholder="연결 중..." autocomplete="off" disabled>
        </div>
        <button class="noleo-send-button" id="noleoChatSendBtn" type="submit" disabled aria-label="메시지 전송">
          <span class="material-symbols-outlined">send</span>
        </button>
      </form>
      <div class="noleo-footer-info"><span>놀러가자 회원 채팅</span><span class="noleo-online" id="noleoChatConnectionLabel">연결 대기</span></div>
    </div>
  </div>
  <!-- Floating banner and its original image kept intact. -->
  <div class="noleo-floating-area">
    <div class="noleo-tooltip" id="noleoChatTooltip">
      <span>🧭 실시간 채팅 </span><span class="noleo-tooltip-badge">질문하기</span>
    </div>
    <button class="noleo-chat-trigger" id="noleoChatToggleBtn" type="button" aria-label="실시간 채팅 열기">
      <img id="noleoChatBtnImage" src="<%= request.getContextPath() %>/resources/images/liveChat/live-chat.png"
           alt="실시간 채팅" class="noleo-chatbot-image" />
      <span id="noleoChatBtnIcon" style="display:none;"></span><span class="noleo-online-ping"></span>
    </button>
  </div>
</div>

<script defer src="<%= request.getContextPath() %>/resources/js/livechat/chat.js?v=20261020-1"></script>


