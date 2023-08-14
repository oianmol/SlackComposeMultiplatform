package uitests.screens

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import uitests.robots.clickNodeWithTag

class SearchCreateChannelUiRobot {
    context(ComposeContentTestRule)
    suspend fun clickCreateChannel() {
        clickNodeWithTag("newChannelFab")
    }
}

suspend fun searchCreateChannelUiRobot(robot: suspend SearchCreateChannelUiRobot.() -> Unit) {
    SearchCreateChannelUiRobot().apply {
        robot(this)
    }
}