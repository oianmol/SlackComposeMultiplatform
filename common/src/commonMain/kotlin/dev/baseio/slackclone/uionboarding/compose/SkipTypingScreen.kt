package dev.baseio.slackclone.uionboarding.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.LocalWindow
import dev.baseio.slackclone.commonui.material.SlackSurfaceAppBar
import dev.baseio.slackclone.commonui.theme.*
import dev.baseio.slackclone.navigation.ComposeNavigator
import dev.baseio.slackclone.navigation.SlackScreens
import dev.baseio.slackclone.uidashboard.compose.WindowSize
import dev.baseio.slackclone.uidashboard.compose.getWindowSizeClass

@Composable
fun SkipTypingUI(composeNavigator: ComposeNavigator) {
  val scaffoldState = rememberScaffoldState()
  val size = getWindowSizeClass(LocalWindow.current)

  Scaffold(
    backgroundColor = SlackCloneColor,
    contentColor = SlackCloneColorProvider.colors.textSecondary,
    modifier = Modifier, scaffoldState = scaffoldState,
    topBar = {
      SlackSurfaceAppBar(
        title = {},
        navigationIcon = {
          ClearBackIcon(composeNavigator)
        },
        backgroundColor = SlackCloneColor,
        elevation = 0.dp
      )
    },
    snackbarHost = {
      scaffoldState.snackbarHostState
    }
  ) { innerPadding ->
    Box(modifier = Modifier.padding(innerPadding)) {
      SlackCloneSurface(
        color = SlackCloneColor,
        modifier = Modifier
          .padding(28.dp)
      ) {
        when (size) {
          WindowSize.Phones -> SkipTypingPhone(composeNavigator)
          WindowSize.Tablets, WindowSize.BigTablets, WindowSize.DesktopOne, WindowSize.DesktopTwo -> {
            SkipTypingLarge(composeNavigator)
          }
        }
      }
    }

  }


}

@Composable
private fun SkipTypingLarge(composeNavigator: ComposeNavigator) {
  Row(
    Modifier.fillMaxSize(),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Image(
      modifier = Modifier.weight(1f, fill = true),
      painter = PainterRes.gettingStarted(),
      contentDescription = null,
      contentScale = ContentScale.Fit
    )
    Column(
      verticalArrangement = Arrangement.SpaceAround,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.weight(1f, fill = true).fillMaxHeight()
    ) {
      TitleSubtitleText()
      Spacer(Modifier.padding(8.dp))
      Column {
        EmailMeMagicLink(composeNavigator)
        Box(modifier = Modifier.height(12.dp))
        IWillSignInManually(composeNavigator)
      }
    }
  }

}

@Composable
private fun SkipTypingPhone(composeNavigator: ComposeNavigator) {
  Column(
    verticalArrangement = Arrangement.SpaceAround,
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.fillMaxSize()
  ) {
    Image(
      painter = PainterRes.gettingStarted(),
      contentDescription = null,
      contentScale = ContentScale.Fit
    )
    TitleSubtitleText()
    Spacer(Modifier.padding(8.dp))
    Column {
      EmailMeMagicLink(composeNavigator)
      Box(modifier = Modifier.height(12.dp))
      IWillSignInManually(composeNavigator)
    }
  }
}

@Composable
private fun ClearBackIcon(composeNavigator: ComposeNavigator) {
  IconButton(onClick = {
    composeNavigator.navigateUp()
  }) {
    Icon(
      imageVector = Icons.Filled.Clear,
      contentDescription = "Clear",
      modifier = Modifier.padding(start = 8.dp), tint = Color.White
    )
  }
}

@Composable
fun EmailMeMagicLink(composeNavigator: ComposeNavigator) {
  OutlinedButton(
    onClick = {
      composeNavigator.navigateScreen(SlackScreens.EmailAddressInputUI)
    },
    border = BorderStroke(1.dp, color = Color.White),
    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
    modifier = Modifier
      .fillMaxWidth()
      .height(40.dp),
  ) {
    Text(
      text = "Email me a magic link",
      style = SlackCloneTypography.subtitle1.copy(color = Color.White)
    )
  }
}

@Composable
private fun IWillSignInManually(composeNavigator: ComposeNavigator) {
  Button(
    onClick = {
      composeNavigator.navigateScreen(SlackScreens.WorkspaceInputUI)
    },
    Modifier
      .fillMaxWidth()
      .height(40.dp),
    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
  ) {
    Text(
      text = "I'll sign in manually",
      style = SlackCloneTypography.subtitle1.copy(color = SlackCloneColor)
    )
  }
}

@Composable
private fun TitleSubtitleText(modifier: Modifier = Modifier) {
  Text(
    text = buildAnnotatedString {
      withStyle(
        style = SpanStyle(
          fontSize = SlackCloneTypography.h5.fontSize, color = Color.White, fontWeight = FontWeight.Bold
        )
      ) {
        append("Want to skip the typing ?\n\n")
      }
      withStyle(
        style = SpanStyle(
          fontSize = SlackCloneTypography.h6.fontSize, color = SlackLogoYellow, fontWeight = FontWeight.Bold
        )
      ) {
        append("We can email you a magic sign-in link that adds all your workspaces at once")
      }
    },
    textAlign = TextAlign.Center,
    modifier = modifier.fillMaxWidth(),
    style = SlackCloneTypography.h4
  )
}
