package dev.baseio.slackclone.onboarding.vm

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SlackAnimationDelegateImpl : SlackAnimationDelegate {
    override val state = MutableStateFlow(AuthCreateWorkspaceVMState())

    override suspend fun endLoading(coroutineScope: CoroutineScope) {
        state.value = state.value.copy(isAnimationStarting = false)
        delay(250)
        showLoading(coroutineScope)
    }

    override fun showLoading(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            state.value = state.value.copy(isAnimationStarting = true)
            delay(SlackAnim.ANIM_DURATION.toLong().plus(700))
            state.value = state.value.copy(isAnimationStarting = false)
            delay(SlackAnim.ANIM_DURATION.toLong().plus(800))
            endLoading(coroutineScope)
        }
    }
}