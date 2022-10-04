package dev.baseio.slackclone.uionboarding.vm

import ViewModel
import dev.baseio.slackdata.protos.KMSKWorkspace
import dev.baseio.slackdata.protos.KMSKWorkspaces
import dev.baseio.slackdomain.usecases.auth.LoginUseCase
import dev.baseio.slackdomain.usecases.workspaces.FindWorkspacesUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class EmailInputVM(
  private val loginUseCase: LoginUseCase,
  private val findWorkspacesUseCase: FindWorkspacesUseCase
) :
  ViewModel() {
  val email = MutableStateFlow("")
  val uiState = MutableStateFlow<UiState>(UiState.Empty)

  private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
    uiState.value = UiState.Exception(throwable)
  }


  fun onNextPressed() {
    if (uiState.value is UiState.Workspaces) {
      loginNow()
    }
    findWorkspaces()
  }

  private fun findWorkspaces() {
    viewModelScope.launch(exceptionHandler) {
      uiState.value = UiState.Loading
      val workspaces = findWorkspacesUseCase.invoke(email.value)
      uiState.value = UiState.Workspaces(workspaces)
    }
  }

  private inline fun loginNow() {
    val workspaceState = (uiState.value as UiState.Workspaces)
    val email = workspaceState.email
    val password = workspaceState.password
    workspaceState.selectedWorkspace?.let { workspace ->
      val workspaceId = workspace.uuid
      viewModelScope.launch(exceptionHandler) {
        uiState.value = UiState.Loading
        loginUseCase.invoke(
          email ?: throw Exception("Email cannot be empty"),
          password ?: throw Exception("Password cannot be empty"),
          workspaceId
        )
        uiState.value = UiState.LoggedIn
      }
      return
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