package io.paku.climblog.provider.encode

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import com.facebook.spectrum.EncodedImageSink
import com.facebook.spectrum.EncodedImageSource
import com.facebook.spectrum.Spectrum
import com.facebook.spectrum.SpectrumSoLoader
import com.facebook.spectrum.image.EncodedImageFormat
import com.facebook.spectrum.image.ImageSize
import com.facebook.spectrum.logging.SpectrumLogcatLogger
import com.facebook.spectrum.options.TranscodeOptions
import com.facebook.spectrum.plugins.SpectrumPluginJpeg
import com.facebook.spectrum.plugins.SpectrumPluginPng
import com.facebook.spectrum.plugins.SpectrumPluginWebp
import com.facebook.spectrum.requirements.EncodeRequirement
import com.facebook.spectrum.requirements.ResizeRequirement
import com.facebook.spectrum.requirements.RotateRequirement
import io.paku.climblog.business.domain.model.EncodeResult
import io.paku.climblog.business.domain.provider.encode.EncodeFileProvider
import io.paku.climblog.util.BitmapUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream

internal class EncodeFileProviderImpl(
    private val applicationContext: Context
) : EncodeFileProvider {
    companion object {
        private const val SPECTRUM_CONTEXT = "io.paku.climblog.transcode"
    }

    private val spectrum: Spectrum

    init {
        SpectrumSoLoader.init(applicationContext)
        spectrum = Spectrum.make(
            SpectrumLogcatLogger(Log.DEBUG),
            arrayOf(
                SpectrumPluginJpeg.get(),
                SpectrumPluginPng.get(),
                SpectrumPluginWebp.get()
            )
        )
    }

    override suspend fun encodeImageFromUri(uri: String) = withContext(Dispatchers.IO) {
        val uriFromPath = Uri.parse(uri)
        ByteArrayInputStream(uriFromPath.toInputStream()?.readBytes() ?: ByteArray(0)).use {  }
        uriFromPath.toInputStream()?.use { inputStream ->
            val (width, height) = BitmapUtil.getResizedRatio(inputStream, 1_440)

            val byteArray = uriFromPath.toInputStream()?.buffered()
                ?.use bufferedInputStream@{ bufferedInputStream ->
                    val source = EncodedImageSource.from(bufferedInputStream)
                    val byteArrayStream = ByteArrayOutputStream()
                    val sink = EncodedImageSink.from(byteArrayStream)

                    val encodeRequirement = EncodeRequirement(
                        EncodedImageFormat.JPEG,
                        80,
                        EncodeRequirement.Mode.LOSSY
                    )

                    val resizedRequirement = ResizeRequirement(
                        ResizeRequirement.Mode.EXACT_OR_LARGER,
                        ImageSize(width, height)
                    )

                    val rotateRequirement = RotateRequirement(true)

                    val transcodeOptions = TranscodeOptions.Builder(encodeRequirement)
                        .rotate(rotateRequirement)
                        .resize(resizedRequirement)
                        .build()

                    val result =
                        spectrum.transcode(source, sink, transcodeOptions, SPECTRUM_CONTEXT)

                    return@bufferedInputStream if (result.isSuccessful) {
                        byteArrayStream.toByteArray()
                    } else {
                        null
                    }
                } ?: throw FileNotFoundException()

            EncodeResult(
                byteArray = byteArray,
                mimeType = "image/jpeg",
                width = width,
                height = height
            )
        }

        throw FileNotFoundException()
    }

    override suspend fun encodeFileFromUri(uri: String): EncodeResult {
        val uriFromPath = Uri.parse(uri)
        val mimeType = if (uriFromPath.scheme.equals(ContentResolver.SCHEME_CONTENT)) {
            applicationContext.contentResolver.getType(uriFromPath) ?: ""
        } else if (uriFromPath.scheme.equals(ContentResolver.SCHEME_FILE)) {
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                MimeTypeMap.getFileExtensionFromUrl(uri)
            ) ?: ""
        } else {
            ""
        }

        val byteArray =
            applicationContext.contentResolver.openInputStream(uriFromPath)?.use { input ->
                ByteArrayOutputStream().use { output ->
                    input.copyTo(output)
                    output
                }.toByteArray()
            } ?: throw FileNotFoundException("Encoding file Failure")

        return EncodeResult(
            byteArray = byteArray,
            mimeType = mimeType,
            width = 0,
            height = 0
        )
    }

    override suspend fun getFileSizeFromUri(uri: String): Long {
        val fileDescriptor =
            applicationContext.contentResolver.openFileDescriptor(Uri.parse(uri), "r")
        return (fileDescriptor?.statSize ?: 0).also { fileDescriptor?.close() }
    }

    private fun Uri.toInputStream(): InputStream? {
        return applicationContext.contentResolver.openInputStream(this)
    }
}