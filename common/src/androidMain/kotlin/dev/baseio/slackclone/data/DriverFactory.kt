package dev.baseio.slackclone.data

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import dev.baseio.database.SlackDB

actual class DriverFactory(private val context: Context) {
  actual fun createDriver(): SqlDriver {
    return AndroidSqliteDriver(SlackDB.Schema, context, "SlackDB.db")
  }

  actual suspend fun createDriverBlocking(): SqlDriver {
    return AndroidSqliteDriver(SlackDB.Schema, context, "SlackDB.db")
  }
}