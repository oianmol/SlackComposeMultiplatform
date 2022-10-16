package dev.baseio.slackclone.uichat.chatthread

import androidx.compose.ui.text.input.TextFieldValue

import dev.baseio.slackclone.chatcore.data.UiLayerChannels
import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.usecases.channels.UseCaseChannelRequest
import dev.baseio.slackdomain.usecases.chat.UseCaseSendMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import dev.baseio.slackdomain.LOGGED_IN_USER
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import ViewModel
import dev.baseio.slackdata.SKKeyValueData
import dev.baseio.slackdomain.usecases.chat.UseCaseFetchAndUpdateChangeInMessages
import dev.baseio.slackdomain.usecases.chat.UseCaseFetchMessages
import kotlinx.coroutines.flow.launchIn

class ChatScreenVM constructor(
  private val useCaseObserveMessages: UseCaseFetchAndUpdateChangeInMessages,
  private val useCaseFetchMessages: UseCaseFetchMessages,
  private val useCaseSendMessage: UseCaseSendMessage,
  private val skKeyValueData: SKKeyValueData
) : ViewModel() {
  lateinit var channel: UiLayerChannels.SKChannel
  var chatMessagesFlow = MutableStateFlow<Flow<List<DomainLayerMessages.SKMessage>>>(emptyFlow())
  var message = MutableStateFlow(TextFieldValue())
  var chatBoxState = MutableStateFlow(BoxState.Collapsed)
  var alertLongClickSkMessage = MutableStateFlow<DomainLayerMessages.SKMessage?>(null)
    private set

  fun requestFetch(SKChannel: UiLayerChannels.SKChannel) {
    this.channel = SKChannel
    useCaseObserveMessages.invoke(UseCaseChannelRequest(workspaceId = channel.workspaceId, channel.uuid))
      .launchIn(viewModelScope)
    chatMessagesFlow.value =
      useCaseFetchMessages.invoke(UseCaseChannelRequest(SKChannel.workspaceId, SKChannel.uuid))
  }

  fun sendMessage(search: String) {
    if (search.isNotEmpty()) {
      viewModelScope.launch {
        val user = Json.decodeFromString<DomainLayerUsers.SKUser>(skKeyValueData.get(LOGGED_IN_USER)!!)
        val message = DomainLayerMessages.SKMessage(
          Clock.System.now().toEpochMilliseconds().toString(),
          channel.workspaceId,
          channel.uuid,
          search,
          channel.uuid,
          user.uuid,
          Clock.System.now().toEpochMilliseconds(),
          Clock.System.now().toEpochMilliseconds(),
          isDeleted = false,
          isSynced = false
        )
        useCaseSendMessage(message)
      }
      message.value = TextFieldValue()
      chatBoxState.value = BoxState.Collapsed
    }
  }

  fun switchChatBoxState() {
    chatBoxState.value = chatBoxState.value.toggle()
  }

  fun alertLongClick(skMessage: DomainLayerMessages.SKMessage) {
    alertLongClickSkMessage.value = skMessage
  }

  fun deleteMessage() {
    viewModelScope.launch {
      alertLongClickSkMessage.value?.copy(isDeleted = true)?.let { useCaseSendMessage(it) }
      alertLongClickSkMessage.value = null
    }
  }

  fun clearLongClickMessageRequest() {
    alertLongClickSkMessage.value = null
  }

}

private fun BoxState.toggle(): BoxState {
  return if (this == BoxState.Collapsed) BoxState.Expanded else BoxState.Collapsed
}
