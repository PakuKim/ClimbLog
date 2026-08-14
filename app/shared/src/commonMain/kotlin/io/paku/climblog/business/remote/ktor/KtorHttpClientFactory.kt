package io.paku.climblog.business.remote.ktor

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import io.paku.climblog.business.data.source.local.SessionLocalDataSource
import io.paku.climblog.business.model.CommonError
import io.paku.climblog.business.model.CommonException
import io.paku.climblog.business.remote.dto.response.auth.AuthResponse
import io.paku.climblog.core.Platform
import io.paku.climblog.core.getPlatform
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal object KtorHttpClientFactory {
    private val BASE_URL = when(getPlatform()) {
        Platform.ANDROID -> "http://10.0.2.2:8080/"
        Platform.IOS -> "http://127.0.0.1:8080/"
        else -> "http://localhost:8080/"
    }
    private const val REFRESH_TOKEN_URL = "auth/refresh"
    private const val CONNECTION_TIMEOUT = 10_000L

    fun create(
        session: SessionLocalDataSource
    ) = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                    explicitNulls = false
                }
            )
        }

        install(DefaultRequest) {
            url(BASE_URL)
            contentType(ContentType.Application.Json)
        }

        install(HttpTimeout) {
            connectTimeoutMillis = CONNECTION_TIMEOUT
            requestTimeoutMillis = CONNECTION_TIMEOUT
            socketTimeoutMillis = CONNECTION_TIMEOUT
        }

        install(Logging) {
            level = LogLevel.ALL
            logger = object : Logger {
                override fun log(message: String) {
                    println("AppDebug KtorHttpClient message:$message")
                }
            }
        }

        install(Auth) {
            bearer {
                loadTokens {
                    val accessToken = session.getAccessToken()?.removePrefix("Bearer ")
                    val refreshToken = session.getRefreshToken()?.removePrefix("Bearer ")

                    if (accessToken != null && refreshToken != null) {
                        BearerTokens(accessToken, refreshToken)
                    } else null
                }

                refreshTokens {
                    client.post {
                        markAsRefreshTokenRequest()
                        url(REFRESH_TOKEN_URL)
                        setBody(
                            buildJsonObject {
                                put("refreshToken", session.getRefreshToken()?.removePrefix("Bearer "))
                            }
                        )
                    }.body<AuthResponse>().let { response ->
                        val accessToken = response.accessToken
                        val refreshToken = response.refreshToken
                        session.saveAccessToken(accessToken)
                        session.saveRefreshToken(refreshToken)

                        BearerTokens(accessToken, refreshToken)
                    }
                }
            }
        }

        HttpResponseValidator {
            validateResponse { response ->
                if (!response.status.isSuccess()) {
                    if (response.status == HttpStatusCode.Unauthorized) {
                        session.clear()
                        throw CommonException(
                            error = CommonError.UnAuthorized,
                            code = HttpStatusCode.Unauthorized.value
                        )
                    } else {
                        throw CommonException(message = "errorMessage", code = 0)
                    }
                }
            }

            handleResponseExceptionWithRequest { cause, _ ->
                when (cause) {
                    is HttpRequestTimeoutException -> throw CommonException(CommonError.PoorNetwork)
                }
            }

        }
    }
}
