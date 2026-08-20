package io.paku.climblog.data.provider

import com.amazonaws.HttpMethod
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import io.paku.climblog.domain.provider.S3Provider
import java.net.URL
import java.util.Date

class S3ProviderImpl(
    accessKey: String,
    secretKey: String,
    region: String
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
}
