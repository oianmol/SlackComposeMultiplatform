package dev.baseio.slackdomain.util

import kotlin.time.Duration

expect enum class TimeUnit {
    DAYS,
    HOURS,
    MILLISECONDS,
    MINUTES,
    SECONDS
}

expect fun TimeUnit.toMillis(duration: Long):Long