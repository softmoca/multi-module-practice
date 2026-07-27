# multi-module-practice

토스 테크 블로그 [테스트 의존성 관리로 높은 품질의 테스트 코드 유지하기](https://toss.tech/article/how-to-manage-test-dependency-in-gradle) 를 직접 손으로 확인해보기 위한 3모듈 Gradle 연습 프로젝트입니다.

`softmoca/spring-roomescape-waiting` 의 구조(순수 도메인 + 포트/어댑터 + JdbcTemplate)를 최소 크기로 옮겨왔습니다.

## 모듈 구성

| 모듈 | 역할 | 의존 |
| --- | --- | --- |
| `domain` | 순수 도메인. 스프링조차 없다 | 없음 |
| `db` | JdbcTemplate 기반 저장소 구현체 | `implementation project(':domain')` |
| `application` | 서비스와 main 함수 | `implementation project(':domain')`, `runtimeOnly project(':db')` |

의존 화살표는 항상 `domain` 쪽으로만 향합니다. `application` 이 `db` 를 `runtimeOnly` 로 의존하기 때문에, 서비스에서 `JdbcReservationRepository` 를 import 하면 컴파일 자체가 실패합니다. 계층 규칙이 사람의 주의력이 아니라 빌드로 강제됩니다.

## 디렉터리

```
settings.gradle
build.gradle                     공통 설정 (java, BOM, 테스트 의존성)
domain/
  build.gradle                   java-test-fixtures 적용
  src/main/java/roomescape/domain/reservation/
    Reservation.java             순수 도메인 객체
    ReservationRepository.java   포트(인터페이스)
  src/testFixtures/java/...
    ReservationFixtures.java     모듈 밖으로 공개되는 테스트용 빌더
  src/test/java/...
    ReservationTest.java         모듈 밖에서는 보이지 않는 실제 테스트
db/
  build.gradle                   java-test-fixtures 적용, H2는 testFixturesRuntimeOnly
  src/main/java/roomescape/db/JdbcReservationRepository.java
  src/main/resources/schema.sql
  src/testFixtures/java/roomescape/db/DatabaseCleaner.java
  src/test/java/roomescape/db/JdbcReservationRepositoryTest.java
application/
  build.gradle
  src/main/java/roomescape/RoomescapeApplication.java
  src/main/java/roomescape/application/ReservationService.java
  src/test/java/roomescape/application/ReservationServiceIntegrationTest.java
```

## 준비

Gradle Wrapper 바이너리는 웹으로 올릴 수 없어 포함되어 있지 않습니다. 클론 후 한 번만 실행해주세요.

```bash
gradle wrapper --gradle-version 8.13
./gradlew test
```

로컬에 Gradle이 없다면 IntelliJ로 열면 자동으로 내려받습니다. JDK 21이 필요합니다.

## 현재 상태

`main` 브랜치는 3단계까지 모두 적용된 완성 상태입니다. `./gradlew test` 가 전부 통과합니다. 학습은 이걸 일부러 깨뜨렸다가 되돌리는 방식으로 합니다.

### 1단계 - 벽에 부딪혀 보기

`domain/build.gradle` 의 `id 'java-test-fixtures'` 를 주석 처리하고 실행합니다.

```bash
./gradlew :db:test
```

`ReservationFixtures` 를 import 할 수 없다는 컴파일 에러를 확인합니다. `src/testFixtures` 를 `src/test` 로 옮겨도 결과는 같습니다. `src/test` 는 jar에 담기지 않아 모듈 밖으로 나가지 못하기 때문입니다.

### 2단계 - 테스트 클래스 공유 뚫기

플러그인을 되살리고, 소비하는 쪽 모듈에 한 줄을 선언합니다.

```groovy
testImplementation testFixtures(project(':domain'))
```

`domain/src/test/.../ReservationTest` 는 여전히 다른 모듈에서 보이지 않는다는 점도 함께 확인합니다. 공개되는 것은 `testFixtures` 에 둔 것만입니다.

### 3단계 - 테스트 전용 의존성 전파 뚫기

`db/build.gradle` 에서 H2 선언을 바꿔봅니다.

```groovy
testRuntimeOnly 'com.h2database:h2'          // db 테스트는 통과, application 테스트는 드라이버 없음
testFixturesRuntimeOnly 'com.h2database:h2'  // 둘 다 통과
```

`testRuntimeOnly` 로 두고 `./gradlew :application:test` 를 돌리면 H2 드라이버를 찾지 못해 실패합니다. `testFixturesRuntimeOnly` 로 바꾸고 `application` 에 아래를 선언하면 통과합니다.

```groovy
testImplementation testFixtures(project(':db'))
```

이때 `DatabaseCleaner` 도 함께 재사용됩니다. 저장소 세부사항(H2)을 application 이 직접 알지 않고도 테스트가 돌아간다는 것이 핵심입니다.

## 확인용 명령어

```bash
./gradlew :domain:build
unzip -l domain/build/libs/domain-0.0.1-SNAPSHOT.jar
unzip -l domain/build/libs/domain-0.0.1-SNAPSHOT-test-fixtures.jar

./gradlew :db:dependencies --configuration testCompileClasspath
./gradlew :application:dependencies --configuration testRuntimeClasspath
./gradlew test
```

## 한눈에 보는 정리

| 선언 | 언제 쓰나 |
| --- | --- |
| `testFixtures` 소스셋 | 여러 모듈이 공유할 테스트 전용 Builder/Helper 를 두는 자리 |
| `testImplementation testFixtures(project)` | 다른 모듈의 fixtures 클래스를 테스트 컴파일 시점에 쓸 때 |
| `testFixturesRuntimeOnly` | 테스트 전용 런타임 의존성을 모듈 밖으로 전파하고 싶을 때 |
| `testRuntimeOnly testFixtures(project)` | 의존 모듈의 테스트 전용 런타임 의존성만 물려받을 때 |
