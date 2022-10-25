package dev.baseio.slackclone.uichat.newchat

import dev.baseio.slackclone.SlackViewModel
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseCreateChannel
import dev.baseio.slackdomain.usecases.users.UseCaseFetchAndSaveUsers
import dev.baseio.slackdomain.usecases.users.UseCaseFetchChannelsWithSearch
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class NewChatThreadVM(
    private val useCaseGetSelectedWorkspace: UseCaseGetSelectedWorkspace,
    private val useCaseFetchAndSaveUsers: UseCaseFetchAndSaveUsers,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val useCaseCreateChannel: UseCaseCreateChannel,
    private val useCaseFetchChannelsWithSearch: UseCaseFetchChannelsWithSearch,
    private val navigationPopWith: (DomainLayerChannels.SKChannel) -> Unit
) : SlackViewModel(coroutineDispatcherProvider) {
    val search = MutableStateFlow("")
    var channelsStream = MutableStateFlow<List<DomainLayerChannels.SKChannel>>(emptyList())
        private set

    var errorStream = MutableStateFlow<Throwable?>(null)
        private set

    init {
        viewModelScope.launch {
            useCaseGetSelectedWorkspace.invokeFlow().onEach { workspace ->
                workspace?.uuid?.let { useCaseFetchAndSaveUsers(it) }
            }.launchIn(this)

            search.collectLatest { search ->
                useCaseGetSelectedWorkspace.invokeFlow()
                    .mapNotNull { it }
                    .flatMapConcat { workspace ->
                        useCaseFetchChannelsWithSearch(workspace.uuid, search)
                    }.flowOn(coroutineDispatcherProvider.io)
                    .onEach {
                        channelsStream.value = it
                    }.flowOn(coroutineDispatcherProvider.main)
                    .launchIn(viewModelScope)
            }
        }
    }

    fun search(newValue: String) {
        search.value = newValue
    }

    private fun navigate(channel: DomainLayerChannels.SKChannel) {
        navigationPopWith(channel)
    }

    fun createChannel(channel: DomainLayerChannels.SKChannel) {
        viewModelScope.launch(
            CoroutineExceptionHandler { _, throwable ->
                errorStream.value = throwable
            }
        ) {
            channel.channelId.takeIf { it.isNotEmpty() }?.let {
                navigate(channel)
            } ?: run {
                val result = useCaseCreateChannel.invoke(channel)
                val channelNew = result.getOrThrow()
                navigate(channelNew)
            }
        }
    }
}
