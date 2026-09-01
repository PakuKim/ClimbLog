package io.paku.climblog.provider.social

import android.content.Context
import com.kakao.sdk.auth.TokenManagerProvider
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import io.paku.climblog.business.domain.model.SocialLoginResult
import io.paku.climblog.business.domain.model.SocialLoginType
import io.paku.climblog.business.domain.provider.social.SocialLoginProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

internal class KakaoLoginProviderImpl(
    private val applicationContext: Context
) : SocialLoginProvider {
    companion object {
        private const val USER_CANCELLED = "user cancelled."
    }

    override suspend fun latestLoginResult(type: SocialLoginType): SocialLoginResult {
        val authToken = TokenManagerProvider.instance.manager.getToken() ?: return login(type)
        return accessTokenToResult(authToken.accessToken)
    }

    override suspend fun login(type: SocialLoginType): SocialLoginResult {
        logout(type)
        val authToken = loginWithKakao(applicationContext)
        return accessTokenToResult(authToken.accessToken)
    }

    private suspend fun loginWithKakao(context: Context): OAuthToken {
        return if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            runCatching {
                loginWithKakaoApp(context)
            }.recoverCatching {
                loginWithKakaoWeb(context)
            }.getOrThrow()
        } else {
            loginWithKakaoWeb(context)
        }
    }

    private suspend fun loginWithKakaoApp(context: Context): OAuthToken =
        suspendCancellableCoroutine { continuation ->
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                error?.let {
                    if (it.message == USER_CANCELLED) return@loginWithKakaoTalk
                    continuation.resumeWithException(Exception(it))
                }
                continuation.resumeWith(Result.success(token ?: return@loginWithKakaoTalk))
            }
        }

    private suspend fun loginWithKakaoWeb(context: Context): OAuthToken =
        suspendCancellableCoroutine { continuation ->
            UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
                error?.let {
                    if (it.message == USER_CANCELLED) return@loginWithKakaoAccount
                    continuation.resumeWithException(Exception(it))
                }
                continuation.resumeWith(Result.success(token ?: return@loginWithKakaoAccount))
            }
        }

    private suspend fun accessTokenToResult(accessToken: String) =
        suspendCancellableCoroutine { continuation ->
            UserApiClient.instance.me { _, error ->
                error?.let {
                    continuation.resumeWithException(it)
                    return@me
                }
                continuation.resumeWith(
                    Result.success(
                        SocialLoginResult(
                            type = SocialLoginType.KAKAO,
                            token = accessToken,
                        )
                    )
                )
            }
        }

    override suspend fun logout(type: SocialLoginType) = suspendCancellableCoroutine { continuation ->
        UserApiClient.instance.logout {
            it?.let {
                if (it is ClientError && it.reason == ClientErrorCause.TokenNotFound) {
                    return@let
                }
                continuation.resumeWithException(it)
            }
            continuation.resumeWith(Result.success(Unit))
        }
    }
}