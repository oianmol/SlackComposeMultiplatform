package dev.baseio.slackclone.uionboarding.vmtest

import androidx.test.core.app.ApplicationProvider
import dev.baseio.security.AndroidSecurityProvider

actual fun initializePlatform() {
    AndroidSecurityProvider.initialize(ApplicationProvider.getApplicationContext())
}