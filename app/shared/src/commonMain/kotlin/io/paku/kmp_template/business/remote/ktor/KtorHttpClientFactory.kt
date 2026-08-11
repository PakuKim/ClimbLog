package io.paku.kmp_template.business.remote.ktor

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import io.paku.kmp_template.business.data.source.local.SessionLocalDataSource
import io.paku.kmp_template.business.model.CommonError
import io.paku.kmp_template.business.model.CommonException
import kotlinx.serialization.json.Json

internal object KtorHttpClientFactory {
    private const val BASE_URL = ""
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

        createClientPlugin("SessionPlugin") {
            onRequest { request, _ ->
                request.url(BASE_URL)
                request.contentType(ContentType.Application.Json)
                session.getAccessToken()?.let {
                    request.header(HttpHeaders.Authorization, "Bearer $it")
                }
            }

            onResponse { response ->
                val newAccessToken = response.headers["X-New-Access-Token"]
                val newRefreshToken = response.headers["X-New-Refresh-Token"]
                if (newAccessToken != null && newRefreshToken != null) {
                    session.updateAccessToken(newAccessToken)
                    session.updateRefreshToken(newRefreshToken)
                }
            }
        }

        HttpResponseValidator {
            validateResponse { response ->
                if (!response.status.isSuccess()) {
                    throw if (response.status == HttpStatusCode.Unauthorized) {
                        session.clear()
                        CommonException(
                            error = CommonError.UnAuthorized,
                            code = HttpStatusCode.Unauthorized.value
                        )
                    } else {
//                        val errorMessage = runCatching {
//                            JSONObject(response.bodyAsText()).getString(RESPONSE_ERROR_MESSAGE)
//                        }.getOrNull()
//
//                        val errorCode = runCatching {
//                            JSONObject(response.bodyAsText()).getInt(RESPONSE_ERROR_CODE)
//                        }.getOrNull()

                        CommonException(message = "errorMessage", code = 0)
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