# Authentication Architecture

## 1. Document Purpose

This document describes the authentication and authorization architecture of the ClimbLog system.

It covers:

- User authentication
- Access token management
- Refresh token management
- Social login
- Client authentication state
- Server-side authentication
- Redis-based refresh token storage
- Authentication boundaries between Client and Server
- Platform-specific social login considerations

This document describes the **current authentication architecture and its responsibilities**.

Agent workflow, approval rules, and implementation procedures are defined separately in:

`agent/AGENT.md`

---

## 2. Authentication Overview

ClimbLog uses a token-based authentication architecture.

The main authentication components are:

| Component | Responsibility |
|---|---|
| Client Application | Initiates login and stores authentication state |
| KMP Shared Layer | Provides shared authentication logic and contracts |
| Ktor Server | Authenticates users and issues tokens |
| Relational Database | Stores persistent user and social account information |
| Redis | Stores refresh token state |
| Social Providers | Authenticate users through external identity providers |

The authentication architecture separates:

- Identity verification
- Application user management
- Access token authentication
- Refresh token management
- Client authentication state

---

## 3. Authentication Flow

The general authentication flow is:

1. User selects a login provider.
2. Client performs provider-specific authentication.
3. Client obtains the provider credential or authorization result.
4. Client sends the authentication request to the Ktor Server.
5. Server validates the authentication information.
6. Server finds or creates the corresponding application user.
7. Server issues an access token.
8. Server issues a refresh token.
9. Refresh token state is stored server-side in Redis.
10. Client stores the authentication state required for subsequent requests.
11. Client uses the access token for authenticated API requests.

The exact provider-specific flow may differ depending on the platform and authentication provider.

---

## 4. Authentication Components

### Client

The Client is responsible for:

- Starting the login flow
- Interacting with platform-specific login APIs
- Obtaining provider credentials
- Sending authentication requests to the Server
- Maintaining the current authentication state
- Attaching access tokens to authenticated requests
- Refreshing authentication when required
- Clearing authentication state when the session becomes invalid

The Client should not determine whether a user is ultimately authorized to access protected backend resources.

---

### Server

The Ktor Server is responsible for:

- Validating authentication requests
- Verifying provider credentials where applicable
- Resolving application users
- Creating new users when required
- Issuing access tokens
- Issuing refresh tokens
- Validating access tokens
- Refreshing access tokens
- Revoking refresh token state
- Enforcing authorization rules

The Server is the authoritative component for application authentication and authorization.

---

### Database

The relational database stores persistent user-related information.

Examples include:

- User
- Social account
- Device token
- Follow relationships
- Other persistent user data

Authentication-related persistence that represents application state belongs in the relational database.

Short-lived authentication session state is handled separately through Redis.

---

### Redis

Redis is used for refresh token state.

This allows refresh tokens to be:

- Stored server-side
- Invalidated
- Expired
- Checked independently of the relational database

Redis is therefore part of the authentication session management infrastructure rather than the primary persistent user database.

---

## 5. Access Tokens

Access tokens are used to authenticate API requests after the user has successfully logged in.

The Client includes the access token when accessing protected endpoints.

The Server validates the token before allowing access to protected resources.

### Access Token Responsibilities

An access token should provide the information required to identify the authenticated principal and validate the request.

The Client should treat the access token as sensitive authentication material.

It must not:

- Log access tokens
- Include access tokens in analytics
- Expose tokens in debugging output
- Commit tokens to source control

---

## 6. Refresh Tokens

Refresh tokens are used to obtain a new access token when the current access token expires.

Unlike access tokens, refresh token state is maintained server-side through Redis.

The general refresh flow is:

1. Client detects an expired or invalid access token.
2. Client sends a refresh request.
3. Server validates the refresh token against server-side state.
4. Server issues a new access token.
5. Server updates refresh token state when required.
6. Client updates its authentication state.

The exact token rotation behavior must follow the current server implementation.

---

## 7. Redis Refresh Token Storage

Redis provides server-side refresh token state.

A refresh token record may contain information such as:

- User identity
- Token identifier
- Expiration
- Session information
- Device-related information where applicable

The exact Redis key and value structure is defined by the server implementation.

### Refresh Token Lifecycle

Refresh token state should have a bounded lifetime.

Expired authentication state must not remain indefinitely in Redis.

When a refresh token is revoked, its corresponding server-side state must no longer be accepted for authentication.

---

## 8. Token Security

Authentication tokens must be treated as secrets.

### Tokens Must Not Be Logged

Do not log:

- Access tokens
- Refresh tokens
- Social provider credentials
- Authorization codes
- Client secrets
- Worker authentication tokens

When authentication failures are logged, use safe identifiers such as:

- User ID
- Request ID
- Authentication method
- Error category

Do not include the credential itself.

---

## 9. Social Authentication

ClimbLog supports social authentication through external identity providers.

The current Android authentication providers include:

- Google
- Kakao
- Naver

The Client is responsible for initiating provider-specific authentication.

The Server is responsible for associating the authenticated external identity with an application User.

### Social Account Model

A user's external identity should be represented separately from the core User entity.

This allows a single application User to have one or more external authentication identities.

The project uses a dedicated social account persistence model for this purpose.

---

## 10. User and Social Account Separation

Authentication identity and application user identity are different concepts.

### User

The User represents the ClimbLog application account.

It contains application-level user information.

### Social Account

The Social Account represents an external identity associated with a User.

It may contain information such as:

- Provider
- Provider-specific user identifier
- Associated User ID

This separation allows authentication providers to change without changing the fundamental identity of the application user.

---

## 11. Social Login Flow

A typical social login flow consists of:

### Step 1 — Provider Authentication

The Client authenticates with the external provider.

### Step 2 — Provider Credential

The Client obtains the credential or authorization result required by the backend.

### Step 3 — Server Authentication

The Client sends the authentication request to the Ktor Server.

### Step 4 — External Identity Verification

The Server validates the external authentication information.

The exact verification method depends on the provider.

### Step 5 — User Resolution

The Server determines whether the external identity is already associated with an application User.

### Step 6 — User Creation

If no existing association exists, the Server may create the required User and Social Account records.

### Step 7 — Token Issuance

The Server issues application-level access and refresh tokens.

The Client then uses these tokens for normal authenticated API communication.

---

## 12. Android Social Authentication

Android-specific social authentication uses platform-supported authentication mechanisms and provider SDKs where required.

The shared authentication architecture should not contain Android UI implementation details.

Platform-specific login code belongs in the appropriate Android implementation layer.

The resulting authentication information is passed into the shared/server authentication flow through an explicit contract.

---

## 13. iOS Social Authentication

iOS social authentication requires platform-specific implementation.

The shared architecture should expose the necessary authentication abstraction while keeping provider-specific iOS implementation outside the common business layer.

### Current Implementation Consideration

The repository should be treated as authoritative for the current implementation status of:

- Google Login
- Kakao Login
- Naver Login

If an iOS provider implementation is incomplete, it should not be represented as fully implemented merely because the shared authentication interface exists.

---

## 14. Authentication State on Client

The Client needs to represent the user's current authentication state.

Typical states include:

| State | Meaning |
|---|---|
| Unauthenticated | No valid application session exists |
| Authenticating | Login or token restoration is in progress |
| Authenticated | A valid authenticated session exists |
| Refreshing | Authentication credentials are being refreshed |
| Authentication Failure | Session restoration or refresh failed |

The exact state model is determined by the current shared Client implementation.

Authentication state should be managed independently of individual screen implementations.

---

## 15. Authenticated API Requests

Authenticated API requests use the access token issued by the Server.

The networking layer is responsible for attaching authentication credentials to requests where required.

Feature modules should not manually construct authentication headers for every request.

This responsibility belongs to the shared networking/authentication infrastructure.

### Request Boundary

A typical authenticated request contains:

- API endpoint
- Request body or parameters
- Access token authentication

The Server validates the access token before executing protected operations.

---

## 16. Token Refresh

Access tokens are intentionally short-lived compared with long-lived application sessions.

When an access token expires, the Client should attempt token refresh rather than immediately treating the user as logged out.

The refresh operation should be centralized.

Individual Feature modules should not independently implement token refresh logic.

### Refresh Failure

If refresh fails because the refresh token is:

- Expired
- Revoked
- Invalid
- Missing
- Rejected by the Server

the Client should transition to an unauthenticated state and require the user to authenticate again.

---

## 17. Authentication vs Authorization

Authentication and authorization are separate concerns.

### Authentication

Authentication answers:

> Who is this user?

Examples:

- Validating a JWT
- Verifying a social provider credential
- Resolving a User ID

### Authorization

Authorization answers:

> Is this authenticated user allowed to perform this operation?

Examples:

- Accessing another user's private resource
- Editing a video
- Deleting a comment
- Updating a profile
- Accessing administrative functionality

A valid access token does not automatically grant permission to perform every operation.

---

## 18. Server Authorization

Authorization must be enforced by the Server.

The Client may hide unavailable UI actions for user experience, but this must not be treated as a security boundary.

For protected operations, the Server must validate:

1. The request is authenticated.
2. The authenticated User is identified.
3. The User has permission to perform the requested operation.
4. The target resource belongs to or is accessible by the User.

This prevents malicious clients from bypassing UI restrictions.

---

## 19. Authentication Boundary

The authentication architecture follows these boundaries:

| Responsibility | Owner |
|---|---|
| Provider-specific UI | Platform Client |
| Provider credential acquisition | Platform Client |
| Application authentication | Ktor Server |
| Application User | Relational Database |
| Social identity association | Relational Database |
| Access token validation | Ktor Server |
| Refresh token state | Redis |
| API authorization | Ktor Server |
| Authentication UI state | Client |

No single module should own responsibilities belonging to another layer.

---

## 20. Authentication Data Flow

### Login

The Client initiates provider authentication.

The resulting authentication information is sent to the Server.

The Server resolves the external identity, associates it with an application User, and issues application tokens.

### Authenticated Request

The Client sends an access token with the request.

The Server validates the token and resolves the authenticated User.

The requested operation is then subject to authorization checks.

### Token Refresh

The Client sends the refresh token when an access token needs to be renewed.

The Server validates the refresh token against server-side authentication state and issues a new access token when valid.

### Logout

Logout should invalidate the appropriate server-side refresh state and clear the Client's local authentication state.

The exact logout behavior should follow the current implementation.

---

## 21. Logout

Logout is responsible for ending the current application session.

A complete logout operation may include:

1. Revoking or invalidating refresh token state.
2. Clearing local authentication credentials.
3. Clearing in-memory authentication state.
4. Returning the application to an unauthenticated state.

The Server remains authoritative for server-side session invalidation.

Simply deleting a local access token is not sufficient when refresh token state remains valid on the Server.

---

## 22. Device Tokens

Authentication-related user data may also include device-specific notification information.

The project contains a dedicated device token model for this purpose.

Device tokens are conceptually separate from:

- Access tokens
- Refresh tokens
- Social provider credentials

A device notification token should never be treated as an authentication credential.

---

## 23. Error Handling

Authentication failures should be represented using explicit error categories.

Potential categories include:

- Invalid credentials
- Invalid provider credential
- Expired access token
- Expired refresh token
- Revoked refresh token
- Unauthorized operation
- Authentication service failure
- Network failure

The exact error model should follow the current Server and Shared implementation.

Authentication errors should not expose sensitive provider or token information to the Client unnecessarily.

---

## 24. Security Principles

The authentication architecture follows these principles:

1. **The Server is authoritative for application authentication.**
2. **The Server is authoritative for authorization.**
3. **Access tokens are treated as sensitive credentials.**
4. **Refresh token state is maintained server-side.**
5. **Refresh tokens are stored in Redis with bounded lifetime.**
6. **Social identities are separated from application User identity.**
7. **Provider-specific authentication remains platform-aware.**
8. **Feature modules do not implement authentication infrastructure independently.**
9. **Authentication credentials are never written to logs.**
10. **Client-side UI restrictions are not treated as security boundaries.**
11. **Authentication failures must not expose sensitive credentials.**
12. **Secrets are provided through secure configuration rather than source code.**

---

## 25. Architecture Change Considerations

Authentication changes can affect multiple parts of the system.

Before modifying authentication, inspect:

- Client authentication state
- Social login implementations
- Shared authentication contracts
- Remote API implementation
- Server authentication routes
- JWT configuration
- Refresh token handling
- Redis configuration
- User persistence
- Social account persistence
- Authorization checks
- Logout behavior

Changes to token structure may affect both Client and Server.

Changes to social account identity may affect:

- User creation
- Existing user lookup
- Account linking
- Login behavior
- Database constraints

Changes to refresh token handling may affect:

- Redis keys
- Session invalidation
- Token rotation
- Client refresh logic
- Logout behavior

Authentication changes should therefore be analyzed end-to-end before implementation.

---

## 26. Current Architecture Principles

The ClimbLog authentication architecture follows these principles:

1. Authentication is centralized around the Ktor Server.
2. External provider authentication is separated from application authentication.
3. Application Users are separate from external Social Accounts.
4. Access tokens authenticate API requests.
5. Refresh tokens maintain longer-lived sessions.
6. Redis stores refresh token state.
7. Authorization is enforced on the Server.
8. Client modules consume authentication functionality rather than reimplementing it.
9. Platform-specific provider integrations remain isolated from common business logic.
10. Authentication credentials are never exposed through logs or source control.

---

## 27. Related Documents

### System Architecture

`agent/docs/architecture/overview.md`

Describes the overall system architecture.

### Module Architecture

`agent/docs/architecture/modules.md`

Defines module responsibilities and dependency boundaries.

### Video Pipeline

`agent/docs/architecture/video-pipeline.md`

Describes video upload and transcoding architecture.

### Database

`agent/docs/architecture/database.md`

Describes relational database and Redis persistence architecture.

### Infrastructure

`agent/docs/architecture/infrastructure.md`

Describes infrastructure services and deployment architecture.

### Roadmap

`agent/docs/roadmap.md`

Describes planned improvements and future architectural work.

---

## 28. Source of Truth

This document describes the documented authentication architecture.

The repository implementation remains the final source of truth.

If the implementation and this document differ:

1. Inspect the current repository implementation.
2. Determine whether the difference is intentional.
3. Do not silently change the implementation to match this document.
4. Do not silently change this document to hide an architectural discrepancy.
5. If an architectural change is required, follow the approval process defined in `agent/AGENT.md`.
6. Update this document after an approved authentication architecture change has been implemented and verified.