package dev.baseio.slackclone.uionboarding

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.doOnDestroy
import dev.baseio.slackclone.koinApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

class GettingStartedComponent(
    componentContext: ComponentContext,
    val onCreateWorkspaceRequested: (Boolean) -> Unit,
    val navigateDashboard: () -> Unit
) : ComponentContext by componentContext {

    val viewModel =
        instanceKeeper.getOrCreate { GettingStartedVM(koinApp.koin.get(),koinApp.koin.get(), navigateDashboard = navigateDashboard) }

    data class GettingStartedState(
        val introTextExpanded: Boolean,
        val isStartAnimation: Boolean,
        val showSlackAnim: Boolean
    )
}

fun CoroutineScope(context: CoroutineContext, lifecycle: Lifecycle): CoroutineScope {
    val scope = CoroutineScope(context)
    lifecycle.doOnDestroy(scope::cancel)
    return scope
}

fun LifecycleOwner.coroutineScope(context: CoroutineContext): CoroutineScope = CoroutineScope(context, lifecycle)
