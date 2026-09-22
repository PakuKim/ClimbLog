package io.paku.climblog.platform

import androidx.compose.runtime.Composable

@Composable
expect fun rememberImagePicker(onImagePicked: (ByteArray?) -> Unit): ImagePicker

interface ImagePicker {
    fun pickImage()
}
