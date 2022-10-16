package dev.baseio.slackclone.uidashboard.vm

import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces
import dev.baseio.slackdomain.usecases.workspaces.UseCaseSetLastSelectedWorkspace
import dev.baseio.slackdomain.usecases.workspaces.UseCaseFetchWorkspaces
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ViewModel
import dev.baseio.slackdomain.usecases.auth.UseCaseLogout
import kotlinx.coroutines.flow.emptyFlow

class SideNavVM(
    private val useCaseFetchWorkspaces: UseCaseFetchWorkspaces,
    private val useCaseLastSelectedWorkspace: UseCaseSetLastSelectedWorkspace,
    private val userProfileDelegate: UserProfileDelegate,
    private val useCaseClearAuth: UseCaseLogout
) : ViewModel(), UserProfileDelegate by userProfileDelegate {

    var workspacesFlow = MutableStateFlow<Flow<List<DomainLayerWorkspaces.SKWorkspace>>>(emptyFlow())
        private set

    init {
        getCurrentUser(viewModelScope)
        viewModelScope.launch {
            val result = flow()
            workspacesFlow.value = result
        }
    }

    suspend fun flow(): Flow<List<DomainLayerWorkspaces.SKWorkspace>> {
        return useCaseFetchWorkspaces.invoke()
    }

    fun select(skWorkspace: DomainLayerWorkspaces.SKWorkspace) {
        viewModelScope.launch {
            useCaseLastSelectedWorkspace.invoke(skWorkspace)
        }
    }

    fun logout() {
        useCaseClearAuth.invoke()
    }
}