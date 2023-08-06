package uitests.screens

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import uitests.robots.typeOnTFWithTag

class NewChatThreadScreenRobot {
    context(ComposeContentTestRule)
    suspend fun typeChannelName(text: String) {
        typeOnTFWithTag("nametf", text)
    }
}

suspend fun newChatThreadScreenRobot(robot: suspend NewChatThreadScreenRobot.() -> Unit) {
    NewChatThreadScreenRobot().apply {
        robot(this)
    }
}