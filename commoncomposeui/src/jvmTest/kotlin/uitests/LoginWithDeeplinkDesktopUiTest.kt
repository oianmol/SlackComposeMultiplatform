package uitests

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Before
import org.junit.Test
import uitests.base.composeappsetup.SlackAppSetup
import uitests.base.composeappsetup.SlackAppSetupImpl
import uitests.base.mockgrpc.FakeDependencies
import uitests.base.mockgrpc.FakeSlackAppDependencies
import uitests.screens.dashboardScreenRobot
import uitests.screens.emailAddressInputRobot
import uitests.screens.gettingStartedRobot

class LoginWithDeeplinkDesktopUiTest : SlackAppSetup by SlackAppSetupImpl(),
    FakeDependencies by FakeSlackAppDependencies() {


    @Before
    fun setup() {
        runBlocking {
            setupFakeNetwork()
        }
    }

    @Test
    fun testLoginFlowWithDeepLink(): Unit = runBlocking {
        with(rule) {
            setAppContent()
            awaitIdle()

            gettingStartedRobot {
                sendMagicLink()
            }

            emailAddressInputRobot {
                enterEmailAndSubmit()
                enterWorkspaceAndSubmit()
            }

            withContext(Dispatchers.Main) {
                rootComponent.navigateAuthorizeWithToken("faketoken")
            }

            dashboardScreenRobot {
                testWorkspaceIsLoaded()
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
    }

}