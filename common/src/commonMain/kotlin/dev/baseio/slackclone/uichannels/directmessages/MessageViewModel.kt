package dev.baseio.slackclone.uichannels.directmessages

import ViewModel

import dev.baseio.slackclone.chatcore.data.UiLayerChannels
import dev.baseio.slackdomain.mappers.UiModelMapper
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchChannelsWithLastMessage
import kotlinx.coroutines.flow.*

class MessageViewModel constructor(
  private val useCaseFetchChannels: UseCaseFetchChannelsWithLastMessage,
  private val channelPresentationMapper: UiModelMapper<DomainLayerChannels.SKChannel, UiLayerChannels.SKChannel>,
) : ViewModel() {


  val channels = MutableStateFlow(fetchFlow())

  fun refresh() {
    channels.value = useCaseFetchChannels.performStreamingNullable(null)
  }

  fun fetchFlow(): Flow<List<DomainLayerMessages.SKLastMessage>> {
    return useCaseFetchChannels.performStreamingNullable(null)
  }

  fun mapToUI(channel: DomainLayerChannels.SKChannel): UiLayerChannels.SKChannel {
    return channelPresentationMapper.mapToPresentation(channel)
  }

}