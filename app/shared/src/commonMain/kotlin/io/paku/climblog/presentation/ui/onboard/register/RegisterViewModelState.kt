package io.paku.climblog.presentation.ui.onboard.register

import io.paku.climblog.business.domain.model.SocialAuthType
import io.paku.climblog.presentation.base.State

data class RegisterViewModelState(
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
    val socialAuthType: SocialAuthType = SocialAuthType.GOOGLE
): State {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as RegisterViewModelState
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
        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + handle.hashCode()
        result = 31 * result + age.hashCode()
        result = 31 * result + height.hashCode()
        result = 31 * result + armReach.hashCode()
        result = 31 * result + gender.hashCode()
        result = 31 * result + (profilePhotoUrl?.hashCode() ?: 0)
        result = 31 * result + (profileImageBytes?.contentHashCode() ?: 0)
        result = 31 * result + isHandleChecked.hashCode()
        result = 31 * result + isHandleAvailable.hashCode()
        return result
    }
}
