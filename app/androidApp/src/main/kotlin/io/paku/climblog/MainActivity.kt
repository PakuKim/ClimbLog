package io.paku.climblog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.paku.climblog.presentation.App
import io.paku.climblog.util.ActivityUtil

internal class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        ActivityUtil.setActivity(this)
        setContent {
            App()
        }
    }
}