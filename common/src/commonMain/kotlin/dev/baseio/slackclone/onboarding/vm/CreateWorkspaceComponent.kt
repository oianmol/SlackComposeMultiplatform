package dev.baseio.slackclone.onboarding.vm

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import dev.baseio.slackclone.getKoin

class CreateWorkspaceComponent(
    componentContext: ComponentContext,
    private val login: Boolean,
    val navigateDashboard: () -> Unit
) : ComponentContext by componentContext {

    val authCreateWorkspaceVM =
        instanceKeeper.getOrCreate {
            AuthCreateWorkspaceVM(
                getKoin().get(),
                navigateDashboard = navigateDashboard,
                useCaseCreateWorkspace = getKoin().get(), useCaseSaveFCMToken = getKoin().get(),
            )
        }

    fun isLogin(): Boolean {
        return login
    }
}
