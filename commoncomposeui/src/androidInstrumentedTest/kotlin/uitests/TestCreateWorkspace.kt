package uitests

import AppUiTestSetup
import UiAutomation
import UiAutomationDelegateImpl
import UiTestDiSetup
import kotlinx.coroutines.runBlocking
import org.junit.Test

class TestCreateWorkspace : UiTestDiSetup by AppUiTestSetup(),
    UiAutomation by UiAutomationDelegateImpl() {

    @Test
    fun testCreateWorkspaceFlow() = runBlocking {
        with(rule) {
            setAppContent()
            awaitIdle()
        }
    }

}