package dev.baseio.slackclone

import dev.baseio.slackclone.data.injection.viewModelDelegateModule
import dev.baseio.slackdata.injection.dataMappersModule
import dev.baseio.slackdata.injection.dataSourceModule
import dev.baseio.slackdata.injection.dispatcherModule
import dev.baseio.slackdata.injection.encryptionModule
import dev.baseio.slackdata.injection.useCaseModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module

private lateinit var koinApp: KoinApplication

fun getKoin() = koinApp.koin

expect fun platformModule(): Module

fun initKoin(): KoinApplication {
    return startKoin {
        modules(
            platformModule(),
            dataSourceModule,
            encryptionModule,
            dataMappersModule,
            useCaseModule,
            viewModelDelegateModule,
            dispatcherModule
        )
    }.also {
        koinApp = it
    }
}
