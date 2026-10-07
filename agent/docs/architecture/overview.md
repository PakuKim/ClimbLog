# ClimbLog Architecture Overview

## 1. Document Purpose

This document describes the current high-level architecture of the ClimbLog system.

It provides a system-level overview of:

- Application targets
- Major system components
- Architectural layers
- Client and server responsibilities
- Media processing responsibilities
- Infrastructure boundaries
- Major data flows
- Relationships between architecture documents

This document should remain focused on the **current system architecture**.

Implementation history and completed development phases should be documented separately.

---

## 2. System Overview

ClimbLog is a Kotlin Multiplatform-based climbing video platform.

The system consists of:

- Android application
- iOS application
- Desktop application
- Web application
- Ktor backend server
- Standalone video-processing worker
- Shared Kotlin Multiplatform modules
- Relational database
- Redis
- Cloudflare R2 object storage
- CDN

The system separates client application logic, backend business logic, and video-processing infrastructure.

---

## 3. High-Level System Components

### Client Applications

ClimbLog provides multiple application targets.

| Application | Responsibility |
|---|---|
| Android | Android client application |
| iOS | iOS client application |
| Desktop | Desktop client application |
| Web | Web client application |

The client applications share common Kotlin Multiplatform code where appropriate.

Platform-specific functionality remains isolated behind platform boundaries.

---

### Shared Kotlin Multiplatform Layer

Shared modules contain reusable application logic.

Major responsibilities include:

- Domain models
- Business use cases
- Repository interfaces
- Repository implementations
- Local persistence
- Remote API communication
- Platform abstractions
- Navigation contracts
- Common UI components

The shared layer allows application targets to reuse business and data logic without coupling the domain layer to a specific platform.

---

### Ktor Server

The server provides the backend API.

Major responsibilities include:

- HTTP API
- Authentication
- Authorization
- User management
- Video registration
- Video metadata
- Social features
- Comments
- Likes
- Follow relationships
- Notifications
- Transcoding job management
- Object-storage integration
- Database access

The server does not perform CPU-intensive video transcoding as part of the normal API request lifecycle.

---

### Video Worker

The video worker is a standalone JVM application.

Its primary responsibility is media processing.

Major responsibilities include:

- Claiming transcoding jobs
- Downloading raw videos
- Inspecting source media
- Running FFmpeg
- Generating HLS media
- Generating thumbnails
- Uploading processed media
- Reporting job completion or failure

The worker communicates with the server through internal APIs.

It does not directly access the server's database implementation.

---

## 4. Architectural Layers

The client-side application follows Clean Architecture principles.

### Presentation

Responsible for:

- UI
- ViewModels
- UI state
- User interaction
- Navigation

Presentation code should not directly depend on infrastructure implementations.

---

### Domain

Responsible for:

- Business rules
- Domain models
- Use cases
- Repository interfaces

The domain layer should remain independent of:

- Android
- iOS
- Compose implementation details
- Room
- Network implementation
- Server implementation
- Database implementation

---

### Data

Responsible for:

- Repository implementations
- Data coordination
- Data-layer models
- Mapping between data sources and domain models

The data layer coordinates local and remote sources according to application requirements.

---

### Local

Responsible for:

- Local database
- DataStore
- Local persistence
- Local entities
- Local data sources

---

### Remote

Responsible for:

- HTTP communication
- API clients
- Remote data sources
- Remote response models
- Network configuration

---

### Platform

Responsible for genuinely platform-specific capabilities.

Examples include:

- Camera
- Video playback
- Native platform APIs
- Platform-specific services

Platform-specific implementations should not leak into domain logic.

---

## 5. Client Architecture

The client architecture follows a layered structure.

### Presentation

Feature modules provide application-facing UI and presentation logic.

Examples include:

- Main feature
- Onboarding feature

---

### Domain

Shared business logic is provided through the domain layer.

Feature modules consume domain use cases rather than directly accessing infrastructure implementations.

---

### Data

The data layer implements repository contracts and coordinates data sources.

---

### Local and Remote

Local and remote data sources provide persistence and network access respectively.

The data layer decides how these sources are combined.

---

## 6. Backend Architecture

The backend is separated into two major execution responsibilities.

### API Server

The Ktor server handles:

- Client requests
- Business operations
- Authentication
- Authorization
- Database transactions
- Job creation
- Job state management

### Worker

The worker handles:

- Media processing
- FFmpeg execution
- Media inspection
- Thumbnail generation
- Object-storage operations for processed media

This separation prevents long-running media processing from blocking API request handling.

---

## 7. Video Processing System

Video processing is an asynchronous workflow.

The major responsibilities are separated as follows.

### Client

The client:

- Requests a presigned upload URL
- Uploads raw video directly to object storage
- Registers video metadata with the server

### Server

The server:

- Generates the presigned upload URL
- Registers the video
- Creates a transcoding job
- Tracks transcoding state
- Provides playback metadata

### Worker

The worker:

- Claims the transcoding job
- Downloads the raw video
- Runs FFmpeg
- Generates HLS output
- Generates a thumbnail
- Uploads processed media
- Reports the final job state

### Object Storage

Object storage contains:

- Raw uploaded videos
- Processed HLS files
- Thumbnails

### CDN

The CDN provides delivery of processed media to clients.

The detailed media workflow is documented in:

`agent/docs/architecture/video-pipeline.md`

---

## 8. Authentication and Authorization

Authentication is handled by the backend and shared client infrastructure where appropriate.

The authentication architecture consists of:

- Client authentication flows
- Token management
- Server-side authentication
- Authorization checks
- Redis-backed shared authentication state where required
- Social authentication providers

Authentication and authorization are separate concerns.

The detailed authentication architecture is documented in:

`agent/docs/architecture/authentication.md`

---

## 9. Persistence

The backend uses a relational database for persistent application data.

Persistent data includes concepts such as:

- Users
- Social accounts
- Device tokens
- Follow relationships
- Videos
- Transcoding jobs
- Comments
- Likes
- Crux sections
- Notifications

Redis is used for state that benefits from an external shared in-memory store.

The detailed persistence architecture is documented in:

`agent/docs/architecture/database.md`

---

## 10. Infrastructure

The system uses external infrastructure for media storage, media processing, and content delivery.

Major infrastructure components include:

- Cloudflare R2
- CDN
- Oracle Cloud worker infrastructure
- FFmpeg
- Relational database
- Redis

The detailed infrastructure architecture is documented in:

`agent/docs/architecture/infrastructure.md`

---

## 11. Major Data Flows

### Application Data Flow

The general client data flow is:

```text id="y3b3qk"
Application
    |
    v
Presentation
    |
    v
Domain
    |
    v
Data
    |
    +------------------+
    |                  |
    v                  v
Local              Remote
                       |
                       v
                    Server
```

The actual implementation of each layer should be verified against the repository.

---

### Video Upload Flow

The high-level video upload flow is:

```text id="f2f5g9"
Client
    |
    v
Request Upload URL
    |
    v
Server
    |
    v
Presigned URL
    |
    v
Direct Upload
    |
    v
Object Storage
    |
    v
Video Registration
    |
    v
Transcoding Job
    |
    v
Worker
    |
    v
Processed Media
    |
    v
Object Storage
    |
    v
CDN
    |
    v
Client Playback
```

The detailed implementation is documented in:

`agent/docs/architecture/video-pipeline.md`

---

## 12. Responsibility Boundaries

The following boundaries are important to the current architecture.

### Client and Server

The client is responsible for presentation and user interaction.

The server is responsible for authoritative business operations and authorization.

The client must not be treated as the source of truth for protected business rules.

---

### Server and Worker

The server manages transcoding jobs.

The worker executes transcoding.

The worker must not depend directly on server implementation details.

---

### Application Server and Object Storage

Large media payloads should be transferred directly between the client and object storage using presigned operations.

The application server should not act as a proxy for large raw video uploads.

---

### Domain and Infrastructure

Domain logic should remain independent of infrastructure implementation details.

Infrastructure-specific implementations should remain behind appropriate boundaries.

---

## 13. Architecture Documentation Map

The architecture documentation is divided by responsibility.

### `overview.md`

Current high-level system architecture.

This document.

### `modules.md`

Detailed module structure and dependency relationships.

### `video-pipeline.md`

Video upload, transcoding, job processing, HLS, thumbnail, and playback media flow.

### `authentication.md`

Authentication, authorization, tokens, social login, and related security boundaries.

### `database.md`

Relational database, Redis, persistence models, transactions, and database access architecture.

### `infrastructure.md`

Cloudflare R2, CDN, Oracle worker infrastructure, FFmpeg, networking, and deployment-related architecture.

---

## 14. Architecture Change Considerations

When modifying the architecture:

1. Inspect the current repository implementation.
2. Read the relevant architecture documentation.
3. Identify all affected components.
4. Determine whether the existing architecture can support the requirement.
5. Prefer extending existing boundaries over introducing parallel patterns.
6. Update the relevant architecture documentation when the current architecture changes.

Architecture documentation should describe the current intended system, not historical implementation details.

---

## 15. Current Architecture Principle

The ClimbLog architecture is based on the following principles:

### Separation of Responsibilities

Each component should have a clear responsibility.

### Shared Business Logic

Common business and data logic should be shared through Kotlin Multiplatform where appropriate.

### Platform Isolation

Platform-specific capabilities should remain isolated.

### Asynchronous Media Processing

Video transcoding should remain outside the synchronous API request lifecycle.

### Direct Media Transfer

Large media files should be transferred directly through object storage rather than through the application server.

### Explicit Boundaries

Domain, data, infrastructure, server, and worker responsibilities should remain clearly separated.

### Repository as Source of Truth

Documentation describes the intended architecture, but the actual repository implementation is authoritative.

---

## 16. Related Documents

- Agent rules: `agent/AGENT.md`
- Module architecture: `agent/docs/architecture/modules.md`
- Video pipeline: `agent/docs/architecture/video-pipeline.md`
- Authentication: `agent/docs/architecture/authentication.md`
- Database: `agent/docs/architecture/database.md`
- Infrastructure: `agent/docs/architecture/infrastructure.md`
- Roadmap: `agent/docs/roadmap.md`
- Task Artifacts: `agent/artifacts/`