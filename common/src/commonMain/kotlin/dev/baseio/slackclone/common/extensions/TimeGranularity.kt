package dev.baseio.slackclone.common.extensions

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil

fun calculateTimeAgoByTimeGranularity(currentTime: Long, pastTime: Long): String {
  val period = Instant.fromEpochMilliseconds(pastTime)
    .periodUntil(Instant.fromEpochMilliseconds(currentTime), TimeZone.UTC)
  return "${period.minutes} minutes ago"
}