import Foundation
import shared
import GoogleSignIn
import KakaoSDKUser
import KakaoSDKAuth
import KakaoSDKCommon
import naveridlogin_sdk_ios

class IosSocialAuthBridge: SocialAuthDelegate {
    
    func login(provider: SocialProvider) async throws -> SocialAuthResult {
        switch provider {
        case .google:
            return try await loginWithGoogle()
        case .kakao:
            return try await loginWithKakao()
        case .naver:
            return try await loginWithNaver()
        default:
            throw NSError(domain: "SocialAuth", code: -1, userInfo: [NSLocalizedDescriptionKey: "Unsupported provider"])
        }
    }
    
    private func loginWithGoogle() async throws -> SocialAuthResult {
        return try await withCheckedThrowingContinuation { continuation in
            GIDSignIn.sharedInstance.signIn(withPresenting: getRootViewController()!) { result, error in
                if let error = error {
                    continuation.resume(throwing: error)
                    return
                }
                
                guard let user = result?.user else {
                    continuation.resume(throwing: NSError(domain: "Google", code: -1, userInfo: nil))
                    return
                }
                
                let authResult = SocialAuthResult(
                    provider: .google,
                    accessToken: nil,
                    idToken: user.idToken?.tokenString,
                    email: user.profile?.email ?? "",
                    name: user.profile?.name ?? "Google User"
                )
                continuation.resume(returning: authResult)
            }
        }
    }
    
    private func loginWithKakao() async throws -> SocialAuthResult {
        return try await withCheckedThrowingContinuation { continuation in
            let loginAction: ((OAuthToken?, Error?) -> Void) = { (token, error) in
                if let error = error {
                    continuation.resume(throwing: error)
                    return
                }
                
                UserApi.shared.me { (user, error) in
                    if let error = error {
                        continuation.resume(throwing: error)
                        return
                    }
                    
                    let authResult = SocialAuthResult(
                        provider: .kakao,
                        accessToken: token?.accessToken,
                        idToken: nil,
                        email: user?.kakaoAccount?.email ?? "",
                        name: user?.kakaoAccount?.profile?.nickname ?? "Kakao User"
                    )
                    continuation.resume(returning: authResult)
                }
            }
            
            if (UserApi.isKakaoTalkLoginAvailable()) {
                UserApi.shared.loginWithKakaoTalk(completion: loginAction)
            } else {
                UserApi.shared.loginWithKakaoAccount(completion: loginAction)
            }
        }
    }
    
    private func loginWithNaver() async throws -> SocialAuthResult {
        // Naver SDK for iOS is traditionally more delegate-heavy.
        // This is a simplified async wrapper concept.
        return try await withCheckedThrowingContinuation { continuation in
            let loginInstance = NaverThirdPartyLoginConnection.getSharedInstance()
            // Note: In real app, you need to set up a delegate to catch onSuccess/onFailure
            // For this bridge, we'll return a mock result as a template.
            // Actual implementation would involve a temporary delegate class.
            
            // Placeholder:
            let authResult = SocialAuthResult(
                provider: .naver,
                accessToken: loginInstance?.accessToken,
                idToken: nil,
                email: "", // Fetch via Naver API separately
                name: "Naver User"
            )
            continuation.resume(returning: authResult)
        }
    }
    
    private func getRootViewController() -> UIViewController? {
        return UIApplication.shared.windows.first?.rootViewController
    }
}
