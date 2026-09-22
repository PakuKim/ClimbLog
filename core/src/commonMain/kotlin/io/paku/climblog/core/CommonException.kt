package io.paku.climblog.core

import kotlin.jvm.JvmOverloads

class CommonException @JvmOverloads constructor(
    message: String? = null,
    cause: Throwable? = null,
    val code: Int? = null,
) : Exception(message, cause) {
    override val message: String = message ?: "error=${cause?.message}:code=${code}"
}
