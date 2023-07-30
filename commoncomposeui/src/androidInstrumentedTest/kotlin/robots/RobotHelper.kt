package dev.transactionapp.mobile.robots

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput

context(ComposeContentTestRule)
suspend fun clickNodeWithTag(tag: String) {
    onNodeWithTag(tag)
        .performClick()
    awaitIdle()
}


context(ComposeContentTestRule)
suspend fun scrollAndTypeOnTFWithTag(tag: String, text: String) {
    scrollToTag(tag)
    onNodeWithTag(tag)
        .performTextInput(text)
    awaitIdle()
}

context(ComposeContentTestRule)
suspend fun scrollToTag(tag: String){
    onNodeWithTag(tag)
        .performScrollTo()
    awaitIdle()
}


context(ComposeContentTestRule)
@OptIn(ExperimentalTestApi::class)
suspend fun assertExists(tag: String){
    waitUntilAtLeastOneExists(hasTestTag(tag))
    awaitIdle()
}


context(ComposeContentTestRule)
suspend fun clickWithText(text: String) {
    onNodeWithText(text)
        .performClick()
    awaitIdle()
}