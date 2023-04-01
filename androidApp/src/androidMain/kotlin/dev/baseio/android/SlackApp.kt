package dev.baseio.android

import android.app.Application
import android.content.Context
import dev.baseio.security.AndroidSecurityProvider
import dev.baseio.slackclone.initKoin
import org.koin.core.KoinApplication
import org.koin.dsl.module

class SlackApp : Application() {
    lateinit var koinApplication: KoinApplication

    override fun onCreate() {
        super.onCreate()
        AndroidSecurityProvider.initialize(this)
        koinApplication = initKoin().also {
            it.modules(module { single<Context> { this@SlackApp } })
        }
    }
}