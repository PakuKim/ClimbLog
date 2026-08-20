package io.paku.climblog.presentation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.messaging.messaging
import io.paku.climblog.business.domain.interactors.notification.SendDeviceTokenUseCase
import io.paku.climblog.business.domain.interactors.session.FetchSessionUseCase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class AppViewModel(
    private val fetchSessionUseCase: FetchSessionUseCase,
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
