package io.paku.climblog.core

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
actual fun rememberImagePicker(onImagePicked: (ByteArray?) -> Unit): ImagePicker {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        scope.launch {
            val bytes = uri?.let { readUriBytes(context, it) }
            onImagePicked(bytes)
        }
    }

    return remember {
        object : ImagePicker {
            override fun pickImage() {
                launcher.launch("image/*")
            }
        }
    }
}

private suspend fun readUriBytes(context: Context, uri: Uri): ByteArray? = withContext(Dispatchers.IO) {
    context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
}

/*
*
* import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import com.facebook.spectrum.EncodedImageSink
import com.facebook.spectrum.EncodedImageSource
import com.facebook.spectrum.Spectrum
import com.facebook.spectrum.options.TranscodeOptions
import com.facebook.spectrum.requirements.EncodeRequirement
import com.facebook.spectrum.requirements.EncodedImageFormat
import com.facebook.spectrum.requirements.ImageSize
import com.facebook.spectrum.requirements.ResizeRequirement
import com.facebook.spectrum.requirements.RotateRequirement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

data class EncodeFileResult(
    val file: File,
    val mimeType: String,
    val width: Int,
    val height: Int
)

class ImageEncoderImpl(
    private val context: Context,
    private val spectrum: Spectrum
) {

    suspend fun encodeImageFromUri(uriString: String): EncodeFileResult = withContext(Dispatchers.IO) {
        val uri = Uri.parse(uriString)

        // 1. 메모리에 피셀을 올리지 않고 원본 이미지 크기 측정 (inJustDecodeBounds = true)
        val (originalWidth, originalHeight) = getImageDimensions(uri)
        val (targetWidth, targetHeight) = getResizedRatio(originalWidth, originalHeight, 1_440)

        // 2. Transcode 결과를 담을 임시 파일(File) 생성
        val outputFile = File.createTempFile("encoded_img_", ".jpg", context.cacheDir)

        context.contentResolver.openInputStream(uri)?.buffered()?.use { inputStream ->
            FileOutputStream(outputFile).use { fileOutputStream ->
                val source = EncodedImageSource.from(inputStream)
                val sink = EncodedImageSink.from(fileOutputStream) // 메모리 대신 파일로 직접 Sink

                val encodeRequirement = EncodeRequirement(
                    EncodedImageFormat.JPEG,
                    80, // Quality를 100에서 80~85 수준으로 조정하면 용량이 대폭 줄어듭니다.
                    EncodeRequirement.Mode.LOSSY
                )

                val resizedRequirement = ResizeRequirement(
                    ResizeRequirement.Mode.EXACT_OR_LARGER,
                    ImageSize(targetWidth, targetHeight)
                )

                val transcodeOptions = TranscodeOptions.Builder(encodeRequirement)
                    .rotate(RotateRequirement(true))
                    .resize(resizedRequirement)
                    .build()

                val result = spectrum.transcode(source, sink, transcodeOptions, "SPECTRUM_CONTEXT")

                if (!result.isSuccessful) {
                    outputFile.delete()
                    throw IllegalStateException("Spectrum transcode failed")
                }
            }
        } ?: throw java.io.FileNotFoundException("Cannot open Uri: $uriString")

        EncodeFileResult(
            file = outputFile,
            mimeType = "image/jpeg",
            width = targetWidth,
            height = targetHeight
        )
    }

    // 메모리 할당 없는 이미지 Size 측정
    private fun getImageDimensions(uri: Uri): Pair<Int, Int> {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true // 픽셀 데이터를 로드하지 않음
        }
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream, null, options)
        }
        return Pair(options.outWidth, options.outHeight)
    }

    private fun getResizedRatio(width: Int, height: Int, maxDimension: Int): Pair<Int, Int> {
        if (width <= maxDimension && height <= maxDimension) return Pair(width, height)
        val ratio = width.toFloat() / height.toFloat()
        return if (width > height) {
            Pair(maxDimension, (maxDimension / ratio).toInt())
        } else {
            Pair((maxDimension * ratio).toInt(), maxDimension)
        }
    }
}
*/