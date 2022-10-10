package dev.baseio.slackclone.uionboarding.compose

import PainterRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.commonui.reusable.SlackImageBox
import dev.baseio.slackclone.commonui.theme.SlackCloneColorProvider
import dev.baseio.slackclone.commonui.theme.SlackCloneTypography
import dev.baseio.slackclone.navigation.ComposeNavigator
import dev.baseio.slackclone.uionboarding.vm.EmailInputVM
import dev.baseio.slackclone.uionboarding.vm.WorkspaceInputVM
import dev.baseio.slackdata.protos.KMSKWorkspace
import kotlinx.coroutines.delay

@Composable
fun EmailAddressInputUI(composeNavigator: ComposeNavigator, emailInputVM: EmailInputVM) {
  val colors = SlackCloneColorProvider.colors
  val uiState by emailInputVM.uiState.collectAsState()

  PlatformSideEffects.PlatformColors(colors.uiBackground, colors.uiBackground)
  LaunchedEffect(uiState) {
    when {
      uiState.isLoggedIn -> {
        navigateDashboard(composeNavigator)
      }

      uiState.workspaces != null -> {
        uiState.validationMessage?.let {
          delay(1500)
          emailInputVM.clearValidationMessage()
        }
      }
    }
  }


  CommonInputUI(
    { modifier ->
      when {
        uiState.isInitial -> EmailInputView(modifier, emailInputVM)
        uiState.isLoading == true -> LoadingColumn(modifier)
        uiState.workspaces != null -> {
          uiState.selectedWorkspace?.let { kmskWorkspace ->
            WorkspaceLoginView(
              modifier,
              kmskWorkspace,
              uiState.email,
              uiState.password,
              onEmailChange = {
                emailInputVM.uiState.value =
                  emailInputVM.uiState.value.copy(email = it)
              }, onPasswordChange = {
                emailInputVM.uiState.value =
                  emailInputVM.uiState.value.copy(password = it)
              }
            )
          } ?: run {
            Column {
              ErrorMessageBox(uiState.validationMessage)
              WorkspacesList(
                modifier,
                uiState.workspaces!!.workspacesList,
              ) { kmskWorkspace ->
                emailInputVM.switchLogin(kmskWorkspace)
              }
            }
          }
        }
      }

    },
    when {
      uiState.isLoading == true -> "Fetching your workspaces..."
      uiState.isInitial -> "We will find the workspaces that you are signed up for.\nFirst, enter your email\nWe suggest using the email address you use at work."
      uiState.throwable != null -> uiState.throwable!!.message
        ?: "An Unknown error has occurred..."

      uiState.isLoggedIn -> "Woo hoo! You are logged in!"
      uiState.workspaces != null -> when {
        uiState.workspaces?.workspacesList?.isEmpty() == true -> "No workspace(s) found..."
        uiState.selectedWorkspace == null -> "Select a workspace to proceed!"
        else -> ""
      }

      else -> "Unknown state!"
    }
  ) {
    emailInputVM.onNextPressed()
  }
}

@Composable
fun ErrorMessageBox(showErrorMessage: String?) {
  showErrorMessage.takeIf { !it.isNullOrEmpty() }?.let { it ->
    Box(
      Modifier.background(
        color = SlackCloneColorProvider.colors.lineColor.copy(0.4f),
        shape = RoundedCornerShape(8.dp)
      )
        .padding(4.dp)
    ) {
      Text(it, style = SlackCloneTypography.h5.copy(color = Color.Red), modifier = Modifier.padding(8.dp))
    }
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
      val modifierLoginView = this@CommonInputUI.let {
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
    modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Image(
      modifier = Modifier.size(128.dp).padding(8.dp),
      painter = PainterRes.slackLogo(),
      contentDescription = null
    )
    Text(
      "First of all, enter your email address for ${workspace.name}",
      modifier = Modifier.padding(4.dp),
      style = SlackCloneTypography.h5.copy(color = SlackCloneColorProvider.colors.textPrimary)
    )
    Text(
      "We suggest using the email address that you use at ${workspace.domain}",
      modifier = Modifier.padding(4.dp),
      style = SlackCloneTypography.h6.copy(color = SlackCloneColorProvider.colors.textSecondary)
    )

    Card(backgroundColor = SlackCloneColorProvider.colors.uiBackground, modifier = Modifier.padding(8.dp)) {
      Column {
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