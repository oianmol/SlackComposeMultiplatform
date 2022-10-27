package dev.baseio.slackclone.uiqrscanner

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.baseio.slackclone.commonui.reusable.QrCodeScanner

@Composable
fun QRScannerUI(qrCodeComponent: QRCodeComponent) {
    Scaffold {
        QrCodeScanner(Modifier.padding(it)) {code->
            qrCodeComponent.viewModel.authorize(code)
        }
    }

}