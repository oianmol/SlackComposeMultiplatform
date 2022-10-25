package dev.baseio.slackclone.uichannels.createsearch

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import dev.baseio.slackclone.koinApp
import dev.baseio.slackdomain.model.channel.DomainLayerChannels

class CreateNewChannelComponent constructor(
    componentContext: ComponentContext,
    val navigationPop: () -> Unit,
    val navigationWith: (DomainLayerChannels.SKChannel) -> Unit
) : ComponentContext by componentContext {
    fun onChannelSelected(channel: DomainLayerChannels.SKChannel) {
        navigationWith(channel)
    }

    val viewModel = instanceKeeper.getOrCreate {
        CreateNewChannelVM(koinApp.koin.get(), koinApp.koin.get(), koinApp.koin.get()) {
            navigationWith(it)
        }
    }
}
