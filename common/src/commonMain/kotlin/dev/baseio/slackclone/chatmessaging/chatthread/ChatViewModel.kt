package dev.baseio.slackclone.chatmessaging.chatthread

import com.arkivanov.decompose.value.MutableValue
import dev.baseio.slackclone.SlackViewModel
import dev.baseio.slackclone.getKoin
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceReadChannels
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchAndSaveChannelMembers
import dev.baseio.slackdomain.usecases.channels.UseCaseGetChannelMembers
import dev.baseio.slackdomain.usecases.channels.UseCaseWorkspaceChannelRequest
import dev.baseio.slackdomain.usecases.chat.UseCaseFetchAndSaveMessages
import dev.baseio.slackdomain.usecases.chat.UseCaseStreamLocalMessages
import dev.icerock.moko.paging.LambdaPagedListDataSource
import dev.icerock.moko.paging.Pagination
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

class ChatViewModel(
    coroutineDispatcherProvider: CoroutineDispatcherProvider = getKoin().get(),
    private val useCaseFetchAndSaveChannelMembers: UseCaseFetchAndSaveChannelMembers = getKoin().get(),
    private val useCaseFetchAndSaveMessages: UseCaseFetchAndSaveMessages = getKoin().get(),
    private val useCaseChannelUsers: UseCaseGetChannelMembers = getKoin().get(),
    private val useCaseStreamLocalMessages: UseCaseStreamLocalMessages = getKoin().get(),
    private val sendMessageDelegate: SendMessageDelegate = getKoin().get(),
    private val skLocalDataSourceReadChannels: SKLocalDataSourceReadChannels = getKoin().get(),
) : SlackViewModel(coroutineDispatcherProvider), SendMessageDelegate by sendMessageDelegate {
    lateinit var channelFlow: MutableValue<DomainLayerChannels.SKChannel>

    val channelMembers = MutableStateFlow<List<DomainLayerUsers.SKUser>>(emptyList())
    var chatMessagesFlow = MutableStateFlow<List<DomainLayerMessages.SKMessage>>(emptyList())

    var securityKeyRequested = MutableStateFlow(false)
        private set
    var securityKeyOffer = MutableStateFlow(false)
        private set

    var chatBoxState = MutableStateFlow(BoxState.Collapsed)
        private set

    private var parentJob: Job = Job()

    var skMessagePagination: Pagination<DomainLayerMessages.SKMessage> = getPagination()

    fun requestFetch(channel: DomainLayerChannels.SKChannel) {
        channelFlow = MutableValue(channel)
        sendMessageDelegate.channel = (channelFlow.value)
        skMessagePagination.refresh()

        parentJob.cancel()
        parentJob = Job()
        channelMembers.value = emptyList()
        loadWorkspaceChannelData(channel)
    }

    private fun loadWorkspaceChannelData(channel: DomainLayerChannels.SKChannel) {
        with(UseCaseWorkspaceChannelRequest(channel.workspaceId, channel.channelId)) {
            fetchChannelUsers()
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
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        } + parentJob) {
            useCaseFetchAndSaveChannelMembers.invoke(this@refreshChannelMembers)
        }

    private fun UseCaseWorkspaceChannelRequest.messageChangeListener() {
        useCaseStreamLocalMessages(this@messageChangeListener)
            .onEach { skMessageList ->
                chatMessagesFlow.value = skMessageList
            }
            .catch {
                it.printStackTrace()
            }
            .launchIn(viewModelScope + parentJob)
    }

    private fun UseCaseWorkspaceChannelRequest.fetchChannelUsers() {
        useCaseChannelUsers(this@fetchChannelUsers)
            .onEach { skUserList ->
                channelMembers.value = skUserList
            }
            .catch {
                it.printStackTrace()
            }
            .launchIn(viewModelScope + parentJob)
    }

    private fun getPagination() = Pagination(
        parentScope = viewModelScope,
        dataSource = LambdaPagedListDataSource { currentList ->
            // fetch and save messages in the range
            val request = UseCaseWorkspaceChannelRequest(
                channelFlow.value.workspaceId,
                (channelFlow.value.channelId),
                20,
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
                // chatMessagesFlow.value = result.getOrNull() ?: emptyList()
                println("Next page successful loaded")
            } else {
                println("Next page loading failed")
            }
        },
        refreshListener = { result: Result<List<DomainLayerMessages.SKMessage>> ->
            if (result.isSuccess) {
                println("Refresh successful")
                // chatMessagesFlow.value = (result.getOrNull() ?: emptyList())
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
            sendMessageDelegate.deleteMessageNow(channel)
        }
    }

    fun clearLongClickMessageRequest() {
        deleteMessageRequest.value = null
    }

    fun requestFetch(channelId: String, function: (DomainLayerChannels.SKChannel) -> Unit) {
        viewModelScope.launch {
            val channel = skLocalDataSourceReadChannels.getChannelByChannelId(channelId)
            channel?.let {
                requestFetch(it)
                function(channel)
            }
        }
    }

    fun messageUpdate(textFieldValue: TextFieldValue) {
        chatMessage.value = TextFieldValue(
            text = textFieldValue.text,
            selection = textFieldValue.selection,
            composition = textFieldValue.composition
        )
    }

    fun requestSecurityKeys() {
        securityKeyRequested.value = true
    }

    fun offerSecurityKeys() {
        securityKeyOffer.value = true
    }
}

private fun BoxState.toggle(): BoxState {
    return if (this == BoxState.Collapsed) BoxState.Expanded else BoxState.Collapsed
}
