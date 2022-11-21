package dev.baseio.slackdata.localdata

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import dev.baseio.database.SlackDB

internal actual fun testDbConnection(): SqlDriver {
    return JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        .also { SlackDB.Schema.create(it) }
}