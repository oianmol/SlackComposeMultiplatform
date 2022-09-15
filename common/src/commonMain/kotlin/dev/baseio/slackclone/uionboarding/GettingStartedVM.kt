package dev.baseio.slackclone.uionboarding

import ViewModel
import androidx.compose.runtime.mutableStateOf
import dev.baseio.slackclone.uionboarding.compose.SlackAnimSpec
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GettingStartedVM : ViewModel() {
  var introTextExpanded = mutableStateOf(false)
  var isStartAnimation = mutableStateOf(false)
  var showSlackAnim = mutableStateOf(true)

  suspend fun endAnimation() {
    showSlackAnim.value = false
    delay(250)
    introTextExpanded.value = !introTextExpanded.value
  }

  fun animate() {
    viewModelScope.launch {
      isStartAnimation.value = true
      delay(SlackAnimSpec.ANIM_DURATION.toLong().plus(700))
      isStartAnimation.value = false
      delay(SlackAnimSpec.ANIM_DURATION.toLong().plus(800))
      endAnimation()
    }
  }

  override fun onClear() {
    super.onClear()
    viewModelScope.cancel()
  }
}