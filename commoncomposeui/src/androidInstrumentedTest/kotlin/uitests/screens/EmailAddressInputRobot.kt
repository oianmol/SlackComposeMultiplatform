package uitests.screens

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import uitests.robots.clickWithText
import uitests.robots.typeOnTFWithTag

class EmailAddressInputRobot {

    context(ComposeContentTestRule)
    suspend fun enterEmailAndSubmit(){
        typeOnTFWithTag("emailinput", "random@yopmail.com")
        clickWithText("Next")
    }

    context(ComposeContentTestRule)
    suspend fun enterWorkspaceAndSubmit(){
        typeOnTFWithTag("workspacetf", "random")
        clickWithText("Next")
    }
}

suspend fun emailAddressInputRobot(robot: suspend EmailAddressInputRobot.() -> Unit) {
    EmailAddressInputRobot().apply {
        robot(this)
    }
}
