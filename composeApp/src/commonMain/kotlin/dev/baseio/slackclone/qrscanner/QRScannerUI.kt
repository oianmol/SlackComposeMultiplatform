package dev.baseio.slackclone.qrscanner

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.commonui.material.SlackSurfaceAppBar
import dev.baseio.slackclone.commonui.reusable.QrCodeScanner
import dev.baseio.slackclone.commonui.reusable.QrCodeView
import dev.baseio.slackclone.commonui.theme.LocalSlackCloneColor
import dev.baseio.slackclone.commonui.theme.SlackCloneColor
import dev.baseio.slackclone.commonui.theme.SlackCloneSurface
import dev.baseio.slackclone.commonui.theme.SlackCloneTypography
import dev.baseio.slackclone.onboarding.QrCodeDelegate
import dev.baseio.slackclone.onboarding.compose.PlatformSideEffects

@Composable
internal fun QRScannerUI(
    modifier: Modifier = Modifier,
    mode: QrScannerMode,
    qrCodeDelegate: QrCodeDelegate,
    navigateBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    when (mode) {
        QrScannerMode.SHOW_QR_SCANNER_CAMERA -> {
            var qrCodeReceived by remember { mutableStateOf(false) }

            val func: (String) -> Unit = { qrCode ->
                if (qrCodeReceived.not()) {
                    qrCodeDelegate.authorize(qrCode, coroutineScope)
                }
                qrCodeReceived = true
            }
            QrCodeScanner(modifier, func)
        }

        QrScannerMode.SHOW_QR_CODE_VIEW -> {
            PlatformSideEffects.SlackCloneColorOnPlatformUI()

            LaunchedEffect(Unit) {
                qrCodeDelegate.loadQrCode(coroutineScope)
            }

            Scaffold(
                modifier = modifier,
                backgroundColor = SlackCloneColor,
                contentColor = LocalSlackCloneColor.current.textSecondary,
                topBar = {
                    SlackSurfaceAppBar(
                        title = {
                            Text(
                                text = "QR Scanner",
                                style = SlackCloneTypography.subtitle1.copy(color = LocalSlackCloneColor.current.appBarTextTitleColor)
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
                                    tint = LocalSlackCloneColor.current.appBarIconColor
                                )
                            }
                        },
                        backgroundColor = LocalSlackCloneColor.current.appBarColor
                    )
                }
            ) {
                val qrResponse by qrCodeDelegate.qrCode.collectAsState()
                Box(Modifier.padding(it).fillMaxSize()) {
                    qrResponse?.let { it1 ->
                        QrCodeView(
                            Modifier.size(300.dp).align(
                                Alignment.Center
                            ), it1
                        )
                    }
                        ?: CircularProgressIndicator(
                            color = Color.White, modifier = Modifier.align(
                                Alignment.Center
                            )
                        )
                }
            }
        }
    }
}
