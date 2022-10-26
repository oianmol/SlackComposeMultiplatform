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

expect val testMainDispatcher: CoroutineDispatcher

class TestCoroutineDispatcherProvider(
    override val default: CoroutineDispatcher = testMainDispatcher,
    override val io: CoroutineDispatcher = testMainDispatcher,
    override val main: CoroutineDispatcher = testMainDispatcher,
    override val unconfirmed: CoroutineDispatcher = testMainDispatcher
) : CoroutineDispatcherProvider
