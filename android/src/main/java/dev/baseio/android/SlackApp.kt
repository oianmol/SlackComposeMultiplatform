package dev.baseio.android

import android.app.Application
import dev.baseio.database.SlackDB
import dev.baseio.security.AndroidSecurityProvider
import dev.baseio.security.RsaEcdsaKeyManager
import dev.baseio.slackclone.initKoin
import dev.baseio.slackdata.DriverFactory
import dev.baseio.slackdata.SKKeyValueData
import org.example.android.R
import org.koin.core.KoinApplication
import org.koin.dsl.module

class SlackApp : Application() {
  lateinit var koinApplication: KoinApplication

  override fun onCreate() {
    super.onCreate()
    AndroidSecurityProvider.initialize(this)
    val rsaEcdsaKeyManager = RsaEcdsaKeyManager(
      senderVerificationKey = this.resources.openRawResource(R.raw.sender_verification_key).readBytes(),
      chainId = "1"
    )
    val skKeyValueData = SKKeyValueData(this)
    koinApplication =
      initKoin(
        module {
          single { skKeyValueData }
          single { SlackDB.invoke(DriverFactory(this@SlackApp).createDriver(SlackDB.Schema)) }
          single {
            rsaEcdsaKeyManager
          }
        }
      )
  }
}