package dev.baseio.slackclone.uionboarding

import dev.baseio.slackclone.uionboarding.compose.SlackAnimSpec
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.reduce
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.doOnDestroy
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class GettingStartedComponent(
  componentContext: ComponentContext,
  coroutineDispatcherProvider: CoroutineDispatcherProvider,
  val onCreateWorkspaceRequested: (Boolean) -> Unit
) : ComponentContext by componentContext {

  private val scope = coroutineScope(coroutineDispatcherProvider.main + SupervisorJob())

  var componentState = MutableValue(
    GettingStartedState(
      introTextExpanded = false,
      isStartAnimation = false,
      showSlackAnim = true
    )
  )
    private set

  private suspend fun endAnimation() {
    componentState.reduce {
      it.copy(showSlackAnim = false)
    }
    delay(250)
    componentState.reduce {
      it.copy(introTextExpanded = true)
    }
  }

  fun animate() {
    scope.launch {
      componentState.reduce {
        it.copy(isStartAnimation = true)
      }
      delay(SlackAnimSpec.ANIM_DURATION.toLong().plus(700))
      componentState.reduce {
        it.copy(isStartAnimation = false)
      }
      delay(SlackAnimSpec.ANIM_DURATION.toLong().plus(800))
      endAnimation()
    }
  }

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

fun LifecycleOwner.coroutineScope(context: CoroutineContext): CoroutineScope =
  CoroutineScope(context, lifecycle)