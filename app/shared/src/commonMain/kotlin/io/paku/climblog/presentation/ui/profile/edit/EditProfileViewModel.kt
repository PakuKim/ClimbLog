package io.paku.climblog.presentation.ui.profile.edit

import io.paku.climblog.business.domain.interactors.user.FetchUserUseCase
import io.paku.climblog.business.domain.interactors.user.UpdateProfileUseCase
import io.paku.climblog.presentation.base.BaseViewModel
import io.paku.climblog.presentation.base.Event

internal class EditProfileViewModel(
    private val fetchUserUseCase: FetchUserUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase
) : BaseViewModel<EditProfileViewModelState, EditProfileViewModelEvent>() {

    override fun createInitialState(): EditProfileViewModelState = EditProfileViewModelState()

    override fun createTriggerEvent(event: Event) {
        if (event is EditProfileViewModelEvent) {
            onEvent(event)
        }
    }

    fun onEvent(event: EditProfileViewModelEvent) {
        when (event) {
            is EditProfileViewModelEvent.OnNameChanged -> updateState { copy(name = event.name) }
            is EditProfileViewModelEvent.OnAgeChanged -> updateState { copy(age = event.age) }
            is EditProfileViewModelEvent.OnHeightChanged -> updateState { copy(height = event.height) }
            is EditProfileViewModelEvent.OnArmReachChanged -> updateState { copy(armReach = event.armReach) }
            is EditProfileViewModelEvent.OnGenderChanged -> updateState { copy(gender = event.gender) }
            is EditProfileViewModelEvent.OnProfileImageChanged -> updateState { copy(profileImageBytes = event.bytes) }
            is EditProfileViewModelEvent.OnUpdateSubmit -> submitUpdate()
        }
    }

    private fun loadCurrentProfile() = launch {
        val user = fetchUserUseCase()
        updateState {
            copy(
                name = user.name,
                age = user.age.toString(),
                height = user.height.toString(),
                armReach = user.armReach.toString(),
                gender = user.gender,
                profilePhotoUrl = user.profilePhotoUrl
            )
        }
    }

    private fun submitUpdate() = launchWithLoading {
        val state = state.value
        updateProfileUseCase(
            name = state.name,
            age = state.age.toIntOrNull(),
            height = state.height.toIntOrNull(),
            armReach = state.armReach.toIntOrNull(),
            gender = state.gender,
            profilePhotoUrl = state.profilePhotoUrl
        )

        updateState { copy(updateSuccess = true) }
    }

    init {
        loadCurrentProfile()
    }
}
