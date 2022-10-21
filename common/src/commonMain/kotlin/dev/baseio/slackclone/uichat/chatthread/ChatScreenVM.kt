package dev.baseio.slackclone.uichat.chatthread

import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.usecases.channels.UseCaseWorkspaceChannelRequest
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchAndSaveChannelMembers
import kotlinx.coroutines.launch
import ViewModel
import dev.baseio.slackdomain.datasources.local.messages.SKLocalDataSourceMessages
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import dev.baseio.slackdomain.usecases.channels.UseCaseGetChannelMembers
import dev.baseio.slackdomain.usecases.chat.UseCaseFetchAndSaveMessages
import dev.icerock.moko.paging.LambdaPagedListDataSource
import dev.icerock.moko.paging.Pagination
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*

class ChatScreenVM(
  private val useCaseFetchAndSaveChannelMembers: UseCaseFetchAndSaveChannelMembers,
  private val useCaseFetchAndSaveMessages: UseCaseFetchAndSaveMessages,
  private val useCaseChannelMembers: UseCaseGetChannelMembers,
  private val skLocalDataSourceMessages: SKLocalDataSourceMessages,
  private val sendMessageDelegate: SendMessageDelegate,
) : ViewModel(), SendMessageDelegate by sendMessageDelegate {
  lateinit var channelFlow: MutableStateFlow<DomainLayerChannels.SKChannel>

  val channelMembers = MutableStateFlow<List<DomainLayerUsers.SKUser>>(emptyList())
  var chatMessagesFlow = MutableStateFlow<List<DomainLayerMessages.SKMessage>>(emptyList())


  var showChannelDetails = MutableStateFlow(false)
    private set

  var chatBoxState = MutableStateFlow(BoxState.Collapsed)
    private set


  val exceptions = CoroutineExceptionHandler { coroutineContext, throwable ->
    throwable.printStackTrace()
  }

  var parentJob: Job? = null
  private var limit = 20

  var skMessagePagination: Pagination<DomainLayerMessages.SKMessage> = getPagination()


  fun requestFetch(channel: DomainLayerChannels.SKChannel) {
    channelFlow = MutableStateFlow(channel)
    sendMessageDelegate.channel = (channelFlow.value)
    skMessagePagination.refresh()

    parentJob?.cancel()
    parentJob = Job()
    channelMembers.value = emptyList()

    loadWorkspaceChannelData(channel)
  }

  private fun loadWorkspaceChannelData(channel: DomainLayerChannels.SKChannel) {
    with(UseCaseWorkspaceChannelRequest(channel.workspaceId, channel.channelId)) {
      fetchChannelMembers()
      refreshChannelMembers()
    }
  }

  fun sendMessageNow(message: String) {
    viewModelScope.launch {
      sendMessageDelegate.sendMessage(message.trim().trimEnd())
      chatBoxState.value = BoxState.Collapsed
      skMessagePagination.refresh()
    }
  }

  private fun UseCaseWorkspaceChannelRequest.refreshChannelMembers() =
    viewModelScope.launch(parentJob!! + exceptions) {
      useCaseFetchAndSaveChannelMembers.invoke(this@refreshChannelMembers)
    }

  private fun UseCaseWorkspaceChannelRequest.fetchChannelMembers() {
    viewModelScope.launch(parentJob!! + exceptions) {
      useCaseChannelMembers.invoke(this@fetchChannelMembers).collectLatest { skUserList ->
        channelMembers.value = skUserList
      }
    }
  }

  private fun getPagination() = Pagination(
    parentScope = viewModelScope,
    dataSource = LambdaPagedListDataSource { currentList ->
      //fetch and save messages in the range
      val request = UseCaseWorkspaceChannelRequest(
        channelFlow.value.workspaceId,
        (channelFlow.value.channelId),
        limit,
        currentList?.size ?: 0
      )
      viewModelScope.launch {
        useCaseFetchAndSaveMessages.invoke(
          request
        )
      }
      skLocalDataSourceMessages.getLocalMessages(
        request.workspaceId,
        request.channelId!!,
        request.limit,
        request.offset
      )
    },
    comparator = { a, b ->
      a.uuid.hashCode() - b.uuid.hashCode()
    },
    nextPageListener = { result: Result<List<DomainLayerMessages.SKMessage>> ->
      if (result.isSuccess) {
        chatMessagesFlow.value = result.getOrNull() ?: emptyList()
        println("Next page successful loaded")
      } else {
        println("Next page loading failed")
      }
    },
    refreshListener = { result: Result<List<DomainLayerMessages.SKMessage>> ->
      if (result.isSuccess) {
        println("Refresh successful")
        chatMessagesFlow.value = (result.getOrNull() ?: emptyList())
      } else {
        println("Refresh failed")
      }
    },
    initValue = emptyList()
  )


  fun switchChatBoxState() {
    chatBoxState.value = chatBoxState.value.toggle()
  }

  fun deleteMessage() {
    viewModelScope.launch {
      sendMessageDelegate.deleteMessageNow()
      skMessagePagination.refresh()
    }
  }

  fun clearLongClickMessageRequest() {
    deleteMessageRequest.value = null
  }

  fun onClickHash(hashTag: String) {

  }

  fun showChannelDetailsRequested() {
    showChannelDetails.value = !showChannelDetails.value
  }

}

private fun BoxState.toggle(): BoxState {
  return if (this == BoxState.Collapsed) BoxState.Expanded else BoxState.Collapsed
}
