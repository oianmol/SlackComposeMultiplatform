package dev.baseio.slackclone.uionboarding.vmtest

import dev.baseio.slackclone.data.injection.viewModelDelegateModule
import dev.baseio.slackdata.injection.dataMappersModule
import dev.baseio.slackdata.injection.fakeDataSourceModule
import dev.baseio.slackdata.injection.testDataModule
import dev.baseio.slackdata.injection.testDispatcherModule
import dev.baseio.slackdata.injection.useCaseModule
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

open class SlackKoinUnitTest : KoinTest {

    private val coroutineDispatcherProvider: CoroutineDispatcherProvider by inject()

    @BeforeTest
    fun setUp() {
        startKoin {
            modules(
                testDataModule,
                useCaseModule,
                viewModelDelegateModule,
                dataMappersModule,
                fakeDataSourceModule,
                testDispatcherModule
            )
        }
    }


    @AfterTest
    fun tearDown() {
        stopKoin()
    }

}