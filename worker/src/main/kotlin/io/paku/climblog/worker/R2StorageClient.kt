package io.paku.climblog.worker

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import java.io.File

class R2StorageClient(private val config: WorkerConfig) {
    private val s3Client: AmazonS3 = run {
        val builder = AmazonS3ClientBuilder.standard()
            .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(config.r2AccessKey, config.r2SecretKey)))
        if (config.r2Endpoint.isNotBlank()) {
            builder.withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(config.r2Endpoint, "auto"))
        } else {
            builder.withRegion("auto")
        }
        builder.build()
    }

    fun downloadFile(key: String, destinationFile: File) {
        destinationFile.parentFile?.mkdirs()
        val s3Object = s3Client.getObject(GetObjectRequest(config.r2Bucket, key))
        s3Object.objectContent.use { input ->
            destinationFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }

    fun uploadDirectory(prefix: String, directory: File) {
        if (!directory.exists()) return
        directory.walkTopDown().filter { it.isFile }.forEach { file ->
            val relativePath = file.relativeTo(directory).path.replace('\\', '/')
            val key = "$prefix$relativePath"
            val contentType = when {
                key.endsWith(".m3u8") -> "application/vnd.apple.mpegurl"
                key.endsWith(".ts") -> "video/mp2t"
                key.endsWith(".jpg") || key.endsWith(".jpeg") -> "image/jpeg"
                else -> "application/octet-stream"
            }
            val metadata = ObjectMetadata().apply {
                this.contentType = contentType
                this.contentLength = file.length()
            }
            s3Client.putObject(PutObjectRequest(config.r2Bucket, key, file).withMetadata(metadata))
        }
    }
}
