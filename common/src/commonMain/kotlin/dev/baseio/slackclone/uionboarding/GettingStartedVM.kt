package dev.baseio.slackclone.uionboarding

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.reduce
import dev.baseio.grpc.IGrpcCalls
import dev.baseio.slackclone.SlackViewModel
import dev.baseio.slackclone.koinApp
import dev.baseio.slackclone.uionboarding.compose.SlackAnimSpec
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
    private val qrCodeAuthUser: UseCaseQRAuthUser,
    private val navigateDashboard: () -> Unit
) :
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
        listenQrCodeResponseInternal()
    }

    private fun listenQrCodeResponseInternal() {
        // TODO don't use grpccalls directly instead invoke a useCase
        koinApp.koin.get<IGrpcCalls>().getQrCodeResponse().onEach { kmskQrCodeResponse ->
            if (kmskQrCodeResponse.hasAuthResult()) {
                whenAuthorized(kmskQrCodeResponse)
            } else {
                whenQrCodeAvailable(kmskQrCodeResponse)
            }
        }.catch {
            loadingQR.value = false
            message.value = it.message.toString()
        }.launchIn(viewModelScope)
    }

    private fun whenQrCodeAvailable(kmskQrCodeResponse: KMSKQrCodeResponse) {
        qrCode.value = kmskQrCodeResponse
        loadingQR.value = false
    }

    private suspend fun whenAuthorized(kmskQrCodeResponse: KMSKQrCodeResponse) {
        qrCodeAuthUser(
            DomainLayerUsers.SKAuthResult(
                kmskQrCodeResponse.authResult.token,
                kmskQrCodeResponse.authResult.refreshToken,
                status = DomainLayerUsers.SKStatus(
                    kmskQrCodeResponse.authResult.status.information,
                    kmskQrCodeResponse.authResult.status.statusCode
                )
            )
        )
        navigateDashboard()
    }

    private fun clearQR() {
        qrCode.value = null
        message.value = ""
    }
}
