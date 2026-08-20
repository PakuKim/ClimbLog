package io.paku.climblog.presentation.ui.onboard.register

import io.paku.climblog.presentation.base.Event

sealed class RegisterViewModelEvent : Event {
    data class OnNameChanged(val name: String) : RegisterViewModelEvent()
    data class OnHandleChanged(val handle: String) : RegisterViewModelEvent()
    data class OnAgeChanged(val age: String) : RegisterViewModelEvent()
    data class OnHeightChanged(val height: String) : RegisterViewModelEvent()
    data class OnArmReachChanged(val armReach: String) : RegisterViewModelEvent()
    data class OnGenderChanged(val gender: String) : RegisterViewModelEvent()
    data class OnProfileImagePicked(val bytes: ByteArray?) : RegisterViewModelEvent()
    object OnCheckHandle : RegisterViewModelEvent()
    object OnRegisterSubmit : RegisterViewModelEvent()
}
