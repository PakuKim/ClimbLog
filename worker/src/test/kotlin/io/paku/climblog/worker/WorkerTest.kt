package io.paku.climblog.worker

import kotlin.test.Test
import kotlin.test.assertEquals

class WorkerTest {

    @Test
    fun testWorkerConfigDefaults() {
        val config = WorkerConfig()
        assertEquals("http://localhost:8080", config.ktorBaseUrl)
        assertEquals("climblog-worker-01", config.workerId)
        assertEquals(5L, config.pollIntervalSeconds)
        assertEquals(600L, config.leaseSeconds)
    }

    @Test
    fun testCompletionUrlConstruction() {
        val config = WorkerConfig(r2PublicBaseUrl = "https://media.climblog.io/")
        val outputPrefix = "processed/123/"
        val hlsUrl = "${config.r2PublicBaseUrl.removeSuffix("/")}/$outputPrefix/master.m3u8".replace("//", "/")
            .replace("https:/", "https://")
        val thumbnailUrl = "${config.r2PublicBaseUrl.removeSuffix("/")}/$outputPrefix/thumbnail.jpg".replace("//", "/")
            .replace("https:/", "https://")

        assertEquals("https://media.climblog.io/processed/123/master.m3u8", hlsUrl)
        assertEquals("https://media.climblog.io/processed/123/thumbnail.jpg", thumbnailUrl)
    }

    @Test
    fun testRenditionSelection_sourceBelow480p_preservesSourceResolution() {
        val sourceHeight = 360
        val targetHeights = listOf(1080, 720, 480)
            .filter { it <= sourceHeight }
            .ifEmpty { listOf(sourceHeight) }

        assertEquals(listOf(360), targetHeights)
    }

    @Test
    fun testRenditionSelection_source720p_noUpscalingTo1080p() {
        val sourceHeight = 720
        val targetHeights = listOf(1080, 720, 480)
            .filter { it <= sourceHeight }
            .ifEmpty { listOf(sourceHeight) }

        assertEquals(listOf(720, 480), targetHeights)
    }

    @Test
    fun testMasterPlaylist_landscapeResolution() {
        val transcoder = FfmpegTranscoder(WorkerConfig())
        val tempDir = java.io.File.createTempFile("master_test_landscape", "").apply { delete(); mkdirs() }
        try {
            val metadata = VideoMetadata(width = 1920, height = 1080, fps = 30.0)
            transcoder.generateMasterPlaylist(tempDir, listOf("1080p", "720p", "480p"), metadata)

            val masterContent = java.io.File(tempDir, "master.m3u8").readText()
            kotlin.test.assertTrue(masterContent.contains("RESOLUTION=1920x1080"))
            kotlin.test.assertTrue(masterContent.contains("RESOLUTION=1280x720"))
            kotlin.test.assertTrue(masterContent.contains("RESOLUTION=854x480"))
        } finally {
            tempDir.deleteRecursively()
        }
    }

    @Test
    fun testMasterPlaylist_portraitResolution() {
        val transcoder = FfmpegTranscoder(WorkerConfig())
        val tempDir = java.io.File.createTempFile("master_test_portrait", "").apply { delete(); mkdirs() }
        try {
            val metadata = VideoMetadata(width = 1080, height = 1920, fps = 30.0)
            transcoder.generateMasterPlaylist(tempDir, listOf("1080p", "720p", "480p"), metadata)

            val masterContent = java.io.File(tempDir, "master.m3u8").readText()
            kotlin.test.assertTrue(masterContent.contains("RESOLUTION=608x1080"))
            kotlin.test.assertTrue(masterContent.contains("RESOLUTION=406x720"))
            kotlin.test.assertTrue(masterContent.contains("RESOLUTION=270x480"))
        } finally {
            tempDir.deleteRecursively()
        }
    }
}
