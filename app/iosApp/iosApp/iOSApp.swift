import SwiftUI
import shared
import KakaoSDKCommon
import KakaoSDKAuth
import GoogleSignIn
import naveridlogin_sdk_ios

@main
struct iOSApp: App {
    
    init() {
        // Kakao SDK Init
        KakaoSdk.initSDK(appKey: "YOUR_KAKAO_NATIVE_APP_KEY")
        
        // Naver SDK Init
        let naverLogin = NaverThirdPartyLoginConnection.getSharedInstance()
        naverLogin?.isNaverAppOauthEnable = true
        naverLogin?.isInAppOauthEnable = true
        naverLogin?.serviceUrlScheme = "climblog"
        naverLogin?.consumerKey = "YOUR_NAVER_CLIENT_ID"
        naverLogin?.consumerSecret = "YOUR_NAVER_CLIENT_SECRET"
        naverLogin?.appName = "ClimbLog"
        
        // Register Social Auth Delegate to Kotlin Shared Module
        SocialAuthManager_iosKt.setSocialAuthDelegate(delegate: IosSocialAuthBridge())
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    // Handle Kakao Login Redirect
                    if (AuthApi.isKakaoTalkLoginUrl(url)) {
                        _ = AuthController.handleOpenUrl(url: url)
                    }
                    
                    // Handle Google Login Redirect
                    _ = GIDSignIn.sharedInstance.handle(url)
                    
                    // Handle Naver Login Redirect
                    NaverThirdPartyLoginConnection.getSharedInstance().receiveAccessToken(url)
                }
        }
    }
}
