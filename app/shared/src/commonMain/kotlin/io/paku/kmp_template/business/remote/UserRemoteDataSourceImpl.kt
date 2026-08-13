package io.paku.kmp_template.business.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.paku.kmp_template.business.data.source.remote.UserRemoteDataSource
import io.paku.kmp_template.business.remote.dto.response.user.GetUserResponse

internal class UserRemoteDataSourceImpl(
    private val client: HttpClient
): UserRemoteDataSource {
    private companion object {
        const val USER_URL = "user/me"
    }

    override suspend fun getUser(): Long {
        return client.get(USER_URL)
            .body<GetUserResponse>().id
    }
}