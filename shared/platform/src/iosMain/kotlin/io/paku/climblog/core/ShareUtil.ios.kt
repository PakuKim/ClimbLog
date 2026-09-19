package io.paku.climblog.core

import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

actual fun shareLink(url: String) {
    val items = listOf(url)
    val controller = UIActivityViewController(items, null)
    
    val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
    rootViewController?.presentViewController(controller, animated = true, completion = null)
}
