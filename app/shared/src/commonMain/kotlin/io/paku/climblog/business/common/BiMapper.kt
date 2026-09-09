package io.paku.climblog.business.common

interface BiMapper<LEFT, RIGHT> {
    fun mapToRight (from: LEFT): RIGHT
    fun mapToLeft (from: RIGHT): LEFT
}