package dev.baseio.slackclone

import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

internal expect fun NativeCoroutineScope(
    context: CoroutineContext = EmptyCoroutineContext
): CoroutineScope