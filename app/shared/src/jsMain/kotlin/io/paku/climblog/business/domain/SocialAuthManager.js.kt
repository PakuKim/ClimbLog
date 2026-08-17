package io.paku.climblog.business.domain

import io.paku.climblog.business.domain.model.SocialAuthResult
import io.paku.climblog.business.domain.model.SocialProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@JsName("google")
external object GoogleAuth {
    val accounts: dynamic
}

@JsName("Kakao")
external object KakaoAuth {
    fun init(key: String)
    fun isInitialized(): Boolean
    val Auth: dynamic
    val API: dynamic
}

@JsName("naver")
external object NaverAuth {
    val LoginWithNaverId: dynamic
}

actual class SocialAuthManager actual constructor() {
    private companion object {
        const val GOOGLE_CLIENT_ID = ""
        const val KAKAO_JS_KEY = ""
        const val NAVER_CLIENT_ID = ""
        const val NAVER_CALLBACK_URL = ""
    }

    actual suspend fun login(provider: SocialProvider): Result<SocialAuthResult> {
        return when (provider) {
            SocialProvider.GOOGLE -> loginGoogle()
            SocialProvider.KAKAO -> loginKakao()
            SocialProvider.NAVER -> loginNaver()
        }
    }

    private suspend fun loginGoogle(): Result<SocialAuthResult> = suspendCancellableCoroutine { continuation ->
        try {
            val client = GoogleAuth.accounts.oauth2.initTokenClient(js("""({
                client_id: '$GOOGLE_CLIENT_ID',
                scope: 'email profile',
                callback: (response) => {
                    if (response.access_token) {
                        continuation.resume(Result.success(
                            SocialAuthResult(
                                provider = SocialProvider.GOOGLE,
                                accessToken = response.access_token,
                                idToken = null,
                                email = "", 
                                name = "Google Web User"
                            )
                        ))
                    } else {
                        continuation.resume(Result.failure(Exception("Google login failed")))
                    }
                }
            })"""))
            client.requestAccessToken()
        } catch (e: Exception) {
            continuation.resume(Result.failure(e))
        }
    }

    private suspend fun loginKakao(): Result<SocialAuthResult> = suspendCancellableCoroutine { continuation ->
        if (!KakaoAuth.isInitialized()) {
            KakaoAuth.init(KAKAO_JS_KEY)
        }

        KakaoAuth.Auth.login(js("""({
            success: function(authObj) {
                KakaoAuth.API.request({
                    url: '/v2/user/me',
                    success: function(res) {
                        continuation.resume(Result.success(
                            SocialAuthResult(
                                provider = SocialProvider.KAKAO,
                                accessToken = authObj.access_token,
                                idToken = null,
                                email = res.kakao_account.email,
                                name = res.kakao_account.profile.nickname
                            )
                        ))
                    },
                    fail: function(error) {
                        continuation.resume(Result.failure(Exception("Kakao API failed: " + JSON.stringify(error))))
                    }
                })
            },
            fail: function(err) {
                continuation.resume(Result.failure(Exception("Kakao Login failed: " + JSON.stringify(err))))
            }
        })"""))
    }

    private suspend fun loginNaver(): Result<SocialAuthResult> = suspendCancellableCoroutine { continuation ->
        try {
            val naverLogin = js("""new NaverAuth.LoginWithNaverId({
                clientId: '$NAVER_CLIENT_ID',
                callbackUrl: '$NAVER_CALLBACK_URL',
                isPopup: true,
                loginButton: { color: "green", type: 3, height: 60 }
            })""")
            
            naverLogin.init()
            
            // Naver JS SDK usually requires a callback page.
            continuation.resume(Result.failure(Exception("Naver Web Login requires callback page setup")))
        } catch (e: Exception) {
            continuation.resume(Result.failure(e))
        }
    }
}
