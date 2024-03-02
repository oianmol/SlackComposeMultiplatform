package dev.baseio.slackclone

import dev.baseio.grpc.GrpcCalls
import dev.baseio.slackclone.data.injection.viewModelDelegateModule
import dev.baseio.slackdata.injection.dataMappersModule
import dev.baseio.slackdata.injection.dataSourceModule
import dev.baseio.slackdata.injection.dispatcherModule
import dev.baseio.slackdata.injection.encryptionModule
import dev.baseio.slackdata.injection.useCaseModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import dev.baseio.slackclone.BuildConfig

lateinit var slackKoinApp: KoinApplication

fun getKoin() = slackKoinApp.koin

expect fun platformModule(): Module

fun initKoin(): KoinApplication {
    if (::slackKoinApp.isInitialized) {
        return slackKoinApp
    }
    return startKoin {
        modules(
            platformModule(),
            dataSourceModule {
                GrpcCalls(
                    skKeyValueData = slackKoinApp.koin.get(),
                    address = BuildConfig.ipAddr,
                    coroutineDispatcherProvider = slackKoinApp.koin.get()
                )
            },
            encryptionModule,
            dataMappersModule,
            useCaseModule,
            viewModelDelegateModule,
            dispatcherModule
        )
    }.also {
        slackKoinApp = it
    }
}
