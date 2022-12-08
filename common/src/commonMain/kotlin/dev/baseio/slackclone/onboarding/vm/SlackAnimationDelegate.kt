package dev.baseio.slackclone.onboarding.vm

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow

interface SlackAnimationDelegate {
    val state: MutableStateFlow<AuthCreateWorkspaceVMState>
    suspend fun endLoading(coroutineScope: CoroutineScope)
    fun showLoading(coroutineScope: CoroutineScope)
}
