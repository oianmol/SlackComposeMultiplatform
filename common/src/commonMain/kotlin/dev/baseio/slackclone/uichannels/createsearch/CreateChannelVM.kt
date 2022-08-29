package dev.baseio.slackclone.uichannels.createsearch

import ViewModel
import dev.baseio.slackclone.domain.model.channel.DomainLayerChannels
import dev.baseio.slackclone.domain.usecases.channels.UseCaseCreateChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class CreateChannelVM constructor(
  private val useCaseCreateChannel: UseCaseCreateChannel
) :
  ViewModel() {

  var channel =
    MutableStateFlow(DomainLayerChannels.SlackChannel(isOneToOne = false, avatarUrl = null))

  fun createChannel() {
    viewModelScope.launch {
      if (channel.value.name?.isNotEmpty() == true) {
        val channel = useCaseCreateChannel.perform(channel.value)
        TODO("navigateBackWithResult")
        /*composeNavigator.navigateBackWithResult(
          NavigationKeys.navigateChannel,
          channel?.uuid!!,
          SlackScreen.Dashboard.name
        )*/
      }

    }
  }
}