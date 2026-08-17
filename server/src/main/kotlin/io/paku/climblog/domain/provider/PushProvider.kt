package io.paku.climblog.domain.provider

interface PushProvider {
    suspend fun sendPush(
        token: String,
        title: String,
        body: String,
        data: Map<String, String> = emptyMap()
    )
}
