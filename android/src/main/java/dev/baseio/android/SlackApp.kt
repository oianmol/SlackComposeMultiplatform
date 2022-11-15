package dev.baseio.android

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import dev.baseio.database.SlackDB
import dev.baseio.security.AndroidSecurityProvider
import dev.baseio.slackclone.initKoin
import dev.baseio.slackdata.DriverFactory
import dev.baseio.slackdata.SKKeyValueData
import org.koin.core.KoinApplication
import org.koin.dsl.module

class SlackApp : Application() {
  lateinit var koinApplication: KoinApplication

  override fun onCreate() {
    super.onCreate()
    AndroidSecurityProvider.initialize(this)
    val skKeyValueData = SKKeyValueData(this)
    koinApplication =
      initKoin(
        module {
          single { skKeyValueData }
          single { this@SlackApp.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
          single { SlackDB.invoke(DriverFactory(this@SlackApp).createDriver(SlackDB.Schema)) }
        }
      )
  }
}