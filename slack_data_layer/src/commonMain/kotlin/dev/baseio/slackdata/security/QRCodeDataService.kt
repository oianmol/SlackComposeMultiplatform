package dev.baseio.slackdata.security

import dev.baseio.slackdomain.qrcode.QrCodeDataGenerator
import dev.baseio.slackdomain.security.IByteArraySplitter
import dev.baseio.slackdomain.security.SecurityKeyDataPart

class ByteArraySplitterImpl(private val qrCodeDataGenerator: QrCodeDataGenerator) : IByteArraySplitter {
    override fun split(key: ByteArray): List<SecurityKeyDataPart> {
        return qrCodeDataGenerator.generateFrom(key)
    }
}