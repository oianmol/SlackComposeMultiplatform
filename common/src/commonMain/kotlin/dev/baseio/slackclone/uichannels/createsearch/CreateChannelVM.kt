package dev.baseio.slackclone.uichannels.createsearch

import dev.baseio.slackclone.chatcore.data.UiLayerChannels
import dev.baseio.slackclone.navigation.ComposeNavigator
import dev.baseio.slackclone.navigation.NavigationKey
import dev.baseio.slackclone.navigation.SlackScreens
import dev.baseio.slackdomain.mappers.UiModelMapper
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseCreateChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import ViewModel
class CreateChannelVM constructor(
  private val useCaseCreateChannel: UseCaseCreateChannel,
  private val useCaseGetSelectedWorkspace: UseCaseGetSelectedWorkspace,
  private val channelMapper: UiModelMapper<DomainLayerChannels.SKChannel, UiLayerChannels.SKChannel>
) :
  ViewModel() {

  var createChannelState =
    MutableStateFlow(
      DomainLayerChannels.SKChannel(
        isOneToOne = false,
        avatarUrl = null,
        workspaceId = ""
      )
    )

  fun createChannel(composeNavigator: ComposeNavigator) {
    viewModelScope.launch {
      if (createChannelState.value.name?.isNotEmpty() == true) {
        val lastSelectedWorkspace = useCaseGetSelectedWorkspace.perform()
        lastSelectedWorkspace?.let {
          createChannelState.value = createChannelState.value.copy(
            workspaceId = lastSelectedWorkspace.uuid,
            uuid = "${createChannelState.value.name}_${lastSelectedWorkspace.uuid}"
          )
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
}