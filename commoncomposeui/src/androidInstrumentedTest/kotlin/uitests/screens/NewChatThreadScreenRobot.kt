package uitests.screens

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import uitests.robots.clickNodeWithTag
import uitests.robots.typeOnTFWithTag

@OptIn(ExperimentalTestApi::class)
class NewChatThreadScreenRobot {
    context(ComposeContentTestRule)
    suspend fun typeChannelName(text: String) {
        typeOnTFWithTag("nametf", text)
    }

    context(ComposeContentTestRule)
    suspend fun clickCreate() {
        clickNodeWithTag("btnCreateChannel")
    }

    context(ComposeContentTestRule)
    fun verifyChatThreadOpen() {
        waitUntilNodeCount(hasText("Message Not Available", substring = true), 2, 2000)
        // because we need to setup fake encryption keys for test.
    }
}

suspend fun newChatThreadScreenRobot(robot: suspend NewChatThreadScreenRobot.() -> Unit) {
    NewChatThreadScreenRobot().apply {
        robot(this)
    }
}