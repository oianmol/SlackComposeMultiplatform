package screens

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeContentTestRule

class DashboardScreenRobot {

    context(ComposeContentTestRule)
    @OptIn(ExperimentalTestApi::class)
    fun testWorkspaceIsLoaded() {
        waitUntilAtLeastOneExists(hasText("testworkspace"))
    }
}

suspend fun dashboardScreenRobot(robot: suspend DashboardScreenRobot.() -> Unit) {
    DashboardScreenRobot().apply {
        robot(this)
    }
}