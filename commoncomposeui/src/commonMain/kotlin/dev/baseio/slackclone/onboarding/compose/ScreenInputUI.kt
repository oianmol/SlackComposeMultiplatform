package dev.baseio.slackclone.onboarding.compose

import androidx.compose.runtime.*

@Composable
fun EmailAddressInputUI(
    navigateBack: () -> Unit,
    navigateNext: (String) -> Unit,
) {
    var email by remember { mutableStateOf("") }

    CommonInputUI(
        navigateBack = navigateBack,
        navigateNext = {
            navigateNext(email)
        },
        subtitleText = "We\'ll send you an email that\'ll instantly sign you in.",
        TopView = { modifier ->
            EmailAddressInputView(modifier, email) {
                email = it
            }
        },
    )
}

@Composable
fun WorkspaceInputUI(
    navigateBack: () -> Unit,
    navigateNext: (String) -> Unit,
) {
    var workspaceUrl by remember { mutableStateOf("") }

    CommonInputUI(
        navigateBack = navigateBack,
        navigateNext = {
            navigateNext(workspaceUrl)
        },
        subtitleText = "This is the address you use to sign in to Slack",
        TopView = {
            WorkspaceInputView(it,workspaceUrl) { workspace ->
                workspaceUrl = workspace
            }
        },
    )
}
