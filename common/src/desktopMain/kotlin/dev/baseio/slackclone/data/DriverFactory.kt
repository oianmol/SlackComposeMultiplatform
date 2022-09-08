package dev.baseio.slackclone.data

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import dev.baseio.database.SlackDB

actual class DriverFactory {
  actual fun createDriver(schema: SqlDriver.Schema): SqlDriver {
    return JdbcSqliteDriver(url = "jdbc:sqlite:SlackDB.db").also {
      SlackDB.Schema.create(it)
    }
  }

  actual suspend fun createDriverBlocking(schema: SqlDriver.Schema): SqlDriver {
    return JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also {
      SlackDB.Schema.create(it)
    }
  }
}