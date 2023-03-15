package dev.baseio.slackclone

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.rickclephas.kmp.nativecoroutines.NativeCoroutineScope
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel


abstract class SlackViewModel(coroutineDispatcherProvider: CoroutineDispatcherProvider) :
    InstanceKeeper.Instance {
    private val job = SupervisorJob()

    @NativeCoroutineScope
    internal val viewModelScope = NativeCoroutineScope(job + coroutineDispatcherProvider.main)

    override fun onDestroy() {
        viewModelScope.cancel() // Cancel the scope when the instance is destroyed
    }
}
