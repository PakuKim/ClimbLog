package io.paku.climblog.domain.interactor.auth

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpStatusCode
import io.paku.climblog.domain.model.AppException
import io.paku.climblog.domain.model.token.GoogleTokenInfo
import io.paku.climblog.domain.model.token.KakaoTokenInfo
import io.paku.climblog.domain.model.token.NaverUserInfo

internal class VerifySocialTokenUseCase(
    private val httpClient: HttpClient
) {
    suspend operator fun  invoke(
        provider: String,
        socialToken: String
    ): String {
        val providerId = when (provider.uppercase()) {
            "GOOGLE" -> verifyGoogle(socialToken)
            "KAKAO" -> verifyKakao(socialToken)
            "NAVER" -> verifyNaver(socialToken)
            else -> throw AppException(HttpStatusCode.BadRequest, "Unsupported provider: $provider")
        }

        return providerId
    }

    private suspend fun verifyGoogle(idToken: String): String {
        val info = httpClient.get("https://oauth2.googleapis.com/tokeninfo?id_token=$idToken")
            .body<GoogleTokenInfo>()
        return info.sub
    }

    private suspend fun verifyKakao(accessToken: String): String {
        val info = httpClient.get("https://kapi.kakao.com/v2/user/me") {
            header("Authorization", "Bearer $accessToken")
        }.body<KakaoTokenInfo>()
        return info.id.toString()
    }

    private suspend fun verifyNaver(accessToken: String): String {
        val info = httpClient.get("https://openapi.naver.com/v1/nid/me") {
            header("Authorization", "Bearer $accessToken")
        }.body<NaverUserInfo>()
        val response = info.response ?: throw AppException(HttpStatusCode.InternalServerError, "Invalid response from Naver")
        return response.id
    }
}