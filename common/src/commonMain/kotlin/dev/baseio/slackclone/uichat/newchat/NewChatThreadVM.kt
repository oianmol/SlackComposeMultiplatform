package dev.baseio.slackclone.uichat.newchat

import ViewModel
import dev.baseio.slackclone.chatcore.data.UiLayerChannels
import dev.baseio.slackclone.navigation.ComposeNavigator
import dev.baseio.slackclone.navigation.NavigationKey
import dev.baseio.slackclone.navigation.SlackScreens
import dev.baseio.slackdomain.mappers.UiModelMapper
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseSearchChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map

class NewChatThreadVM constructor(
  private val ucFetchChannels: UseCaseSearchChannel,
  private val chatPresentationMapper: UiModelMapper<DomainLayerChannels.SKChannel, UiLayerChannels.SKChannel>
) :
  ViewModel() {

  val search = MutableStateFlow("")
  var users = MutableStateFlow(flow(""))

  private fun flow(search: String) = ucFetchChannels.performStreamingNullable(search).map { channels ->
    channels.map { channel ->
      chatPresentationMapper.mapToPresentation(channel)
    }
  }?: emptyFlow()

  fun search(newValue: String) {
    search.value = newValue
    users.value = flow(newValue)
  }

  fun navigate(channel: UiLayerChannels.SKChannel, composeNavigator: ComposeNavigator) {
     composeNavigator.deliverResult(
       NavigationKey.NavigateChannel,
       channel,
       SlackScreens.Dashboard
     )
  }


}