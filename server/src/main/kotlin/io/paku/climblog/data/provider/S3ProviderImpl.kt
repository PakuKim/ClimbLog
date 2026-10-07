package io.paku.climblog.data.provider

import com.amazonaws.HttpMethod
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import io.paku.climblog.domain.provider.S3Provider
import java.io.File
import java.util.Date

internal class S3ProviderImpl(
    private val accessKey: String,
    private val secretKey: String,
    private val region: String,
    private val endpoint: String? = null
) : S3Provider {
    private val s3Client: AmazonS3 = run {
        val builder = AmazonS3ClientBuilder.standard()
            .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(accessKey, secretKey)))
        if (!endpoint.isNullOrBlank()) {
            builder.withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(endpoint, region))
        } else {
            builder.withRegion(region)
        }
        builder.build()
    }

    override fun generatePresignedPutUrl(
        bucketName: String,
        key: String,
        contentType: String
    ): String {
        val expiration = Date().apply {
            time += 1000 * 60 * 15 // 15 minutes
        }

        val generatePresignedUrlRequest = GeneratePresignedUrlRequest(bucketName, key)
            .withMethod(HttpMethod.PUT)
            .withExpiration(expiration)
            .withContentType(contentType)

        return s3Client.generatePresignedUrl(generatePresignedUrlRequest).toString()
    }

    override fun uploadFile(
        bucketName: String,
        key: String,
        file: File,
        contentType: String
    ) {
        val metadata = ObjectMetadata().apply {
            this.contentType = contentType
            this.contentLength = file.length()
        }
        s3Client.putObject(PutObjectRequest(bucketName, key, file).withMetadata(metadata))
    }

    override fun downloadFile(
        bucketName: String,
        key: String,
        destinationFile: File
    ) {
        val s3Object = s3Client.getObject(GetObjectRequest(bucketName, key))
        s3Object.objectContent.use { input ->
            destinationFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }

    override fun doesObjectExist(bucketName: String, key: String): Boolean {
        return try {
            s3Client.doesObjectExist(bucketName, key)
        } catch (e: Exception) {
            false
        }
    }
}
