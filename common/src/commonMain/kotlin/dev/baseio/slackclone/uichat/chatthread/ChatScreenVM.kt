package dev.baseio.slackclone.uichat.chatthread

import androidx.compose.ui.text.input.TextFieldValue

import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.usecases.channels.UseCaseWorkspaceChannelRequest
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchAndSaveChannelMembers
import dev.baseio.slackdomain.usecases.channels.UseCaseInviteUserToChannel
import dev.baseio.slackdomain.usecases.chat.UseCaseSendMessage
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import ViewModel
import dev.baseio.slackclone.commonui.reusable.MentionsPatterns
import dev.baseio.slackclone.commonui.reusable.SpanInfos
import dev.baseio.slackdata.SKKeyValueData
import dev.baseio.slackdata.datasources.local.channels.skUser
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import dev.baseio.slackdomain.usecases.channels.UseCaseGetChannelMembers
import dev.baseio.slackdomain.usecases.chat.UseCaseFetchAndSaveMessages
import dev.baseio.slackdomain.usecases.chat.UseCaseFetchMessages
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*

class ChatScreenVM constructor(
  private val useCaseFetchMessages: UseCaseFetchMessages,
  private val useCaseSendMessage: UseCaseSendMessage,
  private val skKeyValueData: SKKeyValueData,
  private val useCaseFetchAndSaveChannelMembers: UseCaseFetchAndSaveChannelMembers,
  private val useCaseFetchAndSaveMessages: UseCaseFetchAndSaveMessages,
  private val useCaseChannelMembers: UseCaseGetChannelMembers,
  private val useCaseInviteUserToChannel: UseCaseInviteUserToChannel
) : ViewModel() {
  val channelMembers = MutableStateFlow<List<DomainLayerUsers.SKUser>>(emptyList())
  lateinit var channelFlow: MutableStateFlow<DomainLayerChannels.SKChannel>
  var chatMessagesFlow = MutableStateFlow<List<DomainLayerMessages.SKMessage>>(emptyList())
  var message = MutableStateFlow(TextFieldValue())

  var spanInfoList = MutableStateFlow<List<SpanInfos>>(emptyList())

  var chatBoxState = MutableStateFlow(BoxState.Collapsed)
  var alertLongClickSkMessage = MutableStateFlow<DomainLayerMessages.SKMessage?>(null)
    private set

  val exceptions = CoroutineExceptionHandler { coroutineContext, throwable ->
    throwable.printStackTrace()
  }

  var parentJob: Job? = null

  fun requestFetch(channel: DomainLayerChannels.SKChannel) {
    parentJob?.cancel()
    parentJob = Job()
    channelFlow = MutableStateFlow(channel)
    with(UseCaseWorkspaceChannelRequest(channel.workspaceId, channel.channelId)) {
      viewModelScope.launch(parentJob!! + exceptions) {
        useCaseFetchMessages.invoke(this@with).collectLatest { skMessageList ->
          chatMessagesFlow.value = skMessageList
        }
      }
      viewModelScope.launch(parentJob!! + exceptions) {
        useCaseChannelMembers.invoke(this@with).collectLatest { skUserList ->
          channelMembers.value = skUserList
        }
      }
      viewModelScope.launch(parentJob!! + exceptions) {
        useCaseFetchAndSaveChannelMembers.invoke(this@with)
      }
      viewModelScope.launch(parentJob!! + exceptions) {
        useCaseFetchAndSaveMessages.invoke(this@with)
      }
    }
  }

  fun sendMessage(message: String) {
    if (message.isNotEmpty()) {
      val sortedList = spanInfoList.value.takeIf { it.size == 3 }?.sortedBy { it.start }
      sortedList?.firstOrNull()?.let {
        if (it.tag == MentionsPatterns.INVITE_TAG) {
          val user = sortedList[1].spanText.replace("@", "")
          val channel = sortedList[2].spanText.replace("#", "")
          viewModelScope.launch {
            val result = useCaseInviteUserToChannel(user, channel)
            this@ChatScreenVM.message.value = TextFieldValue("We just invited $user to $channel!")
            chatBoxState.value = BoxState.Collapsed
          }
          return // don't move ahead for sending the message
        }
      }

      viewModelScope.launch {
        useCaseSendMessage(
          DomainLayerMessages.SKMessage(
            uuid = Clock.System.now().toEpochMilliseconds().toString(),
            workspaceId = channelFlow.value.workspaceId,
            channelId = channelFlow.value.channelId,
            message = message,
            sender = skKeyValueData.skUser().uuid,
            createdDate = Clock.System.now().toEpochMilliseconds(),
            modifiedDate = Clock.System.now().toEpochMilliseconds(),
            isDeleted = false,
            isSynced = false
          )
        )
      }
      this.message.value = TextFieldValue()
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

  fun onClickHash(hashTag: String) {

  }

  fun setSpanInfo(spans: List<SpanInfos>) {
    spanInfoList.value = spans
  }

}

private fun BoxState.toggle(): BoxState {
  return if (this == BoxState.Collapsed) BoxState.Expanded else BoxState.Collapsed
}
