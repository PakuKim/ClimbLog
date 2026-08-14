package io.paku.climblog.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberImagePicker(onImagePicked: (ByteArray?) -> Unit): ImagePicker {
    return remember {
        object : ImagePicker {
            override fun pickImage() {
                onImagePicked(null)
            }
        }
    }
}
