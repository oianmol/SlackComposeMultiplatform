package dev.baseio.slackdata.localdata

import com.squareup.sqldelight.db.SqlDriver

internal expect fun testDbConnection(): SqlDriver
