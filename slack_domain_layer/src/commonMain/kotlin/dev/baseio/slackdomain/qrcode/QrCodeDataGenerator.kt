package dev.baseio.slackdomain.qrcode

import dev.baseio.slackdomain.security.SecurityKeyDataPart

interface QrCodeDataGenerator {
    fun generateFrom(key: ByteArray) : List<SecurityKeyDataPart>
}