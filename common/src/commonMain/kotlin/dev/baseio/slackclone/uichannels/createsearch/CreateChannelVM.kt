package dev.baseio.slackclone.uichannels.createsearch

import ViewModel
import dev.baseio.slackclone.chatcore.data.UiLayerChannels
import dev.baseio.slackclone.navigation.ComposeNavigator
import dev.baseio.slackclone.navigation.NavigationKey
import dev.baseio.slackclone.navigation.SlackScreens
import dev.baseio.slackdomain.domain.mappers.UiModelMapper
import dev.baseio.slackdomain.domain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.domain.usecases.channels.UseCaseCreateChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class CreateChannelVM constructor(
  private val useCaseCreateChannel: UseCaseCreateChannel,
  private val channelMapper: UiModelMapper<DomainLayerChannels.SlackChannel, UiLayerChannels.SlackChannel>
) :
  ViewModel() {

  var createChannelState =
    MutableStateFlow(DomainLayerChannels.SlackChannel(isOneToOne = false, avatarUrl = null, name = "***"))

  fun createChannel(composeNavigator: ComposeNavigator) {
    viewModelScope.launch {
      if (createChannelState.value.name?.isNotEmpty() == true) {
        val channel = useCaseCreateChannel.perform(createChannelState.value)
        composeNavigator.navigateUp()
        composeNavigator.deliverResult(
          NavigationKey.NavigateChannel,
          channelMapper.mapToPresentation(channel!!),
          SlackScreens.CreateChannelsScreen
        )
      }
    }
  }
}