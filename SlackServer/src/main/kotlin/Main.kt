import dev.baseio.slackserver.data.Database
import dev.baseio.slackserver.data.impl.*
import dev.baseio.slackserver.services.*
import dev.baseio.slackserver.services.interceptors.AuthInterceptor
import io.grpc.ServerBuilder

fun main() {
  val workspaceDataSource = WorkspaceDataSourceImpl(Database.slackDB)
  val channelsDataSource = ChannelsDataSourceImpl(Database.slackDB)
  val messagesDataSource = MessagesDataSourceImpl(Database.slackDB)
  val usersDataSource = UsersDataSourceImpl(Database.slackDB)
  val authDataSource = AuthDataSourceImpl(Database.slackDB)

  ServerBuilder.forPort(17600)
    .addService(WorkspaceService(workspaceDataSource = workspaceDataSource))
    .addService(ChannelService(channelsDataSource = channelsDataSource))
    .addService(MessagingService(messagesDataSource = messagesDataSource))
    .addService(UserService(usersDataSource = usersDataSource))
    .addService(AuthService(authDataSource = authDataSource))
    //.intercept(AuthInterceptor())
    .build()
    .start()
    .awaitTermination()
}