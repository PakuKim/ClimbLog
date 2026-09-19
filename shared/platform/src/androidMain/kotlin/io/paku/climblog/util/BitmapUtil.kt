package io.paku.climblog.util

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.InputStream

object BitmapUtil {
    fun getBitmapFromUri(uri: Uri, contentResolver: ContentResolver): Bitmap? {
        var inputStream: InputStream?
        return try {
            inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getResizedRatio(
        inputStream: InputStream,
        resizedWidth: Int,
    ): Pair<Int, Int> {
        return BitmapFactory.Options().run {
            inJustDecodeBounds = true

            BitmapFactory.decodeStream(inputStream, null, this)

            val scale = resizedWidth.toDouble() / outWidth
            val resizeHeightTo = (outHeight * scale).toInt()
            resizedWidth to resizeHeightTo
        }
    }
}