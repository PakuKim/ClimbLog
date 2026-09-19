package io.paku.climblog.core

import androidx.compose.runtime.Composable
import io.paku.climblog.business.domain.model.permission.PermissionStatus
import io.paku.climblog.business.domain.model.permission.PermissionType

expect class PermissionsManager(callback: PermissionCallback) : PermissionHandler {
    @Composable
    override fun AskPermission(permission: PermissionType)
    @Composable
    override fun isPermissionGranted(permission: PermissionType): Boolean
    @Composable
    override fun LaunchSettings()
}

interface PermissionCallback {
    fun onPermissionStatus(permissionType: PermissionType, status: PermissionStatus)
}

@Composable
expect fun createPermissionsManager(callback: PermissionCallback): PermissionsManager
