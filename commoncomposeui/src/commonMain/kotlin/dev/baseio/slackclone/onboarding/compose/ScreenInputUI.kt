package dev.baseio.slackclone.onboarding.compose

import androidx.compose.runtime.Composable

@Composable
fun EmailAddressInputUI() {
    CommonInputUI(
        navigateBack = {},
        navigateNext = { },
        subtitleText = "We\\'ll send you an email that\\'ll instantly sign you in.",
        TopView = { modifier ->
            EmailAddressInputView(modifier)
        },
    )
}

@Composable
fun WorkspaceInputUI() {
    CommonInputUI(
        navigateBack = {},
        navigateNext = {},
        subtitleText = "This is the address you use to sign in to Slack",
        TopView = {
            WorkspaceInputView(it)
        },
    )
}
