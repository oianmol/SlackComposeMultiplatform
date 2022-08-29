package dev.baseio.slackclone.uichat.chatthread

import ViewModel
import androidx.paging.PagingData
import dev.baseio.slackclone.domain.model.message.DomainLayerMessages
import dev.baseio.slackclone.domain.usecases.chat.UseCaseFetchMessages
import dev.baseio.slackclone.chatcore.data.UiLayerChannels
import dev.baseio.slackclone.domain.usecases.chat.UseCaseSendMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.*

class ChatScreenVM constructor(
  private val useCaseFetchMessages: UseCaseFetchMessages,
  private val useCaseSendMessage: UseCaseSendMessage
) : ViewModel() {
  var channel: UiLayerChannels.SlackChannel? = null
  var chatMessagesFlow = MutableStateFlow<Flow<PagingData<DomainLayerMessages.SlackMessage>>?>(null)
  var message = MutableStateFlow("")
  var chatBoxState = MutableStateFlow(BoxState.Collapsed)

  fun requestFetch(slackChannel: UiLayerChannels.SlackChannel) {
    this.channel = slackChannel
    chatMessagesFlow.value = useCaseFetchMessages.performStreaming(slackChannel.uuid)
  }

  fun sendMessage(search: String) {
    if (search.isNotEmpty() && channel?.uuid != null) {
      viewModelScope.launch {
        val message = DomainLayerMessages.SlackMessage(
          UUID.randomUUID().toString(),
          channel!!.uuid!!,
          search,
          UUID.randomUUID().toString(),
          "Anmol Verma",
          System.currentTimeMillis(),
          System.currentTimeMillis(),
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