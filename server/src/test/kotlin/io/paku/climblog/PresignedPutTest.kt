package io.paku.climblog

import io.paku.climblog.domain.provider.S3Provider
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PresignedPutTest {

    private class TestS3Provider : S3Provider {
        var lastBucket: String? = null
        var lastKey: String? = null
        var lastContentType: String? = null

        override fun generatePresignedPutUrl(bucketName: String, key: String, contentType: String): String {
            lastBucket = bucketName
            lastKey = key
            lastContentType = contentType
            return "https://r2.example.com/$bucketName/$key?X-Amz-Signature=test&Content-Type=$contentType"
        }

        override fun uploadFile(bucketName: String, key: String, file: File, contentType: String) {}

        override fun downloadFile(bucketName: String, key: String, destinationFile: File) {}

        override fun doesObjectExist(bucketName: String, key: String): Boolean = true
    }

    @Test
    fun testGeneratePresignedPutUrl_preservesExactContentTypeAndKeyStructure() {
        val provider = TestS3Provider()
        val uploadUrl = provider.generatePresignedPutUrl(
            bucketName = "climblog-bucket",
            key = "raw/uuid_myvideo.mp4",
            contentType = "video/mp4"
        )

        assertEquals("climblog-bucket", provider.lastBucket)
        assertEquals("raw/uuid_myvideo.mp4", provider.lastKey)
        assertEquals("video/mp4", provider.lastContentType)
        assertTrue(uploadUrl.contains("Content-Type=video/mp4"))
    }
}
