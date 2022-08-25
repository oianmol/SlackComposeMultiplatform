package dev.baseio.slackclone.common.injection

import dev.baseio.slackclone.common.injection.dispatcher.CoroutineDispatcherProvider
import dev.baseio.slackclone.common.injection.dispatcher.RealCoroutineDispatcherProvider

class DispatcherModule {
    fun providesCoroutineDispatcher(): CoroutineDispatcherProvider {
        return RealCoroutineDispatcherProvider()
    }
}
