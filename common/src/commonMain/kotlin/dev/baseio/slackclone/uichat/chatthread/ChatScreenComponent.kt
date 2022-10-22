package dev.baseio.slackclone.uichat.chatthread

import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.usecases.channels.UseCaseWorkspaceChannelRequest
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchAndSaveChannelMembers
import kotlinx.coroutines.launch
import ViewModel
import com.arkivanov.decompose.ComponentContext
import dev.baseio.slackclone.uionboarding.coroutineScope
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import dev.baseio.slackdomain.usecases.channels.UseCaseGetChannelMembers
import dev.baseio.slackdomain.usecases.chat.UseCaseFetchAndSaveMessages
import dev.baseio.slackdomain.usecases.chat.UseCaseStreamLocalMessages
import dev.icerock.moko.paging.LambdaPagedListDataSource
import dev.icerock.moko.paging.Pagination
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*

class ChatScreenComponent(
  componentContext: ComponentContext,
  coroutineDispatcherProvider: CoroutineDispatcherProvider,
  private val useCaseFetchAndSaveChannelMembers: UseCaseFetchAndSaveChannelMembers,
  private val useCaseFetchAndSaveMessages: UseCaseFetchAndSaveMessages,
  private val useCaseChannelMembers: UseCaseGetChannelMembers,
  private val useCaseStreamLocalMessages: UseCaseStreamLocalMessages,
  private val sendMessageDelegate: SendMessageDelegate,
) : ComponentContext by componentContext, SendMessageDelegate by sendMessageDelegate {

  private val viewModelScope = coroutineScope(coroutineDispatcherProvider.main + SupervisorJob())

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
      messageChangeListener()
    }
  }

  fun sendMessageNow(message: String) {
    viewModelScope.launch {
      sendMessageDelegate.sendMessage(message.trim().trimEnd())
      chatBoxState.value = BoxState.Collapsed
    }
  }

  private fun UseCaseWorkspaceChannelRequest.refreshChannelMembers() =
    viewModelScope.launch(parentJob!! + exceptions) {
      useCaseFetchAndSaveChannelMembers.invoke(this@refreshChannelMembers)
    }

  private fun UseCaseWorkspaceChannelRequest.messageChangeListener() {
    viewModelScope.launch(parentJob!! + exceptions) {
      useCaseStreamLocalMessages.invoke(this@messageChangeListener).collectLatest { skMessageList ->
        chatMessagesFlow.value = skMessageList
      }
    }
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
      useCaseFetchAndSaveMessages.invoke(
        request
      )
    },
    comparator = { a, b ->
      a.uuid.hashCode() - b.uuid.hashCode()
    },
    nextPageListener = { result: Result<List<DomainLayerMessages.SKMessage>> ->
      if (result.isSuccess) {
        //chatMessagesFlow.value = result.getOrNull() ?: emptyList()
        println("Next page successful loaded")
      } else {
        println("Next page loading failed")
      }
    },
    refreshListener = { result: Result<List<DomainLayerMessages.SKMessage>> ->
      if (result.isSuccess) {
        println("Refresh successful")
        //chatMessagesFlow.value = (result.getOrNull() ?: emptyList())
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
