package io.paku.climblog.remote.mapper.user

import io.paku.climblog.contract.user.UserResponse
import io.paku.climblog.core.BiMapper
import io.paku.climblog.data.model.user.UserData

internal object UserResponseMapper : BiMapper<UserResponse, UserData> {
    override fun mapToRight(from: UserResponse): UserData {
        return UserData(
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

    override fun mapToLeft(from: UserData): UserResponse {
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
