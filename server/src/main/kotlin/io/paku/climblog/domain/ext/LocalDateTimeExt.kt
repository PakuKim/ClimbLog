package io.paku.climblog.domain.ext

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

fun LocalDateTime.Companion.now(): LocalDateTime {
    return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
}

fun LocalDateTime.toEpochMilliseconds(timeZone: TimeZone = TimeZone.currentSystemDefault()): Long {
    return this.toInstant(timeZone).toEpochMilliseconds()
}
