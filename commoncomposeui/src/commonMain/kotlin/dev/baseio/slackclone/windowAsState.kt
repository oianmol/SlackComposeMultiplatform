package dev.baseio.slackclone

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

@Composable
expect fun rememberComposeWindow(): State<WindowInfo>