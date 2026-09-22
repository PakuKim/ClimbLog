package io.paku.climblog.platform

import io.paku.climblog.domain.model.MediaSource
import io.paku.climblog.domain.model.video.VideoQuality
import io.paku.climblog.domain.provider.VideoCompressor
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.CompletableDeferred
import platform.AVFoundation.AVAsset
import platform.AVFoundation.AVAssetExportPreset1280x720
import platform.AVFoundation.AVAssetExportPreset1920x1080
import platform.AVFoundation.AVAssetExportPreset640x480
import platform.AVFoundation.AVAssetExportSession
import platform.AVFoundation.AVAssetExportSessionStatusCancelled
import platform.AVFoundation.AVAssetExportSessionStatusCompleted
import platform.AVFoundation.AVAssetExportSessionStatusFailed
import platform.AVFoundation.AVAssetTrack
import platform.AVFoundation.AVFileTypeMPEG4
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.naturalSize
import platform.AVFoundation.tracksWithMediaType
import platform.Foundation.NSFileManager
import platform.Foundation.NSUUID
import platform.Foundation.temporaryDirectory

class IOSVideoCompressor : VideoCompressor {
    @OptIn(ExperimentalForeignApi::class)
    override suspend fun compress(
        source: MediaSource,
        quality: VideoQuality
    ): MediaSource {
        val platformSource = source as? PlatformMedia ?: throw IllegalArgumentException("Unsupported media source")
        val asset = AVAsset.assetWithURL(platformSource.url)
        
        // 1. Get original size to avoid upscaling
        val videoTrack = asset.tracksWithMediaType(AVMediaTypeVideo).firstOrNull() as? AVAssetTrack
        val originalSize = videoTrack?.naturalSize
        
        val preset = if (originalSize != null) {
            val originalLongSide = originalSize.useContents { maxOf(width, height) }
            when {
                // If original is smaller than target, use a lower preset or original quality (passthrough)
                originalLongSide <= 480.0 -> AVAssetExportPreset640x480
                originalLongSide <= 720.0 -> {
                    if (quality == VideoQuality.LOW) AVAssetExportPreset640x480 else AVAssetExportPreset1280x720
                }
                else -> {
                    when (quality) {
                        VideoQuality.LOW -> AVAssetExportPreset640x480
                        VideoQuality.STANDARD -> AVAssetExportPreset1280x720
                        VideoQuality.HIGH -> AVAssetExportPreset1920x1080
                    }
                }
            }
        } else {
            // Fallback if size can't be determined
            when (quality) {
                VideoQuality.HIGH -> AVAssetExportPreset1920x1080
                VideoQuality.STANDARD -> AVAssetExportPreset1280x720
                VideoQuality.LOW -> AVAssetExportPreset640x480
            }
        }

        val exportSession = AVAssetExportSession(asset, preset) ?: return source
        
        val fileManager = NSFileManager.defaultManager
        val tempDir = fileManager.temporaryDirectory
        val outputUrl = tempDir.URLByAppendingPathComponent("compressed_${NSUUID().UUIDString}.mp4")!!
        
        exportSession.outputURL = outputUrl
        exportSession.outputFileType = AVFileTypeMPEG4
        exportSession.shouldOptimizeForNetworkUse = true

        val deferred = CompletableDeferred<PlatformMedia>()
        
        exportSession.exportAsynchronouslyWithCompletionHandler {
            when (exportSession.status) {
                AVAssetExportSessionStatusCompleted -> {
                    deferred.complete(PlatformMedia(outputUrl))
                }
                AVAssetExportSessionStatusFailed -> {
                    deferred.completeExceptionally(Exception("Video compression failed: ${exportSession.error?.localizedDescription}"))
                }
                AVAssetExportSessionStatusCancelled -> {
                    deferred.completeExceptionally(Exception("Video compression cancelled"))
                }
                else -> {
                    deferred.completeExceptionally(Exception("Video compression status: ${exportSession.status}"))
                }
            }
        }

        return try {
            deferred.await()
        } catch (e: Exception) {
            if (fileManager.fileExistsAtPath(outputUrl.path!!)) {
                fileManager.removeItemAtURL(outputUrl, null)
            }
            throw e
        }
    }
}
