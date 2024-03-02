package dev.baseio.slackclone.channels

import dev.baseio.slackclone.SlackViewModel
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchAllChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchRecentChannels
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest

class SlackChannelVM(
    coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val ucFetchChannels: UseCaseFetchAllChannels,
    private val useCaseGetSelectedWorkspace: UseCaseGetSelectedWorkspace,
    private val ucFetchRecentChannels: UseCaseFetchRecentChannels
) : SlackViewModel(coroutineDispatcherProvider) {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun allChannels(): Flow<List<DomainLayerChannels.SKChannel>> {
        return useCaseGetSelectedWorkspace.invokeFlow().flatMapLatest {
            ucFetchChannels(it!!.uuid)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun loadRecentChannels(): Flow<List<DomainLayerChannels.SKChannel>> {
        return useCaseGetSelectedWorkspace.invokeFlow().flatMapLatest {
            ucFetchRecentChannels(it!!.uuid)
        }
    }
}
