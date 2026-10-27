/** 공통 JSP 전체 번역 - /translate Servlet: {texts,targetLanguage} -> string[] */
(function () {
  'use strict';
  if (window.__noleoTranslateLoaded) { console.warn('[translate] 중복 로드'); return; }
  window.__noleoTranslateLoaded = true;
  const originals = new WeakMap();
  const rendered = new WeakMap();
  const excluded = 'script,style,noscript,textarea,code,pre,svg,option,[contenteditable="true"],[data-no-translate],.notranslate';
  const names = {ko:'한국어',en:'English',ja:'日本語'};
  const ctx = (window.TRANSLATE_CONTEXT_PATH || (location.pathname.split('/')[1] ? '/' + location.pathname.split('/')[1] : '')).replace(/\/$/, '');
  const endpoint = ctx + '/translate';
  let language = 'ko', version = 0, controller = null, timer = null, translating = false;
  console.info('[translate] 스크립트 로드됨. API:', endpoint);

  function collect() {
    if (!document.body) return [];
    const found = [];
    const walker = document.createTreeWalker(document.body, NodeFilter.SHOW_TEXT, {
      acceptNode(node) {
        const parent = node.parentElement;
        if (!parent || parent.closest(excluded) || parent.closest('#languageDropdown,#languageToggle') || !node.nodeValue.trim()) return NodeFilter.FILTER_REJECT;
        if (!originals.has(node)) originals.set(node, node.nodeValue);
        found.push(node);
        return NodeFilter.FILTER_ACCEPT;
      }
    });
    while (walker.nextNode()) {}
    return found;
  }
  function setBusy(value) {
    // 언어 전환은 진행 중에도 가능해야 하므로 버튼을 비활성화하지 않습니다.
    // 이전 요청은 apply()에서 AbortController로 취소합니다.
  }
  function setLabel(lang) {
    const label=document.getElementById('currentLanguage');
    if (label) label.textContent=names[lang];
    ['koBtn','enBtn','jaBtn'].forEach(id => {
      const el=document.getElementById(id);
      if(el) el.classList.toggle('selected', id === lang+'Btn');
    });
  }
  async function apply(lang, onlyNew) {
    language=lang;
    const ticket=++version;
    if (controller) controller.abort();
    controller=new AbortController();
    const signal=controller.signal;
    const all=collect();
    if (lang==='ko') {
      all.forEach(n => { const original=originals.get(n); if(original!==undefined) n.nodeValue=original; rendered.delete(n); });
      console.info('[translate] 한국어 원문 복원:', all.length, '개');
      setBusy(false); setLabel(lang); return;
    }
    const nodes=onlyNew ? all.filter(n => rendered.get(n)!==lang) : all;
    console.info('[translate] 번역 시작:', lang, '대상:', nodes.length, 'API:', endpoint);
    if(!nodes.length) { setLabel(lang); console.info('[translate] 번역할 텍스트 없음'); return; }
    translating=true; setBusy(true);
    try {
      for(let i=0;i<nodes.length;i+=40) {
        const group=nodes.slice(i,i+40).filter(n=>n.isConnected);
        if(!group.length) continue;
        const texts=group.map(n=>originals.get(n).trim());
        const res=await fetch(endpoint,{method:'POST',credentials:'same-origin',signal,
          headers:{'Content-Type':'application/json; charset=UTF-8'},body:JSON.stringify({texts,targetLanguage:lang})});
        if(!res.ok) throw new Error('HTTP '+res.status+' / '+endpoint);
        const translated=await res.json();
        if(!Array.isArray(translated)||translated.length!==group.length||translated.some(x=>typeof x!=='string')) throw new Error('Servlet 응답은 문자열 배열이어야 합니다.');
        if(ticket!==version) return;
        group.forEach((n,j)=>{
          const source=originals.get(n);
          n.nodeValue=(source.match(/^\s*/)||[''])[0]+translated[j]+(source.match(/\s*$/)||[''])[0];
          rendered.set(n,lang);
        });
        console.info('[translate] 진행:', Math.min(i+40,nodes.length),'/',nodes.length);
      }
      if(ticket===version) console.info('[translate] 번역 완료:',lang);
    } catch(e) {
      if(e.name!=='AbortError') console.error('[translate] 번역 실패:',e);
    } finally {
      if(ticket===version) { translating=false; setBusy(false); setLabel(lang); }
    }
  }
  function init() {
    console.info('[translate] 초기화. 버튼:', ['koBtn','enBtn','jaBtn'].map(id=>!!document.getElementById(id)));
    Object.entries({koBtn:'ko',enBtn:'en',jaBtn:'ja'}).forEach(([id,lang])=>{
      const btn=document.getElementById(id);
      if(!btn||btn.dataset.translateReady) return;
      btn.dataset.translateReady='1';
      btn.addEventListener('click',()=>{
        console.info('[translate] 언어 버튼 클릭:',lang);
        try { sessionStorage.setItem('noleoLanguage',lang); } catch(e) {}
        apply(lang,false);
        const dropdown=document.getElementById('languageDropdown');
        if(dropdown) dropdown.classList.remove('active');
        const toggle=document.getElementById('languageToggle');
        if(toggle) toggle.setAttribute('aria-expanded','false');
      });
    });
    // top.jsp 언어 메뉴 열기/닫기 (기존 코드에서 누락된 이벤트)
    const toggle = document.getElementById('languageToggle');
    const dropdown = document.getElementById('languageDropdown');
    if (toggle && dropdown) {
      toggle.addEventListener('click', (event) => {
        event.stopPropagation();
        const open = dropdown.classList.toggle('active');
        toggle.setAttribute('aria-expanded', String(open));
        console.info('[translate] 언어 메뉴:', open ? '열림' : '닫힘');
      });
      document.addEventListener('click', (event) => {
        if (!toggle.contains(event.target) && !dropdown.contains(event.target)) {
          dropdown.classList.remove('active');
          toggle.setAttribute('aria-expanded', 'false');
        }
      });
      document.addEventListener('keydown', (event) => {
        if (event.key === 'Escape') {
          dropdown.classList.remove('active');
          toggle.setAttribute('aria-expanded', 'false');
        }
      });
    } else {
      console.warn('[translate] 언어 메뉴 요소가 없습니다:', {toggle: !!toggle, dropdown: !!dropdown});
    }
    const observer=new MutationObserver(records=>{
      if(language==='ko'||translating) return;
      if(!records.some(r=>r.type==='childList'&&r.addedNodes.length)) return;
      clearTimeout(timer);
      timer=setTimeout(()=>apply(language,true),350);
    });
    observer.observe(document.body,{childList:true,subtree:true});
    let saved='ko';
    try { saved=sessionStorage.getItem('noleoLanguage')||'ko'; } catch(e) {}
    if(names[saved] && saved!=='ko') apply(saved,false);
    else setLabel('ko');
  }
  if(document.readyState==='loading') document.addEventListener('DOMContentLoaded',init);
  else init();
})();