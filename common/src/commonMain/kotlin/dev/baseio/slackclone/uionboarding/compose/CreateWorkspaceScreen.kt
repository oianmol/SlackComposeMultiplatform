package dev.baseio.slackclone.uionboarding.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.LocalWindow
import dev.baseio.slackclone.commonui.theme.SlackCloneColor
import dev.baseio.slackclone.commonui.theme.SlackCloneColorProvider
import dev.baseio.slackclone.commonui.theme.SlackCloneSurface
import dev.baseio.slackclone.commonui.theme.SlackCloneTypography
import dev.baseio.slackclone.navigation.ComposeNavigator
import dev.baseio.slackclone.uidashboard.compose.WindowSize
import dev.baseio.slackclone.uidashboard.compose.getWindowSizeClass
import dev.baseio.slackclone.uionboarding.vm.WorkspaceCreateVM

@Composable
fun CreateWorkspaceScreen(composeNavigator: ComposeNavigator, viewModel: WorkspaceCreateVM) {
  val scaffoldState = rememberScaffoldState()
  val size = getWindowSizeClass(LocalWindow.current)
  PlatformSideEffects.GettingStartedScreen()

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
        when (size) {
          WindowSize.Phones -> CreateWorkspacePhoneLayout(composeNavigator, viewModel)
          else -> {
            CreateWorkspaceLargeScreenLayout(composeNavigator, viewModel)
          }
        }
      }
    }

  }
}

@Composable
fun Title() {
  Text(
    "Create a new workspace",
    style = SlackCloneTypography.h4.copy(
      fontWeight = FontWeight.Bold,
      color = SlackCloneColorProvider.colors.appBarTextTitleColor
    )
  )
}

@Composable
fun SubTitle() {
  Text(
    "To make a workspace from scratch, please confirm your email address.",
    style = SlackCloneTypography.h6.copy(color = SlackCloneColorProvider.colors.appBarTextSubTitleColor)
  )
}

@Composable
fun Heading() {
  Column(Modifier.fillMaxWidth()) {
    Title()
    SubTitle()
  }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WorkspaceCreateForm(viewModel: WorkspaceCreateVM, composeNavigator: ComposeNavigator) {
  val email by viewModel.emailForm.collectAsState()
  val name by viewModel.nameForm.collectAsState()
  Column {
    EmailTF(Modifier.padding(8.dp), email) { emailNew ->
      viewModel.emailForm.value = emailNew
    }
    NameTF(Modifier.padding(8.dp), name) { nameNew ->
      viewModel.nameForm.value = nameNew
    }
    CreateWorkspaceButton(composeNavigator, viewModel)
  }


}

@Composable
fun CreateWorkspaceButton(composeNavigator: ComposeNavigator, viewModel: WorkspaceCreateVM) {
  Button(
    onClick = {
      viewModel.createWorkspace()
    },
    Modifier
      .fillMaxWidth()
      .height(40.dp),
    colors = ButtonDefaults.buttonColors(backgroundColor = Color(52, 120, 92, 255))
  ) {
    Text(
      text = "Create Workspace",
      style = SlackCloneTypography.subtitle1.copy(color = Color.White, fontWeight = FontWeight.Bold)
    )
  }
}

@Composable
fun CreateWorkspaceLargeScreenLayout(composeNavigator: ComposeNavigator, viewModel: WorkspaceCreateVM) {
  Row {
    Column(Modifier.weight(1f, fill = true)) {
      Heading()
      WorkspaceCreateForm(viewModel, composeNavigator)
    }
    Column(
      Modifier.weight(1f, fill = true).padding(24.dp).fillMaxHeight(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Image(
        modifier = Modifier.size(256.dp),
        painter = PainterRes.gettingStarted(),
        contentDescription = null,
        contentScale = ContentScale.Fit
      )
    }
  }

}

@Composable
fun CreateWorkspacePhoneLayout(composeNavigator: ComposeNavigator, viewModel: WorkspaceCreateVM) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Heading()
    Spacer(Modifier.size(8.dp))
    WorkspaceCreateForm(viewModel, composeNavigator)
  }
}
