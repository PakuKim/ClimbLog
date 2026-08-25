package io.paku.climblog.provider.social

import android.content.Context
import com.kakao.sdk.auth.TokenManagerProvider
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import io.paku.climblog.business.domain.model.SocialLoginResult
import io.paku.climblog.business.domain.model.SocialLoginType
import io.paku.climblog.core.ActivityProvider
import io.paku.climblog.core.SocialLoginProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

internal class KakaoLoginProviderImpl(
    private val context: Context
) : SocialLoginProvider {
    companion object {
        private const val USER_CANCELLED = "user cancelled."
    }

    override suspend fun latestLoginResult(type: SocialLoginType): SocialLoginResult {
        val authToken = TokenManagerProvider.instance.manager.getToken() ?: return login(type)
        return accessTokenToResult(authToken.accessToken)
    }

    override suspend fun login(type: SocialLoginType): SocialLoginResult {
        val activity = ActivityProvider.getActivity() ?: throw IllegalStateException("Activity not found")
        logout(type)
        val authToken = loginWithKakao(activity)
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
                    return@loginWithKakaoTalk
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
                    return@loginWithKakaoAccount
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
                continuation.resumeWithException(it)
                return@logout
            }
            continuation.resumeWith(Result.success(Unit))
        }
    }
}