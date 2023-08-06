package uitests.screens

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import uitests.robots.clickNodeWithTag

@OptIn(ExperimentalTestApi::class)
class DashboardScreenRobot {

    context(ComposeContentTestRule)
    fun testWorkspaceIsLoaded() {
        waitUntilAtLeastOneExists(hasText("testworkspace"))
    }

    context(ComposeContentTestRule)
    suspend fun clickFabNewThread() {
        clickNodeWithTag("fabnewthread")
    }

    context(ComposeContentTestRule)
    suspend fun clickWorkspaceIcon() {
        clickNodeWithTag("workspaceButton")
    }

    context(ComposeContentTestRule)
    suspend fun expandPublicChannelsGroup() {
        clickNodeWithTag("expand_collapse_Channels")
    }

    context(ComposeContentTestRule)
    suspend fun clickAddNewButton(){
        clickNodeWithTag("button_add")
    }
}

suspend fun dashboardScreenRobot(robot: suspend DashboardScreenRobot.() -> Unit) {
    DashboardScreenRobot().apply {
        robot(this)
    }
}