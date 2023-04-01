package dev.baseio.slackclone.chatmessaging.newchat

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import dev.baseio.slackclone.getKoin
import dev.baseio.slackdomain.model.channel.DomainLayerChannels

class NewChatThreadComponent(
    componentContext: ComponentContext,
    val navigationPop: () -> Unit,
    val navigationPopWith: (DomainLayerChannels.SKChannel) -> Unit
) : ComponentContext by componentContext {

    val viewModel = instanceKeeper.getOrCreate {
        SearchCreateChannelVM(
            getKoin().get(),
            getKoin().get(),
            getKoin().get(),
            getKoin().get(),
            getKoin().get()
        ) {
            navigationPopWith(it)
        }
    }

    init {
    }
}
