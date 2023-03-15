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
    token: String,
    navigateBackNow: () -> Unit,
    navigateDashboard: () -> Unit
) :
    SlackViewModel(coroutineDispatcherProvider) {
    val state = MutableStateFlow(AuthCreateWorkspaceVMState())

    private suspend fun endLoading() {
        state.value = state.value.copy(isAnimationStarting = false)
        delay(250)
        showLoading()
    }

    fun showLoading() {
        viewModelScope.launch {
            state.value = state.value.copy(isAnimationStarting = true)
            delay(SlackAnim.ANIM_DURATION.toLong().plus(700))
            state.value = state.value.copy(isAnimationStarting = false)
            delay(SlackAnim.ANIM_DURATION.toLong().plus(800))
            endLoading()
        }
    }

    init {
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