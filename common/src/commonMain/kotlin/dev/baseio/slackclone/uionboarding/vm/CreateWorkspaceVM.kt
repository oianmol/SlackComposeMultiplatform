package dev.baseio.slackclone.uionboarding.vm

import dev.baseio.slackclone.SlackViewModel
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.usecases.workspaces.UseCaseCreateWorkspace
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class CreateWorkspaceVM(
    coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val useCaseCreateWorkspace: UseCaseCreateWorkspace,
    val navigateDashboard: () -> Unit
) : SlackViewModel(coroutineDispatcherProvider) {
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    val domain = MutableStateFlow("")
    val error = MutableStateFlow<Throwable?>(null)
    val loading = MutableStateFlow(false)

    fun createWorkspace() {
        viewModelScope.launch(
            CoroutineExceptionHandler { _, throwable ->
                throwable.printStackTrace()
                error.value = throwable
                loading.value = false
            }
        ) {
            error.value = null
            loading.value = true
            useCaseCreateWorkspace(email.value, password.value, domain.value)
            loading.value = false
            navigateDashboard()
        }
    }
}
