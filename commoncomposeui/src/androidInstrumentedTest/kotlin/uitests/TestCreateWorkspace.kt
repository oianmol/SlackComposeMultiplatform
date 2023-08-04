package uitests

import AppUiTestSetup
import UiAutomation
import UiAutomationDelegateImpl
import UiTestDiSetup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Test
import screens.dashboardScreenRobot
import screens.emailAddressInputRobot
import screens.gettingStartedRobot

class TestCreateWorkspace : UiTestDiSetup by AppUiTestSetup(),
    UiAutomation by UiAutomationDelegateImpl() {

    @Test
    fun testCreateWorkspaceFlow(): Unit = runBlocking {
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

}