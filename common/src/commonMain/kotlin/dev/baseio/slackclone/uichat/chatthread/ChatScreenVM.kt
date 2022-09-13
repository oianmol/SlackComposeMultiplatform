package dev.baseio.slackclone.uichat.chatthread

import ViewModel

import dev.baseio.slackclone.chatcore.data.UiLayerChannels
import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.usecases.chat.UseCaseFetchMessages
import dev.baseio.slackdomain.usecases.chat.UseCaseSendMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class ChatScreenVM constructor(
  private val useCaseFetchMessages: UseCaseFetchMessages,
  private val useCaseSendMessage: UseCaseSendMessage
) : ViewModel(){
  var channel: UiLayerChannels.SKChannel? = null
  var chatMessagesFlow = MutableStateFlow<Flow<List<DomainLayerMessages.SKMessage>>>(emptyFlow())
  var message = MutableStateFlow("")
  var chatBoxState = MutableStateFlow(BoxState.Expanded)

  fun requestFetch(SKChannel: UiLayerChannels.SKChannel) {
    this.channel = SKChannel
    chatMessagesFlow.value = useCaseFetchMessages.performStreaming(SKChannel.uuid)
  }

  fun sendMessage(search: String) {
    if (search.isNotEmpty()) {
      viewModelScope.launch {
        val message = DomainLayerMessages.SKMessage(
          Clock.System.now().toEpochMilliseconds().toString(),
          channel!!.uuid,
          search,
          channel!!.uuid,
          "SlackUser",
          Clock.System.now().toEpochMilliseconds(),
          Clock.System.now().toEpochMilliseconds(),
        )
        useCaseSendMessage.perform(message)
      }
      message.value = ""
      chatBoxState.value = BoxState.Collapsed
    }
  }

  fun switchChatBoxState() {
    chatBoxState.value =
      if (chatBoxState.value == BoxState.Collapsed) BoxState.Expanded else BoxState.Collapsed

  }

}