package dev.baseio.slackclone.common.extensions

import dev.baseio.slackdomain.util.TimeUnit
import dev.baseio.slackdomain.util.toMillis
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil

enum class TimeGranularity {
  SECONDS {
    override fun toMillis(): Long {
      return TimeUnit.SECONDS.toMillis(1);
    }
  },
  MINUTES {
    override fun toMillis(): Long {
      return TimeUnit.MINUTES.toMillis(1);
    }
  },
  HOURS {
    override fun toMillis(): Long {
      return TimeUnit.HOURS.toMillis(1);
    }
  },
  DAYS {
    override fun toMillis(): Long {
      return TimeUnit.DAYS.toMillis(1);
    }
  },
  WEEKS {
    override fun toMillis(): Long {
      return TimeUnit.DAYS.toMillis(7);
    }
  },
  MONTHS {
    override fun toMillis(): Long {
      return TimeUnit.DAYS.toMillis(30);
    }
  },
  YEARS {
    override fun toMillis(): Long {
      return TimeUnit.DAYS.toMillis(365);
    }
  },
  DECADES {
    override fun toMillis(): Long {
      return TimeUnit.DAYS.toMillis(365 * 10);
    }
  };

  abstract fun toMillis(): Long
}

fun calculateTimeAgoByTimeGranularity(
  currentTime: Long,
  pastTime: Long,
  granularity: TimeGranularity = TimeGranularity.MINUTES
): String {
  val timeDifferenceInMillis = Instant.fromEpochMilliseconds(pastTime)
    .periodUntil(Instant.fromEpochMilliseconds(currentTime), TimeZone.UTC).seconds.times(1000)
  return "${timeDifferenceInMillis / granularity.toMillis()} " +
      granularity.name.lowercase() + " ago";
}

fun calculateHumanFriendlyTimeAgo(
  currentTime: Long,
  pastTime: Long
): String {
  val timeDifferenceInMillis = Instant.fromEpochMilliseconds(pastTime)
    .periodUntil(Instant.fromEpochMilliseconds(currentTime), TimeZone.UTC).seconds.times(1000)
  return if (timeDifferenceInMillis / TimeGranularity.DECADES.toMillis() > 0) {
    "several decades ago"
  } else if (timeDifferenceInMillis / TimeGranularity.YEARS.toMillis() > 0) {
    "several years ago"
  } else if (timeDifferenceInMillis / TimeGranularity.MONTHS.toMillis() > 0) {
    "several months ago"
  } else if (timeDifferenceInMillis / TimeGranularity.WEEKS.toMillis() > 0) {
    "several weeks ago"
  } else if (timeDifferenceInMillis / TimeGranularity.DAYS.toMillis() > 0) {
    "several days ago"
  } else if (timeDifferenceInMillis / TimeGranularity.HOURS.toMillis() > 0) {
    "several hours ago"
  } else if (timeDifferenceInMillis / TimeGranularity.MINUTES.toMillis() > 0) {
    "several minutes ago"
  } else {
    "moments ago"
  }
}