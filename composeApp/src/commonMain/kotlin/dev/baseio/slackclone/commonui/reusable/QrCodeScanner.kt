package dev.baseio.slackclone.commonui.reusable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal expect fun QrCodeScanner(modifier: Modifier, onQrCodeScanned: (String) -> Unit)
