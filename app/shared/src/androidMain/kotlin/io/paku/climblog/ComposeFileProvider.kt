package io.paku.climblog

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import io.paku.climblog.app.shared.R
import java.io.File
import java.util.Objects

class ComposeFileProvider : FileProvider(
    R.xml.path_provider
) {
    companion object {
        fun getImageUri(context: Context): Uri {
            val tempFile = File.createTempFile(
                "picture_${System.currentTimeMillis()}", ".jpg", context.cacheDir
            )

            val authority = context.applicationContext.packageName + ".provider"
            return getUriForFile(
                Objects.requireNonNull(context),
                authority,
                tempFile,
            )
        }
    }
}