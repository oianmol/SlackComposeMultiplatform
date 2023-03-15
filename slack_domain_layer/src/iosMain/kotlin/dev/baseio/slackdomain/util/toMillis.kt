package dev.baseio.slackdomain.util

actual fun TimeUnit.toMillis(duration: Long): Long {
    return toMilliFactor.times(duration)
}