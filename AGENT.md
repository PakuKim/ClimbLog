# AGENT.md - ClimbLog (Full-Stack KMP Video SNS Platform)

This document serves as the primary context, architecture guide, and rule set for AI coding agents working on the **ClimbLog** project.

---

## 1. Project Overview & Goal
* **App Name**: ClimbLog
* **Core Concept**: A video-focused SNS platform designed for climbers to share, analyze, and review climbing videos.
* **UI/UX Reference**: Instagram (Reels, Search Tab, Profile UI, Bottom Sheets).
* **Key Feature Highlights**:
    * Vertical short-form video feed with custom playback speed (0.5x ~ 2.0x).
    * Video section bookmarks (Crux section replay similar to YouTube Chapters).
    * Video upload with S3 Presigned URL + Native video picking.
    * Real-time push notifications via Firebase Cloud Messaging (FCM).
    * Multiplatform support (Android, iOS, Web, Desktop, Server).

---

## 2. Tech Stack & Infrastructure Specification

### Common & Client (`app:shared`)
* **Language & Platform**: Kotlin Multiplatform (KMP)
* **UI Framework**: Compose Multiplatform
* **Dependency Injection**: Koin
* **Networking**: Ktor Client (`ktor-client-core`, `ktor-client-cio`/`darwin`/`okhttp`)
* **Serialization**: `kotlinx.serialization` (JSON)
* **Pagination**: AndroidX Paging3 Multiplatform (`androidx.paging:paging-common`)
* **Local Storage**: Multiplatform DataStore / SQLDelight
* **Push Notification**: Firebase Cloud Messaging (FCM - Expect/Actual or KMP Firebase SDK)
* **Media Player**: Expect/Actual implementation
    * Android: Media3 / ExoPlayer
    * iOS: AVFoundation (`AVPlayer`)

### Server Software (`server`)
* **Framework**: Ktor Server (Async Engine)
* **Dependency Injection**: Koin (Server-side DI)
* **Database ORM**: Exposed ORM
* **Cache & Token Storage**: Redis using Lettuce client (Refresh Tokens, Caching)
* **Authentication**: JWT (JSON Web Token) with Access/Refresh Token cycle
* **Push Notification Engine**: Firebase Admin SDK (FCM payload dispatching)

### Server Infrastructure (AWS Cloud)
* **Compute / Server**: AWS EC2 or AWS ECS (Docker Container running Ktor Server)
* **Database**: AWS RDS (PostgreSQL for production / H2 for local dev)
* **In-Memory Cache**: AWS ElastiCache for Redis
* **Object Storage**: AWS S3 (Raw video & image storage, Presigned URL generation)
* **Video Encoding & Transcoding**: AWS Elemental MediaConvert (Converts raw MP4 to HLS `.m3u8` streaming format)
* **CDN (Content Delivery Network)**: AWS CloudFront (Fast streaming & caching for video/image assets)
* **Push Messaging**: Firebase Cloud Messaging (FCM Infrastructure)

---

## 3. Project Architecture & Conventions

### A. Build Logic (Convention Plugins)
* **Directory**: `build-logic/`
* **Rule**: Avoid repeating build scripts across Gradle modules. Define reusable plugins under `build-logic` (e.g., `climblog.kmp.library`, `climblog.compose`, `climblog.ktor.server`).

### B. Module & Layer Structure (Clean Architecture)
ClimbLog/
├── build-logic/            # Gradle Convention Plugins
├── app/
│   ├── androidApp/        # Android Entry Point
│   ├── iosApp/            # iOS Entry Point
│   └── shared/            # KMP Shared Logic
│       ├── commonMain/
│       │   ├── business/
│       │   │   ├── domain/       # Entities, UseCases, Repository Interfaces
│       │   │   └── data/         # Repository Impl, Remote/Local Data Sources
│       │   │       ├── remote/   # Ktor Client API Services
│       │   │       └── local/    # DataStore / SQLDelight
│       │   ├── presentation/     # Compose Multiplatform UI, ViewModels, UI State
│       │   └── di/               # Koin Modules (Client)
│       ├── androidMain/          # Android Actual Implementations (ExoPlayer)
│       └── iosMain/              # iOS Actual Implementations (AVPlayer)
└── server/                       # Ktor Backend Project
├── src/main/kotlin/
│   └── com/climblog/
│       ├── domain/           # Server Domain Models & Repositories
│       ├── data/             # Exposed Tables, Redis Client, S3 Client
│       ├── presentation/     # Ktor Routing / Endpoint Handlers
│       └── plugins/          # Ktor Server Configuration (JWT, DI, Routing)

---

## 4. Coding Standards & AI Constraints

### DO'S
1. **Unify UI State**: Manage UI state using `StateFlow<UiState>` inside ViewModels. Use Kotlin `sealed interface` for State and Intent/Event.
2. **Expect/Actual Boundaries**: Use `expect/actual` ONLY for native capabilities (e.g., Video Player, Social Auth SDK, System Share Sheet). Keep business logic in `commonMain`.
3. **Presigned URL Video Upload Workflow**: Video uploads MUST NOT pass raw video bytes directly through the Ktor Server.
  - **Step 1**: Client requests Presigned URL from Ktor Server.
  - **Step 2**: Ktor Server generates S3 Presigned URL via AWS SDK.
  - **Step 3**: Client uploads video directly to AWS S3 using Ktor Client streaming/chunked upload.
  - **Step 4**: S3 triggers AWS MediaConvert to transcode the video into HLS (`.m3u8`) for streaming via CloudFront.
4. **Resilient Error Handling**: Wrap all repository and API calls in `Result<T>` or custom Domain Error types.
5. **Clean Dependency Separation**: `domain` layer must not depend on `data` or `presentation` layers.

### DON'TS
1. **NO Android Context in Common Logic**: Never pass `android.content.Context` into `commonMain` ViewModels, UseCases, or Repositories.
2. **NO Hardcoded Credentials**: AWS Access Keys, S3 Bucket names, JWT Secrets, and DB credentials must be loaded via Environment Variables or `local.properties`.
3. **NO Blocked Threads**: Avoid blocking IO calls (`Thread.sleep()`). Always use Coroutines with proper Dispatchers (`Dispatchers.IO`).
4. **NO Direct Database Queries in Server Routes**: Always route requests through UseCases or Repository interfaces in `server`.

---

## 5. Screen Scenarios & Requirements

### ① Splash & Onboarding
* **Splash**: Check JWT validity and route to Main or Login.
* **Social Login**: Google, Kakao, Naver.
* **Additional User Info (Registration Step)**:
  * Profile Photo (Nullable)
  * Unique Handle/ID (Must perform duplication check API)
  * Gender, Age, Height, Arm Reach (For climbing stats)
  * If social login provides data, pre-fill and disable textfields.

### ② Main - Home (Instagram Reels Style)
* **Top Bar**: Notification icon (Red badge when unread notifications exist -> Navigate to Notification List).
  * Use FirebaseMessaging for notifications.
  
* **Feed Video Player**:
  * Vertical Paging (Paging3 Multiplatform).
  * Auto-play current item, pause others. Click to pause/play.
  * Native video rendering via `expect/actual` (Media3 ExoPlayer / AVPlayer).
  * Replay "Crux Section" (YouTube chapter style overlay).
  * Video playback speed controller (0.5x to 2.0x).
  * Interaction: Like button toggle, Comments (Pop-up BottomSheet), OS Native Share Sheet.

### ③ Main - Search
* Search Bar for `userId` / `userName`.
* Random Video Thumbnail Grid before search query input.
* Clicking profile opens the target user's Profile screen.

### ④ Main - Profile
* Top Bar:
  * My Profile: (+) Add Video icon (Navigate to Upload screen), Hamburger Menu.
  * Target User Profile: Follow/Unfollow button.
* Display User Info (Height, Arm Reach, Stats) & Uploaded Videos Grid.

---

## 6. Prompt Execution Instructions for AI Agent
When writing or refactoring code:
1. Always follow Clean Architecture boundaries.
2. Ensure Compose UI code uses design tokens (Colors, Typography) modeled after Instagram's dark/light design system.
3. Keep server routing modularized inside `server/presentation/routes/`.
4. Ensure code compiles for both `androidMain` and `iosMain` targets when modifying `commonMain`.
