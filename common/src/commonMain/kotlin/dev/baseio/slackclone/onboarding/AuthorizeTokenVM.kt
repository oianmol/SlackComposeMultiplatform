package dev.baseio.slackclone.onboarding

import dev.baseio.slackclone.SlackViewModel
import dev.baseio.slackclone.onboarding.vm.AuthCreateWorkspaceVMState
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.usecases.auth.UseCaseFetchAndSaveCurrentUser
import dev.baseio.slackdomain.usecases.workspaces.UseCaseFetchAndSaveWorkspaces
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AuthorizeTokenVM(
    coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val useCaseFetchAndSaveCurrentUser: UseCaseFetchAndSaveCurrentUser,
    private val useCaseFetchAndSaveUserWorkspace: UseCaseFetchAndSaveWorkspaces,
    private val authToken: String,
    private val navigateDashboard: () -> Unit
) :
    SlackViewModel(coroutineDispatcherProvider) {

    val uiState = MutableStateFlow(AuthCreateWorkspaceVMState())

    init {
        initiateWithToken(authToken, navigateDashboard)
    }

    /**
     * recursive loading of the slack animation
     */
    fun showSlackProgressAnimation() {
        viewModelScope.launch {
            uiState.value = uiState.value.copy(loaderState = true)
            delay(SlackAnim.ANIM_DURATION.toLong().plus(700))
            uiState.value = uiState.value.copy(loaderState = false)
            delay(SlackAnim.ANIM_DURATION.toLong().plus(800))
            uiState.value = uiState.value.copy(loaderState = false)
            delay(250)
            showSlackProgressAnimation()
        }
    }


    private fun initiateWithToken(
        authToken: String,
        navigateDashboard: () -> Unit
    ) {
        viewModelScope.launch(
            CoroutineExceptionHandler { _, throwable ->
                throwable.printStackTrace()
                uiState.value = uiState.value.copy(error = throwable)
            }
        ) {
            uiState.value = uiState.value.copy(loading = true)
            useCaseFetchAndSaveUserWorkspace.invoke(authToken)
            useCaseFetchAndSaveCurrentUser.invoke()
            navigateDashboard()
        }
    }

    fun retry() {
        initiateWithToken(authToken, navigateDashboard)
    }
}
