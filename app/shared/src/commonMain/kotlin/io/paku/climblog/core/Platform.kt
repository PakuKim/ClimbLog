package io.paku.climblog.core

enum class Platform {
    ANDROID, IOS, WEB, DESKTOP;
}

expect fun getPlatform(): Platform