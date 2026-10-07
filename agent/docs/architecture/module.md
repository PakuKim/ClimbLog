# Module Architecture

## 1. Document Purpose

This document describes the current module architecture of the ClimbLog project.

It defines:

- Module structure
- Module responsibilities
- Dependency direction
- KMP shared-layer architecture
- Feature and application module relationships
- Server and Worker boundaries
- `api` versus `implementation` dependency principles
- Model naming conventions
- Module boundary rules

This document describes **what the module architecture is**.

Agent behavior, approval rules, modification procedures, and workflow requirements are defined separately in:

`agent/AGENT.md`

---

## 2. Module Overview

ClimbLog is organized into the following major module groups.

| Group | Modules | Responsibility |
|---|---|---|
| Build Logic | `build-logic` | Gradle Convention Plugins and shared build configuration |
| Core | `core` | Project-wide foundational functionality |
| Shared | `shared:contract` | Shared contracts and DTOs |
| Shared | `shared:domain` | Domain models, repository interfaces, and use cases |
| Shared | `shared:data` | Repository implementations and data coordination |
| Shared | `shared:local` | Local persistence and caching |
| Shared | `shared:remote` | Remote API communication |
| Shared | `shared:platform` | Platform-specific capabilities |
| Shared | `shared:navigation` | Shared navigation definitions and contracts |
| Shared | `shared:ui-common` | Shared UI components and UI infrastructure |
| Feature | `feature:main` | Main application features |
| Feature | `feature:onboard` | Onboarding and authentication-related features |
| Application | `app:androidApp` | Android application entry point |
| Application | `app:iosApp` | iOS application entry point |
| Application | `app:desktopApp` | Desktop application entry point |
| Application | `app:webApp` | Web application entry point |
| Backend | `server` | Ktor backend API |
| Worker | `worker` | Standalone video transcoding worker |

---

## 3. Repository Directory Structure

The repository is organized into the following top-level directories.

### `build-logic/`

Contains Gradle Convention Plugins and shared build configuration.

### `core/`

Contains foundational functionality shared across appropriate modules.

### `shared/`

Contains Kotlin Multiplatform shared modules.

Current shared modules:

- `shared:contract`
- `shared:domain`
- `shared:data`
- `shared:local`
- `shared:remote`
- `shared:platform`
- `shared:navigation`
- `shared:ui-common`

### `feature/`

Contains application-level feature modules.

Current feature modules:

- `feature:main`
- `feature:onboard`

### `app/`

Contains platform-specific application entry points.

Current application modules:

- `app:androidApp`
- `app:iosApp`
- `app:desktopApp`
- `app:webApp`

### `server/`

Contains the Ktor backend application.

### `worker/`

Contains the standalone JVM video transcoding worker.

---

## 4. Architectural Layers

The Shared application architecture is organized into the following conceptual layers:

1. Presentation
2. Domain
3. Data
4. Infrastructure

### Presentation

Presentation is primarily represented by:

- `feature:main`
- `feature:onboard`
- `shared:ui-common`
- `shared:navigation`

Presentation is responsible for:

- UI
- User interaction
- UI state
- Navigation
- Calling domain use cases

Presentation should not directly manage infrastructure implementations.

### Domain

The Domain layer is represented by:

`shared:domain`

It contains:

- Domain models
- Use cases
- Repository interfaces
- Business rules
- Domain-level abstractions

The Domain layer must remain independent of infrastructure implementations.

### Data

The Data layer is represented by:

`shared:data`

It contains:

- Repository implementations
- Data coordination
- Cache policies
- Domain/Data model mapping
- Local and remote data source coordination

### Infrastructure

Infrastructure functionality is primarily represented by:

- `shared:local`
- `shared:remote`
- `shared:platform`

These modules provide concrete implementations for persistence, networking, and platform-specific capabilities.

---

## 5. Dependency Direction

The intended application dependency direction is:

### Feature to Domain

Feature modules depend on Domain functionality.

A Feature should obtain business behavior through Domain use cases rather than directly accessing infrastructure implementations.

### Domain to Infrastructure

Domain does not directly depend on infrastructure implementations.

Domain defines abstractions where necessary, while concrete implementations are provided by lower-level modules.

### Data to Local and Remote

Data coordinates Local and Remote data sources.

This allows Repository implementations to decide where data should be retrieved from or persisted.

### Platform Isolation

Platform-specific functionality is provided through `shared:platform`.

Only functionality that genuinely requires platform-specific implementation should be placed there.

---

## 6. Feature Modules

### `feature:main`

`feature:main` contains the primary application functionality.

Typical responsibilities include:

- Main screens
- Video-related screens
- Feed functionality
- Profile functionality
- Search functionality
- Other core service features

Feature code should focus on presentation and user interaction.

Business rules should be implemented in Domain use cases rather than duplicated inside Feature classes.

### `feature:onboard`

`feature:onboard` contains onboarding and authentication-related user flows.

Typical responsibilities include:

- Login
- Registration
- Initial configuration
- Social login UI
- Onboarding flow

Authentication business logic should remain outside the Feature layer.

The Feature layer should invoke the appropriate Domain use cases and expose their results through UI state.

---

## 7. Domain Module

### `shared:domain`

The Domain module contains the application's core business concepts.

Responsibilities include:

- Domain models
- Use cases
- Repository interfaces
- Business rules
- Domain-level abstractions

The Domain module should not contain implementation details related to:

- HTTP clients
- Database frameworks
- Room
- DataStore
- UI
- Android-specific APIs
- iOS-specific APIs
- Server implementation
- Worker implementation

### Repository Abstraction

When a Feature requires persistent or remote data, the Domain layer should depend on a repository abstraction.

For example:

```kotlin
interface VideoRepository {
    suspend fun getVideo(id: String): Video
}
```

The concrete implementation belongs to the Data layer.

This keeps the Domain layer independent of storage and networking implementations.

---

## 8. Data Module

### `shared:data`

The Data module connects Domain abstractions with concrete data sources.

Responsibilities include:

- Repository implementations
- Local and Remote data source coordination
- Cache strategy
- Data mapping
- Data synchronization
- Error conversion where appropriate

A repository implementation may coordinate:

- `shared:local`
- `shared:remote`

The Data layer should not contain UI-specific logic.

### Repository Implementation

The general responsibility relationship is:

- Domain defines the repository contract.
- Data implements the repository.
- Local provides local persistence.
- Remote provides remote communication.

This separation allows infrastructure implementations to change without requiring changes to Domain business rules.

---

## 9. Local Module

### `shared:local`

The Local module provides local persistence and caching functionality.

Responsibilities may include:

- Room
- DataStore
- Local data sources
- Local entities
- Local cache
- Local persistence configuration

The Local module should expose only the functionality required by its consumers.

### Local Boundary

Local should not depend on:

- Feature modules
- UI modules
- Remote implementations
- Domain presentation logic
- Platform modules unless a genuinely unavoidable platform dependency exists

In particular, Local should not become a general-purpose container for platform-specific functionality.

---

## 10. Remote Module

### `shared:remote`

The Remote module is responsible for communication with backend services.

Responsibilities include:

- HTTP client configuration
- API services
- Remote data sources
- Request models
- Response models
- Serialization
- Authentication headers
- Remote error handling

The current project uses Ktor Client for shared network communication where applicable.

Remote must remain independent of:

- Feature UI
- Presentation state
- Android UI implementation
- iOS UI implementation

---

## 11. Platform Module

### `shared:platform`

The Platform module contains functionality that genuinely requires platform-specific implementations.

Potential responsibilities include:

- Camera
- Video playback
- Platform-specific system APIs
- Platform-specific environment functionality

Kotlin Multiplatform `expect` / `actual` should be used only when platform-specific behavior is genuinely required.

### Platform Design Principle

Do not move common functionality into Platform merely because it is convenient.

Platform should not become a general-purpose utility module.

The goal is to keep the shared architecture platform-independent while providing explicit boundaries for unavoidable platform-specific functionality.

---

## 12. Navigation Module

### `shared:navigation`

The Navigation module provides shared navigation definitions and contracts.

Responsibilities may include:

- Routes
- Destinations
- Navigation arguments
- Shared navigation contracts
- Cross-feature navigation definitions

Navigation should not contain Feature-specific business logic.

Feature internals should remain inside their respective Feature modules.

---

## 13. UI Common Module

### `shared:ui-common`

The UI Common module contains reusable UI infrastructure and components.

Responsibilities may include:

- Shared UI components
- Common UI utilities
- Shared theme configuration
- Design system elements
- Reusable UI state components

Feature-specific components should remain inside their Feature module unless they have demonstrated reuse across multiple Features.

Avoid moving components into `ui-common` prematurely.

---

## 14. Application Modules

Application modules are platform-specific entry points that compose the shared and feature modules into runnable applications.

### `app:androidApp`

Responsible for:

- Android Application initialization
- Android-specific configuration
- Android dependency injection initialization
- Activity and application configuration
- Android platform integration

### `app:iosApp`

Responsible for:

- iOS application initialization
- Swift and KMP integration
- iOS-specific configuration
- iOS platform integration

### `app:desktopApp`

Responsible for:

- Desktop application initialization
- Desktop-specific configuration
- Desktop platform integration

### `app:webApp`

Responsible for:

- Web application initialization
- Web-specific configuration
- Web platform integration

Application modules should primarily act as **composition roots**.

They should not become containers for business logic that belongs in Domain or Feature modules.

---

## 15. Server Module

### `server`

The Server module contains the Ktor backend application.

Responsibilities include:

- HTTP API
- Authentication
- Authorization
- Database access
- Redis integration
- Video registration
- Transcoding job management
- Worker communication
- Backend-specific infrastructure

Server implementation details must not leak into Client Shared modules.

For example, Client modules should not directly depend on:

- Exposed tables
- Server database classes
- Server repositories
- Server services
- Server-specific infrastructure implementations

---

## 16. Worker Module

### `worker`

The Worker is a standalone JVM application responsible for asynchronous video processing.

Responsibilities include:

- Transcoding job polling and claiming
- Job lease handling
- Raw video download
- `ffprobe` analysis
- FFmpeg transcoding
- HLS generation
- Thumbnail generation
- Processed media upload
- Job completion reporting
- Job failure reporting
- Retry handling

The Worker communicates with the Server through explicitly defined internal APIs.

### Worker Independence

The Worker must not directly depend on Server implementation details.

The Worker should not access:

- Server repositories
- Server services
- Server Exposed tables
- Server database connections
- Server internal business implementations

This keeps the Worker independently deployable and allows its runtime environment to remain separate from the Ktor application.

---

## 17. Contract Module

### `shared:contract`

The Contract module contains models and definitions shared across architectural boundaries where a stable contract is required.

Examples include:

- Authentication contracts
- User contracts
- Video contracts
- Comment contracts
- Notification contracts
- Video status definitions

The Contract module should remain implementation-independent.

It should not contain:

- UI state
- Database entities
- Room annotations
- Server-specific persistence logic
- Platform-specific implementations

---

## 18. Model Naming Convention

The same conceptual entity may require different representations at different architectural layers.

ClimbLog uses explicit naming conventions to make these boundaries visible.

### Video Model

| Layer | Model |
|---|---|
| Domain | `Video` |
| Data | `VideoData` |
| Local | `VideoEntity` |
| Remote | `VideoResponse` |

This convention applies to other domain concepts where multiple representations are required.

### Example

For a User model:

| Layer | Model |
|---|---|
| Domain | `User` |
| Data | `UserData` |
| Local | `UserEntity` |
| Remote | `UserResponse` |

The exact models should follow the existing repository conventions.

Do not introduce a new suffix or naming pattern without first checking existing implementations.

---

## 19. Model Package Organization

Related models should be grouped by domain concept.

For example:

### Domain

```text
model/
  video/
    Video.kt
```

### Data

```text
model/
  video/
    VideoData.kt
```

### Local

```text
model/
  video/
    VideoEntity.kt
```

### Remote

```text
model/
  video/
    VideoResponse.kt
```

This package structure keeps related models together while preserving their architectural boundaries.

When adding a new model, follow the existing package organization of the corresponding module.

---

## 20. API vs Implementation Dependencies

Gradle `api` and `implementation` should be selected according to whether a dependency is part of the module's public API.

### Use `api` When

Use `api` when:

- A dependency's type is exposed through the module's public API.
- Consumers must directly reference the dependency's public types.
- The dependency is intentionally part of the module's public contract.

### Use `implementation` When

Use `implementation` by default when:

- The dependency is used only internally.
- Consumers do not need to know about the dependency.
- The dependency is an implementation detail.
- The dependency is used by an internal repository.
- The dependency is used by internal infrastructure.

The default should be:

`implementation`

Only promote a dependency to `api` when there is a concrete public API requirement.

---

## 21. Dependency Cleanup

Dependencies should be continuously reviewed for correctness and necessity.

The following should be checked during dependency cleanup:

- Unused dependencies
- Duplicate dependencies
- Unnecessary transitive dependencies
- `api` dependencies that can become `implementation`
- Dependencies placed in the wrong module
- Infrastructure dependencies exposed to Feature modules
- Libraries that are no longer used
- Dependencies that can be removed after architectural changes

Dependency cleanup should not be considered complete merely because the project still compiles.

The following should also be verified:

- Public API compatibility
- Transitive dependency changes
- Compile impact on consumers
- Runtime initialization behavior
- Platform-specific build behavior
- Dependency graph correctness

---

## 22. Dependency Rules

The following rules define the intended module boundaries.

| Module | May Depend On | Should Not Depend On |
|---|---|---|
| `feature:*` | Domain, Navigation, UI Common | Local, Remote, Server implementation |
| `shared:domain` | Minimal shared/core contracts | Data, Local, Remote, Feature, Platform |
| `shared:data` | Domain, Local, Remote | Feature, UI |
| `shared:local` | Minimal shared/core functionality | Feature, UI, Platform |
| `shared:remote` | Contract, Core, required shared functionality | Feature, UI |
| `shared:platform` | Core and required platform abstractions | Feature, Data |
| `shared:navigation` | Required shared contracts/UI infrastructure | Feature internals |
| `shared:ui-common` | Minimal shared/core functionality | Feature internals |
| `app:*` | Feature, Shared, Platform | Server implementation |
| `server` | Backend-specific dependencies | Client Feature implementation |
| `worker` | Worker-specific dependencies and required contracts | Server implementation and direct DB access |

These are architectural rules rather than a replacement for inspecting the actual Gradle dependency graph.

If the repository differs from these rules, the repository remains the final source of truth.

---

## 23. Dependency Injection Boundary

Dependency Injection should preserve the separation between abstraction and implementation.

The general responsibility is:

| Layer | Responsibility |
|---|---|
| Domain | Defines abstractions where required |
| Data | Provides Repository implementations |
| Local | Provides local data implementations |
| Remote | Provides remote data implementations |
| Platform | Provides platform-specific implementations |
| Application / Composition Root | Assembles concrete implementations |

DI modules should not become a place to hide architectural dependencies.

Avoid introducing abstractions solely for the purpose of making DI configuration appear cleaner.

---

## 24. Feature Dependency Principle

Feature modules should access business functionality through Domain use cases.

A typical dependency relationship is:

1. Feature invokes a Use Case.
2. Use Case operates on Domain models and abstractions.
3. Data provides the Repository implementation.
4. Repository coordinates Local and Remote data sources.

Feature modules should not directly access infrastructure implementations such as:

- Room DAO
- DataStore implementation
- Ktor API service
- Remote Data Source
- Database Entity
- Server DTO
- Infrastructure implementation

This prevents Feature modules from becoming tightly coupled to implementation details.

---

## 25. Module Boundary Principles

A module should have a clear responsibility and a meaningful reason to exist.

Before introducing a new module, evaluate:

1. Can the responsibility remain within an existing module?
2. Does the responsibility have an independent reason to change?
3. Is a new dependency boundary actually required?
4. Does the new module improve architectural clarity?
5. Does the additional build and dependency complexity provide meaningful value?

Do not create modules merely because a package contains many files.

Likewise, do not merge modules simply to reduce the module count if doing so would weaken architectural boundaries.

---

## 26. Architecture Change Considerations

Changes to module architecture should be evaluated against:

- Gradle dependency graph
- Convention Plugins
- `api` / `implementation` usage
- KMP source sets
- KMP targets
- Dependency Injection initialization
- Package structure
- Public APIs
- Test source sets
- Application entry points
- Server and Worker independence

A successful build alone is not sufficient to validate a module architecture change.

The dependency boundaries must also remain intentional and understandable.

---

## 27. Current Architecture Principles

The ClimbLog module architecture follows these principles:

1. Domain must remain independent of infrastructure implementations.
2. Feature modules should not directly access infrastructure implementations.
3. Data connects Domain abstractions with Local and Remote implementations.
4. Local and Remote should remain focused on their respective responsibilities.
5. Platform should contain only genuinely platform-specific functionality.
6. Application modules act primarily as composition roots.
7. Server and Worker remain independently deployable backend components.
8. `api` should only be used when a dependency is intentionally exposed.
9. Unused and unnecessary dependencies should be removed.
10. Unnecessary abstractions and module boundaries should be avoided.
11. Existing repository conventions should be preserved unless an approved architectural change requires otherwise.
12. The repository remains the final source of truth for the actual dependency graph.

---

## 28. Related Architecture Documents

The module architecture should be considered together with the following documents.

### System Overview

`agent/docs/architecture/overview.md`

Describes the overall ClimbLog system and the relationship between Client, Server, Worker, and infrastructure.

### Video Pipeline

`agent/docs/architecture/video-pipeline.md`

Describes video upload, storage, transcoding, processing, and playback architecture.

### Authentication

`agent/docs/architecture/authentication.md`

Describes authentication, authorization, token management, and social login architecture.

### Database

`agent/docs/architecture/database.md`

Describes relational database, Redis, persistence, and data storage architecture.

### Infrastructure

`agent/docs/architecture/infrastructure.md`

Describes Cloudflare R2, CDN, Oracle Worker infrastructure, deployment, and external services.

### Roadmap

`agent/docs/roadmap.md`

Describes planned improvements and future architectural work.

---

## 29. Source of Truth

This document describes the intended and documented module architecture.

The actual repository is authoritative.

When this document and the repository differ:

1. Inspect the repository.
2. Determine whether the difference is intentional.
3. Do not silently modify the architecture to match this document.
4. If an architectural change is required, follow the approval process defined in `agent/AGENT.md`.
5. Update this document when an approved change modifies the current module architecture.