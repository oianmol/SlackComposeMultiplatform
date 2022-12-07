package dev.baseio.slackclone.onboarding.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.baseio.slackclone.commonui.theme.SlackCloneColor
import dev.baseio.slackclone.commonui.theme.SlackCloneTypography
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
                SlackAnimation(uiState.isAnimationStarting)
            }

            uiState.error != null -> {
                Text(
                    text = "Failed to send you the email 🤷🏻‍♂ \n${uiState.error?.message}️",
                    fontWeight = FontWeight.Light,
                    style = SlackCloneTypography.subtitle2.copy(
                        color = slackWhite,
                        letterSpacing = 4.sp
                    ),
                    modifier = Modifier
                )
            }

            else -> {
                Text(
                    text = "We have sent you the 📧!,Please check your mailbox before the link expires!️",
                    fontWeight = FontWeight.Bold,
                    style = SlackCloneTypography.subtitle2.copy(
                        color = slackWhite,
                        letterSpacing = 4.sp
                    ),
                    modifier = Modifier
                )

            }
        }
    }

}