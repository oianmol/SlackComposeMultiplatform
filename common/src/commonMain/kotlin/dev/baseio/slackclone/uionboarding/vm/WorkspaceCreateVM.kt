package dev.baseio.slackclone.uionboarding.vm

import ViewModel
import dev.baseio.slackdomain.usecases.workspaces.UseCaseCreateWorkspace
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class WorkspaceCreateVM(private val useCaseCreateWorkspace: UseCaseCreateWorkspace) : ViewModel() {
  val emailForm = MutableStateFlow("")
  val nameForm = MutableStateFlow("")

  fun createWorkspace() {
    viewModelScope.launch {
      useCaseCreateWorkspace(emailForm.value, nameForm.value)
    }
  }
}