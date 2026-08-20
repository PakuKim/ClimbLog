package io.paku.climblog.business.domain.provider

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import io.paku.climblog.business.domain.model.SocialAuthResponse
import io.paku.climblog.business.domain.model.SocialAuthType
import io.paku.climblog.business.domain.provider.social.SocialLoginProvider

internal class GoogleLoginProviderImpl(
    private val context: Context,
) : SocialLoginProvider {
    private val credentialManager: CredentialManager by lazy {
        CredentialManager.create(context)
    }

    private val googleIdOption: GetGoogleIdOption by lazy {
        GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts = false)
            .setServerClientId("GOOGLE_WEB_CLIENT_ID")
            .setAutoSelectEnabled(true)
            .build()
    }

    private val credentialRequest: GetCredentialRequest by lazy {
        GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

    override suspend fun latestLoginResult(type: SocialAuthType): SocialAuthResponse {
        return login(type)
    }

    override suspend fun login(type: SocialAuthType): SocialAuthResponse {
        logout(type)
        return loginInternal()
    }

    private suspend fun loginInternal(): SocialAuthResponse {
        val result = credentialManager.getCredential(context, credentialRequest)
        val credential = result.credential
        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

        return SocialAuthResponse(
            type = SocialAuthType.GOOGLE,
            token = googleIdTokenCredential.idToken
        )
    }

    override suspend fun logout(type: SocialAuthType) {
        return credentialManager.clearCredentialState(ClearCredentialStateRequest())
    }
}