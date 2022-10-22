package dev.baseio.slackclone.uichat.chatthread

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import dev.baseio.slackclone.koinApp

class ChatScreenComponent(
  componentContext: ComponentContext,
) : ComponentContext by componentContext {

  val chatViewModel = instanceKeeper.getOrCreate {
    ChatViewModel(
      koinApp.koin.get(),
      koinApp.koin.get(),
      koinApp.koin.get(),
      koinApp.koin.get(),
      koinApp.koin.get(),
      koinApp.koin.get()
    )
  }
}
