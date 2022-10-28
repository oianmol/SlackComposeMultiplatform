package dev.baseio.slackclone.uionboarding.vmtest

import dev.baseio.slackclone.data.injection.viewModelDelegateModule
import dev.baseio.slackdata.injection.dataMappersModule
import dev.baseio.slackdata.injection.fakeDataSourceModule
import dev.baseio.slackdata.injection.testDataModule
import dev.baseio.slackdata.injection.testDispatcherModule
import dev.baseio.slackdata.injection.useCaseModule
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchAndSaveChannels
import dev.baseio.slackdomain.usecases.users.UseCaseFetchAndSaveUsers
import dev.baseio.slackdomain.usecases.workspaces.UseCaseCreateWorkspace
import dev.baseio.slackdomain.usecases.workspaces.UseCaseFetchAndSaveWorkspaces
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

open class SlackKoinUnitTest : KoinTest {

    protected lateinit var selectedWorkspace: DomainLayerWorkspaces.SKWorkspace
    protected val coroutineDispatcherProvider: CoroutineDispatcherProvider by inject()
    protected val useCaseCreateWorkspace: UseCaseCreateWorkspace by inject()
    protected val useCaseGetSelectedWorkspace: UseCaseGetSelectedWorkspace by inject()
    protected val getWorkspaces: UseCaseFetchAndSaveWorkspaces by inject()
    protected val getChannels: UseCaseFetchAndSaveChannels by inject()
    protected val getUsers: UseCaseFetchAndSaveUsers by inject()

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
        getWorkspaces.invoke()
        selectedWorkspace = useCaseGetSelectedWorkspace.invoke()!!
        getChannels.invoke(selectedWorkspace.uuid, 0, 20)
        getUsers.invoke(selectedWorkspace.uuid)
    }


    @AfterTest
    fun tearDown() {
        stopKoin()
        //Dispatchers.resetMain()
    }

}