package io.paku.climblog.platform

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.Presentation
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.Effects
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

@OptIn(UnstableApi::class)
class AndroidVideoCompressor(
    private val context: Context
) : io.paku.climblog.domain.provider.VideoCompressor {
    override suspend fun compress(
        source: io.paku.climblog.domain.model.MediaSource,
        quality: io.paku.climblog.domain.model.video.VideoQuality
    ): io.paku.climblog.domain.model.MediaSource = withContext(Dispatchers.IO) {
        val platformSource = source as? PlatformMedia ?: throw IllegalArgumentException("Unsupported media source")
        val outputDir = File(context.cacheDir, "compressed_videos").apply { mkdirs() }
        val outputFile = File(outputDir, "compressed_${UUID.randomUUID()}.mp4")
        
        val deferred = CompletableDeferred<PlatformMedia>()
        
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(context, platformSource.uri)
            val originalWidth = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toInt() ?: 0
            val originalHeight = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toInt() ?: 0
            val rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.toInt() ?: 0
            
            val (width, height) = if (rotation == 90 || rotation == 270) {
                originalHeight to originalWidth
            } else {
                originalWidth to originalHeight
            }

            // Calculate target resolution (no upscaling)
            val targetWidth: Int
            val targetHeight: Int
            
            // quality.width is the target long side (1920, 1280, 854)
            // quality.height is the target short side (1080, 720, 480)
            if (width >= height) { // Landscape or Square
                val scale = minOf(1.0, quality.width.toDouble() / width)
                targetWidth = (width * scale).toInt()
                targetHeight = (height * scale).toInt()
            } else { // Portrait
                val scale = minOf(1.0, quality.width.toDouble() / height)
                targetHeight = (height * scale).toInt()
                targetWidth = (width * scale).toInt()
            }

            val mediaItem = MediaItem.fromUri(source.uri)
            val editedMediaItemBuilder = EditedMediaItem.Builder(mediaItem)
                .setRemoveAudio(false)
            
            if (targetWidth < width || targetHeight < height) {
                val presentation = Presentation.createForWidthAndHeight(
                    targetWidth, 
                    targetHeight, 
                    Presentation.LAYOUT_SCALE_TO_FIT
                )
                editedMediaItemBuilder.setEffects(
                    Effects(emptyList(), listOf(presentation))
                )
            }

            val editedMediaItem = editedMediaItemBuilder.build()

            withContext(Dispatchers.Main) {
                val transformer = Transformer.Builder(context)
                    .setVideoMimeType(MimeTypes.VIDEO_H264)
                    .build()

                val listener = object : Transformer.Listener {
                    override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                        deferred.complete(PlatformMedia(Uri.fromFile(outputFile), context.contentResolver))
                    }

                    override fun onError(composition: Composition, exportResult: ExportResult, exportException: ExportException) {
                        deferred.completeExceptionally(exportException)
                    }
                }

                transformer.addListener(listener)
                
                try {
                    transformer.start(editedMediaItem, outputFile.absolutePath)
                } catch (e: Exception) {
                    deferred.completeExceptionally(e)
                }
            }
        } catch (e: Exception) {
            deferred.completeExceptionally(e)
        } finally {
            retriever.release()
        }

        try {
            deferred.await()
        } catch (e: Exception) {
            if (outputFile.exists()) outputFile.delete()
            throw e
        }
    }
}
