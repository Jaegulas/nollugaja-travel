// =========================================================
// 주제: 파일 역할
// 설명: chat 화면의 사용자 인터랙션과 비동기 요청을 처리
// =========================================================

(function () {
  'use strict';
  function init() {
    const root = document.getElementById('noleoChatbotContainer');
    if (!root || root.dataset.initialized === 'true') return;
    root.dataset.initialized = 'true';
    const win = document.getElementById('noleoChatWindow');
    const toggle = document.getElementById('noleoChatToggleBtn');
    const tooltip = document.getElementById('noleoChatTooltip');
    const close = document.getElementById('noleoChatCloseBtn');
    const status = document.getElementById('noleoChatStatus');
    const connection = document.getElementById('noleoChatConnectionLabel');
    const messages = document.getElementById('noleoChatMessages');
    const input = document.getElementById('noleoChatInput');
    const send = document.getElementById('noleoChatSendBtn');
    const form = document.getElementById('noleoChatForm');
    let socket = null;
    const protocol = location.protocol === 'https:' ? 'wss://' : 'ws://';
    const url = protocol + location.host + root.dataset.contextPath + '/member-chat';

    // =========================================================
    // 주제: note 처리
    // 설명: note 기능에 필요한 처리 로직을 수행
    // =========================================================
    function note(message) {
      const node = document.createElement('div');
      node.className = 'noleo-system-note';
      node.textContent = message;
      messages.appendChild(node);
      messages.scrollTop = messages.scrollHeight;
    }
    // =========================================================
    // 주제: enabled 처리
    // 설명: enabled 기능에 필요한 처리 로직을 수행
    // =========================================================
    function enabled(yes) {
      input.disabled = !yes; send.disabled = !yes;
      input.placeholder = yes ? '메시지를 입력하세요 (최대 500자)' : '로그인 후 이용할 수 있습니다';
    }
    function render(data) {
      if (data.type === 'ready') { root.dataset.memberId = data.memberId || ''; status.textContent = '회원 채팅 연결됨'; connection.textContent = '● 온라인'; enabled(true); return; }
      if (data.type === 'error') { note(data.message || '오류가 발생했습니다.'); return; }
      if (data.type !== 'message' && data.type !== 'history') return;
      const mine = data.memberId && data.memberId === root.dataset.memberId;
      const row = document.createElement('div');
      row.className = 'noleo-message' + (mine ? ' user' : '');
      const content = document.createElement('div');
      content.className = 'noleo-message-content';
      const name = document.createElement('div');
      name.className = 'noleo-message-name';
      name.textContent = data.memberName || '회원';
      const bubble = document.createElement('div');
      bubble.className = 'noleo-message-bubble';
      bubble.textContent = data.message || ''; // textContent: never inject user HTML
      const time = document.createElement('span');
      time.className = 'noleo-message-time';
      time.textContent = data.time ? String(data.time).replace('T', ' ').slice(0,16) : '';
      content.append(name,bubble,time); row.appendChild(content);
      messages.appendChild(row); messages.scrollTop = messages.scrollHeight;
    }
    // =========================================================
    // 주제: connect 처리
    // 설명: connect 기능에 필요한 처리 로직을 수행
    // =========================================================
    function connect() {
      if (socket && (socket.readyState === WebSocket.OPEN || socket.readyState === WebSocket.CONNECTING)) return;
      status.textContent = '연결 중...';
      connection.textContent = '연결 중';
      enabled(false);
      socket = new WebSocket(url);
      socket.onmessage = function (event) {
        try { render(JSON.parse(event.data)); } catch (e) { note('메시지 처리 오류'); }
      };
      socket.onclose = function (event) {
        enabled(false);
        connection.textContent = '연결 종료';
        status.textContent = event.code === 1008 ? '로그인 상태를 확인해 주세요.' : '연결이 종료되었습니다.';
        socket = null;
      };
      socket.onerror = function () { status.textContent = '채팅 서버 연결 오류'; };
    }
    // =========================================================
    // 주제: open 처리
    // 설명: open 기능에 필요한 처리 로직을 수행
    // =========================================================
    function open() { win.classList.add('open'); connect(); }
    function hide() { win.classList.remove('open'); if (socket) { socket.close(); socket=null; } enabled(false); }
    toggle.addEventListener('click', function () { win.classList.contains('open') ? hide() : open(); });
    tooltip.addEventListener('click', open);
    close.addEventListener('click', hide);
    form.addEventListener('submit', function (e) {
      e.preventDefault();
      const text = input.value.trim();
      if (!text || !socket || socket.readyState !== WebSocket.OPEN) return;
      socket.send(text); input.value = ''; input.focus();
    });
  }
  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', init);
  else init();
})();
