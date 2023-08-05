package uitests

import kotlinx.coroutines.runBlocking
import org.junit.Before
import uitests.base.AuthorizedTest
import uitests.base.FakeDependencies
import uitests.base.FakeSlackAppDependencies
import uitests.base.SlackAppSetup
import uitests.base.SlackAppSetupImpl
import uitests.base.SlackAuthorizedTest
import uitests.base.UiAutomation
import uitests.base.UiAutomationDelegateImpl

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