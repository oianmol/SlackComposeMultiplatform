package dev.baseio.slackdata.injection

import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdata.RealCoroutineDispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import mainDispatcher
import org.koin.dsl.module

val testDispatcherModule = module {
    single<CoroutineDispatcherProvider> { TestCoroutineDispatcherProvider() }
}

class TestCoroutineDispatcherProvider(
    override val default: CoroutineDispatcher = mainDispatcher,
    override val io: CoroutineDispatcher = mainDispatcher,
    override val main: CoroutineDispatcher = mainDispatcher,
    override val unconfirmed: CoroutineDispatcher = mainDispatcher
) : CoroutineDispatcherProvider
