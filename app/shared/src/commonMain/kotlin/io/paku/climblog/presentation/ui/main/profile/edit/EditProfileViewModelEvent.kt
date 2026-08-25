package io.paku.climblog.presentation.ui.main.profile.edit

import io.paku.climblog.presentation.base.ViewModelEvent

sealed class EditProfileViewModelEvent: ViewModelEvent {
    data class OnNameChanged(val name: String) : EditProfileViewModelEvent()
    data class OnAgeChanged(val age: String) : EditProfileViewModelEvent()
    data class OnHeightChanged(val height: String) : EditProfileViewModelEvent()
    data class OnArmReachChanged(val armReach: String) : EditProfileViewModelEvent()
    data class OnGenderChanged(val gender: String) : EditProfileViewModelEvent()
    data class OnProfileImageChanged(val bytes: ByteArray?) : EditProfileViewModelEvent()
    object OnUpdateSubmit : EditProfileViewModelEvent()
}