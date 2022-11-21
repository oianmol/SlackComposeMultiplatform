package dev.baseio.slackclone.uionboarding.vmtest

import dev.baseio.slackclone.data.injection.viewModelDelegateModule
import dev.baseio.slackclone.Platform.ANDROID
import dev.baseio.slackclone.platformType
import dev.baseio.slackdata.injection.dataMappersModule
import dev.baseio.slackdata.injection.encryptionModule
import dev.baseio.slackdata.injection.fakeDataSourceModule
import dev.baseio.slackdata.injection.testDataModule
import dev.baseio.slackdata.injection.testDispatcherModule
import dev.baseio.slackdata.injection.useCaseModule
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceReadChannels
import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces
import dev.baseio.slackdomain.usecases.channels.UseCaseCreateChannel
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchAndSaveChannelMembers
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchAndSaveChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseWorkspaceChannelRequest
import dev.baseio.slackdomain.usecases.users.UseCaseFetchAndSaveUsers
import dev.baseio.slackdomain.usecases.users.UseCaseFetchChannelsWithSearch
import dev.baseio.slackdomain.usecases.workspaces.UseCaseCreateWorkspace
import dev.baseio.slackdomain.usecases.workspaces.UseCaseFetchAndSaveWorkspaces
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

expect fun initializePlatform()

open class SlackKoinUnitTest : KoinTest {

    lateinit var koinApplication: KoinApplication
    protected lateinit var selectedWorkspace: DomainLayerWorkspaces.SKWorkspace
    protected val coroutineDispatcherProvider: CoroutineDispatcherProvider by inject()
    protected val useCaseCreateWorkspace: UseCaseCreateWorkspace by inject()
    protected val useCaseCreateChannel:UseCaseCreateChannel by inject()
    protected val useCaseFetchChannelsWithSearch: UseCaseFetchChannelsWithSearch by inject()
    protected val useCaseGetSelectedWorkspace: UseCaseGetSelectedWorkspace by inject()
    protected val getWorkspaces: UseCaseFetchAndSaveWorkspaces by inject()
    protected val getChannels: UseCaseFetchAndSaveChannels by inject()
    val skLocalDataSourceChannels:SKLocalDataSourceReadChannels by inject()
    protected val useCaseFetchAndSaveChannelMembers: UseCaseFetchAndSaveChannelMembers by inject()
    protected val getUsers: UseCaseFetchAndSaveUsers by inject()

    @BeforeTest
    fun setUp() {
        koinApplication = startKoin {
            modules(
                testDataModule,
                useCaseModule,
                viewModelDelegateModule,
                dataMappersModule,
                encryptionModule,
                fakeDataSourceModule,
                testDispatcherModule
            )
        }
        initializePlatform()
        when (platformType()) {
            ANDROID -> {
                Dispatchers.setMain(coroutineDispatcherProvider.main)
            }

            else -> {
                // nothing special
            }
        }
    }

    suspend fun authorizeUserFirst() {
        useCaseCreateWorkspace.invoke("pp@pp.com", "pp", "pp")
        getWorkspaces.invoke()
        selectedWorkspace = useCaseGetSelectedWorkspace.invoke()!!
        getChannels.invoke(selectedWorkspace.uuid, 0, 20)
        val channels = skLocalDataSourceChannels.fetchAllChannels(selectedWorkspace.uuid).first()
        channels.forEach {
            useCaseFetchAndSaveChannelMembers.invoke(UseCaseWorkspaceChannelRequest(it.workspaceId,it.channelId))
        }
        getUsers.invoke(selectedWorkspace.uuid)
    }


    @AfterTest
    fun tearDown() {
        stopKoin()
        when (platformType()) {
            ANDROID -> {
                Dispatchers.resetMain()
            }

            else -> {
                // nothing special
            }
        }
    }

}