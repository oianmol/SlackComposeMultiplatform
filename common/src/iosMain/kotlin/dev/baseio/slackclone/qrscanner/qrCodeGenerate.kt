package dev.baseio.slackclone.qrscanner

import dev.baseio.extensions.toByteArrayFromNSData
import platform.CoreGraphics.CGAffineTransformMakeScale
import platform.CoreGraphics.CGColorSpaceCreateDeviceRGB
import platform.CoreImage.CIContext
import platform.CoreImage.CIFilter
import platform.CoreImage.PNGRepresentationOfImage
import platform.CoreImage.kCIFormatRGBA8
import platform.Foundation.setValue

actual fun qrCodeGenerate(data: String): ByteArray {
    CIFilter().apply {
        name = "CIQRCodeGenerator"
    }.let {
        it.setValue(data, forKey = "inputMessage")

        val context = CIContext()
        it.outputImage?.imageByApplyingTransform(CGAffineTransformMakeScale(3.0, 3.0))
            ?.let { output ->
                context.PNGRepresentationOfImage(
                    image = output,
                    format = kCIFormatRGBA8,
                    colorSpace = CGColorSpaceCreateDeviceRGB(),
                    options = hashMapOf<Any?, Any>()
                )?.toByteArrayFromNSData()
            }
    }
    return ByteArray(0)
}