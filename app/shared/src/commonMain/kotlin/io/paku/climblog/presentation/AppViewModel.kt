package io.paku.climblog.presentation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.messaging.messaging
import io.paku.climblog.business.domain.interactors.auth.LoginUseCase
import io.paku.climblog.business.domain.interactors.notification.SendDeviceTokenUseCase
import io.paku.climblog.business.domain.interactors.session.FetchSessionUseCase
import io.paku.climblog.business.domain.interactors.user.FetchUserUseCase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class AppViewModel(
    private val fetchSessionUseCase: FetchSessionUseCase,
    private val loginUseCase: LoginUseCase,
    private val fetchUserUseCase: FetchUserUseCase,
    private val sendDeviceTokenUseCase: SendDeviceTokenUseCase
): ViewModel() {
    private val _isAuthorized: MutableState<Boolean> = mutableStateOf(false)
    val authorized = _isAuthorized

    private fun initFcm() = viewModelScope.launch {
        try {
            val token = Firebase.messaging.getToken()
            if (_isAuthorized.value) {
                sendDeviceTokenUseCase(token)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loginClick() = viewModelScope.launch {
        loginUseCase(
            email = "test@example.com",
            password = "password123!"
        )
    }

    fun userClick() = viewModelScope.launch {
        fetchUserUseCase()
    }

    init {
        viewModelScope.launch {
            fetchSessionUseCase().collectLatest {
                _isAuthorized.value = (it != null)
                if (it != null) {
                    initFcm()
                }
            }
        }
    }
}
