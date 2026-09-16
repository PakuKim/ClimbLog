package io.paku.climblog.domain.provider

interface MediaConvertProvider {
    /**
     * Creates a MediaConvert job to transcode a raw video into HLS.
     * @param videoId ID of the video in database
     * @param inputPath S3 path of the input video (e.g. s3://bucket/raw/file.mp4)
     * @param outputPath S3 path prefix for HLS output (e.g. s3://bucket/processed/id/)
     * @return Job ID
     */
    suspend fun createHlsJob(videoId: Long, inputPath: String, outputPath: String): String
}
