package dev.baseio.slackclone.uionboarding

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.reduce
import dev.baseio.grpc.IGrpcCalls
import dev.baseio.slackclone.SlackViewModel
import dev.baseio.slackclone.koinApp
import dev.baseio.slackclone.uionboarding.compose.SlackAnimSpec
import dev.baseio.slackdata.protos.KMSKQrCodeResponse
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GettingStartedVM(coroutineDispatcherProvider: CoroutineDispatcherProvider) :
    SlackViewModel(coroutineDispatcherProvider) {

    var qrCode = MutableStateFlow<KMSKQrCodeResponse?>(null)
    var loadingQR = MutableStateFlow(false)
    var message = MutableStateFlow("")

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
            delay(SlackAnimSpec.ANIM_DURATION.toLong().plus(700))
            componentState.reduce {
                it.copy(isStartAnimation = false)
            }
            delay(SlackAnimSpec.ANIM_DURATION.toLong().plus(800))
            endAnimation()
        }
    }

    fun loadQrCode() {
        qrCode.value?.let {
            clearQR()
            return
        }
        loadingQR.value = true
        message.value = "Preparing for Authentication"
        viewModelScope.launch {
            withContext(koinApp.koin.get<CoroutineDispatcherProvider>().io + CoroutineExceptionHandler { coroutineContext, throwable ->
                loadingQR.value = false
                message.value = throwable.message.toString()
            }) {
                qrCode.value = koinApp.koin.get<IGrpcCalls>().getQrCodeResponse()
                message.value = "QR successfully generated"
                loadingQR.value = false
            }
        }
    }

    fun clearQR() {
        qrCode.value = null
        message.value = ""
    }
}
