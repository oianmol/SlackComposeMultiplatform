package uitests.base.authorizedtest

import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceReadChannels
import dev.baseio.slackdomain.usecases.auth.UseCaseFetchAndSaveCurrentUser
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchAndSaveChannelMembers
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchAndSaveChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseWorkspaceChannelRequest
import dev.baseio.slackdomain.usecases.users.UseCaseFetchAndSaveUsers
import dev.baseio.slackdomain.usecases.workspaces.UseCaseAuthWorkspace
import dev.baseio.slackdomain.usecases.workspaces.UseCaseFetchAndSaveWorkspaces
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.test.KoinTest
import org.koin.test.inject
import uitests.AuthTestFixtures

class SlackAuthorizedTest : AuthorizedTest, KoinTest {
    override val useCaseAuthWorkspace: UseCaseAuthWorkspace by inject()
    override val useCaseGetSelectedWorkspace: UseCaseGetSelectedWorkspace by inject()
    override val getWorkspaces: UseCaseFetchAndSaveWorkspaces by inject()
    override val getChannels: UseCaseFetchAndSaveChannels by inject()
    override val skLocalDataSourceChannels: SKLocalDataSourceReadChannels by inject()
    override val useCaseFetchAndSaveChannelMembers: UseCaseFetchAndSaveChannelMembers by inject()
    override val useCaseFetchAndSaveCurrentUser: UseCaseFetchAndSaveCurrentUser by inject()
    override val getUsers: UseCaseFetchAndSaveUsers by inject()

    override fun authenticateUser() {
        runBlocking {
            useCaseAuthWorkspace.invoke(AuthTestFixtures.testUser().email, "slack.com")
            useCaseFetchAndSaveCurrentUser.invoke()
            getWorkspaces.invoke("some token")
            val selectedWorkspace = useCaseGetSelectedWorkspace.invoke()!!
            getChannels.invoke(selectedWorkspace.uuid, 0, 20)
            val channels =
                skLocalDataSourceChannels.fetchAllChannels(selectedWorkspace.uuid).first()
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
}
