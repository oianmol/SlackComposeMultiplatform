package dev.baseio.slackclone.onboarding

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.doOnDestroy
import dev.baseio.slackclone.getKoin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

class GettingStartedComponent(
  componentContext: ComponentContext,
  val firstRun:Boolean,
  val navigateBack: () -> Unit,
  private val navigateDashboard: () -> Unit,
  val emailMagicLink: () -> Unit
) : ComponentContext by componentContext {

  val viewModel =
    instanceKeeper.getOrCreate {
      GettingStartedVM(
        coroutineDispatcherProvider = getKoin().get(),
        navigateDashboard = navigateDashboard,
        navigateBackNow = {
          navigateBack.invoke()
        },
        qrCodeDelegate = getKoin().get(),
      )
    }

  data class GettingStartedState(
    val introTextExpanded: Boolean,
    val isAnimationStarting: Boolean,
    val showSlackAnim: Boolean
  )
}

fun CoroutineScope(context: CoroutineContext, lifecycle: Lifecycle): CoroutineScope {
  val scope = CoroutineScope(context)
  lifecycle.doOnDestroy(scope::cancel)
  return scope
}

fun LifecycleOwner.coroutineScope(context: CoroutineContext): CoroutineScope = CoroutineScope(context, lifecycle)
