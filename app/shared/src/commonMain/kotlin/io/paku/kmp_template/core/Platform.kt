package io.paku.kmp_template.core

enum class Platform {
    ANDROID, IOS, WEB, DESKTOP, JVM;
}

expect fun getPlatform(): Platform