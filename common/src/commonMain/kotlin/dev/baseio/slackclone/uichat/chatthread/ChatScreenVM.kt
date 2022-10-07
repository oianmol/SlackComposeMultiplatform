package dev.baseio.slackclone.uichat.chatthread

import ViewModel
import androidx.compose.ui.text.input.TextFieldValue

import dev.baseio.slackclone.chatcore.data.UiLayerChannels
import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.usecases.channels.UseCaseChannelRequest
import dev.baseio.slackdomain.usecases.chat.UseCaseFetchMessages
import dev.baseio.slackdomain.usecases.chat.UseCaseSendMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import SKKeyValueData
import dev.baseio.slackdomain.LOGGED_IN_USER
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ChatScreenVM constructor(
  private val useCaseFetchMessages: UseCaseFetchMessages,
  private val useCaseSendMessage: UseCaseSendMessage,
  private val skKeyValueData: SKKeyValueData
) : ViewModel() {
  var channel: UiLayerChannels.SKChannel? = null
  var chatMessagesFlow = MutableStateFlow<Flow<List<DomainLayerMessages.SKMessage>>>(emptyFlow())
  var message = MutableStateFlow(TextFieldValue())
  var chatBoxState = MutableStateFlow(BoxState.Collapsed)

  fun requestFetch(SKChannel: UiLayerChannels.SKChannel) {
    this.channel = SKChannel
    chatMessagesFlow.value =
      useCaseFetchMessages.performStreaming(UseCaseChannelRequest(SKChannel.workspaceId, SKChannel.uuid))
  }

  fun sendMessage(search: String) {
    if (search.isNotEmpty()) {
      viewModelScope.launch {
        val user = Json.decodeFromString<DomainLayerUsers.SKUser>(skKeyValueData.get(LOGGED_IN_USER)!!)
        val message = DomainLayerMessages.SKMessage(
          Clock.System.now().toEpochMilliseconds().toString(),
          channel!!.workspaceId,
          channel!!.uuid,
          search,
          channel!!.uuid,
          user.uuid,
          Clock.System.now().toEpochMilliseconds(),
          Clock.System.now().toEpochMilliseconds(),
          user.name
        )
        useCaseSendMessage.perform(message)
      }
      message.value = TextFieldValue()
      chatBoxState.value = BoxState.Collapsed
    }
  }

  fun switchChatBoxState() {
    chatBoxState.value = chatBoxState.value.toggle()
  }

}

private fun BoxState.toggle(): BoxState {
  return if (this == BoxState.Collapsed) BoxState.Expanded else BoxState.Collapsed
}
