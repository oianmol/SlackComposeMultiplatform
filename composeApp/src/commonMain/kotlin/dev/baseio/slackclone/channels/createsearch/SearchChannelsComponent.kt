package dev.baseio.slackclone.channels.createsearch

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import dev.baseio.slackclone.RootComponent
import dev.baseio.slackclone.getKoin
import dev.baseio.slackdomain.model.channel.DomainLayerChannels

class SearchChannelsComponent constructor(
    componentContext: ComponentContext,
    val navigationPop: () -> Unit,
    val navigateRoot: (RootComponent.Config) -> Unit,
    val navigationPopWith: (DomainLayerChannels.SKChannel) -> Unit

) : ComponentContext by componentContext {

    val viewModel = instanceKeeper.getOrCreate {
        SearchChannelVM(
            getKoin().get(),
            getKoin().get(),
            getKoin().get(),
            getKoin().get()
        )
    }
}
