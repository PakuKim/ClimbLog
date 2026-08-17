package io.paku.climblog.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.UIKit.UIApplication
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerMediaURL
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.darwin.NSObject
import platform.posix.memcpy

@Composable
actual fun rememberVideoPicker(onVideoPicked: (VideoFile?) -> Unit): VideoPicker {
    return remember {
        IOSVideoPicker(onVideoPicked)
    }
}

class IOSVideoPicker(
    private val onVideoPicked: (VideoFile?) -> Unit
) : VideoPicker {

    private val delegate = object : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {
        override fun imagePickerController(picker: UIImagePickerController, didFinishPickingMediaWithInfo: Map<Any?, *>) {
            val url = didFinishPickingMediaWithInfo[UIImagePickerControllerMediaURL] as? NSURL
            val data = url?.let { NSData.dataWithContentsOfURL(it) }
            val bytes = data?.let { nsDataToByteArray(it) }
            
            if (bytes != null && url != null) {
                onVideoPicked(VideoFile(bytes, url.lastPathComponent ?: "video.mp4", "video/mp4", url.absoluteString))
            } else {
                onVideoPicked(null)
            }
            picker.dismissViewControllerAnimated(true, null)
        }

        override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
            picker.dismissViewControllerAnimated(true, null)
            onVideoPicked(null)
        }
    }

    override fun pickVideo() {
        val picker = UIImagePickerController()
        picker.sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
        picker.mediaTypes = listOf("public.movie")
        picker.delegate = delegate

        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        rootViewController?.presentViewController(picker, animated = true, completion = null)
    }
}

@OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)
private fun nsDataToByteArray(data: NSData): ByteArray {
    val bytes = data.bytes
    val length = data.length
    return ByteArray(length.toInt()).apply {
        if (length > 0u) {
            usePinned { pinned ->
                memcpy(pinned.addressOf(0), bytes, length)
            }
        }
    }
}
