package io.paku.climblog.business.domain

import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import io.paku.climblog.business.domain.model.SocialAuthResult
import io.paku.climblog.business.domain.model.SocialProvider
import io.paku.climblog.core.ActivityProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

actual class SocialAuthManager actual constructor() {
    private companion object {
        // TODO: Add your Google Web Client ID here
        const val GOOGLE_WEB_CLIENT_ID = ""
    }

    actual suspend fun login(provider: SocialProvider): Result<SocialAuthResult> {
        val activity = ActivityProvider.getActivity() ?: return Result.failure(Exception("Activity not found"))
        
        return when (provider) {
            SocialProvider.GOOGLE -> loginGoogle(activity)
            SocialProvider.KAKAO -> loginKakao(activity)
            SocialProvider.NAVER -> loginNaver(activity)
        }
    }

    private suspend fun loginGoogle(activity: android.app.Activity): Result<SocialAuthResult> = runCatching {
        val credentialManager = CredentialManager.create(activity)
        
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts = false)
            .setServerClientId(GOOGLE_WEB_CLIENT_ID)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result = credentialManager.getCredential(activity, request)
        val credential = result.credential
        
        if (credential is GoogleIdTokenCredential) {
            SocialAuthResult(
                provider = SocialProvider.GOOGLE,
                accessToken = null,
                idToken = credential.idToken,
                email = credential.id,
                name = credential.displayName ?: "Google User",
            )
        } else {
            throw Exception("Invalid credential type")
        }
    }

    private suspend fun loginKakao(activity: android.app.Activity): Result<SocialAuthResult> = suspendCancellableCoroutine { continuation ->
        val callback: (com.kakao.sdk.auth.model.OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                continuation.resume(Result.failure(error))
            } else if (token != null) {
                UserApiClient.instance.me { user, userError ->
                    if (user != null) {
                        continuation.resume(
                            Result.success(
                                SocialAuthResult(
                                    provider = SocialProvider.KAKAO,
                                    accessToken = token.accessToken,
                                    idToken = null,
                                    email = user.kakaoAccount?.email ?: "",
                                    name = user.kakaoAccount?.profile?.nickname ?: "Kakao User",
                                )
                            )
                        )
                    } else {
                        continuation.resume(Result.failure(userError ?: Exception("Kakao user info not found")))
                    }
                }
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(activity)) {
            UserApiClient.instance.loginWithKakaoTalk(activity, callback = callback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(activity, callback = callback)
        }
    }

    private suspend fun loginNaver(activity: android.app.Activity): Result<SocialAuthResult> = suspendCancellableCoroutine { continuation ->
        val oauthLoginCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                val accessToken = NaverIdLoginSDK.getAccessToken()
                continuation.resume(
                    Result.success(
                        SocialAuthResult(
                            provider = SocialProvider.NAVER,
                            accessToken = accessToken,
                            idToken = null,
                            email = "", // Fetch via server-side or Naver profile API
                            name = "Naver User",
                        )
                    )
                )
            }
            override fun onFailure(httpStatus: Int, message: String) {
                continuation.resume(Result.failure(Exception("Naver login failed: $message")))
            }
            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        }
        
        NaverIdLoginSDK.authenticate(activity, oauthLoginCallback)
    }
}
