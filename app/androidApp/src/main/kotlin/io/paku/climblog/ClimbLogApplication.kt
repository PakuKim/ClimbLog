package io.paku.climblog

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NidOAuth

class ClimbLogApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // TODO: Add your Kakao App Key here
        KakaoSdk.init(
            context = this,
            appKey = BuildConfig.KAKAO_NATIVE_APP_KEY
        )
        
        // TODO: Add your Naver Client info here
        NidOAuth.initialize(
            context = this,
            clientId = "",
            clientSecret = "",
            clientName = "ClimbLog",
        )
    }
}
