package io.paku.climblog.presentation.ui.onboard.register

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import io.paku.climblog.business.domain.interactors.auth.SocialRegisterUseCase
import io.paku.climblog.business.domain.interactors.user.CheckHandleUseCase
import io.paku.climblog.business.domain.model.SocialLoginType
import io.paku.climblog.presentation.base.BaseViewModel
import io.paku.climblog.presentation.base.ViewModelEvent
import io.paku.climblog.presentation.navigation.AppNavigation

internal class RegisterViewModel(
    savedStateHandle: SavedStateHandle,
    private val checkHandleUseCase: CheckHandleUseCase,
    private val socialRegisterUseCase: SocialRegisterUseCase
) : BaseViewModel<RegisterViewModelState, RegisterViewModelEvent, Nothing>() {
    private val registerArgs: AppNavigation.Register = savedStateHandle.toRoute()
    val socialLoginType: SocialLoginType = registerArgs.socialLoginType

    fun init(
        socialLoginType: SocialLoginType
    ) {
        updateState {
            copy(
                socialLoginType = socialLoginType,
            )
        }
    }

    override fun createInitialState(): RegisterViewModelState = RegisterViewModelState()

    fun onEvent(event: RegisterViewModelEvent) {
        when (event) {
            is RegisterViewModelEvent.OnNameChanged -> updateState { copy(name = event.name) }
            is RegisterViewModelEvent.OnHandleChanged -> updateState {
                copy(handle = event.handle, isHandleChecked = false)
            }
            is RegisterViewModelEvent.OnAgeChanged -> updateState { copy(age = event.age) }
            is RegisterViewModelEvent.OnHeightChanged -> updateState { copy(height = event.height) }
            is RegisterViewModelEvent.OnArmReachChanged -> updateState { copy(armReach = event.armReach) }
            is RegisterViewModelEvent.OnGenderChanged -> updateState { copy(gender = event.gender) }
            is RegisterViewModelEvent.OnProfileImagePicked -> updateState { copy(profileImageBytes = event.bytes) }
            is RegisterViewModelEvent.OnHandleCheckClick -> checkHandle()
            is RegisterViewModelEvent.OnRegisterClick -> submitRegistration()
        }
    }

    override fun createTriggerEvent(event: ViewModelEvent) {
        if (event is RegisterViewModelEvent) {
            onEvent(event)
        }
    }

    private fun checkHandle() = launchWithLoading {
        val handle = state.value.handle
        if (handle.isBlank()) return@launchWithLoading

        val exists = checkHandleUseCase(handle)
        updateState {
            copy(isHandleChecked = true, isHandleAvailable = !exists)
        }
    }

    private fun submitRegistration() = launchWithLoading {
        val state = this@RegisterViewModel.state.value
        if (!state.isHandleAvailable) return@launchWithLoading

        val ageInt = state.age.toInt()
        val heightInt = state.height.toInt()
        val armReachInt = state.armReach.toInt()

        socialRegisterUseCase(
            type = socialLoginType,
            handle = state.handle,
            name = state.name,
            age = ageInt,
            height = heightInt,
            armReach = armReachInt,
            gender = state.gender,
            profilePhotoUrl = state.profilePhotoUrl
        )
    }
}
