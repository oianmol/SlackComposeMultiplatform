package dev.baseio.slackclone.uionboarding.vm

import ViewModel
import dev.baseio.grpc.findWorkspacesForEmail
import dev.baseio.grpc.login
import dev.baseio.slackdata.protos.KMSKWorkspace
import dev.baseio.slackdata.protos.KMSKWorkspaces
import dev.baseio.slackdata.protos.kmSKAuthUser
import dev.baseio.slackdata.protos.kmSKUser
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class EmailInputVM : ViewModel() {
  val email = MutableStateFlow("")

  private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
    uiState.value = UiState.Exception(throwable)
  }

  val uiState = MutableStateFlow<UiState>(UiState.Empty)

  fun onNextPressed() {
    if (uiState.value is UiState.Workspaces) {
      (uiState.value as UiState.Workspaces).selectedWorkspace?.let { workspace ->
        viewModelScope.launch(exceptionHandler) {
          login(kmSKAuthUser {
            this.email = (uiState.value as UiState.Workspaces).email
            this.password = (uiState.value as UiState.Workspaces).password
            this.user = kmSKUser {
              workspaceId = workspace.uuid
            }
          })
          // TODO save the token to local prefs
          uiState.value = EmailInputVM.UiState.LoggedIn
        }
        return
      }
    }
    viewModelScope.launch(exceptionHandler) {
      uiState.value = UiState.Loading
      val workspaces = findWorkspacesForEmail(email.value)
      uiState.value = UiState.Workspaces(workspaces)
    }
  }

  fun switchLogin(kmskWorkspace: KMSKWorkspace) {
    uiState.value = (uiState.value as UiState.Workspaces).copy(selectedWorkspace = kmskWorkspace)
  }

  sealed class UiState {
    object Empty : UiState()
    object Loading : UiState()
    data class Exception(val throwable: Throwable) : UiState()
    object LoggedIn : UiState()
    data class Workspaces(
      val workspaces: KMSKWorkspaces,
      var selectedWorkspace: KMSKWorkspace? = null, var email: String? = null, var password: String? = null
    ) : UiState()
  }
}