package io.paku.climblog.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.autoreleasepool
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUUID
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.temporaryDirectory
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.UIKit.UIWindowScene
import platform.UniformTypeIdentifiers.UTTypeImage
import platform.UniformTypeIdentifiers.UTTypeMovie
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@Composable
actual fun rememberGalleryManager(
    onResult: (Media?) -> Unit
): GalleryManager {
    val delegate = remember {
        object : NSObject(), PHPickerViewControllerDelegateProtocol {
            override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
                picker.dismissViewControllerAnimated(true, null)

                val result = didFinishPicking.firstOrNull() as? PHPickerResult
                if (result == null) {
                    onResult(null)
                    return
                }

                val itemProvider = result.itemProvider

                // 1. 비디오 파일 처리
                if (itemProvider.hasItemConformingToTypeIdentifier(UTTypeMovie.identifier)) {
                    itemProvider.loadFileRepresentationForTypeIdentifier(UTTypeMovie.identifier) { url, error ->
                        if (url != null && error == null) {
                            val media = createMediaFromUrl(url, isVideo = true)
                            dispatch_async(dispatch_get_main_queue()) { onResult(media) }
                        } else {
                            dispatch_async(dispatch_get_main_queue()) { onResult(null) }
                        }
                    }
                }
                // 2. 이미지 파일 처리
                else if (itemProvider.hasItemConformingToTypeIdentifier(UTTypeImage.identifier)) {
                    itemProvider.loadFileRepresentationForTypeIdentifier(UTTypeImage.identifier) { url, error ->
                        if (url != null && error == null) {
                            val media = createMediaFromUrl(url, isVideo = false)
                            dispatch_async(dispatch_get_main_queue()) { onResult(media) }
                        } else {
                            dispatch_async(dispatch_get_main_queue()) { onResult(null) }
                        }
                    }
                } else {
                    onResult(null)
                }
            }
        }
    }

    return remember {
        GalleryManager {
            val configuration = PHPickerConfiguration().apply {
                // 이미지와 비디오를 모두 허용하도록 필터 설정
                selectionLimit = 1
                filter = PHPickerFilter.anyFilterMatchingSubfilters(
                    listOf(
                        PHPickerFilter.imagesFilter,
                        PHPickerFilter.videosFilter
                    )
                )
            }

            val picker = PHPickerViewController(configuration = configuration).apply {
                this.delegate = delegate
            }

            // 최신 iOS 대응 RootViewController 획득
            val rootViewController = (UIApplication.sharedApplication.connectedScenes
                .filterIsInstance<UIWindowScene>()
                .firstOrNull { it.activationState == platform.UIKit.UISceneActivationStateForegroundActive }
                ?.windows
                ?.filterIsInstance<platform.UIKit.UIWindow>()
                ?.firstOrNull { it.isKeyWindow() }
                ?: UIApplication.sharedApplication.keyWindow)?.rootViewController

            rootViewController?.presentViewController(picker, true, null)
        }
    }

//    val imagePicker = UIImagePickerController()
//    val galleryDelegate = remember {
//        object : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {
//            override fun imagePickerController(
//                picker: UIImagePickerController, didFinishPickingMediaWithInfo: Map<Any?, *>
//            ) {
//                val image = didFinishPickingMediaWithInfo.getValue(
//                    UIImagePickerControllerEditedImage
//                ) as? UIImage ?: didFinishPickingMediaWithInfo.getValue(
//                    UIImagePickerControllerOriginalImage
//                ) as? UIImage
//                onResult.invoke(Media(image))
//                picker.dismissViewControllerAnimated(true, null)
//            }
//        }
//    }
//
//    return remember {
//        GalleryManager {
//            imagePicker.setSourceType(UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary)
//            imagePicker.setAllowsEditing(true)
//            imagePicker.setDelegate(galleryDelegate)
//            UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(
//                imagePicker, true, null
//            )
//        }
//    }
}

actual class GalleryManager actual constructor(private val onLaunch: () -> Unit) {
    actual fun launch() {
        onLaunch()
    }
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private fun createMediaFromUrl(sourceUrl: NSURL, isVideo: Boolean): Media? {
    val fileManager = NSFileManager.defaultManager
    val tempDir = fileManager.temporaryDirectory

    val originalFileName = sourceUrl.lastPathComponent ?: "media_${NSUUID().UUIDString}"
    val destinationUrl = tempDir.URLByAppendingPathComponent(originalFileName)!!

    // 임시 폴더 내 기존 동일 파일 삭제 후 복사 (접근 권한 유지)
    if (fileManager.fileExistsAtPath(destinationUrl.path!!)) {
        fileManager.removeItemAtURL(destinationUrl, null)
    }

    val copySuccess = fileManager.copyItemAtURL(sourceUrl, destinationUrl, null)
    if (!copySuccess) return null

    // NSURL -> NSData 변환 및 PlatformMedia 생성
    // 메모리관리를 위해 autoreleasePool 사용
    val platformMedia = autoreleasepool {
        val nsData = NSData.dataWithContentsOfURL(destinationUrl) ?: return@autoreleasepool null
        PlatformMedia(nsData)
    } ?: return null

    // 파일 확장자 기반 MIME 타입 추론
    val extension = destinationUrl.pathExtension?.lowercase() ?: ""
    val mimeType = getMimeTypeFromExtension(extension, isVideo)

    return if (isVideo) {
        Media.Video(
            mimeType = mimeType,
            fileName = originalFileName,
            source = platformMedia
        )
    } else {
        Media.Image(
            mimeType = mimeType,
            fileName = originalFileName,
            source = platformMedia
        )
    }
}


private fun getMimeTypeFromExtension(extension: String, isVideo: Boolean): String {
    return when (extension) {
        // 비디오
        "mp4" -> "video/mp4"
        "mov" -> "video/quicktime"
        "m4v" -> "video/x-m4v"
        // 이미지
        "jpg", "jpeg" -> "image/jpeg"
        "png" -> "image/png"
        "heic" -> "image/heic"
        "webp" -> "image/webp"
        else -> if (isVideo) "video/*" else "image/*"
    }
}
