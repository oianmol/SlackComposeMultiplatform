package dev.baseio.slackclone.uionboarding.vm

import dev.baseio.slackclone.SlackViewModel
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.usecases.workspaces.UseCaseCreateWorkspace
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AuthCreateWorkspaceVM(
    coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val useCaseCreateWorkspace: UseCaseCreateWorkspace,
    val navigateDashboard: () -> Unit
) : SlackViewModel(coroutineDispatcherProvider) {
    val state = MutableStateFlow(AuthCreateWorkspaceVMState())

    fun createWorkspace() {
        viewModelScope.launch(
            CoroutineExceptionHandler { _, throwable ->
                throwable.printStackTrace()
                state.value = state.value.copy(error = throwable, loading = false)
            }
        ) {
            state.value = state.value.copy(error = null, loading = true)
            useCaseCreateWorkspace(state.value.email, state.value.password, state.value.domain)
            state.value = state.value.copy(loading = false)
            navigateDashboard()
        }
    }
}

data class AuthCreateWorkspaceVMState(
    var email: String = "",
    var password: String = "",
    var domain: String = "",
    var error: Throwable? = null,
    var loading: Boolean = false
)
