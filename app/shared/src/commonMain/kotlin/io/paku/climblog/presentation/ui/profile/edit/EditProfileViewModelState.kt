package io.paku.climblog.presentation.ui.profile.edit

import io.paku.climblog.presentation.base.State

data class EditProfileViewModelState(
    val name: String = "",
    val age: String = "",
    val height: String = "",
    val armReach: String = "",
    val gender: String = "M",
    val profilePhotoUrl: String? = null,
    val profileImageBytes: ByteArray? = null,
    val updateSuccess: Boolean = false
): State {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as EditProfileViewModelState
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