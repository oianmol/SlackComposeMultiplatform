package dev.baseio.slackclone.uionboarding.vmtest

import dev.baseio.security.Capillary

actual fun initializePlatform() {
    Capillary.initialize()
}