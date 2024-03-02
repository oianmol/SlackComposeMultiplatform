package dev.baseio.slackclone

import dev.baseio.database.SlackDB
import dev.baseio.slackdata.DriverFactory
import dev.baseio.slackdata.SKKeyValueData
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module {
    return module {
        single { SKKeyValueData() }
        single { SlackDB.invoke(DriverFactory().createDriver()) }
    }
}
