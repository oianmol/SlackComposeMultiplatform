package dev.baseio.slackclone.uionboarding

import dev.baseio.grpc.IGrpcCalls
import dev.baseio.slackclone.koinApp
import dev.baseio.slackdata.protos.KMSKAuthResult
import dev.baseio.slackdata.protos.KMSKQrCodeResponse
import dev.baseio.slackdata.protos.kmSKQRAuthVerify
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import dev.baseio.slackdomain.usecases.auth.UseCaseQRAuthUser
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
  fun loadQrCode(viewModelScope: CoroutineScope)
  suspend fun whenAuthorized(kmskQrCodeResponse: KMSKAuthResult)
  var navigateBack: () -> Unit
  var exception: MutableStateFlow<Throwable?>
  fun authorize(code: String, viewModelScope: CoroutineScope)
  fun toggleScanningMode()

  var scanningMode: MutableStateFlow<Boolean>
}

class QrCodeDelegateImpl(private val qrCodeAuthUser: UseCaseQRAuthUser, private val iGrpcCalls: IGrpcCalls) :
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
    authorizeJob?.let {
      return
    }
    authorizeJob = viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
      exception.value = throwable
    }) {
      val authResult = iGrpcCalls.authorizeQrCode(kmSKQRAuthVerify {
        this.token = code
      })
      whenAuthorized(authResult)
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
    koinApp.koin.get<IGrpcCalls>().getQrCodeResponse().onEach { kmskQrCodeResponse ->
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

  override suspend fun whenAuthorized(kmskQrCodeResponse: KMSKAuthResult) {
    qrCodeAuthUser(
      DomainLayerUsers.SKAuthResult(
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

  private fun clearQR() {
    qrCode.value = null
    message.value = ""
  }
}
