package io.paku.climblog.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSFileManager
import platform.Foundation.NSUUID
import platform.Foundation.temporaryDirectory
import platform.Foundation.writeToURL
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePNGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerEditedImage
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.darwin.NSObject

@Composable
actual fun rememberCameraManager(onResult: (PlatformMedia?) -> Unit): CameraManager {
    val delegate = remember {
        object : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {
            override fun imagePickerController(
                picker: UIImagePickerController,
                didFinishPickingMediaWithInfo: Map<Any?, *>
            ) {
                val image = didFinishPickingMediaWithInfo[UIImagePickerControllerEditedImage] as? UIImage
                    ?: didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage

                if (image != null) {
                    val data = UIImageJPEGRepresentation(image, 0.8) ?: UIImagePNGRepresentation(image)
                    if (data != null) {
                        val fileManager = NSFileManager.defaultManager
                        val tempDir = fileManager.temporaryDirectory
                        val fileName = "camera_${NSUUID().UUIDString}.jpg"
                        val fileUrl = tempDir.URLByAppendingPathComponent(fileName)!!
                        data.writeToURL(fileUrl, true)
                        onResult(PlatformMedia(fileUrl))
                    } else {
                        onResult(null)
                    }
                } else {
                    onResult(null)
                }
                picker.dismissViewControllerAnimated(true, null)
            }

            override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
                picker.dismissViewControllerAnimated(true, null)
                onResult(null)
            }
        }
    }

    return remember {
        CameraManager {
            if (UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera)) {
                val imagePicker = UIImagePickerController().apply {
                    this.sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
                    this.allowsEditing = true
                    this.delegate = delegate
                }
                
                val rootViewController = (UIApplication.sharedApplication.connectedScenes
                    .filterIsInstance<platform.UIKit.UIWindowScene>()
                    .firstOrNull { it.activationState == platform.UIKit.UISceneActivationStateForegroundActive }
                    ?.windows
                    ?.filterIsInstance<platform.UIKit.UIWindow>()
                    ?.firstOrNull { it.isKeyWindow() }
                    ?: UIApplication.sharedApplication.keyWindow)?.rootViewController

                rootViewController?.presentViewController(imagePicker, true, null)
            } else {
                onResult(null)
            }
        }
    }
}

actual class CameraManager actual constructor(
    private val onLaunch: () -> Unit
) {
    actual fun launch() {
        onLaunch()
    }
}
