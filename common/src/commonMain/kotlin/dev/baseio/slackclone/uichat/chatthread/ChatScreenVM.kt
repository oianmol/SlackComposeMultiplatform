package dev.baseio.slackclone.uichat.chatthread

import androidx.compose.ui.text.input.TextFieldValue

import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.usecases.channels.UseCaseWorkspaceChannelRequest
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchAndSaveChannelMembers
import dev.baseio.slackdomain.usecases.chat.UseCaseSendMessage
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import ViewModel
import dev.baseio.slackdata.SKKeyValueData
import dev.baseio.slackdata.datasources.local.channels.skUser
import dev.baseio.slackdomain.usecases.chat.UseCaseFetchAndSaveMessages
import dev.baseio.slackdomain.usecases.chat.UseCaseFetchMessages
import kotlinx.coroutines.flow.*

class ChatScreenVM constructor(
  private val useCaseFetchMessages: UseCaseFetchMessages,
  private val useCaseSendMessage: UseCaseSendMessage,
  private val skKeyValueData: SKKeyValueData,
  private val useCaseFetchAndSaveChannelMembers: UseCaseFetchAndSaveChannelMembers,
  private val useCaseFetchAndSaveMessages: UseCaseFetchAndSaveMessages
) : ViewModel() {
  lateinit var channelFlow: MutableStateFlow<DomainLayerChannels.SKChannel>
  var chatMessagesFlow = MutableStateFlow<Flow<List<DomainLayerMessages.SKMessage>>>(emptyFlow())
  var message = MutableStateFlow(TextFieldValue())
  var chatBoxState = MutableStateFlow(BoxState.Collapsed)
  var alertLongClickSkMessage = MutableStateFlow<DomainLayerMessages.SKMessage?>(null)
    private set

  fun requestFetch(channel: DomainLayerChannels.SKChannel) {
    channelFlow = MutableStateFlow(channel)
    with(UseCaseWorkspaceChannelRequest(channel.workspaceId, channel.channelId)) {
      chatMessagesFlow.value = useCaseFetchMessages.invoke(this)
      viewModelScope.launch {
        useCaseFetchAndSaveChannelMembers.invoke(this@with)
      }
      viewModelScope.launch {
        useCaseFetchAndSaveMessages.invoke(this@with)
      }
    }
  }

  fun sendMessage(search: String) {
    if (search.isNotEmpty()) {
      viewModelScope.launch {
        val user = skKeyValueData.skUser()
        val message = DomainLayerMessages.SKMessage(
          uuid = Clock.System.now().toEpochMilliseconds().toString(),
          workspaceId = channelFlow.value.workspaceId,
          channelId = channelFlow.value.channelId,
          message = search,
          sender = user.uuid,
          createdDate = Clock.System.now().toEpochMilliseconds(),
          modifiedDate = Clock.System.now().toEpochMilliseconds(),
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
