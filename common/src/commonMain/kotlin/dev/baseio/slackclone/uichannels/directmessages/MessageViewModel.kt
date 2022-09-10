package dev.baseio.slackclone.uichannels.directmessages

import ViewModel

import dev.baseio.slackclone.chatcore.data.UiLayerChannels
import dev.baseio.slackdomain.domain.mappers.UiModelMapper
import dev.baseio.slackdomain.domain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.domain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.domain.usecases.channels.UseCaseFetchChannelsWithLastMessage
import kotlinx.coroutines.flow.*

class MessageViewModel constructor(
  private val useCaseFetchChannels: UseCaseFetchChannelsWithLastMessage,
  private val channelPresentationMapper: UiModelMapper<DomainLayerChannels.SlackChannel, UiLayerChannels.SlackChannel>,
) : ViewModel() {


  val channels = MutableStateFlow(fetchFlow())

  fun refresh() {
    channels.value = useCaseFetchChannels.performStreaming(null)
  }

  fun fetchFlow(): Flow<List<DomainLayerMessages.LastMessage>> {
    return useCaseFetchChannels.performStreaming(null)
  }

  fun mapToUI(channel: DomainLayerChannels.SlackChannel): UiLayerChannels.SlackChannel {
    return channelPresentationMapper.mapToPresentation(channel)
  }

}