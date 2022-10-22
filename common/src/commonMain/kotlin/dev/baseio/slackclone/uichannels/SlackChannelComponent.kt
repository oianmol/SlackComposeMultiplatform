package dev.baseio.slackclone.uichannels

import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchRecentChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchAllChannels
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import kotlinx.coroutines.flow.*
import com.arkivanov.decompose.ComponentContext

class SlackChannelComponent constructor(
  componentContext: ComponentContext,
  private val ucFetchChannels: UseCaseFetchAllChannels,
  private val useCaseGetSelectedWorkspace: UseCaseGetSelectedWorkspace,
  private val ucFetchRecentChannels: UseCaseFetchRecentChannels
) : ComponentContext by componentContext {

  val channels = MutableStateFlow<Flow<List<DomainLayerChannels.SKChannel>>>(emptyFlow())

  fun allChannels() {
    channels.value = useCaseGetSelectedWorkspace.invokeFlow().flatMapLatest {
      ucFetchChannels(it!!.uuid)
    }
  }

  fun loadRecentChannels() {
    channels.value =
      useCaseGetSelectedWorkspace.invokeFlow().flatMapLatest {
        ucFetchRecentChannels(it!!.uuid)
      }
  }

}