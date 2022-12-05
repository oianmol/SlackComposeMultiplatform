package dev.baseio.slackclone.onboarding.vm

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import dev.baseio.slackclone.getKoin

class CreateWorkspaceComponent(
    componentContext: ComponentContext,
    private val email: String,
    val workspace:String,
    val navigateDashboard: () -> Unit,
    val navigateBack:()->Unit
) : ComponentContext by componentContext {

    val viewModel =
        instanceKeeper.getOrCreate {
            AuthCreateWorkspaceVM(
                getKoin().get(),
                navigateDashboard = navigateDashboard,
                useCaseCreateWorkspace = getKoin().get(), useCaseSaveFCMToken = getKoin().get(),
            )
        }
}
