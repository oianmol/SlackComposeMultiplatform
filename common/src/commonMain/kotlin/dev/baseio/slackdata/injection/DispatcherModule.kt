package dev.baseio.slackdata.injection

import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdata.RealCoroutineDispatcherProvider
import org.koin.dsl.module

val dispatcherModule = module {
  single<CoroutineDispatcherProvider> { RealCoroutineDispatcherProvider() }
}
