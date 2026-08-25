package io.paku.climblog.provider.social

import com.navercorp.nid.NidOAuth
import com.navercorp.nid.oauth.util.NidOAuthCallback
import io.paku.climblog.business.domain.model.SocialLoginResult
import io.paku.climblog.business.domain.model.SocialLoginType
import io.paku.climblog.core.ActivityProvider
import io.paku.climblog.core.SocialLoginProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal class NaverLoginProviderImpl : SocialLoginProvider {
    override suspend fun latestLoginResult(type: SocialLoginType): SocialLoginResult {
        val accessToken = NidOAuth.getAccessToken() ?: return login(type)

        return SocialLoginResult(
            type = type,
            token = accessToken
        )
    }

    override suspend fun login(type: SocialLoginType): SocialLoginResult =
        suspendCancellableCoroutine { continuation ->
            val activity = ActivityProvider.getActivity()
                ?: return@suspendCancellableCoroutine continuation.resumeWithException(Exception(""))
            val callback = object : NidOAuthCallback {
                override fun onSuccess() {
                    val accessToken = NidOAuth.getAccessToken()
                        ?: return continuation.resumeWithException(Exception("Access token not found"))
                    continuation.resume(
                        SocialLoginResult(
                            type = type,
                            token = accessToken
                        )
                    )
                }

                override fun onFailure(errorCode: String, errorDesc: String) {
                    continuation.resumeWithException(Exception("Naver login failed: $errorDesc"))
                }
            }

            NidOAuth.requestLogin(
                context = activity,
                callback = callback
            )
        }

    override suspend fun logout(type: SocialLoginType) =
        suspendCancellableCoroutine { continuation ->
            val callback = object : NidOAuthCallback {
                override fun onSuccess() {
                    continuation.resume(Unit)
                }

                override fun onFailure(errorCode: String, errorDesc: String) {
                    continuation.resumeWithException(Exception("Naver logout failed: $errorDesc"))
                }
            }

            NidOAuth.logout(callback)
        }

}