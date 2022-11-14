package dev.baseio.slackclone.uionboarding.vm

import dev.baseio.slackclone.SlackViewModel
import dev.baseio.slackclone.fcmToken
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.usecases.auth.UseCaseSaveFCMToken
import dev.baseio.slackdomain.usecases.workspaces.UseCaseCreateWorkspace
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AuthCreateWorkspaceVM(
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val useCaseCreateWorkspace: UseCaseCreateWorkspace,
    private val useCaseSaveFCMToken:UseCaseSaveFCMToken,
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
            when {
                validationFailed() -> {
                    state.value = state.value.copy(
                        error = Exception("Please check the form and input the required details!"),
                        loading = false
                    )
                }

                else -> {
                    state.value = state.value.copy(error = null, loading = true)
                    useCaseCreateWorkspace(state.value.email, state.value.password, state.value.domain)
                    useCaseSaveFCMToken.invoke(fcmToken())
                    state.value = state.value.copy(loading = false)
                    navigateDashboard()
                }
            }

        }
    }

    private fun validationFailed() =
        state.value.email.trim().isEmpty() || state.value.password.trim().isEmpty() || state.value.domain.trim()
            .isEmpty()
}

data class AuthCreateWorkspaceVMState(
    var email: String = "",
    var password: String = "",
    var domain: String = "",
    var error: Throwable? = null,
    var loading: Boolean = false
)
