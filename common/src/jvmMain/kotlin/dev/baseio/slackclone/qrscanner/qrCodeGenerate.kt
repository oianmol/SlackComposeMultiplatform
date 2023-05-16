package dev.baseio.slackclone.qrscanner

import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import java.io.ByteArrayOutputStream

actual fun qrCodeGenerate(data: String): ByteArray {
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 400, 400)
    with(ByteArrayOutputStream()) {
        MatrixToImageWriter.writeToStream(bitMatrix, "png", this)
        return this.toByteArray()
    }
}