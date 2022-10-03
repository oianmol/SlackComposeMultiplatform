package dev.baseio.slackclone.uionboarding.vm

import ViewModel
import dev.baseio.grpc.findWorkspaceByName
import dev.baseio.grpc.login
import dev.baseio.slackdata.protos.KMSKWorkspace
import dev.baseio.slackdata.protos.kmSKAuthUser
import dev.baseio.slackdata.protos.kmSKUser
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class WorkspaceInputVM : ViewModel() {
  val uiState = MutableStateFlow<UiState>(UiState.Empty)
  var workspace = MutableStateFlow("")

  fun onNextClick() {
    if (uiState.value is UiState.Workspace) {
      viewModelScope.launch(CoroutineExceptionHandler { coroutineContext, throwable ->
        uiState.value = UiState.Exception(throwable)
      }) {
        val email = (uiState.value as UiState.Workspace).email
        val password = (uiState.value as UiState.Workspace).password
        val workspaceIdd = (uiState.value as UiState.Workspace).kmskWorkspace.uuid
        uiState.value = UiState.Loading
        val onLogin = login(kmSKAuthUser {
          this.email = email
          this.password = password
          this.user = kmSKUser {
            workspaceId = workspaceIdd
          }
        })
        // TODO save the token to local prefs
        uiState.value = UiState.LoggedIn
      }
    } else {
      viewModelScope.launch(CoroutineExceptionHandler { coroutineContext, throwable ->
        uiState.value = UiState.Exception(throwable)
      }) {
        uiState.value = UiState.Loading
        val name = findWorkspaceByName(workspace.value)
        uiState.value = UiState.Workspace(name)
      }
    }
  }

  sealed class UiState {
    object Empty : UiState()
    object Loading : UiState()
    data class Workspace(val kmskWorkspace: KMSKWorkspace, var email: String? = null, var password: String? = null) :
      UiState()

    object LoggedIn : UiState()
    data class Exception(val throwable: Throwable) : UiState()
  }
}