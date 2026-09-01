package io.paku.climblog.presentation.ui.onboard.register

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import io.paku.climblog.business.domain.interactors.auth.SocialRegisterUseCase
import io.paku.climblog.business.domain.interactors.user.CheckHandleUseCase
import io.paku.climblog.business.domain.model.SocialLoginType
import io.paku.climblog.core.Media
import io.paku.climblog.presentation.base.BaseViewModel
import io.paku.climblog.presentation.base.ViewModelEvent
import io.paku.climblog.presentation.base.ViewModelState
import io.paku.climblog.presentation.navigation.AppNavigation

data class RegisterViewModelState(
    val name: String = "",
    val handle: String = "",
    val handleChecked: Boolean? = null,
    val age: String = "",
    val height: String = "",
    val armReach: String = "",
    val gender: String = "M",
    val profileImage: Media.Image? = null,
    val socialLoginType: SocialLoginType = SocialLoginType.GOOGLE
): ViewModelState {
    val registrationAvailable: Boolean =
        name.isNotBlank() && handleChecked == true && age.isNotBlank() && height.isNotBlank() && armReach.isNotBlank()
}

sealed class RegisterViewModelEvent : ViewModelEvent {
    data class OnNameChanged(
        val name: String
    ) : RegisterViewModelEvent()

    data class OnHandleChanged(
        val handle: String
    ) : RegisterViewModelEvent()

    data class OnAgeChanged(
        val age: String
    ) : RegisterViewModelEvent()

    data class OnHeightChanged(
        val height: String
    ) : RegisterViewModelEvent()

    data class OnArmReachChanged(
        val armReach: String
    ) : RegisterViewModelEvent()

    data class OnGenderChanged(
        val gender: String
    ) : RegisterViewModelEvent()

    data class OnProfileImageChanged(
        val image: Media.Image
    ) : RegisterViewModelEvent()

    object OnHandleCheckClick : RegisterViewModelEvent()
    object OnRegisterClick : RegisterViewModelEvent()
}

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
            is RegisterViewModelEvent.OnHandleChanged -> updateState { copy(handle = event.handle, handleChecked = false) }
            is RegisterViewModelEvent.OnAgeChanged -> updateState { copy(age = event.age) }
            is RegisterViewModelEvent.OnHeightChanged -> updateState { copy(height = event.height) }
            is RegisterViewModelEvent.OnArmReachChanged -> updateState { copy(armReach = event.armReach) }
            is RegisterViewModelEvent.OnGenderChanged -> updateState { copy(gender = event.gender) }
            is RegisterViewModelEvent.OnProfileImageChanged -> updateState { copy(profileImage = event.image) }
            is RegisterViewModelEvent.OnHandleCheckClick -> checkHandle()
            is RegisterViewModelEvent.OnRegisterClick -> registration()
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
            copy(handleChecked = !exists)
        }
    }

    private fun registration() = launchWithLoading {
        val state = this@RegisterViewModel.state.value
        if (!state.registrationAvailable) return@launchWithLoading

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
            profilePhotoUrl = null
        )
    }
}
