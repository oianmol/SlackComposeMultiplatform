package dev.baseio.slackserver.data

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.asJdbcDriver
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.baseio.SlackCloneDB
import java.io.File

object Database {
  private val connectionUrl: String by lazy {
    val url = System.getenv("connection") ?: "jdbc:h2:file:./slackdb"
    // If this is a local h2 database, ensure the directories exist
    if (url.startsWith("jdbc:h2:file:")) {
      val dbFile = File(url.removePrefix("jdbc:h2:file:")).absoluteFile
      if (!dbFile.parentFile.exists()) {
        dbFile.parentFile.mkdirs()
      }
      "jdbc:h2:file:${dbFile.absolutePath}"
    } else {
      url
    }
  }

  private val datasourceConfig by lazy {
    HikariConfig().apply {
      jdbcUrl = connectionUrl
      System.getenv("username")?.toString()?.let(this::setUsername)
      System.getenv("password")?.toString()?.let(this::setPassword)
      System.getenv("poolSize")?.toString()?.toInt()?.let(this::setMaximumPoolSize)
    }
  }
  private val dataSource by lazy {
    HikariDataSource(datasourceConfig)
  }
  private val sqlDriver: SqlDriver by lazy { dataSource.asJdbcDriver() }


  val slackDB by lazy {
    SlackCloneDB(sqlDriver).also {
      SlackCloneDB.Schema.create(sqlDriver)
    }
  }
}