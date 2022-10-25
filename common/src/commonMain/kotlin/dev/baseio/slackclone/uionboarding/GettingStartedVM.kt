package dev.baseio.slackclone.uionboarding

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.reduce
import dev.baseio.slackclone.SlackViewModel
import dev.baseio.slackclone.uionboarding.compose.SlackAnimSpec
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GettingStartedVM(coroutineDispatcherProvider: CoroutineDispatcherProvider) :
    SlackViewModel(coroutineDispatcherProvider) {
    var componentState = MutableValue(
        GettingStartedComponent.GettingStartedState(
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
        viewModelScope.launch {
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
}
