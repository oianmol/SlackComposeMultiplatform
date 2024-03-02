package dev.baseio.slackclone

import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.Modifier

actual fun Modifier.notchPadding(): Modifier = this.displayCutoutPadding().statusBarsPadding()

