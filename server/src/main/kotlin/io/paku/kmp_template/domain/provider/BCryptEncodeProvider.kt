package io.paku.kmp_template.domain.provider

interface BCryptEncodeProvider {
    fun hash(password: String): String
    fun verify(password: String, hash: String): Boolean
}