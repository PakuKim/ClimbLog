package io.paku.climblog.ext

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun Context.createTempUri(): Uri? {
    val now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
    val content = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "img_$now.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
    }

    return this.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, content)
}