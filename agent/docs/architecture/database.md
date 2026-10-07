# Database Architecture

## 1. Document Purpose

This document describes the persistence architecture of ClimbLog.

It focuses on:

- Relational database responsibilities
- Redis responsibilities
- Database access boundaries
- Persistence model structure
- Transaction principles
- Connection management
- Migration principles
- Data consistency requirements
- Database-related operational considerations

This document does **not** define:

- General system architecture
- KMP module dependency rules
- Authentication flow
- Video processing flow
- Infrastructure topology

Those concerns are documented separately.

---

## 2. Persistence Overview

ClimbLog uses two primary persistence systems:

| System | Primary Responsibility |
|---|---|
| Relational Database | Durable application data |
| Redis | Short-lived and high-speed state |

The relational database is the authoritative persistent store for application entities.

Redis is used for data that benefits from fast access and does not need to be the permanent source of application records.

### Persistence Boundary

The general responsibility is:

```text
Ktor Server
    │
    ├── Relational Database
    │       └── Durable application state
    │
    └── Redis
            └── Temporary / high-speed state
```

The server is responsible for coordinating persistence operations.

Client applications must not access either persistence system directly.

---

# 3. Relational Database

## 3.1 Responsibility

The relational database stores durable application state that must survive application restarts and server restarts.

Examples include:

- Users
- Social accounts
- Device tokens
- Follow relationships
- Videos
- Transcoding jobs
- Video comments
- Video likes
- Video cruxes
- Notifications

The exact table set is defined by the repository's current Exposed table definitions.

---

## 3.2 Database Technology

The server uses Kotlin Exposed as the database access layer.

Database interaction should remain behind the server's data-access boundary.

The application should not expose Exposed-specific table objects to unrelated layers.

A typical persistence operation follows this pattern:

```text
Application / Use Case
        │
        ▼
Data Access
        │
        ▼
Exposed
        │
        ▼
Relational Database
```

The exact implementation should follow the repository rather than this conceptual model.

---

# 4. Current Relational Data Model

The current persistence model contains tables corresponding to the major application domains.

| Table | Responsibility |
|---|---|
| `UserTable` | User account information |
| `UserSocialAccountsTable` | Social authentication account associations |
| `UserDeviceTokenTable` | Device push notification tokens |
| `UserFollowTable` | User follow relationships |
| `VideoTable` | Registered video metadata and processing state |
| `TranscodingJobTable` | Asynchronous video processing jobs |
| `VideoCommentTable` | Video comments |
| `VideoLikeTable` | Video like relationships |
| `VideoCruxTable` | Video-specific crux / segment information |
| `NotificationTable` | User notification records |

The table names above describe the current architecture baseline. Repository definitions remain authoritative if the schema changes.

---

# 5. Entity Relationships

The database represents relationships between users, videos, and related resources.

At a conceptual level:

```text
User
 ├── Social Accounts
 ├── Device Tokens
 ├── Follow Relationships
 ├── Videos
 ├── Comments
 ├── Likes
 └── Notifications

Video
 ├── Transcoding Job
 ├── Comments
 ├── Likes
 └── Cruxes
```

These relationships should be enforced through database constraints where appropriate.

Application-level validation may complement database constraints, but should not be treated as a replacement for fundamental persistence integrity.

---

# 6. User Data

## 6.1 Users

`UserTable` represents the application's primary user entity.

The user entity should remain independent from any particular social authentication provider.

This allows a single application user to have multiple social authentication associations when supported by the application.

---

## 6.2 Social Accounts

`UserSocialAccountsTable` stores the relationship between an application user and an external social authentication identity.

Conceptually:

```text
User
  │
  ├── Google Account
  ├── Kakao Account
  └── Naver Account
```

The external provider identity must not replace the application's internal user identity.

---

## 6.3 Device Tokens

`UserDeviceTokenTable` stores device-specific push notification information.

Device tokens are application delivery data and are separate from authentication tokens.

The database should therefore treat:

- access tokens
- refresh tokens
- device push tokens

as different concepts with different lifecycles.

---

# 7. Video Persistence

## 7.1 Video Records

`VideoTable` stores persistent metadata for registered videos.

A video record may include information such as:

- User ownership
- Title / metadata
- Source object information
- Processed media information
- Thumbnail information
- Processing status
- Timestamps

The exact columns and types must be taken from the current table definition.

The database stores metadata and state; binary video content is stored in object storage rather than the relational database.

---

## 7.2 Transcoding Jobs

`TranscodingJobTable` represents asynchronous processing work associated with a video.

The job record provides persistent coordination between the API server and the standalone worker.

Conceptually:

```text
Video
  │
  └── TranscodingJob
          │
          ├── QUEUED
          ├── PROCESSING
          ├── COMPLETED
          └── FAILED
```

The worker uses job state stored by the server rather than maintaining the authoritative job queue only in memory.

Detailed worker behavior is documented in `video-pipeline.md`.

---

# 8. Comments, Likes, and Cruxes

## 8.1 Comments

`VideoCommentTable` stores comments associated with videos and users.

Comment persistence should preserve the relationship between:

- Comment author
- Target video
- Comment content
- Creation/update information where applicable

---

## 8.2 Likes

`VideoLikeTable` represents user-to-video like relationships.

Like operations should prevent unintended duplicate relationships through appropriate uniqueness constraints or equivalent application/database guarantees.

---

## 8.3 Video Cruxes

`VideoCruxTable` stores structured points or segments associated with a video.

Crux data belongs to the video domain and should remain separate from the physical media files stored in object storage.

---

# 9. Notifications

`NotificationTable` stores persistent notification records.

Notifications are application data and therefore differ from push delivery itself.

The distinction is:

```text
Notification Record
        │
        ├── Stored in relational database
        │
        └── May trigger push delivery
                  │
                  └── Device token
```

A push notification being delivered does not replace the persistent notification record when the application requires notification history.

---

# 10. Transactions

Database operations that must succeed or fail together should be performed within a single transaction.

A transaction should be used when multiple persistent changes represent one logical operation.

For example, video registration may require coordinated creation of:

```text
Video
+
TranscodingJob
```

These records should not be left in a partially-created state.

The required atomicity should be determined by the business operation rather than by individual SQL statements.

---

# 11. Transaction Boundaries

Transaction boundaries should remain close to the operation that requires atomicity.

Avoid unnecessarily large transactions that include:

- Network requests
- Long-running FFmpeg processing
- Object storage uploads
- External API calls
- Long waits

External operations should generally not be held inside a database transaction unless there is a specific consistency requirement that justifies it.

For asynchronous processing, persistent state transitions should be used to coordinate independently executed operations.

---

# 12. Connection Management

The server should use an explicit database connection pool in production.

The production configuration should provide:

- JDBC URL
- Database credentials
- Pool size
- Connection timeout
- Appropriate lifecycle management

The connection pool must be initialized once for the application lifecycle and shared by database operations rather than creating a new pool per request.

For production deployments, HikariCP is the intended connection-pooling mechanism.

---

# 13. Development Database

Development and local testing may use H2.

H2 is useful for:

- Local development
- Fast integration testing
- Schema experimentation
- Automated tests

However, H2 should not be treated as the production database.

Differences between H2 and the production relational database must be considered when validating:

- SQL behavior
- Constraints
- Indexes
- Data types
- Transaction behavior
- Migration behavior

Production compatibility should ultimately be verified against the actual production database engine.

---

# 14. Database Initialization

Database initialization and migration must operate against the same database connection configuration used by the application.

A common failure mode is:

```text
Migration
    │
    ▼
Database A

Application
    │
    ▼
Database B
```

This can occur particularly with in-memory H2 databases when different JDBC URLs or connection lifecycles create separate database instances.

Therefore, initialization code must ensure that:

1. The intended database instance is selected.
2. Schema initialization/migration executes against that instance.
3. Application queries use the same database instance.
4. Initialization completes before dependent database operations begin.

---

# 15. Schema Migration

Schema changes must be handled explicitly.

A migration should:

1. Identify the existing schema state.
2. Determine the required schema changes.
3. Apply only the required changes.
4. Verify the resulting schema.
5. Preserve existing data unless data migration explicitly requires otherwise.

Migration logic must not silently assume that the database is empty.

For development databases, an empty database may require initial table creation rather than incremental migration.

---

# 16. Migration Safety

Before modifying a production schema, consider:

- Existing data
- Existing indexes
- Foreign-key relationships
- Nullability changes
- Default values
- Unique constraints
- Column type compatibility
- Rollback implications
- Application compatibility during deployment

Destructive schema changes should require explicit justification.

Examples include:

- Dropping columns
- Dropping tables
- Removing constraints
- Changing existing data semantics

---

# 17. Redis

## 17.1 Responsibility

Redis is used for short-lived, high-speed application state.

The primary current responsibility is refresh-token state.

Redis should not become a general-purpose replacement for the relational database.

---

## 17.2 Refresh Token Storage

Refresh-token state is stored in Redis because it benefits from:

- Fast lookup
- Expiration support
- Simple invalidation
- Short-lived lifecycle management

The relational database remains responsible for durable user/account state.

Authentication-specific behavior is documented in `authentication.md`.

---

# 18. Redis Key Design

Redis keys should be:

- Deterministic
- Namespaced
- Easy to identify
- Stable across application instances

A conceptual namespace may follow:

```text
auth:{user-or-token-identifier}
```

The exact key format must follow the current implementation.

Agents must inspect the repository before changing Redis key formats because changing a key namespace can invalidate existing authentication state.

---

# 19. Redis Expiration

Redis entries representing temporary authentication state should use an appropriate TTL.

Expiration should be aligned with the lifecycle of the data.

For example:

```text
Refresh Token
     │
     └── Redis
          │
          └── TTL
               │
               └── Automatic expiration
```

TTL configuration must remain consistent with the application's token expiration policy.

---

# 20. Redis Consistency

Redis should not be treated as the authoritative source for durable user data.

If Redis becomes unavailable, the system should distinguish between:

- Data that can be reconstructed from the relational database
- Temporary state that can safely expire
- Authentication state whose absence requires re-authentication

Agents must not introduce Redis caching for persistent entities without evaluating:

- Cache invalidation
- Stale data
- Failure behavior
- Memory usage
- Key lifecycle
- Consistency requirements

---

# 21. Database Access Principles

Database-related code should follow these principles:

### Keep persistence behind a clear boundary

Application logic should not directly manipulate low-level database infrastructure unless the architecture explicitly requires it.

### Keep database models separate from domain models

Persistence representations should not automatically become domain models.

### Keep infrastructure details isolated

JDBC, Exposed, HikariCP, Redis clients, and connection configuration belong to infrastructure/data-access concerns.

### Prefer explicit operations

Database operations should clearly express their intent.

Examples:

- Find user
- Save user
- Find video
- Create transcoding job
- Update transcoding status
- Save comment

Avoid exposing generic database operations when a domain-specific operation provides a clearer boundary.

---

# 22. Indexing and Constraints

Database indexes should support actual query patterns.

Before adding an index, verify:

- The query exists.
- The query is performance-sensitive or sufficiently frequent.
- The index provides a meaningful benefit.
- The write overhead is acceptable.

Constraints should be used to enforce invariants that must always hold.

Examples include:

- Unique social account identifiers
- Unique user/video relationships where required
- Required foreign-key relationships
- Valid status values where supported by the database design

Do not add indexes or constraints speculatively.

---

# 23. Data Integrity

The database should protect critical invariants wherever practical.

Application validation and database constraints serve different purposes:

| Concern | Preferred Boundary |
|---|---|
| Request validation | Application |
| Business rules | Domain / Application |
| Referential integrity | Database |
| Uniqueness | Database + Application |
| Transactional atomicity | Database transaction |
| Temporary state expiration | Redis |

The database should remain the final enforcement layer for fundamental relational integrity.

---

# 24. Failure Handling

Database failures must not be hidden.

Relevant failures include:

- Connection failure
- Timeout
- Constraint violation
- Transaction rollback
- Migration failure
- Redis connection failure
- Redis timeout

The server should return appropriate application-level errors while preserving enough diagnostic information for server-side investigation.

Sensitive database credentials, tokens, and connection strings must never be logged.

---

# 25. Backup and Recovery

Production database operations should include a recovery strategy appropriate to the deployment environment.

The strategy should address:

- Automated backups
- Backup retention
- Restore testing
- Recovery Point Objective (RPO)
- Recovery Time Objective (RTO)

Backups should be treated as operational infrastructure rather than an application feature.

Redis data should not be assumed to provide the same durability guarantees as the relational database.

---

# 26. Database Configuration

Database configuration should be environment-specific.

Configuration must not be hard-coded into source code.

Typical configuration categories include:

```text
DATABASE_URL
DATABASE_USER
DATABASE_PASSWORD

REDIS_URL
REDIS_HOST
REDIS_PORT
REDIS_PASSWORD
```

The exact environment variable names are repository-defined and must be verified before use.

Production secrets must be supplied through the deployment environment or secret-management mechanism.

---

# 27. Local vs Production Environment

The persistence environment may differ by deployment stage.

| Environment | Relational Database | Redis |
|---|---|---|
| Local development | H2 or configured local DB | Local/configured Redis |
| Integration testing | Test database | Test Redis or isolated instance |
| Production | Production relational DB | Production Redis |

An agent must not assume that local persistence behavior represents production behavior.

When investigating database issues, always identify:

1. Database engine
2. JDBC URL / connection target
3. Database lifecycle
4. Migration state
5. Connection pool configuration
6. Redis endpoint
7. Redis key/TTL behavior

---

# 28. Architecture Change Guidelines

Any change to persistence architecture requires inspection before implementation.

Examples include:

- Replacing H2
- Introducing PostgreSQL
- Changing connection pooling
- Changing Exposed table structure
- Adding migrations
- Changing Redis usage
- Introducing a new cache
- Moving persistent data between systems
- Changing transaction boundaries

Before implementation, the agent must determine:

- Current implementation
- Existing data impact
- Required schema changes
- Compatibility concerns
- Migration strategy
- Rollback strategy
- Verification approach

If the change exceeds the approved task scope, stop and request approval.

---

# 29. Current Persistence Principles

The current architecture follows these principles:

1. Relational storage is the source of truth for durable application data.
2. Redis is used for short-lived or high-speed state.
3. Binary media is stored in object storage rather than the relational database.
4. Database operations are performed through server-side persistence boundaries.
5. Related writes requiring atomicity use transactions.
6. External network operations should not unnecessarily remain inside database transactions.
7. Production uses a connection pool.
8. Schema changes must be explicit and verifiable.
9. Redis entries require an intentional lifecycle and expiration policy.
10. Persistence infrastructure must remain isolated from client modules.

---

# 30. Related Documents

For related architecture information, refer to:

- `agent/AGENT.md` — Agent operating rules
- `agent/docs/architecture/overview.md` — System-wide architecture
- `agent/docs/architecture/modules.md` — Module boundaries and dependencies
- `agent/docs/architecture/video-pipeline.md` — Video processing persistence and worker coordination
- `agent/docs/architecture/authentication.md` — Authentication and Redis refresh-token usage
- `agent/docs/architecture/infrastructure.md` — Infrastructure and deployment architecture
- `agent/docs/roadmap.md` — Planned architecture and production work

---

# 31. Source of Truth

The repository implementation is the final source of truth.

This document describes the intended persistence architecture and provides architectural context for Agents.

When this document conflicts with the actual implementation:

1. Inspect the repository.
2. Confirm the discrepancy.
3. Do not silently assume either side is correct.
4. If the task requires changing the architecture, follow the approval workflow defined in `agent/AGENT.md`.
5. Update this document when an approved change permanently changes the persistence architecture.