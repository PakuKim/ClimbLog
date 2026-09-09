# S3 POST Object Upload Implementation Plan (Ktor Multipart)

Transition from manual S3 Multipart Upload to a simplified S3 POST Object flow using Ktor Client's native Multipart support.

## User Review Required

> [!IMPORTANT]
> - **Simplified Flow**: We will use S3 POST Object API which allows uploading a file via `multipart/form-data`.
> - **Server-Independent**: Video bytes will still go directly from Client to S3, keeping the Ktor Server's bandwidth free.
> - **Ktor Integration**: The client will use `MultiPartFormDataContent` to handle the heavy lifting of multipart construction and streaming.

## Proposed Changes

### 1. Domain & Models
- [NEW] `S3PostModels.kt`: `PresignedPostRequest` and `PresignedPostResponse` (URL + Form Fields).
- [DELETE] `MultipartUploadModels.kt`: No longer need `uploadId`, `partNumbers`, etc.

### 2. S3 Provider (Server)
- **[MODIFY]** `S3Provider`: Replace multipart initiation with `generatePresignedPost`.
- **[MODIFY]** `S3ProviderImpl`: Use AWS SDK to generate a POST Policy and Signature. This provides the client with required fields like `AWSAccessKeyId`, `policy`, and `signature`.

### 3. Ktor Routes (Server)
- **[MODIFY]** `VideoRoute.kt`:
  - Replace `/uploads/initiate` with `/uploads/presigned-post`.
  - Simplify completion logic.

### 4. Client Repository & Data Source
- **[MODIFY]** `VideoRepository` & `VideoRemoteDataSource`:
  - Add `getPresignedPost` and `uploadVideoToS3Multipart`.
  - Remove all manual part-management methods.
- **[MODIFY]** `VideoRemoteDataSourceImpl`: Implement `uploadVideoToS3Multipart` using `client.post` with `MultiPartFormDataContent`.

### 5. Upload Video UseCase
- **[MODIFY]** `UploadVideoUseCase`:
  1. Compress Video.
  2. Get Presigned POST fields from Ktor Server.
  3. Upload directly to S3 via Ktor Multipart POST.
  4. Notify Server of completion.

### 6. Cleanup
- **[DELETE]** `MultipartUploader.kt`.

## Technical Details

### S3 POST Upload Logic
S3 POST requires specific form fields in a specific order. The `file` field MUST be the last field in the form.
1. `key`
2. `Content-Type`
3. `X-Amz-Algorithm`
4. `X-Amz-Credential`
5. `X-Amz-Date`
6. `Policy`
7. `X-Amz-Signature`
8. **`file`** (Video bytes)

## Verification Plan

### Automated Tests
- Verify `VideoRemoteDataSourceImpl` correctly attaches all S3 required fields.

### Manual Verification
- Perform a video upload and verify the `multipart/form-data` request in the Network Inspector.
- Ensure progress reporting still works correctly during the streaming upload.
