package dev.baseio.slackclone.chatmessaging.chatthread

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import dev.baseio.slackclone.getKoin

class ChatScreenComponent(
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    val chatViewModel = instanceKeeper.getOrCreate {
        ChatViewModel()
    }

}
