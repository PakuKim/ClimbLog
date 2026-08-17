package io.paku.climblog.business.domain

import io.paku.climblog.business.domain.model.SocialAuthResult
import io.paku.climblog.business.domain.model.SocialProvider
import kotlin.native.concurrent.ThreadLocal

/**
 * iOS implementation of SocialAuthManager.
 * Since native SDKs (Google, Kakao, Naver) for iOS are best managed via Swift/Objective-C in Xcode,
 * this implementation uses a delegate pattern to bridge with the native iOS app.
 */

@ThreadLocal
object SocialAuthDelegateHolder {
    var delegate: SocialAuthDelegate? = null
}

fun setSocialAuthDelegate(delegate: SocialAuthDelegate) {
    SocialAuthDelegateHolder.delegate = delegate
}

actual class SocialAuthManager actual constructor() {
    
    actual suspend fun login(provider: SocialProvider): Result<SocialAuthResult> = runCatching {
        val delegate = SocialAuthDelegateHolder.delegate 
            ?: throw Exception("SocialAuthDelegate not initialized. Please call setSocialAuthDelegate in Swift.")
            
        delegate.login(provider)
    }
}
