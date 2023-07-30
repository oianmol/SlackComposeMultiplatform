package dev.baseio.slackclone.onboarding.vmtest

import dev.baseio.database.SlackDB
import dev.baseio.grpc.IGrpcCalls
import dev.baseio.slackclone.Platform
import dev.baseio.slackclone.Platform.ANDROID
import dev.baseio.slackclone.data.injection.viewModelDelegateModule
import dev.baseio.slackclone.platformType
import dev.baseio.slackdata.injection.dataMappersModule
import dev.baseio.slackdata.injection.encryptionModule
import dev.baseio.slackdata.injection.testDataSourcesModule
import dev.baseio.slackdata.injection.testDispatcherModule
import dev.baseio.slackdata.injection.useCaseModule
import dev.baseio.slackdata.localdata.testDbConnection
import dev.baseio.slackdata.provideKeystoreIfRequired
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceReadChannels
import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces
import dev.baseio.slackdomain.usecases.auth.UseCaseFetchAndSaveCurrentUser
import dev.baseio.slackdomain.usecases.channels.UseCaseCreateChannel
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchAndSaveChannelMembers
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchAndSaveChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseWorkspaceChannelRequest
import dev.baseio.slackdomain.usecases.users.UseCaseFetchAndSaveUsers
import dev.baseio.slackdomain.usecases.users.UseCaseFetchChannelsWithSearch
import dev.baseio.slackdomain.usecases.workspaces.UseCaseAuthWorkspace
import dev.baseio.slackdomain.usecases.workspaces.UseCaseFetchAndSaveWorkspaces
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import io.mockative.classOf
import io.mockative.Mock
import io.mockative.any
import io.mockative.given
import io.mockative.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

abstract class SlackKoinTest : KoinTest {

    @Mock
    var iGrpcCalls: IGrpcCalls = mock(classOf())

    val koinApplication: KoinApplication by lazy {
        startKoin {
            modules(
                module {
                    single {
                        SlackDB.invoke(testDbConnection())
                    }
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

    protected lateinit var selectedWorkspace: DomainLayerWorkspaces.SKWorkspace
    protected val coroutineDispatcherProvider: CoroutineDispatcherProvider by inject()
    protected val useCaseAuthWorkspace: UseCaseAuthWorkspace by inject()
    protected val useCaseCreateChannel: UseCaseCreateChannel by inject()
    protected val useCaseFetchChannelsWithSearch: UseCaseFetchChannelsWithSearch by inject()
    protected val useCaseGetSelectedWorkspace: UseCaseGetSelectedWorkspace by inject()
    private val getWorkspaces: UseCaseFetchAndSaveWorkspaces by inject()
    private val getChannels: UseCaseFetchAndSaveChannels by inject()
    private val skLocalDataSourceChannels: SKLocalDataSourceReadChannels by inject()
    protected val useCaseFetchAndSaveChannelMembers: UseCaseFetchAndSaveChannelMembers by inject()
    protected val useCaseFetchAndSaveCurrentUser: UseCaseFetchAndSaveCurrentUser by inject()
    protected val getUsers: UseCaseFetchAndSaveUsers by inject()

    @BeforeTest
    fun setUp() {
        platformMess()
    }

    private fun iGrpcCalls() = koinApplication.koin.get<IGrpcCalls>()

    suspend fun assumeAuthorized() {
        given(iGrpcCalls()).invocation {
            skKeyValueData
        }.thenReturn(koinApplication.koin.get())
        given(iGrpcCalls())
            .suspendFunction(iGrpcCalls()::currentLoggedInUser)
            .whenInvokedWith(any())
            .thenReturn(AuthTestFixtures.testUser())

        given(iGrpcCalls())
            .suspendFunction(iGrpcCalls()::currentLoggedInUser)
            .whenInvokedWith()
            .thenReturn(AuthTestFixtures.testUser())

        given(iGrpcCalls())
            .suspendFunction(iGrpcCalls()::sendMagicLink)
            .whenInvokedWith(any(), any())
            .thenReturn(AuthTestFixtures.testWorkspace())

        given(iGrpcCalls())
            .suspendFunction(iGrpcCalls()::sendMagicLink)
            .whenInvokedWith(any())
            .thenReturn(AuthTestFixtures.testWorkspace())

        given(iGrpcCalls())
            .suspendFunction(iGrpcCalls()::getWorkspaces)
            .whenInvokedWith(any())
            .thenReturn(AuthTestFixtures.testWorkspaces())

        given(iGrpcCalls())
            .suspendFunction(iGrpcCalls()::getAllDMChannels)
            .whenInvokedWith(any())
            .thenReturn(AuthTestFixtures.testDMChannels())

        given(iGrpcCalls())
            .suspendFunction(iGrpcCalls()::getPublicChannels)
            .whenInvokedWith(any(), any(), any(), any())
            .thenReturn(AuthTestFixtures.testPublicChannels("1"))

        useCaseAuthWorkspace.invoke(AuthTestFixtures.testUser().email, "slack.com")
        useCaseFetchAndSaveCurrentUser.invoke()
        getWorkspaces.invoke("some token")
        selectedWorkspace = useCaseGetSelectedWorkspace.invoke()!!
        getChannels.invoke(selectedWorkspace.uuid, 0, 20)
        val channels = skLocalDataSourceChannels.fetchAllChannels(selectedWorkspace.uuid).first()
        channels.forEach {
            useCaseFetchAndSaveChannelMembers.invoke(
                UseCaseWorkspaceChannelRequest(
                    it.workspaceId,
                    it.channelId
                )
            )
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

    private fun platformMess() {
        when (platformType()) {
            ANDROID -> {
                Dispatchers.setMain(coroutineDispatcherProvider.main)
                provideKeystoreIfRequired()
            }

            Platform.IOS -> {
            }

            Platform.JVM -> {
            }
        }
    }
}
