package dev.baseio.slackclone.common.injection

import dev.baseio.slackclone.common.injection.dispatcher.CoroutineDispatcherProvider
import dev.baseio.slackclone.common.injection.dispatcher.RealCoroutineDispatcherProvider
import org.koin.dsl.module

val dispatcherModule = module {
  single<CoroutineDispatcherProvider> { RealCoroutineDispatcherProvider() }
}
