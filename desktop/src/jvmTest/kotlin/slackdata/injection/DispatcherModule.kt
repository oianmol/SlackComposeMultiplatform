package dev.baseio.slackdata.injection

import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdata.RealCoroutineDispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import mainDispatcher
import org.koin.dsl.module

val testDispatcherModule = module {
    single<CoroutineDispatcherProvider> { TestCoroutineDispatcherProvider() }
}

class TestCoroutineDispatcherProvider(
    override val default: CoroutineDispatcher = TestCoroutineDispatcher(),
    override val io: CoroutineDispatcher =  TestCoroutineDispatcher(),
    override val main: CoroutineDispatcher =  TestCoroutineDispatcher(),
    override val unconfirmed: CoroutineDispatcher =  TestCoroutineDispatcher()
) : CoroutineDispatcherProvider
