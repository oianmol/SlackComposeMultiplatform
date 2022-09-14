package dev.baseio.slackclone.uichannels

import ViewModel
import dev.baseio.slackclone.chatcore.data.UiLayerChannels
import dev.baseio.slackdomain.mappers.UiModelMapper
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces
import dev.baseio.slackdomain.usecases.channels.UseCaseChannelRequest
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchChannels
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import kotlinx.coroutines.flow.*

class SlackChannelVM constructor(
  private val ucFetchChannels: UseCaseFetchChannels,
  private val chatPresentationMapper: UiModelMapper<DomainLayerChannels.SKChannel, UiLayerChannels.SKChannel>,
  private val useCaseGetSelectedWorkspace: UseCaseGetSelectedWorkspace
) : ViewModel() {

  val channels = MutableStateFlow<Flow<List<UiLayerChannels.SKChannel>>>(emptyFlow())

  fun allChannels() {
    channels.value = useCaseGetSelectedWorkspace.performStreaming(Unit).flatMapLatest {
      fetchChannels(it)
    }
  }

  private fun fetchChannels(it: DomainLayerWorkspaces.SKWorkspace?) =
    ucFetchChannels.performStreaming(it!!.uuid).map { channels ->
      domSlackToPresentation(channels)
    }

  fun loadDirectMessageChannels() {
    channels.value =
      useCaseGetSelectedWorkspace.performStreaming(Unit).flatMapLatest {
        fetchChannels(it)
      }
  }

  fun loadStarredChannels() {
    channels.value =
      useCaseGetSelectedWorkspace.performStreaming(Unit).flatMapLatest {
        fetchChannels(it)
      }

  }

  private fun domSlackToPresentation(channels: List<DomainLayerChannels.SKChannel>) =
    channels.map { channel ->
      chatPresentationMapper.mapToPresentation(channel)
    }

}