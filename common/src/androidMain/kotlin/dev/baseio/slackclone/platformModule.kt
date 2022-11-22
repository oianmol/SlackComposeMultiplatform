package dev.baseio.slackclone

import android.app.NotificationManager
import android.content.Context
import dev.baseio.database.SlackDB
import dev.baseio.slackdata.DriverFactory
import dev.baseio.slackdata.SKKeyValueData
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module {
    return module {
        single { SKKeyValueData(get()) }
        single { get<Context>().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
        single { SlackDB.invoke(DriverFactory(get()).createDriver(SlackDB.Schema)) }
    }
}