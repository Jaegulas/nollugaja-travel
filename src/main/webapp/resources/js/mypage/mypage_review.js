// =========================================================
// 주제: 파일 역할
// 설명: 마이페이지 내 리뷰 조회와 상세 페이지 이동을 처리
// =========================================================

/* =========================================
   mypage-review.js  -  내 리뷰 (review + review_image + review_like + review_comment)

   [내 리뷰] 탭을 누르면 loadReview() 실행
     → fetch  GET  CTX + '/Mypage/myReviewList'
     → 서버가 MyReviewDTO 목록을 JSON 으로 보내줌
       { "success": true,
         "list": [{ "reviewId":24, "category":"숙소", "title":"...", "content":"리뷰 본문",
                    "createdAt":"2026-10-11", "viewCount":52, "likeCount":3, "commentCount":2,
                    "images":["사진1주소","사진2주소"] }, ...] }

   이 파일보다 먼저 있어야 하는 것 : mypage-common.js (CTX, esc, imgTag)

   ★ 디자인 선택 : 'card' / 'magazine' / 'feed' 중 하나
   ========================================= */
var REVIEW_DESIGN = 'card';

var reviewList = [];
var reviewLoaded = false;   // 한 번 불러왔으면 탭을 다시 눌러도 또 요청하지 않기


/* ---------- 혹시 mypage-common.js 에 없을 때를 대비한 도우미 ---------- */

// 글자 안의 < > & " ' 를 안전하게 바꾸기 (리뷰 글에 태그가 있어도 그대로 글자로 보이게)
if (typeof window.esc !== 'function') {
  window.esc = function (str) {
    return String(str == null ? '' : str)
      .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;').replace(/'/g, '&#39;');
  };
}

// <img> 태그 만들기
if (typeof window.imgTag !== 'function') {
  window.imgTag = function (src, alt) {
    return '<img src="' + esc(src) + '" alt="' + esc(alt) + '" loading="lazy">';
  };
}


/* ---------- 불러오기 ---------- */

// force = true 이면 이미 불러왔어도 다시 불러오기 (리뷰 쓰고 돌아왔을 때 등)
// =========================================================
// 주제: loadReview 처리
// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
// =========================================================
function loadReview(force) {
  if (reviewLoaded && !force) {
    renderReview();
    return;
  }

  var box = document.getElementById('review-area');
  box.innerHTML = '<p class="rv-loading">리뷰를 불러오는 중...</p>';

  fetch(CTX + '/Mypage/getMypageReviewAll')
  .then(checkLogin)
    .then(function (res) {
      if (!res.ok) throw new Error('서버 응답 오류: ' + res.status);
      return res.json();
    })
    .then(function (data) {
      if (!data.success) {
        box.innerHTML = '<p class="empty">' + esc(data.message || '리뷰를 불러오지 못했어요.') + '</p>';
        return;
      }
      reviewList = data.list || [];
      reviewLoaded = true;
      renderReview();
    })
    .catch(function (err) {
      console.error('내 리뷰 불러오기 실패', err);
	  if (err.message === 'LOGIN_REQUIRED') return; 
      box.innerHTML = '<p class="empty">리뷰를 불러오지 못했어요. 잠시 후 다시 시도해주세요.</p>';
    });
}

// =========================================================
// 주제: renderReview 처리
// 설명: renderReview 기능에 필요한 처리 로직을 수행
// =========================================================
function renderReview() {
  var box = document.getElementById('review-area');
  document.getElementById('review-count').textContent = reviewList.length + '개';

  if (reviewList.length === 0) {
    box.innerHTML = '<p class="empty">작성한 리뷰가 없어요.</p>';
    return;
  }

  var html = '<div class="review-' + REVIEW_DESIGN + '-list">';
  reviewList.forEach(function (r) {
    if (REVIEW_DESIGN === 'card')     html += reviewCard(r);
    if (REVIEW_DESIGN === 'magazine') html += reviewMagazine(r);
    if (REVIEW_DESIGN === 'feed')     html += reviewFeed(r);
  });
  html += '</div>';
  box.innerHTML = html;

  checkLongText();   // 긴 글에만 "더보기" 버튼 보이기
}


/* ---------- 공통 조각 ---------- */

// 사진 주소 정리
//   "http..." 로 시작하면 그대로
//   "/upload/review/a.jpg" 처럼 서버 안 경로면 앞에 CTX 붙이기
//   ★ DB 에 "upload/review/a.jpg" 처럼 / 없이 저장했다면 그것도 CTX + "/" 를 붙여줌
// =========================================================
// 주제: photoUrl 처리
// 설명: photoUrl 기능에 필요한 처리 로직을 수행
// =========================================================
function photoUrl(src) {
  if (!src) return '';
  if (src.indexOf('http') === 0) return src;
  return CTX + (src.charAt(0) === '/' ? '' : '/') + src;
}

// 사진이 있는지
// =========================================================
// 주제: hasPhoto 처리
// 설명: 입력값 또는 현재 상태가 조건에 맞는지 검사
// =========================================================
function hasPhoto(r) {
  return r.images && r.images.length > 0;
}

// 첫 번째 사진
function firstImage(r) {
  return hasPhoto(r) ? photoUrl(r.images[0]) : '';
}

// 사진 여러 장이면 "📷 3" 표시
// =========================================================
// 주제: photoCount 처리
// 설명: photoCount 기능에 필요한 처리 로직을 수행
// =========================================================
function photoCount(r) {
  var n = r.images ? r.images.length : 0;
  return n > 1 ? '<span class="rv-photo-count">📷 ' + n + '</span>' : '';
}

// 날짜 · 조회 · 좋아요 · 댓글
// =========================================================
// 주제: reviewMeta 처리
// 설명: reviewMeta 기능에 필요한 처리 로직을 수행
// =========================================================
function reviewMeta(r) {
  return '<div class="rv-meta">' +
           '<span>' + esc(r.createdAt) + '</span>' +
           '<span>조회 ' + r.viewCount + '</span>' +
           '<span>♥ ' + r.likeCount + '</span>' +
           '<span>💬 ' + r.commentCount + '</span>' +
         '</div>';
}

// 본문 + 더보기 버튼 (버튼은 처음엔 숨김, 긴 글일 때만 보임)
// =========================================================
// 주제: reviewText 처리
// 설명: reviewText 기능에 필요한 처리 로직을 수행
// =========================================================
function reviewText(r) {
  return '<p class="rv-text">' + esc(r.content) + '</p>' +
         '<button type="button" class="rv-more" hidden>더보기 ▾</button>';
}

// ★ 리뷰 상세 페이지 주소는 프로젝트에 맞게
// =========================================================
// 주제: detailUrl 처리
// 설명: detailUrl 기능에 필요한 처리 로직을 수행
// =========================================================
function detailUrl(r) {
  return CTX + '/Review?reviewId=' + encodeURIComponent(r.reviewId);
}


/* ---------- 디자인 1 : 큰 사진 카드 (2열) ---------- */
// =========================================================
// 주제: reviewCard 처리
// 설명: reviewCard 기능에 필요한 처리 로직을 수행
// =========================================================
function reviewCard(r) {
  var photo = '';
  var badge = '';
  if (hasPhoto(r)) {
    photo = '<a class="rv-photo" href="' + detailUrl(r) + '">' +
              imgTag(firstImage(r), r.title) + photoCount(r) +
              '<span class="rv-badge">' + esc(r.category) + '</span>' +
            '</a>';
  } else {
    badge = '<span class="rv-badge-inline">' + esc(r.category) + '</span>';
  }

  return '<div class="rv-card' + (hasPhoto(r) ? '' : ' no-photo') + '">' +
           photo +
           '<div class="rv-body">' +
             badge +
             '<a class="rv-title" href="' + detailUrl(r) + '">' + esc(r.title) + '</a>' +
             reviewText(r) +
             reviewMeta(r) +
           '</div>' +
         '</div>';
}

/* ---------- 디자인 2 : 매거진 (왼쪽 큰 사진 + 오른쪽 글) ---------- */
// =========================================================
// 주제: reviewMagazine 처리
// 설명: reviewMagazine 기능에 필요한 처리 로직을 수행
// =========================================================
function reviewMagazine(r) {
  var photo = '';
  if (hasPhoto(r)) {
    photo = '<a class="rv-photo" href="' + detailUrl(r) + '">' +
              imgTag(firstImage(r), r.title) + photoCount(r) +
            '</a>';
  }

  return '<div class="rv-mag">' +
           photo +
           '<div class="rv-body">' +
             '<span class="rv-badge-inline">' + esc(r.category) + '</span>' +
             '<a class="rv-title" href="' + detailUrl(r) + '">' + esc(r.title) + '</a>' +
             reviewText(r) +
             reviewMeta(r) +
           '</div>' +
         '</div>';
}

/* ---------- 디자인 3 : 피드 (가운데 한 줄, 사진 여러 장 가로로) ---------- */
// =========================================================
// 주제: reviewFeed 처리
// 설명: reviewFeed 기능에 필요한 처리 로직을 수행
// =========================================================
function reviewFeed(r) {
  var photos = '';
  if (hasPhoto(r)) {
    photos = '<div class="rv-photos">';
    r.images.forEach(function (src) {
      photos += imgTag(photoUrl(src), r.title);
    });
    photos += '</div>';
  }

  return '<div class="rv-feed' + (hasPhoto(r) ? '' : ' no-photo') + '">' +
           '<div class="rv-feed-head">' +
             '<span class="rv-badge-inline">' + esc(r.category) + '</span>' +
             '<span class="rv-date">' + esc(r.createdAt) + '</span>' +
           '</div>' +
           photos +
           '<div class="rv-body">' +
             '<a class="rv-title" href="' + detailUrl(r) + '">' + esc(r.title) + '</a>' +
             reviewText(r) +
             reviewMeta(r) +
           '</div>' +
         '</div>';
}


/* ---------- 긴 글 확인 : 잘린 글에만 "더보기" 보이기 ---------- */
// =========================================================
// 주제: checkLongText 처리
// 설명: 입력값 또는 현재 상태가 조건에 맞는지 검사
// =========================================================
function checkLongText() {
  document.querySelectorAll('#review-area .rv-text').forEach(function (p) {
    // 글 전체 높이 > 보이는 높이 이면 잘린 것
    if (p.scrollHeight > p.clientHeight + 2) {
      p.nextElementSibling.hidden = false;
    }
  });
}

// 더보기 / 접기
document.getElementById('review-area').addEventListener('click', function (e) {
  var btn = e.target.closest('.rv-more');
  if (!btn) return;

  var text = btn.previousElementSibling;
  var open = text.classList.toggle('open');
  btn.textContent = open ? '접기 ▴' : '더보기 ▾';
});
