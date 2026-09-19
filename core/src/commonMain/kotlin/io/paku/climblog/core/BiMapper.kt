package io.paku.climblog.core

interface BiMapper<LEFT, RIGHT> {
    fun mapToRight (from: LEFT): RIGHT
    fun mapToLeft (from: RIGHT): LEFT
}