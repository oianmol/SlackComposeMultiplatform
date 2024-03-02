package dev.baseio.slackclone.commonui.reusable

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asComposeImageBitmap
import dev.baseio.slackdata.protos.KMSKQrCodeResponse
import org.jetbrains.skia.Bitmap

@Composable
internal actual fun QrCodeView(modifier: Modifier, response: KMSKQrCodeResponse) {
    val byteArray = response.byteArrayList.map { it.byte.toByte() }.toByteArray()
    val bitmap = Bitmap.makeFromImage(org.jetbrains.skia.Image.makeFromEncoded(byteArray))
    Image(bitmap = bitmap.asComposeImageBitmap(), contentDescription = null, modifier = modifier)
}
