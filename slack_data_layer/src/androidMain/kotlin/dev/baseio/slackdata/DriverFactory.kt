package dev.baseio.slackdata

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

actual class DriverFactory(private val context: Context) {
  actual fun createDriver(schema: SqlDriver.Schema): SqlDriver {
    return AndroidSqliteDriver(schema, context, "SlackDB.db")
  }

  actual suspend fun createDriverBlocking(schema: SqlDriver.Schema): SqlDriver {
    return AndroidSqliteDriver(schema, context, "SlackDB.db")
  }

  actual fun createInMemorySqlDriver(schema: SqlDriver.Schema): SqlDriver {
    return AndroidSqliteDriver(schema, context, null)
  }
}