package dev.baseio.slackclone.common.extensions

import java.util.*
import java.util.concurrent.TimeUnit

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
      return TimeUnit.DAYS.toMillis((365 * 10).toLong())
    }
  };

  abstract fun toMillis(): Long
}

fun calculateTimeAgoByTimeGranularity(currentTime:Long,pastTime: Date, granularity: TimeGranularity): String? {
  val timeDifferenceInMillis: Long = currentTime - pastTime.getTime()
  return (timeDifferenceInMillis / granularity.toMillis()).toString() + " " +
      granularity.name.lowercase(Locale.getDefault()) + " ago"
}