package dev.baseio.slackclone.data

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import dev.baseio.database.SlackDB

actual class DriverFactory(private val context: Context) {
  actual fun createDriver(schema: SqlDriver.Schema): SqlDriver {
    return AndroidSqliteDriver(schema, context, "SlackDB.db")
  }

  actual suspend fun createDriverBlocking(schema: SqlDriver.Schema): SqlDriver {
    return AndroidSqliteDriver(schema, context, "SlackDB.db")
  }
}