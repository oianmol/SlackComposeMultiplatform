package dev.baseio.slackclone

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

abstract class SlackViewModel(coroutineDispatcherProvider: CoroutineDispatcherProvider) : InstanceKeeper.Instance {
    val viewModelScope = CoroutineScope(coroutineDispatcherProvider.main + SupervisorJob())

    override fun onDestroy() {
        viewModelScope.cancel() // Cancel the scope when the instance is destroyed
    }
}
