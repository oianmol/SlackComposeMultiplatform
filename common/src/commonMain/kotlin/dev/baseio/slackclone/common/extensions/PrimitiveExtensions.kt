package dev.baseio.slackclone.common.extensions

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun Long.calendar(): Instant = Instant.fromEpochMilliseconds(this)

fun Instant.formattedMonthDate(): String {
    val localDateTime = this.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${localDateTime.date.dayOfMonth} ${localDateTime.month.name}"
}

fun Instant.formattedTime(): String {
    val localDateTime = this.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${localDateTime.hour}:${localDateTime.minute}"
}
