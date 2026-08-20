package io.paku.climblog.business.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.paku.climblog.business.data.source.remote.AuthRemoteDataSource
import io.paku.climblog.business.remote.dto.request.auth.SocialLoginRequest
import io.paku.climblog.business.remote.dto.request.auth.SocialRegisterRequest
import io.paku.climblog.business.remote.dto.response.auth.AuthResponse

internal class AuthRemoteDataSourceImpl(
    private val client: HttpClient
): AuthRemoteDataSource {
    private companion object {
        const val SOCIAL_LOGIN_URL = "auth/social/login"
        const val SOCIAL_REGISTER_URL = "auth/social/register"
        const val LOGOUT_URL = "auth/logout"
    }

    override suspend fun socialLogin(
        provider: String,
        socialToken: String,
    ): Pair<String, String> {
        val request = SocialLoginRequest(
            provider = provider,
            socialToken = socialToken
        )

        val response = client.post(SOCIAL_LOGIN_URL) {
            setBody(request)
        }.body<AuthResponse>()

        return response.accessToken to response.refreshToken
    }

    override suspend fun socialRegister(
        socialToken: String,
        provider: String,
        handle: String,
        name: String,
        age: Int,
        height: Int,
        armReach: Int,
        gender: String,
        profilePhotoUrl: String?
    ){
        val request = SocialRegisterRequest(
            socialToken = socialToken,
            provider = provider,
            handle = handle,
            name = name,
            age = age,
            height = height,
            armReach = armReach,
            gender = gender,
            profilePhotoUrl = profilePhotoUrl
        )

        client.post(SOCIAL_REGISTER_URL) {
            setBody(request)
        }
    }

    override suspend fun logout() {
        client.post(LOGOUT_URL)
    }
}
