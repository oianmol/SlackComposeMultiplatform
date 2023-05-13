package dev.baseio.slackclone.onboarding.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.baseio.slackclone.commonui.material.SlackSurfaceAppBar
import dev.baseio.slackclone.commonui.theme.LocalSlackCloneColor
import dev.baseio.slackclone.commonui.theme.SlackCloneTypography
import dev.baseio.slackclone.onboarding.AuthorizeTokenComponent

@Composable
internal fun ProcessAuthTokenScreen(component: AuthorizeTokenComponent) {
    val uiState by component.viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        component.viewModel.showSlackProgressAnimation()
    }
    Scaffold(
        backgroundColor = LocalSlackCloneColor.current.uiBackground,
        contentColor = LocalSlackCloneColor.current.textSecondary,
        topBar = {
            SlackSurfaceAppBar(
                title = {},
                navigationIcon = {
                    IconButton({
                        component.navigateBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                backgroundColor = LocalSlackCloneColor.current.uiBackground,
                contentColor = LocalSlackCloneColor.current.textSecondary,
                actions = {}
            )
        },
    ) { paddingValues ->
        Box(
            Modifier.fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.loading -> {
                    SlackAnimation(uiState.loaderState)
                }

                uiState.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Failed to verify the token! \n${uiState.error?.message}️",
                            fontWeight = FontWeight.Light,
                            style = SlackCloneTypography.subtitle1.copy(
                                color = slackWhite,
                                letterSpacing = 4.sp
                            ),
                            modifier = Modifier.padding(8.dp)
                        )

                        TextButton(
                            {
                                component.viewModel.retry()
                            }, colors = ButtonDefaults.buttonColors(
                                backgroundColor = LocalSlackCloneColor.current.buttonColor,
                                contentColor = LocalSlackCloneColor.current.buttonTextColor
                            )
                        ) {
                            Text(
                                text = "Retry",
                                fontWeight = FontWeight.Light,
                                style = SlackCloneTypography.subtitle2.copy(
                                    letterSpacing = 4.sp
                                ),
                            )
                        }
                    }
                }

                else -> {
                    Column(modifier = Modifier.align(Alignment.Center).padding(8.dp)) {
                        Text(
                            text = "Awesome! you are logged in!",
                            fontWeight = FontWeight.Bold,
                            style = SlackCloneTypography.subtitle2.copy(
                                color = slackWhite,
                                letterSpacing = 4.sp
                            ),
                            modifier = Modifier.padding(8.dp)
                        )

                        CircularProgressIndicator()
                    }

                }
            }
        }
    }
}
