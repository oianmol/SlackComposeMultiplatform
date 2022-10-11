package dev.baseio.slackclone.uionboarding.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.commonui.theme.SlackCloneSurface
import dev.baseio.slackclone.commonui.theme.SlackCloneColorProvider
import dev.baseio.slackclone.commonui.theme.SlackCloneTypography
import dev.baseio.slackclone.navigation.ComposeNavigator
import dev.baseio.slackclone.navigation.SlackScreens

@Composable
fun CommonInputUI(
  topView: @Composable ColumnScope.(modifier: Modifier) -> Unit,
  bottomView: @Composable ColumnScope.(modifier: Modifier) -> Unit,
  subtitleText: String,
) {
  val scaffoldState = rememberScaffoldState()

  Scaffold(
    backgroundColor = SlackCloneColorProvider.colors.uiBackground,
    contentColor = SlackCloneColorProvider.colors.textSecondary,
    modifier = Modifier,
    scaffoldState = scaffoldState,
    snackbarHost = {
      scaffoldState.snackbarHostState
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
            this@Column.topView(Modifier)
            SubTitle(modifier = Modifier, subtitleText)
          }
          bottomView(Modifier)
        }
      }
    }

  }
}

@Composable
fun NextButtonWithText(modifier: Modifier = Modifier, text:String, onNextClick: () -> Unit) {
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
      text = text,
      style = SlackCloneTypography.subtitle2.copy(color = SlackCloneColorProvider.colors.buttonTextColor)
    )
  }
}

fun navigateDashboard(composeNavigator: ComposeNavigator) {
  composeNavigator.navigateRoute(SlackScreens.DashboardRoute, removeRoute = { it, remove ->
    remove() // remove all routes!
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

