package io.paku.climblog

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NidOAuth

internal class ClimbLogApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(
            context = this,
            appKey = BuildConfig.KAKAO_NATIVE_APP_KEY
        )

        NidOAuth.initialize(
            context = this,
            clientId = BuildConfig.NAVER_CLIENT_ID,
            clientSecret = BuildConfig.NAVER_CLIENT_SECRET,
            clientName = this.getString(R.string.app_name),
        )
    }
}
