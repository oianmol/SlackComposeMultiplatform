package dev.baseio.slackclone.uichannels

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import dev.baseio.slackclone.koinApp

class SlackChannelComponent(
  componentContext: ComponentContext,
  key: String
) : ComponentContext by componentContext {

  val viewModel = instanceKeeper.getOrCreate(key) {
    SlackChannelVM(
      koinApp.koin.get(),
      koinApp.koin.get(),
      koinApp.koin.get(),
      koinApp.koin.get()
    )
  }

}