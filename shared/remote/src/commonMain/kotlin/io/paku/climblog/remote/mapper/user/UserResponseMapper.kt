package io.paku.climblog.remote.mapper.user

import io.paku.climblog.contract.user.UserResponse
import io.paku.climblog.core.BiMapper

internal object UserResponseMapper : BiMapper<UserResponse, io.paku.climblog.data.model.user.UserData> {
    override fun mapToRight(from: UserResponse): io.paku.climblog.data.model.user.UserData {
        return _root_ide_package_.io.paku.climblog.data.model.user.UserData(
            id = from.id,
            name = from.name,
            handle = from.handle,
            age = from.age,
            height = from.height,
            armReach = from.armReach,
            gender = from.gender,
            profilePhotoUrl = from.profilePhotoUrl
        )
    }

    override fun mapToLeft(from: io.paku.climblog.data.model.user.UserData): UserResponse {
        return UserResponse(
            id = from.id,
            name = from.name,
            handle = from.handle,
            age = from.age,
            height = from.height,
            armReach = from.armReach,
            gender = from.gender,
            profilePhotoUrl = from.profilePhotoUrl
        )
    }
}
