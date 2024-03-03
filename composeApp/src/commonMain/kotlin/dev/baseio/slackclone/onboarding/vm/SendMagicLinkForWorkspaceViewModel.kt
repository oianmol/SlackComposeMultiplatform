package dev.baseio.slackclone.onboarding.vm

import dev.baseio.slackclone.SlackViewModel
import dev.baseio.slackclone.fcmToken
import dev.baseio.slackclone.onboarding.SlackAnim
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.usecases.auth.UseCaseSaveFCMToken
import dev.baseio.slackdomain.usecases.workspaces.UseCaseAuthWorkspace
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SendMagicLinkForWorkspaceViewModel(
    coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val useCaseAuthWorkspace: UseCaseAuthWorkspace,
    private val useCaseSaveFCMToken: UseCaseSaveFCMToken,
    private val email: String,
    private val workspace: String
) : SlackViewModel(coroutineDispatcherProvider) {
    val uiState = MutableStateFlow(AuthCreateWorkspaceVMState())

    private suspend fun endLoading() {
        uiState.value = uiState.value.copy(loaderState = false)
        delay(250)
        showSlackProgressAnimation()
    }

    fun showSlackProgressAnimation() {
        viewModelScope.launch {
            uiState.value = uiState.value.copy(loaderState = true)
            delay(SlackAnim.ANIM_DURATION.toLong().plus(700))
            uiState.value = uiState.value.copy(loaderState = false)
            delay(SlackAnim.ANIM_DURATION.toLong().plus(800))
            endLoading()
        }
    }

    fun sendMagicLink() {
        viewModelScope.launch(
            CoroutineExceptionHandler { _, throwable ->
                throwable.printStackTrace()
                uiState.value = uiState.value.copy(error = throwable, loading = false)
            }
        ) {
            uiState.value = uiState.value.copy(error = null, loading = true)
            useCaseAuthWorkspace(
                email,
                workspace,
            )
            useCaseSaveFCMToken.invoke(fcmToken())
            uiState.value = uiState.value.copy(loading = false)
        }
    }
}

data class AuthCreateWorkspaceVMState(
    var error: Throwable? = null,
    var loading: Boolean = false,
    var loaderState: Boolean = false
)
