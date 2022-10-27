package dev.baseio.slackclone.uiqrscanner

import dev.baseio.grpc.IGrpcCalls
import dev.baseio.slackclone.SlackViewModel
import dev.baseio.slackclone.koinApp
import dev.baseio.slackdata.protos.kmSKQRAuthVerify
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class QRCodeAuthorizeVM(
    coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val iGrpcCalls: IGrpcCalls = koinApp.koin.get(),
    private val navigateBack: () -> Unit
) :
    SlackViewModel(coroutineDispatcherProvider) {

    var exception = MutableStateFlow<Throwable?>(null)
        private set

    fun authorize(code: String) {
        viewModelScope.launch(CoroutineExceptionHandler { coroutineContext, throwable ->
            exception.value = throwable
        }) {
            iGrpcCalls.authorizeQrCode(kmSKQRAuthVerify {
                this.token = code
            })
            navigateBack()
        }
    }
}