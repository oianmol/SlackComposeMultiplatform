package dev.baseio.slackclone.uichannels

import dev.baseio.slackclone.SlackViewModel
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchAllChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchRecentChannels
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest

class SlackChannelVM(
    coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val ucFetchChannels: UseCaseFetchAllChannels,
    private val useCaseGetSelectedWorkspace: UseCaseGetSelectedWorkspace,
    private val ucFetchRecentChannels: UseCaseFetchRecentChannels
) : SlackViewModel(coroutineDispatcherProvider) {
    val channels = MutableStateFlow<Flow<List<DomainLayerChannels.SKChannel>>>(emptyFlow())

    fun allChannels() {
        channels.value = useCaseGetSelectedWorkspace.invokeFlow().flatMapLatest {
            ucFetchChannels(it!!.uuid)
        }
    }

    fun loadRecentChannels() {
        channels.value =
            useCaseGetSelectedWorkspace.invokeFlow().flatMapLatest {
                ucFetchRecentChannels(it!!.uuid)
            }
    }
}
