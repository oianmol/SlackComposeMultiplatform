package dev.baseio.slackclone.onboarding.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import dev.baseio.slackclone.commonui.theme.SlackCloneColor
import dev.baseio.slackclone.onboarding.vm.EmailMagicLinkComponent

@Composable
internal fun ProcessEmailWorkspaceSendEmailUI(component: EmailMagicLinkComponent) {
    val uiState by component.viewModel.state.collectAsState()
    LaunchedEffect(Unit) {
        component.viewModel.showLoading()
    }
    Box(Modifier.fillMaxSize().background(SlackCloneColor)) {
        when {
            uiState.loading -> {
                SlackAnimation(uiState.isAnimationStarting,"loading",uiState.error)
            }

            uiState.error != null -> {

            }
        }
    }

}