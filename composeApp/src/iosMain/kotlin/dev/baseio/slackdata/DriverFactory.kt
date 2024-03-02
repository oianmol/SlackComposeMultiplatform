package dev.baseio.slackdata

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import app.cash.sqldelight.driver.native.wrapConnection
import co.touchlab.sqliter.DatabaseConfiguration
import dev.baseio.database.SlackDB

actual class DriverFactory {
    private var index = 0

    actual fun createDriver(): SqlDriver {
        index++
        return NativeSqliteDriver(
            DatabaseConfiguration(
                name = "test-$index.db",
                version = SlackDB.Schema.version.toInt(),
                create = { connection ->
                    wrapConnection(connection) { SlackDB.Schema.create(it) }
                },
                inMemory = true
            )
        ).also {
            SlackDB.Schema.create(it)
        }
    }

}
