package dev.baseio.slackdomain.qrcode

import dev.baseio.slackdomain.security.SecurityKeyDataPart

interface QrCodeGenerator {
    fun generateFrom(key: ByteArray) : List<SecurityKeyDataPart>
}