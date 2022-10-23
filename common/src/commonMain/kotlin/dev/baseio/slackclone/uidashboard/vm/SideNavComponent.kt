package dev.baseio.slackclone.uidashboard.vm

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import dev.baseio.slackclone.koinApp

class SideNavComponent(
  componentContext: ComponentContext,
  navigateOnboardingRoot: () -> Unit
) : ComponentContext by componentContext {

  val viewModel = instanceKeeper.getOrCreate {
    SideNavVM(koinApp.koin.get(), koinApp.koin.get(), koinApp.koin.get(), koinApp.koin.get(), navigateOnboardingRoot)
  }

}