package dev.baseio.slackclone.uidashboard.home

import com.arkivanov.decompose.ComponentContext
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import kotlinx.coroutines.flow.*

class HomeScreenComponent(
    componentContext: ComponentContext,
    private val useCaseGetSelectedWorkspace: UseCaseGetSelectedWorkspace
) : ComponentContext by componentContext {

    var lastSelectedWorkspace = MutableStateFlow(flow())
        private set

    fun flow() = useCaseGetSelectedWorkspace.invokeFlow()
}
