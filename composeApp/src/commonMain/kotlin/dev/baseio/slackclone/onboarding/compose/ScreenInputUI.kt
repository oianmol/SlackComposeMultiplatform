package dev.baseio.slackclone.onboarding.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import dev.baseio.slackdomain.isEmailValid

@Composable
internal fun EmailAddressInputUI(
    navigateBack: () -> Unit,
    navigateNext: (String) -> Unit,
) {
    var email by rememberSaveable { mutableStateOf("") }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    CommonInputUI(
        modifier = Modifier,
        navigateBack = navigateBack,
        navigateNext = {
            if (isEmailValid(email)) {
                errorMessage = null
                navigateNext(email)
            } else {
                errorMessage = "Please enter a valid email!"
            }
        },
        subtitleText = errorMessage ?: "We will send you an email that will instantly sign you in.",
        TopView = { modifier ->
            EmailAddressInputView(modifier, email) {
                email = it
            }
        },
    )
}

@Composable
internal fun WorkspaceInputUI(
    navigateBack: () -> Unit,
    navigateNext: (String) -> Unit,
) {
    var workspaceUrl by rememberSaveable { mutableStateOf("") }

    CommonInputUI(
        navigateBack = navigateBack,
        navigateNext = {
            navigateNext(workspaceUrl)
        },
        subtitleText = "This is the workspace domain you want to authorize.",
        TopView = {
            WorkspaceInputView(it, workspaceUrl) { workspace ->
                workspaceUrl = workspace
            }
        },
    )
}
