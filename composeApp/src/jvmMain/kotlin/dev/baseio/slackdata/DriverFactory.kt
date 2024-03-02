package dev.baseio.slackdata

import app.cash.sqldelight.db.QueryResult.AsyncValue
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import dev.baseio.database.SlackDB
import java.io.File

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        val databasePath = File(System.getProperty("user.home"), "SlackDB.db")
        return JdbcSqliteDriver("jdbc:sqlite:" + databasePath.absolutePath).also {
            SlackDB.Schema.create(it)
        }
    }

}
