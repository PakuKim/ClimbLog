package io.paku.kmp_template

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.paku.kmp_template.business.domain.interactors.session.FetchSessionUseCase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class AppViewModel(
    private val fetchSessionUseCase: FetchSessionUseCase,
): ViewModel() {
    private val _isAuthorized: MutableState<Boolean> = mutableStateOf(false)
    val authorized = _isAuthorized

    init {
        viewModelScope.launch {
            fetchSessionUseCase().collectLatest {
                _isAuthorized.value = (it != null)
            }
        }
    }
}