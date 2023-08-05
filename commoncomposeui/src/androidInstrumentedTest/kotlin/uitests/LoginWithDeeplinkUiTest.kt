package uitests

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Before
import org.junit.Test
import screens.dashboardScreenRobot
import screens.emailAddressInputRobot
import screens.gettingStartedRobot
import uitests.base.FakeDependencies
import uitests.base.FakeSlackAppDependencies
import uitests.base.SlackAppSetup
import uitests.base.SlackAppSetupImpl
import uitests.base.UiAutomation
import uitests.base.UiAutomationDelegateImpl

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

        fakeListenToChangeInMessages()

        fakeListenToChangeInUsers()

        fakeListenToChangeInChannels()

        fakeListenToChangeInDMChannels()

        fakeListenToChangeInChannelMembers()
    }

}