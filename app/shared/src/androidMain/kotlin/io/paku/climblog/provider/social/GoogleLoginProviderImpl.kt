package io.paku.climblog.provider.social

import androidx.activity.ComponentActivity
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import io.paku.climblog.BuildKonfig
import io.paku.climblog.business.domain.model.SocialLoginResult
import io.paku.climblog.business.domain.model.SocialLoginType
import io.paku.climblog.core.ActivityProvider
import io.paku.climblog.core.SocialLoginProvider

internal class GoogleLoginProviderImpl : SocialLoginProvider {
    private val activity: ComponentActivity by lazy {
        ActivityProvider.getActivity() ?: throw IllegalStateException("Activity not found")
    }

    private val credentialManager: CredentialManager by lazy {
        CredentialManager.create(activity)
    }

    private val googleIdOption: GetGoogleIdOption by lazy {
        GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts = false)
            .setServerClientId(BuildKonfig.GOOGLE_WEB_CLIENT_ID)
            .setAutoSelectEnabled(false)
            .build()
    }

    private val credentialRequest: GetCredentialRequest by lazy {
        GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

    override suspend fun latestLoginResult(type: SocialLoginType): SocialLoginResult {
        return loginInternal()
    }

    override suspend fun login(type: SocialLoginType): SocialLoginResult {
        logout(type)
        return loginInternal()
    }

    private suspend fun loginInternal(): SocialLoginResult {
        val result = credentialManager.getCredential(activity, credentialRequest)
        val credential = result.credential
        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

        return SocialLoginResult(
            type = SocialLoginType.GOOGLE,
            token = googleIdTokenCredential.idToken
        )
    }

    override suspend fun logout(type: SocialLoginType) {
        return credentialManager.clearCredentialState(ClearCredentialStateRequest())
    }
}