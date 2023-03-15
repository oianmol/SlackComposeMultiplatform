package dev.baseio.slackclone.common.extensions

import dev.baseio.slackdomain.util.TimeUnit
import dev.baseio.slackdomain.util.toMillis

enum class TimeGranularity {
    SECONDS {
        override fun toMillis(): Long {
            return TimeUnit.SECONDS.toMillis(1)
        }
    },
    MINUTES {
        override fun toMillis(): Long {
            return TimeUnit.MINUTES.toMillis(1)
        }
    },
    HOURS {
        override fun toMillis(): Long {
            return TimeUnit.HOURS.toMillis(1)
        }
    },
    DAYS {
        override fun toMillis(): Long {
            return TimeUnit.DAYS.toMillis(1)
        }
    },
    WEEKS {
        override fun toMillis(): Long {
            return TimeUnit.DAYS.toMillis(7)
        }
    },
    MONTHS {
        override fun toMillis(): Long {
            return TimeUnit.DAYS.toMillis(30)
        }
    },
    YEARS {
        override fun toMillis(): Long {
            return TimeUnit.DAYS.toMillis(365)
        }
    },
    DECADES {
        override fun toMillis(): Long {
            return TimeUnit.DAYS.toMillis(365 * 10)
        }
    };

    abstract fun toMillis(): Long
}

fun calculateTimeAgo(
    currentTime: Long,
    pastTime: Long
): String {
    return timeAgo(currentTime, pastTime)
}

fun timeAgo(currentDate: Long, pastDate: Long): String {
    val milliSecPerMinute = (60 * 1000).toLong() // Milliseconds Per Minute
    val milliSecPerHour = milliSecPerMinute * 60 // Milliseconds Per Hour
    val milliSecPerDay = milliSecPerHour * 24 // Milliseconds Per Day
    val milliSecPerMonth = milliSecPerDay * 30 // Milliseconds Per Month
    val milliSecPerYear = milliSecPerDay * 365 // Milliseconds Per Year
    // Difference in Milliseconds between two dates
    val msExpired: Long = currentDate - pastDate

    // Second or Seconds ago calculation
    return if (msExpired < milliSecPerMinute) {
        if (kotlin.math.round((msExpired / 1000).toFloat()) == 1f) {
            kotlin.math.round((msExpired / 1000).toFloat()).toString() + " second ago"
        } else {
            (kotlin.math.round((msExpired / 1000).toFloat()).toString() + " seconds ago").toString()
        }
    } else if (msExpired < milliSecPerHour) {
        if (kotlin.math.round((msExpired / milliSecPerMinute).toFloat()) == 1f) {
            kotlin.math.round((msExpired / milliSecPerMinute).toFloat()).toString() + " minute ago"
        } else {
            kotlin.math.round((msExpired / milliSecPerMinute).toFloat()).toString() + " minutes ago"
        }
    } else if (msExpired < milliSecPerDay) {
        if (kotlin.math.round((msExpired / milliSecPerHour).toFloat()) == 1f) {
            kotlin.math.round((msExpired / milliSecPerHour).toFloat()).toString() + " hour ago"
        } else {
            kotlin.math.round((msExpired / milliSecPerHour).toFloat()).toString() + " hours ago"
        }
    } else if (msExpired < milliSecPerMonth) {
        if (kotlin.math.round((msExpired / milliSecPerDay).toFloat()) == 1f) {
            kotlin.math.round((msExpired / milliSecPerDay).toFloat()).toString() + " day ago"
        } else {
            kotlin.math.round((msExpired / milliSecPerDay).toFloat()).toString() + " days ago"
        }
    } else if (msExpired < milliSecPerYear) {
        if (kotlin.math.round((msExpired / milliSecPerMonth).toFloat()) == 1f) {
            kotlin.math.round((msExpired / milliSecPerMonth).toFloat()).toString() + "  month ago"
        } else {
            kotlin.math.round((msExpired / milliSecPerMonth).toFloat()).toString() + "  months ago"
        }
    } else {
        if (kotlin.math.round((msExpired / milliSecPerYear).toFloat()) == 1f) {
            kotlin.math.round((msExpired / milliSecPerYear).toFloat()).toString() + " year ago"
        } else {
            kotlin.math.round((msExpired / milliSecPerYear).toFloat()).toString() + " years ago"
        }
    }
}
