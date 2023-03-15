package dev.baseio.slackdata

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import java.io.File

actual class DriverFactory {
    actual fun createDriver(schema: SqlDriver.Schema): SqlDriver {
        val databasePath = File(System.getProperty("user.home"), "SlackDB.db")
        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:"+databasePath.absolutePath)
        schema.create(driver)
        return driver
    }

    actual suspend fun createDriverBlocking(schema: SqlDriver.Schema): SqlDriver {
        val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        schema.create(driver)
        return driver
    }

    actual fun createInMemorySqlDriver(schema: SqlDriver.Schema): SqlDriver {
        val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        schema.create(driver)
        return driver
    }
}