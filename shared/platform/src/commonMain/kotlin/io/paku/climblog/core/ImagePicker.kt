package io.paku.climblog.core

import androidx.compose.runtime.Composable

@Composable
expect fun rememberImagePicker(onImagePicked: (ByteArray?) -> Unit): ImagePicker

interface ImagePicker {
    fun pickImage()
}
