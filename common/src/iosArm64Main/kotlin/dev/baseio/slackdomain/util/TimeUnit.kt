package dev.baseio.slackdomain.util

actual enum class TimeUnit(val toMilliFactor: Long) {
    DAYS(24 * 60 * 60 * 1000),
    HOURS(60 * 60 * 1000),
    MILLISECONDS(1),
    MINUTES(60 * 1000),
    SECONDS(1000)
}