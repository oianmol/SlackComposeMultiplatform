package dev.baseio.slackclone.dashboard.home

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import dev.baseio.slackclone.getKoin

class UserProfileComponent(
    componentContext: ComponentContext,
    val navigateOnboardingRoot: () -> Unit
) :
    ComponentContext by componentContext {

    val viewModel = instanceKeeper.getOrCreate { UserProfileVM(getKoin().get(), getKoin().get(), navigateOnboardingRoot) }
}
