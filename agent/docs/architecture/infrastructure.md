# Infrastructure Architecture

## 1. Document Purpose

This document describes the infrastructure architecture of ClimbLog.

It focuses on:

- Application hosting
- Object storage
- CDN delivery
- Worker infrastructure
- Network boundaries
- Deployment configuration
- Environment configuration
- Infrastructure responsibilities
- Operational requirements

This document does **not** define:

- General application architecture
- KMP module structure
- Authentication implementation
- Database schema
- Video transcoding behavior

Those concerns are documented separately.

---

# 2. Infrastructure Overview

ClimbLog consists of several infrastructure components with distinct responsibilities.

| Component | Responsibility |
|---|---|
| Ktor Server | API and application backend |
| Cloudflare R2 | Object storage for media |
| Cloudflare CDN | Public media delivery |
| Oracle Cloud Worker | Video processing runtime |
| Relational Database | Persistent application data |
| Redis | Short-lived application state |
| Client Applications | Android / iOS / Desktop / Web clients |

The infrastructure is designed so that large media files do not need to pass through the Ktor API server during normal upload and playback.

At a high level:

```text
Client
  │
  ├── API requests ──────────────► Ktor Server
  │                                  │
  │                                  ├── Database
  │                                  └── Redis
  │
  ├── Media upload ──────────────► Cloudflare R2
  │
  └── Media playback ◄─────────── Cloudflare CDN
                                     │
                                     └── Cloudflare R2

Ktor Server
  │
  └── Internal Worker API
             │
             ▼
      Oracle Cloud Worker
             │
             └── Cloudflare R2
```

---

# 3. Infrastructure Responsibilities

Each infrastructure component should have a clearly defined responsibility.

## 3.1 Ktor Server

The Ktor server is the primary backend application.

Its infrastructure responsibilities include:

- Exposing public API endpoints
- Authenticating requests
- Authorizing application operations
- Managing application persistence
- Creating and coordinating transcoding jobs
- Generating object-storage upload information
- Providing internal APIs for the worker

The server does not perform long-running video transcoding itself.

---

## 3.2 Cloudflare R2

Cloudflare R2 provides object storage for media assets.

It stores:

- Original uploaded media
- Processed HLS media
- Generated thumbnails
- Other application-managed media objects when required

R2 is accessed through its S3-compatible API.

The server and worker may therefore use S3-compatible SDK functionality without requiring AWS S3 as the underlying storage service.

---

## 3.3 Cloudflare CDN

The CDN provides public delivery of processed media.

The intended flow is:

```text
Client
  │
  │ HTTPS
  ▼
CDN
  │
  ▼
R2
```

Clients should use the public CDN domain for playback rather than directly addressing the private object-storage endpoint.

The current public media domain is:

```text
https://media.climblog.io
```

The exact domain must be verified against the deployment configuration before making infrastructure changes.

---

## 3.4 Oracle Cloud Worker

The video worker runs as a standalone JVM application on Oracle Cloud ARM64 infrastructure.

Its responsibilities include:

- Polling for available transcoding jobs
- Claiming jobs through the internal server API
- Downloading source media
- Running FFmpeg processing
- Uploading processed media
- Reporting job completion or failure

The worker is intentionally separated from the Ktor application runtime.

Detailed processing behavior belongs to:

`agent/docs/architecture/video-pipeline.md`

---

# 4. Object Storage Architecture

## 4.1 Storage Model

R2 acts as the media storage layer.

The logical object structure is:

```text
R2 Bucket
├── raw/
└── processed/
    └── {videoId}/
```

The `raw/` prefix contains original uploaded media.

The `processed/{videoId}/` prefix contains generated media associated with a registered video.

The exact object naming rules are defined by the current implementation.

---

## 4.2 Raw Media

Raw media is uploaded directly from the client to R2.

The API server provides the information required for the client to perform the upload.

This avoids routing large video payloads through the application server.

The intended architecture is:

```text
Client
  │
  │ Direct upload
  ▼
R2
```

rather than:

```text
Client
  │
  ▼
Ktor Server
  │
  ▼
R2
```

This reduces unnecessary server bandwidth consumption and avoids coupling media transfer performance to API server capacity.

---

## 4.3 Processed Media

Processed media is written back to R2 by the worker.

The worker should not expose R2 credentials to clients.

The resulting objects are delivered through the CDN.

---

# 5. R2 Access

R2 credentials are server-side infrastructure secrets.

They must never be:

- Embedded in client applications
- Committed to source control
- Included in API responses
- Written to logs
- Exposed through debugging endpoints

The client receives only the minimum information required to perform an authorized upload.

---

# 6. CDN Architecture

## 6.1 Public Media Delivery

Processed media is intended to be publicly consumable through the configured CDN domain when the corresponding application resource is public.

The CDN provides:

- Edge delivery
- Reduced origin load
- Geographic distribution
- HTTP caching

The application should store or construct public media references according to the current media URL strategy.

---

## 6.2 CDN and Origin Separation

The CDN should be treated as the public delivery layer.

R2 remains the storage origin.

```text
Application
    │
    └── stores media metadata

Worker
    │
    └── writes media
            │
            ▼
           R2
            │
            ▼
           CDN
            │
            ▼
         Clients
```

The application should not require clients to know internal storage credentials or infrastructure endpoints.

---

# 7. Server Hosting

The Ktor server requires a JVM runtime and access to:

- Relational database
- Redis
- R2
- Worker internal API configuration
- Application secrets

The exact hosting provider and deployment mechanism are environment-specific.

The repository should define the application artifact and runtime configuration independently from the hosting platform wherever practical.

---

# 8. Worker Hosting

## 8.1 Runtime

The current worker deployment target is:

- Oracle Cloud
- ARM64 architecture
- Ubuntu 24.04 Minimal
- OpenJDK 21
- Standalone JVM application

The worker runs independently from the Ktor server.

---

## 8.2 Worker Process

The worker should run as a managed operating-system service rather than as a manually started terminal process in production.

The intended operational model is:

```text
Oracle VM
   │
   └── systemd
         │
         └── ClimbLog Worker
                 │
                 ├── Ktor internal API
                 ├── R2
                 └── FFmpeg
```

A worker process should automatically restart when appropriate and start automatically after VM reboot.

The exact service configuration belongs to the deployment environment.

---

# 9. Worker Runtime Dependencies

The worker requires:

- JVM
- FFmpeg
- FFprobe
- Network access to the Ktor server
- Network access to R2
- Writable temporary working directory

FFmpeg and FFprobe must be available to the worker process through the configured executable path.

The worker should validate required runtime dependencies during startup or deployment verification.

---

# 10. Worker Temporary Storage

Video processing requires local temporary disk space.

The worker may temporarily store:

- Downloaded source video
- FFmpeg intermediate data
- Generated HLS segments
- Generated playlists
- Generated thumbnails

Temporary files must not be treated as durable application storage.

After successful or failed processing, temporary files should be removed when safe to do so.

The worker's disk capacity must be sufficient for the largest expected processing workload.

---

# 11. Network Boundaries

The infrastructure contains several network boundaries.

## Public Access

Clients can access:

- Public API endpoints
- Public CDN media endpoints

## Internal Access

The worker communicates with the Ktor server through internal worker APIs.

The worker must authenticate these requests.

## Private Infrastructure

The following should remain server-side:

- Database credentials
- Redis credentials
- R2 credentials
- Worker authentication credentials
- Other infrastructure secrets

---

# 12. Worker Authentication

Worker-to-server communication requires a dedicated authentication mechanism.

The current architecture uses:

```text
X-Worker-Token
```

The worker token is an infrastructure secret.

It must be:

- Configured through the worker environment
- Stored securely
- Excluded from source control
- Excluded from logs
- Rotatable without source-code changes

The worker token must not be confused with user authentication tokens.

---

# 13. Environment Configuration

Infrastructure configuration should be supplied through environment-specific configuration.

The current worker configuration includes:

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

These values describe infrastructure configuration rather than application business logic.

Actual environment variable names in the repository remain authoritative.

---

# 14. Environment Separation

Different environments should use separate infrastructure resources where practical.

At minimum, avoid accidentally sharing production resources with local development.

Potentially isolated resources include:

- R2 buckets or prefixes
- Database instances
- Redis instances
- Worker authentication credentials
- CDN domains
- API endpoints

A local developer environment must not be able to accidentally overwrite production media or data through default configuration.

---

# 15. Secrets Management

Infrastructure secrets must not be stored in source code.

Sensitive values include:

- Database passwords
- Redis credentials
- R2 access keys
- R2 secret keys
- Worker authentication tokens
- JWT signing secrets
- External provider secrets

Configuration files containing real production secrets must not be committed to Git.

Example configuration files may contain placeholders, but never real credentials.

---

# 16. Deployment Artifacts

The backend and worker should produce deployable artifacts independently.

Typical artifacts include:

```text
Ktor Server
    └── JVM application artifact

Worker
    └── Standalone JVM application artifact
```

The worker must remain deployable without packaging the entire server application.

Likewise, server deployment should not depend on the worker's local runtime environment.

---

# 17. Deployment Process

A production deployment should generally follow this sequence:

1. Build the application artifact.
2. Run automated verification.
3. Deploy the server artifact.
4. Verify server startup.
5. Deploy the worker artifact when required.
6. Verify worker startup.
7. Verify infrastructure connectivity.
8. Verify application health.
9. Monitor logs and processing behavior.

Deployment-specific automation may later be implemented through CI/CD.

---

# 18. Worker Deployment Verification

A worker deployment is not complete merely because the JVM process starts.

Verification should confirm:

- JVM is available
- FFmpeg is available
- FFprobe is available
- R2 is reachable
- Ktor internal API is reachable
- Worker authentication succeeds
- Temporary directory is writable
- Worker can claim a job
- Worker can report job state

End-to-end media processing verification should follow the test strategy documented in `video-pipeline.md`.

---

# 19. Infrastructure Health

Infrastructure health should be observable at each major boundary.

Important boundaries include:

```text
Client → Ktor
Ktor → Database
Ktor → Redis
Ktor → R2
Worker → Ktor
Worker → R2
Worker → FFmpeg
CDN → R2
```

A failure at one boundary should be distinguishable from a failure at another.

---

# 20. Logging

Infrastructure logs should provide enough information to diagnose operational failures without exposing secrets.

Useful information includes:

- Service startup/shutdown
- Worker lifecycle
- Job identifiers
- Processing duration
- External service failures
- Connection failures
- Retry attempts
- Deployment version

Sensitive information must not be logged.

Examples of values that must not appear in logs:

- Passwords
- Access keys
- Secret keys
- Worker tokens
- JWT signing secrets
- Refresh tokens

---

# 21. Resource Management

Infrastructure resources should be sized according to actual workload.

Important worker resources include:

- CPU
- Memory
- Disk
- Network bandwidth

Video processing is CPU-intensive, while temporary media storage can consume substantial disk space.

The current worker concurrency is intentionally conservative.

Increasing concurrency should only be done after evaluating:

- CPU capacity
- Memory usage
- Disk usage
- Network throughput
- Processing duration
- Job contention

This is an operational scaling decision rather than a default configuration change.

---

# 22. Storage Lifecycle

Raw uploaded media may no longer be required after successful processing.

Raw-object cleanup should therefore be handled through an explicit lifecycle policy rather than relying indefinitely on application code.

The intended production strategy is to apply an object-storage lifecycle rule to old raw media.

For example:

```text
raw/{object}
      │
      └── Retention period
              │
              ▼
          Automatic deletion
```

The exact retention period must be configured and verified against the production storage policy.

Processed media should follow a separate retention policy because it is required for playback.

---

# 23. CDN Caching Considerations

CDN caching behavior must be compatible with media lifecycle and object naming.

Stable processed-object paths are useful for efficient caching.

When processed media is replaced, the application should avoid serving stale content unintentionally.

Changes to:

- Cache-Control
- TTL
- Cache invalidation
- Object naming

should be evaluated together.

Do not introduce cache invalidation behavior without first understanding the current CDN configuration.

---

# 24. Infrastructure Security Principles

Infrastructure changes must preserve the following principles:

1. Client applications receive no infrastructure credentials.
2. Database and Redis endpoints remain server-side.
3. R2 credentials remain server-side.
4. Worker APIs require dedicated authentication.
5. Secrets are provided through deployment configuration.
6. Public media delivery does not expose storage credentials.
7. Internal infrastructure endpoints are not unnecessarily exposed.
8. Production and development resources should remain isolated.
9. Logs must not contain secrets.
10. Infrastructure permissions should follow least privilege.

---

# 25. Infrastructure Change Guidelines

Infrastructure changes require repository and deployment-context inspection before implementation.

Examples include:

- Changing cloud providers
- Changing R2 configuration
- Changing CDN configuration
- Moving the worker
- Changing VM architecture
- Changing JVM versions
- Adding systemd services
- Changing deployment scripts
- Introducing CI/CD
- Changing network access rules
- Changing environment variables
- Changing storage lifecycle policies

Before implementation, the agent should determine:

- Current deployment configuration
- Existing infrastructure dependencies
- Required credentials
- Compatibility impact
- Migration requirements
- Rollback strategy
- Verification procedure

If the change exceeds the approved task scope, stop and request approval.

---

# 26. Current Infrastructure Principles

The current architecture follows these principles:

1. Ktor handles application APIs rather than long-running media processing.
2. R2 provides object storage for media.
3. Clients upload media directly to R2 when supported by the upload flow.
4. Processed media is delivered through the CDN.
5. The worker is an independently deployable JVM application.
6. The worker runs on Oracle Cloud ARM64 infrastructure.
7. Worker-to-server communication uses an authenticated internal API.
8. Infrastructure credentials remain outside client applications and source code.
9. Production worker processes should be managed by the operating system.
10. Temporary processing files are not durable storage.
11. Raw media should have an explicit lifecycle policy.
12. Infrastructure components should have clear operational boundaries.

---

# 27. Related Documents

For related architecture information, refer to:

- `agent/AGENT.md` — Agent operating rules
- `agent/docs/architecture/overview.md` — System-wide architecture
- `agent/docs/architecture/modules.md` — Module boundaries and dependencies
- `agent/docs/architecture/video-pipeline.md` — Video processing and worker behavior
- `agent/docs/architecture/authentication.md` — Authentication and authorization
- `agent/docs/architecture/database.md` — Database and Redis persistence
- `agent/docs/roadmap.md` — Planned infrastructure and production work

---

# 28. Source of Truth

The repository and actual deployment configuration are the final sources of truth.

This document describes the intended infrastructure architecture and provides context for Agents.

When this document differs from the actual infrastructure or repository configuration:

1. Inspect the current implementation and deployment configuration.
2. Confirm the discrepancy.
3. Do not silently assume that the documentation is correct.
4. Follow the approval workflow in `agent/AGENT.md` before changing infrastructure architecture.
5. Update this document when an approved change permanently changes the infrastructure architecture.