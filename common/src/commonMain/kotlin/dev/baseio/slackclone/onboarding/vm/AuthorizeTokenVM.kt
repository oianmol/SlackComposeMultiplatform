package dev.baseio.slackclone.onboarding.vm

import dev.baseio.slackclone.SlackViewModel
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.usecases.auth.UseCaseFetchAndSaveCurrentUser
import dev.baseio.slackdomain.usecases.workspaces.UseCaseFetchAndSaveWorkspaces
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class AuthorizeTokenVM(
    coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val slackAnimationDelegate: SlackAnimationDelegate,
    private val useCaseFetchAndSaveCurrentUser: UseCaseFetchAndSaveCurrentUser,
    private val useCaseFetchAndSaveUserWorkspace: UseCaseFetchAndSaveWorkspaces,
    token: String,
    navigateBackNow: () -> Unit,
    navigateDashboard: () -> Unit
) :
    SlackViewModel(coroutineDispatcherProvider), SlackAnimationDelegate by slackAnimationDelegate {
    init {
        processToken(navigateBackNow, token, navigateDashboard)
    }


    private fun processToken(navigateBackNow: () -> Unit, token: String, navigateDashboard: () -> Unit) {
        viewModelScope.launch(CoroutineExceptionHandler { coroutineContext, throwable ->
            throwable.printStackTrace()
            navigateBackNow()
            state.value = state.value.copy(error = throwable)
        }) {
            state.value = state.value.copy(loading = true)
            useCaseFetchAndSaveUserWorkspace.invoke(token)
            useCaseFetchAndSaveCurrentUser.invoke()
            navigateDashboard()
        }
    }
}