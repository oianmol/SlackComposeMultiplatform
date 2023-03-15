package dev.baseio.slackclone.dashboard.vm

import dev.baseio.slackclone.SlackViewModel
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetWorkspaces
import dev.baseio.slackdomain.usecases.workspaces.UseCaseSetLastSelectedWorkspace
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SideNavVM(
    private val useCaseFetchWorkspaces: UseCaseGetWorkspaces,
    private val useCaseLastSelectedWorkspace: UseCaseSetLastSelectedWorkspace,
    private val useCaseLastGetSelectedWorkspace: UseCaseGetSelectedWorkspace,
    private val userProfileDelegate: UserProfileDelegate,
    coroutineDispatcherProvider: CoroutineDispatcherProvider,
    navigateOnboardingRoot: () -> Unit
) : SlackViewModel(coroutineDispatcherProvider), UserProfileDelegate by userProfileDelegate {

    private var lastSelectedWorkspace = MutableStateFlow<DomainLayerWorkspaces.SKWorkspace?>(null)
    init {
        useCaseLastGetSelectedWorkspace.invokeFlow().onEach {
            lastSelectedWorkspace.value = it
        }.launchIn(viewModelScope)
        getCurrentUser(viewModelScope, navigateOnboardingRoot)
    }

    fun flow(): Flow<List<DomainLayerWorkspaces.SKWorkspace>> {
        return useCaseFetchWorkspaces.invoke()
    }

    fun select(skWorkspace: DomainLayerWorkspaces.SKWorkspace) {
        viewModelScope.launch {
            useCaseLastSelectedWorkspace.invoke(skWorkspace)
        }
    }

    override fun logout() {
        userProfileDelegate.logout()
    }

    fun isSelectedWorkspace(skWorkspace: DomainLayerWorkspaces.SKWorkspace): Boolean {
        return lastSelectedWorkspace.value?.uuid == skWorkspace.uuid
    }
}
