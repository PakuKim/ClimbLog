package io.paku.climblog

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.paku.climblog.presentation.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "ClimbLog",
    ) {
        App()
    }
}