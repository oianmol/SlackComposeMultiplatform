package dev.baseio.slackclone.channels.directmessages

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import dev.baseio.slackclone.getKoin

class DirectMessagesComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext {

    val viewModel =
        instanceKeeper.getOrCreate { DirectMessagesVM(getKoin().get(), getKoin().get(), getKoin().get()) }
}
