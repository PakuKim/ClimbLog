package io.paku.climblog.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerMediaURL
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.darwin.NSObject

@Composable
actual fun rememberVideoPicker(onVideoPicked: (Media.Video?) -> Unit): VideoPicker {
    return remember {
        IOSVideoPicker(onVideoPicked)
    }
}

class IOSVideoPicker(
    private val onVideoPicked: (Media.Video?) -> Unit
) : VideoPicker {

    private val delegate = object : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {
        override fun imagePickerController(picker: UIImagePickerController, didFinishPickingMediaWithInfo: Map<Any?, *>) {
            val url = didFinishPickingMediaWithInfo[UIImagePickerControllerMediaURL] as? NSURL
            
            if (url != null) {
                onVideoPicked(
                    Media.Video(
                        mimeType = "video/mp4", // Simplified
                        fileName = url.lastPathComponent ?: "video.mp4",
                        source = PlatformMedia(url)
                    )
                )
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
