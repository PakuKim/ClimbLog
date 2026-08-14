package io.paku.climblog.presentation.base

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.paku.climblog.business.model.CommonException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel<STATE : State, EVENT : Event>: ViewModel() {
    private val initialState: STATE by lazy { createInitialState() }
    private val _state: MutableState<STATE> = mutableStateOf(initialState)
    val state = _state

    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()

    private val _error: MutableSharedFlow<String> = MutableSharedFlow()
    val error: SharedFlow<String> = _error.asSharedFlow()

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    protected abstract fun createInitialState(): STATE
    protected abstract fun createTriggerEvent(event: Event)

    init {
        subscribeEvents()
    }

    protected val ceh = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        setError(
            when (throwable) {
                is CommonException -> {
                    throwable.message
                }

                else -> {
                    "오류가 발생하였습니다."
                }
            }
        )
        setLoading(false)
    }

    private fun subscribeEvents() {
        viewModelScope.launch {
            _event.collect {
                createTriggerEvent(it)
            }
        }
    }

    protected inline fun launch(
        coroutineContext: CoroutineContext = ceh,
        crossinline action: suspend CoroutineScope.() -> Unit,
    ): Job {
        return viewModelScope.launch(coroutineContext) {
            action(this)
        }
    }

    protected fun launchWithLoading(
        coroutineContext: CoroutineContext = ceh,
        action: suspend CoroutineScope.() -> Unit,
    ): Job {
        return viewModelScope.launch(coroutineContext) {
            setLoading(true).join()
            action(this)
            setLoading(false).join()
        }
    }

    protected fun updateState(action: STATE.() -> STATE) {
        val newState = state.value.action()
        _state.value = newState
    }

    private fun setError(errorMsg: String) = viewModelScope.launch {
        _error.emit(errorMsg)
    }

    protected fun setLoading(isVisible: Boolean) = viewModelScope.launch {
        _isLoading.emit(isVisible)
    }
}