package dev.baseio.slackclone.uichannels.createsearch

import ViewModel
import dev.baseio.slackclone.domain.model.channel.DomainLayerChannels
import dev.baseio.slackclone.domain.usecases.channels.UseCaseCreateChannel
import dev.baseio.slackclone.navigation.ComposeNavigator
import dev.baseio.slackclone.navigation.NavigationKey
import dev.baseio.slackclone.navigation.SlackScreens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class CreateChannelVM constructor(
  private val useCaseCreateChannel: UseCaseCreateChannel
) :
  ViewModel() {

  var channel =
    MutableStateFlow(DomainLayerChannels.SlackChannel(isOneToOne = false, avatarUrl = null))

  fun createChannel(composeNavigator: ComposeNavigator) {
    viewModelScope.launch {
      if (channel.value.name?.isNotEmpty() == true) {
        val channel = useCaseCreateChannel.perform(channel.value)
        composeNavigator.navigateBackWithResult(
          NavigationKey.NavigateChannel,
          channel!!,
          SlackScreens.Dashboard
        )
      }

    }
  }
}