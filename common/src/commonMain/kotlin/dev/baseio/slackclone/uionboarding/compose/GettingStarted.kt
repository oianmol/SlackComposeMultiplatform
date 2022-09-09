package dev.baseio.slackclone.uionboarding.compose

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.LocalWindow
import dev.baseio.slackclone.commonui.theme.*
import dev.baseio.slackclone.navigation.ComposeNavigator
import dev.baseio.slackclone.navigation.SlackScreens
import dev.baseio.slackclone.uidashboard.compose.WindowSize
import dev.baseio.slackclone.uidashboard.compose.getWindowSizeClass
import dev.baseio.slackclone.uionboarding.GettingStartedVM

@Composable
fun GettingStartedUI(composeNavigator: ComposeNavigator, gettingStartedVM: GettingStartedVM) {
  val scaffoldState = rememberScaffoldState()
  val showSlackAnim by gettingStartedVM.showSlackAnim
  val size = getWindowSizeClass(LocalWindow.current)

  Scaffold(
    backgroundColor = SlackCloneColor,
    contentColor = SlackCloneColorProvider.colors.textSecondary,
    modifier = Modifier.fillMaxSize(), scaffoldState = scaffoldState, snackbarHost = {
      scaffoldState.snackbarHostState
    }
  ) { innerPadding ->
    Box(modifier = Modifier.padding(innerPadding)) {
      SlackCloneSurface(
        color = SlackCloneColor,
        modifier = Modifier
          .padding(28.dp)
      ) {
        if (showSlackAnim) {
          SlackAnimation(gettingStartedVM)
        } else {
          AnimatedVisibility(visible = !showSlackAnim) {
            when (size) {
              WindowSize.Phones -> PhoneLayout(gettingStartedVM, composeNavigator)

              WindowSize.Tablets, WindowSize.BigTablets, WindowSize.DesktopOne, WindowSize.DesktopTwo -> {
                LargeScreenLayout(gettingStartedVM, composeNavigator)
              }
            }

          }
        }
      }
    }

  }
}

@Composable
private fun LargeScreenLayout(
  gettingStartedVM: GettingStartedVM,
  composeNavigator: ComposeNavigator
) {
  val density = LocalDensity.current

  Row(
    Modifier.fillMaxSize(),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically
  ) {
    CenterImage(Modifier.padding(24.dp).weight(1f, fill = true), gettingStartedVM)
    Column(
      Modifier.weight(1f, fill = true).padding(24.dp).fillMaxHeight(), verticalArrangement = Arrangement.SpaceAround,
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Spacer(Modifier.padding(8.dp))
      IntroText(modifier = Modifier.padding(top = 12.dp), gettingStartedVM, {
        IntroEnterTransitionVertical(density)
      }) {
        IntroExitTransitionVertical()
      }
      Spacer(Modifier.padding(8.dp))
      GetStartedButton(composeNavigator, gettingStartedVM, { GetStartedEnterTransitionVertical(density) }, {
        GetStartedExitTransVertical()
      })
      Spacer(Modifier.padding(8.dp))
    }
  }
}

@Composable
private fun PhoneLayout(
  gettingStartedVM: GettingStartedVM,
  composeNavigator: ComposeNavigator
) {
  val density = LocalDensity.current
  Column(
    verticalArrangement = Arrangement.SpaceAround,
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.fillMaxSize()
  ) {
    IntroText(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), gettingStartedVM, {
      IntroEnterTransitionHorizontal(density)
    }) {
      IntroExitTransitionHorizontal()
    }
    CenterImage(Modifier.weight(1f, fill = false), gettingStartedVM)
    Spacer(Modifier.padding(8.dp))
    GetStartedButton(composeNavigator, gettingStartedVM, { GetStartedEnterTransitionHorizontal(density) }, {
      GetStartedExitTransHorizontal()
    })
  }
}

@Composable
private fun CenterImage(modifier: Modifier = Modifier, gettingStartedVM: GettingStartedVM) {
  val painter = PainterRes.gettingStarted()
  val expanded by gettingStartedVM.introTextExpanded
  AnimatedVisibility(
    visible = expanded, enter = ImageEnterTransition(),
    exit = ImageExitTrans()
  ) {
    Image(
      modifier = modifier,
      painter = painter,
      contentDescription = null,
      contentScale = ContentScale.Fit
    )
  }

}

@Composable
private fun ImageExitTrans() = shrinkOut() + fadeOut()

@Composable
private fun ImageEnterTransition() = expandIn(
  expandFrom = Alignment.Center
) + fadeIn(
  // Fade in with the initial alpha of 0.3f.
  initialAlpha = 0.3f
)

@Composable
private fun GetStartedButton(
  composeNavigator: ComposeNavigator,
  gettingStartedVM: GettingStartedVM, enterAnim: @Composable () -> EnterTransition,
  exitAnim: @Composable () -> ExitTransition
) {
  val expanded by gettingStartedVM.introTextExpanded

  AnimatedVisibility(
    visible = expanded, enter = enterAnim(),
    exit = exitAnim()
  ) {
    Button(
      onClick = {
        composeNavigator.navigateScreen(SlackScreens.SkipTypingScreen)
      },
      Modifier
        .fillMaxWidth()
        .height(40.dp),
      colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
    ) {
      Text(
        text = "Get started",
        style = SlackCloneTypography.subtitle1.copy(color = SlackCloneColor)
      )
    }
  }
}

@Composable
private fun GetStartedExitTransHorizontal() = slideOutHorizontally() + shrinkHorizontally() + fadeOut()

@Composable
private fun GetStartedEnterTransitionHorizontal(density: Density) =
  slideInHorizontally {
    // Slide in from 40 dp from the bottom.
    with(density) { +5680.dp.roundToPx() }
  } + expandHorizontally(
    // Expand from the top.
    expandFrom = Alignment.Start
  ) + fadeIn(
    // Fade in with the initial alpha of 0.3f.
    initialAlpha = 0.3f
  )

@Composable
private fun GetStartedExitTransVertical() = slideOutVertically() + shrinkVertically() + fadeOut()

@Composable
private fun GetStartedEnterTransitionVertical(density: Density) =
  slideInVertically {
    // Slide in from 40 dp from the bottom.
    with(density) { +5680.dp.roundToPx() }
  } + expandVertically(
    // Expand from the top.
    expandFrom = Alignment.Top
  ) + fadeIn(
    // Fade in with the initial alpha of 0.3f.
    initialAlpha = 0.3f
  )

@Composable
private fun IntroText(
  modifier: Modifier = Modifier,
  gettingStartedVM: GettingStartedVM,
  enterAnim: @Composable () -> EnterTransition,
  exitAnim: @Composable () -> ExitTransition
) {
  val expanded by gettingStartedVM.introTextExpanded

  AnimatedVisibility(
    visible = expanded, enter = enterAnim(),
    exit = exitAnim()
  ) {
    Text(
      text = buildAnnotatedString {
        withStyle(
          style = SpanStyle(
            fontWeight = FontWeight.Bold, color = Color.White
          )
        ) {
          append("Picture this: a messaging app,")
        }
        withStyle(
          style = SpanStyle(
            SlackLogoYellow,
            fontWeight = FontWeight.Bold
          )
        ) {
          append(" but built for work.")
        }
      },
      modifier = modifier,
      style = SlackCloneTypography.h4
    )
  }

}

@Composable
private fun IntroExitTransitionHorizontal() = slideOutHorizontally() + shrinkHorizontally() + fadeOut()

@Composable
private fun IntroEnterTransitionHorizontal(density: Density) = slideInHorizontally {
  // Slide in from 12580 dp from the left.
  with(density) { -12580.dp.roundToPx() }
} + expandHorizontally(
  // Expand from the top.
  expandFrom = Alignment.Start
) + fadeIn(
  // Fade in with the initial alpha of 0.3f.
  initialAlpha = 0.3f
)

@Composable
private fun IntroExitTransitionVertical() = slideOutVertically() + shrinkVertically() + fadeOut()

@Composable
private fun IntroEnterTransitionVertical(density: Density) = slideInVertically {
  // Slide in from 12580 dp from the left.
  with(density) { -12580.dp.roundToPx() }
} + expandVertically(
  // Expand from the top.
  expandFrom = Alignment.Top
) + fadeIn(
  // Fade in with the initial alpha of 0.3f.
  initialAlpha = 0.3f
)
