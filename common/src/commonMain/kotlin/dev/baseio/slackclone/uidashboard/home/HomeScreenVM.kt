package dev.baseio.slackclone.uidashboard.home

import ViewModel
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchChannels
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import kotlinx.coroutines.flow.MutableStateFlow

class HomeScreenVM(private val useCaseGetSelectedWorkspace: UseCaseGetSelectedWorkspace,
                   private val useCaseFetchChannels: UseCaseFetchChannels) : ViewModel() {
  var lastSelectedWorkspace = MutableStateFlow(flow())
    private set

  fun flow() = useCaseGetSelectedWorkspace.performStreaming(Unit)
}