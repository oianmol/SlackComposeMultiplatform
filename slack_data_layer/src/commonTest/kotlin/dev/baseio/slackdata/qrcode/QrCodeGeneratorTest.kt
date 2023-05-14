package dev.baseio.slackdata.qrcode

import dev.baseio.slackdomain.qrcode.QrCodeGenerator
import kotlin.test.Test
import kotlin.test.assertTrue

class QrCodeGeneratorTest {
    private val subject: QrCodeGenerator = QrCodeGeneratorImpl()

    @Test
    fun testQrCodeGeneratorImpl() {
        //given
        val bytes = ByteArray(4086) { 0 }
        //when
        val keyData = subject.generateFrom(bytes)
        //then
        assertTrue(
            actual = keyData.first().totalParts == 2,
            message = "expected 2 parts only"
        )
    }
}