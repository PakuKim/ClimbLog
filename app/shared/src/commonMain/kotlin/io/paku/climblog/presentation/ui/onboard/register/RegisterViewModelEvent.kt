package io.paku.climblog.presentation.ui.onboard.register

import io.paku.climblog.presentation.base.ViewModelEvent

sealed class RegisterViewModelEvent : ViewModelEvent {
    data class OnNameChanged(val name: String) : RegisterViewModelEvent()
    data class OnHandleChanged(val handle: String) : RegisterViewModelEvent()
    data class OnAgeChanged(val age: String) : RegisterViewModelEvent()
    data class OnHeightChanged(val height: String) : RegisterViewModelEvent()
    data class OnArmReachChanged(val armReach: String) : RegisterViewModelEvent()
    data class OnGenderChanged(val gender: String) : RegisterViewModelEvent()
    data class OnProfileImagePicked(val bytes: ByteArray?) : RegisterViewModelEvent()
    object OnHandleCheckClick : RegisterViewModelEvent()
    object OnRegisterClick : RegisterViewModelEvent()
}
