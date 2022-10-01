package dev.baseio.slackdata

import com.squareup.sqldelight.db.SqlDriver

expect class DriverFactory {
  fun createDriver(schema:SqlDriver.Schema): SqlDriver
  suspend fun createDriverBlocking(schema:SqlDriver.Schema): SqlDriver
}
