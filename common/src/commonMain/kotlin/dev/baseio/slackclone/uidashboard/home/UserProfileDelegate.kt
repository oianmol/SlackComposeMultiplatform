package dev.baseio.slackclone.uidashboard.home

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import dev.baseio.slackclone.koinApp

class UserProfileComponent(
  componentContext: ComponentContext,
  val navigateOnboardingRoot: () -> Unit
) :
  ComponentContext by componentContext {

  val viewModel = instanceKeeper.getOrCreate { UserProfileVM(koinApp.koin.get(),koinApp.koin.get(), navigateOnboardingRoot) }


}