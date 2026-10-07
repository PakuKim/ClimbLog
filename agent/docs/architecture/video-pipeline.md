# Video Pipeline Architecture

## 1. Document Purpose

This document describes the current ClimbLog video processing architecture.

It covers the complete video lifecycle from client upload to final playback.

The main areas covered are:

- Video upload
- Cloudflare R2 storage
- Video registration
- Transcoding job creation
- Worker job processing
- FFmpeg transcoding
- HLS generation
- Thumbnail generation
- Processed media storage
- CDN delivery
- Client playback
- Job state management
- Worker lease and retry behavior

This document describes **what the current video pipeline is**.

Agent workflow and modification rules are defined separately in:

`agent/AGENT.md`

---

## 2. Architecture Overview

ClimbLog uses an **asynchronous video processing architecture**.

The client uploads the original video directly to Cloudflare R2 instead of sending the video payload through the Ktor server.

The Ktor server is responsible for:

- Generating upload authorization
- Registering the uploaded video
- Creating the transcoding job
- Managing job state
- Providing internal APIs for the Worker

The standalone Worker is responsible for:

- Downloading the original video
- Analyzing the source
- Running FFmpeg
- Generating HLS output
- Generating thumbnails
- Uploading processed media
- Reporting job completion or failure

The high-level components are:

| Component | Responsibility |
|---|---|
| Android / iOS Client | Video selection, upload, registration, playback |
| Ktor Server | Upload authorization, video registration, job management |
| Cloudflare R2 | Raw and processed media storage |
| Oracle Cloud Worker | FFmpeg-based video processing |
| Cloudflare CDN | Processed media delivery |
| Android Media3 | Android video playback |
| iOS AVPlayer | iOS video playback |

---

## 3. End-to-End Video Flow

The complete video lifecycle consists of the following stages.

### Stage 1 — Request Upload Authorization

The client requests an upload URL from the Ktor server.

The server generates a presigned PUT URL for the raw video object.

The server also determines the object key that will be used for the uploaded file.

The raw object is stored under the `raw/` prefix.

### Stage 2 — Direct Upload to R2

The client uploads the video directly to Cloudflare R2 using the presigned PUT URL.

The video payload does **not** pass through the Ktor server.

This reduces:

- Server bandwidth usage
- Server memory pressure
- Request duration
- Backend involvement in large file transfers

### Stage 3 — Register Video

After the upload succeeds, the client sends a video registration request to the Ktor API.

The registration request contains the information required to associate the uploaded object with a new video.

The server validates the request and creates the corresponding database records.

### Stage 4 — Create Transcoding Job

Video registration and transcoding job creation are performed atomically.

The transaction creates:

- A `Video` record
- A `TranscodingJob` record

The newly created video starts in the `PROCESSING` state.

The transcoding job starts in the `QUEUED` state.

### Stage 5 — Worker Claims Job

The standalone Worker periodically requests an available job from the Ktor internal API.

The server atomically claims an eligible job and assigns it to the Worker.

The Worker receives:

- Job ID
- Video ID
- Input object key
- Output prefix
- Relevant processing information

### Stage 6 — Download Raw Video

The Worker downloads the original video from Cloudflare R2.

The raw file is stored temporarily in the Worker working directory.

### Stage 7 — Analyze Source

Before transcoding, the Worker uses `ffprobe` to inspect the source media.

Relevant information includes:

- Width
- Height
- Frame rate
- Audio availability
- Other media characteristics required for processing

The source properties are used to determine the appropriate output ladder.

### Stage 8 — FFmpeg Transcoding

The Worker runs FFmpeg to generate HLS output.

The processing pipeline supports:

- H.264 video
- AAC audio when audio exists
- Multiple resolutions
- HLS VOD
- Fixed segment duration
- Source frame-rate preservation within the configured limit
- Portrait video
- Videos without audio

### Stage 9 — Thumbnail Generation

The Worker generates a thumbnail from the processed video.

For very short videos, the Worker uses a fallback position when the preferred thumbnail timestamp is not available.

### Stage 10 — Upload Processed Media

The Worker uploads the generated files to R2 under the video's processed prefix.

The processed media includes:

- Master playlist
- Variant playlists
- Video segments
- Thumbnail

### Stage 11 — Complete Job

After all required output files have been uploaded successfully, the Worker calls the completion API.

The Server updates:

- Transcoding job status
- Video status
- HLS URL
- Thumbnail URL

The video becomes available for playback.

### Stage 12 — Client Playback

The client retrieves the processed HLS URL.

Android uses Media3 for playback.

iOS uses AVPlayer.

The HLS content is delivered through the Cloudflare CDN rather than directly from the application server.

---

## 4. Storage Architecture

Cloudflare R2 is used as the primary object storage for video media.

The storage is logically separated into raw and processed content.

### Raw Media

Original uploaded files use the:

`raw/`

prefix.

Example:

```text
raw/{videoId}_{filename}
```

The exact object key generated during upload is treated as authoritative and is stored with the transcoding job.

### Processed Media

Processed files use the:

`processed/{videoId}/`

prefix.

Example:

```text
processed/{videoId}/
```

This directory contains the generated HLS assets and thumbnail.

### Storage Responsibility

| Data | Storage | Purpose |
|---|---|---|
| Original video | R2 `raw/` | Source for transcoding |
| HLS master playlist | R2 `processed/` | Adaptive playback entry point |
| HLS variant playlists | R2 `processed/` | Resolution-specific playback |
| HLS segments | R2 `processed/` | Video media delivery |
| Thumbnail | R2 `processed/` | Video preview |

---

## 5. Upload Architecture

### Direct-to-R2 Upload

The client does not upload the raw video through the Ktor API.

The upload flow is:

1. Client requests a presigned PUT URL.
2. Server generates the URL and object key.
3. Client uploads the video directly to R2.
4. Client confirms registration through the video API.

This architecture separates:

- API traffic
- Large media transfer
- Video processing

### Why Direct Upload Is Used

Direct upload prevents the backend API from becoming a bottleneck for large media files.

The Ktor server does not need to:

- Receive the entire video body
- Buffer the video
- Temporarily store the raw video
- Forward the video to R2

The server remains responsible for metadata and job orchestration rather than large binary transfer.

---

## 6. Video Registration

Video registration is the boundary between upload and asynchronous processing.

The server creates the video and its processing job within the same database transaction.

### Transactional Records

A successful registration creates:

#### Video

Initial state:

`PROCESSING`

The processed URLs are initially unavailable until transcoding completes.

#### TranscodingJob

Initial state:

`QUEUED`

The job stores the exact input object key and output prefix required by the Worker.

### Important Invariant

A registered video should not exist in a state where its required transcoding job is missing.

Likewise, a transcoding job should not reference an unregistered video.

The transaction preserves this relationship.

---

## 7. Transcoding Job Model

The transcoding system is based on explicit job state.

### Job States

The current states are:

| Status | Meaning |
|---|---|
| `QUEUED` | Job is waiting to be processed |
| `PROCESSING` | Worker has claimed the job |
| `COMPLETED` | Processing finished successfully |
| `FAILED` | Processing failed and will not be retried further |

### State Lifecycle

The normal lifecycle is:

`QUEUED` → `PROCESSING` → `COMPLETED`

When processing fails:

`PROCESSING` → `FAILED`

Retry behavior may allow a failed processing attempt to be retried before the job reaches its final failure state.

---

## 8. Worker Job Claiming

The Worker does not access the database directly.

Instead, it communicates with the Ktor server through internal APIs.

### Claim API

The Worker uses:

`POST /internal/v1/transcoding/jobs/claim`

The Server is responsible for selecting and claiming an available job.

This prevents multiple Worker processes from processing the same job simultaneously.

### Worker Identity

Each Worker has a unique:

`workerId`

The claimed job records the Worker identity.

This allows the Server to determine which Worker currently owns the processing lease.

---

## 9. Worker Lease

Transcoding jobs use a lease mechanism to prevent permanently locked jobs.

A claimed job contains:

- `workerId`
- `lockedUntil`

The Worker receives a lease for a configured period.

The current default lease duration is approximately:

`600 seconds`

If the Worker fails unexpectedly and the lease expires, the Server can make the job eligible for recovery according to the job recovery policy.

### Purpose of the Lease

The lease protects against situations such as:

- Worker process crash
- VM restart
- Network interruption
- FFmpeg process failure
- Worker shutdown during processing

Without a lease, a job could remain permanently stuck in `PROCESSING`.

---

## 10. Internal Worker APIs

The Worker communicates with the Ktor server using dedicated internal endpoints.

### Claim Job

```text
POST /internal/v1/transcoding/jobs/claim
```

Used to acquire an available transcoding job.

### Complete Job

```text
POST /internal/v1/transcoding/jobs/{id}/complete
```

Used when all required processing and uploads have completed successfully.

### Fail Job

```text
POST /internal/v1/transcoding/jobs/{id}/failed
```

Used when processing fails.

The Worker does not directly manipulate Server database tables.

---

## 11. Worker Authentication

Worker APIs are protected separately from public client APIs.

The Worker sends an internal authentication token through:

```text
X-Worker-Token
```

The Server validates the token before allowing Worker operations.

### Security Requirements

Worker credentials must:

- Be provided through environment configuration
- Never be hardcoded
- Never be committed to the repository
- Never be printed in logs

The Worker authentication mechanism is intended for trusted internal communication rather than end-user authentication.

---

## 12. FFmpeg Processing

The Worker uses FFmpeg for media processing.

Before transcoding, the Worker uses `ffprobe` to determine source properties.

### Source Analysis

The Worker determines relevant properties such as:

- Source width
- Source height
- Source frame rate
- Audio availability
- Video orientation

These properties determine the output configuration.

---

## 13. Output Resolution Ladder

The standard target resolutions are:

- 1080p
- 720p
- 480p

The pipeline must **not upscale** videos.

### Resolution Selection

If the source resolution is smaller than a target resolution, that target is omitted.

For example:

| Source | Output |
|---|---|
| 1920 × 1080 | 1080p, 720p, 480p |
| 1280 × 720 | 720p, 480p |
| 640 × 360 | Source resolution |
| 1080 × 1920 | Portrait variants based on source dimensions |

The actual output dimensions are calculated from the source aspect ratio.

---

## 14. Frame Rate Handling

The processing pipeline limits output frame rate to a maximum of:

`30 FPS`

The source frame rate is preserved when it is below the configured maximum.

For example:

| Source | Target |
|---|---|
| 30 FPS | 30 FPS |
| 60 FPS | 30 FPS |
| 29.97 FPS | 29.97 FPS |

This prevents unnecessary frame-rate conversion when the source is already within the supported range.

---

## 15. HLS Configuration

The Worker generates HLS VOD output.

The current configuration uses approximately:

`6 seconds`

per HLS segment.

### HLS Characteristics

The generated media uses:

- HLS VOD
- Multiple variant playlists
- Master playlist
- Approximately 6-second segments
- Closed GOP
- Approximately 2-second GOP interval
- Scene-change threshold disabled for predictable segmentation

The master playlist references the available resolution variants.

---

## 16. Video Encoding

The current video encoding target is:

`H.264`

The pipeline is designed for broad compatibility with mobile playback environments.

### Audio

When the source contains audio, the output uses:

`AAC`

Videos without audio are also supported.

The Worker must not assume that every input contains an audio stream.

---

## 17. Aspect Ratio Handling

The transcoding pipeline preserves the source aspect ratio.

This is particularly important for portrait climbing videos.

For example, a portrait source may produce dimensions such as:

| Target Height | Example Output |
|---|---|
| 1080 | 608 × 1080 |
| 720 | 406 × 720 |
| 480 | 270 × 480 |

The output dimensions should be calculated from the source aspect ratio rather than using hardcoded landscape dimensions.

### Important Rule

The master playlist must contain the actual generated width and height.

Hardcoded landscape metadata must not be used for portrait outputs.

---

## 18. Thumbnail Generation

The Worker generates a thumbnail during video processing.

The preferred thumbnail position is approximately:

`1 second`

into the video.

For very short videos, the Worker uses a fallback timestamp that is valid for the available duration.

The generated thumbnail is uploaded to the processed media prefix.

---

## 19. Processed Media Layout

Processed media is stored under:

```text
processed/{videoId}/
```

The directory contains the HLS master playlist, variant playlists, segments, and thumbnail.

A conceptual structure is:

### Master Playlist

```text
processed/{videoId}/master.m3u8
```

### Variant Playlists

```text
processed/{videoId}/...
```

The exact variant filenames are determined by the Worker implementation.

### Thumbnail

```text
processed/{videoId}/...
```

The exact thumbnail filename is determined by the Worker implementation.

The repository implementation remains authoritative for exact filenames.

---

## 20. CDN Delivery

Processed media is delivered through the Cloudflare CDN.

The public media base URL is configured through:

`R2_PUBLIC_BASE_URL`

The current production-style public domain is:

`https://media.climblog.io`

The Server stores the resulting HLS and thumbnail URLs after successful processing.

### Delivery Architecture

The client does not request processed video data through Ktor.

Instead:

1. Client requests video metadata from the API.
2. API returns the processed media URL.
3. Client requests media through the CDN.
4. CDN serves the media from R2.

This keeps large media delivery outside the application server.

---

## 21. Video Status Management

The `Video` status represents the availability of the processed video.

### Processing

A newly registered video starts as:

`PROCESSING`

The video is not considered ready for normal playback until the Worker successfully completes processing.

### Completed

After successful transcoding and upload:

`COMPLETED`

The video has valid processed media URLs.

### Failed

If processing permanently fails:

`FAILED`

The application can use this state to represent a video that could not be processed successfully.

---

## 22. Retry Policy

Transcoding failures are retryable up to the configured maximum attempt count.

The current policy allows up to:

`3 attempts`

The exact retry transition must remain consistent between Server and Worker implementations.

Retries must not create duplicate active processing for the same job.

The lease and claim mechanism are responsible for preventing concurrent ownership of a job.

---

## 23. Worker Runtime

The Worker is designed to run as a standalone JVM process.

The current deployment target is:

### Oracle Cloud

`VM.Standard.A1.Flex`

### Operating System

`Ubuntu 24.04 Minimal`

### Architecture

`ARM64`

### JVM

`OpenJDK 21`

### Media Processing

`FFmpeg`

`ffprobe`

The Worker is intentionally separated from the Ktor Server so that video processing can consume CPU and disk resources without directly competing with API request handling.

---

## 24. Worker Configuration

The Worker uses environment-based configuration.

Important configuration values include:

| Variable | Purpose |
|---|---|
| `KTOR_BASE_URL` | Internal Ktor API base URL |
| `WORKER_AUTH_TOKEN` | Worker authentication token |
| `WORKER_ID` | Worker identity |
| `R2_ACCESS_KEY` | R2 access credential |
| `R2_SECRET_KEY` | R2 secret credential |
| `R2_BUCKET` | R2 bucket name |
| `R2_ENDPOINT` | R2 S3-compatible endpoint |
| `R2_PUBLIC_BASE_URL` | Public CDN/media base URL |
| `WORKER_POLL_INTERVAL_SECONDS` | Job polling interval |
| `WORKER_LEASE_SECONDS` | Job lease duration |
| `WORK_DIR` | Temporary processing directory |

Secrets must only be provided through secure environment configuration.

They must never be committed to source control.

---

## 25. Worker Concurrency

The current Worker is designed around a single active processing job.

The current concurrency target is:

`1`

This intentionally limits simultaneous FFmpeg workloads on the Oracle ARM64 instance.

Higher concurrency is a future optimization rather than a requirement of the current architecture.

Any change to concurrency should consider:

- CPU capacity
- Memory usage
- Temporary disk usage
- Network throughput
- FFmpeg process count
- R2 bandwidth
- Job lease duration
- Failure recovery

---

## 26. Temporary File Management

Video processing requires temporary local files.

The Worker uses `WORK_DIR` as its processing directory.

Temporary files must be cleaned up after processing.

Cleanup should occur even when processing fails.

The Worker should therefore perform cleanup in a `finally`-equivalent execution path.

This prevents failed jobs from gradually consuming all available local disk space.

---

## 27. Failure Handling

Failures may occur at multiple stages:

### Upload Failure

The client fails to upload the raw video.

The video registration request should not be considered successful merely because an upload URL was generated.

### Registration Failure

The server fails to create the Video and TranscodingJob transaction.

No valid processing job should be created outside the registration transaction.

### Claim Failure

The Worker cannot claim a job because of:

- Network failure
- Server failure
- Authentication failure
- No available jobs

The Worker should continue polling according to its configured interval.

### Processing Failure

FFmpeg or `ffprobe` may fail.

The Worker reports the failure to the Server.

### Upload Failure

The Worker may successfully transcode the media but fail to upload the generated files.

The job must not be marked as completed until all required outputs have been uploaded successfully.

### Completion Failure

The Worker may finish processing but fail to notify the Server.

The lease mechanism provides recovery protection for this situation.

---

## 28. Atomicity and Consistency

The video registration boundary is transactional.

The following records should be created atomically:

- Video
- TranscodingJob

The media upload itself is intentionally outside the database transaction because R2 object storage and the relational database do not participate in the same transaction.

Therefore, the system relies on explicit state transitions and job processing to maintain consistency.

### Important Invariants

The following conditions should remain true:

- Every processing Video has a corresponding TranscodingJob.
- Every TranscodingJob references a valid Video.
- A completed job has all required processed media.
- A completed Video has valid processed media URLs.
- The Worker never directly modifies the Server database.
- Raw media and processed media use separate storage prefixes.

---

## 29. Legacy Architecture

The previous video pipeline used AWS-managed transcoding infrastructure.

The legacy architecture included components such as:

- AWS MediaConvert
- MediaConvert queues
- SQS
- EventBridge
- Presigned POST upload flow

These components are no longer part of the current video processing architecture.

The current architecture uses:

- Cloudflare R2
- Presigned PUT upload
- Ktor job orchestration
- Standalone Oracle Cloud Worker
- FFmpeg
- Cloudflare CDN

### Legacy Dependency Principle

Legacy video-processing infrastructure should not be reintroduced unless there is an explicit architectural decision to do so.

Existing AWS SDK usage may remain where it is required for Cloudflare R2's S3-compatible API.

---

## 30. Observability Requirements

The current pipeline should expose enough information to diagnose processing failures without exposing secrets.

Useful operational information includes:

- Job ID
- Video ID
- Worker ID
- Processing stage
- Attempt count
- Processing duration
- FFmpeg exit status
- Output generation status
- Upload status
- Completion status

### Sensitive Information

Logs must not contain:

- R2 secret keys
- Worker authentication tokens
- Database credentials
- Other infrastructure secrets

Detailed observability and centralized logging are separate infrastructure concerns and may be expanded in future roadmap work.

---

## 31. Current Architecture Principles

The video pipeline follows these principles:

1. **Large media files bypass the application server.**
2. **The Ktor server manages metadata and job orchestration.**
3. **The Worker owns CPU-intensive media processing.**
4. **The Worker communicates with the Server through internal APIs.**
5. **The Worker does not directly access the Server database.**
6. **Video registration and job creation are atomic.**
7. **Job ownership is protected by a lease.**
8. **Transcoding does not upscale source media.**
9. **Source aspect ratio is preserved.**
10. **Output frame rate is limited to 30 FPS.**
11. **Processed media is delivered through the CDN.**
12. **Temporary Worker files are cleaned up after processing.**
13. **Secrets are provided through environment configuration.**
14. **Legacy MediaConvert/SQS/EventBridge processing is no longer part of the architecture.**

---

## 32. Architecture Change Guidelines

Changes to the video pipeline should be evaluated across the complete lifecycle.

Before modifying the pipeline, inspect:

- Client upload implementation
- Upload API
- Video registration API
- Database transaction
- `TranscodingJob`
- Internal Worker APIs
- Worker job claiming
- Lease handling
- FFmpeg processing
- R2 storage
- CDN URL generation
- Client playback

A change that appears local may affect multiple stages of the pipeline.

For example, changing the R2 object key format can affect:

- Upload
- Registration
- TranscodingJob
- Worker download
- Processed output
- Database URLs
- CDN delivery

Therefore, pipeline changes should be analyzed end-to-end before implementation.

---

## 33. Related Documents

### System Architecture

`agent/docs/architecture/overview.md`

Provides the overall system architecture and the relationship between Client, Server, Worker, and infrastructure.

### Module Architecture

`agent/docs/architecture/modules.md`

Defines module responsibilities and dependency boundaries.

### Authentication

`agent/docs/architecture/authentication.md`

Defines authentication and authorization architecture.

### Database

`agent/docs/architecture/database.md`

Defines relational database, Redis, and persistence architecture.

### Infrastructure

`agent/docs/architecture/infrastructure.md`

Defines R2, CDN, Oracle Worker, deployment, and infrastructure configuration.

### Roadmap

`agent/docs/roadmap.md`

Defines planned improvements and future work.

---

## 34. Source of Truth

This document describes the documented video pipeline architecture.

The repository implementation remains the final source of truth.

If the implementation and this document differ:

1. Inspect the current repository implementation.
2. Determine whether the difference is intentional.
3. Do not silently modify the implementation to match this document.
4. Do not silently modify this document to hide an architectural discrepancy.
5. If an architectural change is required, follow the approval process defined in `agent/AGENT.md`.
6. Update this document after an approved architecture change is implemented and verified.