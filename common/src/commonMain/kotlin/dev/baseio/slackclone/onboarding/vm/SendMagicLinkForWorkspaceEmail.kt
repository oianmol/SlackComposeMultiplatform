package dev.baseio.slackclone.onboarding.vm

import dev.baseio.slackclone.SlackViewModel
import dev.baseio.slackclone.fcmToken
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.usecases.auth.UseCaseSaveFCMToken
import dev.baseio.slackdomain.usecases.workspaces.UseCaseAuthWorkspace
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SendMagicLinkForWorkspaceEmail(
    coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val slackAnimationDelegate: SlackAnimationDelegate,
    private val useCaseAuthWorkspace: UseCaseAuthWorkspace,
    private val useCaseSaveFCMToken: UseCaseSaveFCMToken,
    private val email: String,
    private val workspace: String
) : SlackViewModel(coroutineDispatcherProvider), SlackAnimationDelegate by slackAnimationDelegate {

    init {
        sendMagicLink()
    }

    fun sendMagicLink() {
        viewModelScope.launch(
            CoroutineExceptionHandler { _, throwable ->
                throwable.printStackTrace()
                state.value = state.value.copy(error = throwable, loading = false)
            }
        ) {
            state.value = state.value.copy(error = null, loading = true)
            useCaseAuthWorkspace(
                email,
                workspace,
            )
            useCaseSaveFCMToken.invoke(fcmToken())
            state.value = state.value.copy(loading = false)
        }
    }
}

data class AuthCreateWorkspaceVMState(
    var error: Throwable? = null,
    var loading: Boolean = false,
    var isAnimationStarting: Boolean = false
)
