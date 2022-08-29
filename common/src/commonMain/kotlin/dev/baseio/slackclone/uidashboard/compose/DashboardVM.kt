package dev.baseio.slackclone.uidashboard.compose

import ViewModel
import dev.baseio.slackclone.chatcore.data.UiLayerChannels
import dev.baseio.slackclone.domain.mappers.UiModelMapper
import dev.baseio.slackclone.domain.model.channel.DomainLayerChannels
import dev.baseio.slackclone.domain.usecases.channels.UseCaseCreateChannels
import dev.baseio.slackclone.domain.usecases.channels.UseCaseFetchUsers
import dev.baseio.slackclone.domain.usecases.channels.UseCaseGetChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DashboardVM  constructor(
  private val useCaseGetChannel: UseCaseGetChannel,
  private val useCaseFetchUsers: UseCaseFetchUsers,
  private val useCaseSaveChannel: UseCaseCreateChannels,
  private val channelMapper: UiModelMapper<DomainLayerChannels.SlackChannel, UiLayerChannels.SlackChannel>
) : ViewModel() {

  val selectedChatChannel = MutableStateFlow<UiLayerChannels.SlackChannel?>(null)
  val isChatViewClosed = MutableStateFlow(true)

  init {
    observeChannelCreated()
    preloadUsers()
  }

  private fun observeChannelCreated() {
    //TODO Discover how compose navigator can observeResult ?
    /*composeNavigator.observeResult<String>(
      NavigationKeys.navigateChannel,
    ).onStart {
      val message = savedStateHandle.get<String>(NavigationKeys.navigateChannel)
      message?.let {
        emit(it)
      }
    }.map {
      useCaseGetChannel.perform(it)
    }.onEach { slackChannel ->
      navigateChatThreadForChannel(slackChannel)
    }
      .launchIn(viewModelScope)

    selectedChatChannel.onEach {
      savedStateHandle.set(NavigationKeys.navigateChannel, it?.uuid)
    }.launchIn(viewModelScope)*/
  }

  private fun navigateChatThreadForChannel(slackChannel: DomainLayerChannels.SlackChannel?) {
    slackChannel?.let {
      selectedChatChannel.value = channelMapper.mapToPresentation(it)
      isChatViewClosed.value = false
    }
  }

  private fun preloadUsers() {
    viewModelScope.launch {
      val users = useCaseFetchUsers.perform(10)
      useCaseSaveChannel.perform(users)
    }
  }

}