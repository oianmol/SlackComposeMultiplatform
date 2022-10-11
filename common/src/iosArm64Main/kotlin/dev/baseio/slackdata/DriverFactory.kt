package dev.baseio.slackdata

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

actual class DriverFactory {
  actual fun createDriver(schema: SqlDriver.Schema): SqlDriver {
   return NativeSqliteDriver(schema,"SlackDB.db",4)
  }

  actual suspend fun createDriverBlocking(schema: SqlDriver.Schema): SqlDriver {
    return NativeSqliteDriver(schema,"SlackDB.db",4)
  }
}