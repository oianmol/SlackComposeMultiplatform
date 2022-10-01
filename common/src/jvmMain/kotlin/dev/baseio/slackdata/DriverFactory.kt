package dev.baseio.slackdata

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import java.io.File

actual class DriverFactory {
  actual fun createDriver(schema: SqlDriver.Schema): SqlDriver {
    val databasePath = File(System.getProperty("java.io.tmpdir"), "ComposeTodoDatabase.db")
    return JdbcSqliteDriver(url = "jdbc:sqlite:${databasePath.absolutePath}").also {
      schema.create(it)
    }
  }

  actual suspend fun createDriverBlocking(schema: SqlDriver.Schema): SqlDriver {
    val databasePath = File(System.getProperty("java.io.tmpdir"), "ComposeTodoDatabase.db")
    return JdbcSqliteDriver(url = "jdbc:sqlite:${databasePath.absolutePath}").also {
      schema.create(it)
    }
  }
}