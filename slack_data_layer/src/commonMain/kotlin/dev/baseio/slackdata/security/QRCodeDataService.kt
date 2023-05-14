package dev.baseio.slackdata.security

import dev.baseio.slackdomain.qrcode.QrCodeGenerator
import dev.baseio.slackdomain.security.IByteArraySplitter
import dev.baseio.slackdomain.security.SecurityKeyDataPart

class ByteArraySplitterImpl(private val qrCodeGenerator: QrCodeGenerator) : IByteArraySplitter {
    override fun split(key: ByteArray): List<SecurityKeyDataPart> {
        return qrCodeGenerator.generateFrom(key)
    }
}