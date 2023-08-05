package uitests.screens

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import uitests.robots.clickNodeWithTag

@OptIn(ExperimentalTestApi::class)
class GettingStartedRobot {

    context(ComposeContentTestRule)
    suspend fun sendMagicLink() {
        waitUntilAtLeastOneExists(hasTestTag("magiclink"), 6000)
        clickNodeWithTag("magiclink")
    }

    context(ComposeContentTestRule)
    suspend fun scanQRCode() {
        waitUntilAtLeastOneExists(hasTestTag("scanqr"), 6000)
        clickNodeWithTag("scanqr")
    }

    context(ComposeContentTestRule)
    suspend fun closeQrCodeScanner() {
        clickNodeWithTag("closescanqr")
    }
}

suspend fun gettingStartedRobot(robot: suspend GettingStartedRobot.() -> Unit) {
    GettingStartedRobot().apply {
        robot(this)
    }
}
