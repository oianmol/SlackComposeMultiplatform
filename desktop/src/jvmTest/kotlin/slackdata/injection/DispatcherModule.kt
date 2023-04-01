package dev.baseio.slackdata.injection

import dev.baseio.slackdomain.CoroutineDispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

val testDispatcherModule = module {
    single<CoroutineDispatcherProvider> { TestCoroutineDispatcherProvider() }
}

class TestCoroutineDispatcherProvider(
    override val default: CoroutineDispatcher = Dispatchers.Main,
    override val io: CoroutineDispatcher = Dispatchers.Main,
    override val main: CoroutineDispatcher = Dispatchers.Main,
    override val unconfirmed: CoroutineDispatcher = Dispatchers.Main,
) : CoroutineDispatcherProvider
