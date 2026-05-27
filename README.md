# 🗺️ 한국 관광 가이드 프로젝트

## 📋 프로젝트 개요

한국관광공사 공공 API를 활용해 전국 관광지를 카테고리/지역별로 탐색하고,
카카오 지도에 마커로 표시하는 Spring Boot 웹 애플리케이션입니다.

---

## 🛠️ 기술 스택

| 구분 | 기술 |
|------|------|
| Backend | Spring Boot 3.5.14, Java 21 |
| Security | Spring Security |
| Database | MariaDB + Spring Data JPA |
| Frontend | HTML, CSS, JavaScript (Vanilla) |
| 지도 API | 카카오 지도 SDK |
| 관광 API | 한국관광공사 TourAPI (data.go.kr) |
| 기타 | spring-dotenv (환경변수 관리) |

---

## 📁 프로젝트 구조

```
src/main/java/com/project/project/
│
├── ProjectApplication.java          ← 앱 진입점
│
├── config/
│   ├── AppConfig.java               ← RestTemplate Bean 등록
│   └── SecurityConfig.java          ← Spring Security 설정
│
├── controller/
│   ├── TourController.java          ← 관광지 API 엔드포인트
│   ├── AuthController.java          ← 회원가입/로그인/로그아웃
│   ├── FavoriteController.java      ← 즐겨찾기 API
│   ├── ReviewController.java        ← 리뷰 API
│   └── VisitHistoryController.java  ← 방문 기록 API
│
├── service/
│   ├── TourService.java             ← 공공 API 호출 로직
│   ├── UserService.java             ← 회원가입, Spring Security 인증
│   ├── FavoriteService.java         ← 즐겨찾기 비즈니스 로직
│   ├── ReviewService.java           ← 리뷰 비즈니스 로직
│   └── VisitHistoryService.java     ← 방문 기록 비즈니스 로직
│
├── repository/
│   ├── UserRepository.java          ← users 테이블 접근
│   ├── FavoriteRepository.java      ← favorites 테이블 접근
│   ├── ReviewRepository.java        ← reviews 테이블 접근
│   └── VisitHistoryRepository.java  ← visit_history 테이블 접근
│
├── entity/
│   ├── User.java                    ← 회원 테이블 매핑
│   ├── Favorite.java                ← 즐겨찾기 테이블 매핑
│   ├── Review.java                  ← 리뷰 테이블 매핑
│   └── VisitHistory.java            ← 방문 기록 테이블 매핑
│
└── dto/
    └── SignupDto.java               ← 회원가입 요청 데이터

src/main/resources/
├── application.properties           ← 서버/DB/JPA 설정
└── static/
    └── index.html                   ← 프론트엔드 (SPA)
```

---

## 🗄️ DB 테이블 구조

### users
| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGINT | PK |
| username | VARCHAR | 아이디 (unique) |
| password | VARCHAR | BCrypt 암호화 비밀번호 |
| nickname | VARCHAR | 닉네임 |
| email | VARCHAR | 이메일 (unique) |
| provider | VARCHAR | 로그인 방식 (local/kakao/google) |
| provider_id | VARCHAR | 소셜 로그인 ID |
| created_at | DATETIME | 가입일 |

### favorites
| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGINT | PK |
| user_id | BIGINT | FK → users |
| content_id | VARCHAR | 관광지 고유 ID |
| title | VARCHAR | 관광지 이름 |
| addr | VARCHAR | 주소 |
| image | VARCHAR | 이미지 URL |
| content_type_id | VARCHAR | 카테고리 ID |
| mapx / mapy | VARCHAR | 경도/위도 |
| created_at | DATETIME | 저장일 |

### reviews
| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGINT | PK |
| user_id | BIGINT | FK → users |
| content_id | VARCHAR | 관광지 고유 ID |
| spot_title | VARCHAR | 관광지 이름 |
| content | VARCHAR(1000) | 리뷰 내용 |
| rating | INT | 별점 (1~5) |
| created_at | DATETIME | 작성일 |

### visit_history
| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGINT | PK |
| user_id | BIGINT | FK → users |
| content_id | VARCHAR | 관광지 고유 ID |
| title | VARCHAR | 관광지 이름 |
| addr | VARCHAR | 주소 |
| image | VARCHAR | 이미지 URL |
| visited_at | DATETIME | 방문일 |

---

## 🔌 API 목록

### 관광지
| 메서드 | URL | 설명 |
|--------|-----|------|
| GET | `/api/spots?areaCode={지역코드}&contentTypeId={카테고리}` | 관광지 목록 조회 |

### 인증
| 메서드 | URL | 설명 |
|--------|-----|------|
| POST | `/api/auth/signup` | 회원가입 |
| POST | `/api/auth/login` | 로그인 |
| POST | `/api/auth/logout` | 로그아웃 |
| GET  | `/api/auth/me` | 현재 로그인 사용자 확인 |

### 즐겨찾기 (로그인 필요)
| 메서드 | URL | 설명 |
|--------|-----|------|
| POST | `/api/favorites/toggle` | 즐겨찾기 추가/취소 |
| GET  | `/api/favorites` | 즐겨찾기 목록 |
| GET  | `/api/favorites/check?contentId={id}` | 즐겨찾기 여부 확인 |

### 리뷰
| 메서드 | URL | 설명 |
|--------|-----|------|
| POST   | `/api/reviews` | 리뷰 작성 (로그인 필요) |
| GET    | `/api/reviews/spot/{contentId}` | 관광지 리뷰 목록 |
| GET    | `/api/reviews/my` | 내 리뷰 목록 (로그인 필요) |
| DELETE | `/api/reviews/{reviewId}` | 리뷰 삭제 (로그인 필요) |

### 방문 기록 (로그인 필요)
| 메서드 | URL | 설명 |
|--------|-----|------|
| POST | `/api/visits` | 방문 기록 저장 |
| GET  | `/api/visits` | 방문 기록 목록 |

---

## 🗺️ 지역 코드

| 코드 | 지역 | 코드 | 지역 |
|------|------|------|------|
| 1 | 서울 | 31 | 경기 |
| 2 | 인천 | 32 | 강원 |
| 3 | 대전 | 33 | 충북 |
| 4 | 대구 | 34 | 충남 |
| 5 | 광주 | 35 | 경북 |
| 6 | 부산 | 36 | 경남 |
| 7 | 울산 | 37 | 전북 |
| 8 | 세종 | 38 | 전남 |
| 39 | 제주 | | |

## 📂 카테고리 코드

| 코드 | 카테고리 |
|------|---------|
| 12 | 🏞️ 관광지 |
| 14 | 🏛️ 문화시설 |
| 15 | 🎉 축제/공연/행사 |
| 28 | 🏄 레포츠 |
| 32 | 🏨 숙박 |
| 38 | 🛍️ 쇼핑 |
| 39 | 🍽️ 음식점 |

---

## 🔐 환경변수 (.env)

```
TOUR_API_KEY=한국관광공사_API_키
DB_PASSWORD=MariaDB_비밀번호
```

> `.env` 파일은 `.gitignore`에 등록되어 있어 깃에 올라가지 않습니다.

---

## 🚀 실행 방법

1. MariaDB에서 `projectDB` 데이터베이스 생성
2. `.env` 파일에 API 키와 DB 비밀번호 입력
3. VSCode → 실행 및 디버그(▶) → Spring Boot 선택
4. 브라우저에서 `http://localhost:8080` 접속
