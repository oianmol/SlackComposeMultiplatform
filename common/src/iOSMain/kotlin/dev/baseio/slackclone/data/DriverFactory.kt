package dev.baseio.slackclone.data

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import dev.baseio.database.SlackDB

actual class DriverFactory {
  actual fun createDriver(): SqlDriver {
   return NativeSqliteDriver(SlackDB.Schema,"SlackDB.db")
  }

  actual suspend fun createDriverBlocking(): SqlDriver {
    return NativeSqliteDriver(SlackDB.Schema,"SlackDB.db")
  }
}