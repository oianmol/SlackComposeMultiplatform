package dev.baseio.slackclone.common.extensions

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun String?.isValid(): Boolean {
  return !this.isNullOrEmpty()
}

fun Long.calendar(): Instant = Clock.System.now()

fun Instant.formattedMonthDate(): String {
  // TODO make this method return formattedMonthDate
  return this@formattedMonthDate.toLocalDateTime(TimeZone.UTC).date.toString()
}

fun Instant.formattedTime(): String {
  // TODO make this method return formattedTime
  return this@formattedTime.toLocalDateTime(TimeZone.UTC).time.toString()
}
