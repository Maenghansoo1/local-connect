# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Tech Stack

- **Java 21**, **Spring Boot 4.0.5** (Spring Framework 7)
- **Spring Data JPA** + **Hibernate** — DDL auto: `update`, SQL 로그 활성화
- **MariaDB 12.2** — `local-connect` 데이터베이스, localhost:3306
- **Lombok** — 단, Eclipse에서 Lombok 어노테이션 처리가 불안정하므로 주의 (아래 참고)
- **Maven** 빌드 도구

## 실행 방법

**Eclipse에서 실행한다** (필수).

- `LocalConnectApplication.java` 우클릭 → `Run As` → `Spring Boot App`
- `./mvnw spring-boot:run`은 사용하지 않는다. Eclipse가 컴파일한 `.class` 파일과 Maven 빌드가 충돌해 `Unresolved compilation problem` 오류가 발생한다.

컴파일 확인이 필요할 때만 터미널에서 사용:
```
set JAVA_HOME=C:\Program Files\Java\jdk-21.0.10
mvnw clean compile
```

## Lombok 주의사항

Eclipse에서 `@RequiredArgsConstructor`와 `@Slf4j`가 제대로 처리되지 않는다.
- **생성자 주입은 직접 생성자를 작성한다.**
- **로거는 `@Slf4j` 대신 `private static final Logger log = LoggerFactory.getLogger(XXX.class);` 를 사용한다.**

```java
// 사용 금지
@RequiredArgsConstructor
public class SomeService {
    private final SomeRepository someRepository;
}

// 올바른 방법
public class SomeService {
    private final SomeRepository someRepository;

    public SomeService(SomeRepository someRepository) {
        this.someRepository = someRepository;
    }
}
```

`@Getter`, `@Setter`, `@Slf4j`, `@Builder`, `@NoArgsConstructor` 등 필드/메서드 생성 어노테이션은 정상 동작한다.

## Database Setup

```sql
CREATE DATABASE `local-connect`;
```

DB 접속 정보는 `application.properties`에 직접 작성되어 있다 (root / 1234).

## 패키지 구조 규칙

기능별로 패키지를 구성한다:

```
com.local.connect
├── {기능}/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   ├── dto/
│   └── client/       # 외부 API 호출
├── scheduler/        # @Scheduled 작업
└── common/
    ├── config/
    └── exception/
```

> 참고: pom.xml의 groupId는 `com.localconnect`이지만 실제 패키지는 `com.local.connect`를 사용한다.

## 개발 규칙

### 1. 보안 — 환경변수
- API 키 등 민감 정보는 프로젝트 루트의 `.env` 파일에 저장한다 (`.gitignore` 등록됨).
- Spring에서는 `.env`를 시스템 환경변수로 로드한 뒤 `@Value`로 주입한다.
- `application.properties`나 코드에 민감 정보를 직접 작성하지 않는다.

```
# .env 예시
TOUR_API_KEY=your_decoding_key_here
DB_PASSWORD=1234
```

```java
@Value("${TOUR_API_KEY}")
private String apiKey;
```

### 2. 스케줄러 — Quartz
- 정기 작업(매일 새벽 데이터 동기화)은 Spring `@Scheduled` 대신 **Quartz**를 사용한다.
- `pom.xml`에 `spring-boot-starter-quartz` 의존성을 추가한다.
- Job 클래스는 `QuartzJobBean`을 상속하고, 트리거는 Cron 표현식으로 설정한다.

### 3. 데이터 처리 — Upsert
- 공공 API 응답 JSON에서 핵심 필드만 추출해 DTO로 변환한다.
- DB 저장 시 `contentId`(고유키) 기준으로 **Upsert** 처리한다.
  - 존재하면 Update, 없으면 Insert.
  - `existsByContentId` 조회 후 분기하거나 JPA `save` + unique 제약 활용.

### 4. 코드 품질 — DTO / Entity 분리
- **Entity**: DB 테이블 매핑 전용. 비즈니스 로직 포함 금지.
- **DTO**: API 요청/응답, 외부 API 파싱 전용. `@Entity` 금지.
- **Service**: 모든 비즈니스 로직은 서비스 레이어에서만 처리.
- Controller → Service → Repository 흐름을 반드시 지킨다. Controller에서 Repository를 직접 호출하지 않는다.

---

## 공공데이터포털 API

- 서비스: `한국관광공사_국문 관광정보 서비스_GW`
- 엔드포인트: `https://apis.data.go.kr/B551011/KorService1/`
- API 키는 `application.properties`에 `api.tour.key`로 주입한다.
- `RestClient` URI 빌더의 이중 인코딩을 방지하려면 URL 문자열을 직접 조합하고 `URI.create(url)`로 전달한다.
- 공공 API 응답에서 `items` 필드는 결과 없을 때 `""`(빈 문자열), 1건일 때 객체, 다건일 때 배열로 내려온다 — 커스텀 Jackson Deserializer로 처리한다.
- `jackson-databind`는 `spring-boot-starter-webmvc`에 포함되지 않으므로 `pom.xml`에 명시적으로 추가해야 한다...
