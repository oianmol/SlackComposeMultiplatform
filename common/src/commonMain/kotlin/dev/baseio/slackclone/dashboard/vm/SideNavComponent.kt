package dev.baseio.slackclone.dashboard.vm

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import dev.baseio.slackclone.getKoin

class SideNavComponent(
    componentContext: ComponentContext,
    navigateOnboardingRoot: () -> Unit
) : ComponentContext by componentContext {

    val viewModel = instanceKeeper.getOrCreate {
        SideNavVM(
            getKoin().get(),
            getKoin().get(),
            getKoin().get(),
            getKoin().get(),
            getKoin().get(),
            navigateOnboardingRoot
        )
    }
}
