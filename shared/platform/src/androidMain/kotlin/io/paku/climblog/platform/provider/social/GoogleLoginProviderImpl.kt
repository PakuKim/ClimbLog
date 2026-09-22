package io.paku.climblog.platform.provider.social

import androidx.activity.ComponentActivity
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import io.paku.climblog.platform.util.ActivityUtil

internal class GoogleLoginProviderImpl(
    private val serverClientId: String
) : io.paku.climblog.domain.provider.social.SocialLoginProvider {
    private val activity: ComponentActivity by lazy {
        ActivityUtil.getActivity() ?: throw IllegalStateException("Activity not found")
    }

    private val credentialManager: CredentialManager by lazy {
        CredentialManager.create(activity)
    }

    private val googleIdOption: GetGoogleIdOption by lazy {
        GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts = false)
            .setServerClientId(serverClientId)
            .setAutoSelectEnabled(false)
            .build()
    }

    private val credentialRequest: GetCredentialRequest by lazy {
        GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

    override suspend fun latestLoginResult(type: io.paku.climblog.domain.model.social.SocialLoginType): io.paku.climblog.domain.model.social.SocialLoginResult {
        return loginInternal()
    }

    override suspend fun login(type: io.paku.climblog.domain.model.social.SocialLoginType): io.paku.climblog.domain.model.social.SocialLoginResult {
        logout(type)
        return loginInternal()
    }

    private suspend fun loginInternal(): io.paku.climblog.domain.model.social.SocialLoginResult {
        val result = credentialManager.getCredential(activity, credentialRequest)
        val credential = result.credential
        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

        return _root_ide_package_.io.paku.climblog.domain.model.social.SocialLoginResult(
            type = io.paku.climblog.domain.model.social.SocialLoginType.GOOGLE,
            token = googleIdTokenCredential.idToken
        )
    }

    override suspend fun logout(type: io.paku.climblog.domain.model.social.SocialLoginType) {
        return credentialManager.clearCredentialState(ClearCredentialStateRequest())
    }
}
