package uitests

import kotlinx.coroutines.runBlocking
import org.junit.Before
import uitests.base.uiautomator.UiAutomation
import uitests.base.uiautomator.UiAutomationDelegateImpl
import uitests.base.authorizedtest.AuthorizedTest
import uitests.base.authorizedtest.SlackAuthorizedTest
import uitests.base.composeappsetup.SlackAppSetup
import uitests.base.composeappsetup.SlackAppSetupImpl
import uitests.base.mockgrpc.FakeDependencies
import uitests.base.mockgrpc.FakeSlackAppDependencies

class CreatePublicChannelUiTest : SlackAppSetup by SlackAppSetupImpl(),
    FakeDependencies by FakeSlackAppDependencies(),
    AuthorizedTest by SlackAuthorizedTest(), // we assume that the user is authorized!
    UiAutomation by UiAutomationDelegateImpl() {

    @Before
    fun setup() {
        runBlocking {
            setupFakeNetwork()
        }
    }

    private suspend fun setupFakeNetwork() {
        fakeLocalKeyValueStorage()

        fakeCurrentLoggedinUser()

        fakeSendMagicLink()

        fakeGetWorkspaces()

        fakeDMChannels()

        fakePublicChannels()

        fakeListenToChangeInMessages()

        fakeListenToChangeInUsers()

        fakeListenToChangeInChannels()

        fakeListenToChangeInDMChannels()

        fakeListenToChangeInChannelMembers()
    }

}