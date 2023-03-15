package dev.baseio.slackclone.onboarding

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.reduce
import dev.baseio.slackclone.SlackViewModel
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GettingStartedVM(
  coroutineDispatcherProvider: CoroutineDispatcherProvider,

  private val navigateDashboard: () -> Unit,
  private val navigateBackNow:()->Unit,
  private val qrCodeDelegate: QrCodeDelegate
) :
  SlackViewModel(coroutineDispatcherProvider), QrCodeDelegate by qrCodeDelegate {

  init {
    qrCodeDelegate.navigateDashboardNow = {
      navigateDashboard.invoke()
    }
    qrCodeDelegate.navigateBack = {
      navigateBackNow.invoke()
    }
  }

  var componentState = MutableValue(
    GettingStartedComponent.GettingStartedState(
      introTextExpanded = false,
      isAnimationStarting = false,
      showSlackAnim = true,
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
    viewModelScope.launch {
      componentState.reduce {
        it.copy(isAnimationStarting = true)
      }
      delay(SlackAnim.ANIM_DURATION.toLong().plus(700))
      componentState.reduce {
        it.copy(isAnimationStarting = false)
      }
      delay(SlackAnim.ANIM_DURATION.toLong().plus(800))
      endAnimation()
    }
  }


}

object SlackAnim {
  const val ANIM_DURATION = 1500
}
