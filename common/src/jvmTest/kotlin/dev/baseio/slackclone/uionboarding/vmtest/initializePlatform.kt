package dev.baseio.slackclone.uionboarding.vmtest

import dev.baseio.security.JVMSecurityProvider

actual fun initializePlatform() {
    JVMSecurityProvider.initialize()
}