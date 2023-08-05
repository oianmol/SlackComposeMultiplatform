package uitests.base

import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceReadChannels
import dev.baseio.slackdomain.usecases.auth.UseCaseFetchAndSaveCurrentUser
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchAndSaveChannelMembers
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchAndSaveChannels
import dev.baseio.slackdomain.usecases.users.UseCaseFetchAndSaveUsers
import dev.baseio.slackdomain.usecases.workspaces.UseCaseAuthWorkspace
import dev.baseio.slackdomain.usecases.workspaces.UseCaseFetchAndSaveWorkspaces
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import org.junit.Before

interface AuthorizedTest {

    @Before
    fun authenticateUser()
    val useCaseAuthWorkspace: UseCaseAuthWorkspace
    val useCaseGetSelectedWorkspace: UseCaseGetSelectedWorkspace
    val getWorkspaces: UseCaseFetchAndSaveWorkspaces
    val getChannels: UseCaseFetchAndSaveChannels
    val skLocalDataSourceChannels: SKLocalDataSourceReadChannels
    val useCaseFetchAndSaveChannelMembers: UseCaseFetchAndSaveChannelMembers
    val useCaseFetchAndSaveCurrentUser: UseCaseFetchAndSaveCurrentUser
    val getUsers: UseCaseFetchAndSaveUsers
}
