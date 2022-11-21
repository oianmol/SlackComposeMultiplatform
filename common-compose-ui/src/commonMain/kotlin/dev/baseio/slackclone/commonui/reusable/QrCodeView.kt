package dev.baseio.slackclone.commonui.reusable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.baseio.slackdata.protos.KMSKQrCodeResponse

@Composable
expect fun QrCodeView(modifier: Modifier, response: KMSKQrCodeResponse)

