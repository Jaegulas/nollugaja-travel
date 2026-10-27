<%-- =========================================================
     주제: 파일 역할
     설명: members 영역의 favorites-section 화면을 구성하고 서버에서 전달받은 데이터를 표시
========================================================= --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- =========================================================
주제: 마이페이지 맛집·관광지 찜 목록 영역
설명: 기존 마이페이지 JSP에서 <jsp:include page="/WEB-INF/members/favorites-section.jsp" /> 사용
========================================================= --%>
<section id="my-favorites-section" class="w-full mt-8 p-5 sm:p-7 rounded-2xl bg-surface-container-lowest text-on-surface shadow-sm">
 <h2 class="text-xl font-bold mb-2">내가 찜한 장소</h2>
 <p class="text-on-surface-variant text-sm mb-5">맛집과 관광지를 한곳에서 확인하세요.</p>
 <p id="my-favorites-status" class="text-sm text-on-surface-variant">찜 목록을 불러오는 중입니다...</p>
 <div class="grid grid-cols-1 lg:grid-cols-2 gap-5 mt-5">
  <div><h3 class="font-bold text-lg mb-3">🍴 맛집 <span id="my-food-count">(0)</span></h3><div id="my-food-favorites" class="space-y-3"></div></div>
  <div><h3 class="font-bold text-lg mb-3">📍 관광지 <span id="my-tour-count">(0)</span></h3><div id="my-tour-favorites" class="space-y-3"></div></div>
 </div>
</section>
<script>
(function(){
 const root=document.getElementById("my-favorites-section");if(!root)return;
 const base="${pageContext.request.contextPath}",status=document.getElementById("my-favorites-status");
 const foodBox=document.getElementById("my-food-favorites"),tourBox=document.getElementById("my-tour-favorites");
 let foods=[],tours=[];
 //=========================================================
 //주제: 마이페이지 카드 구성
 //설명: DB 문자열은 innerHTML 대신 textContent로 안전하게 표시
 //=========================================================
 function card(item,type){
  const box=document.createElement("article");box.className="flex gap-3 p-3 rounded-xl bg-surface-container shadow-sm";
  const img=document.createElement("img");img.className="w-20 h-20 shrink-0 rounded-lg object-cover";
  img.src=/^https?:\/\//i.test(item.image||"")?item.image:base+"/resources/images/food/"+(type==="food"?"restaurant.png":"fallback_tour.png");
  img.onerror=function(){this.onerror=null;this.src=base+"/resources/images/food/fallback_tour.png";};
  const info=document.createElement("div");info.className="flex-1 min-w-0";
  const name=document.createElement("h4");name.textContent=item.name||"이름 정보 없음";name.className="font-bold truncate";
  const addr=document.createElement("p");addr.textContent=item.address||"주소 정보 없음";addr.className="text-sm text-on-surface-variant truncate mt-1";
  const actions=document.createElement("div");actions.className="flex gap-3 items-center mt-2";
  const link=document.createElement("a");link.textContent="지도 보기 ↗";link.target="_blank";link.rel="noopener noreferrer";
  link.href="https://map.naver.com/p/search/"+encodeURIComponent((item.name||"")+" "+(item.address||""));
  link.className="text-primary text-sm font-bold";
  const del=document.createElement("button");del.type="button";del.textContent="찜 취소";
  del.className="text-secondary-container text-sm font-bold hover:underline";
  del.onclick=async function(){
   del.disabled=true;
   const url=base+(type==="food"?"/FoodLike":"/TourLike");
   const params=type==="food"?{foodId:item.foodId}:{contentId:item.contentId};
   try{
    const r=await fetch(url,{method:"POST",credentials:"same-origin",headers:{"Content-Type":"application/x-www-form-urlencoded;charset=UTF-8"},body:new URLSearchParams(params)});
    if(r.status===401){status.textContent="로그인 후 사용 가능합니다.";return;}
    const data=await r.json();if(!r.ok||!data.success||data.liked)throw Error("찜 취소 실패");
    if(type==="food")foods=foods.filter(f=>f.foodId!==item.foodId);
    else tours=tours.filter(t=>t.contentId!==item.contentId);
    render();
   }catch(e){console.error(e);alert("찜 취소에 실패했습니다.");}
   finally{del.disabled=false;}
  };
  actions.append(link,del);info.append(name,addr,actions);box.append(img,info);return box;
 }
 //=========================================================
 //주제: 조회 결과 표시
 //설명: 로그인한 회원의 맛집/관광지만 표시
 //=========================================================
 function render(){
  foodBox.replaceChildren();tourBox.replaceChildren();
  document.getElementById("my-food-count").textContent="("+foods.length+")";
  document.getElementById("my-tour-count").textContent="("+tours.length+")";
  foods.forEach(f=>foodBox.appendChild(card(f,"food")));
  tours.forEach(t=>tourBox.appendChild(card(t,"tour")));
  if(!foods.length)foodBox.textContent="찜한 맛집이 없습니다.";
  if(!tours.length)tourBox.textContent="찜한 관광지가 없습니다.";
 }
 //주제: 서버에서 찜 목록 읽기
 async function load(){
  try{
   const r=await fetch(base+"/MyFavorites",{credentials:"same-origin"});
   if(r.status===401){
    status.replaceChildren();status.append("로그인 후 사용 가능합니다. ");
    const a=document.createElement("a");a.href=base+"/Login";a.textContent="로그인하기";a.className="text-primary underline";status.appendChild(a);
    return;
   }
   const data=await r.json();if(!r.ok||!data.success)throw Error(data.message||"목록 조회 실패");
   foods=data.foods||[];tours=data.tours||[];status.textContent="";render();
  }catch(e){console.error(e);status.textContent="찜 목록을 불러오지 못했습니다.";}
 }
 load();
})();
</script>
