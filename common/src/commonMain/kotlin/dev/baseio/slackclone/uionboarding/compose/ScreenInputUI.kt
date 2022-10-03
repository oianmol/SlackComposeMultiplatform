package dev.baseio.slackclone.uionboarding.compose

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
            WorkspaceFoundView(
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
        EmailInputVM.UiState.LoggedIn ->{

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
      when (uiState) {
        is WorkspaceInputVM.UiState.Empty -> WorkspaceInputView(modifier, workspaceInputVM)
        is WorkspaceInputVM.UiState.Loading -> LoadingColumn(modifier)

        is WorkspaceInputVM.UiState.Workspace -> {
          val uiState = uiState as WorkspaceInputVM.UiState.Workspace
          WorkspaceFoundView(
            modifier,
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
    "This is the address you use to sign in to Slack"
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
fun WorkspaceFoundView(
  modifier: Modifier,
  workspace: KMSKWorkspace,
  email: String?,
  password: String?,
  onEmailChange: (String) -> Unit,
  onPasswordChange: (String) -> Unit
) {
  Column(
    modifier.fillMaxSize().wrapContentWidth(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    WorkspaceListItem(workspace)
    Column(
      modifier = modifier
        .fillMaxWidth()
        .wrapContentWidth()
    ) {
      EmailHeading()
      EmailTF(modifier.fillMaxWidth(), email ?: "", onChange = {
        onEmailChange(it)
      })
      PasswordTF(modifier.fillMaxWidth(), password ?: "", onChange = {
        onPasswordChange(it)
      })
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
    Text("We suggest using the email address that you use at ${kmskWorkspace.domain}")
  }, text = {
    Text("First of all, enter your email address for ${kmskWorkspace.name}")
  })
}

