// =========================================================
// 주제: 파일 역할
// 설명: Q&A 화면의 등록·수정·삭제 및 인터랙션을 처리
// =========================================================

/*=========================================================
주제: Q&A 게시판 화면 동작
설명: 필터/펼치기/모달/조회수 처리, DB 저장은 QnaController 사용
=========================================================*/
(function(){
 const page=document.getElementById('qna-page');if(!page)return;
 const form=document.getElementById('qna-filter-form'),modal=document.getElementById('qna-write-modal');
 const writeForm=document.getElementById('qna-write-form'),btnOpen=document.getElementById('qna-open-write');
 const toast=document.getElementById('qna-toast');let toastTimer;
 const csrf=writeForm.querySelector('[name="csrf"]').value;
 // 알림 (비로그인 상태도 안내)
 // =========================================================
 // 주제: notify 처리
 // 설명: notify 기능에 필요한 처리 로직을 수행
 // =========================================================
 function notify(msg){toast.textContent=msg;toast.classList.add('show');clearTimeout(toastTimer);toastTimer=setTimeout(()=>toast.classList.remove('show'),3300);}
 function openModal(isEdit){
  if(page.dataset.loggedIn!=='true'){notify('로그인 후 질문을 작성할 수 있어요. 상단 로그인 메뉴를 이용해주세요.');return;}
  if(!isEdit){writeForm.reset();document.getElementById('qna-write-action').value='ask';document.getElementById('qna-edit-id').value='';document.getElementById('qna-modal-title').textContent='질문 작성하기';writeForm.querySelector('.qna-submit').textContent='질문 등록하기 →';}
  modal.classList.add('show');modal.setAttribute('aria-hidden','false');document.body.classList.add('qna-modal-open');document.getElementById('qna-write-title').focus();
 }
 // =========================================================
 // 주제: closeModal 처리
 // 설명: closeModal 기능에 필요한 처리 로직을 수행
 // =========================================================
 function closeModal(){modal.classList.remove('show');modal.setAttribute('aria-hidden','true');document.body.classList.remove('qna-modal-open');}
 btnOpen.addEventListener('click',()=>openModal(false));
 document.querySelectorAll('.qna-empty-write').forEach(b=>b.addEventListener('click',()=>openModal(false)));
 ['qna-modal-close','qna-modal-cancel'].forEach(id=>document.getElementById(id).addEventListener('click',closeModal));
 modal.addEventListener('click',e=>{if(e.target===modal)closeModal();});
 document.addEventListener('keydown',e=>{if(e.key==='Escape')closeModal();});
 // 분류/답변상태/정렬/페이지 이동: 현재 검색 조건 유지
 document.querySelectorAll('[data-qna-category]').forEach(b=>b.addEventListener('click',()=>{document.getElementById('qna-hidden-category').value=b.dataset.qnaCategory;form.submit();}));
 document.querySelectorAll('[data-qna-status]').forEach(b=>b.addEventListener('click',()=>{document.getElementById('qna-hidden-status').value=b.dataset.qnaStatus;form.submit();}));
 document.getElementById('qna-sort').addEventListener('change',()=>form.submit());
 document.querySelectorAll('[data-qna-page]').forEach(b=>b.addEventListener('click',()=>{if(b.disabled)return;const params=new URLSearchParams(new FormData(form));params.set('page',b.dataset.qnaPage);window.location.href=form.action+'?'+params.toString();}));
 // 질문 펼치기: 답변은 이미 JSP로 렌더링된 상태 (외부 API 호출 없음)
 document.querySelectorAll('.qna-item').forEach(item=>{
  const button=item.querySelector('.qna-item-main');
  // =========================================================
  // 주제: expand 처리
  // 설명: expand 기능에 필요한 처리 로직을 수행
  // =========================================================
  function expand(){
   const wasOpen=item.classList.contains('is-open');
   document.querySelectorAll('.qna-item.is-open').forEach(x=>{x.classList.remove('is-open');x.querySelector('.qna-item-main').setAttribute('aria-expanded','false');});
   if(wasOpen)return;
   item.classList.add('is-open');button.setAttribute('aria-expanded','true');
   if(!item.dataset.viewed){item.dataset.viewed='1';
    fetch(window.location.pathname,{method:'POST',credentials:'same-origin',headers:{'Content-Type':'application/x-www-form-urlencoded;charset=UTF-8'},body:new URLSearchParams({action:'view',csrf:csrf,id:item.dataset.questionId})}).catch(()=>{});
   }
  }
  button.addEventListener('click',expand);
  // 처음 페이지 진입 후 ?open=ID이면 해당 카드 열기
  if(page.dataset.open===item.dataset.questionId){expand();setTimeout(()=>item.scrollIntoView({block:'start'}),100);}
  const edit=item.querySelector('.qna-edit-btn');
  if(edit)edit.addEventListener('click',()=>{
   openModal(true);document.getElementById('qna-modal-title').textContent='질문 수정하기';
   document.getElementById('qna-write-action').value='edit';document.getElementById('qna-edit-id').value=edit.dataset.id;
   document.getElementById('qna-write-title').value=edit.dataset.title;
   document.getElementById('qna-write-category').value=edit.dataset.category;
   document.getElementById('qna-write-content').value=item.querySelector('.qna-question-body .qna-long-text').textContent;
   writeForm.querySelector('.qna-submit').textContent='수정 완료 →';
  });
 });
 // 본인 글 삭제 시 확인
 document.querySelectorAll('.qna-delete-form').forEach(f=>f.addEventListener('submit',e=>{if(!window.confirm('정말 삭제할까요? 삭제하면 되돌릴 수 없습니다.'))e.preventDefault();}));
})();
