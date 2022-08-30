package dev.baseio.slackclone.uichat.newchat

import ViewModel
import dev.baseio.slackclone.chatcore.data.UiLayerChannels
import dev.baseio.slackclone.domain.mappers.UiModelMapper
import dev.baseio.slackclone.domain.model.channel.DomainLayerChannels
import dev.baseio.slackclone.domain.usecases.channels.UseCaseSearchChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class NewChatThreadVM  constructor(
  private val ucFetchChannels: UseCaseSearchChannel,
  private val chatPresentationMapper: UiModelMapper<DomainLayerChannels.SlackChannel, UiLayerChannels.SlackChannel>
) :
  ViewModel() {

  val search = MutableStateFlow("")
  var users = MutableStateFlow(flow(""))

  private fun flow(search: String) = ucFetchChannels.performStreaming(search).map { channels ->
    channels.map { channel ->
      chatPresentationMapper.mapToPresentation(channel)
    }
  }

  fun search(newValue: String) {
    search.value = newValue
    users.value = flow(newValue)
  }

  fun navigate(channel: UiLayerChannels.SlackChannel) {
    TODO("navigateBackWithResult SlackChannel")
   /* composeNavigator.navigateBackWithResult(
      NavigationKeys.navigateChannel,
      channel.uuid!!,
      SlackScreen.Dashboard.name
    )*/
  }


}