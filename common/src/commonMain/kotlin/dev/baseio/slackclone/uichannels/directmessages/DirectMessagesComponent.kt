package dev.baseio.slackclone.uichannels.directmessages

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import dev.baseio.slackclone.koinApp

class DirectMessagesComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext {

    val viewModel =
        instanceKeeper.getOrCreate { DirectMessagesVM(koinApp.koin.get(), koinApp.koin.get(), koinApp.koin.get()) }
}
