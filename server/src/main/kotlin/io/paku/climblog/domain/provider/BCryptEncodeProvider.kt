package io.paku.climblog.domain.provider

interface BCryptEncodeProvider {
    fun hash(password: String): String

    fun verify(password: String, hash: String): Boolean
}