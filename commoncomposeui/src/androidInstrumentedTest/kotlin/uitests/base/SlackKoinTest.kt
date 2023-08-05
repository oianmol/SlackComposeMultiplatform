package uitests.base

import dev.baseio.database.SlackDB
import dev.baseio.grpc.IGrpcCalls
import dev.baseio.slackclone.data.injection.viewModelDelegateModule
import dev.baseio.slackdata.DriverFactory
import dev.baseio.slackdata.injection.dataMappersModule
import dev.baseio.slackdata.injection.encryptionModule
import dev.baseio.slackdata.injection.useCaseModule
import io.mockative.Mock
import io.mockative.classOf
import io.mockative.mock
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module
import testDataSourcesModule
import testDispatcherModule

abstract class SlackKoinTest {

    @Mock
    var iGrpcCalls: IGrpcCalls = mock(classOf())

    val testKoinApplication: KoinApplication by lazy {
        startKoin {
            modules(
                module {
                    single { SlackDB.invoke(DriverFactory(get()).createDriver(SlackDB.Schema)) }
                },
                useCaseModule,
                viewModelDelegateModule,
                dataMappersModule,
                encryptionModule,
                testDataSourcesModule {
                    iGrpcCalls
                },
                testDispatcherModule
            )
        }
    }
}