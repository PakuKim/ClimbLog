package io.paku.kmp_template

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform