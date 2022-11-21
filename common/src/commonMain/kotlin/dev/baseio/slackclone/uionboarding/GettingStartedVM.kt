package dev.baseio.slackclone.uionboarding

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.reduce
import dev.baseio.grpc.IGrpcCalls
import dev.baseio.slackclone.SlackViewModel
import dev.baseio.slackclone.koinApp
import dev.baseio.slackclone.uichat.chatthread.SendMessageDelegate
import dev.baseio.slackdata.protos.KMSKQrCodeResponse
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import dev.baseio.slackdomain.usecases.auth.UseCaseQRAuthUser
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
      isStartAnimation = false,
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
        it.copy(isStartAnimation = true)
      }
      delay(SlackAnim.ANIM_DURATION.toLong().plus(700))
      componentState.reduce {
        it.copy(isStartAnimation = false)
      }
      delay(SlackAnim.ANIM_DURATION.toLong().plus(800))
      endAnimation()
    }
  }


}

object SlackAnim {
  const val ANIM_DURATION = 1500
}
