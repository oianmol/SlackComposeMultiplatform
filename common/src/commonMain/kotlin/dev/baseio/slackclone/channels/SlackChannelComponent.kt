package dev.baseio.slackclone.channels

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import dev.baseio.slackclone.getKoin

class SlackChannelComponent(
    componentContext: ComponentContext,
    key: String
) : ComponentContext by componentContext {

    val viewModel = instanceKeeper.getOrCreate(key) {
        SlackChannelVM(
            getKoin().get(),
            getKoin().get(),
            getKoin().get(),
            getKoin().get()
        )
    }
}
