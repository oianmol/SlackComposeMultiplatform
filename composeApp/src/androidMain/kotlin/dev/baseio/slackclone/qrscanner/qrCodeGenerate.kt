package dev.baseio.slackclone.qrscanner

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.io.ByteArrayOutputStream

actual suspend fun qrCodeGenerate(data: String): ByteArray {
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 400, 400)
    val w = bitMatrix.width
    val h = bitMatrix.height
    val pixels = IntArray(w * h)
    for (y in 0 until h) {
        for (x in 0 until w) {
            pixels[y * w + x] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
        }
    }
    val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    bitmap.setPixels(pixels, 0, w, 0, 0, w, h)
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    val byteArray = stream.toByteArray()
    bitmap.recycle()
    return byteArray
}