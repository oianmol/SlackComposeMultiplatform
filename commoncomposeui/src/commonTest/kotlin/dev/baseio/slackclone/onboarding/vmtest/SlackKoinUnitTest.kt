package dev.baseio.slackclone.onboarding.vmtest

import dev.baseio.database.SlackDB
import dev.baseio.grpc.IGrpcCalls
import dev.baseio.security.CapillaryEncryption
import dev.baseio.security.CapillaryInstances
import dev.baseio.security.toPublicKey
import dev.baseio.slackclone.Platform
import dev.baseio.slackclone.data.injection.viewModelDelegateModule
import dev.baseio.slackclone.Platform.ANDROID
import dev.baseio.slackclone.platformType
import dev.baseio.slackdata.common.kmEmpty
import dev.baseio.slackdata.datasources.remote.channels.toKMSlackPublicKey
import dev.baseio.slackdata.injection.*
import dev.baseio.slackdata.localdata.testDbConnection
import dev.baseio.slackdata.protos.*
import dev.baseio.slackdata.provideKeystoreIfRequired
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceReadChannels
import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces
import dev.baseio.slackdomain.usecases.channels.UseCaseCreateChannel
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchAndSaveChannelMembers
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchAndSaveChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseWorkspaceChannelRequest
import dev.baseio.slackdomain.usecases.users.UseCaseFetchAndSaveUsers
import dev.baseio.slackdomain.usecases.users.UseCaseFetchChannelsWithSearch
import dev.baseio.slackdomain.usecases.workspaces.UseCaseAuthWorkspace
import dev.baseio.slackdomain.usecases.workspaces.UseCaseFetchAndSaveWorkspaces
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import org.kodein.mock.Mocker
import org.kodein.mock.UsesMocks
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

@UsesMocks(IGrpcCalls::class)
abstract class SlackKoinUnitTest : KoinTest {
    protected val mocker = Mocker()
    lateinit var koinApplication: KoinApplication

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
    protected val getUsers: UseCaseFetchAndSaveUsers by inject()

    @BeforeTest
    fun setUp() {
        initKoinApp()
        platformMess()
    }

    private fun initKoinApp() {
        koinApplication = startKoin {
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
                testDataSourcesModule(mocker),
                testDispatcherModule
            )
        }
    }

    suspend fun authorizeUserFirst() {
        mocker.every { iGrpcCalls().skKeyValueData } returns koinApplication.koin.get()
        mocker.everySuspending { iGrpcCalls().currentLoggedInUser(isAny()) } returns testUser()
        mocker.everySuspending { iGrpcCalls().sendMagicLink(isAny(), isAny()) } returns kmEmpty {  }
        mocker.everySuspending { iGrpcCalls().getWorkspaces(isAny()) } returns testWorkspaces()
        mocker.everySuspending {
            iGrpcCalls().getAllDMChannels(
                isAny(),
                isAny(),
                isAny(),
                isAny()
            )
        } returns testDMChannels()
        mocker.everySuspending {
            iGrpcCalls().getPublicChannels(
                isAny(),
                isAny(),
                isAny(),
                isAny()
            )
        } returns testPublicChannels("1")

        useCaseAuthWorkspace.invoke(testUser().email,"slack.com")
        getWorkspaces.invoke("some token")
        selectedWorkspace = useCaseGetSelectedWorkspace.invoke()!!
        getChannels.invoke(selectedWorkspace.uuid, 0, 20)
        val channels = skLocalDataSourceChannels.fetchAllChannels(selectedWorkspace.uuid).first()
        channels.forEach {
            useCaseFetchAndSaveChannelMembers.invoke(UseCaseWorkspaceChannelRequest(it.workspaceId, it.channelId))
        }
        getUsers.invoke(selectedWorkspace.uuid)
    }

    suspend fun testPublicChannels(workId:String): KMSKChannels {
        return kmSKChannels {
            this.channelsList.add(testPublicChannel("1",workId))
            this.channelsList.add(testPublicChannel("2",workId))
        }
    }

    suspend fun testPublicChannel(id:String,workId:String): KMSKChannel {
        return kmSKChannel {
            this.uuid = "channel_public_$id"
            this.name = "channel_public_$id"
            this.workspaceId = workId
            this.publicKey =
                CapillaryInstances.getInstance("channel_public_$id").publicKey().encoded.toKMSlackPublicKey()
        }
    }


    suspend fun testDMChannels(): KMSKDMChannels {
        return kmSKDMChannels {
            this.channelsList.add(kmSKDMChannel {
                this.uuid = "channel_dm_1"
                this.workspaceId = "1"
                this.senderId = "1"
                this.receiverId = "2"
                this.publicKey = CapillaryInstances.getInstance("channel_dm_1").publicKey().encoded.toKMSlackPublicKey()
            })
        }
    }

    suspend fun testEncryptedMessageFrom(first: KMSKChannel, message: String): KMSKEncryptedMessage {
        val capillary = CapillaryInstances.getInstance(first.uuid, isTest = true)
        return kmSKEncryptedMessage {
            val encrypted = capillary.encrypt(
                message.encodeToByteArray(),
                first.publicKey.keybytesList.map { it.byte.toByte() }.toByteArray().toPublicKey()
            )
            this.first = encrypted.first
            this.second = encrypted.second
        }
    }

    suspend fun channelPublicMessage(message: String) = kmSKMessage {
        this.channelId = testPublicChannels("1").channelsList.first().uuid
        this.uuid = "random${Clock.System.now().toEpochMilliseconds()}"
        this.sender = "1"
        this.workspaceId = testWorkspaces().workspacesList.first().uuid
        this.text = testEncryptedMessageFrom(testPublicChannels("1").channelsList.first(),message)
    }

    suspend fun testPublichannelMembers(channel: KMSKChannel) = kmSKChannelMembers {
        val channelPrivateKey = CapillaryInstances.getInstance(channel.uuid).privateKey()
        return kmSKChannelMembers {
            this.membersList.add(kmSKChannelMember {
                val userPublicKey = CapillaryInstances.getInstance("1").publicKey()
                val encryptedPrivateKey = CapillaryEncryption.encrypt(channelPrivateKey.encoded, userPublicKey)
                this.uuid = "somerandom${channel.uuid}1"
                this.workspaceId = channel.workspaceId
                this.channelId = channel.uuid
                this.memberId = "1"
                this.channelPrivateKey = kmSKEncryptedMessage {
                    this.first = encryptedPrivateKey.first
                    this.second = encryptedPrivateKey.second
                }
            })
            this.membersList.add(kmSKChannelMember {
                val userPublicKey = CapillaryInstances.getInstance("2").publicKey()
                val encryptedPrivateKey = CapillaryEncryption.encrypt(channelPrivateKey.encoded, userPublicKey)
                this.uuid = "somerandom${channel.uuid}2"
                this.workspaceId = channel.workspaceId
                this.channelId = channel.uuid
                this.memberId = "2"
                this.channelPrivateKey = kmSKEncryptedMessage {
                    this.first = encryptedPrivateKey.first
                    this.second = encryptedPrivateKey.second
                }
            })
        }
    }


    fun testWorkspaces(): KMSKWorkspaces {
        return kmSKWorkspaces {
            this.workspacesList.add(kmSKWorkspace {
                this.uuid = "1"
                this.name = "slack"
                this.domain = "slack.com"
            })
        }
    }

    fun iGrpcCalls() = koinApplication.koin.get<IGrpcCalls>()

    fun kmskAuthResult() = kmSKAuthResult {
        this.token = "xyz"
        this.refreshToken = "some"
        this.status = kmSKStatus {
            this.information = "Cool!"
            this.statusCode = "200"
        }
    }

    suspend fun testUser() = kmSKUser {
        this.uuid = "1"
        this.email = "sdfdsf@sdfdf.com"
        this.name = "sdfdsf"
        this.workspaceId = "1"
        this.publicKey = CapillaryInstances.getInstance("1").publicKey().encoded.toKMSlackPublicKey()
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