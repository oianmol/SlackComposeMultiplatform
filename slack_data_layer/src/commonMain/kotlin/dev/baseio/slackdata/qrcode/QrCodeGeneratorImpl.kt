package dev.baseio.slackdata.qrcode

import dev.baseio.slackdomain.qrcode.QrCodeGenerator
import dev.baseio.slackdomain.security.SecurityKeyDataPart
import kotlin.math.ceil

class QrCodeGeneratorImpl : QrCodeGenerator {
    override fun generateFrom(key: ByteArray): List<SecurityKeyDataPart> {
        return key.splitAsKeyDataPart()
    }
}

private fun ByteArray.splitAsKeyDataPart(): List<SecurityKeyDataPart> {
    val parts = mutableListOf<SecurityKeyDataPart>()
    var startByte = 0
    val endBytes = this.size
    var partNumber = 0
    val totalParts = ceil(endBytes.div(2953.0)).toInt()

    while (partNumber != totalParts) {
        val allocation = kotlin.math.min(2953, endBytes)
        val end = kotlin.math.min(endBytes, allocation.plus(startByte))
        val part = this.copyOfRange(startByte, end)
        parts.add(
            SecurityKeyDataPart(
                partNumber = partNumber,
                totalParts = totalParts,
                partData = part.contentToString()
            )
        )
        startByte += allocation.plus(1)
        partNumber += 1
    }
    return parts
}
