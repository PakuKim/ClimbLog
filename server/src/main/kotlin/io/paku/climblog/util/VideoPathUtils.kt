package io.paku.climblog.util

object VideoPathUtils {
    /**
     * S3 prefix for raw uploads: raw/{filename}
     */
    fun getRawS3Path(s3Bucket: String, s3Key: String): String {
        return "s3://$s3Bucket/$s3Key"
    }

    /**
     * S3 prefix destination for processed output for a videoId: s3://{bucket}/processed/{videoId}/
     */
    fun getProcessedS3Destination(s3Bucket: String, videoId: Long): String {
        return "s3://$s3Bucket/processed/$videoId/"
    }

    /**
     * S3 destination for HLS output group:
     * s3://{bucket}/processed/{videoId}/master
     */
    fun getHlsS3Destination(s3Bucket: String, videoId: Long): String {
        return "s3://$s3Bucket/processed/$videoId/master"
    }

    /**
     * S3 destination for thumbnail file group:
     * s3://{bucket}/processed/{videoId}/thumbnail/thumbnail
     */
    fun getThumbnailS3Destination(s3Bucket: String, videoId: Long): String {
        return "s3://$s3Bucket/processed/$videoId/thumbnail/thumbnail"
    }

    /**
     * S3 Key for master playlist file validation:
     * processed/{videoId}/master.m3u8
     */
    fun getMasterPlaylistS3Key(videoId: Long): String {
        return "processed/$videoId/master.m3u8"
    }

    /**
     * Public CloudFront URL for HLS master playlist
     */
    fun getCloudFrontHlsUrl(cloudFrontDomain: String, videoId: Long): String {
        return "https://$cloudFrontDomain/processed/$videoId/master.m3u8"
    }

    /**
     * Public CloudFront URL for thumbnail (generated frame capture at 1.0s)
     */
    fun getCloudFrontThumbnailUrl(cloudFrontDomain: String, videoId: Long): String {
        return "https://$cloudFrontDomain/processed/$videoId/thumbnail/thumbnail.0000001.jpg"
    }
}
