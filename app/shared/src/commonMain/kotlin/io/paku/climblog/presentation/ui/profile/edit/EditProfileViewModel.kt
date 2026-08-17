package io.paku.climblog.presentation.ui.profile.edit

import io.paku.climblog.business.domain.interactors.user.FetchUserUseCase
import io.paku.climblog.business.domain.interactors.user.UpdateProfileUseCase
import io.paku.climblog.presentation.base.BaseViewModel
import io.paku.climblog.presentation.base.Event
import io.paku.climblog.presentation.base.State

data class EditProfileState(
    val name: String = "",
    val age: String = "",
    val height: String = "",
    val armReach: String = "",
    val gender: String = "M",
    val profilePhotoUrl: String? = null,
    val profileImageBytes: ByteArray? = null,
    val updateSuccess: Boolean = false
) : State {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as EditProfileState
        if (name != other.name) return false
        if (age != other.age) return false
        if (height != other.height) return false
        if (armReach != other.armReach) return false
        if (gender != other.gender) return false
        if (profilePhotoUrl != other.profilePhotoUrl) return false
        if (profileImageBytes != null) {
            if (other.profileImageBytes == null) return false
            if (!profileImageBytes.contentEquals(other.profileImageBytes)) return false
        } else if (other.profileImageBytes != null) return false
        if (updateSuccess != other.updateSuccess) return false
        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + age.hashCode()
        result = 31 * result + height.hashCode()
        result = 31 * result + armReach.hashCode()
        result = 31 * result + gender.hashCode()
        result = 31 * result + (profilePhotoUrl?.hashCode() ?: 0)
        result = 31 * result + (profileImageBytes?.contentHashCode() ?: 0)
        result = 31 * result + updateSuccess.hashCode()
        return result
    }
}

sealed class EditProfileEvent : Event {
    data class OnNameChanged(val name: String) : EditProfileEvent()
    data class OnAgeChanged(val age: String) : EditProfileEvent()
    data class OnHeightChanged(val height: String) : EditProfileEvent()
    data class OnArmReachChanged(val armReach: String) : EditProfileEvent()
    data class OnGenderChanged(val gender: String) : EditProfileEvent()
    data class OnProfileImagePicked(val bytes: ByteArray?) : EditProfileEvent()
    object OnUpdateSubmit : EditProfileEvent()
}

class EditProfileViewModel(
    private val fetchUserUseCase: FetchUserUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase
) : BaseViewModel<EditProfileState, EditProfileEvent>() {

    override fun createInitialState(): EditProfileState = EditProfileState()

    override fun createTriggerEvent(event: Event) {
        if (event is EditProfileEvent) {
            onEvent(event)
        }
    }

    fun onEvent(event: EditProfileEvent) {
        when (event) {
            is EditProfileEvent.OnNameChanged -> updateState { copy(name = event.name) }
            is EditProfileEvent.OnAgeChanged -> updateState { copy(age = event.age) }
            is EditProfileEvent.OnHeightChanged -> updateState { copy(height = event.height) }
            is EditProfileEvent.OnArmReachChanged -> updateState { copy(armReach = event.armReach) }
            is EditProfileEvent.OnGenderChanged -> updateState { copy(gender = event.gender) }
            is EditProfileEvent.OnProfileImagePicked -> updateState { copy(profileImageBytes = event.bytes) }
            is EditProfileEvent.OnUpdateSubmit -> submitUpdate()
        }
    }

    private fun loadCurrentProfile() = launch {
        fetchUserUseCase().onSuccess { user ->
            updateState {
                copy(
                    name = user.name,
                    age = user.age?.toString() ?: "",
                    height = user.height?.toString() ?: "",
                    armReach = user.armReach?.toString() ?: "",
                    gender = user.gender ?: "M",
                    profilePhotoUrl = user.profilePhotoUrl
                )
            }
        }
    }

    private fun submitUpdate() = launchWithLoading {
        val s = state.value
        updateProfileUseCase(
            name = s.name,
            age = s.age.toIntOrNull(),
            height = s.height.toIntOrNull(),
            armReach = s.armReach.toIntOrNull(),
            gender = s.gender,
            profilePhotoUrl = s.profilePhotoUrl // In real app, upload bytes first then use URL
        ).onSuccess {
            updateState { copy(updateSuccess = true) }
        }
    }

    init {
        loadCurrentProfile()
    }
}
