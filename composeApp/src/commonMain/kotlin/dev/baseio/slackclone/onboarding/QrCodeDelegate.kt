package dev.baseio.slackclone.onboarding

import dev.baseio.grpc.IGrpcCalls
import dev.baseio.slackclone.getKoin
import dev.baseio.slackdata.protos.KMSKQrCodeResponse
import dev.baseio.slackdata.protos.kmSKQRAuthVerify
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import dev.baseio.slackdomain.usecases.auth.UseCaseAuthWithQrCode
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

interface QrCodeDelegate {
    var qrCode: MutableStateFlow<KMSKQrCodeResponse?>
    var loadingQR: MutableStateFlow<Boolean>
    var message: MutableStateFlow<String>
    var navigateDashboardNow: () -> Unit
    var navigateBack: () -> Unit
    var exception: MutableStateFlow<Throwable?>
    var scanningMode: MutableStateFlow<Boolean>

    fun loadQrCode(viewModelScope: CoroutineScope)
    fun authorize(code: String, viewModelScope: CoroutineScope)
    fun toggleScanningMode()
}

class QrCodeDelegateImpl(private val useCaseAuthWithQrCode: UseCaseAuthWithQrCode, private val iGrpcCalls: IGrpcCalls) :
    QrCodeDelegate {
    override var qrCode = MutableStateFlow<KMSKQrCodeResponse?>(null)
    override var scanningMode = MutableStateFlow(false)
    override var loadingQR = MutableStateFlow(false)
    override var message = MutableStateFlow("")
    override var exception = MutableStateFlow<Throwable?>(null)
    override var navigateDashboardNow: () -> Unit = {}
    override var navigateBack: () -> Unit = {}

    private var authorizeJob: Job? = null

    override fun toggleScanningMode() {
        scanningMode.value = !scanningMode.value
    }

    override fun authorize(code: String, viewModelScope: CoroutineScope) {
        authorizeJob?.takeIf { it.isActive }?.let {
            return
        }
        authorizeJob = viewModelScope.launch(
            CoroutineExceptionHandler { _, throwable ->
                exception.value = throwable
            }
        ) {
            val kmskQrCodeResponse = iGrpcCalls.authorizeQrCode(
                kmSKQRAuthVerify {
                    this.token = code
                }
            )
            useCaseAuthWithQrCode(
                result = DomainLayerUsers.SKAuthResult(
                    kmskQrCodeResponse.token,
                    kmskQrCodeResponse.refreshToken,
                    status = DomainLayerUsers.SKStatus(
                        kmskQrCodeResponse.status.information,
                        kmskQrCodeResponse.status.statusCode
                    )
                )
            )
            navigateDashboardNow()
        }
    }

    override fun loadQrCode(viewModelScope: CoroutineScope) {
        qrCode.value?.let {
            clearQR()
            return
        }
        loadingQR.value = true
        message.value = "Preparing for Authentication"
        listenQrCodeResponseInternal(viewModelScope)
    }

    private fun listenQrCodeResponseInternal(viewModelScope: CoroutineScope) {
        // TODO don't use grpccalls directly instead invoke a useCase
        getKoin().get<IGrpcCalls>().getQrCodeResponse().onEach { kmskQrCodeResponse ->
            if (kmskQrCodeResponse.hasAuthResult()) {
                navigateBack()
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

    private fun clearQR() {
        qrCode.value = null
        message.value = ""
    }
}
