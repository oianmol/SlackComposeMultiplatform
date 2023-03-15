package dev.baseio.slackdata

import co.touchlab.sqliter.DatabaseConfiguration
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import com.squareup.sqldelight.drivers.native.wrapConnection

actual class DriverFactory {
  private var index = 0

  actual fun createInMemorySqlDriver(schema: SqlDriver.Schema): SqlDriver {
    index++
    return NativeSqliteDriver(
      DatabaseConfiguration(
        name = "test-$index.db",
        version = schema.version,
        create = { connection ->
          wrapConnection(connection) { schema.create(it) }
        },
        upgrade = { connection, oldVersion, newVersion ->
          wrapConnection(connection) {
            schema.migrate(it, oldVersion, newVersion)
          }
        },
        inMemory = true
      )
    )
  }

  actual suspend fun createDriverBlocking(schema: SqlDriver.Schema): SqlDriver {
    return NativeSqliteDriver(DatabaseConfiguration(
      name = "SlackDB.db",
      version = schema.version,
      create = { connection ->
        wrapConnection(connection) { schema.create(it) }
      },
      upgrade = { connection, oldVersion, newVersion ->
        wrapConnection(connection) {
          schema.migrate(it, oldVersion, newVersion)
        }
      },
    ))
  }

  actual fun createDriver(schema: SqlDriver.Schema): SqlDriver {
    return NativeSqliteDriver(DatabaseConfiguration(
      name = "SlackDB.db",
      version = schema.version,
      create = { connection ->
        wrapConnection(connection) { schema.create(it) }
      },
      upgrade = { connection, oldVersion, newVersion ->
        wrapConnection(connection) {
          schema.migrate(it, oldVersion, newVersion)
        }
      },
    ))
  }


}