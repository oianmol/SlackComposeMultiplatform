package dev.baseio.slackclone.common.extensions

import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*

fun String?.isValid(): Boolean {
  return !this.isNullOrEmpty()
}

fun Int.divide(divideBy: Double, decimals: Int = 2): String {
  this.div(divideBy).toBigDecimal().setScale(decimals, RoundingMode.UP)
  return (this / divideBy).toBigDecimal().setScale(decimals, RoundingMode.UP).toPlainString()
}


fun Long.calendar(): Calendar {
  return Calendar.getInstance().apply {
    this.timeInMillis = this@calendar
  }
}

fun Calendar.formattedMonthDate(): String {
  return SimpleDateFormat("MMM dd").format(this.time)
}

fun Calendar.formattedTime(): String {
  return SimpleDateFormat("hh:mm a").format(this.time)
}
