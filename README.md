# KMP-Template

이 프로젝트는 Android, iOS, Web, Desktop (JVM), Server를 지원하는 Kotlin Multiplatform (KMP) 템플릿입니다.

## 🏗 프로젝트 구조 (Project Structure)

프로젝트는 모듈화되어 있으며, 각 플랫폼의 독립성을 유지하면서도 핵심 로직을 최대한 공유하도록 설계되었습니다.

- **`/:server`**: Ktor 기반의 백엔드 서버 모듈입니다.
- **`/:core`**: 모든 타겟(클라이언트 및 서버)에서 공유되는 핵심 로직과 공통 모델이 포함되어 있습니다.
- **`/:app:shared`**: Compose Multiplatform UI와 비즈니스 로직이 포함된 핵심 클라이언트 모듈입니다.
- **`/:app:androidApp` / `/:app:iosApp` / `/:app:desktopApp` / `/:app:webApp`**: 각 플랫폼별 엔트리 포인트입니다.
- **`/:build-logic`**: Gradle Convention Plugin을 통해 프로젝트 전반의 빌드 설정을 표준화합니다.

---

## 🛠 아키텍처 (Architecture)

### 1. Build Logic (Convention Plugins)
프로젝트의 빌드 설정 중복을 최소화하기 위해 `build-logic`을 사용합니다.
- **표준화**: Android, KMP, Compose, Serialization 등 공통 설정을 Plugin으로 정의하여 각 모듈의 `build.gradle.kts`를 간결하게 유지합니다.
- **유지보수**: 의존성 버전이나 공통 빌드 옵션을 한곳에서 관리합니다.

### 2. Clean Architecture (Client & Server)
클라이언트(`app:shared`)와 서버(`server`) 모두 **Clean Architecture** 원칙을 따라 레이어가 분리되어 있습니다.

#### [Client] `app:shared/commonMain`
- **`business/domain`**: 비즈니스 규칙과 레포지토리 인터페이스를 정의합니다.
- **`business/data`**: 레포지토리 구현체와 데이터 소스 제어를 담당합니다.
    - **`remote`**: Ktor Client를 사용한 API 통신.
    - **`local`**: DataStore나 SQLDelight를 사용한 로컬 영속성 관리.
- **`presentation`**: Compose Multiplatform 기반의 UI 레이어입니다.
- **`di`**: Koin을 사용하여 각 레이어의 의존성을 주입합니다.

#### [Server] `server`
- **`domain`**: 엔티티와 비즈니스 로직, 레포지토리 인터페이스를 포함합니다.
- **`data`**: 실제 데이터베이스(Exposed, Redis) 연동 및 레포지토리 구현부입니다.
- **`presentation`**: Ktor Route를 통한 API 엔드포인트 정의 및 요청 처리를 담당합니다.
- **`plugin`**: DI, 보안(JWT), 직렬화 등 Ktor의 핵심 기능을 설정합니다.

---

## 🛠 기술 스택 (Tech Stack)

### 공통 (Shared & Core)
- **Kotlin Multiplatform**: 플랫폼 간 코드 공유.
- **Koin**: 의존성 주입 (Dependency Injection).
- **Ktor Client**: 멀티플랫폼 네트워크 통신.
- **Kotlinx Serialization**: JSON 데이터 직렬화.
- **Compose Multiplatform**: 선언형 UI 프레임워크.

### 서버 (Server)
- **Ktor Server**: 비동기 서버 프레임워크.
- **Koin**: 서버 사이드 의존성 주입.
- **Exposed**: Kotlin SQL 라이브러리 (H2 Database 사용).
- **Redis (Lettuce)**: 리프레시 토큰 관리 및 캐싱.
- **JWT**: 인증 및 보안.

---

## 🚀 서버 설정 (Redis 설치 가이드)

서버 모듈은 리프레시 토큰 저장을 위해 **Redis**를 사용합니다. 서버를 실행하기 전 Redis가 설치 및 실행 중이어야 합니다.

### 🍎 macOS (Homebrew)
1. **설치**: `brew install redis`
2. **실행**: `brew services start redis`
3. **확인**: `redis-cli ping` -> `PONG`

### 🪟 Windows
#### 방법 1: WSL2 (추천)
- `sudo apt update && sudo apt install redis-server` 후 `sudo service redis-server start` 실행.
#### 방법 2: Docker
- `docker run --name redis -p 6379:6379 -d redis` 실행.

---

## 🏃 실행 방법 (Running the apps)

- **Server**: `./gradlew :server:run`
- **Android**: `./gradlew :app:androidApp:assembleDebug`
- **Desktop**: `./gradlew :app:desktopApp:run`
- **Web**: `./gradlew :app:webApp:wasmJsBrowserDevelopmentRun`
- **iOS**: Xcode에서 `/app/iosApp` 프로젝트 실행.

---