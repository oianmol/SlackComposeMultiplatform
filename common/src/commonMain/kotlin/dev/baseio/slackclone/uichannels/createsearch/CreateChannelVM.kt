package dev.baseio.slackclone.uichannels.createsearch

import ViewModel
import dev.baseio.slackclone.chatcore.data.UiLayerChannels
import dev.baseio.slackclone.domain.mappers.UiModelMapper
import dev.baseio.slackclone.domain.model.channel.DomainLayerChannels
import dev.baseio.slackclone.domain.usecases.channels.UseCaseCreateChannel
import dev.baseio.slackclone.navigation.ComposeNavigator
import dev.baseio.slackclone.navigation.NavigationKey
import dev.baseio.slackclone.navigation.SlackScreens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class CreateChannelVM constructor(
  private val useCaseCreateChannel: UseCaseCreateChannel,
  private val channelMapper: UiModelMapper<DomainLayerChannels.SlackChannel, UiLayerChannels.SlackChannel>
) :
  ViewModel() {

  var channel =
    MutableStateFlow(DomainLayerChannels.SlackChannel(isOneToOne = false, avatarUrl = null))

  fun createChannel(composeNavigator: ComposeNavigator) {
    viewModelScope.launch {
      if (channel.value.name?.isNotEmpty() == true) {
        val channel = useCaseCreateChannel.perform(channel.value)
        composeNavigator.deliverResult(
          NavigationKey.NavigateChannel,
          channelMapper.mapToPresentation(channel!!),
          SlackScreens.CreateChannelsScreen
        )
        composeNavigator.navigateUp()
      }
    }
  }
}