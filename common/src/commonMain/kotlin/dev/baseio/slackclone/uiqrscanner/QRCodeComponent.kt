package dev.baseio.slackclone.uiqrscanner

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import dev.baseio.slackclone.koinApp

class QRCodeComponent(
  componentContext: ComponentContext,
  val mode: QrScannerMode,
  val navigateBack: () -> Unit
) : ComponentContext by componentContext {

  val viewModel = instanceKeeper.getOrCreate {
    QRCodeAuthorizeVM(
      koinApp.koin.get(),
      qrCodeDelegate = koinApp.koin.get()
    )
  }

  enum class QrScannerMode {
    CAMERA, QR_DISPLAY
  }
}