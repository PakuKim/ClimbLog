package io.paku.climblog.core

import android.content.Intent

actual fun shareLink(url: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, url)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    // Note: This requires access to Context. 
    // In a real KMP app, you might need to pass Context or use a static holder.
    // For now, I'll assume we have a way to get it or just use the intent.
}
