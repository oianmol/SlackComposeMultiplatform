package dev.baseio.slackclone.onboarding.compose

import PainterRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandIn
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import dev.baseio.slackclone.LocalWindow
import dev.baseio.slackclone.Platform
import dev.baseio.slackclone.commonui.theme.SlackCloneColor
import dev.baseio.slackclone.commonui.theme.LocalSlackCloneColor
import dev.baseio.slackclone.commonui.theme.SlackCloneSurface
import dev.baseio.slackclone.commonui.theme.SlackCloneTypography
import dev.baseio.slackclone.commonui.theme.SlackLogoYellow
import dev.baseio.slackclone.getKoin
import dev.baseio.slackclone.platformType
import dev.baseio.slackclone.dashboard.compose.WindowSize
import dev.baseio.slackclone.dashboard.compose.getWindowSizeClass
import dev.baseio.slackclone.onboarding.GettingStartedComponent
import dev.baseio.slackclone.onboarding.GettingStartedVM
import dev.baseio.slackclone.qrscanner.QRScannerUI
import dev.baseio.slackclone.qrscanner.QrScannerMode

@Composable
internal fun GettingStartedUI(
  gettingStartedVM: GettingStartedComponent,
  viewModel: GettingStartedVM = gettingStartedVM.viewModel
) {
  val scaffoldState = rememberScaffoldState()
  val uiState by viewModel.componentState.subscribeAsState()
  val size = getWindowSizeClass(LocalWindow.current)
  PlatformSideEffects.GettingStartedScreen()

  Scaffold(
    backgroundColor = SlackCloneColor,
    contentColor = LocalSlackCloneColor.current.textSecondary,
    modifier = Modifier.fillMaxSize(),
    scaffoldState = scaffoldState,
    snackbarHost = {
      scaffoldState.snackbarHostState
    }
  ) { innerPadding ->
    Box(modifier = Modifier.padding(innerPadding)) {
      SlackCloneSurface(
        color = SlackCloneColor,
        modifier = Modifier
          .padding(12.dp)
      ) {
        if (uiState.showSlackAnim) {
          val shouldStartLogoAnimation by gettingStartedVM.viewModel.componentState.subscribeAsState()
          LaunchedEffect(Unit) {
            gettingStartedVM.viewModel.animate()
          }
          SlackAnimation(shouldStartLogoAnimation.isAnimationStarting)
        } else {
          when (size) {
            WindowSize.Phones -> PhoneLayout(gettingStartedVM)
            else -> {
              LargeScreenLayout(gettingStartedVM)
            }
          }
        }
      }
    }
  }
}

@Composable
internal fun LargeScreenLayout(
  gettingStartedVM: GettingStartedComponent

) {
  val scanMode by gettingStartedVM.viewModel.scanningMode.collectAsState()

  val density = LocalDensity.current

  Row(
    Modifier.fillMaxSize(),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(
      Modifier.weight(1f, fill = true).padding(24.dp).fillMaxHeight(),
      verticalArrangement = Arrangement.SpaceAround,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(Modifier.padding(16.dp))
      IntroText(modifier = Modifier.padding(top = 12.dp), gettingStartedVM, {
        IntroEnterTransitionVertical(density)
      }) {
        IntroExitTransitionVertical()
      }
      Spacer(Modifier.padding(16.dp))
      GetStartedButton(gettingStartedVM, { GetStartedEnterTransitionVertical(density) }, {
        GetStartedExitTransVertical()
      })
    }

    Column(
      Modifier.weight(1f, fill = true).padding(24.dp).fillMaxHeight(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      if (scanMode) {
        QRScannerUI(mode = QrScannerMode.CAMERA, qrCodeDelegate = getKoin().get()) {
          gettingStartedVM.navigateBack()
        }
      } else {
        CenterImage(Modifier.padding(24.dp), gettingStartedVM)
      }
    }
  }
}

@Composable
internal fun PhoneLayout(
  gettingStartedVM: GettingStartedComponent
) {
  val scanMode by gettingStartedVM.viewModel.scanningMode.collectAsState()

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

    if (scanMode) {
      QRScannerUI(modifier = Modifier.weight(1f, fill = false), QrScannerMode.CAMERA, getKoin().get()) {
        gettingStartedVM.navigateBack()
      }
    } else {
      CenterImage(Modifier.weight(1f, fill = false), gettingStartedVM)
    }
    Spacer(Modifier.padding(8.dp))
    GetStartedButton(gettingStartedVM, { GetStartedEnterTransitionHorizontal(density) }, {
      GetStartedExitTransHorizontal()
    })
  }
}

@Composable
internal fun TeamNewToSlack(modifier: Modifier, onClick: () -> Unit) {
  ClickableText(
    text = buildAnnotatedString {
      withStyle(
        style = SpanStyle(
          color = Color.White
        )
      ) {
        append("Is your team new to slack ?")
      }

      withStyle(
        style = SpanStyle(
          color = Color.White,
          textDecoration = TextDecoration.Underline
        )
      ) {
        append(" Create a workspace?")
      }
    },
    modifier = modifier,
    style = SlackCloneTypography.subtitle2,
    onClick = {
      onClick()
    }
  )
}

@Composable
internal fun CenterImage(modifier: Modifier = Modifier, gettingStartedVM: GettingStartedComponent) {
  val painter = PainterRes.gettingStarted()
  val expanded by gettingStartedVM.viewModel.componentState.subscribeAsState()
  AnimatedVisibility(
    visible = expanded.introTextExpanded,
    enter = ImageEnterTransition(),
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
internal fun ImageExitTrans() = shrinkOut() + fadeOut()

@Composable
internal fun ImageEnterTransition() = expandIn(
  expandFrom = Alignment.Center
) + fadeIn(
  // Fade in with the initial alpha of 0.3f.
  initialAlpha = 0.3f
)

@Composable
internal fun GetStartedButton(
  gettingStartedComponent: GettingStartedComponent,
  enterAnim: @Composable () -> EnterTransition,
  exitAnim: @Composable () -> ExitTransition
) {
  val expanded by gettingStartedComponent.viewModel.componentState.subscribeAsState()
  val loading by gettingStartedComponent.viewModel.loadingQR.collectAsState()
  val scanningMode by gettingStartedComponent.viewModel.scanningMode.collectAsState()

  AnimatedVisibility(
    visible = expanded.introTextExpanded,
    enter = enterAnim(),
    exit = exitAnim()
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      if (platformType() != Platform.JVM) {
        if (loading) CircularProgressIndicator(color = SlackLogoYellow) else QrCodeButton(scanningMode) {
          when (platformType()) {
            Platform.ANDROID, Platform.IOS -> {
              gettingStartedComponent.viewModel.toggleScanningMode()
            }

            Platform.JVM -> TODO("This doesn't belong here!")
          }
        }
      }

      Spacer(Modifier.padding(8.dp))

      LoginButton(gettingStartedComponent)
      Spacer(Modifier.padding(8.dp))

      TeamNewToSlack(Modifier.padding(8.dp)) {
        gettingStartedComponent.emailMagicLink()
      }
    }
  }
}

@Composable
internal fun QrCodeButton(scanMode: Boolean, onClick: () -> Unit) {
  OutlinedButton(
    onClick = {
      onClick()
    },
    Modifier
      .fillMaxWidth()
      .height(40.dp),
    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
    border = BorderStroke(2.dp, Color.White)
  ) {
    Text(
      text = if (!scanMode) "Scan QR" else "Close Scanner",
      style = SlackCloneTypography.subtitle1.copy(color = Color.White, fontWeight = FontWeight.Bold)
    )
  }
}


@Composable
internal fun LoginButton(
  gettingStartedVM: GettingStartedComponent
) {
  Button(
    onClick = {
      gettingStartedVM.emailMagicLink()
    },
    Modifier
      .fillMaxWidth()
      .height(40.dp),
    colors = ButtonDefaults.buttonColors(backgroundColor = Color(52, 120, 92, 255))
  ) {
    Text(
      text = "Email me a magic link.",
      style = SlackCloneTypography.subtitle1.copy(color = Color.White, fontWeight = FontWeight.Bold)
    )
  }
}

@Composable
internal fun GetStartedExitTransHorizontal() = slideOutHorizontally() + shrinkHorizontally() + fadeOut()

@Composable
internal fun GetStartedEnterTransitionHorizontal(density: Density) =
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
internal fun GetStartedExitTransVertical() = slideOutVertically() + shrinkVertically() + fadeOut()

@Composable
internal fun GetStartedEnterTransitionVertical(density: Density) =
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
internal fun IntroText(
  modifier: Modifier = Modifier,
  gettingStartedVM: GettingStartedComponent,
  enterAnim: @Composable () -> EnterTransition,
  exitAnim: @Composable () -> ExitTransition
) {
  val expanded by gettingStartedVM.viewModel.componentState.subscribeAsState()

  AnimatedVisibility(
    visible = expanded.introTextExpanded,
    enter = enterAnim(),
    exit = exitAnim()
  ) {
    Column(modifier) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
          modifier = Modifier.size(128.dp),
          painter = PainterRes.slackLogo(),
          contentDescription = null
        )
        Text(
          text = buildAnnotatedString {
            withStyle(
              style = SpanStyle(
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            ) {
              append("Slack")
            }
          },
          modifier = Modifier.padding(4.dp),
          style = SlackCloneTypography.h4
        )
      }

      Text(
        text = buildAnnotatedString {
          withStyle(
            style = SpanStyle(
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          ) {
            append("Slack brings the team together")
          }
          withStyle(
            style = SpanStyle(
              SlackLogoYellow,
              fontWeight = FontWeight.Bold
            )
          ) {
            append(" wherever you are.")
          }
        },
        style = SlackCloneTypography.h4
      )
    }
  }
}

@Composable
internal fun IntroExitTransitionHorizontal() = slideOutHorizontally() + shrinkHorizontally() + fadeOut()

@Composable
internal fun IntroEnterTransitionHorizontal(density: Density) = slideInHorizontally {
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
internal fun IntroExitTransitionVertical() = slideOutVertically() + shrinkVertically() + fadeOut()

@Composable
internal fun IntroEnterTransitionVertical(density: Density) = slideInVertically {
  // Slide in from 12580 dp from the left.
  with(density) { -12580.dp.roundToPx() }
} + expandVertically(
  // Expand from the top.
  expandFrom = Alignment.Top
) + fadeIn(
  // Fade in with the initial alpha of 0.3f.
  initialAlpha = 0.3f
)
