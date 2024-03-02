package dev.baseio.slackclone.channels.createsearch

import dev.baseio.slackclone.SlackViewModel
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchChannelCount
import dev.baseio.slackdomain.usecases.channels.UseCaseSearchChannel
import dev.baseio.slackdomain.usecases.channels.UseCaseWorkspaceChannelRequest
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
class SearchChannelVM(
    private val ucFetchChannels: UseCaseSearchChannel,
    private val useCaseFetchChannelCount: UseCaseFetchChannelCount,
    private val useCaseGetSelectedWorkspace: UseCaseGetSelectedWorkspace,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider
) : SlackViewModel(coroutineDispatcherProvider) {
    val search = MutableStateFlow("")
    val channelCount = MutableStateFlow(0)
    var channels = MutableStateFlow<List<DomainLayerChannels.SKChannel>>(emptyList())

    init {
        viewModelScope.launch {
            val currentSelectedWorkspaceId = useCaseGetSelectedWorkspace()
            currentSelectedWorkspaceId?.let {
                val count = useCaseFetchChannelCount(workspaceId = currentSelectedWorkspaceId.uuid)
                channelCount.value = count
            }
            search.debounce(250.milliseconds).collectLatest { search ->
                useCaseGetSelectedWorkspace.invokeFlow().flatMapConcat {
                    ucFetchChannels(UseCaseWorkspaceChannelRequest(it!!.uuid, search))
                }
                    .flowOn(coroutineDispatcherProvider.io)
                    .onEach {
                        channels.value = it
                    }.flowOn(coroutineDispatcherProvider.main)
                    .launchIn(viewModelScope)
            }
        }
    }
}
