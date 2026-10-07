package io.paku.climblog.worker

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import kotlin.math.min

data class VideoMetadata(
    val width: Int,
    val height: Int,
    val fps: Double
)

class FfmpegTranscoder(private val config: WorkerConfig) {
    private val json = Json { ignoreUnknownKeys = true }

    fun probe(inputFile: File): VideoMetadata {
        val pb = ProcessBuilder(
            config.ffprobePath,
            "-v", "error",
            "-select_streams", "v:0",
            "-show_entries", "stream=width,height,r_frame_rate",
            "-of", "json",
            inputFile.absolutePath
        )
        pb.redirectErrorStream(true)
        val process = pb.start()
        val output = process.inputStream.bufferedReader().use { it.readText() }
        val exitCode = process.waitFor()
        if (exitCode != 0) {
            throw RuntimeException("ffprobe failed with exit code $exitCode: $output")
        }

        val root = json.parseToJsonElement(output).jsonObject
        val streams = root["streams"]?.jsonArray ?: throw RuntimeException("No video stream found in probe output")
        val stream = streams.firstOrNull()?.jsonObject ?: throw RuntimeException("Empty video streams array")

        val width = stream["width"]?.jsonPrimitive?.content?.toInt() ?: 1280
        val height = stream["height"]?.jsonPrimitive?.content?.toInt() ?: 720
        val rFrameRate = stream["r_frame_rate"]?.jsonPrimitive?.content ?: "30/1"

        val fps = parseFps(rFrameRate)
        return VideoMetadata(width, height, fps)
    }

    private fun parseFps(rFrameRate: String): Double {
        return try {
            val parts = rFrameRate.split("/")
            if (parts.size == 2) {
                val num = parts[0].toDouble()
                val den = parts[1].toDouble()
                if (den > 0.0) num / den else 30.0
            } else {
                rFrameRate.toDouble()
            }
        } catch (e: Exception) {
            30.0
        }
    }

    fun transcode(inputFile: File, outputDir: File, metadata: VideoMetadata): List<String> {
        outputDir.mkdirs()

        val sourceWidth = metadata.width
        val sourceHeight = metadata.height
        val sourceFps = metadata.fps

        val outputFps = min(sourceFps, 30.0)
        val gopSize = (outputFps * 2).toInt()

        // Determine target ladder (No upscaling, preserve aspect ratio)
        // Supported ladder heights: 1080, 720, 480
        val targetHeights = listOf(1080, 720, 480)
            .filter { it <= sourceHeight }
            .ifEmpty { listOf(sourceHeight) }

        val generatedRenditions = mutableListOf<String>()

        // For each target height, generate HLS rendition
        for (height in targetHeights) {
            val bandwidth = when (height) {
                1080 -> 4_000_000
                720 -> 2_000_000
                else -> 800_000
            }
            val resFolder = File(outputDir, "${height}p")
            resFolder.mkdirs()

            val playlistPath = File(resFolder, "index.m3u8").absolutePath
            val segmentPattern = File(resFolder, "segment_%03d.ts").absolutePath

            // Scale maintaining aspect ratio, width divisible by 2 (-2)
            val scaleFilter = "scale=-2:$height"

            val cmd = mutableListOf(
                config.ffmpegPath,
                "-y",
                "-i", inputFile.absolutePath,
                "-vf", scaleFilter,
                "-c:v", "libx264",
                "-profile:v", "main",
                "-pix_fmt", "yuv420p",
                "-g", gopSize.toString(),
                "-keyint_min", gopSize.toString(),
                "-sc_threshold", "0",
                "-c:a", "aac",
                "-b:a", "128k",
                "-ac", "2",
                "-ar", "44100",
                "-hls_time", "6",
                "-hls_playlist_type", "vod",
                "-hls_segment_filename", segmentPattern,
                playlistPath
            )

            if (outputFps < sourceFps) {
                // Add fps filter if capping at 30
                // We can insert before scale filter
                cmd.add(4, "-r")
                cmd.add(5, outputFps.toString())
            }

            executeProcess(cmd)
            generatedRenditions.add("${height}p")
        }

        // Generate Master Playlist dynamically
        generateMasterPlaylist(outputDir, generatedRenditions, metadata)

        // Generate Thumbnail at 1s
        generateThumbnail(inputFile, outputDir)

        return generatedRenditions
    }

    internal fun generateMasterPlaylist(outputDir: File, renditions: List<String>, metadata: VideoMetadata) {
        val masterFile = File(outputDir, "master.m3u8")
        val sb = StringBuilder()
        sb.append("#EXTM3U\n")
        sb.append("#EXT-X-VERSION:3\n")

        for (rendition in renditions) {
            val height = rendition.removeSuffix("p").toInt()
            val bandwidth = when {
                height >= 1080 -> 4_000_000
                height >= 720 -> 2_000_000
                else -> 800_000
            }

            val (resWidth, resHeight) = if (metadata.height > 0) {
                val scale = height.toDouble() / metadata.height.toDouble()
                var calcWidth = (metadata.width * scale).toInt()
                if (calcWidth % 2 != 0) calcWidth += 1 // Ensure width is even for video encoders
                calcWidth to height
            } else {
                1280 to height
            }
            val resolution = "${resWidth}x${resHeight}"

            sb.append("#EXT-X-STREAM-INF:BANDWIDTH=$bandwidth,RESOLUTION=$resolution\n")
            sb.append("$rendition/index.m3u8\n")
        }

        masterFile.writeText(sb.toString())
    }

    private fun generateThumbnail(inputFile: File, outputDir: File) {
        val thumbFile = File(outputDir, "thumbnail.jpg")
        val cmd = listOf(
            config.ffmpegPath,
            "-y",
            "-ss", "00:00:01.000",
            "-i", inputFile.absolutePath,
            "-vframes", "1",
            "-q:v", "2",
            thumbFile.absolutePath
        )
        try {
            executeProcess(cmd)
        } catch (e: Exception) {
            // Fallback: seek to 0s if 1s fails
            val fallbackCmd = listOf(
                config.ffmpegPath,
                "-y",
                "-ss", "00:00:00.000",
                "-i", inputFile.absolutePath,
                "-vframes", "1",
                "-q:v", "2",
                thumbFile.absolutePath
            )
            executeProcess(fallbackCmd)
        }
    }

    private fun executeProcess(cmd: List<String>) {
        val pb = ProcessBuilder(cmd)
        pb.redirectErrorStream(true)
        val process = pb.start()
        val output = process.inputStream.bufferedReader().use { it.readText() }
        val exitCode = process.waitFor()
        if (exitCode != 0) {
            throw RuntimeException("Command failed with exit code $exitCode: ${cmd.firstOrNull()}\nOutput: $output")
        }
    }
}
