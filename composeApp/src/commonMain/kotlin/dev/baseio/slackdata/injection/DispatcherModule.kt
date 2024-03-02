package dev.baseio.slackdata.injection

import dev.baseio.slackdata.RealCoroutineDispatcherProvider
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import org.koin.dsl.module

val dispatcherModule = module {
    single<CoroutineDispatcherProvider> { RealCoroutineDispatcherProvider() }
}
