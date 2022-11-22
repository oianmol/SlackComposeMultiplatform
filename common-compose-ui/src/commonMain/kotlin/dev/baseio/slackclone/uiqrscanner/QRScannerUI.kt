package dev.baseio.slackclone.uiqrscanner

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.commonui.material.SlackSurfaceAppBar
import dev.baseio.slackclone.commonui.reusable.QrCodeScanner
import dev.baseio.slackclone.commonui.reusable.QrCodeView
import dev.baseio.slackclone.commonui.theme.SlackCloneColor
import dev.baseio.slackclone.commonui.theme.SlackCloneColorProvider
import dev.baseio.slackclone.commonui.theme.SlackCloneSurface
import dev.baseio.slackclone.commonui.theme.SlackCloneTypography
import dev.baseio.slackclone.uionboarding.QrCodeDelegate

@Composable
fun QRScannerUI(modifier: Modifier = Modifier, mode: QrScannerMode, qrCodeDelegate: QrCodeDelegate, navigateBack: () -> Unit) {
  val coroutineScope = rememberCoroutineScope()
  Scaffold(modifier) {
    when (mode) {
      QrScannerMode.CAMERA -> {
        QrCodeScanner(Modifier.fillMaxSize().padding(it)) { code ->
          qrCodeDelegate.authorize(code, coroutineScope)
        }
      }

      QrScannerMode.QR_DISPLAY -> {
        val qrResponse by qrCodeDelegate.qrCode.collectAsState()

        LaunchedEffect(Unit) {
          qrCodeDelegate.loadQrCode(this)
        }

        SlackCloneSurface(
          color = SlackCloneColor,
          modifier = Modifier
        ) {
          Column(
            Modifier.fillMaxSize(),
          ) {
            SlackSurfaceAppBar(
              title = {
                Text(
                  text = "QR Scanner",
                  style = SlackCloneTypography.subtitle1.copy(color = SlackCloneColorProvider.colors.appBarTextTitleColor)
                )
              },
              navigationIcon = {
                IconButton(onClick = {
                  navigateBack()
                }) {
                  Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = "Clear",
                    modifier = Modifier.padding(start = 8.dp),
                    tint = SlackCloneColorProvider.colors.appBarIconColor
                  )
                }
              },
              backgroundColor = SlackCloneColorProvider.colors.appBarColor
            )
            Column(Modifier.fillMaxSize()) {
              qrResponse?.let { it1 -> QrCodeView(Modifier.size(300.dp), it1) }
                ?: CircularProgressIndicator(color = Color.White)
            }
          }
        }
      }
    }

  }

}