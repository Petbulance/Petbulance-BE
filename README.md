# Petbulance-BE

반려동물 보호자를 위한 동물병원 탐색, 영수증 기반 리뷰, 커뮤니티, 알림, 관리자 기능을 제공하는 Petbulance 백엔드 서버입니다.  
팀 프로젝트에서 백엔드 개발자로 참여했으며, 병원 검색/매칭, 리뷰 도메인, 인증/권한, 관리자 기능, 운영 모니터링 영역을 중심으로 구현과 리팩터링을 진행했습니다.

## 프로젝트 개요

Petbulance는 보호자가 반려동물에게 맞는 병원을 찾고, 실제 진료 경험을 리뷰로 남길 수 있도록 돕는 서비스입니다.

- 위치 기반 동물병원 검색 및 매칭
- 영수증 이미지 기반 리뷰 작성 흐름
- 병원 상세, 병원 카드, 리뷰 필터링
- 커뮤니티 게시글, 댓글, 좋아요
- 소셜 로그인과 JWT 인증
- 관리자용 회원/병원/리뷰/공지/문의 관리
- FCM 알림, S3 이미지 업로드, AI 진단 API 연동

## 담당 역할

팀 프로젝트의 백엔드 개발자 중 한 명으로 참여했습니다.

| 영역 | 작업 내용 |
|---|---|
| 병원 도메인 | 병원 검색, 상세 조회, 카드 조회, 위치 기반 매칭 API 개선 |
| 리뷰 도메인 | 영수증 리뷰, 리뷰 검색/필터, 병원별 리뷰 커서 페이지네이션, 리뷰 좋아요 기능 |
| 인증/권한 | JWT 만료 처리, 토큰 예외 처리, 소셜 로그인 연동 흐름 개선 |
| 관리자 기능 | 회원 제재, 병원 관리, 리뷰 활성/비활성, 약관 적용 기능 개선 |
| 운영 품질 | Actuator, Prometheus, Sentry 기반 관측 포인트 추가 |
| 테스트 | 리뷰 서비스 단위 테스트 및 좌표 처리 로직 검증 |

## 기술 스택

| 영역 | 기술 |
|---|---|
| Backend | Java 17, Spring Boot 3.5, Spring Web, Spring Security |
| Persistence | Spring Data JPA, QueryDSL, MySQL, H2 Test DB |
| Cache | Redis |
| Auth | JWT, OAuth2 Client, Kakao/Naver/Google Login |
| File / Cloud | AWS S3, AWS Lambda SDK |
| Geo | Hibernate Spatial, JTS, Lucene Spatial Extras |
| AI / External | Gemini API, WebClient |
| Notification | Firebase Admin SDK, FCM |
| Docs / Ops | Springdoc OpenAPI, Actuator, Prometheus, Sentry |
| Deployment | Docker Compose, Blue/Green compose files |

## 주요 기능

### 병원 검색 및 매칭

사용자의 현재 위치, 동물 종류, 필터 조건을 기반으로 병원 목록을 조회합니다.

- 병원 목록 검색: `GET /hospitals`
- 병원 상세 조회: `GET /hospitals/{hospitalId}`
- 병원 카드 조회: `GET /hospitals/card/{hospitalId}`
- 위치 기반 병원 매칭: `GET /hospitals/matching`
- 병원 상세 매칭 정보: `GET /hospitals/{hospitalId}/matching`

QueryDSL 기반 동적 조건과 좌표 정보를 활용해 필터링, 거리 계산, 상세 정보를 조합합니다.

### 영수증 리뷰

진료 영수증 이미지를 업로드하고, 병원 리뷰를 작성하는 흐름을 제공합니다.

- 영수증 이미지 인식: `POST /receipts`
- 병원명 검색: `GET /receipts/{hospitalName}`
- 리뷰 검색: `GET /receipts/search/{value}`
- 리뷰 필터링: `GET /receipts/filter`
- 병원별 리뷰 커서 페이지네이션: `GET /receipts/reviews/{hospitalId}`
- 리뷰 작성/수정/삭제
- 리뷰 좋아요/좋아요 취소
- 내 리뷰 조회

리뷰 목록은 cursor 기반으로 조회하며, 정렬 기준과 이미지 리뷰 여부를 분리해 요청할 수 있습니다.

### 인증과 사용자 기능

- JWT 기반 access token 발급/검증
- refresh token Redis 저장
- Kakao, Naver, Google OAuth2 로그인
- 닉네임 설정과 프로필 수정
- 소셜 계정 연결/해제
- 알림 설정 관리
- 회원 탈퇴와 권한 조회

### 커뮤니티

- 게시글 작성, 조회, 수정, 삭제
- 게시글 검색
- 댓글 작성/조회/수정/삭제
- 게시글 좋아요
- 최근 검색어와 최근 조회 병원 관리
- 신고 기능

### 관리자 기능

관리자는 별도 API를 통해 서비스 운영 데이터를 관리합니다.

- 회원 검색, 상세 조회, 삭제
- 리뷰/커뮤니티 제재 및 복구
- 병원 등록, 조회, 삭제
- 리뷰 활성/비활성 관리
- 공지, 문의, 신고 관리
- 앱 버전, 지역, 품종, 약관 메타데이터 관리
- 대시보드 지표 조회

## API 그룹

| Domain | Base Path |
|---|---|
| Auth | `/auth` |
| Users | `/users` |
| Hospitals | `/hospitals` |
| Reviews / Receipts | `/receipts` |
| Posts | `/posts` |
| Comments | `/comments` |
| Notices | `/notices` |
| QnA | `/qna` |
| Reports | `/reports` |
| Notifications | `/notifications` |
| Admin | `/admin/**` |
| AI | `/ai` |

## 프로젝트 구조

```text
src/main/java/com/example/Petbulance_BE
├── global
│   ├── common          # 공통 응답, 예외, S3, 인증 공통 로직
│   ├── config          # Redis, QueryDSL, WebClient 설정
│   ├── filter          # JWT / Logout filter
│   ├── firebase        # FCM 설정 및 발송
│   ├── oauth2          # OAuth2 provider response 처리
│   ├── security        # Spring Security 설정
│   └── util
└── domain
    ├── admin           # 관리자 기능
    ├── ai              # AI 진단 API
    ├── hospital        # 병원 검색/상세/매칭
    ├── review          # 영수증 리뷰
    ├── post/comment    # 커뮤니티
    ├── user/terms      # 사용자와 약관
    ├── notification    # 알림
    └── dashboard       # 관리자 대시보드
```

## 실행 방법

### 사전 요구사항

- Java 17
- MySQL
- Redis

### 로컬 실행

```bash
./gradlew bootRun
```

### 테스트

```bash
./gradlew test
```

### 배포 compose

```bash
docker compose -f docker-compose-blue.yml up -d
docker compose -f docker-compose-green.yml up -d
```

## 운영 관점

- Actuator endpoint와 Prometheus registry를 통해 애플리케이션 상태를 수집합니다.
- Sentry를 통해 런타임 예외를 추적합니다.
- Blue/Green compose 파일을 분리해 배포 전환 구조를 구성했습니다.

## 정리

Petbulance-BE는 위치 기반 검색, 리뷰, 인증, 관리자 기능이 한 서비스 안에서 유기적으로 연결되는 백엔드 프로젝트입니다.  
특히 병원 검색과 리뷰 도메인은 QueryDSL, 좌표 처리, cursor paging, 이미지 업로드, 외부 API 연동이 함께 들어가 있어 단순 CRUD보다 복합적인 백엔드 설계를 경험할 수 있었습니다.
