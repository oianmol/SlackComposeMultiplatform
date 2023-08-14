package uitests

import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import uitests.base.authorizedtest.AuthorizedTest
import uitests.base.authorizedtest.SlackAuthorizedTest
import uitests.base.composeappsetup.SlackAppSetup
import uitests.base.composeappsetup.SlackAppSetupImpl
import uitests.base.mockgrpc.FakeDependencies
import uitests.base.mockgrpc.FakeSlackAppDependencies
import uitests.screens.dashboardScreenRobot
import uitests.screens.newChatThreadScreenRobot
import uitests.screens.searchCreateChannelUiRobot

class CreatePublicChannelUiTest : SlackAppSetup by SlackAppSetupImpl(),
    FakeDependencies by FakeSlackAppDependencies(),
    AuthorizedTest by SlackAuthorizedTest() {

    @Before
    fun setup() {
        runBlocking {
            setupFakeNetwork()
            authenticateUser()
        }
    }

    @Test
    fun testCreatePublicChannelFlow(): Unit = runBlocking {
        with(rule) {
            setAppContent()
            awaitIdle()

            dashboardScreenRobot {
                clickWorkspaceIcon()
                expandPublicChannelsGroup()
                clickAddNewButton()
            }

            searchCreateChannelUiRobot {
                clickCreateChannel()
            }

            newChatThreadScreenRobot {
                typeChannelName("test_channel")
                clickCreate()

                verifyChatThreadOpen()
            }
        }
    }

    private suspend fun setupFakeNetwork() {
        fakeLocalKeyValueStorage()

        fakeCurrentLoggedinUser()

        fakeSendMagicLink()

        fakeGetWorkspaces()

        fakeDMChannels()

        fakePublicChannels()

        fakeListenToChangeInMessages("test_message_1")

        fakeListenToChangeInUsers()

        fakeListenToChangeInChannels()

        fakeListenToChangeInDMChannels()

        fakeListenToChangeInChannelMembers()

        fakeCreatePublicChannel()

        fakeFetchMessages(listOf("test_message_1"))

        fakeChannelMembers()
    }

}