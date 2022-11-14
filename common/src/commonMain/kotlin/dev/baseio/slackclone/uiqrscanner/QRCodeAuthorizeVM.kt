package dev.baseio.slackclone.uiqrscanner

import dev.baseio.grpc.IGrpcCalls
import dev.baseio.slackclone.SlackViewModel
import dev.baseio.slackclone.koinApp
import dev.baseio.slackclone.uionboarding.QrCodeDelegate
import dev.baseio.slackdata.protos.kmSKQRAuthVerify
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class QRCodeAuthorizeVM(
  coroutineDispatcherProvider: CoroutineDispatcherProvider,
  private val iGrpcCalls: IGrpcCalls = koinApp.koin.get(),
  private val qrCodeDelegate: QrCodeDelegate
) :
  SlackViewModel(coroutineDispatcherProvider), QrCodeDelegate by qrCodeDelegate {

  /**
   * This method authorized the code on the backend and once we get the result with token,
   * we take the user to dashboard screen
   */

}