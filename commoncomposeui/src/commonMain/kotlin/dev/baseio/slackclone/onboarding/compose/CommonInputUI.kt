package dev.baseio.slackclone.onboarding.compose

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.commonui.material.SlackSurfaceAppBar
import dev.baseio.slackclone.commonui.theme.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun CommonInputUI(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    navigateNext: () -> Unit,
    subtitleText: String,
    TopView: @Composable (modifier: Modifier) -> Unit,
) {
    val scaffoldState = rememberScaffoldState()
    PlatformSideEffects.SkipTypingScreen()
    val localSoftwareKeyboardController = LocalSoftwareKeyboardController.current
    SlackCloneTheme {
        Scaffold(
            backgroundColor = LocalSlackCloneColor.current.uiBackground,
            contentColor = LocalSlackCloneColor.current.textSecondary,
            modifier = modifier,
            scaffoldState = scaffoldState,
            snackbarHost = {
                scaffoldState.snackbarHostState
            }, topBar = {
            SlackSurfaceAppBar(
                title = {

                },
                elevation = 0.dp,
                navigationIcon = {
                    IconButton({
                        navigateBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                backgroundColor = LocalSlackCloneColor.current.uiBackground,
                contentColor = LocalSlackCloneColor.current.textSecondary,
                actions = {}
            )
        }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                Column(
                    modifier = Modifier
                        .pointerInput(Unit) {
                            detectTapGestures {
                                localSoftwareKeyboardController?.hide()
                            }
                        }
                        .padding(12.dp)
                        .fillMaxHeight()
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    // Create references for the composables to constrain
                    Spacer(Modifier)
                    Column {
                        TopView(Modifier)
                        SubTitle(modifier = Modifier, subtitleText)
                    }
                    NextButton(modifier = Modifier, navigateNext)
                }
            }
        }
    }
}

@Composable
internal fun NextButton(modifier: Modifier = Modifier, navigateNext: () -> Unit) {
    Button(
        onClick = {
            navigateNext()
        },
        modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(top = 8.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = LocalSlackCloneColor.current.buttonColor
        )
    ) {
        Text(
            text = "Next",
            style = SlackCloneTypography.subtitle2.copy(color = LocalSlackCloneColor.current.buttonTextColor)
        )
    }
}

@Composable
internal fun SubTitle(modifier: Modifier = Modifier, subtitleText: String) {
    Text(
        subtitleText,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentWidth(align = Alignment.Start),
        style = SlackCloneTypography.subtitle2.copy(
            color = LocalSlackCloneColor.current.textPrimary.copy(alpha = 0.8f),
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Start
        )
    )
}
