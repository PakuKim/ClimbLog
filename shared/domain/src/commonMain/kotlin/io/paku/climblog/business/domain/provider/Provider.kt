package io.paku.climblog.business.domain.provider

fun interface Provider<T> {
    fun get(): T
}
