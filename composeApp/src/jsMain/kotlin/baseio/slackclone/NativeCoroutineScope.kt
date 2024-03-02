package dev.baseio.slackclone

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

internal actual fun NativeCoroutineScope(context: CoroutineContext): CoroutineScope =
    CoroutineScope(SupervisorJob() + Dispatchers.Default + context)
