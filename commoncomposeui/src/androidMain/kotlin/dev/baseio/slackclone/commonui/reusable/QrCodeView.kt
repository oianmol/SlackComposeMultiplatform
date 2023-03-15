package dev.baseio.slackclone.commonui.reusable

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import dev.baseio.slackdata.protos.KMSKQrCodeResponse

@Composable
internal actual fun QrCodeView(
    modifier: Modifier,
    response: KMSKQrCodeResponse
) {
    val byteArray = response.byteArrayList.map { it.byte.toByte() }.toByteArray()
    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    Image(bitmap = bitmap.asImageBitmap(), contentDescription = null, modifier = modifier)
}