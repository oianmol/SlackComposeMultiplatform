import dev.baseio.database.SlackDB
import dev.baseio.grpc.IGrpcCalls
import dev.baseio.slackclone.data.injection.viewModelDelegateModule
import dev.baseio.slackclone.onboarding.vmtest.AuthTestFixtures
import dev.baseio.slackdata.DriverFactory
import dev.baseio.slackdata.injection.dataMappersModule
import dev.baseio.slackdata.injection.encryptionModule
import dev.baseio.slackdata.injection.useCaseModule
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
import io.mockative.Mock
import io.mockative.classOf
import io.mockative.mock
import kotlinx.coroutines.flow.first
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject

abstract class SlackKoinTest : KoinTest {

    @Mock
    var iGrpcCalls: IGrpcCalls = mock(classOf())

    val koinApplication: KoinApplication by lazy {
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

    private lateinit var selectedWorkspace: DomainLayerWorkspaces.SKWorkspace
    private val useCaseAuthWorkspace: UseCaseAuthWorkspace by inject()
    protected val useCaseCreateChannel: UseCaseCreateChannel by inject()
    protected val useCaseFetchChannelsWithSearch: UseCaseFetchChannelsWithSearch by inject()
    private val useCaseGetSelectedWorkspace: UseCaseGetSelectedWorkspace by inject()
    private val getWorkspaces: UseCaseFetchAndSaveWorkspaces by inject()
    private val getChannels: UseCaseFetchAndSaveChannels by inject()
    private val skLocalDataSourceChannels: SKLocalDataSourceReadChannels by inject()
    private val useCaseFetchAndSaveChannelMembers: UseCaseFetchAndSaveChannelMembers by inject()
    private val useCaseFetchAndSaveCurrentUser: UseCaseFetchAndSaveCurrentUser by inject()
    private val getUsers: UseCaseFetchAndSaveUsers by inject()

    suspend fun authenticateUser() {
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
}