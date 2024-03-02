package dev.baseio.slackclone.onboarding.vm

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import dev.baseio.slackclone.getKoin

class EmailMagicLinkComponent(
    componentContext: ComponentContext,
    private val email: String,
    val workspace: String,
    private val navigateDashboard: () -> Unit,
    val navigateBack: () -> Unit
) : ComponentContext by componentContext {

    val viewModel =
        instanceKeeper.getOrCreate {
            SendMagicLinkForWorkspaceViewModel(
                coroutineDispatcherProvider = getKoin().get(),
                useCaseAuthWorkspace = getKoin().get(),
                useCaseSaveFCMToken = getKoin().get(),
                email = email,
                workspace = workspace
            )
        }
}
