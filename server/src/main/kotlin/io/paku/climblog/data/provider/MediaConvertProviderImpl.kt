package io.paku.climblog.data.provider

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.mediaconvert.AWSMediaConvert
import com.amazonaws.services.mediaconvert.AWSMediaConvertClientBuilder
import com.amazonaws.services.mediaconvert.model.AacCodingMode
import com.amazonaws.services.mediaconvert.model.AacSettings
import com.amazonaws.services.mediaconvert.model.AudioCodec
import com.amazonaws.services.mediaconvert.model.AudioCodecSettings
import com.amazonaws.services.mediaconvert.model.AudioDescription
import com.amazonaws.services.mediaconvert.model.AudioSelector
import com.amazonaws.services.mediaconvert.model.ContainerSettings
import com.amazonaws.services.mediaconvert.model.ContainerType
import com.amazonaws.services.mediaconvert.model.CreateJobRequest
import com.amazonaws.services.mediaconvert.model.DescribeEndpointsRequest
import com.amazonaws.services.mediaconvert.model.FileGroupSettings
import com.amazonaws.services.mediaconvert.model.FrameCaptureSettings
import com.amazonaws.services.mediaconvert.model.H264QualityTuningLevel
import com.amazonaws.services.mediaconvert.model.H264RateControlMode
import com.amazonaws.services.mediaconvert.model.H264SceneChangeDetect
import com.amazonaws.services.mediaconvert.model.H264Settings
import com.amazonaws.services.mediaconvert.model.HlsGroupSettings
import com.amazonaws.services.mediaconvert.model.Input
import com.amazonaws.services.mediaconvert.model.JobSettings
import com.amazonaws.services.mediaconvert.model.Output
import com.amazonaws.services.mediaconvert.model.OutputGroup
import com.amazonaws.services.mediaconvert.model.OutputGroupSettings
import com.amazonaws.services.mediaconvert.model.OutputGroupType
import com.amazonaws.services.mediaconvert.model.VideoCodec
import com.amazonaws.services.mediaconvert.model.VideoCodecSettings
import com.amazonaws.services.mediaconvert.model.VideoDescription
import com.amazonaws.services.mediaconvert.model.VideoSelector
import io.paku.climblog.domain.provider.MediaConvertProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaConvertProviderImpl(
    private val accessKey: String,
    private val secretKey: String,
    private val region: String,
    private val roleArn: String,
    private val queueArn: String
) : MediaConvertProvider {

    private val mediaConvertClient: AWSMediaConvert by lazy {
        val client = AWSMediaConvertClientBuilder.standard()
            .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(accessKey, secretKey)))
            .withRegion(region)
            .build()
        
        val endpoint = client.describeEndpoints(DescribeEndpointsRequest()).endpoints.firstOrNull()?.url
            ?: throw IllegalStateException("Could not get MediaConvert endpoint")

        AWSMediaConvertClientBuilder.standard()
            .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(accessKey, secretKey)))
            .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(endpoint, region))
            .build()
    }

    override suspend fun createHlsJob(videoId: Long, inputPath: String, outputPath: String): String = withContext(Dispatchers.IO) {
        val jobRequest = CreateJobRequest()
            .withRole(roleArn)
            .withQueue(queueArn)
            .withUserMetadata(mapOf("videoId" to videoId.toString()))
            .withSettings(
                JobSettings()
                    .withInputs(
                        Input()
                            .withFileInput(inputPath)
                            .withAudioSelectors(mapOf("Audio Selector 1" to AudioSelector().withDefaultSelection("DEFAULT")))
                            .withVideoSelector(VideoSelector())
                    )
                    .withOutputGroups(
                        OutputGroup()
                            .withName("HLS")
                            .withOutputGroupSettings(
                                OutputGroupSettings()
                                    .withType(OutputGroupType.HLS_GROUP_SETTINGS)
                                    .withHlsGroupSettings(
                                        HlsGroupSettings()
                                            .withDestination(outputPath)
                                            .withSegmentLength(10)
                                            .withMinSegmentLength(0)
                                    )
                            )
                            .withOutputs(
                                Output()
                                    .withNameModifier("_hls")
                                    .withContainerSettings(ContainerSettings().withContainer(ContainerType.M3U8))
                                    .withVideoDescription(
                                        VideoDescription()
                                            .withCodecSettings(
                                                VideoCodecSettings()
                                                    .withCodec(VideoCodec.H_264)
                                                    .withH264Settings(
                                                        H264Settings()
                                                            .withRateControlMode(H264RateControlMode.QVBR)
                                                            .withSceneChangeDetect(H264SceneChangeDetect.ENABLED)
                                                            .withQualityTuningLevel(H264QualityTuningLevel.SINGLE_PASS_HQ)
                                                    )
                                            )
                                    )
                                    .withAudioDescriptions(
                                        AudioDescription()
                                            .withCodecSettings(
                                                AudioCodecSettings()
                                                    .withCodec(AudioCodec.AAC)
                                                    .withAacSettings(
                                                        AacSettings()
                                                            .withBitrate(128000)
                                                            .withCodingMode(AacCodingMode.CODING_MODE_2_0)
                                                            .withSampleRate(44100)
                                                    )
                                            )
                                    )
                            ),
                        // Thumbnail output
                        OutputGroup()
                            .withName("Thumbnails")
                            .withOutputGroupSettings(
                                OutputGroupSettings()
                                    .withType(OutputGroupType.FILE_GROUP_SETTINGS)
                                    .withFileGroupSettings(
                                        FileGroupSettings().withDestination(outputPath)
                                    )
                            )
                            .withOutputs(
                                Output()
                                    .withNameModifier("_thumb")
                                    .withContainerSettings(ContainerSettings().withContainer(ContainerType.RAW))
                                    .withVideoDescription(
                                        VideoDescription()
                                            .withCodecSettings(
                                                VideoCodecSettings()
                                                    .withCodec(VideoCodec.FRAME_CAPTURE)
                                                    .withFrameCaptureSettings(
                                                        FrameCaptureSettings()
                                                            .withFramerateNumerator(1)
                                                            .withFramerateDenominator(10) // Capture every 10 sec, but we can use specific point
                                                            .withMaxCaptures(1)
                                                            .withQuality(80)
                                                    )
                                            )
                                    )
                            )
                    )
            )

        val result = mediaConvertClient.createJob(jobRequest)
        result.job.id
    }
}
