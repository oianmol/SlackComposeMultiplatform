package dev.baseio.slackclone.uidashboard.home

import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import kotlinx.coroutines.flow.*
import ViewModel

class HomeScreenVM(
  private val useCaseGetSelectedWorkspace: UseCaseGetSelectedWorkspace,
) : ViewModel() {
  var lastSelectedWorkspace = MutableStateFlow(flow())
    private set

  fun flow() = useCaseGetSelectedWorkspace.invokeFlow()

}