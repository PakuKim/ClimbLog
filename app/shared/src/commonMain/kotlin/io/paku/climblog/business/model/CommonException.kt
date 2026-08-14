package io.paku.climblog.business.model

import kotlinx.io.IOException
import kotlin.jvm.JvmOverloads

class CommonException @JvmOverloads constructor(
    error: CommonError = CommonError.Unknown,
    message: String? = null,
    cause: Throwable? = null,
    val code: Int? = null,
) : IOException(null, cause) {
    override val message: String = message ?: "error=${cause?.message}:code=${code}"
}