package io.paku.climblog.worker

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.time.Duration.Companion.seconds

fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("WorkerMain")
    val config = WorkerConfig()
    val jobClient = JobClient(config)
    val r2Client = R2StorageClient(config)
    val transcoder = FfmpegTranscoder(config)

    logger.info("Starting ClimbLog FFmpeg Worker [Id: ${config.workerId}, Server: ${config.ktorBaseUrl}]")

    runBlocking {
        while (true) {
            try {
                val job = jobClient.claimJob()
                if (job == null) {
                    delay(config.pollIntervalSeconds.seconds)
                    continue
                }

                logger.info("Claimed transcoding job ${job.id} for video ${job.videoId} (input: ${job.inputKey})")

                val workDir = File(config.workDir, job.id.toString())
                val inputDir = File(workDir, "input")
                val outputDir = File(workDir, "output")

                try {
                    inputDir.mkdirs()
                    outputDir.mkdirs()

                    val localInputFile = File(inputDir, "source.mp4")
                    
                    // 1. Download raw video from R2
                    logger.info("Downloading raw video from R2 key: ${job.inputKey}")
                    r2Client.downloadFile(job.inputKey, localInputFile)

                    // 2. Probe metadata
                    logger.info("Probing video metadata...")
                    val metadata = transcoder.probe(localInputFile)
                    logger.info("Source metadata: ${metadata.width}x${metadata.height} @ ${metadata.fps}fps")

                    // 3. Transcode & Thumbnail
                    logger.info("Starting FFmpeg transcoding...")
                    transcoder.transcode(localInputFile, outputDir, metadata)

                    // 4. Upload processed files to R2
                    val hlsUrl = "${config.r2PublicBaseUrl.removeSuffix("/")}/${job.outputPrefix}master.m3u8"
                    val thumbnailUrl = "${config.r2PublicBaseUrl.removeSuffix("/")}/${job.outputPrefix}thumbnail.jpg"

                    logger.info("Uploading processed HLS files & thumbnail to R2 prefix: ${job.outputPrefix}")
                    r2Client.uploadDirectory(job.outputPrefix, outputDir)

                    // 5. Complete Job
                    logger.info("Reporting job ${job.id} as COMPLETED")
                    jobClient.completeJob(job.id, hlsUrl, thumbnailUrl)

                } catch (e: Exception) {
                    logger.error("Error processing job ${job.id}: ${e.message}", e)
                    val errorCategory = when {
                        e.message?.contains("download", true) == true -> "R2 download failed"
                        e.message?.contains("ffprobe", true) == true -> "ffprobe failed"
                        e.message?.contains("Command failed", true) == true -> "FFmpeg failed"
                        e.message?.contains("upload", true) == true -> "R2 upload failed"
                        else -> "Processing failed"
                    }
                    val errorMessage = "$errorCategory: ${e.message}"
                    try {
                        jobClient.failJob(job.id, errorMessage)
                    } catch (failEx: Exception) {
                        logger.error("Failed to report job failure to server: ${failEx.message}", failEx)
                    }
                } finally {
                    // Cleanup temporary files
                    try {
                        workDir.deleteRecursively()
                    } catch (cleanupEx: Exception) {
                        logger.warn("Failed to cleanup work directory ${workDir.absolutePath}: ${cleanupEx.message}")
                    }
                }

            } catch (e: Exception) {
                logger.error("Worker poll loop error: ${e.message}", e)
                delay(config.pollIntervalSeconds.seconds)
            }
        }
    }
}
