package io.paku.climblog.business.domain.provider

import android.content.Context
import com.navercorp.nid.NidOAuth
import com.navercorp.nid.oauth.util.NidOAuthCallback
import io.paku.climblog.business.domain.model.SocialAuthResponse
import io.paku.climblog.business.domain.model.SocialAuthType
import io.paku.climblog.business.domain.provider.social.SocialLoginProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal class NaverLoginProviderImpl(
    private val context: Context
) : SocialLoginProvider {
    override suspend fun latestLoginResult(type: SocialAuthType): SocialAuthResponse {
        val accessToken = NidOAuth.getAccessToken() ?: return login(type)

        return SocialAuthResponse(
            type = type,
            token = accessToken
        )
    }

    override suspend fun login(type: SocialAuthType): SocialAuthResponse =
        suspendCancellableCoroutine { continuation ->
            val callback = object : NidOAuthCallback {
                override fun onSuccess() {
                    val accessToken = NidOAuth.getAccessToken()
                        ?: return continuation.resumeWithException(Exception("Access token not found"))
                    continuation.resume(
                        SocialAuthResponse(
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
                context = context,
                callback = callback
            )
        }

    override suspend fun logout(type: SocialAuthType) = suspendCancellableCoroutine { continuation ->
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