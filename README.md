# ✈️ 놀러가자

> **국내 여행 정보를 한 곳에서 탐색하고 실제 여행 행동까지 연결하는 통합 여행 웹 서비스**

여행지 · 숙박 · 음식 · 문화유산 탐색부터 가이드 예약/결제, AI 여행 일정, 마이페이지, Q&A까지 하나의 서비스 흐름으로 연결한 **4인 팀 세미프로젝트**입니다.

> 본 저장소는 팀 프로젝트 결과물을 개인 포트폴리오 목적으로 정리한 저장소입니다.  
> 전체 프로젝트를 혼자 개발한 것이 아니며 개인 담당 영역은 아래에 별도로 명시했습니다.

---

## 📌 프로젝트 정보

| 항목 | 내용 |
|---|---|
| 프로젝트명 | 놀러가자 |
| 개발 기간 | 2026.09.21 ~ 2026.10.25 |
| 개발 인원 | 4명 |
| 개발 방식 | JSP / Servlet 기반 Model 2 MVC |
| Language | Java 21 · JavaScript · JSP · HTML · CSS |
| Database | MySQL 8.0 |
| Server | Tomcat 9 · Nginx |
| Deploy | AWS Lightsail |
| Collaboration | Git · GitHub · Branch · Pull Request |

### 🔗 원본 팀 협업 저장소

https://github.com/kwonsungae/travel

---

# 👥 팀 구성 및 담당 영역

| 팀원 | 담당 기능 |
|---|---|
| 권성애 | 숙박 · AI 챗봇/여행일정 · 관리자 페이지 |
| 배수환 | 여행지 · 가이드 · 예약/결제 · 번역 |
| **이재준** | **음식 · Q&A · 마이페이지 · PPT/발표자료** |
| 한상희 | 문화유산 · 메인 · 회원 · 리뷰 |

---

# 👨‍💻 My Contribution

## 🍽️ 음식 페이지

사용자가 지도에서 선택한 위치를 기준으로 주변 맛집과 관광지를 함께 탐색할 수 있도록 구현했습니다.

### 주요 기능

- 지도 클릭 기반 주변 맛집 조회
- 주변 관광지 조회
- 지역 변경 / 지도 중심 조회
- 맛집 · 관광지 탭
- 지역 · 음식점 · 메뉴 검색
- 음식 상세정보
- 찜 저장 / 해제
- 이미지 누락 시 fallback 이미지

### 구현 방식

지도에서 위치를 클릭하면 해당 좌표의 `lat`, `lng`를 서버의 `/food/listapi`로 전달합니다.

한국관광공사 **TourAPI 위치기반 조회 API**를 이용해 클릭 위치 주변 데이터를 조회했습니다.

```text
음식점 → contentTypeId = 39
관광지 → contentTypeId = 12
```

`fetch()`를 이용해 페이지 전체를 새로고침하지 않고 JSON 데이터를 받아 음식 목록, 관광지 목록, 지도 마커를 갱신했습니다.

또한 Naver Local Search를 이용해 지역명 · 음식점명 · 메뉴 검색 기능을 보완했습니다.

### 핵심 흐름

```text
지도 클릭
    ↓
lat / lng 전달
    ↓
FoodController
    ↓
FoodService / TourService
    ↓
TourAPI 위치 기반 조회
    ↓
JSON 반환
    ↓
목록 + 지도 마커 갱신
```

### 음식 찜

외부 API에서 가져온 음식은 내부 DB의 `food_id`가 없을 수 있기 때문에 찜 요청 시 DB 존재 여부를 확인합니다.

```text
API 음식 조회
    ↓
food_id 확인
    ↓
없으면 DB 등록
    ↓
food_like에 회원 찜 관계 저장
```

현재 찜 여부를 먼저 확인한 뒤 같은 버튼에서 찜 등록과 해제를 처리하도록 구현했습니다.

### 이미지 fallback

외부 API 이미지가 없거나 URL 로딩에 실패하는 경우 음식 종류에 따라 기본 이미지를 출력하도록 구현했습니다.

---

# ❤️ 마이페이지

여러 서비스에 흩어진 개인 데이터를 한 화면에서 통합 관리할 수 있도록 구현했습니다.

### 주요 기능

- 숙소 / 여행지 / 음식 / 문화유산 찜 통합
- 카테고리별 찜 필터
- 찜 해제
- 가이드 예약
- 내 여행일정
- 일정 공유 / 삭제
- 내가 작성한 리뷰
- 회원 탈퇴

### 찜 데이터 통합

각 기능의 찜 데이터는 서로 다른 테이블을 사용합니다.

```text
accommodation_like → acc
travel_like / tour_like → place
food_like → food
cultural_like → heritage
```

각 조회 결과를 공통 형태로 변환하고 `UNION ALL`을 이용해 하나의 목록으로 조회했습니다.

회원별 데이터는 로그인 세션의 `loginId`를 기준으로 조회합니다.

```java
String loginId =
    (String)session.getAttribute("loginId");
```

---

## 🚪 회원 탈퇴

회원 정보를 단순히 `DELETE`하면 기존 리뷰나 Q&A 등 다른 테이블과의 관계가 깨질 수 있기 때문에 **상태 기반 탈퇴 방식**으로 구현했습니다.

### 처리 과정

```text
비밀번호 재확인
        ↓
register.status
ACTIVE → WITHDRAWN
        ↓
withdrawn_at 기록
        ↓
개인정보 익명화
        ↓
개인 찜 / 일정 삭제
        ↓
리뷰 · Q&A는 유지
        ↓
작성자 → 탈퇴회원
```

여러 테이블이 동시에 변경되기 때문에 전체 작업을 **Transaction**으로 처리했습니다.

중간 작업이 실패하면 `rollback`, 모든 작업이 성공하면 `commit`하여 데이터가 일부만 변경되는 문제를 방지했습니다.

---

# 💬 Q&A 게시판

질문과 답변을 별도의 테이블로 관리하고 검색 · 상태 · 권한 · 보안을 서버에서 처리했습니다.

### 주요 기능

- 질문 등록 / 수정 / 삭제
- 로그인 회원 답변 작성
- 답변 삭제
- 카테고리 필터
- 답변대기 / 답변완료
- 검색 · 정렬
- 페이지네이션
- 조회수
- 탈퇴회원 익명화

### 데이터 구조

```text
qna_question
      │
      │ question_id
      ↓
qna_answer
```

질문과 답변을 분리하여 하나의 질문에 연결된 답변을 관리했습니다.

### 작성자 권한

일반 사용자의 수정/삭제 요청에는 `member_id` 조건을 함께 검사하여 자신의 글만 수정할 수 있도록 처리했습니다.

```sql
WHERE question_id = ?
  AND member_id = ?
```

관리자는 관리 권한에 따라 수정/삭제 범위가 확장됩니다.

### CSRF 보호

질문 등록 · 수정 · 삭제 및 답변 작성처럼 데이터를 변경하는 요청은 세션의 CSRF 토큰과 요청 토큰을 비교합니다.

토큰이 일치하지 않으면 서버에서 `403`으로 요청을 차단합니다.

---

# 📊 PPT · 발표자료

프로젝트의 기능 구현뿐만 아니라 최종 결과보고서와 발표자료 제작도 담당했습니다.

- 프로젝트 결과보고서 PPT 구성
- 기능별 구현 방식 정리
- DB / API / 기술 스택 시각화
- 음식 · 마이페이지 · Q&A 핵심 코드 정리
- 기능 시연 발표 흐름 구성
- 발표 대본 작성 및 최종 PPT 통합

---

# ✨ 전체 주요 기능

## 👤 회원

- 일반 회원가입 / 로그인
- 카카오 로그인
- 아이디 / 비밀번호 찾기
- BCrypt 비밀번호 검증
- 로그인 상태 기반 접근 제어
- 회원 탈퇴 및 개인정보 익명화

## 🌏 여행지

- 지역별 여행지 조회
- 키워드 검색
- 여행지 상세정보
- 지도 / 마커
- 찜 저장 · 해제
- 페이지네이션

## 🏨 숙박

- 지역별 숙소 조회
- 숙소 검색
- Kakao Map
- 거리 기반 주변 숙소 조회
- AI 장점 · 단점 요약
- 숙소 찜
- 길찾기 정보
- 이미지 fallback

## 🍽️ 음식

- TourAPI 위치 기반 맛집 / 관광지 조회
- Naver Map
- Naver Local Search
- 지도 마커 / 목록 동기화
- 음식 찜
- 이미지 fallback

## 🏛️ 문화유산

- 지역 / 검색 / 정렬
- 페이지네이션
- 상세정보
- 회원별 좋아요
- TourAPI 연동

## 🧑‍🏫 가이드 · 예약 · 결제

- 가이드 검색
- 가이드 상세정보
- 날짜 · 인원 선택
- 예약 정보 관리
- 결제금액 검증
- Toss Payments 결제
- 결제 상태 관리

## 🤖 AI 여행 일정

- 날짜 · 인원 · 지역 · 여행 스타일 입력
- 일차별 일정 설정
- OpenRouter 기반 AI 일정 생성
- 일정 저장
- PDF 다운로드
- 공유 링크
- 카카오톡 공유

## ⭐ 리뷰

- 리뷰 CRUD
- 이미지 저장
- 댓글
- 좋아요
- 조회수
- 작성자 권한 관리

## 💬 Q&A

- 질문 / 답변
- 검색 / 정렬
- 카테고리
- 페이지네이션
- 작성자 권한
- CSRF 보호

## 🌐 공통 기능

- 다국어 번역
- 날씨
- WebSocket 실시간 채팅
- 공통 Header / Footer
- API Key 설정 파일 분리

---

# 🏗️ Model 2 MVC

```text
Browser
   ↓
Filter
로그인 · 권한
   ↓
Servlet Controller
요청 / URL 처리
   ↓
Service / DAO
비즈니스 로직 · SQL
   ↓
MySQL / External API
   ↓
JSP / JSON
화면 · 비동기 응답
```

---

# 🛠️ Tech Stack

### Backend

- Java 21
- Servlet
- JSP
- JDBC
- BCrypt
- Gson

### Frontend

- HTML5
- CSS3
- JavaScript
- Fetch API

### Database

- MySQL 8.0

### Server / Deploy

- Apache Tomcat 9
- Nginx
- Ubuntu
- AWS Lightsail

### Collaboration

- Git
- GitHub
- Branch
- Pull Request

---

# 🔌 External API

| API | 사용 |
|---|---|
| 한국관광공사 TourAPI | 여행지 · 음식 · 문화유산 |
| Naver Map | 음식 지도 |
| Naver Local Search | 음식 검색 |
| Kakao Maps / Places | 여행지 · 숙박 |
| Kakao Login | 소셜 로그인 |
| Kakao Share | 일정 공유 |
| OpenRouter | 숙박 리뷰 요약 · AI 일정 |
| Toss Payments | 가이드 예약 결제 |
| Translation API | 번역 |
| Weather API | 날씨 |

---

# 🔧 Trouble Shooting

## 1. 마이페이지 찜이 0개로 표시되는 문제

### 문제

DB에는 찜 데이터가 존재했지만 마이페이지에서는 일부 찜이 0개로 표시되었습니다.

### 원인

로그인 시 사용한 세션 키와 일부 기능에서 조회한 세션 키가 달랐습니다.

### 해결

회원 식별 세션 키를 `loginId`로 통일했습니다.

---

## 2. 외부 API 이미지 누락

### 문제

외부 API에서 이미지가 없거나 URL 로딩이 실패하면 빈 이미지가 표시되었습니다.

### 해결

음식 종류와 숙박 유형에 맞는 fallback 이미지를 추가하고 이미지 로딩 오류도 처리했습니다.

---

## 3. 회원 탈퇴 데이터 관계

### 문제

회원 정보를 물리적으로 삭제하면 기존 리뷰 / Q&A 등의 연결 관계가 깨질 수 있었습니다.

### 해결

- `ACTIVE → WITHDRAWN`
- `withdrawn_at` 기록
- 개인정보 익명화
- 개인 찜 / 일정 삭제
- 리뷰 / Q&A 유지
- 작성자를 `탈퇴회원`으로 표시
- Transaction 처리

---

## 4. API 음식과 DB 찜 연결

### 문제

TourAPI에서 가져온 음식은 내부 `food_id`가 존재하지 않을 수 있어 바로 찜 관계를 만들 수 없었습니다.

### 해결

찜 요청 시 음식이 DB에 존재하는지 확인하고, 없다면 등록한 뒤 `food_like`와 연결하는 `find-or-create` 방식을 사용했습니다.

---

# 🚀 배포 구조

```text
사용자 Browser
      ↓ HTTPS
     Nginx
      ↓
    Tomcat 9
      ↓ JDBC
    MySQL 8
```

AWS Lightsail Ubuntu 서버에 WAR 파일을 수동 배포하고 Nginx Reverse Proxy를 이용해 서비스를 구성했습니다.

---

# 🔐 Security

민감정보는 저장소에 포함하지 않습니다.

```text
config.properties
.env
API Key
DB Password
AWS Credential
개인 인증정보
```

---

# 🙋 담당 영역 요약

### 이재준

- 음식 페이지
- TourAPI 위치 기반 맛집 / 관광지 조회
- Naver Map / Naver Local Search
- 음식 찜 기능
- 음식 이미지 fallback
- 마이페이지 통합 찜 목록
- 회원 탈퇴 / 개인정보 익명화
- Q&A 게시판
- 작성자 권한 및 CSRF 처리
- 프로젝트 PPT / 발표자료

---

## 📎 Original Team Repository

https://github.com/kwonsungae/travel

> 본 프로젝트는 4인 팀 프로젝트이며, 이 저장소는 개인 포트폴리오를 위해 프로젝트와 개인 기여 내용을 정리한 저장소입니다.