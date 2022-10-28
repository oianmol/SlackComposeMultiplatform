package dev.baseio.slackclone.uionboarding.vmtest

import dev.baseio.slackclone.data.injection.viewModelDelegateModule
import dev.baseio.slackdata.injection.dataMappersModule
import dev.baseio.slackdata.injection.fakeDataSourceModule
import dev.baseio.slackdata.injection.testDataModule
import dev.baseio.slackdata.injection.testDispatcherModule
import dev.baseio.slackdata.injection.useCaseModule
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.usecases.auth.LoginUseCase
import dev.baseio.slackdomain.usecases.workspaces.FindWorkspacesUseCase
import dev.baseio.slackdomain.usecases.workspaces.UseCaseCreateWorkspace
import dev.baseio.slackdomain.usecases.workspaces.UseCaseFetchAndSaveWorkspaces
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

    protected val coroutineDispatcherProvider: CoroutineDispatcherProvider by inject()
    protected val useCaseCreateWorkspace: UseCaseCreateWorkspace by inject()
    private val workspaces: UseCaseFetchAndSaveWorkspaces by inject()

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
        //Dispatchers.setMain(coroutineDispatcherProvider.main)
    }

    suspend fun authorizeUserFirst() {
        useCaseCreateWorkspace.invoke("anmol.verma4@gmail.com", "password", "gmail")
        workspaces.invoke()
        //Dispatchers.resetMain()
    }


    @AfterTest
    fun tearDown() {
        stopKoin()
    }

}