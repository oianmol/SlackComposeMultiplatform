package dev.baseio.slackclone.uichannels

import ViewModel
import dev.baseio.slackclone.chatcore.data.UiLayerChannels
import dev.baseio.slackdomain.mappers.UiModelMapper
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchChannels
import kotlinx.coroutines.flow.*

class SlackChannelVM constructor(
  private val ucFetchChannels: UseCaseFetchChannels,
  private val chatPresentationMapper: UiModelMapper<DomainLayerChannels.SKChannel, UiLayerChannels.SKChannel>
) : ViewModel() {

  val channels = MutableStateFlow<Flow<List<UiLayerChannels.SKChannel>>>(emptyFlow())

  fun allChannels() {
    channels.value = ucFetchChannels.performStreamingNullable(null).map { channels ->
      domSlackToPresentation(channels)
    }
  }

  fun loadDirectMessageChannels() {
    channels.value = ucFetchChannels.performStreamingNullable(null).map { channels ->
      domSlackToPresentation(channels,)
    }
  }

  fun loadStarredChannels() {
    channels.value = ucFetchChannels.performStreamingNullable(null).map { channels ->
      domSlackToPresentation(channels)
    }
  }

  private fun domSlackToPresentation(channels: List<DomainLayerChannels.SKChannel>) =
    channels.map { channel ->
      chatPresentationMapper.mapToPresentation(channel)
    }

}