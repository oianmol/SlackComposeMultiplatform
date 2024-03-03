package dev.baseio.slackclone.qrscanner

import io.ktor.utils.io.core.toByteArray
import kotlinx.coroutines.await
import kotlin.js.Promise

actual suspend fun qrCodeGenerate(data: String): ByteArray {
    return QRCode.toDataURL(data).await().toByteArray()
}

@JsModule("qrcode")
external object QRCode {
    fun toDataURL(text: String, options: dynamic = definedExternally): Promise<String>
    // Add other methods you might need
}