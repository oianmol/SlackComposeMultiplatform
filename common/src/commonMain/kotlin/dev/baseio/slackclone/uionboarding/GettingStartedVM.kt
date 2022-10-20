package dev.baseio.slackclone.uionboarding

import dev.baseio.slackclone.uionboarding.compose.SlackAnimSpec
import kotlinx.coroutines.delay
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue

class GettingStartedComponent(
  componentContext: ComponentContext
) : ComponentContext by componentContext {
  var componentState = MutableValue(
    GettingStartedState(
      introTextExpanded = false,
      isStartAnimation = false,
      showSlackAnim = false
    )
  )
    private set

  suspend fun endAnimation() {
    componentState.value = componentState.value.copy(showSlackAnim = false)
    delay(250)
    componentState.value = componentState.value.copy(introTextExpanded = !componentState.value.introTextExpanded)
  }

  suspend fun animate() {
    componentState.value = componentState.value.copy(isStartAnimation = true)
    delay(SlackAnimSpec.ANIM_DURATION.toLong().plus(700))
    componentState.value = componentState.value.copy(isStartAnimation = false)
    delay(SlackAnimSpec.ANIM_DURATION.toLong().plus(800))
    endAnimation()
  }

  data class GettingStartedState(
    val introTextExpanded: Boolean,
    val isStartAnimation: Boolean,
    val showSlackAnim: Boolean
  )
}