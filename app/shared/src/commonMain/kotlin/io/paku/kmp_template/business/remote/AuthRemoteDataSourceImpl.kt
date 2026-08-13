package io.paku.kmp_template.business.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.paku.kmp_template.business.data.source.remote.AuthRemoteDataSource
import io.paku.kmp_template.business.remote.dto.response.auth.AuthResponse
import io.paku.kmp_template.business.remote.dto.response.auth.CheckEmailResponse
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal class AuthRemoteDataSourceImpl(
    private val client: HttpClient
): AuthRemoteDataSource {
    private companion object {
        const val REGISTER_URL = "auth/register"
        const val LOGIN_URL = "auth/login"
        const val CHECK_EMAIL_URL = "auth/check-Email"
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