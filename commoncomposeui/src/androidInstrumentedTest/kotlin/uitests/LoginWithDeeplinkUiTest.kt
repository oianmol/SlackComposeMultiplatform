package uitests

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Before
import org.junit.Test
import uitests.screens.dashboardScreenRobot
import uitests.screens.emailAddressInputRobot
import uitests.screens.gettingStartedRobot
import uitests.base.uiautomator.UiAutomation
import uitests.base.uiautomator.UiAutomationDelegateImpl
import uitests.base.composeappsetup.SlackAppSetup
import uitests.base.composeappsetup.SlackAppSetupImpl
import uitests.base.mockgrpc.FakeDependencies
import uitests.base.mockgrpc.FakeSlackAppDependencies

class LoginWithDeeplinkUiTest : SlackAppSetup by SlackAppSetupImpl(),
    FakeDependencies by FakeSlackAppDependencies(),
    UiAutomation by UiAutomationDelegateImpl() {


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