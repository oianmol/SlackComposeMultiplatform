package dev.baseio.slackclone.uichannels.createsearch

import ViewModel
import dev.baseio.slackclone.chatcore.data.UiLayerChannels
import dev.baseio.slackclone.navigation.ComposeNavigator
import dev.baseio.slackclone.navigation.NavigationKey
import dev.baseio.slackclone.navigation.SlackScreens
import dev.baseio.slackdomain.datasources.local.workspaces.SKDataSourceWorkspaces
import dev.baseio.slackdomain.mappers.UiModelMapper
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseCreateChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class CreateChannelVM constructor(
  private val useCaseCreateChannel: UseCaseCreateChannel,
  private val channelMapper: UiModelMapper<DomainLayerChannels.SKChannel, UiLayerChannels.SKChannel>
) :
  ViewModel() {

  var createChannelState =
    MutableStateFlow(
      DomainLayerChannels.SKChannel(
        isOneToOne = false,
        avatarUrl = null,
        name = "***",
        uuid = Clock.System.now().toEpochMilliseconds().toString(),
        workspaceId = TODO("take this workspace id from local datasource")
      )
    )

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