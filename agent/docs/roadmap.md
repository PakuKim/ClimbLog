# ClimbLog Roadmap

## 1. Document Purpose

This document defines the planned development roadmap for ClimbLog.

The roadmap describes:

- What should be completed next
- Why each task is required
- The priority of each task
- Dependencies between tasks
- Completion criteria
- Work that is intentionally deferred

This document is a **planning document**, not the source of truth for the current implementation.

The repository implementation is always authoritative. If the roadmap conflicts with the actual repository state, the repository must be inspected first and the roadmap must be updated when necessary.

Detailed architectural information is maintained separately under:

```text
agent/docs/architecture/
```

---

# 2. Roadmap Principles

## 2.1 Production Readiness First

The immediate objective is to make the existing architecture deployable and operational in a production-like environment before expanding the feature set.

Priority should therefore be given to:

1. Production infrastructure
2. Client completeness
3. Upload reliability
4. Observability and CI/CD
5. Scaling and feature expansion

---

## 2.2 Preserve the Current Architecture

The current KMP + Ktor + Worker + R2 + CDN architecture should be preserved unless implementation evidence demonstrates that a structural change is necessary.

Roadmap tasks must not be interpreted as permission to:

- Replace the current server framework
- Replace the current DI framework
- Redesign the KMP module architecture
- Rewrite the video player architecture
- Rewrite the video processing architecture
- Introduce unnecessary abstractions

Architectural changes require the approval process defined in `agent/AGENT.md`.

---

## 2.3 Repository Is the Source of Truth

Roadmap status must never override the actual implementation.

Before starting a roadmap task, the Agent must inspect:

1. Relevant source code
2. Build and configuration files
3. Existing tests
4. Related architecture documentation
5. Existing task artifacts

The Agent must determine whether the task is:

- Already completed
- Partially completed
- Not started
- Blocked
- No longer required

before making changes.

---

## 2.4 Minimal Change Principle

Each roadmap task should be implemented with the smallest reasonable change that satisfies its acceptance criteria.

Unrelated refactoring must not be included unless it is required to complete the task safely.

---

# 3. Priority Definition

| Priority | Meaning |
|---|---|
| P0 | Required for production readiness or blocks other major work |
| P1 | Important for core client completeness and reliability |
| P2 | Important for operational quality and development efficiency |
| P3 | Scaling, optimization, or future expansion |

Priority does not authorize implementation by itself. Normal Agent approval rules still apply.

---

# 4. Roadmap Overview

| Phase | Focus | Priority | Primary Goal |
|---|---|---:|---|
| Phase A | Production Infrastructure | P0 | Make the backend and video pipeline production-ready |
| Phase B | Client Completeness | P1 | Complete essential client functionality |
| Phase C | Upload Reliability | P1 | Make video upload and processing resilient |
| Phase D | Search, Observability & CI/CD | P2 | Improve usability, visibility, and deployment safety |
| Phase E | Scaling & Expansion | P3 | Scale processing and expand platform coverage |

Recommended execution order:

```text
Phase A
   ↓
Phase B
   ↓
Phase C
   ↓
Phase D
   ↓
Phase E
```

Some tasks within a phase may proceed independently when their dependencies allow it.

---

# 5. Phase A — Production Infrastructure

## Goal

Make the existing ClimbLog architecture operational in a production-like environment without redesigning the existing system.

## Priority

**P0**

## Status

Planned / partially implemented depending on the current repository and deployment environment.

The Agent must verify the actual state before starting each task.

---

## A.1 Production Relational Database

### Objective

Replace development-oriented H2 usage with a production-ready relational database configuration.

### Tasks

- Configure PostgreSQL for production.
- Configure HikariCP connection pooling.
- Separate local and production database configuration.
- Verify database initialization.
- Verify schema migration behavior.
- Verify application startup against an empty production database.
- Verify all currently required tables are created correctly.
- Verify transaction boundaries.
- Verify indexes and constraints required by the current schema.
- Remove accidental dependency on an in-memory H2 instance from production configuration.

### Acceptance Criteria

- The server can start against PostgreSQL without manual table creation.
- Required tables are created or migrated correctly.
- Application components use the same intended database instance.
- Transactions behave correctly.
- Production configuration does not depend on H2.
- Database credentials are provided through secure configuration rather than source code.

### Related Architecture

See:

```text
agent/docs/architecture/database.md
```

---

## A.2 Oracle Cloud Worker Deployment

### Objective

Deploy the standalone transcoding Worker to an Oracle Cloud ARM64 environment.

### Tasks

- Prepare Oracle Cloud ARM64 VM.
- Configure Ubuntu 24.04 Minimal.
- Install OpenJDK 21.
- Install FFmpeg.
- Install FFprobe.
- Deploy the Worker JAR.
- Configure Worker environment variables.
- Configure Worker working directory.
- Configure Worker authentication.
- Configure `systemd` service.
- Configure automatic Worker startup.
- Configure restart behavior.
- Verify Worker → Ktor internal API communication.
- Verify Worker → R2 communication.
- Verify FFmpeg execution on ARM64.
- Verify graceful failure and restart behavior.

### Required Configuration

The Worker configuration must support the currently defined environment variables, including:

```text
KTOR_BASE_URL
WORKER_AUTH_TOKEN
WORKER_ID
R2_ACCESS_KEY
R2_SECRET_KEY
R2_BUCKET
R2_ENDPOINT
R2_PUBLIC_BASE_URL
WORKER_POLL_INTERVAL_SECONDS
WORKER_LEASE_SECONDS
WORK_DIR
```

### Acceptance Criteria

- Worker starts automatically through `systemd`.
- Worker survives process restart.
- Worker can authenticate with the Ktor server.
- Worker can claim a transcoding job.
- Worker can download source media from R2.
- Worker can execute FFmpeg/FFprobe.
- Worker can upload processed media to R2.
- Worker can report completion or failure.
- Worker can recover from an unexpected process restart.
- No secrets are stored in the repository.

### Related Architecture

See:

```text
agent/docs/architecture/video-pipeline.md
agent/docs/architecture/infrastructure.md
```

---

## A.3 R2 Raw Media Lifecycle

### Objective

Prevent unused original video files from accumulating indefinitely.

### Tasks

- Configure lifecycle management for the `raw/` prefix.
- Define an appropriate retention period.
- Verify that processed media under `processed/` is not accidentally deleted.
- Document the lifecycle behavior.

### Initial Target

```text
raw/ → automatic cleanup after approximately 3 days
```

The exact retention period may be adjusted based on operational requirements.

### Acceptance Criteria

- Raw source media has an automatic cleanup policy.
- Processed media is excluded from the raw-media cleanup policy.
- Lifecycle configuration is documented.

---

## A.4 Production Configuration

### Objective

Separate environment-specific configuration from application code.

### Tasks

- Review all production-related environment variables.
- Separate local and production configuration.
- Remove hardcoded secrets and infrastructure credentials.
- Verify server configuration.
- Verify Worker configuration.
- Verify R2 configuration.
- Verify Redis configuration.
- Verify database configuration.

### Acceptance Criteria

- No production secrets are committed.
- Local development remains reproducible.
- Production configuration can be provided externally.
- Server and Worker configuration is clearly documented.

---

## A.5 End-to-End Production Video Pipeline Verification

### Objective

Verify the complete production video flow.

### Required Flow

```text
Client
  ↓
Ktor upload URL
  ↓
R2 direct upload
  ↓
Video registration
  ↓
TranscodingJob
  ↓
Worker claims job
  ↓
R2 download
  ↓
FFmpeg processing
  ↓
R2 processed media
  ↓
CDN
  ↓
Client HLS playback
```

### Acceptance Criteria

A real test video must successfully pass through the complete pipeline.

The verification must include:

- Upload
- Registration
- Job creation
- Worker claim
- Transcoding
- HLS generation
- Thumbnail generation
- R2 upload
- CDN access
- Client playback
- Job completion
- Video status transition

---

# 6. Phase B — Client Completeness

## Goal

Complete essential client functionality that is required for the Android/iOS application to provide the expected core experience.

## Priority

**P1**

---

## B.1 iOS Social Authentication

### Objective

Complete social authentication implementations that are currently missing or incomplete on iOS.

### Tasks

- Implement Google login on iOS.
- Implement Kakao login on iOS.
- Implement Naver login on iOS.
- Connect platform implementations to the shared authentication flow.
- Verify token exchange with the server.
- Verify account creation and existing-account login.
- Verify logout behavior.

### Acceptance Criteria

Each supported provider must be capable of completing:

```text
Social Login
    ↓
Provider Credential
    ↓
Server Authentication
    ↓
Application Session
```

without breaking the existing Android flow.

---

## B.2 Coil 3 Integration

### Objective

Complete image-loading implementation across client UI components.

### Tasks

Review and resolve remaining image-loading TODOs, including areas such as:

- Profile UI
- User items
- Video thumbnails
- Other image-consuming components discovered during repository inspection

### Acceptance Criteria

- Required images load correctly.
- Loading and error states are handled.
- Existing UI behavior is preserved.
- No unnecessary image-loading abstraction is introduced.

---

## B.3 Client Authentication Verification

### Objective

Verify authentication behavior consistently across platforms.

### Tasks

- Verify access-token handling.
- Verify refresh-token flow.
- Verify authentication state restoration.
- Verify logout.
- Verify unauthorized API responses.
- Verify token refresh failure behavior.
- Verify social login error handling.

### Acceptance Criteria

Authentication behavior is consistent with:

```text
agent/docs/architecture/authentication.md
```

---

# 7. Phase C — Upload Reliability & UX

## Goal

Make video upload reliable under real-world conditions such as network failure, user cancellation, application termination, and transcoding failure.

## Priority

**P1**

---

## C.1 Upload Progress

### Tasks

- Verify upload progress reporting.
- Ensure progress is tied to the actual upload lifecycle.
- Handle unknown or unavailable content length safely.
- Prevent UI state inconsistencies.

### Acceptance Criteria

Users can clearly determine whether an upload is progressing, completed, or failed.

---

## C.2 Upload Cancellation

### Tasks

- Support cancellation of an active upload.
- Cancel the underlying network operation.
- Update local UI state.
- Prevent a cancelled upload from being incorrectly registered as completed.
- Define behavior for partially uploaded objects.

### Acceptance Criteria

Cancelling an upload does not leave the client in an incorrect success state.

---

## C.3 Upload Retry

### Tasks

- Define retryable failures.
- Define non-retryable failures.
- Retry failed network operations where appropriate.
- Prevent duplicate video registration.
- Handle expired upload URLs.
- Request a new upload URL when required.

### Acceptance Criteria

Temporary network failures can be recovered without creating duplicate video records.

---

## C.4 Upload State Persistence

### Objective

Improve resilience against application termination or interruption.

### Tasks

- Determine which upload state must be persisted.
- Determine whether upload resumption is supported by the current R2 upload mechanism.
- Persist only the state required for safe recovery.
- Restore interrupted upload state where technically possible.
- Avoid introducing a complex background upload architecture unless required.

### Acceptance Criteria

The behavior of interrupted uploads is explicit and predictable.

If true resumable upload is not supported by the current protocol, the limitation must be documented rather than hidden behind an unreliable abstraction.

---

## C.5 Transcoding State UX

### Tasks

- Represent `PROCESSING`, `COMPLETED`, and `FAILED` states clearly.
- Refresh or observe processing status.
- Display meaningful failure states.
- Support retry behavior where the server permits it.
- Ensure stale processing state does not remain indefinitely without explanation.

### Acceptance Criteria

Users can understand the state of a registered video throughout the transcoding lifecycle.

---

## C.6 Duplicate Registration Protection

### Objective

Prevent client retries from creating duplicate video records.

### Tasks

- Inspect current registration semantics.
- Determine whether registration is idempotent.
- Introduce an idempotency mechanism only if required.
- Ensure client retry behavior is compatible with server behavior.

### Acceptance Criteria

A retry caused by a network timeout does not unintentionally create multiple logical videos.

---

# 8. Phase D — Search, Observability & CI/CD

## Goal

Improve product usability, operational visibility, and development/deployment safety.

## Priority

**P2**

---

# 8.1 Video Search

### Objective

Complete video search support across the application layers.

### Tasks

- Inspect current `SearchVideosUseCase`.
- Define the supported search semantics.
- Update repository interfaces if required.
- Update Data/Remote implementations.
- Update server query handling if required.
- Add appropriate database indexes.
- Add tests.

### Acceptance Criteria

A search query travels correctly through the relevant layers and returns the expected results.

The implementation must preserve the existing module boundaries.

---

# 8.2 Structured Logging

### Objective

Make server and Worker behavior easier to diagnose.

### Tasks

- Review current logging.
- Introduce structured log fields where useful.
- Include request/job identifiers where appropriate.
- Include Worker identifiers.
- Include transcoding job identifiers.
- Avoid logging tokens, credentials, or sensitive information.
- Standardize important failure messages.

### Acceptance Criteria

A failed transcoding job can be traced through relevant server and Worker logs without exposing secrets.

---

# 8.3 Error Tracking

### Objective

Detect and investigate production failures.

### Tasks

- Evaluate an error-tracking solution such as Sentry.
- Define server error reporting.
- Define Worker error reporting.
- Define client error reporting if required.
- Avoid duplicate or noisy error reporting.
- Document the selected solution.

### Acceptance Criteria

Unexpected production failures can be detected and investigated using the selected observability system.

The technology choice must be made based on the actual project requirements rather than added automatically.

---

# 8.4 GitHub Actions CI

### Objective

Automatically validate changes before merging.

### Tasks

- Configure repository checks.
- Build shared modules.
- Run unit tests.
- Build the server.
- Build the Worker.
- Validate relevant Android targets.
- Add static or formatting checks where appropriate.

### Acceptance Criteria

A pull request can automatically verify the relevant project components.

---

# 8.5 Deployment Automation

### Objective

Reduce manual deployment steps after CI is stable.

### Tasks

- Define server deployment strategy.
- Define Worker deployment strategy.
- Define artifact creation.
- Define deployment environment variables.
- Add deployment automation only after manual deployment is reproducible.

### Acceptance Criteria

Deployments are repeatable and do not require undocumented manual steps.

---

# 9. Phase E — Scaling & Expansion

## Goal

Scale the system only after the production architecture has been validated under realistic usage.

## Priority

**P3**

---

## E.1 Worker Concurrency

### Objective

Allow multiple transcoding jobs to be processed efficiently.

### Tasks

- Measure current Worker throughput.
- Identify CPU, memory, disk, and network bottlenecks.
- Determine safe concurrency for the Oracle ARM64 environment.
- Update job leasing behavior if necessary.
- Prevent duplicate job processing.
- Introduce configurable concurrency.
- Verify failure recovery under concurrent processing.

### Initial State

```text
Worker concurrency = 1
```

The current single-job model should not be replaced merely for theoretical scalability.

### Acceptance Criteria

Multiple jobs can be processed concurrently without:

- Duplicate processing
- Incorrect job state
- Resource exhaustion
- Corrupted output
- Lease inconsistencies

---

## E.2 Worker Scaling

### Objective

Support multiple Worker instances when a single Worker is no longer sufficient.

### Tasks

- Verify distributed job claiming.
- Verify lease ownership.
- Verify worker identity.
- Verify retry behavior.
- Verify concurrent workers.
- Evaluate horizontal Worker scaling.

### Acceptance Criteria

Multiple Workers can safely process jobs without processing the same job simultaneously.

---

## E.3 Web and Desktop Expansion

### Objective

Expand platform coverage after the core mobile experience is stable.

### Tasks

- Review Web feature parity.
- Review Desktop feature parity.
- Identify missing core features.
- Prioritize features based on actual requirements.
- Reuse shared domain/data logic where appropriate.
- Avoid forcing mobile-specific UI architecture onto other platforms.

### Acceptance Criteria

Platform expansion follows the existing KMP architecture without weakening module boundaries.

---

## E.4 Production Performance Optimization

### Objective

Optimize the system based on actual measurements.

### Potential Areas

- Database queries
- Database indexes
- Redis usage
- API response time
- R2 transfer
- CDN caching
- Worker processing
- FFmpeg performance
- Client rendering
- Image loading
- Memory usage

### Rule

Performance optimization must be evidence-driven.

Do not optimize components solely because they appear theoretically inefficient.

---

# 10. Task Dependencies

The major dependency relationships are:

| Task | Depends On |
|---|---|
| Production DB | Current DB architecture |
| Worker deployment | Existing Worker implementation |
| End-to-end video verification | Production DB + Worker + R2 + CDN |
| iOS social authentication | Existing shared authentication architecture |
| Upload retry | Current upload protocol |
| Upload persistence | Upload protocol and client lifecycle |
| Search | Existing repository/server architecture |
| Observability | Stable server/Worker runtime |
| CI | Stable project build |
| Deployment automation | Reproducible manual deployment |
| Worker concurrency | Stable single-worker job lifecycle |
| Worker scaling | Correct lease/claim semantics |
| Web/Desktop expansion | Stable shared architecture |

---

# 11. Recommended Execution Order

The recommended execution order is:

## Step 1 — Production Database

Complete PostgreSQL/HikariCP configuration and verify database initialization and migration.

## Step 2 — Worker Deployment

Deploy the existing Worker to Oracle Cloud and establish stable communication with the Ktor server and R2.

## Step 3 — Production Video Pipeline

Verify the complete upload → registration → transcoding → CDN → playback flow.

## Step 4 — R2 Lifecycle

Configure automatic cleanup for raw source media.

## Step 5 — Client Completeness

Complete iOS authentication and remaining core client TODOs.

## Step 6 — Upload Reliability

Improve cancellation, retry, interruption handling, and transcoding state UX.

## Step 7 — Search

Complete the existing search flow.

## Step 8 — Observability

Add structured logging and error tracking based on operational needs.

## Step 9 — CI/CD

Automate build, test, and deployment workflows.

## Step 10 — Scaling

Only after production usage and measurements justify it, increase Worker concurrency or add additional Workers.

---

# 12. Deferred / Explicitly Out of Scope

The following are intentionally deferred unless new evidence demonstrates that they are required.

## 12.1 Server Framework Replacement

Do not replace Ktor solely as an architectural preference.

---

## 12.2 DI Framework Replacement

Do not replace the current DI implementation without a concrete technical reason.

---

## 12.3 KMP Architecture Redesign

The existing module boundaries should be preserved.

---

## 12.4 Video Player Rewrite

Media3 and AVPlayer architecture should not be redesigned without concrete functional or performance requirements.

---

## 12.5 Video Pipeline Rewrite

The current:

```text
R2
→
Ktor
→
TranscodingJob
→
Worker
→
FFmpeg
→
R2
→
CDN
```

architecture should remain the baseline.

---

## 12.6 Premature Worker Scaling

Do not increase concurrency or introduce multiple Workers before measuring the limitations of the current implementation.

---

## 12.7 Unnecessary Abstractions

Do not introduce abstractions simply because they may be useful in a future architecture.

---

# 13. Definition of Done

A roadmap task is considered complete only when all applicable conditions are satisfied.

### Implementation

- Approved scope is implemented.
- Existing architecture boundaries are preserved.
- No unrelated changes are introduced.

### Testing

- Relevant automated tests pass.
- New behavior has appropriate test coverage where practical.
- Build verification is successful.

### Runtime Verification

For infrastructure or runtime tasks:

- The actual runtime behavior is verified.
- Relevant logs are inspected.
- Failure behavior is checked where necessary.

### Documentation

- Relevant architecture documentation is updated when the architecture meaningfully changes.
- Roadmap status is updated when the task changes roadmap state.
- Task-specific implementation details are recorded in the appropriate artifact.

### Reporting

The final report must include:

- What was changed
- Which files were changed
- Why the change was required
- Verification performed
- Test results
- Remaining issues
- Follow-up tasks

---

# 14. Roadmap Maintenance Rules

## 14.1 Status Must Reflect Reality

Each task should eventually be marked with an appropriate status:

```text
Planned
In Progress
Blocked
Completed
Deferred
Cancelled
```

Status must be based on repository and runtime evidence.

---

## 14.2 Completed Tasks Must Not Be Reimplemented

Before starting a roadmap task, inspect the repository to determine whether the task has already been completed.

Historical roadmap entries must not cause already-completed work to be repeated.

---

## 14.3 New Work Must Be Added Explicitly

If new requirements appear, add them to the roadmap rather than silently expanding an existing task.

If the new work changes architecture, use the architectural change escalation process defined in `agent/AGENT.md`.

---

## 14.4 Roadmap vs Architecture Documentation

The roadmap should describe **planned work**.

Architecture documentation should describe **current architecture**.

Do not copy detailed implementation descriptions from architecture documents into the roadmap unless they are necessary to define the task.

---

## 14.5 Task Artifacts

Detailed investigation, implementation reports, verification reports, and handoff information should be stored under:

```text
agent/artifacts/
```

The roadmap should reference those artifacts when useful rather than becoming a historical task log.

---

# 15. Current Strategic Direction

The overall direction of the project is:

```text
Complete the existing architecture
        ↓
Make the production environment reliable
        ↓
Complete essential client functionality
        ↓
Harden upload and processing reliability
        ↓
Improve observability and deployment automation
        ↓
Scale based on actual usage
        ↓
Expand platform coverage
```

The primary principle is:

> **Stabilize first, measure second, scale third.**

ClimbLog should not introduce architectural complexity before the current architecture has been validated in a real production-like environment.

---

# 16. Related Documents

### Agent Rules

```text
agent/AGENT.md
```

### Architecture

```text
agent/docs/architecture/overview.md
agent/docs/architecture/modules.md
agent/docs/architecture/video-pipeline.md
agent/docs/architecture/authentication.md
agent/docs/architecture/database.md
agent/docs/architecture/infrastructure.md
```

### Task Artifacts

```text
agent/artifacts/
```

---

# 17. Source of Truth

When determining the actual state of a roadmap task, use the following order of authority:

1. Repository implementation
2. Build and runtime configuration
3. Automated tests
4. Current architecture documentation
5. Roadmap
6. Historical task artifacts

The roadmap represents the intended direction of the project, but it must never override evidence from the repository or runtime.