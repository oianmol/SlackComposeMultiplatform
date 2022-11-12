package dev.baseio.slackdata

import android.app.Application
import androidx.test.core.app.ApplicationProvider

actual fun readBinaryResource(resourceName: String): ByteArray {
    return ApplicationProvider.getApplicationContext<Application>().resources.openRawResource(R.raw.sender_verification_key).readBytes()
}