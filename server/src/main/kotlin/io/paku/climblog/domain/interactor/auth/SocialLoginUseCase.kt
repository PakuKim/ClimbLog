package io.paku.climblog.domain.interactor.auth

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.paku.climblog.domain.UserRepository
import io.paku.climblog.domain.model.AuthToken
import io.paku.climblog.domain.model.User
import io.paku.climblog.domain.provider.JwtTokenProvider
import kotlinx.serialization.Serializable

@Serializable
data class SocialLoginResult(
    val tokens: AuthToken,
    val isRegistered: Boolean
)

@Serializable
private data class GoogleTokenInfo(
    val email: String,
    val name: String? = null
)

@Serializable
private data class KakaoUserInfo(
    val kakao_account: KakaoAccount? = null
)

@Serializable
private data class KakaoAccount(
    val email: String? = null,
    val profile: KakaoProfile? = null
)

@Serializable
private data class KakaoProfile(
    val nickname: String? = null
)

@Serializable
private data class NaverUserInfo(
    val response: NaverResponse? = null
)

@Serializable
private data class NaverResponse(
    val email: String? = null,
    val name: String? = null
)

class SocialLoginUseCase(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val httpClient: HttpClient
) {
    suspend operator fun invoke(
        provider: String,
        accessToken: String?,
        idToken: String?
    ): Result<SocialLoginResult> = runCatching {
        val (email, name) = when (provider.uppercase()) {
            "GOOGLE" -> verifyGoogle(idToken ?: throw Exception("ID Token is required for Google"))
            "KAKAO" -> verifyKakao(accessToken ?: throw Exception("Access Token is required for Kakao"))
            "NAVER" -> verifyNaver(accessToken ?: throw Exception("Access Token is required for Naver"))
            else -> throw Exception("Unsupported provider: $provider")
        }

        var user = userRepository.findByEmail(email)
        
        if (user == null) {
            val newUser = User(
                email = email,
                name = name ?: "User"
            )
            user = userRepository.save(newUser)
        }
        
        val tokens = jwtTokenProvider.generateToken(user.id)
        val isRegistered = user.handle != null
        
        SocialLoginResult(
            tokens = tokens,
            isRegistered = isRegistered
        )
    }

    private suspend fun verifyGoogle(idToken: String): Pair<String, String?> {
        val info = httpClient.get("https://oauth2.googleapis.com/tokeninfo?id_token=$idToken")
            .body<GoogleTokenInfo>()
        return info.email to info.name
    }

    private suspend fun verifyKakao(accessToken: String): Pair<String, String?> {
        val info = httpClient.get("https://kapi.kakao.com/v2/user/me") {
            header("Authorization", "Bearer $accessToken")
        }.body<KakaoUserInfo>()
        val email = info.kakao_account?.email ?: throw Exception("Email not provided by Kakao")
        return email to info.kakao_account.profile?.nickname
    }

    private suspend fun verifyNaver(accessToken: String): Pair<String, String?> {
        val info = httpClient.get("https://openapi.naver.com/v1/nid/me") {
            header("Authorization", "Bearer $accessToken")
        }.body<NaverUserInfo>()
        val email = info.response?.email ?: throw Exception("Email not provided by Naver")
        return email to info.response.name
    }
}
