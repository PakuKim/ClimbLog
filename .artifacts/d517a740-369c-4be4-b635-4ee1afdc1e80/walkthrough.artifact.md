# Video Upload Optimization & S3 POST Object Walkthrough

Transitioned the video upload flow to use S3 POST Object API with Ktor's native Multipart support and implemented platform-specific video downsizing.

## Key Changes

### 1. Platform-Specific Video Downsizing
- **Android (`AndroidVideoCompressor.kt`)**:
    - Integrated `androidx.media3:media3-transformer` and `media3-effect`.
    - Implemented logic to calculate target resolution based on `VideoQuality` (1080p, 720p, 480p) without upscaling.
    - Used `Presentation` effect for high-quality hardware-accelerated downsizing.
- **iOS (`IOSVideoCompressor.kt`)**:
    - Used `AVAssetExportSession` with quality presets (`1920x1080`, `1280x720`, `640x480`).
    - Added checks to avoid upscaling original videos.

### 2. Server-Side S3 POST Policy Generation
- **`S3Provider` Update**: Replaced manual Multipart Upload methods with `generatePresignedPost`.
- **Signature Utility**: Implemented AWS Signature V4 generation for S3 POST in `S3ProviderImpl`. This generates the mandatory `policy` and `x-amz-signature` fields for the client.
- **Ktor Routes**: Added `/api/v1/videos/uploads/presigned-post` to provide the client with the required form fields.

### 3. Client-Side Ktor Multipart Upload
- **`VideoRemoteDataSourceImpl`**: Implemented `uploadVideoToS3Post` using Ktor's `MultiPartFormDataContent`.
    - Automatically handles boundary generation and streaming.
    - Ensures form fields are ordered correctly (S3 requirements).
    - Supports `onUpload` progress reporting for the entire file.
- **`UploadVideoUseCase`**: Simplified the flow to:
    1. Compress video.
    2. Fetch Presigned POST fields.
    3. Perform a single POST request to S3.
    4. Register metadata.

## Technical Details
- **Order of Fields**: In S3 POST, the `file` field must be the last one. The implementation in `VideoRemoteDataSourceImpl` strictly follows this.
- **No Upscaling Rule**: Both platforms check the original video dimensions and ensure the output resolution never exceeds the source resolution, regardless of the selected quality.

## Verification Results
- **Android/iOS Compilation**: Verified that platform-specific Media3 and AVFoundation code compiles correctly.
- **Network Protocol**: Verified that S3 POST Object protocol is strictly followed with the correct form field sequence.
