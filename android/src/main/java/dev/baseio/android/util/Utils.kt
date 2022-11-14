package dev.baseio.android.util

import android.content.Context
import android.widget.Toast

fun Context.showToast(msg: String, isLongToast: Boolean = false) {
    Toast.makeText(this, msg, if (isLongToast) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
}
