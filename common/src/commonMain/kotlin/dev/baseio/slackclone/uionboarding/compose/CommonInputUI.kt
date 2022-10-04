package dev.baseio.slackclone.uionboarding.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.LocalWindow
import dev.baseio.slackclone.commonui.theme.SlackCloneColor
import dev.baseio.slackclone.commonui.theme.SlackCloneSurface
import dev.baseio.slackclone.commonui.theme.SlackCloneColorProvider
import dev.baseio.slackclone.commonui.theme.SlackCloneTypography
import dev.baseio.slackclone.navigation.ComposeNavigator
import dev.baseio.slackclone.navigation.SlackScreens
import dev.baseio.slackclone.uidashboard.compose.WindowSize
import dev.baseio.slackclone.uidashboard.compose.getWindowSizeClass

@Composable
fun CommonInputUI(
  TopView: @Composable ColumnScope.(modifier: Modifier) -> Unit,
  subtitleText: String,
  onNextClick: () -> Unit
) {
  val scaffoldState = rememberScaffoldState()
  val size = getWindowSizeClass(LocalWindow.current)

  Scaffold(
    backgroundColor = SlackCloneColorProvider.colors.uiBackground,
    contentColor = SlackCloneColorProvider.colors.textSecondary,
    modifier = Modifier,
    scaffoldState = scaffoldState,
    snackbarHost = {
      scaffoldState.snackbarHostState
    }, floatingActionButton = {
      if (size != WindowSize.Phones) {
        FloatingActionButton(onClick = {
          onNextClick()
        }, backgroundColor = Color.White) {
          Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null,
            tint = SlackCloneColor
          )
        }
      }
    }
  ) { innerPadding ->
    Box(modifier = Modifier.padding(innerPadding)) {
      SlackCloneSurface(
        color = SlackCloneColorProvider.colors.uiBackground,
        modifier = Modifier
      ) {
        Column(
          modifier = Modifier
            .padding(12.dp)
            .fillMaxSize(), verticalArrangement = Arrangement.SpaceAround
        ) {
          // Create references for the composables to constrain
          Spacer(Modifier)
          Column {
            this@Column.TopView(Modifier)
            SubTitle(modifier = Modifier, subtitleText)
          }
          if (size == WindowSize.Phones) {
            NextButton(modifier = Modifier, onNextClick)
          } else {
            Spacer(Modifier)
          }
        }
      }
    }

  }
}

@Composable
fun NextButton(modifier: Modifier = Modifier, onNextClick: () -> Unit) {
  Button(
    onClick = {
      onNextClick()
    },
    modifier
      .fillMaxWidth()
      .height(50.dp)
      .padding(top = 8.dp),
    colors = ButtonDefaults.buttonColors(
      backgroundColor = SlackCloneColorProvider.colors.buttonColor
    )
  ) {
    Text(
      text = "Next",
      style = SlackCloneTypography.subtitle2.copy(color = SlackCloneColorProvider.colors.buttonTextColor)
    )
  }
}

fun navigateDashboard(composeNavigator: ComposeNavigator) {
  composeNavigator.navigateRoute(SlackScreens.DashboardRoute, removeRoute = { it, remove ->
    if (it.name == SlackScreens.OnboardingRoute.name) {
      remove()
    }
  })
}

@Composable
private fun SubTitle(modifier: Modifier = Modifier, subtitleText: String) {
  Text(
    subtitleText,
    modifier = modifier
      .fillMaxWidth()
      .wrapContentWidth(align = Alignment.Start),
    style = SlackCloneTypography.subtitle2.copy(
      color = SlackCloneColorProvider.colors.textPrimary.copy(alpha = 0.8f),
      fontWeight = FontWeight.Normal,
      textAlign = TextAlign.Start
    )
  )
}

