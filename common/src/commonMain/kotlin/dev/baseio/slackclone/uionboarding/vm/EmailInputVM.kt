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
  val uiState = MutableStateFlow(UiState())

  private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
    uiState.value = uiState.value.copy(throwable = throwable)
  }

  fun onNextPressed() {
    if (uiState.value.workspaces != null) {
      val email = uiState.value.email
      val password = uiState.value.password
      uiState.value.selectedWorkspace?.let { workspace ->
        val workspaceId = workspace.uuid
        viewModelScope.launch(exceptionHandler) {
          uiState.value = uiState.value.copy(isLoading = true)
          loginUseCase.invoke(
            email ?: throw Exception("Email cannot be empty"),
            password ?: throw Exception("Password cannot be empty"),
            workspaceId
          )
          uiState.value = uiState.value.copy(isLoading = false, isLoggedIn = true)
        }
        return
      } ?: run {
        uiState.value =
          (uiState.value).copy(validationMessage = "Please select a workspace to proceed!")
        return
      }
    }
    findWorkspaces()
  }

  private fun findWorkspaces() {
    viewModelScope.launch(exceptionHandler) {
      uiState.value = uiState.value.copy(isLoading = true)
      val workspaces = findWorkspacesUseCase.byEmail(email.value)
      uiState.value =
        uiState.value.copy(isInitial = false, isLoading = false, workspaces = workspaces, email = email.value)
    }
  }

  fun switchLogin(kmskWorkspace: KMSKWorkspace) {
    uiState.value = (uiState.value).copy(selectedWorkspace = kmskWorkspace)
  }

  fun clearValidationMessage() {
    uiState.value = uiState.value.copy(validationMessage = null)
  }

  data class UiState(
    var isInitial: Boolean = true,
    var isLoading: Boolean? = null,
    var isLoggedIn: Boolean = false,
    var throwable: Throwable? = null,
    val workspaces: KMSKWorkspaces? = null,
    var selectedWorkspace: KMSKWorkspace? = null,
    var email: String? = null,
    var password: String? = null,
    var validationMessage: String? = null
  )
}