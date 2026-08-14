package io.paku.climblog.presentation.ui.onboard.register

import io.paku.climblog.business.domain.interactors.CheckHandleUseCase
import io.paku.climblog.business.domain.interactors.RegisterUserUseCase
import io.paku.climblog.presentation.base.BaseViewModel
import io.paku.climblog.presentation.base.Event
import io.paku.climblog.presentation.base.State

data class RegisterState(
    val email: String = "",
    val name: String = "",
    val handle: String = "",
    val age: String = "",
    val height: String = "",
    val armReach: String = "",
    val gender: String = "M",
    val profilePhotoUrl: String? = null,
    val profileImageBytes: ByteArray? = null,
    val isHandleChecked: Boolean = false,
    val isHandleAvailable: Boolean = false,
    val isSocialUser: Boolean = false,
    val registrationSuccess: Boolean = false
) : State {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as RegisterState
        if (email != other.email) return false
        if (name != other.name) return false
        if (handle != other.handle) return false
        if (age != other.age) return false
        if (height != other.height) return false
        if (armReach != other.armReach) return false
        if (gender != other.gender) return false
        if (profilePhotoUrl != other.profilePhotoUrl) return false
        if (profileImageBytes != null) {
            if (other.profileImageBytes == null) return false
            if (!profileImageBytes.contentEquals(other.profileImageBytes)) return false
        } else if (other.profileImageBytes != null) return false
        if (isHandleChecked != other.isHandleChecked) return false
        if (isHandleAvailable != other.isHandleAvailable) return false
        if (isSocialUser != other.isSocialUser) return false
        if (registrationSuccess != other.registrationSuccess) return false
        return true
    }

    override fun hashCode(): Int {
        var result = email.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + handle.hashCode()
        result = 31 * result + age.hashCode()
        result = 31 * result + height.hashCode()
        result = 31 * result + armReach.hashCode()
        result = 31 * result + gender.hashCode()
        result = 31 * result + (profilePhotoUrl?.hashCode() ?: 0)
        result = 31 * result + (profileImageBytes?.contentHashCode() ?: 0)
        result = 31 * result + isHandleChecked.hashCode()
        result = 31 * result + isHandleAvailable.hashCode()
        result = 31 * result + isSocialUser.hashCode()
        result = 31 * result + registrationSuccess.hashCode()
        return result
    }
}

sealed class RegisterEvent : Event {
    data class OnEmailChanged(val email: String) : RegisterEvent()
    data class OnNameChanged(val name: String) : RegisterEvent()
    data class OnHandleChanged(val handle: String) : RegisterEvent()
    data class OnAgeChanged(val age: String) : RegisterEvent()
    data class OnHeightChanged(val height: String) : RegisterEvent()
    data class OnArmReachChanged(val armReach: String) : RegisterEvent()
    data class OnGenderChanged(val gender: String) : RegisterEvent()
    data class OnProfileImagePicked(val bytes: ByteArray?) : RegisterEvent()
    object OnCheckHandle : RegisterEvent()
    object OnRegisterSubmit : RegisterEvent()
}

class RegisterViewModel(
    private val checkHandleUseCase: CheckHandleUseCase,
    private val registerUserUseCase: RegisterUserUseCase
) : BaseViewModel<RegisterState, RegisterEvent>() {

    override fun createInitialState(): RegisterState = RegisterState()

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.OnEmailChanged -> updateState { copy(email = event.email) }
            is RegisterEvent.OnNameChanged -> updateState { copy(name = event.name) }
            is RegisterEvent.OnHandleChanged -> updateState { 
                copy(handle = event.handle, isHandleChecked = false) 
            }
            is RegisterEvent.OnAgeChanged -> updateState { copy(age = event.age) }
            is RegisterEvent.OnHeightChanged -> updateState { copy(height = event.height) }
            is RegisterEvent.OnArmReachChanged -> updateState { copy(armReach = event.armReach) }
            is RegisterEvent.OnGenderChanged -> updateState { copy(gender = event.gender) }
            is RegisterEvent.OnProfileImagePicked -> updateState { copy(profileImageBytes = event.bytes) }
            is RegisterEvent.OnCheckHandle -> checkHandle()
            is RegisterEvent.OnRegisterSubmit -> submitRegistration()
        }
    }

    override fun createTriggerEvent(event: Event) {
        if (event is RegisterEvent) {
            onEvent(event)
        }
    }

    private fun checkHandle() = launchWithLoading {
        val handle = state.value.handle
        if (handle.isBlank()) return@launchWithLoading
        
        checkHandleUseCase(handle).onSuccess { exists ->
            updateState { 
                copy(isHandleChecked = true, isHandleAvailable = !exists) 
            }
        }.onFailure {
            updateState { copy(isHandleChecked = true, isHandleAvailable = false) }
        }
    }

    private fun submitRegistration() = launchWithLoading {
        val s = state.value
        if (!s.isHandleAvailable) return@launchWithLoading
        
        // Basic validation
        val ageInt = s.age.toIntOrNull()
        val heightInt = s.height.toIntOrNull()
        val armReachInt = s.armReach.toIntOrNull()
        
        registerUserUseCase(
            handle = s.handle,
            name = s.name,
            age = ageInt,
            height = heightInt,
            armReach = armReachInt,
            gender = s.gender,
            profilePhotoUrl = s.profilePhotoUrl
        ).onSuccess {
            updateState { copy(registrationSuccess = true) }
        }
    }
    
    fun initSocialUser(email: String, name: String) {
        updateState { copy(email = email, name = name, isSocialUser = true) }
    }
}
