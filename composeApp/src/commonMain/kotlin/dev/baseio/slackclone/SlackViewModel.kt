package dev.baseio.slackclone

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.CoroutineScope

abstract class SlackViewModel(coroutineDispatcherProvider: CoroutineDispatcherProvider) :
    InstanceKeeper.Instance {
    private val job = SupervisorJob()

    val viewModelScope = CoroutineScope(job + coroutineDispatcherProvider.main)

    override fun onDestroy() {
        viewModelScope.cancel() // Cancel the scope when the instance is destroyed
    }
}
