package dev.baseio.slackclone.uichat.newchat

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import dev.baseio.slackclone.koinApp
import dev.baseio.slackdomain.model.channel.DomainLayerChannels

class NewChatThreadComponent(
  componentContext: ComponentContext,
  val navigationPop: () -> Unit,
  val navigationPopWith: (DomainLayerChannels.SKChannel) -> Unit
) : ComponentContext by componentContext {

  val viewModel = instanceKeeper.getOrCreate {
    NewChatThreadVM(
      koinApp.koin.get(),
      koinApp.koin.get(),
      koinApp.koin.get(),
      koinApp.koin.get(),
      koinApp.koin.get()
    ) {
      navigationPopWith(it)
    }
  }

  init {

  }
}