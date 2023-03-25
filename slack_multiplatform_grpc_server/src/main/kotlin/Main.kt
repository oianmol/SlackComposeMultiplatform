import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import dev.baseio.slackserver.data.models.SkChannel
import dev.baseio.slackserver.data.models.SkChannelMember
import dev.baseio.slackserver.data.models.SkMessage
import dev.baseio.slackserver.dataSourcesModule
import dev.baseio.slackserver.services.*
import dev.baseio.slackserver.services.interceptors.AuthInterceptor
import io.grpc.Server
import io.grpc.ServerBuilder
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.getKoin
import java.security.Security

object SlackServer {

  init {
    Security.addProvider(
      BouncyCastleProvider()
    )
    initializeFCM()

    initKoin()
  }

  fun start(): Server {
    return ServerBuilder.forPort(8080)
        //.useTransportSecurity(tlsCertFile, tlsPrivateKeyFile) // TODO enable this once the kmp library supports this.
        .addService(
          AuthService(
            pushTokenDataSource = getKoin().get(),
            authenticationDelegate = getKoin().get()
          )
        )
        .addService(QrCodeService(database = getKoin().get(), qrCodeGenerator = getKoin().get()))
        .addService(
          WorkspaceService(
            workspaceDataSource = getKoin().get(),
            authDelegate = getKoin().get()
          )
        )
        .addService(
          ChannelService(
            channelsDataSource = getKoin().get(),
            channelMemberDataSource = getKoin().get(),
            usersDataSource = getKoin().get(),
            channelMemberPNSender = getKoin().get(named(SkChannelMember::class.java.name)),
            channelPNSender = getKoin().get(named(SkChannel::class.java.name))
          )
        )
        .addService(
          MessagingService(
            messagesDataSource = getKoin().get(),
            pushNotificationForMessages = getKoin().get(named(SkMessage::class.java.name))
          )
        )
        .addService(UserService(usersDataSource = getKoin().get()))
        .intercept(AuthInterceptor())
        .build()
        .start()
  }
}

fun main() {
  SlackServer.start().awaitTermination()
  stopKoin()
}

private fun initKoin() {
  startKoin {
    modules(dataSourcesModule)
  }
}

fun initializeFCM() {
  val options = FirebaseOptions.builder()
    .setCredentials(GoogleCredentials.getApplicationDefault())
    .build()

  FirebaseApp.initializeApp(options)
}