package dev.baseio.slackclone.uionboarding

import dev.baseio.slackclone.uionboarding.compose.SlackAnimSpec
import kotlinx.coroutines.delay
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.reduce

class GettingStartedComponent(
  componentContext: ComponentContext
) : ComponentContext by componentContext {
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
    delay(500)
    componentState.reduce {
      it.copy(introTextExpanded = true)
    }
  }

  suspend fun animate() {
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

  data class GettingStartedState(
    val introTextExpanded: Boolean,
    val isStartAnimation: Boolean,
    val showSlackAnim: Boolean
  )
}