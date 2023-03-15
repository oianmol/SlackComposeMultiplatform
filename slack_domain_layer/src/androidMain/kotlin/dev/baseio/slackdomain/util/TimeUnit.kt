package dev.baseio.slackdomain.util

actual enum class TimeUnit(val javaTimeUnit: java.util.concurrent.TimeUnit) {
  DAYS(java.util.concurrent.TimeUnit.DAYS),
  HOURS(java.util.concurrent.TimeUnit.HOURS),
  MILLISECONDS(java.util.concurrent.TimeUnit.MILLISECONDS),
  MINUTES(java.util.concurrent.TimeUnit.MINUTES),
  SECONDS(java.util.concurrent.TimeUnit.SECONDS)
}

actual fun TimeUnit.toMillis(duration: Long): Long {
  return javaTimeUnit.toMillis(duration)
}