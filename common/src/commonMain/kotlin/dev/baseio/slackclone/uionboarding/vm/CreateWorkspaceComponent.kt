package dev.baseio.slackclone.uionboarding.vm

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import dev.baseio.slackclone.koinApp

class CreateWorkspaceComponent(
    componentContext: ComponentContext,
    private val login: Boolean,
    val navigateDashboard: () -> Unit
) : ComponentContext by componentContext {

    val authCreateWorkspaceVM =
        instanceKeeper.getOrCreate {
            AuthCreateWorkspaceVM(
                koinApp.koin.get(),
                navigateDashboard = navigateDashboard,
                useCaseCreateWorkspace = koinApp.koin.get()
            )
        }

    fun isLogin(): Boolean {
        return login
    }
}
