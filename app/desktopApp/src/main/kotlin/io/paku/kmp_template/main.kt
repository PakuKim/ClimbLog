package io.paku.kmp_template

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.paku.kmp_template.presentation.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "KMP-Template",
    ) {
        App()
    }
}