import app.cash.turbine.test
import dev.baseio.slackdata.protos.*
import dev.baseio.slackserver.data.database.Database
import dev.baseio.slackserver.data.impl.*
import dev.baseio.slackserver.data.sources.UsersDataSource
import dev.baseio.slackserver.services.*
import dev.baseio.slackserver.services.interceptors.AUTHORIZATION_METADATA_KEY
import dev.baseio.slackserver.services.interceptors.AuthInterceptor
import io.grpc.CallOptions
import io.grpc.ManagedChannel
import io.grpc.Metadata
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import io.grpc.testing.GrpcCleanupRule
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest
import kotlin.time.Duration.Companion.seconds


class TestQRCodeService {

    private lateinit var channel: ManagedChannel

    @Rule
    val grpcCleanup = GrpcCleanupRule()

    val iQrcodeGenerator : IQrCodeGenerator = FakeQrCodeGenerator()
    val workspaceDataSource = WorkspaceDataSourceImpl(Database.slackDB)
    val usersDataSource: UsersDataSource = UsersDataSourceImpl(Database.slackDB)
    val authDataSource = AuthDataSourceImpl(Database.slackDB)
    val authenticationDelegate: AuthenticationDelegate = AuthenticationDelegateImpl(authDataSource, usersDataSource)

    @BeforeTest
    fun before(){
        val workspaceService =  WorkspaceService(workspaceDataSource = workspaceDataSource, authDelegate = authenticationDelegate)
        val qrCodeService = QrCodeService(database = Database.slackDB, qrCodeGenerator = iQrcodeGenerator)

        val serverName: String = InProcessServerBuilder.generateName()
        grpcCleanup.register(
            InProcessServerBuilder
                .forName(serverName).directExecutor()
                .addService(workspaceService)
                .addService(qrCodeService )
                .intercept(AuthInterceptor())
                .build().start()
        )
         channel = grpcCleanup.register(
            InProcessChannelBuilder.forName(serverName).directExecutor().build()
        )
    }

    @Test
    fun `qr code is generated when requested and deleted once unsubscribed by consumer`(){
       runTest{
           val metadata = authorizeTestUser(channel)
           val qrCodeServiceClient = QrCodeServiceGrpcKt.QrCodeServiceCoroutineStub(channel)
           qrCodeServiceClient.generateQRCode(sKQrCodeGenerator {},headers = metadata).test(50.seconds) {
               val item = awaitItem().apply {
                   this.byteArrayList
               }
               assert(item.byteArrayList.isNotEmpty())
               qrCodeServiceClient.verifyQrCode(sKQRAuthVerify {
                   this.token = iQrcodeGenerator.randomToken()
               }, headers = metadata)
               assert(awaitItem().hasAuthResult())
               awaitComplete()

               assert(iQrcodeGenerator.find(iQrcodeGenerator.randomToken())==null)
           }
       }
    }

    private suspend fun authorizeTestUser(channel: ManagedChannel): Metadata {
        val workspaceClient = WorkspaceServiceGrpcKt.WorkspaceServiceCoroutineStub(channel)
        val result = workspaceClient.letMeIn(
            SKCreateWorkspaceRequest.newBuilder()
                .setUser(SKAuthUser.newBuilder().setEmail("anmol.verma4@gmail.com").setPassword("password"))
                .setWorkspace(
                    SKWorkspace.newBuilder()
                        .setName("gmail")
                        .build()
                )
                .build()
        )
        val metadata = Metadata()
        metadata.put(AUTHORIZATION_METADATA_KEY, "Bearer " + result.token);
        return metadata
    }
}