package dev.baseio.slackclone.data

import com.squareup.sqldelight.db.SqlDriver

expect class DriverFactory {
  fun createDriver(): SqlDriver
  suspend fun createDriverBlocking(): SqlDriver
}
