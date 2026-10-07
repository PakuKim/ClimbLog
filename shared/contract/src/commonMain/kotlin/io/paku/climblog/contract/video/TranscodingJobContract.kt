package io.paku.climblog.contract.video

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClaimJobRequest(
    @SerialName("workerId")
    val workerId: String,
    @SerialName("leaseSeconds")
    val leaseSeconds: Long = 600
)

@Serializable
data class TranscodingJobResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("videoId")
    val videoId: Long,
    @SerialName("inputKey")
    val inputKey: String,
    @SerialName("outputPrefix")
    val outputPrefix: String,
    @SerialName("status")
    val status: String,
    @SerialName("attempt")
    val attempt: Int,
    @SerialName("workerId")
    val workerId: String?,
    @SerialName("lockedUntil")
    val lockedUntil: String?
)

@Serializable
data class ClaimJobResponse(
    @SerialName("job")
    val job: TranscodingJobResponse?
)

@Serializable
data class CompleteJobRequest(
    @SerialName("workerId")
    val workerId: String,
    @SerialName("hlsUrl")
    val hlsUrl: String,
    @SerialName("thumbnailUrl")
    val thumbnailUrl: String
)

@Serializable
data class FailedJobRequest(
    @SerialName("workerId")
    val workerId: String,
    @SerialName("errorMessage")
    val errorMessage: String
)
