import dev.baseio.slackserver.data.Database
import dev.baseio.slackserver.data.impl.ChannelsDataSourceImpl
import dev.baseio.slackserver.data.impl.MessagesDataSourceImpl
import dev.baseio.slackserver.data.impl.UsersDataSourceImpl
import dev.baseio.slackserver.data.impl.WorkspaceDataSourceImpl
import dev.baseio.slackserver.services.*
import io.grpc.ServerBuilder
import kotlinx.coroutines.runBlocking

fun main() {
  val workspaceDataSource = WorkspaceDataSourceImpl(Database.slackDB)
  val channelsDataSource = ChannelsDataSourceImpl(Database.slackDB)
  val messagesDataSource = MessagesDataSourceImpl(Database.slackDB)
  val usersDataSource = UsersDataSourceImpl(Database.slackDB)

  val fakeDataLoader = FakeDataPreloader(workspaceDataSource, channelsDataSource, messagesDataSource, usersDataSource)
  runBlocking {
    fakeDataLoader.preload()
  }
  ServerBuilder.forPort(17600)
    .addService(WorkspaceService(workspaceDataSource = workspaceDataSource))
    .addService(ChannelService(channelsDataSource = channelsDataSource))
    .addService(MessagingService(messagesDataSource = messagesDataSource))
    .addService(UserService(usersDataSource = usersDataSource))
    .build()
    .start()
    .awaitTermination()
}