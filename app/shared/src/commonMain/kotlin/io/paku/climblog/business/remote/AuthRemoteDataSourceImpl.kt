package io.paku.climblog.business.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.paku.climblog.business.data.source.remote.AuthRemoteDataSource
import io.paku.climblog.business.remote.dto.request.auth.SocialLoginRequest
import io.paku.climblog.business.remote.dto.response.auth.AuthResponse
import io.paku.climblog.business.remote.dto.response.auth.CheckEmailResponse
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal class AuthRemoteDataSourceImpl(
    private val client: HttpClient
): AuthRemoteDataSource {
    private companion object {
        const val API_BASE = "api/v1/"
        const val REGISTER_URL = "${API_BASE}auth/register"
        const val LOGIN_URL = "${API_BASE}auth/login"
        const val SOCIAL_LOGIN_URL = "${API_BASE}auth/social-login"
        const val CHECK_EMAIL_URL = "${API_BASE}auth/check-email"
    }

    override suspend fun register(
        email: String,
        password: String,
        name: String,
    ) {
        client.post(REGISTER_URL) {
            setBody(
                buildJsonObject {
                    put("email", email)
                    put("password", password)
                    put("name", name)
                }
            )
        }
    }

    override suspend fun login(
        email: String,
        password: String
    ): Pair<String, String> {
        return client.post(LOGIN_URL) {
            setBody(
                buildJsonObject {
                    put("email", email)
                    put("password", password)
                }
            )
        }.body<AuthResponse>().let { it.accessToken to it.refreshToken }
    }

    override suspend fun socialLogin(
        email: String,
        name: String,
        socialId: String,
        provider: String
    ): Triple<String, String, Boolean> {
        val request = SocialLoginRequest(email, name, socialId, provider)
        val response = client.post(SOCIAL_LOGIN_URL) {
            setBody(request)
        }.body<AuthResponse>()
        return Triple(response.accessToken, response.refreshToken, response.isRegistered)
    }

    override suspend fun checkEmail(email: String): Boolean {
        return client.post(CHECK_EMAIL_URL) {
            setBody(
                buildJsonObject {
                    put("email", email)
                }
            )
        }.body<CheckEmailResponse>().isAvailable
    }
}
