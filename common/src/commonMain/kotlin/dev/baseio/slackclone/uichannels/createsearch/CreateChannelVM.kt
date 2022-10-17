package dev.baseio.slackclone.uichannels.createsearch

import dev.baseio.slackclone.navigation.ComposeNavigator
import dev.baseio.slackclone.navigation.NavigationKey
import dev.baseio.slackclone.navigation.SlackScreens
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseCreateChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.datetime.Clock

class CreateChannelVM constructor(
  private val useCaseCreateChannel: UseCaseCreateChannel,
  private val useCaseGetSelectedWorkspace: UseCaseGetSelectedWorkspace,
) :
  ViewModel() {

  var createChannelState =
    MutableStateFlow(
      DomainLayerChannels.SKChannel.SkGroupChannel(
        avatarUrl = null,
        workId = "",
        uuid = Clock.System.now().toEpochMilliseconds().toString(),
        name = "",
        createdDate = Clock.System.now().toEpochMilliseconds(),
        modifiedDate = Clock.System.now().toEpochMilliseconds(),
        deleted = false
      )
    )

  fun createChannel(composeNavigator: ComposeNavigator) {
    viewModelScope.launch(CoroutineExceptionHandler { coroutineContext, throwable ->
      throwable.printStackTrace()
    }) {
      if (createChannelState.value.name?.isNotEmpty() == true) {
        val lastSelectedWorkspace = useCaseGetSelectedWorkspace()
        lastSelectedWorkspace?.let {
          createChannelState.value = createChannelState.value.copy(
            workId = lastSelectedWorkspace.uuid,
            uuid = "${createChannelState.value.name}_${lastSelectedWorkspace.uuid}"
          )
          val channel = useCaseCreateChannel(createChannelState.value).getOrThrow()
          composeNavigator.navigateUp()
          composeNavigator.deliverResult(
            NavigationKey.NavigateChannel,
            channel,
            SlackScreens.CreateChannelsScreen
          )
        }

      }
    }
  }
}