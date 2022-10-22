package dev.baseio.slackclone.uidashboard.home

import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import kotlinx.coroutines.flow.*
import com.arkivanov.decompose.ComponentContext
import dev.baseio.slackclone.koinApp
import dev.baseio.slackclone.uichannels.SlackChannelComponent

class HomeScreenComponent(
  componentContext: ComponentContext,
  private val useCaseGetSelectedWorkspace: UseCaseGetSelectedWorkspace,
) : ComponentContext by componentContext {

  var lastSelectedWorkspace = MutableStateFlow(flow())
    private set

  fun flow() = useCaseGetSelectedWorkspace.invokeFlow()

}