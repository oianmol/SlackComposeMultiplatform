package dev.baseio.slackclone.chatmessaging.chatthread

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import dev.baseio.slackclone.getKoin

class ChatScreenComponent(
    componentContext: ComponentContext,
    navigateToRequestKeys: () -> Unit,
) : ComponentContext by componentContext {

    val chatViewModel = instanceKeeper.getOrCreate {
        ChatViewModel(
            coroutineDispatcherProvider = getKoin().get(),
            useCaseFetchAndSaveChannelMembers = getKoin().get(),
            useCaseFetchAndSaveMessages = getKoin().get(),
            useCaseChannelMembers = getKoin().get(),
            useCaseStreamLocalMessages = getKoin().get(),
            sendMessageDelegate = getKoin().get(),
            skLocalDataSourceReadChannels = getKoin().get(), navigateToRequestKeys = navigateToRequestKeys
        )
    }
}
