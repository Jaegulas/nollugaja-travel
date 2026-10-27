// =========================================================

// 주제: 파일 역할

// 설명: 마이페이지 찜 목록을 조회하고 화면에 렌더링

// =========================================================



/*=========================================================

주제: 마이페이지 찜 목록 통합 (mypage_like.js)

설명: 숙소·문화유산 기존 API + 음식·관광지 MyFavorites API

핵심: 서버 응답 {success:true,list:[...]} 처리 / 네이버 지도 새 탭

=========================================================*/

var MYPAGE_LIKE_VERSION='20261021-accommodation-ready-1';

var LIKE_TYPE_NAME={acc:'숙소',food:'음식',heritage:'문화유산',place:'여행지'};

var likeList=[],likeFilter='all',likeBusy=false,likeReloadRequested=false;

console.info('[마이페이지 찜 JS 로드]',MYPAGE_LIKE_VERSION);

//=========================================================

//주제: 공통 보조 함수

//설명: ContextPath, 안전한 HTML, 네이버 지도 검색 주소

//=========================================================

function likeCtx(){

 if(typeof CTX==='string'&&CTX)return CTX.replace(/\/$/,'');

 var fromBody=document.body&&document.body.dataset?document.body.dataset.ctx:'';

 if(fromBody)return fromBody.replace(/\/$/,'');

 return location.pathname.split('/').slice(0,2).join('/');

}

// =========================================================

// 주제: likeEsc 처리

// 설명: 찜 상태 조회 또는 추가/해제를 처리

// =========================================================

function likeEsc(value){return String(value==null?'':value).replace(/[&<>"']/g,function(c){return {'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c];});}

function naverMapSearch(name,address){

 var query=[name,address].filter(function(s){return s&&String(s).trim();}).join(' ').trim();

 return query?'https://map.naver.com/p/search/'+encodeURIComponent(query):'#';

}

function likeCardUrl(item){

 if(item.type==='food'||item.type==='place'||item.tourLike)return naverMapSearch(item.name,item.addr);

 var url=String(item.url||'').trim();

 if(/^https:\/\//i.test(url))return url;

 if(url.startsWith('/'))return likeCtx()+url;

 return '#';

}

// =========================================================

// 주제: likeDate 처리

// 설명: 찜 상태 조회 또는 추가/해제를 처리

// =========================================================

function likeDate(date){var d=String(date||'');return /^\d{4}-\d{2}-\d{2}/.test(d)?d.slice(0,10):'';}

function halfYearName(date){var d=likeDate(date);return d?d.slice(0,4)+'년 '+(Number(d.slice(5,7))<=6?'상반기':'하반기'):'날짜 정보 없음';}

function shortDate(date){var d=likeDate(date);return d?d.slice(5,7)+'.'+d.slice(8,10):'날짜 미상';}

function likeImageFallback(item){

 // =========================================================

 // 주제: 숙소 이미지 대체 처리

 // 설명: 숙소 사진이 없거나 로드 실패 시 숙박 전용 대표 SVG를 사용

 // =========================================================

 if(item.type==='acc')return likeCtx()+'/resources/images/accommodation/mypage-accommodation-ready.svg';

 var base=likeCtx()+'/resources/images/food/';

 if(item.type==='place')return base+'fallback_tour.png';

 if(item.type!=='food')return base+'no-image.png';

 var title=(item.name||'').toLowerCase();

 if(/밀면|냉면|국수|우동|라멘|면|noodle/.test(title))return base+'fallback_korean.png';

 if(/카페|커피|디저트|베이커리|케이크/.test(title))return base+'fallback_cafe.png';

 return base+'fallback_korean.png';

}

// =========================================================

// 주제: likeImageSource 처리

// 설명: 찜 상태 조회 또는 추가/해제를 처리

// =========================================================

function likeImageSource(item){

 var src=String(item.image||'').trim();if(!src)return likeImageFallback(item);

 if(/^https?:\/\//i.test(src))return src;

 if(src.startsWith(likeCtx()+'/'))return src;

 if(src.startsWith('/resources/'))return likeCtx()+src;

 return likeImageFallback(item);

}

//=========================================================

//주제: DB 응답 형식 정리

//설명: 기존 API는 {success:true,list:[...]}, 새 API는 {foods,tours}

//=========================================================

// 응답에 list가 배열로 오거나 JSON 문자열로 와도 동일하게 처리합니다.

// 정상 컨트롤러는 배열을 보내지만, 이전 배포본이 문자열을 반환해도 목록이 사라지지 않게 합니다.

function likeParseJson(value, source){

 if(typeof value!=='string')return value;

 try{return JSON.parse(value);}catch(error){

  console.warn('[찜 응답 JSON 해석 실패]',source,error);

  return null;

 }

}

// =========================================================

// 주제: normalizeLegacy 처리

// 설명: 입력 데이터를 화면/API/DB에서 사용하기 좋은 형태로 변환 또는 가공

// =========================================================

function normalizeLegacy(response){

 var data=likeParseJson(response,'전체 응답');

 var list=Array.isArray(data)?data:(data&&data.list);

 var rows=likeParseJson(list,'list');

 if(!Array.isArray(rows)){

  if(rows!=null)console.warn('[찜 목록 형식 불일치] list 배열 필요, 실제 형식:',typeof rows);

  return [];

 }

 return rows.filter(function(x){return x&&['acc','food','heritage','place'].includes(x.type);}).map(function(x){

  return {type:x.type,id:String(x.id==null?'':x.id),name:x.name||'',addr:x.addr||x.address||'',image:x.image||'',likedAt:likeDate(x.likedAt||x.regdate),url:x.url||'',tourLike:false,source:'legacy'};

 }).filter(function(x){return x.id!=='';});

}

// =========================================================

// 주제: normalizeFavorites 처리

// 설명: 입력 데이터를 화면/API/DB에서 사용하기 좋은 형태로 변환 또는 가공

// =========================================================

function normalizeFavorites(data){

 var foods=Array.isArray(data.foods)?data.foods:[],tours=Array.isArray(data.tours)?data.tours:[];

 var result=foods.filter(function(f){return f&&f.foodId!=null;}).map(function(f){

  return {type:'food',id:String(f.foodId),name:f.name||f.f_name||'',addr:f.address||'',image:f.image||'',likedAt:likeDate(f.likedAt||f.regdate),url:'',tourLike:false,source:'myfavorites'};

 });

 tours.forEach(function(t){

  if(!t)return;

  var id=String(t.contentId||t.content_id||'').trim();if(!id)return;

  result.push({type:'place',id:id,name:t.name||t.tour_name||'',addr:t.address||'',image:t.image||t.image_url||'',likedAt:likeDate(t.likedAt||t.regdate),url:'',tourLike:true,source:'myfavorites'});

 });

 return result;

}

//=========================================================

//주제: 마이페이지 찜 API 조회

//설명: 어느 한 API 오류가 나도 다른 찜은 표시; 중복은 출처별 정리

//=========================================================

async function loadLike(){

 var box=document.getElementById('like-area'),countEl=document.getElementById('like-count');if(!box)return;

 if(likeBusy){likeReloadRequested=true;return;}

 likeBusy=true;box.innerHTML='<p class="empty">찜 목록을 불러오는 중...</p>';

 try{

  var paths=[likeCtx()+'/Mypage/mypageLikeListPerMember',likeCtx()+'/MyFavorites',likeCtx()+'/ExtraLikes'];

  var res=await Promise.allSettled(paths.map(function(url){return fetch(url,{credentials:'same-origin',cache:'no-store'}).then(async function(r){

   if(!r.ok)throw new Error(url+' HTTP '+r.status);

   var data=await r.json();if(data&&data.success===false)throw new Error(data.message||'조회 실패');

   return data;

  });}));

  var legacy=[],modern=[],extra=[],okCount=0;

  if(res[0].status==='fulfilled'){legacy=normalizeLegacy(res[0].value);okCount++;}

  else console.warn('[기존 숙소·문화유산 찜 조회 실패]',res[0].reason);

  if(res[1].status==='fulfilled'){

   modern=normalizeFavorites(res[1].value);okCount++;

   console.info('[MyFavorites 원본 개수] 음식:',(res[1].value.foods||[]).length,'관광지:',(res[1].value.tours||[]).length);

  }else console.warn('[음식·관광지 찜 조회 실패]',res[1].reason);

  if(res[2].status==='fulfilled'){

   extra=normalizeLegacy(res[2].value).filter(function(x){return x.type==='acc'||x.type==='heritage';});okCount++;

   var extraBody=res[2].value;

   console.info('[ExtraLikes 조회 결과]',{

    list형식:extraBody&&Array.isArray(extraBody.list)?'배열':typeof (extraBody&&extraBody.list),

    숙소:extra.filter(function(x){return x.type==='acc';}).length,

    문화유산:extra.filter(function(x){return x.type==='heritage';}).length

   });

  }

  else console.warn('[숙소·문화유산 찜 직접 조회 실패]',res[2].reason);

  if(okCount===0)throw new Error('찜 조회 API가 모두 실패했습니다.');

  // 음식은 새 MyFavorites 목록 우선, 숙소·문화유산·팀 여행지는 기존 API 유지

  var merged=[],keys=new Set();

  // acc/heritage는 ExtraLikes(올바른 로그인 ID + cultural_heritage 조회)를 우선 사용

  // 이전 API는 팀 여행지(place)를 유지하고 ExtraLikes 장애 시에만 acc/heritage 대체

  var legacyFallback=legacy.filter(function(x){return x.type==='place'||(res[2].status!=='fulfilled'&&(x.type==='acc'||x.type==='heritage'));});

  modern.concat(extra,legacyFallback).forEach(function(item){

   var key=item.type+':'+item.id;

   // 관광지 contentId와 팀 여행지 DB id는 서로 다른 번호 체계이므로 별도 보존

   if(item.type==='place')key+=':'+String(item.tourLike?'tour-api':'team');

   if(keys.has(key))return;keys.add(key);merged.push(item);

  });

  merged.sort(function(a,b){return (b.likedAt||'').localeCompare(a.likedAt||'');});

  likeList=merged;renderLike();

  console.info('[마이페이지 찜 최종]',{전체:likeList.length,음식:likeList.filter(function(x){return x.type==='food';}).length,여행지:likeList.filter(function(x){return x.type==='place';}).length,버전:MYPAGE_LIKE_VERSION});

 }catch(error){

  console.error('[마이페이지 찜 불러오기 오류]',error);

  box.innerHTML='<p class="empty">찜 목록을 불러오지 못했습니다. 새로고침 후 다시 확인해 주세요.</p>';

  if(countEl)countEl.textContent='0곳';

 }finally{

  likeBusy=false;

  if(likeReloadRequested){likeReloadRequested=false;loadLike();}

 }

}

//=========================================================

//주제: 카드 목록 표시

//설명: 팀원의 기존 .like-* CSS와 탭 필터 구조 유지

//=========================================================

function renderLike(){

 var box=document.getElementById('like-area'),countEl=document.getElementById('like-count');if(!box)return;

 if(countEl)countEl.textContent=likeList.length+'곳';

 var chips='<div class="filter-chips">';

 ['all','acc','place','food','heritage'].forEach(function(t){

  var n=likeList.filter(function(x){return t==='all'||x.type===t;}).length;

  chips+='<button type="button" class="chip'+(likeFilter===t?' active':'')+'" data-filter="'+t+'">'+likeEsc(t==='all'?'전체':LIKE_TYPE_NAME[t])+' '+n+'</button>';

 });

 chips+='</div>';

 var showList=likeList.filter(function(x){return likeFilter==='all'||x.type===likeFilter;});

 if(!showList.length){box.innerHTML=chips+'<p class="empty">찜한 곳이 없어요.</p>';return;}

 var html=chips,currentGroup='';

 showList.forEach(function(item){

  var group=halfYearName(item.likedAt);

  if(group!==currentGroup){

   if(currentGroup)html+='</div>';

   var groupCount=showList.filter(function(x){return halfYearName(x.likedAt)===group;}).length;

   html+='<h4 class="like-group-title">'+likeEsc(group)+' <span>'+groupCount+'곳</span></h4><div class="like-grid">';

   currentGroup=group;

  }

  var url=likeCardUrl(item),fallback=likeImageFallback(item),src=likeImageSource(item),isNaver=/^https:\/\/map\.naver\.com\//.test(url);

  var isAccommodationNoImage=item.type==='acc'&&!String(item.image||'').trim();

  var target=isNaver?' target="_blank" rel="noopener noreferrer" data-naver-map="1"':'';

  html+='<div class="like-card" data-type="'+likeEsc(item.type)+'" data-fallback="'+likeEsc(fallback)+'">'+

   '<a href="'+likeEsc(url)+'"'+target+' aria-label="'+likeEsc((item.name||'장소')+' 지도 보기')+'">'+

   '<div class="like-photo'+(isAccommodationNoImage?' accommodation-ready':'')+'"><img src="'+likeEsc(src)+'" alt="'+likeEsc(item.name||'장소 사진')+'" loading="lazy">'+

   (isAccommodationNoImage?'<div class="accommodation-ready-message"><strong>이미지를 준비중입니다</strong><span>숙소 대표 이미지</span></div>':'')+

   '<span class="like-date">'+likeEsc(shortDate(item.likedAt))+' 찜</span></div>'+

   '<div class="like-info"><div class="like-type">'+likeEsc(LIKE_TYPE_NAME[item.type]||'여행지')+'</div><div class="like-name">'+likeEsc(item.name||'이름 정보 없음')+'</div><div class="like-addr">'+likeEsc(item.addr||'주소 정보 없음')+'</div></div></a>'+

   '<button type="button" class="like-remove" title="찜 해제" aria-label="'+likeEsc(item.name||'장소')+' 찜 해제" data-type="'+likeEsc(item.type)+'" data-id="'+likeEsc(item.id)+'" data-tour="'+(item.tourLike?'1':'0')+'">♥</button></div>';

 });

 html+='</div>';box.innerHTML=html;

 box.querySelectorAll('.like-card img').forEach(function(img){

  var fallback=img.closest('.like-card').getAttribute('data-fallback');

  // =========================================================

  // 주제: failed 처리

  // 설명: failed 기능에 필요한 처리 로직을 수행

  // =========================================================


  function failed(){

   if(img.getAttribute('data-fallback-done')==='1')return;

   img.setAttribute('data-fallback-done','1');

   img.src=fallback;

   var card=img.closest('.like-card');

   if(card&&card.getAttribute('data-type')==='acc'){

    var photo=img.closest('.like-photo');

    if(photo){

     photo.classList.add('accommodation-ready');

     if(!photo.querySelector('.accommodation-ready-message')){

      photo.insertAdjacentHTML('beforeend','<div class="accommodation-ready-message"><strong>이미지를 준비중입니다</strong><span>숙소 대표 이미지</span></div>');

     }

    }

   }

  }


  img.addEventListener('error',failed);

  if(img.complete&&img.naturalWidth===0)failed();

 });

}

//=========================================================

//주제: 클릭 처리

//설명: 카드 클릭은 네이버 지도 새 탭, 하트는 DB에서 본인 찜 취소

//=========================================================

async function deleteMyLike(btn){

 if(!confirm('찜을 해제할까요?'))return;

 var type=btn.dataset.type,id=btn.dataset.id,isTour=btn.dataset.tour==='1';

 btn.disabled=true;

 try{

  var extraType=(type==='acc'||type==='heritage');

  var endpoint=isTour?likeCtx()+'/TourLike':(extraType?likeCtx()+'/ExtraLikes':likeCtx()+'/Mypage/mypageLikeDeletePerMember');

  var params=isTour?{contentId:id}:(extraType?{type:type,id:id,liked:'false'}:{type:type,id:id});

  var r=await fetch(endpoint,{method:'POST',credentials:'same-origin',headers:{'Content-Type':'application/x-www-form-urlencoded;charset=UTF-8'},body:new URLSearchParams(params)});

  var data=await r.json();

  if(!r.ok||data.success!==true||((isTour||extraType)&&data.liked!==false))throw new Error(data.message||'찜 취소 실패');

  await loadLike();

 }catch(error){console.error('[찜 취소 실패]',error);alert('찜 해제에 실패했습니다. 다시 확인해 주세요.');}

 finally{btn.disabled=false;}

}

// 문서 캡처 단계에서 지도 링크를 우선 처리해 다른 스크립트의 카드 이동 처리 방지

// =========================================================

// 주제: handleLikeClick 처리

// 설명: 찜 상태 조회 또는 추가/해제를 처리

// =========================================================

function handleLikeClick(e){

 var nav=e.target.closest&&e.target.closest('#like-area a[data-naver-map="1"]');

 if(nav){

  e.preventDefault();e.stopImmediatePropagation();

  var popup=window.open(nav.href,'_blank');

  if(!popup)window.location.assign(nav.href);

  return;

 }

 var chip=e.target.closest&&e.target.closest('#like-area .chip');

 if(chip){e.preventDefault();likeFilter=chip.dataset.filter;renderLike();return;}

 var remove=e.target.closest&&e.target.closest('#like-area .like-remove');

 if(remove){e.preventDefault();e.stopPropagation();if(!remove.disabled)deleteMyLike(remove);}

}

if(!window.__MYPAGE_LIKE_UNDERSCORE_BOUND__){document.addEventListener('click',handleLikeClick,true);window.__MYPAGE_LIKE_UNDERSCORE_BOUND__=true;}

if(document.readyState==='loading')document.addEventListener('DOMContentLoaded',loadLike,{once:true});

else loadLike();

window.addEventListener('pageshow',function(e){if(e.persisted)loadLike();});

window.MYPAGE_LIKE_VERSION=MYPAGE_LIKE_VERSION;
