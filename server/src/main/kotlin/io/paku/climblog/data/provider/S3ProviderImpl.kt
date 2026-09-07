package io.paku.climblog.data.provider

import com.amazonaws.HttpMethod
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import io.paku.climblog.domain.provider.S3PostData
import io.paku.climblog.domain.provider.S3Provider
import java.net.URL
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Base64
import java.util.Date
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class S3ProviderImpl(
    private val accessKey: String,
    private val secretKey: String,
    private val region: String
) : S3Provider {
    private val s3Client: AmazonS3 = AmazonS3ClientBuilder.standard()
        .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(accessKey, secretKey)))
        .withRegion(region)
        .build()

    override fun generatePresignedUploadUrl(
        bucketName: String,
        key: String,
        contentType: String
    ): URL {
        val expiration = Date().apply {
            time += 1000 * 60 * 15 // 15 minutes
        }

        val generatePresignedUrlRequest = GeneratePresignedUrlRequest(bucketName, key)
            .withMethod(HttpMethod.PUT)
            .withExpiration(expiration)
            .withContentType(contentType)

        return s3Client.generatePresignedUrl(generatePresignedUrlRequest)
    }

    override fun generatePresignedPost(
        bucketName: String,
        key: String,
        contentType: String
    ): S3PostData {
        val now = Instant.now()
        val amzDate = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'").withZone(ZoneOffset.UTC).format(now)
        val datestamp = amzDate.substring(0, 8)
        val expiration = now.plusSeconds(3600) // 1 hour
        val expirationStr = DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC).format(expiration)

        val credentialScope = "$datestamp/$region/s3/aws4_request"
        
        val policy = """
            {
              "expiration": "$expirationStr",
              "conditions": [
                {"bucket": "$bucketName"},
                ["starts-with", "${'$'}key", "$key"],
                {"content-type": "$contentType"},
                {"x-amz-algorithm": "AWS4-HMAC-SHA256"},
                {"x-amz-credential": "$accessKey/$credentialScope"},
                {"x-amz-date": "$amzDate"}
              ]
            }
        """.trimIndent().replace("\n", "").replace(" ", "")

        val encodedPolicy = Base64.getEncoder().encodeToString(policy.toByteArray(StandardCharsets.UTF_8))
        val signature = calculateSignature(encodedPolicy, datestamp)

        val fields = mapOf(
            "key" to key,
            "content-type" to contentType,
            "x-amz-algorithm" to "AWS4-HMAC-SHA256",
            "x-amz-credential" to "$accessKey/$credentialScope",
            "x-amz-date" to amzDate,
            "policy" to encodedPolicy,
            "x-amz-signature" to signature
        )

        return S3PostData(
            url = "https://$bucketName.s3.$region.amazonaws.com/",
            fields = fields
        )
    }

    private fun calculateSignature(policy: String, datestamp: String): String {
        val kDate = hmacSha256("AWS4$secretKey".toByteArray(), datestamp)
        val kRegion = hmacSha256(kDate, region)
        val kService = hmacSha256(kRegion, "s3")
        val kSigning = hmacSha256(kService, "aws4_request")
        
        val signatureBytes = hmacSha256(kSigning, policy)
        return signatureBytes.joinToString("") { "%02x".format(it) }
    }

    private fun hmacSha256(key: ByteArray, data: String): ByteArray {
        val sha256Hmac = Mac.getInstance("HmacSHA256")
        val secretKey = SecretKeySpec(key, "HmacSHA256")
        sha256Hmac.init(secretKey)
        return sha256Hmac.doFinal(data.toByteArray(StandardCharsets.UTF_8))
    }
}
