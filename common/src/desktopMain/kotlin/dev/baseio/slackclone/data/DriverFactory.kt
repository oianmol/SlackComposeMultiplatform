package dev.baseio.slackclone.data

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import dev.baseio.database.SlackDB

actual class DriverFactory {
  actual fun createDriver(): SqlDriver {
    return JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also {
      SlackDB.Schema.create(it)
    }
  }

  actual suspend fun createDriverBlocking(): SqlDriver {
    return JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also {
      SlackDB.Schema.create(it)
    }
  }
}