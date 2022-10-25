package dev.baseio.slackclone.uichannels.createsearch

import dev.baseio.slackclone.SlackViewModel
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseCreateChannel
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class CreateNewChannelVM(
    coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val useCaseCreateChannel: UseCaseCreateChannel,
    private val useCaseGetSelectedWorkspace: UseCaseGetSelectedWorkspace,
    private val navigationWith: (DomainLayerChannels.SKChannel) -> Unit
) : SlackViewModel(coroutineDispatcherProvider) {
    var createChannelState =
        MutableStateFlow(
            DomainLayerChannels.SKChannel.SkGroupChannel(
                avatarUrl = null,
                workId = "",
                uuid = Clock.System.now().toEpochMilliseconds().toString(),
                name = "",
                createdDate = Clock.System.now().toEpochMilliseconds(),
                modifiedDate = Clock.System.now().toEpochMilliseconds(),
                deleted = false
            )
        )

    fun createChannel() {
        viewModelScope.launch(
            CoroutineExceptionHandler { _, throwable ->
                throwable.printStackTrace()
            }
        ) {
            if (createChannelState.value.name.isNotEmpty()) {
                val lastSelectedWorkspace = useCaseGetSelectedWorkspace()
                lastSelectedWorkspace?.let {
                    createChannelState.value = createChannelState.value.copy(
                        workId = lastSelectedWorkspace.uuid,
                        uuid = "${createChannelState.value.name}_${lastSelectedWorkspace.uuid}"
                    )
                    val channel = useCaseCreateChannel(createChannelState.value).getOrThrow()
                    navigationWith(channel)
                }
            }
        }
    }
}
