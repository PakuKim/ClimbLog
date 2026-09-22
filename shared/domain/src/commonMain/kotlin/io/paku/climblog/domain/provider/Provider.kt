package io.paku.climblog.domain.provider

fun interface Provider<T> {
    fun get(): T
}
