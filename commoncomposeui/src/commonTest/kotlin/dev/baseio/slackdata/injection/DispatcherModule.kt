package dev.baseio.slackdata.injection

import dev.baseio.slackdomain.CoroutineDispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
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
