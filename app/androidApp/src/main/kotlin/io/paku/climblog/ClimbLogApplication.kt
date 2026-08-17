package io.paku.climblog

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NaverIdLoginSDK

class ClimbLogApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // TODO: Add your Kakao App Key here
        KakaoSdk.init(this, "")
        
        // TODO: Add your Naver Client info here
        NaverIdLoginSDK.initialize(
            this, 
            "", 
            "", 
            "ClimbLog"
        )
    }
}
