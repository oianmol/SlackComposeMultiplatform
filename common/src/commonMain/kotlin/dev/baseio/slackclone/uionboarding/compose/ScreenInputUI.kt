package dev.baseio.slackclone.uionboarding.compose

import PainterRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.commonui.reusable.SlackImageBox
import dev.baseio.slackclone.commonui.theme.SlackCloneColorProvider
import dev.baseio.slackclone.navigation.ComposeNavigator
import dev.baseio.slackclone.uionboarding.vm.EmailInputVM
import dev.baseio.slackclone.uionboarding.vm.WorkspaceInputVM
import dev.baseio.slackdata.protos.KMSKWorkspace

@Composable
fun EmailAddressInputUI(composeNavigator: ComposeNavigator, emailInputVM: EmailInputVM) {
  val colors = SlackCloneColorProvider.colors
  val uiState by emailInputVM.uiState.collectAsState()
  PlatformSideEffects.PlatformColors(colors.uiBackground, colors.uiBackground)
  LaunchedEffect(uiState) {
    if (uiState is EmailInputVM.UiState.LoggedIn) {
      navigateDashboard(composeNavigator)
    }
  }


  CommonInputUI(
    { modifier ->
      when (uiState) {
        EmailInputVM.UiState.Empty -> EmailInputView(modifier, emailInputVM)
        EmailInputVM.UiState.Loading -> LoadingColumn(modifier)
        is EmailInputVM.UiState.Workspaces -> {
          val workspacesState = (uiState as EmailInputVM.UiState.Workspaces)
          workspacesState.selectedWorkspace?.let { kmskWorkspace ->
            WorkspaceLoginView(
              modifier,
              kmskWorkspace,
              workspacesState.email,
              workspacesState.password,
              onEmailChange = {
                emailInputVM.uiState.value =
                  workspacesState.copy(email = it)
              }, onPasswordChange = {
                emailInputVM.uiState.value =
                  workspacesState.copy(password = it)
              }
            )
          } ?: run {
            WorkspacesList(
              modifier,
              workspacesState.workspaces.workspacesList,
            ) { kmskWorkspace ->
              emailInputVM.switchLogin(kmskWorkspace)
            }
          }
        }

        is EmailInputVM.UiState.Exception -> {
        }

        EmailInputVM.UiState.LoggedIn -> {

        }
      }

    },
    "We will send you an email that will instantly sign you in."
  ) {
    emailInputVM.onNextPressed()
  }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun WorkspacesList(
  modifier: Modifier,
  workspacesList: List<KMSKWorkspace>,
  workspaceSelected: (KMSKWorkspace) -> Unit
) {
  LazyColumn(modifier) {
    items(workspacesList) { kmskWorkspace ->
      ListItem(Modifier, icon = {
        SlackImageBox(Modifier, kmskWorkspace.picUrl)
      }, secondaryText = {
        Text(kmskWorkspace.domain)
      }, text = {
        Text(kmskWorkspace.name)
      }, trailing = {
        IconButton(onClick = {
          workspaceSelected(kmskWorkspace)
        }) {
          Icon(Icons.Default.ArrowForward, contentDescription = null)
        }
      })
    }
  }
}

@Composable
fun WorkspaceInputUI(composeNavigator: ComposeNavigator, workspaceInputVM: WorkspaceInputVM) {
  val colors = SlackCloneColorProvider.colors
  val uiState by workspaceInputVM.uiState.collectAsState()
  PlatformSideEffects.PlatformColors(colors.uiBackground, colors.uiBackground)

  LaunchedEffect(uiState) {
    if (uiState is WorkspaceInputVM.UiState.LoggedIn) {
      navigateDashboard(composeNavigator)
    }
  }

  CommonInputUI(
    { modifier ->
      val modifierLoginView = this@CommonInputUI.let{
        modifier.fillMaxWidth().align(Alignment.CenterHorizontally)
      }
      when (uiState) {
        is WorkspaceInputVM.UiState.Empty -> WorkspaceInputView(modifier, workspaceInputVM)
        is WorkspaceInputVM.UiState.Loading -> LoadingColumn(modifier)

        is WorkspaceInputVM.UiState.Workspace -> {
          val uiState = uiState as WorkspaceInputVM.UiState.Workspace
          WorkspaceLoginView(modifierLoginView,
            uiState.kmskWorkspace,
            uiState.email,
            uiState.password,
            onEmailChange = {
              workspaceInputVM.uiState.value =
                (workspaceInputVM.uiState.value as WorkspaceInputVM.UiState.Workspace).copy(email = it)
            }
          ) {
            workspaceInputVM.uiState.value =
              (workspaceInputVM.uiState.value as WorkspaceInputVM.UiState.Workspace).copy(password = it)
          }
        }

        is WorkspaceInputVM.UiState.Exception -> WorkspaceInputView(
          modifier,
          workspaceInputVM
        )

        WorkspaceInputVM.UiState.LoggedIn -> {

        }
      }
    },
    when (uiState) {
      is WorkspaceInputVM.UiState.Empty -> "This is the address you use to sign in to Slack"
      is WorkspaceInputVM.UiState.Exception -> (uiState as WorkspaceInputVM.UiState.Exception).throwable.message
        ?: "Some error occurred!"

      WorkspaceInputVM.UiState.Loading -> "Loading..."
      WorkspaceInputVM.UiState.LoggedIn -> "Logged In!"
      is WorkspaceInputVM.UiState.Workspace -> ""
    }
  ) {
    workspaceInputVM.onNextClick()
  }
}

@Composable
private fun LoadingColumn(modifier: Modifier) {
  Column(modifier) {
    CircularProgressIndicator(color = SlackCloneColorProvider.colors.textPrimary)
  }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WorkspaceLoginView(
  modifier: Modifier,
  workspace: KMSKWorkspace,
  email: String?,
  password: String?,
  onEmailChange: (String) -> Unit,
  onPasswordChange: (String) -> Unit
) {
  Column(
    modifier,
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Image(
      modifier = Modifier.size(128.dp).padding(8.dp),
      painter = PainterRes.slackLogo(),
      contentDescription = null
    )
    Card(
      elevation = 4.dp,
      backgroundColor = SlackCloneColorProvider.colors.uiBackground
    ) {
      Column(
        modifier = Modifier.padding(8.dp)
      ) {
        Text("First of all, enter your email address for ${workspace.name}", modifier = Modifier.padding(4.dp))
        Text(
          "We suggest using the email address that you use at ${workspace.domain}",
          modifier = Modifier.padding(4.dp)
        )
        EmailHeading()
        EmailTF(modifier, email ?: "", onChange = {
          onEmailChange(it)
        })
        PasswordHeading()
        PasswordTF(modifier, password ?: "", onChange = {
          onPasswordChange(it)
        })
      }
    }
  }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun WorkspaceListItem(
  kmskWorkspace: KMSKWorkspace,
) {
  ListItem(Modifier, icon = {
    SlackImageBox(Modifier, kmskWorkspace.picUrl)
  }, secondaryText = {
  }, text = {
  })
}

